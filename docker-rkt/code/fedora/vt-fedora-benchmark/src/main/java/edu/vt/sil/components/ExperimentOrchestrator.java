package edu.vt.sil.components;

import edu.vt.sil.administrator.AdministratorCommand;
import edu.vt.sil.administrator.StorageType;
import edu.vt.sil.messaging.RabbitMQCommand;
import edu.vt.sil.messaging.RabbitMQProducer;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: dedocibula
 * Created on: 1.3.2016.
 */
public final class ExperimentOrchestrator extends AbstractComponent {
    private static final String STOP_WORK_ITEM = "";

    private RabbitMQProducer producer;
    private Set<String> activeWorkers;
    private Set<RabbitMQCommand> executedCommands;

    private AdministratorCommand command;
    private List<Integer> workerCounts;
    private URL fedoraUrl;
    private StorageType storageType;
    private String storageFolder;
    private List<String> hdf5WorkItems;

    public ExperimentOrchestrator(RabbitMQProducer producer) {
        Objects.requireNonNull(producer);

        this.producer = producer;
        this.activeWorkers = new HashSet<>();
        this.executedCommands = new HashSet<>();
        this.workerCounts = new ArrayList<>();
    }

    @Override
    protected void prepare(AdministratorCommand command, String[] arguments) throws Exception {
        this.command = command;

        switch (command) {
            case START_WORKERS:
                if (!activeWorkers.isEmpty())
                    throw new IllegalArgumentException(String.format("Cannot start new workers with %s active workers. " +
                            "Stop workers first.", activeWorkers.size()));
                if (arguments.length != 1)
                    throw new IllegalArgumentException(String.format("Invalid number of parameters. " +
                            "Expected: 1 - Received: %s", arguments.length));

                for (String countString : arguments[0].split(",")) {
                    int workerCount = Integer.parseInt(countString);
                    if (workerCount < 1)
                        throw new IllegalArgumentException("Worker count cannot be less than 1");
                    workerCounts.add(workerCount);
                }
                break;
            case RUN_EXPERIMENT1:
                if (activeWorkers.isEmpty())
                    throw new IllegalArgumentException("There are no active workers. Start workers first");
                if (arguments.length != 4)
                    throw new IllegalArgumentException(String.format("Invalid number of parameters. " +
                            "Expected: 4 - Received: %s", arguments.length));

                fedoraUrl = new URL(arguments[0]);
                // validation
                fedoraUrl.toURI();

                storageType = StorageType.valueOf(arguments[1].toUpperCase());

                storageFolder = arguments[2];
                if (storageFolder == null || storageFolder.isEmpty())
                    throw new IllegalArgumentException("Cannot use null/empty storage folder");

                hdf5WorkItems = prepareHDF5WorkItems(arguments[3], null);
                break;
            case RUN_EXPERIMENT2:
            case RUN_EXPERIMENT3:
                if (activeWorkers.isEmpty())
                    throw new IllegalArgumentException("There are no active workers. Start workers first");
                if (arguments.length != 2)
                    throw new IllegalArgumentException(String.format("Invalid number of parameters. " +
                            "Expected: 2 - Received: %s", arguments.length));
                if (!executedCommands.contains(RabbitMQCommand.EXPERIMENT1))
                    throw new IllegalArgumentException(String.format("%s depends on experiment1. Run it first", command));

                URL fedoraUrl = new URL(arguments[0]);
                // validation
                fedoraUrl.toURI();

                hdf5WorkItems = prepareHDF5WorkItems(arguments[1], fedoraUrl);
                break;
            case STOP_WORKERS:
                break;
            default:
                throw new IllegalArgumentException(String.format("Received illegal command: %s", command));
        }
    }

    @Override
    protected void execute() throws Exception {
        switch (command) {
            case START_WORKERS:
                for (Integer count : workerCounts)
                    activeWorkers.addAll(producer.addWorkers(count));
                break;
            case RUN_EXPERIMENT1:
                Map<String, Object> headers = new HashMap<>();
                headers.put("fedoraUrl", fedoraUrl.toString());
                headers.put("storageType", storageType.toString());
                headers.put("storageFolder", storageFolder);
                executeExperiment(RabbitMQCommand.EXPERIMENT1, headers);
                break;
            case RUN_EXPERIMENT2:
                executeExperiment(RabbitMQCommand.EXPERIMENT2, null);
                break;
            case RUN_EXPERIMENT3:
                executeExperiment(RabbitMQCommand.EXPERIMENT3, null);
                break;
            case STOP_WORKERS:
                activeWorkers.removeAll(producer.sendControlMessage(RabbitMQCommand.SHUTDOWN, activeWorkers.size()));
                executedCommands.clear();
                workerCounts.clear();
        }
    }

    @Override
    public String showLabel(AdministratorCommand command) {
        switch (command) {
            case START_WORKERS:
                return "<comma-separated worker count>";
            case RUN_EXPERIMENT1:
                return "<fedora url> <external storage type (Google_Drive, S3)> <external storage folder> <input file (HDF5 file names)>";
            case RUN_EXPERIMENT2:
            case RUN_EXPERIMENT3:
                return "<fedora url> <input file (HDF5 file names)>";
            case STOP_WORKERS:
                return "";
            default:
                throw new IllegalArgumentException(String.format("Unrecognized command: %s", command));
        }
    }

    private List<String> prepareHDF5WorkItems(String inputFileName, URL fedoraUrl) throws Exception {
        Path inputFile = Paths.get(inputFileName);
        if (Files.notExists(inputFile) || !Files.isRegularFile(inputFile) || !Files.isReadable(inputFile))
            throw new IllegalArgumentException(String.format("No input file: %s", inputFile));

        List<String> lines = Files.readAllLines(inputFile);
        return fedoraUrl == null ? lines : lines.stream()
                .map(l -> String.format("%s/%s", fedoraUrl, l.substring(0, l.length() - 3))).collect(Collectors.toList());
    }

    private void executeExperiment(RabbitMQCommand experiment, Map<String, Object> headers) throws Exception {
        producer.purgeWorkItems();
        for (String workItem : hdf5WorkItems)
            producer.scheduleWorkItem(workItem);
        for (int i = 0; i < workerCounts.stream().reduce(0, Integer::sum); i++)
            producer.scheduleWorkItem(STOP_WORK_ITEM);

        List<String> hosts = producer.sendControlMessage(experiment, activeWorkers.size(), headers);
        if (!hosts.stream().allMatch(activeWorkers::contains))
            throw new Exception("Unexpected error occurred. Received acknowledge from unknown workers");

        executedCommands.add(experiment);
    }
}
