package org.labkey.study.xml;

import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StudyTest {

    @Test
    public void reads_the_suject_column_name() throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(Study.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        File file = new File("src/test/data/labkey/study.xml");
        InputStream studyFileInputStream = new FileInputStream(file);
        Study study = (Study) unmarshaller.unmarshal(studyFileInputStream);

        assertNotNull(study);
        assertEquals("ParticipantId",study.getSubjectColumnName());

    }
}