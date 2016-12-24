package edu.vt.sil.administrator;

import edu.vt.sil.messaging.RabbitMQProducer;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Author: dedocibula
 * Created on: 16.2.2016.
 */
public final class InteractiveAdministrator {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println("Please use parameters:");
            System.out.println("   <rabbitmq host> <rabbitmq username> <rabbitmq password> <remote username> <private key name>");
            System.exit(-1);
        }

        String host = args[0];
        if (host == null || host.isEmpty()) {
            System.out.println("Cannot use null/empty host");
            System.exit(-1);
        }

        String userName = args[1];
        if (userName == null || userName.isEmpty()) {
            System.out.println("Cannot use null/empty user_name");
            System.exit(-1);
        }

        String password = args[2];
        if (password == null || password.isEmpty()) {
            System.out.println("Cannot use null/empty password");
            System.exit(-1);
        }

        String remoteUserName = args[3];
        if (remoteUserName == null || remoteUserName.isEmpty()) {
            System.out.println("Cannot use null/empty remote user name");
            System.exit(-1);
        }

        String privateKeyName = args[4];
        if (privateKeyName == null || privateKeyName.isEmpty()) {
            System.out.println("Cannot use null/empty private key name");
            System.exit(-1);
        }

        System.out.println("Establishing connection to RabbitMQ host");
        try (RabbitMQProducer producer = new RabbitMQProducer(host, userName, password)) {
            System.out.println("Connection established\n");
            CommandHandler handler = new CommandHandler(producer, remoteUserName, privateKeyName);

            try (Scanner scanner = new Scanner(System.in)) {
                String line;
                printHeader(handler);
                while (!(line = scanner.nextLine()).isEmpty()) {
                    try {
                        handleInput(line, handler);
                    } catch (Exception e) {
                        System.out.println(e.toString() + "\n");
                    }
                    printHeader(handler);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void printHeader(CommandHandler handler) {
        System.out.println("------------------------------------------------------------");
        System.out.println("Please enter your command (Empty command or CTRL+C to exit):");
        handler.printCommandLabels();
        System.out.println("------------------------------------------------------------");
    }

    private static void handleInput(String line, CommandHandler handler) throws Exception {
        String[] parts = Arrays.stream(line.trim().split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)")).map(p -> p.replace("\"", "")).toArray(String[]::new);
        if (parts.length < 1) {
            System.out.println("Too few arguments\n");
            return;
        }

        AdministratorCommand command;
        try {
            command = AdministratorCommand.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(String.format("Unrecognized command: %s\n", parts[0]));
            return;
        }
        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);

        handler.handleCommand(command, arguments);
    }
}
