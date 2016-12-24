# Docker container with Sysbench installed
# for running multiple types of benchmark tests

FROM ubuntu:precise

RUN apt-get update && apt-get install -y \
	sysbench \
	make \
	gcc

VOLUME /coremark

CMD ["/bin/echo", "Execute your benchmark"]
