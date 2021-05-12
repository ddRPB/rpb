

package org.labkey.study.xml.datasets;



import org.labkey.data.xml.reportprops.PropertyList;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "datasets", namespace = "http://labkey.org/study/xml")
public class Datasets {

    @XmlElement(namespace = "http://labkey.org/study/xml")
    protected Categories categories;
    @XmlElement(namespace = "http://labkey.org/study/xml")
    protected InnerDatasets datasets;
    @XmlAttribute(name = "defaultDateFormat")
    protected String defaultDateFormat;
    @XmlAttribute(name = "defaultNumberFormat")
    protected String defaultNumberFormat;
    @XmlAttribute(name = "metaDataFile")
    protected String metaDataFile;

    public Categories getCategories() {
        return categories;
    }
    public void setCategories(Categories value) {
        this.categories = value;
    }

    public InnerDatasets getDatasets() {
        return datasets;
    }
    public void setDatasets(InnerDatasets value) {
        this.datasets = value;
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }
    public void setDefaultDateFormat(String value) {
        this.defaultDateFormat = value;
    }

    public String getDefaultNumberFormat() {
        return defaultNumberFormat;
    }
    public void setDefaultNumberFormat(String value) {
        this.defaultNumberFormat = value;
    }

    public String getMetaDataFile() {
        return metaDataFile;
    }
    public void setMetaDataFile(String value) {
        this.metaDataFile = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "category"
    } , namespace = "http://labkey.org/study/xml")

    public static class Categories {

        protected List<String> category;
        public List<String> getCategory() {
            if (category == null) {
                category = new ArrayList<String>();
            }
            return this.category;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dataset"
    } , namespace = "http://labkey.org/study/xml")

    public static class InnerDatasets {
        @XmlElement(namespace = "http://labkey.org/study/xml")
        protected List<Dataset> dataset;
        public List<Dataset> getDataset() {
            if (dataset == null) {
                dataset = new ArrayList<Dataset>();
            }
            return this.dataset;
        }

        public void setDataset(List<Dataset> dataset) {
            this.dataset = dataset;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class Dataset {


            @XmlElement(namespace = "http://labkey.org/study/xml")
            protected PropertyList tags;
            @XmlAttribute(name = "name", required = true)
            protected String name;
            @XmlAttribute(name = "id", required = true)
            protected int id;
            @XmlAttribute(name = "category")
            protected String category;
            @XmlAttribute(name = "cohort")
            protected String cohort;
            @XmlAttribute(name = "showByDefault")
            protected Boolean showByDefault;
            @XmlAttribute(name = "demographicData")
            protected Boolean demographicData;
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "tag")
            protected String tag;
            @XmlAttribute(name = "useTimeKeyField")
            protected Boolean useTimeKeyField;

            public PropertyList getTags() {
                return tags;
            }
            public void setTags(PropertyList value) {
                this.tags = value;
            }

            public String getName() {
                return name;
            }
            public void setName(String value) {
                this.name = value;
            }

            public int getId() {
                return id;
            }
            public void setId(int value) {
                this.id = value;
            }

            public String getCategory() {
                return category;
            }
            public void setCategory(String value) {
                this.category = value;
            }

            public String getCohort() {
                return cohort;
            }
            public void setCohort(String value) {
                this.cohort = value;
            }

            public boolean isShowByDefault() {
                if (showByDefault == null) {
                    return true;
                } else {
                    return showByDefault;
                }
            }
            public void setShowByDefault(Boolean value) {
                this.showByDefault = value;
            }

            public boolean isDemographicData() {
                if (demographicData == null) {
                    return false;
                } else {
                    return demographicData;
                }
            }

            public void setDemographicData(Boolean value) {
                this.demographicData = value;
            }

            public String getType() {
                return type;
            }
            public void setType(String value) {
                this.type = value;
            }

            public String getTag() {
                return tag;
            }
            public void setTag(String value) {
                this.tag = value;
            }

            public Boolean isUseTimeKeyField() {
                return useTimeKeyField;
            }
            public void setUseTimeKeyField(Boolean value) {
                this.useTimeKeyField = value;
            }

        }

    }

}
