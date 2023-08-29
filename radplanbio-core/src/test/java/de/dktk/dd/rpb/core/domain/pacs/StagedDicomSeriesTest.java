package de.dktk.dd.rpb.core.domain.pacs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StagedDicomSeries.class, DicomImage.class, LoggerFactory.class, Logger.class})
public class StagedDicomSeriesTest {
    List<DicomImage> dicomImageList;
    DicomImage image01;
    DicomImage image02;
    String uid01;
    String uid02;

    @Before
    public void setUp() throws Exception {
        // prepare logger
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        // prepare images
        uid01 = "2.25.1111";
        uid02 = "2.25.2222";

        image01 = mock(DicomImage.class);
        when(image01.getReferencedDicomSeriesUID()).thenReturn(uid01);
        image02 = mock(DicomImage.class);
        when(image02.getReferencedDicomSeriesUID()).thenReturn(uid02);
        dicomImageList = new ArrayList<>();
        dicomImageList.add(image01);
        dicomImageList.add(image02);

    }

    // region setClinicalDicomImages/setStageOneDicomImages + createDicomImagesByReferenceMap()

    @Test
    public void setClinicalDicomImages_creates_a_DicomImagesByReference_map_with_two_items() {
        StagedDicomSeries series = new StagedDicomSeries();
        // UID is used to detect the clinical series context
        series.setClinicalSeriesUid("dummy");

        series.setClinicalDicomImages(dicomImageList);
        assertEquals(2, series.getReferencedUidSet().size());
        assertEquals(image01, series.getImagesByReference(uid01).get(0));
        assertEquals(image02, series.getImagesByReference(uid02).get(0));
        assertEquals(null, series.getImagesByReference("not existing id"));
    }
    @Test
    public void setStageOneDicomImages_creates_a_DicomImagesByReference_map_with_two_items() {
        StagedDicomSeries series = new StagedDicomSeries();

        series.setStageOneDicomImages(dicomImageList);
        assertEquals(2, series.getReferencedUidSet().size());
        assertEquals(image01, series.getImagesByReference(uid01).get(0));
        assertEquals(image02, series.getImagesByReference(uid02).get(0));
        assertEquals(null, series.getImagesByReference("not existing id"));
    }

    // endregion
}