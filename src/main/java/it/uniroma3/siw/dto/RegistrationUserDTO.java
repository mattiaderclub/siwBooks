package it.uniroma3.siw.dto;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Oggetto di supporto (DTO) utilizzato durante la registrazione dell'utente.
 * Contiene un oggetto User, le relative credenziali e la conferma della password.

 * Un DTO è un oggetto Java usato per trasportare dati tra il livello di presentazione e il livello di servizio/controllo.

 * In pratica: Il DTO non rappresenta un'entità del database, ma serve solo a raccogliere dati temporanei.
 * È utile quando i dati di input non corrispondono 1 a 1 a un'entità 
 * (ad esempio: conferma password nel form non esiste nella tabella credentials, ma è utile per la logica di registrazione!).
*/
public class RegistrationUserDTO {

	/**
     * Oggetto User da popolare durante la registrazione.
     */
    @Valid
    private User user = new User();

    /**
     * Oggetto Credentials da popolare durante la registrazione.
     */
    @Valid
    private Credentials credentials = new Credentials();

    /**
     * Campo per confermare la password durante la registrazione.
     */
    @NotBlank
    private String confirmPassword;

    // === Getter e Setter ===
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
