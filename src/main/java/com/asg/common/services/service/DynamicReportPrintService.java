package com.asg.common.services.service;

import com.asg.common.lib.service.PrintService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperReport;
import oracle.jdbc.driver.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicReportPrintService {

    private final PrintService printService;
    private final DataSource dataSource;

    public byte[] generateReportPdf(String docId, Map<String, Object> rptParams) throws Exception {
        Map<String, Object> params = printService.buildBaseParams(null, docId);
        JasperReport mainReport = getReportConfig(docId, params);
        if (rptParams != null) {
            convertCompanyPoidParam(rptParams, docId);
            convertDateParams(rptParams);
            params.putAll(rptParams);
        }
        return printService.fillReportToPdf(mainReport, params, dataSource);
    }

    /**
     * Handle COMPANY_POID parameter - create appropriate types for different reports
     */
    private void convertCompanyPoidParam(Map<String, Object> params, String docId) throws Exception {
        Object raw = params.get("COMPANY_POID");
        if (raw instanceof List<?> list) {
            List<Long> companyIds = list.stream()
                    .map(obj -> obj instanceof Number ? ((Number) obj).longValue() : Long.parseLong(obj.toString()))
                    .toList();
            // 999 - All Companies Check
            boolean contains999 = companyIds.contains(999L);
            if (companyIds.size() == 1 || contains999) {
                // Backward Compatibility
                params.put("COMPANY_POID", companyIds.getFirst());
                // Adding CSV as well because we already changed query for Two Reports BillWise Statement and BillWise Statement FC
                params.put("COMPANY_POID_CSV", companyIds.getFirst());
            } else {
                // Multiple values - add as CSV, Add First ID in COMPANY_POID as well to maintain existing logic
                params.put("COMPANY_POID", companyIds.getFirst());
                params.put("COMPANY_POID_CSV", companyIds.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")));
            }
        }
    }
    
    private void convertDateParams(Map<String, Object> params) {
        params.entrySet().forEach(entry -> {
            if (entry.getKey().contains("DATE") && entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    entry.setValue(convertToOracleDate(value));
                }
            }
        });
    }
    
    private String convertToOracleDate(String isoDate) {
        LocalDate date = LocalDate.parse(isoDate);
        return date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toUpperCase();
    }
    
    private JasperReport getReportConfig(String docId, Map<String, Object> params) throws Exception {
        return switch (docId) {
            case "400-205" -> {
                params.put("SUB_CONTACT", printService.load("DynamicReport/GL/BillwiseLedgerStatement_Contact_subreport1.jrxml"));
                yield printService.load("DynamicReport/GL/BillwiseLedgerStatement_A4.jrxml");
            }
            case "400-347" -> {
                params.put("SUB_CONTACT", printService.load("DynamicReport/GL/BillwiseLedgerStatement_Contact_subreport1.jrxml"));
                yield printService.load("DynamicReport/GL/BillwiseLedgerStatementFC_A4.jrxml");
            }
            default -> printService.load("DynamicReport/DynamicReport_A3.jrxml");
        };
    }

}