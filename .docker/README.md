## 概要
.protoファイルから`protoc-gen-grpc-java`ではなく自分で`protoc`でビルドする場合はこちら

## build proto
1. `cd .docker`
2. `docker-compose up -d --build`
3. `docker exec -it protoc-parser protoc --java_out=/proto -I=/proto /proto/Rpc.proto`
4. `cp ./proto/com/example/grpc/api/Rpc.java ../src/main/java/com/example/grpc/api`
5. `docker-compose down --rmi all --volumes --remove-orphans`

### version
```
centos : 7.9.2009
protoc : 3.25.3
protobuf : 3.25.3
```

## refs
* protoc version : https://github.com/protocolbuffers/protobuf/releases
* protobuf version : https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java