/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

package de.dktk.dd.rpb.core.domain.edc;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * FormData domain entity (based on CDISC ODM FormData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="FormData")
public class FormData implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(FormData.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="FormOID")
    private String formOid;

    @XmlAttribute(name="FormName", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String formName;

    @XmlAttribute(name="Version", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String version;

    @XmlAttribute(name="VersionDescription", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String versionDescription;

    @XmlAttribute(name="InterviewerName", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String interviewerName;

    @XmlAttribute(name="InterviewDate", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String interviewDate;

    @XmlAttribute(name="Status", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String status;

    @XmlAttribute(name="StatusChangeTimeStamp", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String statusChangeTimeStamp;

    @XmlAttribute(name="Url", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String url;

    @XmlTransient
    private FormDefinition formDefinition;

    @XmlElement(name="ItemGroupData")
    private List<ItemGroupData> itemGroupDataList;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public FormData() {
        // NOOP
    }

    public FormData(String formOid) {
        this();

        this.formOid = formOid;
    }

    //endregion

    //region Properties

    //region Id

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region FormOID

    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String value) {
        this.formOid = value;
    }

    //endregion

    //region FormName

    public String getFormName() {
        if (this.formName != null) {
            return this.formName;
        }
        else if (this.formDefinition != null) {
            return this.formDefinition.getName();
        }

        return null;
    }

    public void setFormName(String value) {
        this.formName = value;
    }

    //endregion

    //region Version

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

    //endregion

    //region VersionDescription

    public String getVersionDescription() {
        return this.versionDescription;
    }

    public void setVersionDescription(String value) {
        this.versionDescription = value;
    }

    //endregion

    //region InterviewerName

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    //endregion

    //region InterviewDate

    public String getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(String interviewerDate) {
        this.interviewDate = interviewerDate;
    }

    //endregion

    //region Status

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    //endregion

    //region StatusChangeTimeStamp

    public String getStatusChangeTimeStamp() {
        return statusChangeTimeStamp;
    }

    public void setStatusChangeTimeStamp(String statusChangeTimeStamp) {
        this.statusChangeTimeStamp = statusChangeTimeStamp;
    }

    //endregion

    //region Url

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    //endregion

    //region FormDefinition

    public FormDefinition getFormDefinition() {
        return formDefinition;
    }

    public void setFormDefinition(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }

    //endregion

    //region ItemGroupDataList

    public List<ItemGroupData> getItemGroupDataList() {
        return this.itemGroupDataList;
    }

    public void setItemGroupDataList(List<ItemGroupData> list) {
        this.itemGroupDataList = list;
    }

    /**
     * Helper method to add the passed {@link ItemGroupData} to the itemGroupDataList list
     */
    public boolean addItemGroupData(ItemGroupData itemGroupData) {
        if (!this.containsItemGroupData(itemGroupData)) {
            if (this.itemGroupDataList == null) {
                itemGroupDataList = new ArrayList<>();
            }
            return this.itemGroupDataList.add(itemGroupData);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link ItemGroupData} from the itemGroupDataList list
     */
    public boolean removeItemGroupData(ItemGroupData itemGroupData) {
        return this.containsItemGroupData(itemGroupData) && this.itemGroupDataList.remove(itemGroupData);
    }

    /**
     * Helper method to determine if the passed {@link ItemGroupData} is present in the itemGroupDataList list
     */
    public boolean containsItemGroupData(ItemGroupData itemGroupData) {
        return this.itemGroupDataList != null && this.itemGroupDataList.contains(itemGroupData);
    }

    /**
     * Helper method to determine whether passed group OID is present in the itemGroupDataList list
     * @param itemGroupOid itemGroupOid to lookup for
     * @return true if the itemGroupData with specified form OID exists within form
     */
    public boolean containsItemGroupData(String itemGroupOid) {
        if (this.itemGroupDataList != null) {
            for (ItemGroupData itemGroupData : this.itemGroupDataList) {
                if (itemGroupData.getItemGroupOid().equals(itemGroupOid)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper method to get itemGroupData according to form OID
     * @param itemGroupOid formOid to lookup for
     * @return itemGroupData if exists
     */
    public ItemGroupData findItemGroupData(String itemGroupOid) {
        if (this.itemGroupDataList != null) {
            for (ItemGroupData itemGroupData : this.itemGroupDataList) {
                if (itemGroupData.getItemGroupOid().equals(itemGroupOid)) {
                    return itemGroupData;
                }
            }
        }

        return null;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof FormData && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.formOid);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("formOid", this.formOid)
                .toString();
    }

    //endregion

    //region Methods

    //region Metadata

    public void linkOdmDefinitions(Odm odm) {
        if (odm != null) {

            // FormDefinition linking
            FormDefinition formDefinition = odm.findUniqueFormDefinitionOrNone(this.formOid);
            if (formDefinition != null) {
                this.setFormDefinition(formDefinition);
            }

            // Link next level in hierarchy (ItemGroup)
            if (this.itemGroupDataList != null) {
                for (ItemGroupData itemGroupData : this.itemGroupDataList) {
                    itemGroupData.linkOdmDefinitions(odm);
                }
            }
        }
    }

    //endregion

    //region ItemGroupData

    public ItemGroupData getItemGroupData(ItemData itemData) {

        if (this.itemGroupDataList != null) {
            for (ItemGroupData igd : this.itemGroupDataList) {
                if (igd.containsItemData(itemData)) {
                    return igd;
                }
            }
        }

        return null;
    }

    /**
     * Sort ItemGroupData elements so that the repeating groups are the last
     */
    public void sortItemGroupData() {
        if (this.itemGroupDataList != null && this.itemGroupDataList.size() > 0) {

            // First locate how many different group OID exists
            List<String> itemGroups = new ArrayList<>();
            for (ItemGroupData igd : this.itemGroupDataList) {
                if (!itemGroups.contains(igd.getItemGroupOid())) {
                    itemGroups.add(igd.getItemGroupOid());
                }
            }

            // Then cluster item groups (in n lists)
            List<List<ItemGroupData>> masterList = new ArrayList<>();
            for (String groupOid : itemGroups) {

                List<ItemGroupData> groupList = new ArrayList<>();
                for (ItemGroupData igd : this.itemGroupDataList) {
                    if (igd.getItemGroupOid().equals(groupOid)) {
                        groupList.add(igd);
                    }
                }

                // Sort each list according to ItemGroupRepeatKey
                Collections.sort(groupList, new Comparator<ItemGroupData>() {
                    public int compare(ItemGroupData groupData1, ItemGroupData groupData2) {
                    int result = 0;

                    // Both are repeating
                    if (groupData1.getItemGroupRepeatKey() != null && !groupData1.getItemGroupRepeatKey().isEmpty() &&
                        groupData2.getItemGroupRepeatKey() != null && !groupData2.getItemGroupRepeatKey().isEmpty()) {

                        result = Integer.compare(Integer.parseInt(groupData1.getItemGroupRepeatKey()), Integer.parseInt(groupData2.getItemGroupRepeatKey()));
                    }

                    return result;
                    }
                });

                masterList.add(groupList);
            }

            // Sort lists in master list according to how many elements they have
            Collections.sort(masterList, new Comparator<List<ItemGroupData>>() {
                public int compare(List<ItemGroupData> list1, List<ItemGroupData> list2) {
                    return Integer.compare(list1.size(), list2.size());
                }
            });

            // Add all sorted ItemGroupData into new itemGroupDataList
            List<ItemGroupData> newList = new ArrayList<>();
            for (List<ItemGroupData> igdl : masterList) {
                newList.addAll(igdl);
            }

            this.itemGroupDataList = newList;

            // Make un-grouped groups go first
            Collections.sort(this.itemGroupDataList, new Comparator<ItemGroupData>() {
                public int compare(ItemGroupData groupData1, ItemGroupData groupData2) {
                int result = 0;

                // Un-grouped always first
                if (groupData1.getItemGroupOid() != null && groupData1.getItemGroupOid().contains(Constants.OC_IG_UNGROUPED)) {
                    result = -1;
                }
                else if (groupData2.getItemGroupOid() != null && groupData2.getItemGroupOid().contains(Constants.OC_IG_UNGROUPED)) {
                    result = 1;
                }
                else {
                    // First is repeating group
                    if (groupData1.getItemGroupRepeatKey() != null && !groupData1.getItemGroupRepeatKey().isEmpty()) {
                        result = 1;
                    }
                    // Second is repeating group
                    else if (groupData2.getItemGroupRepeatKey() != null && !groupData2.getItemGroupRepeatKey().isEmpty()) {
                        result = -1;
                    }
                }

                return result;
                }
            });
        }
    }

    //endregion

    //region ItemData

    public List<ItemData> getAllItemData() {
        List<ItemData> results = new ArrayList<>();

        boolean sort = true;
        if (this.itemGroupDataList != null) {
            for (ItemGroupData igd : this.itemGroupDataList) {
                if (igd.getItemDataList() != null) {
                    for (ItemData id : igd.getItemDataList()) {
                        // Ignore items that have data but have been removed from study (Definition is not in metadata)
                        if (id.getStatus() != null) {
                            if (!EnumItemDataStatus.INVALID.toString().equals(id.getStatus())) {
                                results.add(id);
                            }
                        }
                        else {
                            results.add(id);
                            sort = false;
                        }
                    }
                }
            }
        }

        // TODO: I have put sorting here but, I would like to have this handled in UI normally
        if (sort) {
            Collections.sort(results, new Comparator<ItemData>() {
                @Override
                public int compare(ItemData data2, ItemData data1) {
                    return data2.getOrderInForm().compareTo(data1.getOrderInForm());
                }
            });
        }

        return results;
    }

    public List<ItemData> findAnnotatedItemData(CrfFieldAnnotation annotation) {
        List<ItemData> result = new ArrayList<>();

        if (annotation != null && this.formOid.equals(annotation.getFormOid())) {
            if (this.itemGroupDataList != null) {
                for (ItemGroupData itemGroupData : this.itemGroupDataList) {
                    result.addAll(itemGroupData.findAnnotatedItemData(annotation));
                }
            }
        }

        return result;
    }

    //endregion

    //endregion

}