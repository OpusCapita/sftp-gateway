FROM openjdk:8-alpine

# Install maven
RUN apk update
RUN apk add maven
RUN apk add htop
RUN apk add mysql-client
RUN apk add curl
RUN apk add nodejs
RUN apk add npm

ENV SERVICE_NAME=sftp-gateway

ENV WRKDIR=/code
ENV TRGTDIR=$WRKDIR/target
ENV APPDIR=/usr/app
ENV UPLOADDIR=/home/upload

ENV NODE_ENV=development NODE_PATH=$WRKDIR/node_modules

# install nodejsnp
#RUN apt-get install -y curl \
#  && curl -sL https://deb.nodesource.com/setup_9.x | bash - \
#  && apt-get install -y nodejs \
#  && curl -L https://www.npmjs.com/install.sh | sh
 
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

RUN ls -lAh $WRKDIR
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
EXPOSE 2223

ENTRYPOINT exec java -jar $APPDIR/SFTPj.jar
