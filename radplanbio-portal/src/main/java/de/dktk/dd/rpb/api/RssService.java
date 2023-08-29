/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2021 RPB Team
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

package de.dktk.dd.rpb.api;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import de.dktk.dd.rpb.core.domain.cms.SampleContent;
import de.dktk.dd.rpb.core.repository.cms.SampleContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling RSS requests to access RPB portal news
 */
@Component
@Path("/rss")
public class RssService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(RssService.class);

    //endregion

    //region Injects

    @Inject
    private SampleContentRepository sampleContentRepository;

    //endregion

    //region Resource Methods

    //region GET

    @GET
    @Produces("application/rss+xml;charset=UTF-8")
    public Response getRssFeed(@Context final UriInfo uriInfo) {
        try {
            final SyndFeed feed = this.generate(uriInfo.getRequestUri().toString());

            // Write the SyndFeed to a writer
            final SyndFeedOutput output = new SyndFeedOutput();
            final Writer writer = new StringWriter();
            output.output(feed, writer);

            // Return a JAX-RS Response with the writer as the body
            return Response.ok(writer.toString()).build();
        }
        catch(Exception err) {
            log.error(err.getMessage(),err);
            return Response.status(500).build();
        }
    }

    //endregion

    //endregion

    //region Private Methods

    private SyndFeed generate(String channelUrl) {

        final SyndFeed feed = new SyndFeedImpl();

        feed.setFeedType("rss_2.0");
        feed.setTitle("RadPlanBio platform - news");
        feed.setDescription("Summarising changes in open source radiotherapy clinical research IT infrastructure");
        feed.setLink(channelUrl);

        final List<SyndEntry> entries = new ArrayList<>();
        for (SampleContent sampleContent : this.sampleContentRepository.find()) {

            final SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(sampleContent.getTitle());
            entry.setPublishedDate(sampleContent.getCreatedDate());

            final SyndContent description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(sampleContent.getSummary());

            entry.setDescription(description);

            entries.add(entry);
        }
        
        feed.setEntries(entries);

        return feed;
    }

    //endregion
    
}
