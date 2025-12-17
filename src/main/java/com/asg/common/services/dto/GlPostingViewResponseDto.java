package com.asg.common.services.dto;

import lombok.Data;

import java.util.List;

@Data
public class GlPostingViewResponseDto {
    private List<LedgerEntryDto> ledgerEntries;
    private List<BillwiseBreakupDto> billwiseBreakup;
    private List<CostBreakupDto> costBreakup;
    private List<VatBreakupDto> vatBreakup;
}