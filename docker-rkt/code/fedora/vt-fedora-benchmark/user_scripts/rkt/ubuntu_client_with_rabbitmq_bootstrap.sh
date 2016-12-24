#!/usr/bin/env bash
RABBITMQ_URL="localhost"
RABBITMQ_USERNAME="admin"
RABBITMQ_PASSWORD="admin"

sudo apt-get upgrade && sudo apt-get update && sudo apt-get install -y \
    curl \
    git \
    ntp \
    vim \
    wget
git clone https://DedoCibula@bitbucket.org/DedoCibula/vt-fedora-benchmark.git
ln -s vt-fedora-benchmark/orchestrators/rkt_orchestrator.py collector.py

wget https://github.com/coreos/rkt/releases/download/v1.3.0/rkt-v1.3.0.tar.gz
tar xzvf rkt-v1.3.0.tar.gz
mv rkt-v1.3.0 /usr/local/lib/
ln -s /usr/local/lib/rkt-v1.3.0/rkt /usr/bin/rkt
rm rkt-v1.3.0.tar.gz

sudo rkt --insecure-options=image run --set-env=RABBITMQ_DEFAULT_USER=${RABBITMQ_USERNAME} --set-env=RABBITMQ_DEFAULT_PASS=${RABBITMQ_PASSWORD} --net=host --hostname=${RABBITMQ_URL} docker://rabbitmq:management
sudo rkt --insecure-options=image fetch docker://dedocibula/fedora-benchmark

wget https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo pip install pika
sudo pip install supervisor
rm get-pip.py

echo_supervisord_conf > /etc/supervisord.conf
echo "[program:rkt_orchestrator]" >> /etc/supervisord.conf
echo "command=nice -n -5 python rkt_orchestrator.py start_with ${RABBITMQ_URL} ${RABBITMQ_USERNAME} ${RABBITMQ_PASSWORD} /vt-fedora-benchmark/experiments/results True" >> /etc/supervisord.conf
echo "directory=${PWD}/vt-fedora-benchmark/orchestrators" >> /etc/supervisord.conf
echo "redirect_stderr=true" >> /etc/supervisord.conf
echo "stdout_logfile=${PWD}/vt-fedora-benchmark/orchestrators/experiment.out" >> /etc/supervisord.conf
echo "autostart=true" >> /etc/supervisord.conf
echo "autorestart=unexpected" >> /etc/supervisord.conf

supervisord