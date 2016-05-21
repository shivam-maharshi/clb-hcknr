import pika
import os

manager = None


class WorkerManager(object):
    RUNNING_WORKERS_FILENAME = os.path.join(os.path.dirname(os.path.realpath(__file__)), "running-containers.txt")

    def __init__(self, host_uid, rabbitmq_host, rabbitmq_username, rabbitmq_password):
        super(WorkerManager, self).__init__()
        self.host_uid = host_uid
        self.rabbitmq_host = rabbitmq_host
        self.rabbitmq_username = rabbitmq_username
        self.rabbitmq_password = rabbitmq_password

    def start_workers(self, count, control_topic_name, work_queue_name, acknowledge_queue, correlation_id):
        raise NotImplementedError

    def stop_workers(self):
        raise NotImplementedError


def on_request(ch, method, props, body):
    print "Received command: " + body

    if body == "ADD_WORKERS" and props.correlation_id:
        if not props.headers and ("controlTopicName" not in props.headers or "workQueueName" not in props.headers):
            print "Missing necessary headers (controlTopicName and workQueueName)"
            return

        manager.stop_workers()

        work_queue_name = props.headers["workQueueName"]
        control_topic_name = props.headers["controlTopicName"]
        worker_count = int(props.headers["workerCount"]) if "workerCount" in props.headers else 1

        manager.start_workers(worker_count, control_topic_name, work_queue_name, props.reply_to, props.correlation_id)

        ch.basic_ack(delivery_tag=method.delivery_tag)
    else:
        print "Unrecognized command: " + body


def start_with(worker_manager):
    assert isinstance(worker_manager, WorkerManager)

    global manager
    manager = worker_manager

    credentials = pika.PlainCredentials(worker_manager.rabbitmq_username, worker_manager.rabbitmq_password)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host=worker_manager.rabbitmq_host, credentials=credentials))
    channel = connection.channel()

    channel.queue_declare(queue='wait_queue', durable=True)

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(on_request, queue='wait_queue')

    print "Listening for commands on the wait queue. CTRL + C to exit"

    channel.start_consuming()
