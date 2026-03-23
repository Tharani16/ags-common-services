package com.asg.common.services.service;

import com.asg.common.lib.dto.LovGetListDto;

public interface TermsAndConditionTemplateService {
    LovGetListDto getTermsAndConditionTemplateByParameterName(String parameterName, String lovName);
}
