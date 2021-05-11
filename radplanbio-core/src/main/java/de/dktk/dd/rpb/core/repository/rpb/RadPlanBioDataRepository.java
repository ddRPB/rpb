package de.dktk.dd.rpb.core.repository.rpb;

import de.dktk.dd.rpb.core.dao.rpb.RadPlanBioDataDao;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default implementation of the {@link IRadPlanBioDataRepository} interface.
 * @see IRadPlanBioDataRepository
 *
 * RadPlanBioDataRepository
 *
 * @author tomas@skripcak.net
 */
@Named
@Singleton
public class RadPlanBioDataRepository implements IRadPlanBioDataRepository {

    //region Logging
    
    private static final Logger log = Logger.getLogger(RadPlanBioDataRepository.class);

    //endregion

    //region Injects

    protected RadPlanBioDataDao dao;

    @Inject
    public void setRadPlanBioDataDao(RadPlanBioDataDao dao) {
        this.dao = dao;
    }

    //endregion

    //region DefaultAccount

    @Override
    @Transactional(readOnly = true)
    public String getDefaultAccountUsernameByApiKey(String accountApiKey) {
        return this.dao.getDefaultAccountUsernameByApiKey(accountApiKey);
    }

    //endregion

    //region Pacs

    @Override
    @Transactional(readOnly = true)
    public String getPacsUrlByAccountApiKey(String accountApiKey) {
        return this.dao.getPacsUrlByAccountApiKey(accountApiKey);
    }

    //endregion

}
