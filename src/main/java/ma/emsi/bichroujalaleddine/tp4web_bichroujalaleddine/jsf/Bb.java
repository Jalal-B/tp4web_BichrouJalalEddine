package ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.llm.LlmClient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class Bb implements Serializable {

    private String choixPdf = "IA";
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    @Inject private FacesContext facesContext;
    @Inject private LlmClient llmClient;

    public String getChoixPdf() { return choixPdf; }
    public void setChoixPdf(String choixPdf) { this.choixPdf = choixPdf; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; } // CRUCIAL : setter pour JSF
    public String getConversation() { return conversation.toString(); }
    public void setConversation(String conversation) { this.conversation = new StringBuilder(conversation); } // JSF compatible
    public List<SelectItem> getPdfOptions() {
        List<SelectItem> options = new ArrayList<>();
        options.add(new SelectItem("IA", "Support IA/RAG"));
        options.add(new SelectItem("GESTION", "Gestion Commerciale"));
        return options;
    }

    public String envoyer() {
        if (question == null || question.isBlank()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Question vide", null));
            return null;
        }
        try {
            reponse = llmClient.questionAvecRag(question, choixPdf);
            conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur assistant", e.getMessage()));
        }
        return null;
    }

    public String nouveauChat() {
        conversation.setLength(0);
        question = "";
        reponse = "";
        return "index";
    }
}
