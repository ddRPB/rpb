/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.service.DataTransformationService;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;


import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Bean for processing (transformation) of ODM data exports
 *
 * @author tomas@skripcak.net
 * @since 04 Jun 2013
 */
@Named("mbOdmData")
@Scope("view")
public class OdmDataBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    /**
     * Set MainBean
     *
     * @param bean MainBean
     */
    public void setMainBean(MainBean bean) {
        this.mainBean = bean;
    }

    //endregion

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    public void setStudyIntegrationFacade(StudyIntegrationFacade value) {
        this.studyIntegrationFacade = value;
    }

    //endregion

    //region DataTransformation

    @Inject
    private DataTransformationService svcDataTransformation;

    //endregion

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //endregion

    //region  Members

    private UploadedFile uploadedFile;
    private StreamedContent transformedFile;

    //endregion

    //region Properties

    //region OdmDataFile

    public UploadedFile getUploadedFile() {
        return this.uploadedFile;
    }

    public void setUploadedFile(UploadedFile file) {
        this.uploadedFile = file;
    }

    //endregion

    //region TransformedFile

    public StreamedContent getTransformedFile() {
        return this.transformedFile;
    }


    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        // Load initial data
        this.load();
    }

    //endregion

    //region Commands

    /**
     * Reload metadata
     */
    public void load() {
        try {
            // Reload the definitions
            this.studyIntegrationFacade.init(this.mainBean);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Transform ODM data
     */
    public void transformData() {
        try {
            String filename = this.uploadedFile.getFileName();

            // Almost correct import data (Subject key has to be loaded after study subject enrollment)
            Odm dataOdm = this.svcDataTransformation.transformXmlToOdm(
                    this.uploadedFile.getInputstream()
            );
            
            dataOdm.groupElementsByOid();

            // Load detected subjects
            if (dataOdm != null &&
                dataOdm.getClinicalDataList() != null &&
                dataOdm.getClinicalDataList().size() == 1) {

                // Path to RPB data folder
                String dataPath = mainBean.getMyAccount().getPartnerSite().getPortal().getDataPath();
                if (dataPath != null && !dataPath.isEmpty()) {
                    // Saxon
                    System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

                    // Create Transformer
                    TransformerFactory tf = TransformerFactory.newInstance();
                    StreamSource xslt = new StreamSource(dataPath + "/xslt/ODMToTAB.xsl");
                    Transformer transformer = tf.newTransformer(xslt);

                    // Source
                    JAXBContext context = JAXBContext.newInstance(Odm.class);
                    JAXBSource source = new JAXBSource(context, dataOdm);

                    // Result
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    Result result = new StreamResult(outputStream);

                    // Transform
                    transformer.transform(source, result);

                    // For download
                    InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
                    this.transformedFile = new DefaultStreamedContent(stream, "text/csv", filename + ".csv");
                }
            }
            else {
                throw new Exception("Failed to create clinical data model.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Event Handlers

    /**
     * Upload input data for import
     * @param event upload event
     */
    public void handleUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            this.messageUtil.infoText("File name: " +
                    event.getFile().getFileName() + " file size: " +
                    event.getFile().getSize() / 1024 + " Kb content type: " +
                    event.getFile().getContentType() + " The document file was uploaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

}
