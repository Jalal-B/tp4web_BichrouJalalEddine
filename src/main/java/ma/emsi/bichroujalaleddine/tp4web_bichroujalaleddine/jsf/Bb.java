package ma.emsi.bichroujalaleddine.tp4web_bichroujalaleddine.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.bichroujalaleddine.tp2_bichroujalaleddine.llm.LlmClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class Bb implements Serializable {

    // On utilise des codes simples ici
    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    // Map codes->prompts
    private static final Map<String, String> PROMPTS = new HashMap<>();
    static {
        PROMPTS.put("ASSISTANT", "You are a helpful assistant. You help the user to find the information they need. If the user type a question, you answer it.");
        PROMPTS.put("TRADUCTEUR", "You are an interpreter. You translate from English to French and from French to English. If the user type a French text, you translate it into English. If the user type an English text, you translate it into French. If the text contains only one to three words, give some examples of usage of these words in English.");
        PROMPTS.put("GUIDE", "Your are a travel guide. If the user type the name of a country or of a town, you tell them what are the main places to visit in the country or the town and you tell them the average price of a meal.");
    }

    @Inject private FacesContext facesContext;
    @Inject private LlmClient llmClient;

    public Bb() {}

    public String getRoleSysteme() { return roleSysteme; }
    public void setRoleSysteme(String roleSysteme) { this.roleSysteme = roleSysteme; }
    public boolean isRoleSystemeChangeable() { return roleSystemeChangeable; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }
    public String getConversation() { return conversation.toString(); }
    public void setConversation(String conversation) { this.conversation = new StringBuilder(conversation); }

    public String envoyer() {
        if (question == null || question.isBlank()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question"));
            return null;
        }
        try {
            if (this.conversation.isEmpty()) {
                // Utilise le vrai prompt !
                llmClient.setSystemRole(PROMPTS.get(this.roleSysteme));
                this.roleSystemeChangeable = false;
            }
            this.reponse = llmClient.envoyerQuestion(question);
            afficherConversation();
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur LLM", e.getMessage()));
        }
        return null;
    }

    public String nouveauChat() { return "index"; }

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();
            this.listeRolesSysteme.add(new SelectItem("ASSISTANT", "Assistant"));
            this.listeRolesSysteme.add(new SelectItem("TRADUCTEUR", "Traducteur Anglais-Français"));
            this.listeRolesSysteme.add(new SelectItem("GUIDE", "Guide touristique"));
        }
        return this.listeRolesSysteme;
    }
}
