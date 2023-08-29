package de.dktk.dd.rpb.core.facade;

import de.dktk.dd.rpb.core.DicomImageFactory;
import de.dktk.dd.rpb.core.domain.pacs.DicomImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomImageRtStruct;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomStudy;
import de.dktk.dd.rpb.core.domain.pacs.StagedSubject;
import de.dktk.dd.rpb.core.service.IConquestService;
import de.dktk.dd.rpb.core.util.JsonStringUtil;
import de.dktk.dd.rpb.core.util.PatientIdentifierUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;

public class ParallelConquestRequestsFacade {
    private static final Logger log = LoggerFactory.getLogger(ParallelConquestRequestsFacade.class);

    private final IConquestService clinicalSystemConquestService;
    private final IConquestService researchSystemConquestService;
    private final String clinicalStudyId;
    private final String stageOneStudyId;
    private final String stageTwoStudyId;
    private final StagedSubject subject;


    public ParallelConquestRequestsFacade(
            IConquestService clinicalSystemConquestService,
            IConquestService researchSystemConquestService,
            StagedDicomStudy selectedStudy,
            StagedSubject subject
    ) {

        this.clinicalSystemConquestService = clinicalSystemConquestService;
        this.researchSystemConquestService = researchSystemConquestService;
        this.clinicalStudyId = selectedStudy.getClinicalStudyInstanceUid();
        this.stageOneStudyId = selectedStudy.getStageOneStudyInstanceUid();
        this.stageTwoStudyId = selectedStudy.getStageTwoStudyInstanceUid();
        this.subject = subject;
    }

    public void addDicomImagesToDicomSeriesList(List<StagedDicomSeries> seriesListWithoutImages) throws ExecutionException, InterruptedException, JSONException {
        ExecutorService service = Executors.newFixedThreadPool(8);
        Map<String, Future<String>> clinicalSeriesResults = new HashMap<>();
        Map<String, Future<String>> stageOneSeriesResults = new HashMap<>();
        Map<String, Future<String>> stageTwoSeriesResults = new HashMap<>();

        for (StagedDicomSeries series : seriesListWithoutImages) {
            if (!series.getClinicalSeriesUid().isEmpty()) {
                String clinicalSeriesUid = series.getClinicalSeriesUid();
                String studySubjectId = subject.getStudySubjectId();
                String hospitalPatientId = PatientIdentifierUtil.removePatientIdPrefix(studySubjectId);
                Future<String> future = clinicalSystemConquestService.getDicomImagesOfSeriesAsFuture(
                        hospitalPatientId,
                        clinicalStudyId,
                        clinicalSeriesUid,
                        service
                );
                clinicalSeriesResults.put(clinicalSeriesUid, future);
            }

            if (!series.getStageOneSeriesUid().isEmpty()) {
                String stageOneSeriesUid = series.getStageOneSeriesUid();
                Future<String> future = researchSystemConquestService.getDicomImagesOfSeriesAsFuture(
                        subject.getUniqueIdentifier(),
                        stageOneStudyId,
                        stageOneSeriesUid,
                        service
                );
                stageOneSeriesResults.put(stageOneSeriesUid, future);
            }

            if (!series.getStageTwoSeriesUid().isEmpty()) {
                String stageTwoSeriesUid = series.getStageTwoSeriesUid();
                Future<String> future = researchSystemConquestService.getDicomImagesOfSeriesAsFuture(
                        subject.getUniqueIdentifier(),
                        stageTwoStudyId,
                        stageTwoSeriesUid,
                        service
                );
                stageTwoSeriesResults.put(stageTwoSeriesUid, future);
            }
        }

        for (StagedDicomSeries stagedSeries : seriesListWithoutImages) {
            if (!stagedSeries.getClinicalSeriesUid().isEmpty()) {
                String clinicalSeriesUid = stagedSeries.getClinicalSeriesUid();
                Future clinicalPacsResult = clinicalSeriesResults.get(clinicalSeriesUid);
                if(clinicalPacsResult != null){
                    String clinicalPacsResultString = (String) clinicalPacsResult.get();
                    clinicalPacsResultString  = JsonStringUtil.trimJsonString(clinicalPacsResultString);

                    List<DicomImage> dicomImageList = new ArrayList<>();
                    List<String> roiNameList = new ArrayList<>();
                    parseResultString(clinicalPacsResultString, dicomImageList, roiNameList);

                    stagedSeries.setClinicalDicomImages(dicomImageList);
                    stagedSeries.setRegionOfInterestList(roiNameList);
                }

            }

            if (!stagedSeries.getStageOneSeriesUid().isEmpty()) {
                String stageOneSeriesUid = stagedSeries.getStageOneSeriesUid();
                Future stageOnePacsResult = stageOneSeriesResults.get(stageOneSeriesUid);
                if(stageOnePacsResult != null){
                    String stageOnePacsResultString = (String) stageOnePacsResult.get();
                    stageOnePacsResultString  = JsonStringUtil.trimJsonString(stageOnePacsResultString);

                    List<DicomImage> dicomImageList = new ArrayList<>();
                    List<String> roiNameList = new ArrayList<>();
                    parseResultString(stageOnePacsResultString, dicomImageList, roiNameList);

                    stagedSeries.setStageOneDicomImages(dicomImageList);
                    if (stagedSeries.getRegionsOfInterestList().size() == 0) {
                        stagedSeries.setRegionOfInterestList(roiNameList);
                    }
                }
            }

            if (!stagedSeries.getStageTwoSeriesUid().isEmpty()) {
                String stageTwoSeriesUid = stagedSeries.getStageTwoSeriesUid();
                Future stageTwoPacsResult = stageTwoSeriesResults.get(stageTwoSeriesUid);
                if(stageTwoPacsResult != null){
                    String stageTwoPacsResultString = (String) stageTwoPacsResult.get();
                    stageTwoPacsResultString  = JsonStringUtil.trimJsonString(stageTwoPacsResultString);

                    List<DicomImage> dicomImageList = new ArrayList<>();
                    List<String> roiNameList = new ArrayList<>();
                    parseResultString(stageTwoPacsResultString, dicomImageList, roiNameList);

                    stagedSeries.setStageTwoDicomImages(dicomImageList);
                }
            }


        }

    }

    private void parseResultString(String queryResultString, List<DicomImage> dicomImageList, List<String> roiNameList) throws JSONException {
        JSONObject json = new JSONObject(queryResultString);

        JSONArray jsonSeries = json.getJSONArray("Series");

        for (int i = 0; i < jsonSeries.length(); i++) {

            JSONObject jsonStudy = jsonSeries.getJSONObject(i);
            String seriesModality = jsonStudy.optString("Modality");

            JSONArray jsonImages = jsonStudy.getJSONArray("Images");

            try {
                for (int j = 0; j < jsonImages.length(); j++) {
                    JSONObject jsonImage = jsonImages.getJSONObject(j);
                    DicomImage image = DicomImageFactory.getDicomImage(seriesModality, jsonImage);

                    if (seriesModality.equals(DICOM_RTSTRUCT)) {
                        DicomImageRtStruct dicomImageRtStruct = (DicomImageRtStruct) image;
                        roiNameList.addAll(dicomImageRtStruct.getRoiNameList());
                    }

                    dicomImageList.add(image);
                }
            } catch (Exception err) {
                String message = "There was a problem unmarshalling DICOM series from JSON!";
                log.error(message, err);
            }

        }
    }

}
