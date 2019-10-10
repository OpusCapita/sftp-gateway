#FROM openjdk:8-alpine
#FROM maven:3-jdk-8-alpine
FROM maven:3-jdk-8
RUN apt-get update
RUN apt-get install htop
# Install maven
#RUN apk update
#RUN apk add maven
#RUN apk add htop
#RUN apk add mysql-client
#RUN apk add curl
#RUN apk add nodejs
#RUN apk add nodejs-npm

RUN apt-get -y install mysql-client
#RUN apt-get install nodejs

ENV SERVICE_NAME=sftp-gateway

ENV WRKDIR=/code
ENV TRGTDIR=$WRKDIR/target
ENV APPDIR=/usr/app
ENV UPLOADDIR=/home/upload

ENV NODE_ENV=development
ENV NODE_PATH=$WRKDIR/node_modules
 
WORKDIR $WRKDIR
ADD src $WRKDIR/src

ADD package.json $WRKDIR/package.json
ADD webpack.config.js $WRKDIR/webpack.config.js
# building local frontend
#RUN npm install && npm cache verify
#RUN npm run build

# Prepare by downloading dependencies
ADD pom.xml $WRKDIR/pom.xml
RUN mvn dependency:resolve

# Adding source, compile and package into a fat jar

RUN mkdir -p "target"
RUN mvn package
RUN mkdir -p $APPDIR
RUN mv $TRGTDIR/SFTPj-0.0.1.jar $APPDIR/SFTPj.jar
#RUN rm -r $WRKDIR

WORKDIR $APPDIR

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2222 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

EXPOSE 2222
EXPOSE 2223

ENTRYPOINT exec java -jar $APPDIR/SFTPj.jar
