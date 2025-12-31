package com.asg.common.services.client;

import com.asg.common.lib.client.GenericRestClient;
import com.asg.common.lib.dto.CompanyDto;
import com.asg.common.lib.dto.StockInfoDto;
import com.asg.common.lib.dto.response.ApiResponseWrapper;
import com.asg.common.lib.utility.RestClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockServiceClient {
    
    private final GenericRestClient restClient;
    
    @Value("${finance.service.url:http://localhost:8086/finance/api}")
    private String financeServiceUrl;
    
    public StockInfoDto getStockInfo(Long stockPoid) {
        String url = financeServiceUrl + "/api/stock-master/" + stockPoid;
        ApiResponseWrapper<StockInfoDto> response = restClient.get(url, new ParameterizedTypeReference<ApiResponseWrapper<StockInfoDto>>() {});
        return RestClientUtil.extractData(response);
    }
}
