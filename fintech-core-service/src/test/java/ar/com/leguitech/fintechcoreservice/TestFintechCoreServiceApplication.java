package ar.com.leguitech.fintechcoreservice;

import org.springframework.boot.SpringApplication;

public class TestFintechCoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(FintechCoreServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
