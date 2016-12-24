#!/bin/bash

for i in {1..20} ; do 
	sudo rkt run registry-1.docker.io/library/busybox:latest --exec echo -- "done"
done