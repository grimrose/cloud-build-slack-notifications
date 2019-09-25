FROM node:10-buster-slim

ARG SBT_VERSION=1.3.2

# install jdk and sbt
RUN mkdir -p /usr/share/man/man1 \
  && apt-get update -qqy \
  && apt-get install -qqy curl openjdk-11-jdk-headless \
  && curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb \
  && dpkg -i sbt-$SBT_VERSION.deb \
  && rm sbt-$SBT_VERSION.deb \
  && apt-get install sbt \
  && apt-get remove -qqy --purge curl \
  && rm /var/lib/apt/lists/*_*
