package com.asg.common.services.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "GLOBAL_PARAMETERS")
public class GlobalParameters {

    @Id
    @Column(name = "PARAMETER_POID")
    private Long parameterPoid;

    @Column(name = "GROUP_POID")
    private Long groupPoid;

    @Column(name = "PARAMETER_NAME")
    private String parameterName;

    @Column(name = "PARAMETER_KEYID_TYPE")
    private String parameterKeyidType;

    @Column(name = "PARAMETER_KEYID")
    private String parameterKeyid;

    @Column(name = "PARAMETER_VALUE")
    private String parameterValue;

    @Column(name = "PARAMETER_DETAILS")
    private String parameterDetails;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "PARAMETER_LINUX_VALUE")
    private String parameterLinuxValue;

    @Column(name = "PARAMETER_TYPE")
    private String parameterType;

    @Column(name = "DELETED")
    private String deleted;
}
