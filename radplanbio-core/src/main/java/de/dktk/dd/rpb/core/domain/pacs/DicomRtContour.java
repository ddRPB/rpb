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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//import org.opencv.core.Point;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Created by root on 9/8/16.
 */
public class DicomRtContour {

    private static final Logger LOGGER = LoggerFactory.getLogger(DicomRtContour.class);

    private String geometricType;
    private int contourPoints;
    private final DicomRtLayer layer;
    private double[] points;
    private double[] coordinatesX;
    private double[] coordinatesY;
    private double area;

    private Double setContourSlabThickness;

    private double[] contourOffsetVector;

    public DicomRtContour(DicomRtLayer layer) {
        this.layer = Objects.requireNonNull(layer);
        this.area = -1.0;
    }

    public DicomRtLayer getLayer() {
        return this.layer;
    }

    public double[] getPoints() {
        return this.points;
    }

    public void setPoints(double[] points) {
        this.points = points;
    }

//    public List<Point> getListOfPoints() {
//        List<Point> listOfPoints = new ArrayList<>();
//        if (points != null && points.length % 3 == 0) {
//
//            for (int i = 0; i < points.length; i = i + 3) {
//                Point p = new Point(points[i], points[i+1]);
//                listOfPoints.add(p);
//            }
//        }
//
//        return listOfPoints;
//    }

    public double getCoordinateX() {
        if (points != null && points.length >= 3) {
            return points[0];
        }
        return 0;
    }

    public double getCoordinateY() {
        if (points != null && points.length >= 3) {
            return points[1];
        }
        return 0;
    }

    public double getCoordinateZ() {
        if (points != null && points.length >= 3) {
            return points[2];
        }
        return 0;
    }

    public String getGeometricType() {
        return this.geometricType;
    }

    public void setGeometricType(String value) {
        this.geometricType = value;
    }

    public int getContourPoints() {
        return this.contourPoints;
    }

    public void setContourPoints(int value) {
        this.contourPoints = value;
    }

    public double getArea() {
        if (this.area < 0) {
            area = polygonArea(this.getCoordinatesX(), this.getCoordinatesY());
        }

        return area;
    }

    public Double getSetContourSlabThickness() {
        return setContourSlabThickness;
    }

    public void setSetContourSlabThickness(Double setContourSlabThickness) {
        this.setContourSlabThickness = setContourSlabThickness;
    }

    public double[] getContourOffsetVector() {
        return contourOffsetVector;
    }

    public void setContourSlabThickness(Double contourSlabThickness) {
        this.setContourSlabThickness = contourSlabThickness;
    }

    public void setContourOffsetVector(double[] contourOffsetVector) {
        this.contourOffsetVector = contourOffsetVector;
    }

    public double[] getCoordinatesX() {
        if (this.coordinatesX == null) {
            if (points != null && points.length % 3 == 0) {
                this.coordinatesX = new double[points.length / 3];

                int j = 0;
                for (int i = 0; i < points.length; i = i + 3) {
                    this.coordinatesX[j] = points[i];
                    j++;
                }
            }
        }

        return this.coordinatesX;
    }

    public double[] getCoordinatesY() {
        if (this.coordinatesY == null) {
            if (points != null && points.length % 3 == 0) {
                this.coordinatesY = new double[points.length / 3];

                int j = 0;
                for (int i = 1; i < points.length; i = i + 3) {
                    this.coordinatesY[j] = points[i];
                    j++;
                }
            }
        }

        return this.coordinatesY;
    }

    public boolean containsContour(DicomRtContour contour) {
        // Assume if one point is inside, all will be inside
        contour.getCoordinateX();
        contour.getCoordinatesY();

        //TODO: Contour bounding
        double minX = 0;// Arrays.stream(this.getCoordinatesX()).min().getAsDouble();
        double maxX = 0;//Arrays.stream(this.getCoordinatesX()).max().getAsDouble();
        double minY = 0;//Arrays.stream(this.getCoordinatesY()).min().getAsDouble();
        double maxY = 0;//Arrays.stream(this.getCoordinatesY()).max().getAsDouble();

        // Outside of the contour bounding box
        if (contour.getCoordinateX() < minX || contour.getCoordinateX() > maxX || contour.getCoordinateY() < minY || contour.getCoordinateY() > maxY) {
            return false;
        }

        int j = this.getContourPoints() - 1;
        boolean isInside = false;

        for (int i = 0; i < this.getContourPoints(); i++) {
            if (this.getCoordinatesY()[i] < contour.getCoordinateY() &&
                    this.getCoordinatesY()[j] >= contour.getCoordinateY() ||
                    this.getCoordinatesY()[j] < contour.getCoordinateY() &&
                            this.getCoordinatesY()[i] >= contour.getCoordinateY()) {
                if (this.getCoordinatesX()[i] + (contour.getCoordinateY() - this.getCoordinatesY()[i]) / (this.getCoordinatesY()[j] - this.getCoordinatesY()[i]) * (this.getCoordinatesX()[j] - this.getCoordinatesX()[i]) < contour.getCoordinateX()) {
                    isInside = !isInside;
                }
            }

            j = i;
        }

        return isInside;
    }

    private double polygonArea(double[] x, double[] y) {
        // Initialise the area
        double area = 0.0;

        // Calculate value of shoelace formula
        for (int i = 0; i < x.length; i++) {
            int j = (i + 1) % x.length;
            area += (x[i] * y[j]) - (x[j] * y[i]);
        }

        return Math.abs(area / 2.0);
    }

}
