FROM openjdk:8 AS TEMP_BUILD_IMAGE
LABEL author="Stefan Meier <Ext-Stefan.Meier@opuscapita.com>"
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update
RUN apt-get install -y apt-utils
RUN apt-get -y install htop

RUN curl -sL https://deb.nodesource.com/setup_13.x | bash - \
  && apt-get install -y nodejs
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

ADD .mvn $WRKDIR/.mvn
ADD mvnw $WRKDIR/mvnw
ADD mvnw.cmd $WRKDIR/mvnw.cmd

RUN chmod +x mvnw

ADD package.json $WRKDIR/package.json
ADD package-lock.json $WRKDIR/package-lock.json
ADD webpack.development.config.js $WRKDIR/webpack.development.config.js
ADD webpack.production.config.js $WRKDIR/webpack.production.config.js
ADD pom.xml $WRKDIR/pom.xml

RUN npm install
RUN npm run webpack-build-dev
# Adding source, compile and package into a fat jar
RUN ./mvnw -DskipTests clean package
RUN mkdir -p $APPDIR
ADD setup-consul.sh $APPDIR/setup-consul.sh
RUN mv $TRGTDIR/SFTPj-0.0.1.jar $APPDIR/SFTPj.jar
RUN cp -rp $WRKDIR/src/main/resources/static/built/ $APPDIR/built/

FROM openjdk:8
LABEL author="Stefan Meier <Stefan.Meier@cdi-ag.de>"

ENV APPDIR=/usr/app
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -XshowSettings:vm"

WORKDIR $APPDIR

COPY --from=TEMP_BUILD_IMAGE $APPDIR/SFTPj.jar .
COPY --from=TEMP_BUILD_IMAGE $APPDIR/setup-consul.sh .
COPY --from=TEMP_BUILD_IMAGE $APPDIR/built ./built

HEALTHCHECK --interval=15s --timeout=30s --start-period=40s --retries=15 \
CMD netstat -an | grep 2222 > /dev/null; if [ 0 != $? ]; then exit 1; fi;

EXPOSE 2222
EXPOSE 3058

ENTRYPOINT if [${LOAD_CONSUL}]; then exec java $JAVA_OPTS -jar $APPDIR/SFTPj.jar --setup-consul; else exec java $JAVA_OPTS -jar $APPDIR/SFTPj.jar; fi
