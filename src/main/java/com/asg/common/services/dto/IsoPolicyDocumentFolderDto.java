package com.asg.common.services.dto;

import lombok.Data;

import java.util.List;

/**
 * One folder in the Home page Documents widget. The SRS says the folders are driven by the
 * document's Category ("it should show the folders based on the category").
 */
@Data
public class IsoPolicyDocumentFolderDto {

    /** The ISO_CATEGORY LOV code stored on the document — the key, not the display text. */
    private String categoryCode;

    /** The folder's name: the ISO_CATEGORY label. Falls back to the code if the LOV has no entry. */
    private String category;

    private int documentCount;

    /** Total files across every document in this folder. */
    private int attachmentCount;

    /** Documents in this folder the employee still has to acknowledge — the folder's badge count. */
    private int acknowledgementPendingCount;

    private List<IsoPolicyDocumentDto> documents;
}
