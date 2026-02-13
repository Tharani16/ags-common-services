package com.asg.common.services.dto;


import lombok.Data;

@Data
public class GlrePostingDto {

    private int loginGroupPoid;
    private int loginCompanyPoid;
    private int loginUserPoid;
    private String docId;
    private int transactionPoid;
    private String docRef;
}
