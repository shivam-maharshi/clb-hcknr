package edu.vt.sil.messaging;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Author: dedocibula
 * Created on: 16.2.2016.
 */
public final class RabbitMQProducer implements AutoCloseable {
    private static final String WAIT_QUEUE_NAME = "wait_queue";
    private static final String WAIT_QUEUE_CALLBACK = "wait_queue_callback_" + UUID.randomUUID();
    private static final String CONTROL_TOPIC_NAME = "control_topic";
    private static final String CONTROL_TOPIC_CALLBACK = "control_topic_callback_" + UUID.randomUUID();
    private static final String WORK_QUEUE_NAME = "work_queue";

    private Connection connection;
    private Channel channel;
    private QueueingConsumer waitQueueConsumer;
    private QueueingConsumer controlTopicConsumer;
    private Map<String, Object> connectionHeaders;

    public RabbitMQProducer(String host, String userName, String password) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(userName);
        factory.setPassword(password);

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(WAIT_QUEUE_NAME, true, false, false, null);
        channel.queueDeclare(WAIT_QUEUE_CALLBACK, false, true, true, null);
        waitQueueConsumer = new QueueingConsumer(channel);
        channel.basicConsume(WAIT_QUEUE_CALLBACK, true, waitQueueConsumer);

        channel.exchangeDeclare(CONTROL_TOPIC_NAME, "fanout");
        channel.queueDeclare(CONTROL_TOPIC_CALLBACK, false, true, true, null);
        controlTopicConsumer = new QueueingConsumer(channel);
        channel.basicConsume(CONTROL_TOPIC_CALLBACK, true, controlTopicConsumer);

        channel.queueDeclare(WORK_QUEUE_NAME, true, false, false, null);

        Map<String, Object> headers = new HashMap<>();
        headers.put("controlTopicName", CONTROL_TOPIC_NAME);
        headers.put("workQueueName", WORK_QUEUE_NAME);
        connectionHeaders = headers;
    }

    public List<String> addWorkers(int count) throws Exception {
        connectionHeaders.put("workerCount", count);
        AMQP.BasicProperties properties = MessageProperties.TEXT_PLAIN
                .builder()
                .headers(connectionHeaders)
                .correlationId(UUID.randomUUID().toString())
                .replyTo(WAIT_QUEUE_CALLBACK)
                .build();
        channel.basicPublish("", WAIT_QUEUE_NAME, properties, RabbitMQCommand.ADD_WORKERS.name().getBytes(StandardCharsets.UTF_8));

        return waitForHostAcknowledgements(waitQueueConsumer, properties.getCorrelationId(), count);
    }

    public List<String> sendControlMessage(RabbitMQCommand command, int waitAcknowledgements) throws Exception {
        return this.sendControlMessage(command, waitAcknowledgements, null);
    }

    public List<String> sendControlMessage(RabbitMQCommand command, int waitAcknowledgements, Map<String, Object> arguments) throws Exception {
        Objects.requireNonNull(command);
        if (waitAcknowledgements < 0)
            throw new IllegalArgumentException("Wait Acknowledgements counter cannot be negative");

        AMQP.BasicProperties properties = MessageProperties.TEXT_PLAIN
                .builder()
                .headers(arguments)
                .correlationId(UUID.randomUUID().toString())
                .replyTo(CONTROL_TOPIC_CALLBACK)
                .build();
        channel.basicPublish(CONTROL_TOPIC_NAME, "", properties, command.name().getBytes(StandardCharsets.UTF_8));

        return waitForHostAcknowledgements(controlTopicConsumer, properties.getCorrelationId(), waitAcknowledgements);
    }

    public void scheduleWorkItem(String workItem) throws Exception {
        Objects.requireNonNull(workItem);

        channel.basicPublish("", WORK_QUEUE_NAME, MessageProperties.TEXT_PLAIN, workItem.getBytes(StandardCharsets.UTF_8));
    }

    public void purgeWorkItems() throws Exception {
        channel.queuePurge(WORK_QUEUE_NAME);
    }

    private List<String> waitForHostAcknowledgements(QueueingConsumer consumer, String correlationId, int waitAcknowledgements) throws InterruptedException {
        List<String> result = new ArrayList<>();

        while (result.size() < waitAcknowledgements) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(correlationId))
                result.add(new String(delivery.getBody()));
        }

        return result;
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
