package ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class MagasinEmbeddings implements Serializable {

    private EmbeddingStore<TextSegment> storeIA;
    private EmbeddingStore<TextSegment> storeGestion;
    private EmbeddingModel embeddingModel;
    private DocumentParser parser;
    private DocumentSplitter splitter;

    @PostConstruct
    public void init() {
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        parser = new ApacheTikaDocumentParser();
        splitter = DocumentSplitters.recursive(600, 0);

        storeIA = creerStore("/langchain4j.pdf");
        storeGestion = creerStore("/LAB 1 Gestion Commerciale.pdf");
    }

    private EmbeddingStore<TextSegment> creerStore(String cheminRessource) {
        try (InputStream is = getClass().getResourceAsStream(cheminRessource)) {
            if (is != null) {
                Document doc = parser.parse(is);
                List<TextSegment> segments = splitter.split(doc);
                List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
                EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
                store.addAll(embeddings, segments);
                return store;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new InMemoryEmbeddingStore<>();
    }

    public EmbeddingStore<TextSegment> getStoreIA() { return storeIA; }
    public EmbeddingStore<TextSegment> getStoreGestion() { return storeGestion; }
    public EmbeddingModel getEmbeddingModel() { return embeddingModel; }
}
