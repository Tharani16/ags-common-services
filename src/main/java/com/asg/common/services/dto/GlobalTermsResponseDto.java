package com.asg.common.services.dto;

import lombok.Data;

import java.util.List;

@Data
public class GlobalTermsResponseDto {
    private List<GlobalTermsDto> termsList;
}
