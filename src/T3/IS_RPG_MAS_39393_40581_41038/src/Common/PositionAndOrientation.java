package Common;

import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class PositionAndOrientation {

    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    private int longitude;
    private int latitude;
    private int orientation;

    public PositionAndOrientation(int longitude, int latitude, int orientation) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.orientation = orientation;
    }

    public PositionAndOrientation(PositionAndOrientation pAndO) {
        this.longitude = pAndO.longitude;
        this.latitude = pAndO.latitude;
        this.orientation = pAndO.orientation;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void incrementLongitude() {
        this.longitude++;
    }

    public void decrementLongitude() {
        this.longitude--;
    }

    public void incrementLatitude() {
        this.latitude++;
    }

    public void decrementLatitude() {
        this.latitude--;
    }

    public PositionAndOrientation getNorthernCell() {
        PositionAndOrientation nc = new PositionAndOrientation(longitude, latitude, orientation);
        nc.latitude++;
        return nc;
    }

    public PositionAndOrientation getSouthernCell() {
        PositionAndOrientation sc = new PositionAndOrientation(longitude, latitude, orientation);
        sc.latitude--;
        return sc;
    }

    public PositionAndOrientation getEasternCell() {
        PositionAndOrientation ec = new PositionAndOrientation(longitude, latitude, orientation);
        ec.longitude++;
        return ec;
    }

    public PositionAndOrientation getWesternCell() {
        PositionAndOrientation wc = new PositionAndOrientation(longitude, latitude, orientation);
        wc.longitude--;
        return wc;
    }

    public String orientationAsString() {
        switch (orientation) {
            case NORTH:
                return "North";
            case SOUTH:
                return "South";
            case WEST:
                return "West";
            case EAST:
                return "East";
            default:
                return "My compass is broken this is a problem";
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.longitude;
        hash = 83 * hash + this.latitude;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PositionAndOrientation other = (PositionAndOrientation) obj;
        if (this.longitude != other.longitude) {
            return false;
        }
        if (this.latitude != other.latitude) {
            return false;
        }
        return true;
    }
    private static final String SEPARATOR = "!";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(longitude);
        sb.append(SEPARATOR);
        sb.append(latitude);
        sb.append(SEPARATOR);
        sb.append(orientation);
        sb.append(SEPARATOR);
        return sb.toString();
    }

    public static PositionAndOrientation fromString(String positionAndOrientation) {
        StringTokenizer st = new StringTokenizer(positionAndOrientation, SEPARATOR);
        return new PositionAndOrientation(Integer.parseInt(st.nextToken()),
                Integer.parseInt(st.nextToken()),
                Integer.parseInt(st.nextToken()));
    }
}
