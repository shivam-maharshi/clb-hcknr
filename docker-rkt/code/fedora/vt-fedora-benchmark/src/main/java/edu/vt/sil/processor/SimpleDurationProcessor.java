package edu.vt.sil.processor;

import edu.vt.sil.entities.ExperimentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public final class SimpleDurationProcessor implements ResultProcessor {
    @Override
    public String getDescription() {
        return "Experiment Duration (sec)";
    }

    @Override
    public String getHeaders(Map<String, List<ExperimentResult>> results) {
        return "Workers,Min,Average,Max";
    }

    @Override
    public List<String> process(Map<String, List<ExperimentResult>> results) {
        List<String> processed = new ArrayList<>();
        for (String vm : results.keySet()) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double average = 0;
            for (ExperimentResult result : results.get(vm)) {
                min = Math.min(min, result.duration);
                max = Math.max(max, result.duration);
                average += result.duration;
            }
            average /= results.get(vm).size();
            processed.add(String.format("%s,%s,%s,%s", vm, min, average, max));
        }
        return processed;
    }
}
