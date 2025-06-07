package it.uniroma3.siw.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * ControllerAdvice globale che fornisce un attributo comune (userDetails) a tutte le pagine Thymeleaf.
 * Serve per mostrare i dati dell'utente loggato in header/footer o per personalizzare la UI.
 */
@ControllerAdvice
public class GlobalController {
	
	/**
     * Metodo che fornisce l'oggetto UserDetails (utente loggato) a tutte le view.
     * Viene eseguito prima di ogni renderizzazione delle pagine.
     *
     * @return UserDetails dell'utente corrente se autenticato, altrimenti null
     */
    @ModelAttribute("userDetails")

    public UserDetails getUser() {
        UserDetails user = null;

        // Recupera l'autenticazione corrente dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Se l'utente non è "Anonymous", ottieni il principal (UserDetails)
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return user;
    }
}