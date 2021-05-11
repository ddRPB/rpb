/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.pacs;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


/**
 * DICOM-RT Structure
 *
 * @author tomas@skripcak.net
 * @since 12 Jan 2015
 */
public class DicomRtStructure {

    //region Members

    private int roiNumber;
    private String roiName;
    private int observationNumber;
    private String rtRoiInterpretedType;
    private String roiObservationLabel;
    private double thickness;
    private double volume; // unit cm^3
    private EnumDataSource volumeSource;

    private Color color;
    private Map<Double, ArrayList<DicomRtContour>> planes;

    //endregion

    //region Constructors

    public DicomRtStructure() {
        this.volume = -1.0;
    }

    //endregion

    //region Properties

    //region RoiNumber

    public int getRoiNumber() {
        return this.roiNumber;
    }

    public void setRoiNumber(int number) {
        this.roiNumber = number;
    }

    //endregion

    //region RoiName

    public String getRoiName() {
        return this.roiName;
    }

    public void setRoiName(String roiName) {
        this.roiName = roiName;
    }

    //endregion

    //region ObservationNumber

    public int getObservationNumber() {
        return observationNumber;
    }

    public void setObservationNumber(int observationNumber) {
        this.observationNumber = observationNumber;
    }

    //endregion

    //region RtRoiInterpretedType

    public String getRtRoiInterpretedType() {
        return this.rtRoiInterpretedType;
    }

    public void setRtRoiInterpretedType(String roiInterpretedType) {
        this.rtRoiInterpretedType = roiInterpretedType;
    }

    //endregion

    //region RoiObservationLabel

    public String getRoiObservationLabel() {
        return roiObservationLabel;
    }

    public void setRoiObservationLabel(String roiObservationLabel) {
        this.roiObservationLabel = roiObservationLabel;
    }

    //endregion

    //region Thickness

    public double getThickness() {
        return this.thickness;
    }

    public void setThickness(double value) {
        this.thickness = value;
    }

    //endregion

    //region Volume

    public double getVolume() {
        // If volume was not initialised from DVH (e.g. DVH does not exist) recalculate it
        if (this.volume < 0) {
            this.volume = this.calculateVolume();
            this.volumeSource = EnumDataSource.CALCULATED;
        }

        return this.volume;
    }

    public void setVolume(double value) {
        this.volume = value;
        this.volumeSource = EnumDataSource.PROVIDED;
    }

    //endregion

    //region VolumeSource

    public EnumDataSource getVolumeSource() {
        return this.volumeSource;
    }

    //endregion

    //region Color

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    //endregion

    //region Planes

    public Map<Double, ArrayList<DicomRtContour>> getPlanes() {
        return this.planes;
    }

    public void setPlanes(Map<Double, ArrayList<DicomRtContour>> contours) {
        this.planes = contours;
    }

    //endregion

    //endregion

    //region Methods

//    public Pair<Integer, Double> calculateLargestContour(ArrayList<DicomRtContour> planeContours) {
//        double maxContourArea = 0.0;
//        int maxContourIndex = 0;
//
//        // Calculate the area for each contour of this structure in provided plane
//        for (int i = 0; i < planeContours.size(); i++) {
//            DicomRtContour polygon = planeContours.get(i);
//
//            // Find the largest polygon of contour
//            if (polygon.getArea() > maxContourArea) {
//                maxContourArea = polygon.getArea();
//                maxContourIndex = i;
//            }
//        }
//
//        return new Pair(maxContourIndex, maxContourArea);
//    }

    private double calculateVolume() {
        double structureVolume = 0.0;

        // Iterate over structure planes (z)
        int n = 0;
        for (ArrayList<DicomRtContour> structurePlaneContours : this.planes.values()) {

            // TODO
            // Calculate the area for each contour in the current plane
            // Pair maxContour = this.calculateLargestContour(structurePlaneContours);
            int maxContourIndex = 0; // (Integer)maxContour.getFirst();
            double maxContourArea = 0; // (Double)maxContour.getSecond();

            for (int i = 0; i < structurePlaneContours.size(); i++) {
                DicomRtContour polygon = structurePlaneContours.get(i);

                // Find the largest polygon of contour
                if (polygon.getArea() > maxContourArea) {
                    maxContourArea = polygon.getArea();
                    maxContourIndex = i;
                }
            }

            // Sum the area of contours in the current plane
            DicomRtContour largestPolygon = structurePlaneContours.get(maxContourIndex);
            double area = largestPolygon.getArea();
            for (int i = 0; i < structurePlaneContours.size(); i++) {
                DicomRtContour polygon = structurePlaneContours.get(i);
                if (i != maxContourIndex) {
                    // If the contour is inside = ring -> subtract it from the total area
                    if (largestPolygon.containsContour(polygon)) {
                        area -= polygon.getArea();
                    }
                    // Otherwise it is outside, so add it to the total area
                    else {
                        area += polygon.getArea();
                    }
                }
            }

            // For first and last plane calculate with half of thickness
            if ((n == 0) || (n == this.planes.size() -1)) {
                structureVolume += area * this.thickness * 0.5;
            }
            // For rest use full slice thickness
            else {
                structureVolume += area * this.thickness;
            }

            n++;
        }

        // DICOM uses millimeters -> convert from mm^3 to cm^3
        return structureVolume / 1000;
    }

    //endregion

    //region Overrides

    @Override
    public String toString() {
        return getRoiName();
    }

    //endregion

}