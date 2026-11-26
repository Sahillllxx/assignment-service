package com.indigo.compute.index.model;

public enum AssignmentCriteriaType {
    STATION, TAIL, AIRCRAFT_TYPE, ZONE;

    public boolean matches(String value) {
        return name().equalsIgnoreCase(value);
    }
}