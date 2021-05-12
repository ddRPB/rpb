package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.labkey.study.xml.datasets.Datasets;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatasetsManifestXmlFileBuilder {
    private List<Datasets.InnerDatasets.Dataset> datasetList;
    private final Integer startValue = 5000;

    public DatasetsManifestXmlFileBuilder() {
        this.datasetList = new ArrayList<>();
    }

    public DatasetsManifestXmlFileBuilder(InputStream inputStream) throws JAXBException {

        Datasets datasets = null;
        if (inputStream != null) {
            datasets = JAXBHelper.unmarshalInputstream2(Datasets.class, inputStream);
        }
        try {
            Datasets.InnerDatasets innerDatasets = datasets.getDatasets();
            this.datasetList = innerDatasets.getDataset();
        } catch (NullPointerException e) {
            this.datasetList = new ArrayList<>();
        }
    }

    public Integer findOrCreateDataset(String name) {
        List<Integer> existingIds = new ArrayList<>();
        existingIds.add(startValue);

        for (Datasets.InnerDatasets.Dataset dataset : this.datasetList) {
            if (dataset.getName().equals(name)) {
                return dataset.getId();
            } else {
                existingIds.add(dataset.getId());
            }
        }

        Integer newIdentifier = Collections.max(existingIds) + 1;
        createNewDataset(newIdentifier, name);
        return newIdentifier;
    }

    private void createNewDataset(Integer id, String name) {
        Datasets.InnerDatasets.Dataset dataset = new Datasets.InnerDatasets.Dataset();
        dataset.setType("Standard");
        dataset.setCategory("EDC");
        dataset.setId(id);
        dataset.setName(name);

        this.datasetList.add(dataset);
    }

    public String build() throws JAXBException {
        Datasets.InnerDatasets innerDatasets = new Datasets.InnerDatasets();
        innerDatasets.setDataset(this.datasetList);

        Datasets datasets = new Datasets();
        datasets.setDatasets(innerDatasets);
        datasets.setMetaDataFile("datasets_metadata.xml");

        return JAXBHelper.jaxbObjectToXML(Datasets.class, datasets);
    }
}
