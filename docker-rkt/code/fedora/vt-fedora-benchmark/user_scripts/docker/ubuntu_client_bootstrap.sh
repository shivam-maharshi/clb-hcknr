#!/usr/bin/env bash
RABBITMQ_URL=
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

docker pull dedocibula/fedora-benchmark

wget https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo pip install pika
sudo pip install supervisor
rm get-pip.py

echo_supervisord_conf > /etc/supervisord.conf
echo "[program:docker_orchestrator]" >> /etc/supervisord.conf
echo "command=nice -n -5 python docker_orchestrator.py start_with ${RABBITMQ_URL} ${RABBITMQ_USERNAME} ${RABBITMQ_PASSWORD} False" >> /etc/supervisord.conf
echo "directory=${PWD}/vt-fedora-benchmark/orchestrators" >> /etc/supervisord.conf
echo "redirect_stderr=true" >> /etc/supervisord.conf
echo "stdout_logfile=${PWD}/vt-fedora-benchmark/orchestrators/experiment.out" >> /etc/supervisord.conf
echo "autostart=true" >> /etc/supervisord.conf
echo "autorestart=unexpected" >> /etc/supervisord.conf

supervisord