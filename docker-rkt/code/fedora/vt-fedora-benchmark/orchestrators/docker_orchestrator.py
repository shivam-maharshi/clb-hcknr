import orchestrator
import os
import sys
import uuid
from subprocess import call


class DockerManager(orchestrator.WorkerManager):
    def __init__(self, host_uid, rabbitmq_host, rabbitmq_username, rabbitmq_password, with_link):
        super(DockerManager, self).__init__(host_uid, rabbitmq_host, rabbitmq_username, rabbitmq_password)
        self.running_containers_file = open(orchestrator.WorkerManager.RUNNING_WORKERS_FILENAME, "ar+")
        self.running_containers = [line.strip() for line in self.running_containers_file if line.strip()]
        self.with_link = with_link

    @staticmethod
    def fetch_results():
        with open(orchestrator.WorkerManager.RUNNING_WORKERS_FILENAME) as f:
            running_containers = f.readlines()
        running_containers = [container.strip() for container in running_containers if container.strip()]
        for i in range(0, len(running_containers)):
            base_path = os.path.join(os.path.dirname(os.path.dirname(os.path.realpath(__file__))), str(i + 1))
            if not os.path.exists(base_path):
                os.makedirs(base_path)
            for file in os.listdir(base_path):
                file_path = os.path.join(base_path, file)
                try:
                    if os.path.isfile(file_path):
                        os.unlink(file_path)
                except Exception, e:
                    print e
            call("docker cp {}:/vt-fedora-benchmark/experiments/. {}".format(running_containers[i], base_path),
                 shell=True)
            with open(os.path.join(base_path, "experiment.out"), "w") as f, open(os.devnull, 'w') as fnull:
                call(["docker", "logs", running_containers[i]], stdout=f, stderr=fnull)
            for file in os.listdir(base_path):
                print os.path.join(base_path, file)

    def start_workers(self, count, control_topic_name, work_queue_name, acknowledge_queue, correlation_id):
        for i in range(1, count + 1):
            id = self.host_uid + "_" + str(i)
            call("docker run -d --privileged " + \
                 ("--link={}:{}".format(self.rabbitmq_host, self.rabbitmq_host) if self.with_link else "") + \
                 " --name=fedora_benchmark_{} dedocibula/fedora-benchmark python experiment_coordinator.py {} {} {} {} {} {} {} {}".format(
                     str(i), self.rabbitmq_host,
                     self.rabbitmq_username, self.rabbitmq_password,
                     id, control_topic_name, work_queue_name,
                     acknowledge_queue, correlation_id), shell=True)
            self.running_containers_file.write("fedora_benchmark_{}\n".format(str(i)))
            self.running_containers.append("fedora_benchmark_{}".format(str(i)))
        self.running_containers_file.flush()

    def stop_workers(self):
        for container in self.running_containers:
            call("docker stop {} && docker rm {}".format(container, container), shell=True)
        self.running_containers = []
        self.running_containers_file.seek(0)
        self.running_containers_file.truncate()


def main():
    command = sys.argv[1]

    if command == "start_with":
        orchestrator.start_with(
            DockerManager(str(uuid.uuid4()), sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5] == "True"))
    elif command == "fetch_results":
        DockerManager.fetch_results()
    else:
        print "Unrecognized command"


if __name__ == '__main__': main()
