//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:07:57 PM CEST 
//


package org.labkey.data.xml.reportprops;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.labkey.data.xml.reportprops package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.labkey.data.xml.reportprops
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PropDef }
     * 
     */
    public PropDef createPropDef() {
        return new PropDef();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link PropValue }
     * 
     */
    public PropValue createPropValue() {
        return new PropValue();
    }

    /**
     * Create an instance of {@link PropertyList }
     * 
     */
    public PropertyList createPropertyList() {
        return new PropertyList();
    }

}
