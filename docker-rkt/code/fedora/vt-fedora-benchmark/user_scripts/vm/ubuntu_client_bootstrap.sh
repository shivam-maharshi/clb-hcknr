#!/bin/bash
RABBITMQ_URL=
RABBITMQ_USERNAME="admin"
RABBITMQ_PASSWORD="admin"

sudo apt-get upgrade && sudo apt-get update && sudo apt-get install -y git
git clone https://DedoCibula@bitbucket.org/DedoCibula/vt-fedora-benchmark.git
vt-fedora-benchmark/utils/client_setup.sh
ln -s vt-fedora-benchmark/orchestrators/process_orchestrator.py collector.py

wget https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo pip install supervisor
rm get-pip.py

echo_supervisord_conf > /etc/supervisord.conf
echo "[program:process_orchestrator]" >> /etc/supervisord.conf
echo "command=nice -n -5 python process_orchestrator.py start_with ${RABBITMQ_URL} ${RABBITMQ_USERNAME} ${RABBITMQ_PASSWORD}" >> /etc/supervisord.conf
echo "directory=${PWD}/vt-fedora-benchmark/orchestrators" >> /etc/supervisord.conf
echo "redirect_stderr=true" >> /etc/supervisord.conf
echo "stdout_logfile=${PWD}/vt-fedora-benchmark/orchestrators/experiment.out" >> /etc/supervisord.conf
echo "autostart=true" >> /etc/supervisord.conf
echo "autorestart=unexpected" >> /etc/supervisord.conf

supervisord