package com.ld3l.grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServerImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        GreetResponse response = GreetResponse.newBuilder()
                .setResult("Hello " + greeting.getFirstName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
//        super.greet(request, responseObserver);
    }
}
