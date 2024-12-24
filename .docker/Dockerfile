FROM exoplatform/jdk:openjdk-11-ubuntu-22 AS base

RUN groupadd -r -g 1000 airline \
    && useradd -u 1000 -r -g 1000 -d /home/airline airline
RUN mkdir -p /home/airline/bin \
    && chown -vR airline:airline /home/airline \
    && chmod -vR 755 /home/airline
USER airline
ENV PATH="$PATH:/home/airline/bin"

RUN cd /tmp \
    && curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs \
    && chmod +x cs \
    && ./cs setup --yes --install-dir /home/airline/bin \
    && ./cs version \
    && sbt --version

WORKDIR /home/airline

ENTRYPOINT ["tail", "-f", "/dev/null"]