/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.core.domain.lab;

import de.dktk.dd.rpb.core.domain.edc.EnumCollectSubjectDob;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.StudyParameterConfiguration;
import de.dktk.dd.rpb.core.util.labkey.ItemDefinitionClinicaComparator;
import de.dktk.dd.rpb.core.util.labkey.ItemDefinitionDefaultComparator;

import java.util.Comparator;

/**
 * Configuration for the LabKey export.
 */
public class LabKeyExportConfiguration {

    // settings from the UI
    private boolean exportOnlyFormFormBasedCrfAttributesWithData;
    private boolean exportOnlyItemGroupBasedCrfAttributesWithData;
    private boolean exportOnlyRepeatingItemGroupBasedCrfAttributes;

    private boolean addDecodedValueColumns;
    private boolean addMultiSelectValueColumns;
    private boolean addPartialDateColumns;

    // settings based on the study configuration
    private boolean sexRequired;
    private boolean fullDateOfBirthRequired;
    private boolean yearOfBirthRequired;

    // helps to sort the ItemDefinitions in a specific order
    private Comparator<ItemDefinition> itemComparator;

    // region Constructor

    public LabKeyExportConfiguration(StudyParameterConfiguration configuration) {
        this.initDefaults();
        this.overwriteDefaultsWithStudySpecificSettings(configuration);
    }

    // endregion

    // region Getter and Setter

    public boolean isExportOnlyFormFormBasedCrfAttributesWithData() {
        return exportOnlyFormFormBasedCrfAttributesWithData;
    }

    public void setExportOnlyFormFormBasedCrfAttributesWithData(boolean exportOnlyFormFormBasedCrfAttributesWithData) {
        this.exportOnlyFormFormBasedCrfAttributesWithData = exportOnlyFormFormBasedCrfAttributesWithData;
    }

    public boolean isExportOnlyItemGroupBasedCrfAttributesWithData() {
        return exportOnlyItemGroupBasedCrfAttributesWithData;
    }

    public void setExportOnlyItemGroupBasedCrfAttributesWithData(boolean exportOnlyItemGroupBasedCrfAttributesWithData) {
        this.exportOnlyItemGroupBasedCrfAttributesWithData = exportOnlyItemGroupBasedCrfAttributesWithData;
    }

    public boolean isExportOnlyRepeatingItemGroupBasedCrfAttributes() {
        return exportOnlyRepeatingItemGroupBasedCrfAttributes;
    }

    public void setExportOnlyRepeatingItemGroupBasedCrfAttributes(boolean exportOnlyRepeatingItemGroupBasedCrfAttributes) {
        this.exportOnlyRepeatingItemGroupBasedCrfAttributes = exportOnlyRepeatingItemGroupBasedCrfAttributes;
    }

    public boolean isAddDecodedValueColumns() {
        return addDecodedValueColumns;
    }

    public void setAddDecodedValueColumns(boolean addDecodedValueColumns) {
        this.addDecodedValueColumns = addDecodedValueColumns;
    }

    public boolean isAddMultiSelectValueColumns() {
        return addMultiSelectValueColumns;
    }

    public void setAddMultiSelectValueColumns(boolean addMultiSelectValueColumns) {
        this.addMultiSelectValueColumns = addMultiSelectValueColumns;
    }

    public boolean isAddPartialDateColumns() {
        return addPartialDateColumns;
    }

    public void setAddPartialDateColumns(boolean addPartialDateColumns) {
        this.addPartialDateColumns = addPartialDateColumns;
    }

    public boolean isSexRequired() {
        return sexRequired;
    }

    public void setSexRequired(boolean sexRequired) {
        this.sexRequired = sexRequired;
    }

    public boolean isFullDateOfBirthRequired() {
        return fullDateOfBirthRequired;
    }

    public void setFullDateOfBirthRequired(boolean fullDateOfBirthRequired) {
        this.fullDateOfBirthRequired = fullDateOfBirthRequired;
    }

    public boolean isYearOfBirthRequired() {
        return yearOfBirthRequired;
    }

    public void setYearOfBirthRequired(boolean yearOfBirthRequired) {
        this.yearOfBirthRequired = yearOfBirthRequired;
    }

    public Comparator<ItemDefinition> getItemComparator() {
        return itemComparator;
    }

    public void setItemComparator(Comparator<ItemDefinition> itemComparator) {
        this.itemComparator = itemComparator;
    }

    public ItemDefinitionClinicaComparator getFormItemComparator(String formOid) {
        return new ItemDefinitionClinicaComparator(formOid);
    }

    // endregion

    // region Methods

    private void initDefaults() {
        this.exportOnlyFormFormBasedCrfAttributesWithData = true;

        this.exportOnlyItemGroupBasedCrfAttributesWithData = true;
        this.exportOnlyRepeatingItemGroupBasedCrfAttributes = true;

        this.addDecodedValueColumns = false;
        this.addMultiSelectValueColumns = false;
        this.addPartialDateColumns = false;

        this.sexRequired = true;
        this.fullDateOfBirthRequired = false;
        this.yearOfBirthRequired = false;

        itemComparator = new ItemDefinitionDefaultComparator();
    }

    private void overwriteDefaultsWithStudySpecificSettings(StudyParameterConfiguration configuration) {
        // study collects gender
        this.setSexRequired(configuration.getSexRequired());

        // study collects date of birth or year of birth
        EnumCollectSubjectDob collectSubjectDob = configuration.getCollectSubjectDob();
        switch (collectSubjectDob){
            case YES:
                this.setFullDateOfBirthRequired(true);
                break;
            case ONLY_YEAR:
                this.setYearOfBirthRequired(true);
                break;
            case NO:
                this.setFullDateOfBirthRequired(false);
                this.setYearOfBirthRequired(false);
        }
    }

    // endregion
}
