//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml.querycustomview;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für analyticsProvidersType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="analyticsProvidersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="analyticsProvider" type="{http://labkey.org/data/xml/queryCustomView}analyticsProviderType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "analyticsProvidersType", propOrder = {
        "analyticsProvider"
})
public class AnalyticsProvidersType {

    @XmlElement(required = true)
    protected List<AnalyticsProviderType> analyticsProvider;

    /**
     * Gets the value of the analyticsProvider property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the analyticsProvider property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnalyticsProvider().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnalyticsProviderType }
     */
    public List<AnalyticsProviderType> getAnalyticsProvider() {
        if (analyticsProvider == null) {
            analyticsProvider = new ArrayList<AnalyticsProviderType>();
        }
        return this.analyticsProvider;
    }

}
