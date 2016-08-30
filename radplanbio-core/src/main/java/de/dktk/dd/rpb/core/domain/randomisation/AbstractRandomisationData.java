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

package de.dktk.dd.rpb.core.domain.randomisation;

import de.dktk.dd.rpb.core.domain.Identifiable;

import org.apache.log4j.Logger;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/*
 * Abstract RandomisationData domain entity
 *
 * @author tomas@skripcak.net
 * @since 24 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE")
@Table(name="RANDDATA")
public abstract class AbstractRandomisationData implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AbstractRandomisationData.class);

    //endregion

    //region Members

    private Integer id; // pk, fk from randomisation configuration which is fk from study
    private AbstractRandomisationConfiguration randomisationConfiguration;

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GenericGenerator(name="randdata_id_seq", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="randomisationConfiguration"))
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

    //region RandomisationConfiguration

    @OneToOne
    @PrimaryKeyJoinColumn
    public AbstractRandomisationConfiguration getRandomisationConfiguration() {
        return this.randomisationConfiguration;
    }

    public void setRandomisationConfiguration(AbstractRandomisationConfiguration randomisationConfiguration) {
        this.randomisationConfiguration = randomisationConfiguration;

        if (this.randomisationConfiguration != null && this.randomisationConfiguration.getData() == null) {
            this.randomisationConfiguration.setData(this);
        }
    }

    //endregion

    //endregion

}