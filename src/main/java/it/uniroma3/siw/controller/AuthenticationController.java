package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.dto.RegistrationUserDTO;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

/**
 * Controller che gestisce la parte di autenticazione, login e registrazione.
 */
@Controller
public class AuthenticationController {
	
	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	private UserService userService;
	
	/**
     * Mostra la homepage.  
     * Se l'utente è admin, lo reindirizza alla dashboard admin.
     */
	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// Se l'utente non è loggato, mostra la homepage generica
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index.html";
		}
		else {
			// Se l'utente è loggato, controlla il suo ruolo
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			
			// Aggiungi le credenziali al Model
	        model.addAttribute("credentials", credentials);
	        
			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
				return "admin/indexAdmin.html";
			}
		}
        return "index.html";
	}
	
	
	/**
     * Metodo di fallback dopo il login: reindirizza in base al ruolo.
     */
    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {       
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
        model.addAttribute("credentials", credentials);
        
    	// Se l'utente è admin, vai alla dashboard admin
    	if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            return "admin/indexAdmin.html";
        }
    	// Altrimenti vai alla homepage standard
        return "index.html";
    }

    
    /**
     * Mostra la pagina di login.
     */
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}
	
	/**
     * Mostra il form per la registrazione di un nuovo utente.
     */
	@GetMapping(value = "/register")
	public String showRegisterForm(Model model) {
	    model.addAttribute("registrationUserDTO", new RegistrationUserDTO());
	    return "formRegisterUser";
	}
	
	/**
     * Registra un nuovo utente, validando i dati inseriti.
     */
	@PostMapping(value = "/register")
	public String registerUser(@Valid @ModelAttribute("registrationUserDTO") RegistrationUserDTO registrationUserDTO,
	                           BindingResult bindingResult,
	                           Model model) {
	    
	    // Controllo che la password e la conferma password siano uguali
	    if (!registrationUserDTO.getCredentials().getPassword()
	            .equals(registrationUserDTO.getConfirmPassword())) {
	        bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Le password non coincidono");
	    }
	    
	    // Controllo se l'email esiste già
	    if (userService.existsByEmail(registrationUserDTO.getUser().getEmail())) {
	        bindingResult.rejectValue("user.email", "error.user.email", "Questa email è già registrata");
	    }

	    // Controllo se lo username esiste già
	    if (credentialsService.existsByUsername(registrationUserDTO.getCredentials().getUsername())) {
	        bindingResult.rejectValue("credentials.username", "error.credentials.username", "Questo username è già in uso");
	    }

	    // Se ci sono errori, torna alla pagina di registrazione
	    if (bindingResult.hasErrors()) {
	        return "formRegisterUser";
	    }

	    // Salva l'utente e le credenziali
	    Credentials credentials = registrationUserDTO.getCredentials();
	    User user = registrationUserDTO.getUser();
	    credentials.setUser(user);		// Associa l'utente alle credenziali
	    // Salva nel database
	    credentialsService.saveCredentials(credentials);

	    model.addAttribute("user", user);
	    return "registrationSuccessful";
	}
}
