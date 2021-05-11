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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.randomisation.*;

import java.util.Random;

/**
 * Clinical Trial Randomisation strategy - Block
 *
 * @author tomas@skripcak.net
 * @since 16 Sep 2013
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
public class BlockRandomisationStrategy implements IRandomisationStrategy {

    //region Members

    private Study study;
    private BlockRandomisationConfiguration configuration;
    private Random random;
    private long seed;

    //endregion

    //region Constructor

    /**
     *
     * @param study RCT study in RadPlanBio
     * @param seed System.currentTimeMillis() as a seed is the default seed for Random() constructor
     */
    public BlockRandomisationStrategy(BlockRandomisationConfiguration configuration, Study study, long seed) {
        this.configuration = configuration;
        this.study = study;
        this.seed = seed;
        this.random = new Random(seed);
    }

    //endregion

    //region Properties

    @SuppressWarnings("unused")
    public long getSeed(){
        return seed;
    }

    public boolean isSeeded() {
        return this.random != null;
    }

    //endregion

    @SuppressWarnings("unused")
    private Random getRandom() {
        if (isSeeded()) {
            return this.random;
        } else {
            return new Random();
        }
    }

    //region Methods

    /**
     * {@inheritDoc}
     */
    public TreatmentArm randomise(TrialSubject subject) {

        // tempData.blocks is a hash map of blocks with stratum as key
        BlockRandomisationData tempData = (BlockRandomisationData)this.configuration.getData();

        // Stratification
        String strata = "";

        // If stratification according to partner site use unique partner site identifier as strata
        if(this.study.getIsStratifyTrialSite()) {
            strata = subject.getTrialSite().getIdentifier() + "__";
        }

        // Stratification according to subject properties
        strata += subject.getStrata();

        // Search if not empty block exists
        Block block = tempData.getBlock(strata);

        // If not generate a new block
        if (block == null || block.isEmpty()) {
            block = generateNewBlock(random, block);
            tempData.setBlock(strata, block);
        }

        return block.pull(random);
    }

    /**
     * Generate new block
     * @param random Random
     * @param emptyBlock Block
     * @return newly generated block with generated block size
     */
    protected Block generateNewBlock(Random random, Block emptyBlock) {
        // Generate block size
        int blockSize = this.calculateBlockSize(random);

        // When empty block is not empty or is null create a new non initialised block
        // otherwise use it as it is
        Block block;
        if(emptyBlock == null || !emptyBlock.isEmpty()){
            block = new Block();
        }
        else {
            block = emptyBlock;
        }

        // Initialise the raw block with minimal size
        // This one is used for filling the generated block with content elements (Treatment arms)
        Block rawBlock = Block.initialise(this.study);

        // Add new treatment arms to block content until it reach the calculated block size
        int i = 0;
        while (i < blockSize) {
            if (rawBlock.isEmpty()) {
                rawBlock = Block.initialise(this.study);
            }

            block.push(rawBlock.pull(random));
            i++;
        }

        return block;
    }

    /**
     * Calculate the size of the new block according to configuration
     *
     * @param random Random
     * @return calculated size for the new block
     */
    protected int calculateBlockSize(Random random) {

        // Check the configuration of block randomisation
        int range = this.configuration.getMaximumBlockSize() - this.configuration.getMinimumBlockSize() + 1;

        // Zero range when maximum is negative or zero
        // When the range is zero the block size is fixed = minimum
        if (this.configuration.getMaximumBlockSize() <= 0) { range = 0; }

        // Get random integer within range <0, range - 1> + minimum = <minimum, maximum>
        int size = random.nextInt(range) + this.configuration.getMinimumBlockSize();

        // Type of block randomisation is configured to be multiply instead of absolute
        // than block size has to be multiplication of minimal block size (minimal block size not from configuration
        // but from the proportion of treatment arms, based on number of subjects in arms)
        if (this.configuration.getType() == BlockRandomisationConfiguration.TYPE.MULTIPLY) {

            int minBlockSize = minimumBlockSize(this.study);

            // While determined size of block is not multiplication of minimal block size for the study
            // generate the new random size within range <0, range - 1> + minimum = <minimum, maximum>
            while(size % minBlockSize != 0) {
                size =  random.nextInt(range) + this.configuration.getMinimumBlockSize();
            }
        }
        return size;
    }

    //endregion

    //region Private methods

    /**
     * Determine minimum block size for provided study (based on planned number of subjects within each treatment arm)
     *
     * @param study RadPlanBio study
     * @return integer represented minimal block size
     */
    private int minimumBlockSize(Study study){
        return Block.initialise(study).getContent().size();
    }

    //endregion

}
