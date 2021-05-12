package de.dktk.dd.rpb.core.domain.edc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Protocol transient domain entity (CDISC ODM)
 *
 * @author RPB Team
 * @version 1.0.0
 * @since 13 Jun 2020
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Protocol")
public class Protocol {

    // region Members

    @XmlElement(name = "StudyEventRef")
    private List<EventReference> eventReferences;

    // endregion

    // region Constructors

    public Protocol() {
        this.eventReferences = new ArrayList<>();
    }

    // endregion

    public List<EventReference> getEventReferences() {
        return eventReferences;
    }

    public void setEventReferences(List<EventReference> eventReferences) {
        this.eventReferences = eventReferences;
    }

    public EventReference getEventReferenceByOid(String oid) {
        if (this.eventReferences == null) return null;

        for (EventReference reference : this.eventReferences) {
            if (reference.getOid().equals(oid)) {
                return reference;
            }
        }
        return null;

    }


}

