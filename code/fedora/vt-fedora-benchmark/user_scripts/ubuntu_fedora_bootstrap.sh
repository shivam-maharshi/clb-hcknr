#!/bin/bash
sudo apt-get upgrade && sudo apt-get update && sudo apt-get install -y git
git clone https://DedoCibula@bitbucket.org/DedoCibula/vt-fedora-benchmark.git
vt-fedora-benchmark/utils/fedora_setup.sh