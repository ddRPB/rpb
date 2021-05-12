package de.dktk.dd.rpb.core.util;

import org.junit.Test;
import org.labkey.data.xml.TablesType;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class JAXBHelperTest {

    @Test
    public void unmashals_a_valid_file() throws JAXBException {
        String filePath = "src/test/data/labkey/datasets_metadata.xml";
        File file = new File(filePath);

        TablesType tablesType = JAXBHelper.unmashalFile(TablesType.class, file);
        List tableList = tablesType.getSharedConfigOrDescriptionOrSchemaCustomizer();

        assertNotNull(tablesType);
    }

    @Test(expected = UnmarshalException.class)
    public void throws_during_unmashaling() throws JAXBException {
        String filePath = "file_does_not_exist";
        File file = new File(filePath);

        TablesType tablesType = JAXBHelper.unmashalFile(TablesType.class, file);
        List tableList = tablesType.getSharedConfigOrDescriptionOrSchemaCustomizer();

        assertNotNull(tablesType);
    }

    @Test
    public void jaxbObjectToXML_returns_a_string() throws JAXBException {
        TablesType tablesType = new TablesType();
        String obj = JAXBHelper.jaxbObjectToXML(TablesType.class, tablesType);

        assertNotNull(obj);

    }
}