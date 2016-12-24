#!/bin/bash

for i in {1..20} ; do 
	docker run --rm -it busybox:latest echo "done"
done