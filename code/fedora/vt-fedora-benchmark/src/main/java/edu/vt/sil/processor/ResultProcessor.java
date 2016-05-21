package edu.vt.sil.processor;

import edu.vt.sil.entities.ExperimentResult;

import java.util.List;
import java.util.Map;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public interface ResultProcessor {
    String getDescription();

    String getHeaders(Map<String, List<ExperimentResult>> results);

    List<String> process(Map<String, List<ExperimentResult>> results);
}
