package edu.vt.sil.processor;

import edu.vt.sil.entities.Event;
import edu.vt.sil.entities.ExperimentResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public final class OverlapProcessor implements ResultProcessor {
    @Override
    public String getDescription() {
        return "Event types overlap (sec)";
    }

    @Override
    public String getHeaders(Map<String, List<ExperimentResult>> results) {
        return "Workers";
    }

    @Override
    public List<String> process(Map<String, List<ExperimentResult>> results) {
        List<String> processed = new ArrayList<>();
        for (String vm : results.keySet()) {
            List<String> events = results.get(vm).get(0).events.stream().map(e -> e.type).distinct().sorted().collect(Collectors.toList());
            for (String event : events) {
                List<List<Event>> eventEvents = results.get(vm).stream()
                        .map(r -> r.events.stream()
                                .filter(e -> e.type.equals(event))
                                .sorted((e1, e2) -> Double.compare(e1.start, e2.start))
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());
                processed.add(event);
                processed.add("," + eventEvents.get(0).stream().map(e -> e.fileName).collect(Collectors.joining(",,")));

                double min = eventEvents.stream().map(e -> e.get(0).start).min(Comparator.naturalOrder()).get();
                for (int i = 0; i < eventEvents.size(); i++) {
                    processed.add(i + ",");
                    double prev = min;
                    for (Event e : eventEvents.get(i)) {
                        processed.set(processed.size() - 1, processed.get(processed.size() - 1) +
                                (e.start - prev) + "," + (e.end - e.start) + ",");
                        prev = e.end;
                    }
                }
            }
        }
        return processed;
    }
}
