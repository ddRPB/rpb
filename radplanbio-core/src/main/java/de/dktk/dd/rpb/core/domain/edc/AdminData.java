/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

import de.dktk.dd.rpb.core.domain.Identifiable;
import org.apache.log4j.Logger;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * OpenClinica AdminData transient domain entity
 *
 * @author gruhlmirko@gmx.de
 * @since 14 Aug 2018
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AdminData")
public class AdminData implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(AdminData.class);

    //endregion

    //region Members

    private Integer id;

    @XmlAttribute(name="StudyOID")
    private String studyOID;

    @XmlElement(name="User")
    private List<User> user;

    //endregion


    //region Constructors

    public AdminData() {
        //NOOP
    }

    /**
     * Copy constructor
     */
    public AdminData(AdminData adminData) {
        this.id = adminData.getId();
        this.studyOID = adminData.studyOID;
        this.user = adminData.user;
    }

    //endregion

    //region Properties

    public String getStudyOID() {
        return studyOID;
    }

    public void setStudyOID(String studyOID) {
        this.studyOID = studyOID;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {

    }

    @Override
    public boolean isIdSet() {
        return false;
    }

    //endregion
}
