package edu.vt.sil.administrator;

import com.google.gson.*;
import edu.vt.sil.messaging.RabbitMQCommand;
import edu.vt.sil.messaging.RabbitMQProducer;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: dedocibula
 * Created on: 2.3.2016.
 */
public final class BatchAdministrator {
    private static final String PROPERTY_FILE = "/experiments-setup.properties";

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream in = BatchAdministrator.class.getResourceAsStream(PROPERTY_FILE)) {
            properties.load(in);
        }

        System.out.println("Establishing connection to RabbitMQ host");
        try (RabbitMQProducer producer = createProducer(properties)) {
            System.out.println("Connection established\n");

            System.out.println("Extracting properties...");
            String fedoraUrl = extractFedoraUrl(properties);
            String dataset = extractDataSetInputFile(properties);
            String storageType = extractExternalStorageType(properties);
            String storageDirectory = extractExternalStorageDirectory(properties);
            CommandHandler handler = createCommandHandler(producer, properties);
            String workerIps = extractWorkersPublicIps(properties);
            Map<String, String> remoteProps = extractRemoteResultsProperties(properties);
            String localResultsDirectory = extractLocalResultsDirectory(properties);
            List<Map<String, List<String>>> benchmarkRuns = extractBenchmarkRuns(properties);
            System.out.println("Properties extracted\n");

            System.out.println("======================================================");
            System.out.println("STARTING BENCHMARK...");
            System.out.println("======================================================");

            for (Map<String, List<String>> run : benchmarkRuns) {
                System.out.println(String.format("Current worker counts: %s\n", run.get("workerCounts")));
                handler.handleCommand(AdministratorCommand.START_WORKERS, run.get("workerCounts").stream().collect(Collectors.joining(",")));
                for (String step : run.get("steps")) {
                    AdministratorCommand command = extractCommand(step);
                    //noinspection ConstantConditions
                    switch (command) {
                        case RUN_EXPERIMENT1:
                            handler.handleCommand(AdministratorCommand.RUN_EXPERIMENT1, fedoraUrl, storageType, storageDirectory, dataset);
                            break;
                        case RUN_EXPERIMENT2:
                            handler.handleCommand(AdministratorCommand.RUN_EXPERIMENT2, fedoraUrl, dataset);
                            break;
                        case RUN_EXPERIMENT3:
                            handler.handleCommand(AdministratorCommand.RUN_EXPERIMENT3, fedoraUrl, dataset);
                            break;
                        default:
                            System.out.println(String.format("Skipping illegal command: %s", command));
                    }
                }
                handler.handleCommand(AdministratorCommand.FETCH_RESULTS, workerIps, remoteProps.get("command"),
                        localResultsDirectory, remoteProps.get("prefix"), remoteProps.get("suffixes"));
                handler.handleCommand(AdministratorCommand.STOP_WORKERS);
            }

            handler.handleCommand(AdministratorCommand.PROCESS_RESULTS, localResultsDirectory, RabbitMQCommand.EXPERIMENT1.name().toLowerCase());
            handler.handleCommand(AdministratorCommand.PROCESS_RESULTS, localResultsDirectory, RabbitMQCommand.EXPERIMENT2.name().toLowerCase());
            handler.handleCommand(AdministratorCommand.PROCESS_RESULTS, localResultsDirectory, RabbitMQCommand.EXPERIMENT3.name().toLowerCase());

            System.out.println("======================================================");
            System.out.println("FINISHING BENCHMARK...");
            System.out.println("======================================================");
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    private static RabbitMQProducer createProducer(Properties properties) throws Exception {
        String rabbitMQHost = properties.getProperty("rabbitmq-host");
        if (rabbitMQHost == null || rabbitMQHost.isEmpty()) {
            System.out.println("Cannot use null/empty RabbitMQ host");
            System.exit(-1);
        }

        String rabbitMQUserName = properties.getProperty("rabbitmq-username");
        if (rabbitMQUserName == null || rabbitMQUserName.isEmpty()) {
            System.out.println("Cannot use null/empty RabbitMQ username");
            System.exit(-1);
        }

        String rabbitMQPassword = properties.getProperty("rabbitmq-password");
        if (rabbitMQPassword == null || rabbitMQPassword.isEmpty()) {
            System.out.println("Cannot use null/empty RabbitMQ password");
            System.exit(-1);
        }

        return new RabbitMQProducer(rabbitMQHost, rabbitMQUserName, rabbitMQPassword);
    }

    private static String extractFedoraUrl(Properties properties) throws Exception {
        String fedoraUrl = properties.getProperty("fedora-url");
        if (fedoraUrl == null || fedoraUrl.isEmpty()) {
            System.out.println("Cannot use null/empty Fedora url");
            System.exit(-1);
        }

        URL url = new URL(fedoraUrl);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("HEAD");
        int responseCode = huc.getResponseCode();

        if (responseCode != 200) {
            System.out.println("Fedora is not reachable");
            System.exit(-1);
        }

        System.out.println(String.format("Fedora url: %s", fedoraUrl));
        return fedoraUrl;
    }

    private static String extractDataSetInputFile(Properties properties) {
        String dataset = properties.getProperty("dataset-input-file");
        if (dataset == null || dataset.isEmpty()) {
            System.out.println("Cannot use null/empty dataset input file");
            System.exit(-1);
        }

        Path inputFile = Paths.get(dataset);
        if (Files.notExists(inputFile) || !Files.isRegularFile(inputFile) || !Files.isReadable(inputFile)) {
            System.out.println(String.format("No input file: %s", inputFile));
            System.exit(-1);
        }

        System.out.println(String.format("Dataset input file: %s", dataset));
        return dataset;
    }

    private static String extractExternalStorageType(Properties properties) {
        String storageType = properties.getProperty("external-storage-type");
        if (storageType == null || storageType.isEmpty() ||
                !Arrays.stream(StorageType.values()).map(Enum::toString).anyMatch(t -> t.equals(storageType.toUpperCase()))) {
            System.out.println("Cannot use null/empty external storage type");
            System.exit(-1);
        }

        System.out.println(String.format("External storage type: %s", storageType));
        return storageType;
    }

    private static String extractExternalStorageDirectory(Properties properties) {
        String storageDirectory = properties.getProperty("external-storage-directory");
        if (storageDirectory == null || storageDirectory.isEmpty()) {
            System.out.println("Cannot use null/empty external storage directory");
            System.exit(-1);
        }

        System.out.println(String.format("External storage directory: %s", storageDirectory));
        return storageDirectory;
    }

    private static CommandHandler createCommandHandler(RabbitMQProducer producer, Properties properties) throws Exception {
        String remoteUserName = properties.getProperty("ssh-username");
        if (remoteUserName == null || remoteUserName.isEmpty()) {
            System.out.println("Cannot use null/empty remote user name");
            System.exit(-1);
        }

        String privateKeyName = properties.getProperty("ssh-private-key-name");
        if (privateKeyName == null || privateKeyName.isEmpty()) {
            System.out.println("Cannot use null/empty private key name");
            System.exit(-1);
        }

        System.out.println(String.format("Remote username: %s", remoteUserName));
        System.out.println(String.format("Private key: %s", privateKeyName));
        return new CommandHandler(producer, remoteUserName, privateKeyName);
    }

    private static String extractWorkersPublicIps(Properties properties) {
        String workersPublicIps = properties.getProperty("workers-public-ips");
        if (workersPublicIps == null)
            throw new IllegalArgumentException("Cannot use null worker ips");
        if (Arrays.stream(workersPublicIps.split(",")).anyMatch(h -> h == null || h.isEmpty()))
            throw new IllegalArgumentException("Cannot use null/empty worker ip");

        System.out.println(String.format("Workers public IPs: %s", workersPublicIps));
        return workersPublicIps;
    }

    private static Map<String, String> extractRemoteResultsProperties(Properties properties) {
        Map<String, String> props = new HashMap<>();
        props.put("command", properties.getProperty("remote-results-command", "python collector.py"));
        props.put("prefix", properties.getProperty("remote-results-prefix", "experiment"));
        props.put("suffixes", properties.getProperty("remote-results-suffixes", ".csv,.out"));

        System.out.println(String.format("Remote results command: %s", props.get("command")));
        System.out.println(String.format("Remote results prefix: %s", props.get("prefix")));
        System.out.println(String.format("Remote results suffixes: %s", props.get("suffixes")));
        return props;
    }

    private static String extractLocalResultsDirectory(Properties properties) throws Exception {
        Path localDirectory = Paths.get(properties.getProperty("local-results-directory"));
        if (Files.notExists(localDirectory) || !Files.isDirectory(localDirectory))
            throw new IllegalArgumentException(String.format("No directory: %s", localDirectory));

        Path currentDateDir = localDirectory.resolve(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH").format(LocalDateTime.now()));
        if (Files.notExists(currentDateDir))
            Files.createDirectory(currentDateDir);

        System.out.println(String.format("Local results directory: %s", localDirectory));
        return currentDateDir.toString();
    }

    private static List<Map<String, List<String>>> extractBenchmarkRuns(Properties properties) throws Exception {
        List<Map<String, List<String>>> result = new ArrayList<>();
        Path benchmarkFile = Paths.get(properties.getProperty("benchmark-suite-file"));
        if (Files.notExists(benchmarkFile) || !Files.isRegularFile(benchmarkFile))
            throw new IllegalArgumentException(String.format("No directory: %s", benchmarkFile));

        JsonObject benchmarkSuite = new JsonParser().parse(Files.newBufferedReader(benchmarkFile)).getAsJsonObject();
        JsonArray benchmarks = benchmarkSuite.getAsJsonArray("benchmarks");
        for (JsonElement benchmark : benchmarks) {
            Map<String, List<String>> benchmarkMap = new HashMap<>();
            List<Double> workerCounts = new Gson().<List<Double>>fromJson(benchmark.getAsJsonObject().getAsJsonArray("worker_counts"), List.class);
            if (workerCounts.stream().anyMatch(count -> count < 1))
                throw new IllegalArgumentException("Worker counts must be integers greater than 0");
            benchmarkMap.put("workerCounts", workerCounts.stream().map(count -> String.valueOf(count.intValue())).collect(Collectors.toList()));
            List<String> steps = new Gson().<List<String>>fromJson(benchmark.getAsJsonObject().getAsJsonArray("steps"), List.class);
            if (steps.stream().anyMatch(step -> extractCommand(step) == null))
                throw new IllegalArgumentException("Steps must correspond to administrative commands (RUN_EXPERIMENT");
            benchmarkMap.put("steps", steps);
            result.add(benchmarkMap);
        }

        System.out.println(String.format("Benchmark suite: %s", benchmarkFile));
        return result;
    }

    private static AdministratorCommand extractCommand(String command) {
        try {
            return AdministratorCommand.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
