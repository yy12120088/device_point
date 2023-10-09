package com.qinglan.example.device_point;

import com.qinglan.example.device_point.server.ChatServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevicePointApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DevicePointApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ChatServer chatServer = new ChatServer();
        chatServer.startQLServer(1060);
    }
}
