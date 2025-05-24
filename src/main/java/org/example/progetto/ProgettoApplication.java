package org.example.progetto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) //è necessario?
@SpringBootApplication
public class ProgettoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgettoApplication.class, args);
    }

}
