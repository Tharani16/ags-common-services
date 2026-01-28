package com.asg.common.services.service;

import com.asg.common.lib.service.PrintService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperReport;
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
            convertCompanyPoidParam(rptParams);
            convertDateParams(rptParams);
            params.putAll(rptParams);
        }
        return printService.fillReportToPdf(mainReport, params, dataSource);
    }

    /**
     * From FE, we are getting Values like  "COMPANY_POID": "ARRAY[999,12]",
     * So adding a logic to Change it to   "COMPANY_POID_LIST": [999, 12],
     */
    private void convertCompanyPoidParam(Map<String, Object> params) {
        Object raw = params.get("COMPANY_POID");
        if (raw instanceof String s && s.startsWith("ARRAY[")) {
            // Strip ARRAY[...] and split
            String inside = s.substring(6, s.length() - 1); // "999,12"
            List<Long> companyIds = Arrays.stream(inside.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();
            //params.put("COMPANY_POID_LIST", companyIds);
            // Handle special "all companies" logic
            if (companyIds.isEmpty() || companyIds.contains(999L)) {
                params.put("COMPANY_POID_LIST", new ArrayList<>());
                // Optionally: params.put("ALL_COMPANIES", "Y");
            } else {
                params.put("COMPANY_POID_LIST", companyIds);
            }
            // Do not Remove COMPANY_POID as it is there in JRXMLs
            //params.remove("COMPANY_POID");
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