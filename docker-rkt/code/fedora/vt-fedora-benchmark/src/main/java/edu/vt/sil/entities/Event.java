package edu.vt.sil.entities;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public class Event {
    public final String fileName;
    public final String type;
    public final double start;
    public final double end;

    public Event(String fileName, String type, double start, double end) {
        this.fileName = fileName;
        this.type = type;
        this.start = start;
        this.end = end;
    }
}
