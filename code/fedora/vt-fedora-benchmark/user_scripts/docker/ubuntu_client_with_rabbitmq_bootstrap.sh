#!/usr/bin/env bash
RABBITMQ_URL="rabbit-mq"
RABBITMQ_USERNAME="admin"
RABBITMQ_PASSWORD="admin"

sudo apt-get upgrade && sudo apt-get update && sudo apt-get install -y \
    curl \
    git \
    ntp \
    vim \
    wget
git clone https://DedoCibula@bitbucket.org/DedoCibula/vt-fedora-benchmark.git
ln -s vt-fedora-benchmark/orchestrators/docker_orchestrator.py collector.py

curl -fsSL https://get.docker.com/ | sh
sudo service ntp restart

docker run -d -p 5672:5672 -p 15672:15672  --hostname ${RABBITMQ_URL} --name ${RABBITMQ_URL} -e RABBITMQ_DEFAULT_USER=${RABBITMQ_USERNAME} -e RABBITMQ_DEFAULT_PASS=${RABBITMQ_PASSWORD} rabbitmq:management
docker pull dedocibula/fedora-benchmark

wget https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo pip install pika
sudo pip install supervisor
rm get-pip.py

echo "127.0.0.1 ${RABBITMQ_URL}" >> /etc/hosts

echo_supervisord_conf > /etc/supervisord.conf
echo "[program:docker_orchestrator]" >> /etc/supervisord.conf
echo "command=nice -n -5 python docker_orchestrator.py start_with ${RABBITMQ_URL} ${RABBITMQ_USERNAME} ${RABBITMQ_PASSWORD} True" >> /etc/supervisord.conf
echo "directory=${PWD}/vt-fedora-benchmark/orchestrators" >> /etc/supervisord.conf
echo "redirect_stderr=true" >> /etc/supervisord.conf
echo "stdout_logfile=${PWD}/vt-fedora-benchmark/orchestrators/experiment.out" >> /etc/supervisord.conf
echo "autostart=true" >> /etc/supervisord.conf
echo "autorestart=unexpected" >> /etc/supervisord.conf

supervisord