package com.asg.common.services.dto;

import lombok.Data;

@Data
public class RecentDocumentDto {
    private String docType;
    private String docShortName;
    private String docName;
    private String routeName;
    private String docId;
    private String docKeyPoid;
    private String docDate;
    private String docRef;
}
