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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import java.util.HashMap;
import java.util.Map;

/**
 * This randomisation data represents generated sequence of blocks from block randomisation
 *
 * @author tomas@skripcak.net
 * @since 23 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@DiscriminatorValue("BLOCK")
public class BlockRandomisationData extends AbstractRandomisationData {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(BlockRandomisationData.class);

    //endregion

    //region Members

    // One to many
    private Map<String, Block> blocks;

    //endregion

    //region Constructors

    public BlockRandomisationData() {
        if (this.blocks == null) {
            this.blocks = new HashMap<>();
        }
    }

    //endregion

    //region Properties

    //region Blocks - separate block for each strata

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "blockData", cascade = CascadeType.ALL)
    @MapKeyColumn(name="MAPKEY")
    public Map<String, Block> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(Map<String, Block> blocks) {
        if (this.blocks == null) {
            this.blocks = new HashMap<>();
        }

        this.blocks = blocks;
    }

    public Block getBlock(String stratum) {
        return blocks != null ? blocks.get(stratum) : null;
    }

    public void setBlock(String stratum, Block currentBlock) {
        blocks.put(stratum, currentBlock);

        if (currentBlock != null) {
            currentBlock.setBlockData(this);
            currentBlock.setMapkey(stratum);
        }
    }

    //endregion

    //endregion

}
