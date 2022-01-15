package com.ld3l.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new GreetServerImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutdown");
            server.shutdown();
        }));


        server.awaitTermination();
    }

}
