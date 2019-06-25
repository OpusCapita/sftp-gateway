FROM openjdk:8-alpine

# Install maven
RUN apk update
RUN apk add maven

ENV WRKDIR=/code
ENV TRGTDIR=$WRKDIR/target
ENV APPDIR=/usr/app
 
WORKDIR $WRKDIR

# Prepare by downloading dependencies
ADD pom.xml $WRKDIR/pom.xml
RUN mvn dependency:resolve

# Adding source, compile and package into a fat jar
ADD src $WRKDIR/src
RUN mvn package
RUN ls -lAh $TRGTDIR
RUN mkdir -p $APPDIR
RUN ls -lAh $APPDIR
RUN mv $TRGTDIR/SFTPj-0.0.1.jar $APPDIR/SFTPj.jar
RUN rm -r $WRKDIR

#HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
#CMD curl --silent --fail http://localhost:3052/api/health/check || exit 1

EXPOSE 2222
ENTRYPOINT exec java -jar $APPDIR/SFTPj.jar
