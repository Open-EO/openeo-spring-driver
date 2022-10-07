package org.openeo.spring.api.loaders;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.openeo.spring.model.DimensionSpatial.AxisEnum;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Constants, enums and utilities for
 * management of Coordinate Reference Systems
 * and its sub-components.
 *
 */
public final class CRSUtils {

    // consts
    public static final int EPSG_WGS84 = 4326;
    public static enum CsType {
        PROJCS, GEOGCS;
    }
    // FIXME GDAL library UnsatisfiedLinkError:
//  private /*static*/ /*final*/ SpatialReference WGS84 = new SpatialReference();
//  /*static*/ {
//      WGS84.ImportFromEPSG(EPSG_WGS84);
//  }

    // heuristic to identify axis type
    public static final List<String> TEMPORAL_AXIS_LABELS = Arrays.asList(
            "DATE", "Date", "date",
            "TIME", "Time", "time",
            "ANSI", "Ansi", "ansi",
            "UNIX", "Unix", "unix",
            "YEAR", "Year", "year",
            "t");

    /*
     * EPSG database: what are the axis labels in there?
     */

//  epsg=# \d epsg_coordinateaxis
//              Table "public.epsg_coordinateaxis"
//  Column          |         Type          | Collation | Nullable | Default
//  -------------------------+-----------------------+-----------+----------+---------
//  coord_sys_code          | integer               |           | not null |
//  coord_axis_name_code    | integer               |           | not null |
//  coord_axis_orientation  | character varying(24) |           | not null |
//  coord_axis_abbreviation | character varying(24) |           | not null |
//  uom_code                | integer               |           | not null |
//  coord_axis_order        | smallint              |           | not null |
//  Indexes:
//      "pk_coordinateaxis" PRIMARY KEY, btree (coord_sys_code, coord_axis_name_code)
//
//  #
//  # Which axis orientation types are there?
//  #
//  epsg=# SELECT DISTINCT coord_axis_orientation AS o FROM epsg_coordinateaxis ORDER BY o;
//   Geocentre > equator/90dE      #
//   Geocentre > equator/PM        # Geocentric CRSs
//   Geocentre > north pole        #
//   North along 0 deg East
//   North along 130 deg West
//   North along 140 deg East
//   North along 160 deg East
//   North along 70 deg East
//   North along 90 deg East
//   South along 180 deg East
//   South along 90 deg East
//   down
//   east
//   east south east
//   east-south-east
//   north
//   north north east
//   north-east
//   north-north-east
//   north-west
//   south
//   up
//   west
//
//  #
//  # Which axes have either north or south orientation?
//  #
//    epsg=# SELECT DISTINCT coord_axis_abbreviation AS abbrv
//    FROM epsg_coordinateaxis
//    WHERE (coord_axis_orientation ILIKE 'north%'
//       OR  coord_axis_orientation ILIKE 'south%')
//      AND  coord_axis_orientation NOT ILIKE 'Geocentre%'
//    ORDER BY abbrv;
//   E   # <----- !
//   J
//   Lat
//   N
//   N(Y)
//   P
//   X  # <----- !
//   Y
//   e  # <----- !
//   n
//   x  # <----- !
//
    public static final List<String> Y_AXIS_LABELS = Arrays.asList(
            "J", "Lat", "n", "N", "N(Y)", "P", "Y");

//  #
//  # Which axes have either east or west orientation?
//  #
//  epsg=# SELECT DISTINCT coord_axis_abbreviation AS abbrv
//    FROM epsg_coordinateaxis
//    WHERE (coord_axis_orientation ILIKE 'west%'
//       OR  coord_axis_orientation ILIKE 'east%')
//      AND  coord_axis_orientation NOT ILIKE 'Geocentre%'
//    ORDER BY abbrv;
//   E
//   E(X)
//   I
//   Long
//   M
//   W
//   X
//   Y  # <----- !
//   y  # <----- !
//
    public static final List<String> X_AXIS_LABELS = Arrays.asList(
            "I", "Long", "E", "E(X)", "M", "W", "X");

//  #
//  # Which axes have either vertical (z) orientation?
//  #
//  epsg=# SELECT DISTINCT coord_axis_abbreviation AS abbrv
//    FROM epsg_coordinateaxis
//    WHERE coord_axis_orientation = 'up'
//       OR coord_axis_orientation = 'down'
//       OR coord_axis_orientation = 'Geocentre > north pole'
//    ORDER BY abbrv;
//   D
//   H
//   R
//   Z
//   h
//
    public static final List<String> Z_AXIS_LABELS = Arrays.asList("D", "H", "R", "Z", "h");

    /**
     * Infers the type of a spatial axis from its label (abbreviation).
     *
     * @param axisLabel the abbreviation of an axis
     * @return the inferred axis orientation/type; {@code null} if no suitable
     *         orientation is found.
     * @deprecated use the more precise {@link #getAxisType(CSAxisOrientation)}.
     */
    public static AxisEnum getAxisTypeHeu(String axisLabel) {
        AxisEnum type = null;

        if (X_AXIS_LABELS.contains(axisLabel)) {
            type = AxisEnum.X;

        } else if (Y_AXIS_LABELS.contains(axisLabel)) {
            type = AxisEnum.Y;

        } else if (Z_AXIS_LABELS.contains(axisLabel)) {
            type = AxisEnum.Z;
        }

        return type;
    }

    /**
     * Returns the type of axis given its original orientation.
     *
     * @param axisOrientation
     * @return the inferred axis type, {@code null} if no match found
     * ({@link CSAxisOrientation#OAO_Other}).
     */
    public static AxisEnum getAxisType(CSAxisOrientation axisOrientation) {
        AxisEnum type = null;

        if (CS_H_AXES.contains(axisOrientation)) {
            type = AxisEnum.X;

        } else if (CS_V_AXES.contains(axisOrientation)) {
            type = AxisEnum.Y;

        } else if (CS_Z_AXES.contains(axisOrientation)) {
            type = AxisEnum.Z;
        }

        return type;
    }

    /**
     * Fetches the abbreviation of the {@code i}-th axis of a given
     * coordinate reference system.
     *
     * @param crs  Parsed JSON of a CRS definition
     * @param i    0-based index of the axis in the CRS
     * @return the "abbreviation" attribute of the {@code i}-th axis
     *         in the {@code crs} (base CRS is ignored here);
     *         {@code null} if the element is not found.
     */
    public static final String getAxisAbbrev(JsonNode crs, int i) {
        String abbrev = null;
        try {
            JsonNode csNode = crs.get(JSON_CS);
            JsonNode axNodes = csNode.get(JSON_CS_AXIS);
            JsonNode abbrNode = axNodes.get(i).get(JSON_CS_AXIS_ABBREVIATION);
            abbrev = abbrNode.toString().replace("\"", "");

        } catch (NullPointerException e) {
            // unsupported JSON structure
        }

        return abbrev;
    }

    /** Key of the coordinate system element in a CRS JSON. */
    public static final String JSON_CS = "coordinate_system";

    /** Key of the coordinate system axis element in a CRS JSON. */
    public static final String JSON_CS_AXIS = "axis";

    /** Key of the abbreviation of a coordinate system axis in a CRS JSON. */
    public static final String JSON_CS_AXIS_ABBREVIATION = "abbreviation";

    /**
     * GDAL axis orientations (corresponds to CS_AxisOrientationEnum).
     *
     * Taken from {@code OGRAxisOrientation} enum in {@code ogr_srs_api.h}.
     *
     * @see <a href="https://gdal.org/java/org/gdal/osr/SpatialReference.html#GetAxisOrientation(java.lang.String,int)">Class SpatialReference</a>
     */
    public static enum CSAxisOrientation {
        OAO_Other, /**< Other */
        OAO_North, /**< North */
        OAO_South, /**< South */
        OAO_East,  /**< East */
        OAO_West,  /**< West */
        OAO_Up,    /**< Up (to space) */
        OAO_Down;  /**< Down (to Earth center) */

        static CSAxisOrientation of(int index) {
            return values()[index];
        }
    }

    /** Types of GDAL axes orientations to be considered as horizontal/X spatial axes. */
    public static final EnumSet<CSAxisOrientation> CS_H_AXES = EnumSet.of(
            CSAxisOrientation.OAO_East,
            CSAxisOrientation.OAO_West);

    /** Types of GDAL axes orientations to be considered as horizontal/Y spatial axes. */
    public static final EnumSet<CSAxisOrientation> CS_V_AXES = EnumSet.of(
            CSAxisOrientation.OAO_North,
            CSAxisOrientation.OAO_South);

    /** Types of GDAL axes orientations to be considered as vertical/Z spatial axes. */
    public static final EnumSet<CSAxisOrientation> CS_Z_AXES = EnumSet.of(
            CSAxisOrientation.OAO_Up,
            CSAxisOrientation.OAO_Down);
}
