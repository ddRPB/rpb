/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.service.IRandomisationStrategy;
import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

/*
 * Abstract RandomisationConfiguration domain entity
 *
 * @author tomas@skripcak.net
 * @since 24 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE")
@Table(name = "RANDCONFIG")
public abstract class AbstractRandomisationConfiguration implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AbstractRandomisationConfiguration.class);
    private final Long seed;

    //endregion

    //region Members

    private Integer id; // pk, fk from study because one to one
    private Study study; // RCT RadPlanBio study
    private IRandomisationStrategy scheme; // algorithmic part of randomisation

    protected AbstractRandomisationData data; // Generated data

    //endregion

    //region Constructors

    public AbstractRandomisationConfiguration(){
        this.seed = System.currentTimeMillis();
    }

    public AbstractRandomisationConfiguration(Long seed){
        this.seed = seed;
    }

    //endregion

    // region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GenericGenerator(name="randconfig_id_seq", strategy="foreign", parameters=@Parameter(name="property", value="study"))
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

    //region Seed

    @Transient
    public Long getSeed() {
        return this.seed;
    }

    //endregion

    //region RCT RadPlanBio study

    @OneToOne
    @PrimaryKeyJoinColumn
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
        if (study != null) {
            this.id = study.getId();
        }
    }

    //endregion

    // region Abstract randomisation data

    @OneToOne(mappedBy = "randomisationConfiguration", cascade={CascadeType.PERSIST,CascadeType.MERGE})
    public abstract AbstractRandomisationData getData();

    public void setData(AbstractRandomisationData data) {
        this.data = data;
    }

    //endregion

    //region Randomisation scheme interface

    @Transient
    public final IRandomisationStrategy getScheme() {
        if (this.scheme == null) {
            this.scheme = this.createScheme();
        }
        return this.scheme;
    }

    //endregion

    //endregion

    //region Abstract methods

    public abstract IRandomisationStrategy createScheme();

    //endregion

}
