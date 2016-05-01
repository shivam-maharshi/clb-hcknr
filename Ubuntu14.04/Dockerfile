FROM ubuntu:14.04
# COPY ./public-html/ /usr/local/apache2/htdocs/
MAINTAINER Shivam Maharshi <shivam.maharshi@gmail.com>

RUN apt-get update && apt-get install -y \
        build-essential \
        git \
        htop \
        mediainfo \
        openssh-server \
        software-properties-common \
        tree \
        vim \
        wget \
        zsh

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get install -y \
        apache2 \
        php5 \
        libapache2-mod-php5 \
        mysql-server \
        mysql-client \
        php5-mysql \
        phpmyadmin

ENV APACHE_WEB_DIR = /var/www/html
RUN cd "$APACHE_WEB_DIR"
RUN wget "https://releases.wikimedia.org/mediawiki/1.26/mediawiki-1.26.2.tar.gz"
RUN tar -xvzf mediawiki-1.26.2.tar.gz
RUN mv mediawiki-1.26.2 wiki
RUN echo "Include /etc/phpmyadmin/apache.conf" >> /etc/apache2/apache2.conf
RUN service apache2 restart
RUN service mysql restart
ENV WIKI_HOME /var/www/html/wiki
WORKDIR $WIKI_HOME
