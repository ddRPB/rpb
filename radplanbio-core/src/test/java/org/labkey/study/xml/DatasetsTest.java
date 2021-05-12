package org.labkey.study.xml;

import org.junit.Test;
import org.labkey.study.xml.datasets.Datasets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatasetsTest {

    @Test
    public void read_datasets_manifest_xml() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(org.labkey.study.xml.datasets.Datasets.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        File file = new File("src/test/data/labkey/datasets_manifest.xml");
        org.labkey.study.xml.datasets.Datasets datasets = (Datasets) unmarshaller.unmarshal(file);

        assertNotNull(datasets);
        assertEquals("Participants", datasets.getDatasets().getDataset().get(0).getName());
    }

}
