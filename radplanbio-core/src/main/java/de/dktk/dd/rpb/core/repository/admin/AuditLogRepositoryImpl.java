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

package de.dktk.dd.rpb.core.repository.admin;

import de.dktk.dd.rpb.core.dao.admin.AuditLogDao;
import de.dktk.dd.rpb.core.dao.support.GenericDao;
import de.dktk.dd.rpb.core.domain.admin.AuditLog;
import de.dktk.dd.rpb.core.repository.support.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link AuditLogRepository} interface.
 * Note: you may use multiple DAO from this layer.
 * @see AuditLogRepository
 *
 * AuditLogRepository
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Named("auditLogRepository")
@Singleton
public class AuditLogRepositoryImpl extends RepositoryImpl<AuditLog, Integer> implements AuditLogRepository {

    //region Finals

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AuditLogRepositoryImpl.class);

    //endregion

    //region Injects

    protected AuditLogDao auditLogDao;

    @Inject
    public void setAuditLogDao(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    /**
     * Dao getter used by the {@link RepositoryImpl}.
     */
    @Override
    public GenericDao<AuditLog, Integer> getDao() {
        return this.auditLogDao;
    }

    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLog getNew() {
        return new AuditLog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLog getNewWithDefaults() {
        return this.getNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AuditLog get(AuditLog model) {
        if (model == null) {
            return null;
        }

        if (model.isIdSet()) {
            return super.get(model);
        }

        return null;
    }

}
