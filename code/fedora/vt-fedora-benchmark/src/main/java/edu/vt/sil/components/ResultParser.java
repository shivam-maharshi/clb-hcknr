package edu.vt.sil.components;

import edu.vt.sil.administrator.AdministratorCommand;
import edu.vt.sil.entities.Event;
import edu.vt.sil.entities.ExperimentResult;
import edu.vt.sil.processor.ResultProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author: dedocibula
 * Created on: 9.2.2016.
 */
public final class ResultParser extends AbstractComponent {
    private ResultProcessor[] processors;

    private Path resultsDir;
    private String resultDescriptor;

    public ResultParser(ResultProcessor... processors) {
        if (processors == null || processors.length == 0)
            throw new IllegalArgumentException("No processors specified");

        this.processors = processors;
    }

    @Override
    protected void prepare(AdministratorCommand command, String[] arguments) throws Exception {
        if (arguments.length != 2)
            throw new IllegalArgumentException(String.format("Invalid number of parameters. Expected: 2 - Received: %s",
                    arguments.length));

        resultsDir = Paths.get(arguments[0]);
        if (Files.notExists(resultsDir) || !Files.isDirectory(resultsDir))
            throw new IllegalArgumentException(String.format("No directory: %s", resultsDir));

        resultDescriptor = arguments[1];
        if (resultDescriptor == null || resultDescriptor.isEmpty())
            throw new IllegalArgumentException("Cannot use null/empty descriptor");
    }

    @Override
    protected void execute() throws Exception {
        Map<String, List<ExperimentResult>> results = extractExperimentResults(resultsDir, resultDescriptor);

        Path output = resultsDir.resolve(String.format("%s_processed.csv", resultDescriptor));

        for (ResultProcessor processor : processors) {
            String description = processor.getDescription();
            String headers = processor.getHeaders(results);
            List<String> content = processor.process(results);
            if (description != null)
                content.add(0, description);
            if (headers != null)
                content.add(1, headers);
            content.add("");
            Files.write(output, content, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }
    }

    @Override
    public String showLabel(AdministratorCommand command) {
        return "<results directory> <results descriptor>";
    }

    private static Map<String, List<ExperimentResult>> extractExperimentResults(Path resultsDir, String descriptor) throws IOException {
        Map<String, List<ExperimentResult>> results = new TreeMap<>();
        Files.list(resultsDir).filter(dir -> Files.isDirectory(dir)).forEach(dir -> {
            String dirName = dir.getFileName().toString();
            results.put(dirName, new ArrayList<>());
            try {
                Files.list(dir).filter(f -> f.getFileName().toString().contains(descriptor)).forEach(f -> {
                    try {
                        results.get(dirName).add(processContent(f.getFileName().toString(), Files.readAllLines(f)));
                    } catch (IOException ignored) {
                    }
                });
            } catch (IOException ignored) {
            }
        });
        return results;
    }

    private static ExperimentResult processContent(String resultName, List<String> content) {
        String[] parts = content.get(0).split(",");
        ExperimentResult result = new ExperimentResult(resultName, parts[1], parts[3], Double.parseDouble(parts[2]), new ArrayList<>());
        content.stream().skip(1).forEach(l -> {
            try {
                String[] smallParts = l.split(",");
                Event event = new Event(smallParts[1], smallParts[0], Double.parseDouble(smallParts[2]), Double.parseDouble(smallParts[3]));
                result.events.add(event);
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while reading file [ " + resultName + " ] line [ " + l + " ]", e);
            }
        });
        return result;
    }
}
