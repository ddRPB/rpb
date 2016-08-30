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

import de.dktk.dd.rpb.core.service.BlockRandomisationStrategy;
import de.dktk.dd.rpb.core.service.IRandomisationStrategy;
import org.apache.log4j.Logger;

import javax.persistence.*;

/**
 * Configuration of block randomisation (both fixed and variable block size)
 *
 * @author tomas@skripcak.net
 * @since 24 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@DiscriminatorValue("BLOCK")
public class BlockRandomisationConfiguration extends AbstractRandomisationConfiguration {

    //region Enums

    // The size of variable block can be either multiplication of the proportion of the treatment arms or absolute flexible block sizes
    public enum TYPE {
        MULTIPLY, ABSOLUTE
    }

    //endregion

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(BlockRandomisationConfiguration.class);

    //endregion

    //region Members

    private int minimumBlockSize;
    private int maximumBlockSize;

    private TYPE type;

    //endregion

    //region Constructors

    public BlockRandomisationConfiguration() {
        super();
    }

    public BlockRandomisationConfiguration(long seed){
        super(seed);
    }

    //endregion

    //region Properties

    //region Minimum block size

    @Column(name = "MINBLOCKSIZE")
    public int getMinimumBlockSize() {
        return this.minimumBlockSize;
    }

    public void setMinimumBlockSize(int value) {
        this.minimumBlockSize = value;
    }

    //endregion

    //region Maximum block size

    @Column(name = "MAXBLOCKSIZE")
    public int getMaximumBlockSize() {
        return this.maximumBlockSize;
    }

    public void setMaximumBlockSize(int value) {
        this.maximumBlockSize = value;
    }

    //endregion

    //region Is block size variable

    @Transient
    public boolean isVariableBlockSize() {
        return this.maximumBlockSize != this.minimumBlockSize;
    }

    //endregion

    //region Type

    @Column(name="BLOCKSIZETYPE")
    @Enumerated(EnumType.STRING)
    public TYPE getType() {
        return this.type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    //endregion

    //region Block randomisation data

    @Override
    @OneToOne(mappedBy = "randomisationConfiguration", cascade={CascadeType.PERSIST,CascadeType.MERGE})
    public AbstractRandomisationData getData() {
        if (this.data == null) {
            this.data = new BlockRandomisationData();
            this.data.setRandomisationConfiguration(this);
            this.data.setId(this.getId());
        }

        return this.data;
    }

    //endregion

    //endregion

    //endregion

    //region Methods

    @Override
    public IRandomisationStrategy createScheme() {
        return new BlockRandomisationStrategy(this, super.getStudy(), super.getSeed());
    }

    //endregion

}