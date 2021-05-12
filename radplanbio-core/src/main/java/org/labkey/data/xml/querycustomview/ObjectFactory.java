//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml.querycustomview;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.org.labkey.data.xml.querycustomview package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CustomView_QNAME = new QName("http://labkey.org/data/xml/queryCustomView", "customView");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.org.labkey.data.xml.querycustomview
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CustomViewType }
     */
    public CustomViewType createCustomViewType() {
        return new CustomViewType();
    }

    /**
     * Create an instance of {@link AggregatesType }
     */
    public AggregatesType createAggregatesType() {
        return new AggregatesType();
    }

    /**
     * Create an instance of {@link NamedFiltersType }
     */
    public NamedFiltersType createNamedFiltersType() {
        return new NamedFiltersType();
    }

    /**
     * Create an instance of {@link AggregateType }
     */
    public AggregateType createAggregateType() {
        return new AggregateType();
    }

    /**
     * Create an instance of {@link ColumnType }
     */
    public ColumnType createColumnType() {
        return new ColumnType();
    }

    /**
     * Create an instance of {@link AnalyticsProvidersType }
     */
    public AnalyticsProvidersType createAnalyticsProvidersType() {
        return new AnalyticsProvidersType();
    }

    /**
     * Create an instance of {@link FiltersType }
     */
    public FiltersType createFiltersType() {
        return new FiltersType();
    }

    /**
     * Create an instance of {@link SortType }
     */
    public SortType createSortType() {
        return new SortType();
    }

    /**
     * Create an instance of {@link PropertyType }
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link PropertiesType }
     */
    public PropertiesType createPropertiesType() {
        return new PropertiesType();
    }

    /**
     * Create an instance of {@link SortsType }
     */
    public SortsType createSortsType() {
        return new SortsType();
    }

    /**
     * Create an instance of {@link FilterType }
     */
    public FilterType createFilterType() {
        return new FilterType();
    }

    /**
     * Create an instance of {@link ColumnsType }
     */
    public ColumnsType createColumnsType() {
        return new ColumnsType();
    }

    /**
     * Create an instance of {@link AnalyticsProviderType }
     */
    public AnalyticsProviderType createAnalyticsProviderType() {
        return new AnalyticsProviderType();
    }

    /**
     * Create an instance of {@link LocalOrRefFiltersType }
     */
    public LocalOrRefFiltersType createLocalOrRefFiltersType() {
        return new LocalOrRefFiltersType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CustomViewType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://labkey.org/data/xml/queryCustomView", name = "customView")
    public JAXBElement<CustomViewType> createCustomView(CustomViewType value) {
        return new JAXBElement<CustomViewType>(_CustomView_QNAME, CustomViewType.class, null, value);
    }

}
