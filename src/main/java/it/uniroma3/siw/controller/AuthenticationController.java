package it.uniroma3.siw.controller;

import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
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

	@Autowired
	private BookService bookService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private ReviewService reviewService;

	/**
	 * Mostra la homepage. Se l'utente è admin, lo reindirizza alla dashboard admin.
	 */
	@GetMapping(value = "/")
	public String index(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		// Se l'utente non è loggato, mostra la homepage generica
		if (currentUser == null) {
			return "index";
		} else {
			// Se l'utente è loggato, controlla il suo ruolo
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			// Aggiungi le credenziali al Model
			model.addAttribute("credentials", credentials);

			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {

				int totalBooks = (int) StreamSupport.stream(bookService.getAllBooks().spliterator(), false).count();
				int totalAuthors = (int) StreamSupport.stream(authorService.getAllAuthors().spliterator(), false)
						.count();
				int totalReviews = (int) StreamSupport.stream(reviewService.getAllReviews().spliterator(), false)
						.count();

				model.addAttribute("totalBooks", totalBooks);
				model.addAttribute("totalAuthors", totalAuthors);
				model.addAttribute("totalReviews", totalReviews);
				return "admin/indexAdmin.html";
			}
		}
		return "index.html";
	}

	/**
	 * Metodo di fallback dopo il login: reindirizza in base al ruolo.
	 */
	@GetMapping(value = "/success")
	public String defaultAfterLogin(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		if (currentUser == null) {
			return "index";
		}

		// Se l'utente è loggato, controlla il suo ruolo
		model.addAttribute("currentUser", currentUser);
		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
		// Aggiungi le credenziali al Model
		model.addAttribute("credentials", credentials);

		// Se l'utente è admin, vai alla dashboard admin
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			int totalBooks = (int) StreamSupport.stream(bookService.getAllBooks().spliterator(), false).count();
			int totalAuthors = (int) StreamSupport.stream(authorService.getAllAuthors().spliterator(), false).count();
			int totalReviews = (int) StreamSupport.stream(reviewService.getAllReviews().spliterator(), false).count();

			model.addAttribute("totalBooks", totalBooks);
			model.addAttribute("totalAuthors", totalAuthors);
			model.addAttribute("totalReviews", totalReviews);

			return "admin/indexAdmin.html";
		}
		// Altrimenti vai alla homepage standard
		return "index.html";
	}

	/**
	 * Mostra la pagina di login.
	 */
	@GetMapping(value = "/login")
	public String showLoginForm(Model model) {
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
			BindingResult bindingResult, Model model) {

		// Controllo che la password e la conferma password siano uguali
		if (!registrationUserDTO.getCredentials().getPassword().equals(registrationUserDTO.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "confirmPassword.nomatch");
		}

		// Controllo se l'email esiste già
		if (userService.existsByEmail(registrationUserDTO.getUser().getEmail())) {
			bindingResult.rejectValue("user.email", "user.email.duplicate");
		}

		// Controllo se lo username esiste già
		if (credentialsService.existsByUsername(registrationUserDTO.getCredentials().getUsername())) {
			bindingResult.rejectValue("credentials.username", "credentials.username.duplicate");
		}

		// Se ci sono errori, torna alla pagina di registrazione
		if (bindingResult.hasErrors()) {
			return "formRegisterUser";
		}

		// Salva l'utente e le credenziali
		Credentials credentials = registrationUserDTO.getCredentials();
		User user = registrationUserDTO.getUser();
		credentials.setUser(user); // Associa l'utente alle credenziali
		// Salva nel database
		credentialsService.saveCredentials(credentials);

		model.addAttribute("user", user);
		return "registrationSuccessful";
	}
}
