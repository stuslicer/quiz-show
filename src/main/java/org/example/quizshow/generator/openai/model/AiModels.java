package org.example.quizshow.generator.openai.model;

public enum AiModels {

    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_4_TURBO("gpt-4-turbo"),
    GPT_4("gpt-4"),
    GPT_4O("gpt-4o"),;

    AiModels(String id) {
        this.id = id;
    }

    private String id;

    public String getId() {
        return id;
    }
}
