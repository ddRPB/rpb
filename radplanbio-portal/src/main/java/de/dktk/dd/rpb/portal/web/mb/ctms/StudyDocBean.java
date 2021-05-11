/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb.ctms;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.ctms.StudyDoc;
import de.dktk.dd.rpb.core.repository.ctms.IStudyDocRepository;
import de.dktk.dd.rpb.core.service.IFileStorageService;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for administration of CTMS organisations
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbStudyDoc")
@Scope(value="view")
public class StudyDocBean extends CrudEntityViewModel<StudyDoc, Integer> {

    //region Finals

    private static final int BUFFER_SIZE = 6124;

    //endregion

    //region Injects

    //region Repository

    @Inject
    private IStudyDocRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IStudyDocRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyDocRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Services

    @Inject
    private IFileStorageService fileStorageService;

    @SuppressWarnings("unused")
    public void setFileStorageService(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    //endregion

    //endregion

    //region Properties

    public StreamedContent getFile() {
        //ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

        String extension = FilenameUtils.getExtension(this.selectedEntity.getFilename());
//        InputStream stream = ((ServletContext)extContext.getContext()).getResourceAsStream(
//                "/WEB-INF/files/" + this.selectedEntity.getStudy().getId() + "/" + this.selectedEntity.getFilename()
//        );

        InputStream stream = null;
        try {
            File file = this.fileStorageService.getFile("files/" + this.selectedEntity.getStudy().getId() + "/" + this.selectedEntity.getFilename());
            stream = new FileInputStream(file);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        return new DefaultStreamedContent(stream, "application/" + extension, this.selectedEntity.getFilename());
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = new ArrayList<Boolean>();
        this.columnVisibilityList.add(Boolean.TRUE); // DisplayName
        this.columnVisibilityList.add(Boolean.FALSE); // FileName
        this.columnVisibilityList.add(Boolean.TRUE); // DocType

        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column1 = viewRoot.findComponent(":form:tabView:dtStudyDocs:colDocType");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column1);
        sm1.setSortField("colDocType");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);


        return results;
    }

    //endregion

    //region EventHandlers

    //region Upload/Download

    /**
     * Upload document
     *
     * @param event event
     */
    public void handleDocUpload(FileUploadEvent event) {
        Study study = (Study)(event.getComponent().getAttributes().get("study"));

        try {
            // Check folder structure
            File studyFolder = this.fileStorageService.getFile("files//" + study.getId() + "//");
            if (!studyFolder.exists()) {
                if (!studyFolder.mkdir()) {
                    throw new IOException("Failed to create a folder for study docs.");
                }
            }

            // Check file if it exists
            File result = this.fileStorageService.getFile("files//" + study.getId() + "//" + event.getFile().getFileName());
            if (result.exists()) {
                throw new IOException("Study doc with the same filename already exists for the study.");
            }

            FileOutputStream fileOutputStream = new FileOutputStream(result);
            byte[] buffer = new byte[BUFFER_SIZE];

            int bulk;
            InputStream inputStream = event.getFile().getInputstream();
            while (true) {
                bulk = inputStream.read(buffer);
                if (bulk < 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, bulk);
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            inputStream.close();

            this.messageUtil.infoText("File Description: " + "file name: " +
                    event.getFile().getFileName() + " file size: " +
                    event.getFile().getSize() / 1024 + " Kb content type: " +
                    event.getFile().getContentType() + " The document file was uploaded.");

            // Setup filename to the db object
            this.newEntity.setFilename(event.getFile().getFileName());
        }
        catch (IOException e) {
            this.messageUtil.error(e);
        }
    }

    //endregion

    //endregion

}