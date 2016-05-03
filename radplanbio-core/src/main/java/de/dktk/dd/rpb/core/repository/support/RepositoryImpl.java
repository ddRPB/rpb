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

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;

import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.Identifiable;

/**
 * Default implementation of the {@link Repository}
 *
 * @see Repository
 */
public abstract class RepositoryImpl<T extends Identifiable<PK>, PK extends Serializable> implements Repository<T, PK> {

    private Logger log;

    public RepositoryImpl() {
        this.log = Logger.getLogger(getClass());
    }

    abstract public GenericDao<T, PK> getDao();

    /**
     * {@inheritDoc}
     */
    public abstract T getNew();

    /**
     * {@inheritDoc}
     */
    public abstract T getNewWithDefaults();

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void save(T model) {
        getDao().save(model);
    }

    @Transactional
    public T merge(T model) {
        return getDao().merge(model);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T getById(PK id) {
        T entity = getNew();
        entity.setId(id);
        return get(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void deleteById(PK id) {
        delete(getById(id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T get(T model) {
        return getDao().get(model);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void delete(T model) {
        if (model == null) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping deletion for null model!");
            }

            return;
        }

        getDao().delete(model);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public void refresh(T entity) {
        getDao().refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUnique(T model) {
        return findUnique(model, new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUnique(T model, SearchParameters sp) {
        return getDao().findUnique(model, sp);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUniqueOrNone(T model) {
        return findUniqueOrNone(model, new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public T findUniqueOrNone(T model, SearchParameters sp) {
        return getDao().findUniqueOrNone(model, sp);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find() {
        return find(getNew(), new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> forceFind() {
        return this.getDao().forceFind(getNew(), new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(T model) {
        return find(model, new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(SearchParameters sp) {
        return find(getNew(), sp);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<T> find(T model, SearchParameters sp) {
        return getDao().find(model, sp);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount() {
        return findCount(getNew(), new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount(T model) {
        return findCount(model, new SearchParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount(SearchParameters sp) {
        return findCount(getNew(), sp);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int findCount(T model, SearchParameters sp) {
        return getDao().findCount(model, sp);
    }
}