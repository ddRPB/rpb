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

package de.dktk.dd.rpb.core.domain.cms;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Sample Content domain entity - represent object structure for the news (RSS)
 *
 * @author tomas@skripcak.net
 * @since 12 December 2013
 */
@Entity
@Table(name = "ARTICLE")
public class SampleContent implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SampleContent.class);

    //endregion

    //region Members

    private Integer id; // pk
    private String title; // not null
    private String url;
    private String summary; // not null
    private String content; // not null
    private Date createdDate; // not null
    private String author;

    //endregion

    //region Constructors

    public SampleContent() {
        this.initDefaultValues();
    }

    public SampleContent(Integer primaryKey) {
        this.setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_id_seq")
    @SequenceGenerator(name = "article_id_seq", sequenceName = "article_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region Title

    @Size(max = 255)
    @NotEmpty
    @Column(name = "TITLE", nullable = false)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    //endregion

    //region URL

    @Transient
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    //endregion

    //region Summary

    @Size(max = 512)
    @NotEmpty
    @Column(name = "ABSTRACT", nullable = false, length = 512)
    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String value) {
        this.summary = value;
    }

    //endregion

    //region Content

    @NotEmpty
    @Column(name = "CONTENT", nullable = false)
    public String getContent() {
        return this.content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    //endregion

    //region Created Date

    @Column(name = "PUBDATE", nullable = false)
    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date value) {
        this.createdDate = value;
    }

    @Transient
    public String getCreatedDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getCreatedDate();
        return date != null ? format.format(date) : null;
    }

    //endregion

    //region Author

    @Transient
    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String value) {
        this.author = value;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        this.content = "Placeholder"; // It is not null, but currently not implemented in UI
    }

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other)
    {
        return this == other || (other instanceof SampleContent && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode()
    {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Portal instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("title", this.getTitle())
                .add("summary", this.getSummary())
                .toString();
    }

    //endregion

}