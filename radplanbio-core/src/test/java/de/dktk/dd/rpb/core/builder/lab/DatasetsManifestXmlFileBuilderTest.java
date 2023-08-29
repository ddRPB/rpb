package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.junit.Test;
import org.labkey.study.xml.datasets.Datasets;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_EDC_ATTRIBUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.xmlunit.matchers.ValidationMatcher.valid;

public class DatasetsManifestXmlFileBuilderTest {

    //region empty constructor

    @Test
    public void creates_valid_xml_content() throws JAXBException {
        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder();
        String xmlContent = builder.build();

        assertThat(xmlContent,
                is(valid(new StreamSource(new File("src/test/resources/xml-schemas/labkey/datasets.xsd")))));
    }

    @Test
    public void adds_a_new_dataset() throws JAXBException {
        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder();
        Integer id = builder.findOrCreateDataset("Participants", LABKEY_EDC_ATTRIBUTES);
        String xmlContent = builder.build();

        Datasets datasets = JAXBHelper.unmashalString2(Datasets.class, xmlContent);
        List<Datasets.InnerDatasets.Dataset> datasetsList = datasets.getDatasets().getDataset();

        assertEquals("Participants", datasetsList.get(0).getName());
        assertEquals((int) id, datasetsList.get(0).getId());
    }

    @Test
    public void adds_a_new_dataset_once_only() throws JAXBException {
        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder();
        Integer idFirstCall = builder.findOrCreateDataset("Participants", LABKEY_EDC_ATTRIBUTES);
        Integer idSecondCall = builder.findOrCreateDataset("Participants", LABKEY_EDC_ATTRIBUTES);
        String xmlContent = builder.build();

        Datasets datasets = JAXBHelper.unmashalString2(Datasets.class, xmlContent);
        List<Datasets.InnerDatasets.Dataset> datasetsList = datasets.getDatasets().getDataset();

        assertEquals(1, datasetsList.size());
        assertEquals(idFirstCall, idSecondCall);
    }


    // endregion

    // region with existing inputstream in constructor

    @Test
    public void does_not_throw_if_input_stream_is_null() throws JAXBException {
        new DatasetsManifestXmlFileBuilder(null);
    }

    @Test
    public void handles_file_without_datasets() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-manifest/is_empty.xml");

        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder(new FileInputStream(file));
        String xmlContent = builder.build();

        assertThat(xmlContent,
                is(valid(new StreamSource(new File("src/test/resources/xml-schemas/labkey/datasets.xsd")))));
    }

    @Test
    public void passes_existing_items() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-manifest/has_other_item.xml");

        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder(new FileInputStream(file));
        String xmlContent = builder.build();

        Datasets datasets = JAXBHelper.unmashalString2(Datasets.class, xmlContent);
        List<Datasets.InnerDatasets.Dataset> datasetsList = datasets.getDatasets().getDataset();
        boolean hasOtherDataSetItem = false;

        for (Datasets.InnerDatasets.Dataset dataset : datasetsList) {
            if (dataset.getName().equals("OtherItem")) {
                hasOtherDataSetItem = true;
                break;
            }

        }
        assertTrue(hasOtherDataSetItem);
    }

    @Test
    public void returns_id_of_existing_items() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-manifest/has_other_item.xml");

        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder(new FileInputStream(file));
        Integer id = builder.findOrCreateDataset("OtherItem", "dummy");
        String xmlContent = builder.build();

        Datasets datasets = JAXBHelper.unmashalString2(Datasets.class, xmlContent);
        List<Datasets.InnerDatasets.Dataset> datasetsList = datasets.getDatasets().getDataset();
        boolean hasOtherDataSetItem = false;
        int idFromXml = 0;

        for (Datasets.InnerDatasets.Dataset dataset : datasetsList) {
            if (dataset.getName().equals("OtherItem")) {
                hasOtherDataSetItem = true;
                idFromXml = dataset.getId();
            }


        }
        assertTrue(hasOtherDataSetItem);
        assertEquals((int) id, idFromXml);
    }

    @Test
    public void adds_new_item() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-manifest/has_other_item.xml");

        DatasetsManifestXmlFileBuilder builder = new DatasetsManifestXmlFileBuilder(new FileInputStream(file));
        Integer id = builder.findOrCreateDataset("NewItem", "dummy");
        String xmlContent = builder.build();

        Datasets datasets = JAXBHelper.unmashalString2(Datasets.class, xmlContent);
        List<Datasets.InnerDatasets.Dataset> datasetsList = datasets.getDatasets().getDataset();
        boolean hasOtherDataSetItem = false;
        int idFromXml = 0;

        for (Datasets.InnerDatasets.Dataset dataset : datasetsList) {
            if (dataset.getName().equals("NewItem")) {
                hasOtherDataSetItem = true;
                idFromXml = dataset.getId();
            }


        }
        assertEquals(2, datasetsList.size());
        assertTrue(hasOtherDataSetItem);
        assertEquals((int) id, idFromXml);

    }


    // endregion

}