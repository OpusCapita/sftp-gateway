FROM maven:3-jdk-8
RUN apt-get update
RUN apt-get install htop

RUN apt-get -y install mysql-client

ENV SERVICE_NAME=sftp-gateway

ENV WRKDIR=/code
ENV TRGTDIR=$WRKDIR/target
ENV APPDIR=/usr/app

ENV NODE_ENV=development
ENV NODE_PATH=$WRKDIR/node_modules
 
WORKDIR $WRKDIR
ADD src $WRKDIR/src

ADD package.json $WRKDIR/package.json
ADD webpack.config.js $WRKDIR/webpack.config.js

# Prepare by downloading dependencies
ADD pom.xml $WRKDIR/pom.xml
RUN mvn dependency:resolve

# Adding source, compile and package into a fat jar
#RUN mkdir -p "target"
RUN mvn clean install
RUN mkdir -p $APPDIR
RUN mv $TRGTDIR/SFTPj-0.0.1.jar $APPDIR/SFTPj.jar

WORKDIR $APPDIR

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2223 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2222 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

EXPOSE 2222
EXPOSE 2223

ENTRYPOINT exec java -jar $APPDIR/SFTPj.jar
