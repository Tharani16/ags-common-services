package com.asg.common.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDetailsListResponseDto {
    private List<AddressDetailsResponseDto> addressDetails;
}