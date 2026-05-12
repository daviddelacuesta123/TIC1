package com.logistica.logistica_urbana.infrastructure.adapter;

import java.util.List;

public class OSRMRouteResponse {

    private String code;
    private List<Route> routes;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public List<Route> getRoutes() { return routes; }
    public void setRoutes(List<Route> routes) { this.routes = routes; }

    public static class Route {
        private Geometry geometry;
        public Geometry getGeometry() { return geometry; }
        public void setGeometry(Geometry geometry) { this.geometry = geometry; }
    }

    public static class Geometry {
        private List<List<Double>> coordinates;
        public List<List<Double>> getCoordinates() { return coordinates; }
        public void setCoordinates(List<List<Double>> coordinates) { this.coordinates = coordinates; }
    }
}
