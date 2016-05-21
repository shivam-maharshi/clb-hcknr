#!/bin/bash
RABBITMQ_URL="localhost"
RABBITMQ_USERNAME="admin"
RABBITMQ_PASSWORD="admin"

wget https://www.rabbitmq.com/rabbitmq-signing-key-public.asc
sudo apt-key add rabbitmq-signing-key-public.asc
sudo apt-get upgrade && supo apt-get update
sudo apt-get install rabbitmq-server -y
sudo service rabbitmq-server start
sudo rabbitmq-plugins enable rabbitmq_management
sudo rabbitmqctl add_user ${RABBITMQ_USERNAME} ${RABBITMQ_PASSWORD}
sudo rabbitmqctl set_user_tags ${RABBITMQ_USERNAME} administrator
sudo rabbitmqctl set_permissions -p / ${RABBITMQ_USERNAME} ".*" ".*" ".*"
sudo rabbitmqctl delete_user guest
sudo service rabbitmq-server restart
rm rabbitmq-signing-key-public.asc

sudo apt-get upgrade && sudo apt-get update && sudo apt-get install -y git
git clone https://DedoCibula@bitbucket.org/DedoCibula/vt-fedora-benchmark.git
vt-fedora-benchmark/utils/client_setup.sh
ln -s vt-fedora-benchmark/orchestrators/process_orchestrator.py collector.py

echo_supervisord_conf > /etc/supervisord.conf
echo "[program:process_orchestrator]" >> /etc/supervisord.conf
echo "command=nice -n -5 python process_orchestrator.py start_with ${RABBITMQ_URL} ${RABBITMQ_USERNAME} ${RABBITMQ_PASSWORD}" >> /etc/supervisord.conf
echo "directory=${PWD}/vt-fedora-benchmark/orchestrators" >> /etc/supervisord.conf
echo "redirect_stderr=true" >> /etc/supervisord.conf
echo "stdout_logfile=${PWD}/vt-fedora-benchmark/orchestrators/experiment.out" >> /etc/supervisord.conf
echo "autostart=true" >> /etc/supervisord.conf
echo "autorestart=unexpected" >> /etc/supervisord.conf

supervisord