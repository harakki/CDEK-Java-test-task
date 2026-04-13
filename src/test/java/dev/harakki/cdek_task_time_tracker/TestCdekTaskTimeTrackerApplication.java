package dev.harakki.cdek_task_time_tracker;

import org.springframework.boot.SpringApplication;

public class TestCdekTaskTimeTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.from(CdekTaskTimeTrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
