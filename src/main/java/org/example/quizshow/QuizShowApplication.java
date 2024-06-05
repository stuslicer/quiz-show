package org.example.quizshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.EnableCommand;
import org.springframework.shell.standard.ShellComponent;

@SpringBootApplication
@ShellComponent
@CommandScan
public class QuizShowApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizShowApplication.class, args);
    }

}
