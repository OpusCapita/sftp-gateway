FROM openjdk:8-alpine

# Install maven
RUN apk update
RUN apk add maven
RUN apk add htop
RUN apk add mysql-client

ENV WRKDIR=/code
ENV TRGTDIR=$WRKDIR/target
ENV APPDIR=/usr/app
ENV UPLOADDIR=/home/upload

ENV SERVICE_NAME=sftp-gateway
 
WORKDIR $WRKDIR

# Prepare by downloading dependencies
ADD pom.xml $WRKDIR/pom.xml
RUN mvn dependency:resolve

# Adding source, compile and package into a fat jar

RUN ls -lAh $WRKDIR
ADD src $WRKDIR/src
RUN mvn package
RUN ls -lAh $TRGTDIR
RUN mkdir -p $APPDIR
RUN ls -lAh $APPDIR
RUN mv $TRGTDIR/SFTPj-0.0.1.jar $APPDIR/SFTPj.jar
RUN rm -r $WRKDIR
RUN mkdir -p $UPLOADDIR

WORKDIR $APPDIR

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2222 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

EXPOSE 2222

#CMD ["java", "-jar", "$APPDIR/SFTPj.jar"]
ENTRYPOINT exec java -jar $APPDIR/SFTPj.jar
