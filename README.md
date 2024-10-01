## 概要

* grpc-java : https://github.com/grpc/grpc-java/tree/master
* grpc-spring-boot-starter : https://github.com/LogNet/grpc-spring-boot-starter
  * getting started : https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html
* java quick start : https://grpc.io/docs/languages/java/quickstart/

## build

1. `./mvnw clean package -Dmaven.test.Skip=true`
2. `cp target/generated-sources/protobuf/java/com/example/grpc/api/rpc/Rpc.java src/main/java/com/example/grpc/api/rpc/`
2. `cp target/generated-sources/protobuf/grpc-java/com/example/grpc/api/rpc/RpcServiceGrpc.java src/main/java/com/example/grpc/api/rpc/`


