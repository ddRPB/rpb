/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ItemGroupDataBuilder.class, Logger.class})
public class StudyEventDataBuilderTest {
    private StudyEventDataBuilder studyEventDataBuilder;
    private String dummyStudyEventOid = "DummyStudyEventOid";
    private String dummyStudyEventRepeatKey = "DummyStudyEventRepeatKey";

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
        this.studyEventDataBuilder = StudyEventDataBuilder.getInstance();
        this.studyEventDataBuilder.setStudyEventOid(this.dummyStudyEventOid);
    }

    @Test
    public void get_instance() {
        assertNotNull(this.studyEventDataBuilder);
    }

    @Test(expected = MissingPropertyException.class)
    public void build_throws_if_StudyEventOid_is_missing() throws MissingPropertyException {
        this.studyEventDataBuilder.setStudyEventOid(null);
        this.studyEventDataBuilder.build();
    }

    @Test
    public void build_returns_a_valid_StudyEventDataBuilder_instance() throws MissingPropertyException {
        EventData studyEventData = this.studyEventDataBuilder.build();
        assertTrue(studyEventData != null);
    }

    @Test
    public void setStudyEventOid_sets_id() throws MissingPropertyException {
        EventData studyEventData = this.studyEventDataBuilder.build();
        assertEquals(this.dummyStudyEventOid, studyEventData.getStudyEventOid());
    }

    @Test
    public void setDummyStudyEventRepeatKey_sets_id() throws MissingPropertyException {
        EventData studyEventData = this.studyEventDataBuilder.
                setStudyEventRepeatKey(this.dummyStudyEventRepeatKey).
                build();
        assertEquals(this.dummyStudyEventRepeatKey, studyEventData.getStudyEventRepeatKey());
    }

    @Test
    public void addFormData_adds_an_FormDataObject() throws MissingPropertyException {
        FormData formData = new FormData();
        formData.setFormOid("dummyId");
        EventData studyEventData = this.studyEventDataBuilder.
                addFormData(formData).
                build();
        assertEquals(formData, studyEventData.getFormDataList().get(0));
    }
}