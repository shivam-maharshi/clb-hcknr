package edu.vt.sil.processor;

import edu.vt.sil.entities.Event;
import edu.vt.sil.entities.ExperimentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public final class EventComparisonProcessor implements ResultProcessor {
    @Override
    public String getDescription() {
        return "Event Types Duration (sec)";
    }

    @Override
    public String getHeaders(Map<String, List<ExperimentResult>> results) {
        String firstLine = "";
        String secondLine = "";
        for (String vm : results.keySet()) {
            firstLine += vm + ",";
            secondLine += "Files,";
            for (String type : results.get(vm).get(0).events
                    .stream().map(e -> e.type).distinct().sorted().collect(Collectors.toList())) {
                firstLine += ",";
                secondLine += type + ",";
            }
        }
        return firstLine + "\n" + secondLine;
    }

    @Override
    public List<String> process(Map<String, List<ExperimentResult>> results) {
        List<String> processed = new ArrayList<>();
        for (String vm : results.keySet()) {
            List<Event> events = new ArrayList<>();
            for (ExperimentResult result : results.get(vm))
                events.addAll(result.events);

            Map<String, List<Event>> collect = events.stream().collect(Collectors.groupingBy(e -> e.fileName));
            int i = 0;
            int eventCount = 0;
            for (String fileName : events.stream().map(e -> e.fileName).distinct().sorted().collect(Collectors.toList())) {
                List<String> value = collect.get(fileName).stream().sorted((e1, e2) -> e1.type.compareTo(e2.type)).map(e -> Double.toString(e.end - e.start)).collect(Collectors.toList());
                eventCount = value.size();
                if (i == processed.size())
                    processed.add("");
                processed.set(i, processed.get(i) + fileName + "," + value.stream().collect(Collectors.joining(",")) + ",");
                i++;
            }
            while (i < processed.size()) {
                processed.set(i, processed.get(i) + "," + IntStream.range(0, eventCount).mapToObj(val -> ",").collect(Collectors.joining()));
                i++;
            }
        }
        return processed;
    }
}
