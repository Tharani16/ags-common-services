package com.asg.common.services.client;

import com.asg.common.lib.client.GenericRestClient;
import com.asg.common.lib.dto.TaxMasterDto;
import com.asg.common.lib.dto.response.ApiResponseWrapper;
import com.asg.common.lib.utility.RestClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaxServiceClient {
    
    private final GenericRestClient restClient;
    
    @Value("${finance.service.url:http://localhost:8086/finance/api}")
    private String financeServiceUrl;
    
    public TaxMasterDto getTaxMaster(Long taxPoid) {
        String url = financeServiceUrl + "/v1/tax-master/simple/" + taxPoid;
        ApiResponseWrapper<TaxMasterDto> response = restClient.get(url, new ParameterizedTypeReference<>() {});
        return RestClientUtil.extractData(response);
    }
}
