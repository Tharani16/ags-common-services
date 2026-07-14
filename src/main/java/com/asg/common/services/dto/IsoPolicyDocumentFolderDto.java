package com.asg.common.services.dto;

import lombok.Data;

import java.util.List;

/**
 * One folder in the Home page Documents widget. The SRS says the folders are driven by the
 * document's Category ("Each of the Department and ISO will be a folder in Home widget").
 */
@Data
public class IsoPolicyDocumentFolderDto {

    private String category;

    private int documentCount;

    /** Total files across every document in this folder. */
    private int attachmentCount;

    /** Documents in this folder the employee still has to acknowledge — the folder's badge count. */
    private int acknowledgementPendingCount;

    private List<IsoPolicyDocumentDto> documents;
}
