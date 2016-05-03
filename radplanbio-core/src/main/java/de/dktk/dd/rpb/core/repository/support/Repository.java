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

package de.dktk.dd.rpb.core.repository.support;

import java.io.Serializable;
import java.util.List;

import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.Identifiable;

/**
 * The interface to manipulate entities against a repository
 * @see Identifiable
 * @see SearchParameters
 * @see <a href="http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/dao.html">dao</a>
 */
public interface Repository<T extends Identifiable<PK>, PK extends Serializable> {

    /**
     * Create a new instance. This method may be convenient when you need to instantiate an entity from components like a Spring Web Flow script.
     *
     * @return a new instance with no property set.
     */
    T getNew();

    /**
     * Creates a new instance and initializes it with some default values.
     * This method may be convenient when you need to instantiate
     * an entity from a Spring Web Flow script.
     *
     * @return a new instance initialized with default values.
     */
    T getNewWithDefaults();

    /**
     * Get an entity by its id.
     *
     * @param id the primaryKey
     * @return entity
     */
    T getById(PK id);

    /**
     * Get an entity based on the passed example. If the example has a primary key set, it will be used. Otherwise
     * if the example has unique field set, it will be used to lookup the entity.
     *
     * @param example entity example
     * @return the matching entity or null if none could be found.
     */
    T get(T example);

    /**
     * Refresh the passed entity with up to date data.
     *
     * @param entity the entity to refresh.
     */
    void refresh(T entity);

    /**
     * Save or update the passed entity.
     *
     * @param entity the entity to save or update.
     */
    void save(T entity);

    /**
     * Merge the state of the given entity into the
     * current persistence context.
     *
     * @param entity the entity to merge.
     */
    T merge(T entity);

    /**
     * Delete from the repository the entity identified by the passed primary key.
     *
     * @param id the primaryKey identifying the entity to delete.
     */
    @SuppressWarnings("unused")
    void deleteById(PK id);

    /**
     * Delete from the repository the passed entity.
     *
     * @param entity The entity to delete.
     */
    void delete(T entity);

    /**
     * Find the unique entity matching the passed example.
     *
     * @param example entity example
     * @return the unique entity found.
     * @throws org.springframework.dao.InvalidDataAccessApiUsageException if no entity or more than one entity is found.
     */
    T findUnique(T example);

    /**
     * Find the unique entity matching the passed example and {@link SearchParameters}.
     *
     * @param exampleOrNamedQueryParameters entity example
     * @param searchParameters searchParameters {@link SearchParameters}
     * @return the unique entity found.
     * @throws org.springframework.dao.InvalidDataAccessApiUsageException if no entity or more than one entity is found.
     */
    T findUnique(T exampleOrNamedQueryParameters, SearchParameters searchParameters);

    /**
     * Find the unique entity matching the passed example.
     *
     * @param example entity example
     * @return the unique entity found or null if none could be found.
     * @throws org.springframework.dao.InvalidDataAccessApiUsageException if more than one entity is found.
     */
    T findUniqueOrNone(T example);

    /**
     * Find the unique entity matching the passed example and {@link SearchParameters}.
     *
     * @param example entity example
     * @return the unique entity found or null if none could be found.
     * @throws org.springframework.dao.InvalidDataAccessApiUsageException if more than one entity is found.
     */
    T findUniqueOrNone(T example, SearchParameters searchParameters);

    /**
     * Load all the entities, up to a certain limit. Implementation could for example load at most 500 entities.
     *
     * @return the list of matching entities or an empty list when no entity is found.
     */
    List<T> find();

    /**
     * Load all the entities, up to a certain limit. Implementation could for example load at most 500 entities.
     *
     * Use this methods when the 2lvl cache should not be considered
     * (e.g. when having multiple instances of entity manager and I want to ensure to load most freshed data from DB)
     */
    List<T> forceFind();

    /**
     * Load all the entities matching the passed example.
     *
     * @param example entity example
     * @return the list of matching entities or an empty list when no entity is found.
     */
    List<T> find(T example);

    /**
     * Load all the entities based on the passed {@link SearchParameters}..
     *
     * @param searchParameters searchParameters
     * @return the list of matching entities or an empty list when no entity is found.
     */
    List<T> find(SearchParameters searchParameters);

    /**
     * Load all the entities based on the passed exampleOrNamedQueryParameters and {@link SearchParameters}.
     *
     * @param exampleOrNamedQueryParameters entity example
     * @return the list of matching entities or an empty list when no entity is found.
     */
    List<T> find(T exampleOrNamedQueryParameters, SearchParameters searchParameters);

    /**
     * @return the number of entities that  {@link #find()} would return.
     */
    @SuppressWarnings("unused")
    int findCount();

    /**
     * @return the number of entities that {@link #find(Identifiable)} would return.
     */
    @SuppressWarnings("unused")
    int findCount(T example);

    /**
     * @return the number of entities that  {@link #find(SearchParameters)} would return.
     */
    @SuppressWarnings("unused")
    int findCount(SearchParameters searchParameters);

    /**
     * @return the number of entities that {@link #find(Identifiable, SearchParameters)} would return.
     */
    int findCount(T example, SearchParameters searchParameters);

}