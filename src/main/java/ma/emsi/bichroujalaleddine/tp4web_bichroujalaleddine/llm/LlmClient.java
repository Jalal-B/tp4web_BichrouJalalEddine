package ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;
import ma.emsi.bichroujalaleddine.tp2_bichroujalaleddine.jsf.Assistant;

import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {
    private static final long serialVersionUID = 1L;
    private String systemRole;
    private Assistant assistant;
    private ChatMemory chatMemory;

    public LlmClient() {
        String apiKey = System.getenv("GEMINIKEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("La clé Gemini n'est pas définie dans la variable d'environnement GEMINIKEY");
        }
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .build();
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        this.chatMemory.clear();
        this.chatMemory.add(SystemMessage.from(systemRole));
    }

    public String envoyerQuestion(String question) {
        return assistant.chat(question);
    }
}
