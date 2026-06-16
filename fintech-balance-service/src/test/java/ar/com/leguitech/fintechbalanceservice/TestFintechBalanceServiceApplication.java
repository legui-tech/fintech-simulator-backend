package ar.com.leguitech.fintechbalanceservice;

import org.springframework.boot.SpringApplication;

public class TestFintechBalanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(FintechBalanceServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
