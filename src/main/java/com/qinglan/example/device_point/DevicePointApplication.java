package com.qinglan.example.device_point;

import com.qinglan.example.device_point.server.QlIotServer;
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
        new Thread(() -> {
            new QlIotServer(). startQLServer(1060);
        }).start();
    }
}
