package com.rsa.asoc.sa.ui.threat.domain.bean;

import java.time.Instant;
import java.util.Objects;

/**
 * Journal entry/notes for an incident
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class JournalEntry {

    private String id;
    private String author;
    private String notes;
    private Instant created;
    private Instant lastUpdated;
    private InvestigationMilestone milestone;
    private boolean hasAttachment;
    private String attachmentFilename;
    private String attachmentContentType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public InvestigationMilestone getMilestone() {
        return milestone;
    }

    public void setMilestone(InvestigationMilestone milestone) {
        this.milestone = milestone;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JournalEntry that = (JournalEntry) obj;
        return hasAttachment == that.hasAttachment && Objects.equals(id, that.id)
                && Objects.equals(author, that.author) && Objects.equals(notes, that.notes)
                && Objects.equals(created, that.created) && Objects.equals(lastUpdated, that.lastUpdated)
                && milestone == that.milestone && Objects.equals(attachmentFilename, that.attachmentFilename)
                && Objects.equals(attachmentContentType, that.attachmentContentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                author,
                notes,
                created,
                lastUpdated,
                milestone,
                hasAttachment,
                attachmentFilename,
                attachmentContentType);
    }
}
