package de.dktk.dd.rpb.core.util.edc;

public class OdmMappingCheckResult {

    private boolean eventDefOidMatch = false;
    private boolean formDefOidMatch = false;
    private boolean itemGroupOidMatch = false;
    private boolean defOidMatch = false;

    public OdmMappingCheckResult() {
    }

    public boolean isEventDefOidMatch() {
        return eventDefOidMatch;
    }

    public void setEventDefOidMatch(boolean eventDefOidMatch) {
        this.eventDefOidMatch = eventDefOidMatch;
    }

    public boolean isFormDefOidMatch() {
        return formDefOidMatch;
    }

    public void setFormDefOidMatch(boolean formDefOidMatch) {
        this.formDefOidMatch = formDefOidMatch;
    }

    public boolean isItemGroupOidMatch() {
        return itemGroupOidMatch;
    }

    public void setItemGroupOidMatch(boolean itemGroupOidMatch) {
        this.itemGroupOidMatch = itemGroupOidMatch;
    }

    public boolean isDefOidMatch() {
        return defOidMatch;
    }

    public void setDefOidMatch(boolean defOidMatch) {
        this.defOidMatch = defOidMatch;
    }

    public boolean matches() {
        return this.eventDefOidMatch && this.formDefOidMatch && this.itemGroupOidMatch && this.defOidMatch;
    }
}
