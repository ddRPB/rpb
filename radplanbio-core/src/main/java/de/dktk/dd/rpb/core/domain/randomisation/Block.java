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
import de.dktk.dd.rpb.core.domain.ctms.Study;

import de.dktk.dd.rpb.core.util.MathUtil;
import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

/**
 * Permuted block of treatment options for clinical trial randomisation
 *
 * @author tomas@skripcak.net
 * @since 23 Jan 2014
 *
 *  Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Table(name = "RANDBLOCK")
public class Block implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Block.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment, serial
    private String mapkey; // block identificator string (strata included)

    // Many to one
    private BlockRandomisationData blockData;

    // Many to many
    private List<TreatmentArm> content; // current state of the randomisation block

    //endregion

    //region Static Methods

    /**
     * Initialise a new block for Randomised Clinical Trial study
     * Minimal block has a size of study treatment arms count and each arm is there once
     *
     * @param study RadPlanBio study (Randomised Clinical Trial RCT)
     * @return newly initialised randomisation block
     */
    public static Block initialise(Study study) {
        Block newBlock = new Block();

        List<TreatmentArm> arms = study.getTreatmentArms();

        // Array of planned subjects counts for each treatment arm
        int[] sizes = new int[arms.size()];
        int i = 0;
        for (TreatmentArm arm : arms) {
            sizes[i] = arm.getPlannedSubjectsCount();
            i++;
        }

        // Calculate greatest common divisor from all treatment arm planned subjects counts
        int divisor = sizes[0];
        for (i = 1; i < sizes.length; i++) {
            divisor = MathUtil.gcd(divisor, sizes[i]);
        }

        // Now I have a divisor which can divide planned count for every treatment arm
        for (TreatmentArm arm : arms) {
            // So determine how many occurrences of each arm I will push into the new block
            int size = arm.getPlannedSubjectsCount() / divisor;
            for (i = 0; i <  size; i++) {
                newBlock.push(arm);
            }
        }

        // It is guaranteed that in new block there will be at least one occurrence of each treatment arm
        return newBlock;
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "randblock_id_seq")
    @SequenceGenerator(name = "randblock_id_seq", sequenceName = "randblock_id_seq")
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

    //region MapKey - strata

    @Column(name = "MAPKEY")
    public String getMapkey() {
        return this.mapkey;
    }

    public void setMapkey(String mapkey) {
        this.mapkey = mapkey;
    }

    //endregion

    //region BlockData

    @ManyToOne
    @JoinColumn(name="DATAID")
    public BlockRandomisationData getBlockData() {
        return this.blockData;
    }

    public void setBlockData(BlockRandomisationData data) {
        this.blockData = data;
    }

    //endregion

    //region Block Content - bunch of treatment arms options

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = { PERSIST, MERGE })
    @JoinTable(name = "BLOCKARM",  joinColumns = { @JoinColumn(name = "BLOCKID") }, inverseJoinColumns = { @JoinColumn(name = "ARMID") })
    public List<TreatmentArm> getContent() {
        return this.content;
    }

    public void setContent(List<TreatmentArm> content) {
        this.content = content;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Put provided treatment arm to the block content
     *
     * @param arm TreatmentArm
     */
    public void push(TreatmentArm arm) {
        if (this.content == null) {
            this.content = new ArrayList<>();
        }
        this.content.add(arm);
    }

    /**
     * Randomly pull treatment arm from the block content
     *
     * @param random Random number generator
     * @return pulled TreatmentArm
     */
    public TreatmentArm pull(Random random) {
        TreatmentArm pulledArm = null;

        try {
            if (this.content.isEmpty()) {
                throw new Exception("Randomisation block is empty and you cannot pull the treatment option from it. You have to initialise a new block.");
            }

            // Randomly generate index which element from block content will be pulled
            int i = random.nextInt(this.content.size());

            // Remove the arm from block content
            pulledArm = this.content.remove(i);
        }
        catch (Exception err) {
            log.error(err.getMessage(), err);
        }

        return pulledArm;
    }

    /**
     * Check whether the block content is emtpy
     *
     * @return true if the block content is empty
     */
    @Transient
    public boolean isEmpty() {
        return this.content.isEmpty();
    }

    //endregion

}