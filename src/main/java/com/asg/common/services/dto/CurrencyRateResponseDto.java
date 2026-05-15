package com.asg.common.services.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyRateResponseDto {

    private String currencyCode;
    private Double rate;
    private Integer currencyDecimals;
}
