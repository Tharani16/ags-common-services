package com.asg.common.services.enums;

import lombok.Getter;

@Getter
public enum ApprovalAction {
    
    SUBMIT_FOR_APPROVAL("SUBMIT_FOR_APPROVAL", "Submit for Approval"),
    RECALL_FOR_CHANGE("RECALL_FOR_CHANGE", "Recall for Change"),
    APPROVE("APPROVE", "Approve"),
    APPROVE_WITH_COMMENTS("APPROVE", "Approve With Comments"),
    RETURN_FOR_CORRECTION("RETURN_FOR_CORRECTION", "Return for Correction"),
    REJECT("REJECT", "Reject"),
    SPECIAL_SUBMIT("SUBMIT_FOR_SPECIAL_APPROVAL", "Special Submit"),
    SPECIAL_APPROVE("SPECIAL_APPROVE", "Special Approve"),

    CANCEL_APPROVAL("CANCEL_APPROVAL", "Cancel Approval"),
    STATUS_CHECK("STATUS_CHECK", "Status Check");

    private final String code;
    private final String displayName;

    ApprovalAction(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static ApprovalAction fromCode(String code) {
        for (ApprovalAction action : values()) {
            if (action.code.equals(code)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid approval action code: " + code);
    }
}
