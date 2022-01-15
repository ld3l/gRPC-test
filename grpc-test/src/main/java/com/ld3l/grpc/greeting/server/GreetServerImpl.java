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
                .setResult("Hello " + greeting.getFirstName() + " " + greeting.getLastName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void greetStream(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        for (int i = 0; i < 50; i++) {
            GreetResponse build = GreetResponse.newBuilder()
                    .setResult("Hello " + greeting.getFirstName() + " " + greeting.getLastName() + " response num:" + i)
                    .build();
            responseObserver.onNext(build);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }
}
