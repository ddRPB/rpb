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

package de.dktk.dd.rpb.portal.web.controllers.rpb;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.dktk.dd.rpb.core.repository.admin.SampleContentRepository;
import de.dktk.dd.rpb.core.domain.admin.SampleContent;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Controller for preparing RSS news for RSS view
 *
 * @author tomas@skripcak.net
 * @since 25 Sep 2013
 */
@Named
@Controller
public class RssController {

    //region Injects

    @Inject
    private SampleContentRepository sampleContentRepository;

    //endregion

    //region Methods

    /**
     * Load RSS news from the database
     *
     * @return ModelAndView for RSS
     */
    @RequestMapping(value="/rssFeed.faces", method = RequestMethod.GET)
    public ModelAndView getFeedInRss() {
        List<SampleContent> items = this.sampleContentRepository.forceFind();

        ModelAndView mav = new ModelAndView();
        mav.setViewName("rssViewer");
        mav.addObject("feedContent", items);

        return mav;
    }

    //endregion

}
