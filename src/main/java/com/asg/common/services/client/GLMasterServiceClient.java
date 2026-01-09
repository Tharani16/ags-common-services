package com.asg.common.services.client;

import com.asg.common.lib.client.GenericRestClient;
import com.asg.common.lib.dto.CompanyDto;
import com.asg.common.lib.dto.GLMasterDto;
import com.asg.common.lib.dto.response.ApiResponseWrapper;
import com.asg.common.lib.enums.UserRolesRightsEnum;
import com.asg.common.lib.utility.RestClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GLMasterServiceClient {
    
    private final GenericRestClient restClient;
    
    @Value("${finance.service.url:http://localhost:8086/finance/api}")
    private String financeServiceUrl;
    
    public GLMasterDto getGLMaster(Long glPoid) {
        String url = financeServiceUrl + "/v1/gl-master/simple/" + glPoid;
        HttpHeaders customHeaders = new HttpHeaders();
        customHeaders.add("X-Document-id", "000-016");
        customHeaders.add("X-Action-Requested", UserRolesRightsEnum.VIEW.name());
        ApiResponseWrapper<GLMasterDto> response = restClient.get(url, new ParameterizedTypeReference<>() {}, customHeaders);
        return RestClientUtil.extractData(response);
    }
}
