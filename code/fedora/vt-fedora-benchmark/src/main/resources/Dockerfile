FROM java:8-jre
MAINTAINER Dedo Cibula <dedo.cibula@gmail.com>

RUN apt-get update && apt-get install -y \
	build-essential \
	fish \
	git \
	htop \
	mediainfo \
	openssh-server \
	software-properties-common \
	tree \
	vim \
	zsh

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get install -y \
	libcap-dev \
	libhdf5-dev \
	python-dev \
	python-h5py \
	python-numpy \
	python-nose \
	python-pycurl \
	python-scipy
RUN wget -q https://bootstrap.pypa.io/get-pip.py -O get-pip.py && python get-pip.py && pip install boto3 pika xmltodict requests && rm get-pip.py

ENV BENCHMARK_HOME /vt-fedora-benchmark/experiments
ENV BENCHMARK_URL https://DedoCibula@bitbucket.org/DedoCibula/vt-fedora-benchmark.git
ENV FITS_URL http://projects.iq.harvard.edu/files/fits/files/fits-0.9.0.zip?m=1449588471

RUN git clone "$BENCHMARK_URL" vt-fedora-benchmark
RUN cd "$BENCHMARK_HOME" && wget -q "$FITS_URL" -O fits-0.9.0.zip && unzip -q fits-0.9.0.zip && chmod +x fits-0.9.0/fits.sh && rm fits-0.9.0.zip
ENV PATH "$PATH:${BENCHMARK_HOME}/fits-0.9.0/"

WORKDIR $BENCHMARK_HOME
CMD ["python"]