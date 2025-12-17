package com.asg.common.services.repository;

import com.asg.common.services.dto.BillwiseBreakupRequestDto;
import com.asg.common.services.dto.GlVoucherLoadBillwiseBreakupResponseDto;
import com.asg.common.services.dto.GlVoucherPendingBillwiseBreakupResponseDto;

import java.util.Date;
import java.util.List;

public interface BillwiseBreakupDtlRepository {

    GlVoucherLoadBillwiseBreakupResponseDto loadBillwiseBreakup(
            Long groupPoid,
            Long companyPoid,
            String docId,
            Long transactionPoid
    );

    GlVoucherPendingBillwiseBreakupResponseDto showPendingBillwiseBreakup(
            Long groupPoid,
            Long companyPoid,
            Long glPoid,
            Date asOnDate);

    GlVoucherPendingBillwiseBreakupResponseDto showAllPendingBillwiseBreakup(
            Long groupPoid,
            Long companyPoid,
            Long glPoid,
            Date asOnDate);

    void insertBillwiseBreakup(List<BillwiseBreakupRequestDto> breakupList);

    void deleteBillwiseBreakup(Long groupPoid, Long companyPoid, String docId, Long transactionPoid, Long loginUserPoid);
}
