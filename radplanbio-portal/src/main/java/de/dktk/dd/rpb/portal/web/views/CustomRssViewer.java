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

package de.dktk.dd.rpb.portal.web.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.rometools.rome.feed.rss.*;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import de.dktk.dd.rpb.core.domain.admin.SampleContent;

/**
 * RSS Viewer based on simple spring implementation
 *
 * @author tomas@skripcak.net
 * @since 12 Dec 2013
 */
public class CustomRssViewer extends AbstractRssFeedView {

    //region Overrides

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
        feed.setTitle("RadPlanBio platform - news");
        feed.setDescription("Sumarising changes in open source healthcare RadPlanBio infrastructure");

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        String contextPath = request.getContextPath();

        feed.setLink(scheme + "://" + serverName + contextPath);

        super.buildFeedMetadata(model, feed, request);
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<SampleContent> listContent = (List<SampleContent>) model.get("feedContent");
        List<Item> items = new ArrayList<Item>(listContent.size());

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        for(SampleContent tempContent : listContent ) {
            Item item = new Item();

            item.setAuthor("RPB");

            Description desc = new Description();
            desc.setValue(tempContent.getSummary());
            item.setDescription(desc);

            Content content = new Content();
            content.setValue(tempContent.getSummary());
            item.setContent(content);

            if (tempContent.getUrl() == null) {
                item.setLink(scheme + "://" + serverName + ":" + port + contextPath);
            }
            else {
                item.setLink(tempContent.getUrl());
            }

            item.setPubDate(tempContent.getCreatedDate());
            item.setTitle(tempContent.getTitle());
            items.add(item);
        }

        return items;
    }

    //endregion

}