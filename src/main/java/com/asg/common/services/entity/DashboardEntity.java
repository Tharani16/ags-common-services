package com.asg.common.services.entity;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class DashboardEntity {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "COMPANY_POID")
    private Long companyPoid;

    @Column(name = "GROUP_POID")
    private Long groupPoid;

    @Column(name = "DOC_ID")
    private String docId;

    @Column(name ="DOC_SHORT_NAME")
    private String docShortName;

    @Column(name = "DOC_KEY_POID")
    private Long docKeyPoid;

    @Column(name = "DOC_REF")
    private String docRef;

    @Column(name = "DOC_DATE")
    private Date docDate;

    @Column(name = "DOC_SUMMARY_INFO")
    private String docSummaryInfo;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_ROLE_POID")
    private  Long userRolePoid;

    @Column(name = "USER_POID")
    private  Long userPoid;

    @Column(name = "ACTION_STATUS")
    private  String actionStatus;

    @Column(name = "ACTIONED_BY")
    private  String actionedBy;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "DATE_TIME")
    private Date datetime;

    @Column(name = "COMMENTS")
    private String comments;

    /*@Column(name = "TASKFLOW_URL")
    private String taskflowurl;*/

}