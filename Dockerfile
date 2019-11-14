FROM maven:3-jdk-8 AS TEMP_BUILD_IMAGE
LABEL author="Stefan Meier <Stefan.Meier@cdi-ag.de>"
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update
RUN apt-get -y install htop

RUN curl -sL https://deb.nodesource.com/setup_13.x | bash - \
  && apt-get install -y nodejs
#RUN apt-get -y install nodejs
#RUN apt-get -y install npm

RUN apt-get -y install mysql-client

ENV SERVICE_NAME=sftp-gateway

ENV WRKDIR=/code
ENV TRGTDIR=$WRKDIR/target
ENV APPDIR=/usr/app
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XshowSettings:vm"

ENV NODE_ENV=development
ENV NODE_PATH=$WRKDIR/node_modules
 
WORKDIR $WRKDIR
ADD src $WRKDIR/src

ADD package.json $WRKDIR/package.json
ADD package-lock.json $WRKDIR/package-lock.json
ADD webpack.config.js $WRKDIR/webpack.config.js
ADD pom.xml $WRKDIR/pom.xml

RUN npm install
RUN npm run webpack-build-prod

# Adding source, compile and package into a fat jar
RUN mvn clean package
RUN mkdir -p $APPDIR
RUN mv $TRGTDIR/SFTPj-0.0.1.jar $APPDIR/SFTPj.jar

FROM maven:3-jdk-8
LABEL author="Stefan Meier <Stefan.Meier@cdi-ag.de>"

ENV APPDIR=/usr/app
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XshowSettings:vm"

WORKDIR $APPDIR

ADD host.ser $APPDIR/host.ser
ADD acl.json $APPDIR/acl.json

COPY --from=TEMP_BUILD_IMAGE $APPDIR/SFTPj.jar .
#COPY --from=TEMP_BUILD_IMAGE $APPDIR/host.ser .
#COPY --from=TEMP_BUILD_IMAGE $APPDIR/acl.json .

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2223 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2222 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

EXPOSE 2222
EXPOSE 2223

ENTRYPOINT exec java $JAVA_OPTS -jar $APPDIR/SFTPj.jar --permissions=./acl.json
