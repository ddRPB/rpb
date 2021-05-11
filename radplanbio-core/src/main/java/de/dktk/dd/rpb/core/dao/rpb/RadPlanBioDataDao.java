/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.core.dao.rpb;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.util.Constants;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * RadPlanBio Database Dao - data layer abstraction
 *
 * @author tomas@skripcak.net
 * @since 14 Jun 2013
 */
@Named
@Singleton
public class RadPlanBioDataDao extends JdbcDaoSupport {

    //region Methods

    //region DefaultAccount

    public String getDefaultAccountUsernameByApiKey(String apiKey) {
        String result = "";

        // Only if API key belong to enabled, non locked user account
        String sql = "SELECT da.username as Username FROM defaultaccount da " +
                "WHERE da.isenabled = TRUE and da.apikeyenabled = TRUE and " +
                "da.nonlocked = TRUE and " +
                "da.apikey = ?";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, apiKey);

        // Can be multiple users with the same name but I want to have not LDAP user that is used for SOAP WS
        for (Map<String, Object> row : rows) {
            String username = (String) row.get("Username");

            // Skip LDAP and participate accounts
            if (username != null && !username.isEmpty()) {
                result = username;
                break;
            }
        }

        return result;
    }

    //endregion

    //region Pacs
    
    public String getPacsUrlByAccountApiKey(String apiKey) {
        String result = null;

        String sql = "SELECT p.pacsbaseurl as Url FROM pacs p " +
                "LEFT JOIN partnersite ps on p.pacsid = ps.pacsid " +
                "LEFT JOIN defaultaccount da on ps.siteid = da.partnersiteid " +
                "WHERE da.apikey = ?";
        
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, apiKey);
        
        for (Map<String, Object> row : rows) {
            String url = (String) row.get("Url");
            if (url != null && !url.isEmpty()) {
                result = url;
                break;
            }
        }

        return result;
    }

    //endregion

}