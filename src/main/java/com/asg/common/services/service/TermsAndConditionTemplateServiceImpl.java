package com.asg.common.services.service;

import com.asg.common.lib.dto.LovGetListDto;
import com.asg.common.lib.exception.CustomException;
import com.asg.common.lib.exception.ResourceNotFoundException;
import com.asg.common.lib.security.util.UserContext;
import com.asg.common.lib.service.LovDataService;
import com.asg.common.services.repository.GlobalParametersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TermsAndConditionTemplateServiceImpl implements TermsAndConditionTemplateService {

    private static final String DEFAULT_VALUES = "defaultValues";

    private final GlobalParametersRepository globalParametersRepository;
    private final LovDataService lovDataService;

    public LovGetListDto getTermsAndConditionTemplateByParameterName(String parameterName, String lovName) {

        String parameterValue = globalParametersRepository.findParameterValueByName(parameterName).orElseThrow(() -> new ResourceNotFoundException("Global parameter", "parameterName", parameterName));

        long defaultPoid;
        try {
            defaultPoid = Long.parseLong(parameterValue);
        } catch (NumberFormatException e) {
            throw new CustomException(parameterValue + " is not a proper value", 400);
        }

        Map<String, Object> result = lovDataService.getLovList("", UserContext.getGroupPoid(), UserContext.getCompanyPoid(), UserContext.getUserPoid(), lovName, 0, 1, "", "", null, List.of(defaultPoid));

        Object obj = result.get(DEFAULT_VALUES);

        if (!(obj instanceof List<?> list)) {
            throw new CustomException("Invalid LOV response structure", 500);
        }

        List<LovGetListDto> dtos = list.stream()
                .filter(LovGetListDto.class::isInstance)
                .map(LovGetListDto.class::cast)
                .toList();

        if (dtos.isEmpty()) {
            throw new ResourceNotFoundException("TermsAndConditionTemplate", "lovName", lovName);
        }

        return dtos.getFirst();
    }
}
