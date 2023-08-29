package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemPresentInForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for ODM ItemDefinitions. Compares by the Clinica specific "ItemPresentInForm" element attribute.
 */
public class ItemDefinitionClinicaComparator implements Comparator<ItemDefinition> {
    private static final Logger log = LoggerFactory.getLogger(ItemDefinitionClinicaComparator.class);

    private final String formOid;

    public ItemDefinitionClinicaComparator(String formOid) {
        this.formOid = formOid;
    }

    @Override
    public int compare(ItemDefinition o1, ItemDefinition o2) {
        int fallbackResult = 0;

        if (formOid == null) {
            log.debug("FormOid is null");
            return fallbackResult;
        }

        if (formOid.isEmpty()) {
            log.debug("FormOid is empty");
            return fallbackResult;
        }

        if (o1.getItemDetails() == null || o2.getItemDetails() == null) {
            log.debug("ItemDetails of an object is null. " + o1.toString() + " " + o2.toString());
            return fallbackResult;
        }

        List<ItemPresentInForm> o1Items = o1.getItemDetails().getItemPresentInForm();
        List<ItemPresentInForm> o2Items = o2.getItemDetails().getItemPresentInForm();

        if (o1Items == null) {
            log.debug(o1.toString() + " has no ItemPresentInForm list.");
            return fallbackResult;
        }

        if (o1Items.size() == 0) {
            log.debug(o1.toString() + " ItemPresentInForm list is empty.");
            return fallbackResult;
        }

        if (o2Items == null) {
            log.debug(o2.toString() + " has no ItemPresentInForm list.");
            return fallbackResult;
        }

        if (o2Items.size() == 0) {
            log.debug(o2.toString() + " ItemPresentInForm list is empty.");
            return fallbackResult;
        }

        for (ItemPresentInForm itemO1 : o1Items) {
            String formOidO1 = itemO1.getFormOid();
            for (ItemPresentInForm itemO2 : o2Items) {
                String formOid02 = itemO2.getFormOid();
                if (formOidO1.equals(formOid02) && this.formOid.equals(formOidO1)) {
                    return itemO1.getOrderInForm().compareTo(itemO2.getOrderInForm());
                }
            }
        }

        log.debug("Comparison failed for " + o1.toString() + " " + o2.toString() + ". Return fallback result");
        return fallbackResult;
    }
}
