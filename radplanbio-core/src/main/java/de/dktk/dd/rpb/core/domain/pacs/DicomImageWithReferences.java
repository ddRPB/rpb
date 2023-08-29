package de.dktk.dd.rpb.core.domain.pacs;

/**
 * Extends the DicomImage class with a Uid property that references another instance.
 * It helps to evaluate references between the different series and images.
 */
public class DicomImageWithReferences extends DicomImage {
    private String referencedSopInstance;

    public DicomImageWithReferences(String sopInstanceUID, Integer size) {
        this.setSopInstanceUID(sopInstanceUID);
        this.setSize(size);
    }

    public String getReferencedSopInstance() {
        return referencedSopInstance;
    }

    public void setReferencedSopInstance(String referencedSopInstance) {
        this.referencedSopInstance = referencedSopInstance;
    }
}
