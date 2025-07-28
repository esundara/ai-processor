package com.ai.processor.service;

import com.ai.processor.dto.SummaryDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Service
public class AISummarizationService {

    private ChatClient chatClient;
    private String modelName;
    private String apiKey;
    private Double modelTemperature;
    ChatModel myChatModel;
    private static final Logger logger = LoggerFactory.getLogger(AISummarizationService.class);


    public AISummarizationService(ChatClient.Builder chatClientBuilder, @Value("${spring.ai.openai.api-key}") String apiKey,
                                      @Value("${spring.ai.openai.chat.options.model}") String model, @Value("${app.temperature}") String temperature) {
        this.modelName = model;
        logger.debug("Model Name used is "+ model);
        this.apiKey = apiKey;
        this.modelTemperature = Double.valueOf(temperature);
        logger.debug("Model temperature used is "+ modelTemperature);
        this.chatClient = chatClientBuilder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(modelTemperature)
                        .build())
                .build();

    }


    public SummaryDTO getSummary(String text, int maxWords) {

        logger.debug("Creating Prompt Template");
        PromptTemplate promptTemplate = new PromptTemplate(
                "Summarize the following text in approximately {maxWords} words, focusing on the main points:\n\n{text}");
        logger.debug("Prompt Template Created");
        // Create a Prompt with the text and maxWords

        logger.debug("Creating Prompt");
        Prompt prompt = promptTemplate.create(Map.of("maxWords", maxWords, "text", text));
        logger.debug("Prompt  Created");
        logger.debug("Extracted Prompt Text: " + prompt.getContents());

        // Call the OpenAI API and get the content of the response

        logger.debug("Calling Open AI");
        String summary = chatClient.prompt(prompt).call().content();
        logger.debug("Response is  "+ summary);
        SummaryDTO sDTO = new SummaryDTO();
        sDTO.setSummary(summary);

        return sDTO;
    }
}