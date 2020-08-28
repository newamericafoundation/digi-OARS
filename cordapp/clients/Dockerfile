FROM azul/zulu-openjdk:8

LABEL VENDOR="R3 Ltd." MAINTAINER="R3 Ltd."

EXPOSE 8080

WORKDIR /opt/client

RUN mkdir -p /opt/client /home/client \
  && groupadd -g 1000 -r client \
  && useradd -r -u 1000 -g client client \
  && chgrp -R 0 /opt/client \
  && chmod -R g=u /opt/client \
  && chown -R client:client /opt/client /home/client

USER client

VOLUME /opt/client/cordapps

COPY build/libs/clients-0.1.jar /opt/client/clients.jar

CMD ["java", "-jar", "/opt/client/clients.jar"]
