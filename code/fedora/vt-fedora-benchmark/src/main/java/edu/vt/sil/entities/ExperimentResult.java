package edu.vt.sil.entities;

import java.util.List;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public class ExperimentResult {
    public final String resultName;
    public final String start;
    public final String end;
    public final double duration;
    public final List<Event> events;

    public ExperimentResult(String resultName, String start, String end, double duration, List<Event> events) {
        this.resultName = resultName;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.events = events;
    }
}
