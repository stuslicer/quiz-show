package org.example.quizshow.shell;

import org.jline.utils.AttributedStyle;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jline.utils.AttributedString;

@Configuration
public class ShellConfig {

    @Bean
    public PromptProvider promptProvider() {
        return () -> new AttributedString("quiz:> ", AttributedStyle.BOLD);
    }
}