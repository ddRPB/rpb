/*
	
	Copyright 2012 VU Medical Center Amsterdam
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
*/

package de.dktk.dd.rpb.core.ocsoap.odm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import de.dktk.dd.rpb.core.domain.edc.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ODM Metadata
 *
 * Provide the access to OpenClinica study metadata
 * Using RadPlanBio Event, Form, ItemGroup and Item Definition entities
 *
 * @author Arjan van der Velde (a.vandervelde (at) xs4all.nl)
 * @author Tomas Skripcak (tomas@skripcak.net)
 * @since 21 Oct 2013
 */
public class MetadataODM extends AbstractODM {

    //region Constructors

	/**
	 * Construct a new MetadataODM from a DOM Document
	 * 
	 * @param odm The XML document
	 * @throws ODMException 
	 * @throws ParserConfigurationException 
	 */
    @SuppressWarnings("unused")
	public MetadataODM(Document odm) throws ODMException, ParserConfigurationException {
		super(odm);
	}

	/**
	 * Construct a new MetadataODM from String XML representation
	 * 
	 * @param odm The String representation of ODM metadata XML
	 * @throws ODMException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	public MetadataODM(String odm) throws ODMException, SAXException, IOException, ParserConfigurationException {
		super(odm);
	}

    //endregion

    //region Methods

	@Override
	public NodeList getStudyOID() throws ODMException {
		try {
			return (NodeList) xPath.evaluate("/cdisc:ODM/cdisc:Study/@OID", odm, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new ODMException(e);
		}
	}

    //region Study parameters

    /**
     * Determine whether the subject person id (PID )is configured as mandatory for the study
     *
     * @return String representing the mandatority of subject person ID (PID) for study
     * @throws ODMException
     */
    public String getSubjectPersonIdRequiredStudyParameter() throws ODMException {
        String xPathQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/OpenClinica:StudyDetails/OpenClinica:StudyParameterConfiguration/OpenClinica:StudyParameterListRef[@StudyParameterListID='SPL_subjectPersonIdRequired']";
        return this.getStringStudyParameter(xPathQuery);
    }

    /**
     * Determine whether the study is configured to collect patient day of birth property
     *
     * @return true if study subjects are stored with day of birth
     * @throws ODMException
     */
    public Boolean getCollectDobStudyParameter() throws ODMException {
        try {
            boolean collectDoB = false;
            Node collectBoDNode = (Node) xPath.compile("/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/OpenClinica:StudyDetails/OpenClinica:StudyParameterConfiguration/OpenClinica:StudyParameterListRef[@StudyParameterListID='SPL_collectDob']").evaluate(odm, XPathConstants.NODE);
            if (collectBoDNode != null) {
                for (int i = 0; i < collectBoDNode.getAttributes().getLength(); i++) {
                    if (collectBoDNode.getAttributes().item(i).getNodeName().equals("Value")){
                       collectDoB = collectBoDNode.getAttributes().item(i).getNodeValue().equals("1");
                    }
                }
            }

            return collectDoB;
        }
        catch (XPathExpressionException e) {
            throw new ODMException(e);
        }
    }

    /**
     * Determine whether the study is configured to collect patient sex property
     *
     * @return true if study subjects are stored with sex
     * @throws ODMException
     */
    public Boolean getCollectSexStudyParameter() throws ODMException {
        String xPathQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/OpenClinica:StudyDetails/OpenClinica:StudyParameterConfiguration/OpenClinica:StudyParameterListRef[@StudyParameterListID='SPL_genderRequired']";
        return this.getBooleanStudyParameter(xPathQuery);
    }

    /**
     * Determine whether the study is configured to allow discrepancy management
     *
     * @return true if discrepancy management is allowed for the study
     * @throws ODMException
     */
    public Boolean getAllowDiscrepancyManagementStudyParameter() throws ODMException {
        String xPathQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/OpenClinica:StudyDetails/OpenClinica:StudyParameterConfiguration/OpenClinica:StudyParameterListRef[@StudyParameterListID='SPL_discrepancyManagement']";
        return this.getBooleanStudyParameter(xPathQuery);
    }

    /**
     * Determine how the StudySubjectId generation is configured for the study
     *
     * @return string representing method of generation of study subject ID
     * @throws ODMException
     */
    public String getSubjectIdGenerationStudyParameter() throws ODMException {
        String xPathQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/OpenClinica:StudyDetails/OpenClinica:StudyParameterConfiguration/OpenClinica:StudyParameterListRef[@StudyParameterListID='SPL_subjectIdGeneration']";
        return this.getStringStudyParameter(xPathQuery);
    }

    public Boolean getSecondaryLabelViewableStudyParameter() throws ODMException {
        String xPathQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/OpenClinica:StudyDetails/OpenClinica:StudyParameterConfiguration/OpenClinica:StudyParameterListRef[@StudyParameterListID='SPL_secondaryLabelViewable']";
        return this.getBooleanStudyParameter(xPathQuery);
    }

    //endregion

    public Odm unmarshallOdm() {
        Odm result = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Odm.class);
            Unmarshaller un = context.createUnmarshaller();
            result = (Odm) un.unmarshal(this.odm);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }

        return result;
    }

    //region Study hierarchy

    /**
     * Get hierarchy (event definitions, forms, groups, items) of study from metadata
     *
     * @return list of event definitions (object graphs) for the study
     * @throws ODMException
     */
    @SuppressWarnings("unused")
    public List<EventDefinition> getStudyEventDefs() throws ODMException {
        List<EventDefinition> eventDefs = new ArrayList<EventDefinition>();

        try {
            // StudyEventDef
            String expression = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/cdisc:StudyEventDef";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(this.odm, XPathConstants.NODESET);


            for (int i = 0; i < nodeList.getLength(); i++) {
                EventDefinition eventDef = new EventDefinition();
                List<String> formRefs = new ArrayList<String>();

                eventDef.setOid(nodeList.item(i).getAttributes().getNamedItem("OID").getNodeValue());
                eventDef.setName(nodeList.item(i).getAttributes().getNamedItem("Name").getNodeValue());
                eventDef.setIsRepeating(nodeList.item(i).getAttributes().getNamedItem("Repeating").getNodeValue().equals("Yes"));
                eventDef.setType(nodeList.item(i).getAttributes().getNamedItem("Type").getNodeValue());

                // Collect form OIDs
                for (int j = 0; j < nodeList.item(i).getChildNodes().getLength(); j++) {
                    if (nodeList.item(i).getChildNodes().item(j).getNodeName().equals("FormRef")) {
                        formRefs.add(nodeList.item(i).getChildNodes().item(j).getAttributes().getNamedItem("FormOID").getNodeValue());
                    }
                }

                // FormDef
                for (String formRef : formRefs) {

                    String formDefQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/cdisc:FormDef[@OID='" + formRef + "']";
                    Node formDefNode = (Node) xPath.compile(formDefQuery).evaluate(this.odm, XPathConstants.NODE);

                    if (formDefNode != null) {

                        FormDefinition formDef = new FormDefinition();
                        List<String> itemGroupRefs = new ArrayList<String>();

                        formDef.setOid(formDefNode.getAttributes().getNamedItem("OID").getNodeValue());
                        formDef.setName(formDefNode.getAttributes().getNamedItem("Name").getNodeValue());
                        formDef.setIsRepeating(formDefNode.getAttributes().getNamedItem("Repeating").getNodeValue().equals("Yes"));

                        // Collect ItemGroup OIDs
                        for (int k = 0; k < formDefNode.getChildNodes().getLength(); k++) {
                            if (formDefNode.getChildNodes().item(k).getNodeName().equals("ItemGroupRef")) {
                                itemGroupRefs.add(formDefNode.getChildNodes().item(k).getAttributes().getNamedItem("ItemGroupOID").getNodeValue());
                            }
                        }

                        // ItemGroupDef
                        for (String itemGroupRef : itemGroupRefs) {
                            String itemGroupDefQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/cdisc:ItemGroupDef[@OID='" + itemGroupRef + "']";
                            Node itemGroupDefNode = (Node) xPath.compile(itemGroupDefQuery).evaluate(this.odm, XPathConstants.NODE);

                            if (itemGroupDefNode != null) {

                                ItemGroupDefinition itemGroupDef = new ItemGroupDefinition();
                                List<String> itemRefs = new ArrayList<String>();

                                itemGroupDef.setOid(itemGroupDefNode.getAttributes().getNamedItem("OID").getNodeValue());
                                itemGroupDef.setName(itemGroupDefNode.getAttributes().getNamedItem("Name").getNodeValue());
                                itemGroupDef.setIsRepeating(itemGroupDefNode.getAttributes().getNamedItem("Repeating").getNodeValue().equals("Yes"));

                                // Collect ItemDefinition OIDs
                                for (int l = 0; l < itemGroupDefNode.getChildNodes().getLength(); l++) {
                                    if (itemGroupDefNode.getChildNodes().item(l).getNodeName().equals("ItemRef")) {
                                        itemRefs.add(itemGroupDefNode.getChildNodes().item(l).getAttributes().getNamedItem("ItemOID").getNodeValue());
                                    }
                                }

                                //ItemDef
                                for (String itemRef : itemRefs) {
                                    String itemDefQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/cdisc:ItemDef[@OID='" + itemRef + "']";
                                    Node itemDefNode = (Node) xPath.compile(itemDefQuery).evaluate(this.odm, XPathConstants.NODE);

                                    if (itemDefNode != null) {

                                        ItemDefinition itemDef = new ItemDefinition();

                                        itemDef.setOid(itemDefNode.getAttributes().getNamedItem("OID").getNodeValue());
                                        itemDef.setName(itemDefNode.getAttributes().getNamedItem("Name").getNodeValue());
                                        itemDef.setDataType(itemDefNode.getAttributes().getNamedItem("DataType").getNodeValue());
                                        itemDef.setDescription(itemDefNode.getAttributes().getNamedItem("Comment").getNodeValue());

                                        for (int k = 0; k < itemDefNode.getChildNodes().getLength(); k++) {
                                            if (itemDefNode.getChildNodes().item(k).getNodeName().equals("Question")) {
                                                for (int l = 0; l < itemDefNode.getChildNodes().item(k).getChildNodes().getLength(); l++) {
                                                    if (itemDefNode.getChildNodes().item(k).getChildNodes().item(l).getNodeName().equals("TranslatedText")) {
                                                        itemDef.setLabel(itemDefNode.getChildNodes().item(k).getChildNodes().item(l).getTextContent().trim());
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        itemGroupDef.getItemDefs().add(itemDef);
                                    }

                                }

                                formDef.getItemGroupDefs().add(itemGroupDef);
                            }
                        }

                        eventDef.getFormDefs().add(formDef);
                    }
                }

                eventDefs.add(eventDef);
            }
        }
        catch (XPathExpressionException e) {
            throw new ODMException(e);
        }

        return eventDefs;
    }

    public EventDefinition getStudyEventDef(String eventDefinitionOid) throws ODMException {
        EventDefinition eventDef = null;

        try {
            // StudyEventDef
            String expression = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/cdisc:StudyEventDef[@OID='" + eventDefinitionOid + "']";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(this.odm, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                eventDef = new EventDefinition();

                eventDef.setOid(nodeList.item(i).getAttributes().getNamedItem("OID").getNodeValue());
                eventDef.setName(nodeList.item(i).getAttributes().getNamedItem("Name").getNodeValue());
                eventDef.setIsRepeating(nodeList.item(i).getAttributes().getNamedItem("Repeating").getNodeValue().equals("Yes"));
                eventDef.setType(nodeList.item(i).getAttributes().getNamedItem("Type").getNodeValue());

                // Collect OpenClinica Event details
                for (int j = 0; j < nodeList.item(i).getChildNodes().getLength(); j++) {
                    if (nodeList.item(i).getChildNodes().item(j).getNodeName().equals("OpenClinica:EventDefinitionDetails")) {
                        Node ocEventDetails = nodeList.item(i).getChildNodes().item(j);
                        for (int k = 0; k < ocEventDetails.getChildNodes().getLength(); k++) {
                            if (ocEventDetails.getChildNodes().item(k).getNodeName().equals("OpenClinica:Description")) {
                                eventDef.setDescription(ocEventDetails.getChildNodes().item(k).getTextContent());
                            }
                            else if (ocEventDetails.getChildNodes().item(k).getNodeName().equals("OpenClinica:Category")) {
                                eventDef.setCategory(ocEventDetails.getChildNodes().item(k).getTextContent());
                            }
                        }
                    }
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return eventDef;
    }

    public ItemDefinition getStudyItemDef(String itemOid) throws ODMException {
        ItemDefinition itemDef = null;

        try {
            // ItemDef
            String itemDefQuery = "/cdisc:ODM/cdisc:Study/cdisc:MetaDataVersion/cdisc:ItemDef[@OID='" + itemOid + "']";
            Node itemDefNode = (Node) xPath.compile(itemDefQuery).evaluate(this.odm, XPathConstants.NODE);

            if (itemDefNode != null) {

                itemDef = new ItemDefinition();

                itemDef.setOid(itemDefNode.getAttributes().getNamedItem("OID").getNodeValue());
                itemDef.setName(itemDefNode.getAttributes().getNamedItem("Name").getNodeValue());
                itemDef.setDataType(itemDefNode.getAttributes().getNamedItem("DataType").getNodeValue());
                itemDef.setDescription(itemDefNode.getAttributes().getNamedItem("Comment").getNodeValue());

                for (int k = 0; k < itemDefNode.getChildNodes().getLength(); k++) {
                    if (itemDefNode.getChildNodes().item(k).getNodeName().equals("Question")) {
                        for (int l = 0; l < itemDefNode.getChildNodes().item(k).getChildNodes().getLength(); l++) {
                            if (itemDefNode.getChildNodes().item(k).getChildNodes().item(l).getNodeName().equals("TranslatedText")) {
                                itemDef.setLabel(itemDefNode.getChildNodes().item(k).getChildNodes().item(l).getTextContent().trim());
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return itemDef;
    }

    //endregion

    //endregion

    //region Private Methods

    private String getStringStudyParameter(String xPathQuery) throws ODMException {
        String result = null;

        Node node = this.getOdmNode(xPathQuery);
        Node valueNode = this.getValueAttribute(node);

        if (valueNode != null) {
            result = valueNode.getNodeValue();
        }

        return result;
    }

    private Boolean getBooleanStudyParameter(String xPathQuery) throws ODMException  {
        Boolean result = null;

        Node node = this.getOdmNode(xPathQuery);
        Node valueNode = this.getValueAttribute(node);
        if (valueNode != null) {
            result = valueNode.getNodeValue().equals(
                Boolean.toString(Boolean.TRUE).toLowerCase()
            );
        }

        return result;
    }

    private Node getValueAttribute(Node node) {
        if (node != null) {
            if (node.getAttributes() != null) {
                for (int i = 0; i < node.getAttributes().getLength(); i++) {
                    if (node.getAttributes().item(i) != null && node.getAttributes().item(i).getNodeName() != null) {
                        if (node.getAttributes().item(i).getNodeName().equals("Value")) {
                            return node.getAttributes().item(i);
                        }
                    }
                }
            }
        }

        return null;
    }

    private Node getOdmNode(String xPathQuery) throws ODMException {
        Node result;

        try {
            result = (Node) xPath.compile(xPathQuery).evaluate(odm, XPathConstants.NODE);
        }
        catch (XPathExpressionException err) {
            throw new ODMException(err);
        }

        return result;
    }

    //endregion

}
