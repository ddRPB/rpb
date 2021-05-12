//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.29 um 07:32:03 AM CEST 
//


package org.labkey.study.xml.viewcategory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.labkey.study.xml.viewcategory package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Categories_QNAME = new QName("http://labkey.org/study/xml/viewCategory", "categories");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.labkey.study.xml.viewcategory
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ViewCategoryType }
     * 
     */
    public ViewCategoryType createViewCategoryType() {
        return new ViewCategoryType();
    }

    /**
     * Create an instance of {@link CategoryType }
     * 
     */
    public CategoryType createCategoryType() {
        return new CategoryType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ViewCategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://labkey.org/study/xml/viewCategory", name = "categories")
    public JAXBElement<ViewCategoryType> createCategories(ViewCategoryType value) {
        return new JAXBElement<ViewCategoryType>(_Categories_QNAME, ViewCategoryType.class, null, value);
    }

}
