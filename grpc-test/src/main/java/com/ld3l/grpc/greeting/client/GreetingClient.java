package com.ld3l.grpc.greeting.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

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


        CountDownLatch latch = new CountDownLatch(1);
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        //stream request
//        StreamObserver<GreetRequest> requestStreamObserver = asyncClient.sendManyRequest(new StreamObserver<GreetResponse>() {
//            @Override
//            public void onNext(GreetResponse value) {
//                System.out.println(value);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("Compl send data");
//                latch.countDown();
//            }
//        });
//        createStreamOfGreetingRequest()
//                .forEach(requestStreamObserver::onNext);
//        requestStreamObserver.onCompleted();
//        latch.await();

        StreamObserver<GreetRequest> biDirectRequestStreamObserver = asyncClient.stringToUpperCase(new StreamObserver<GreetResponse>() {
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

        createStreamOfGreetingRequest()
                .forEach(g->{
                    System.out.println("send to server: " + g.getGreeting().getFirstName());
                    biDirectRequestStreamObserver.onNext(g);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        biDirectRequestStreamObserver.onCompleted();
        latch.await();




        channel.shutdown();
    }

    public static Stream<GreetRequest> createStreamOfGreetingRequest() {
        return Stream.of("ivan", "peter", "elza", "gregory")
                .map(name -> Greeting.newBuilder()
                        .setFirstName(name)
                        .build())
                .map(g -> GreetRequest.newBuilder()
                        .setGreeting(g)
                        .build());
    }
}
