package com.asg.common.services.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DashboardDto {
    @NotNull
    private Long groupPoid;
    @NotNull
    private Long companyPoid;
    @NotNull
    private Long userPoid;

}
