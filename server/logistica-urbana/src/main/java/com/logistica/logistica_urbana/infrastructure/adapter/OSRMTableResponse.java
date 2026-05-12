package com.logistica.logistica_urbana.infrastructure.adapter;

import java.util.List;

public class OSRMTableResponse {

    private String code;
    private double[][] distances;
    private List<Object> sources;
    private List<Object> destinations;

    public OSRMTableResponse() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double[][] getDistances() { return distances; }
    public void setDistances(double[][] distances) { this.distances = distances; }

    public List<Object> getSources() { return sources; }
    public void setSources(List<Object> sources) { this.sources = sources; }

    public List<Object> getDestinations() { return destinations; }
    public void setDestinations(List<Object> destinations) { this.destinations = destinations; }
}
