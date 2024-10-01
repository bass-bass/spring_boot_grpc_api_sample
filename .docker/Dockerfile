FROM centos:7.9.2009
ENV VERSION=25.3
WORKDIR /protocol-buffers
RUN sed -i 's/mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-* && \
    sed -i 's|#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-* && \
    yum -y install vim wget make gcc gcc-c++ && \
    wget https://github.com/protocolbuffers/protobuf/releases/download/v${VERSION}/protobuf-${VERSION}.tar.gz && \
    tar -xvf protobuf-${VERSION}.tar.gz && \
    cd protobuf-${VERSION} && \
    ./configure && \
    make && \
    make install
WORKDIR /proto
