package ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.llm;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.llm.Assistant;
import ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.rag.MagasinEmbeddings;
import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {
    @Inject private MagasinEmbeddings magasinEmbeddings;
    private ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

    public String questionAvecRag(String question, String choixPdf) {
        ContentRetriever retriever =
                EmbeddingStoreContentRetriever.builder()
                        .embeddingStore("IA".equals(choixPdf) ? magasinEmbeddings.getStoreIA() : magasinEmbeddings.getStoreGestion())
                        .embeddingModel(magasinEmbeddings.getEmbeddingModel())
                        .maxResults(2).minScore(0.5).build();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder().contentRetriever(retriever).build();

        String apiKey = System.getenv("GEMINIKEY");
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.0-flash-exp")
                .temperature(0.3)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        return assistant.chat(question);
    }
}
