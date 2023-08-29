package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemDetails;
import de.dktk.dd.rpb.core.domain.edc.ItemPresentInForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ItemDefinition.class, LoggerFactory.class, Logger.class})
public class ItemDefinitionClinicaComparatorTest {
    private ItemDefinition firstItem;
    private ItemDefinition secondItem;

    @Before
    public void setUp() throws Exception {
        // prepare logger
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        firstItem = new ItemDefinition();
        firstItem.setOrderNumber(1);
        secondItem = new ItemDefinition();
        secondItem.setOrderNumber(2);
    }

    @Test
    public void items_are_equal_if_form_oid_is_null() {
        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator(null);
        assertEquals(0, comparator.compare(firstItem, secondItem));
    }

    @Test
    public void items_are_equal_if_form_oid_is_empty() {
        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator("");
        assertEquals(0, comparator.compare(firstItem, secondItem));
    }

    @Test
    public void items_are_equal_if_item_details_are_null() {
        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator("abc");
        assertEquals(0, comparator.compare(firstItem, secondItem));
    }

    @Test
    public void items_are_equal_if_item_present_in_form_list_is_null() {
        ItemDetails itemDetails = new ItemDetails();
        firstItem.setItemDetails(itemDetails);
        secondItem.setItemDetails(itemDetails);
        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator("abc");
        assertEquals(0, comparator.compare(firstItem, secondItem));
    }

    @Test
    public void items_are_equal_if_item_present_in_form_list_is_empty() {
        List<ItemPresentInForm> itemPresentInFormList = new ArrayList<>();
        ItemDetails itemDetails = new ItemDetails();
        itemDetails.setItemPresentInForm(itemPresentInFormList);
        firstItem.setItemDetails(itemDetails);
        secondItem.setItemDetails(itemDetails);
        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator("abc");
        assertEquals(0, comparator.compare(firstItem, secondItem));
    }

    @Test
    public void returns_fallback_if_form_oi_does_not_match() {
        String formOidOne = "FormOidOne";
        String formOidTwo = "FormOidTwo";

        ItemPresentInForm itemPresentInFormOne = new ItemPresentInForm();
        itemPresentInFormOne.setFormOid(formOidOne);
        itemPresentInFormOne.setOrderInForm(6);

        List<ItemPresentInForm> itemPresentInFormListOne = new ArrayList<>();
        itemPresentInFormListOne.add(itemPresentInFormOne);

        ItemDetails itemDetailsOne = new ItemDetails();
        itemDetailsOne.setItemPresentInForm(itemPresentInFormListOne);
        firstItem.setItemDetails(itemDetailsOne);

        ItemPresentInForm itemPresentInFormTwo = new ItemPresentInForm();
        itemPresentInFormTwo.setFormOid(formOidOne);
        itemPresentInFormTwo.setOrderInForm(4);

        List<ItemPresentInForm> itemPresentInFormListTwo = new ArrayList<>();
        itemPresentInFormListTwo.add(itemPresentInFormTwo);

        ItemDetails itemDetailsTwo = new ItemDetails();
        itemDetailsTwo.setItemPresentInForm(itemPresentInFormListTwo);
        secondItem.setItemDetails(itemDetailsTwo);


        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator(formOidTwo);
        assertEquals(0, comparator.compare(firstItem, secondItem));
    }

    @Test
    public void uses_order_in_form_if_available() {
        String formOidOne = "FormOidOne";

        ItemPresentInForm itemPresentInFormOne = new ItemPresentInForm();
        itemPresentInFormOne.setFormOid(formOidOne);
        itemPresentInFormOne.setOrderInForm(6);

        List<ItemPresentInForm> itemPresentInFormListOne = new ArrayList<>();
        itemPresentInFormListOne.add(itemPresentInFormOne);

        ItemDetails itemDetailsOne = new ItemDetails();
        itemDetailsOne.setItemPresentInForm(itemPresentInFormListOne);
        firstItem.setItemDetails(itemDetailsOne);

        ItemPresentInForm itemPresentInFormTwo = new ItemPresentInForm();
        itemPresentInFormTwo.setFormOid(formOidOne);
        itemPresentInFormTwo.setOrderInForm(4);

        List<ItemPresentInForm> itemPresentInFormListTwo = new ArrayList<>();
        itemPresentInFormListTwo.add(itemPresentInFormTwo);

        ItemDetails itemDetailsTwo = new ItemDetails();
        itemDetailsTwo.setItemPresentInForm(itemPresentInFormListTwo);
        secondItem.setItemDetails(itemDetailsTwo);


        ItemDefinitionClinicaComparator comparator = new ItemDefinitionClinicaComparator(formOidOne);
        assertEquals(1, comparator.compare(firstItem, secondItem));
    }
}