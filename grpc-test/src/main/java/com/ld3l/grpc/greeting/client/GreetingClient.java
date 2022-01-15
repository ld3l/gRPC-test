package com.ld3l.grpc.greeting.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Stepan")
                .setLastName("Razin")
                .build();
        //base request
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();
        GreetResponse greet = greetServiceBlockingStub.greet(greetRequest);
        System.out.println(greet.getResult());

        //request with stream response
        greetServiceBlockingStub.greetStream(greetRequest).forEachRemaining(System.out::println);

        //stream request
        CountDownLatch latch = new CountDownLatch(1);
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        StreamObserver<GreetRequest> requestStreamObserver = asyncClient.sendManyRequest(new StreamObserver<GreetResponse>() {
            @Override
            public void onNext(GreetResponse value) {
                System.out.println(value);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Compl send data");
                latch.countDown();
            }
        });
        requestStreamObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Ivan").build())
                .build());
        requestStreamObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Peter").build())
                .build());
        requestStreamObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Elza").build())
                .build());
        requestStreamObserver.onNext(GreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Gregory").build())
                .build());

        requestStreamObserver.onCompleted();

        latch.await();

        channel.shutdown();
    }

}
