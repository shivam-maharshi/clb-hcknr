#!/bin/bash
sudo add-apt-repository ppa:openjdk-r/ppa -y

sudo apt-get update && sudo apt-get install -y \
	build-essential \
	curl \
	screen \
	openssh-server \
	software-properties-common \
	vim \
	wget \
	htop tree zsh fish

sudo apt-get update -y
sudo env DEBIAN_FRONTEND=noninteractive apt-get -y -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" dist-upgrade

wget https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo apt-get install unzip -y
sudo apt-get install libpcap-dev -y
sudo pip install boto3
sudo pip install pika
sudo apt-get install python-dev -y
sudo apt-get install python-numpy python-nose -y
sudo apt-get install python-scipy -y
sudo pip install cython
sudo apt-get install libhdf5-dev -y
sudo pip install xmltodict
sudo pip install h5py
sudo pip install supervisor
sudo apt-get install openjdk-8-jdk -y
rm get-pip.py

cd
sudo apt-get install mediainfo -y
wget http://projects.iq.harvard.edu/files/fits/files/fits-0.9.0.zip?m=1449588471 -O fits-0.9.0.zip
unzip fits-0.9.0.zip
chmod +x fits-0.9.0/fits.sh

if [ -d "vt-fedora-benchmark/experiments" ]; then
	mv fits-0.9.0.zip vt-fedora-benchmark/experiments
	mv fits-0.9.0 vt-fedora-benchmark/experiments
	cd vt-fedora-benchmark/experiments
fi

echo export PATH="$PATH:${PWD}/fits-0.9.0/" >> ~/.bashrc
source ~/.bashrc

sudo apt-get install ntp -y
sudo service ntp restart
