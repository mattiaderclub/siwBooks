package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.AuthorValidator;
import it.uniroma3.siw.dto.AuthorSearchDTO;
import it.uniroma3.siw.dto.UpdateAuthorDTO;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private AuthorValidator authorValidator;

	/*
	 * Mostra la lista di tutti gli autori
	 */
	@GetMapping("/authors")
	public String showAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		Iterable<Author> authors = authorService.getAllAuthors();	// Recupero tutti gli autori

		Map<Long, Double> authorAverageRatings = new HashMap<>();	// Mappa che collega gli autori con la loro valutazione media

		/* 
		 * Riempio la mappa con i valori, chiave -> id autore, valore -> rating medio
		 */
		for (Author author : authors) {
			Double avgRating = authorService.getAverageRatingOfAuthor(author);	// Recupero avgRating dell'autore
			authorAverageRatings.put(author.getId(), avgRating);
		}

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());	// Recupero credenziali
			model.addAttribute("credentials", credentials);
		}

		// Aggiungo cose alla vista html
		model.addAttribute("authors", authors);
		model.addAttribute("authorAverageRatings", authorAverageRatings);
		return "authors.html";
	}

	/*
	 * Mostra i dettagli di un autore
	 */
	@GetMapping("/author/{id}")
	public String getAuthorDetails(@PathVariable("id") Long id, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {
		Author author = authorService.getAuthorById(id);					// Recupera autore
		Double avgRating = authorService.getAverageRatingOfAuthor(author);	// Recupera rating medio autore
		model.addAttribute("author", author);
		model.addAttribute("averageRating", avgRating);

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "author.html";
	}

	/*
	 * Mostra form per inserire un nuovo autore
	 */
	@GetMapping("/admin/formNewAuthor")
	public String formNewAuthor(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("author", new Author());
		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/formNewAuthor.html";
	}

	/*
	 * Salva autore con eventuale foto
	 */
	@PostMapping(value = "/admin/author", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String saveNewAuthor(@Valid @ModelAttribute("author") Author author, BindingResult bindingResult,
			@RequestParam("photoFile") MultipartFile photoFile, Model model) throws IOException {

		authorValidator.validate(author, bindingResult);	// Uso validator per non inserire autore già presente
		if (bindingResult.hasErrors()) {					// Se binding ritorno alla pagina del form
			return "admin/formNewAuthor";
		}

		// Gestione upload immagine: se presente, salva il file su disco e imposta il path nell'autore
		if (photoFile != null && !photoFile.isEmpty()) {
			Path folder = Paths.get("uploads/images/authors");		// cartella di destinazione
			Files.createDirectories(folder);						// crea cartella se assente
			
			
			String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(photoFile.getOriginalFilename());
			Path target = folder.resolve(filename);
			photoFile.transferTo(target);
			
			// Imposta il path "pubblico" (relativo al server) nell'autore
			author.setPhoto("/images/authors/" + filename);
		}

		authorService.saveAuthor(author);				// Salva autore
		return "redirect:/author/" + author.getId();	// Reindirizza alla pagina dell'autore appena creato
	}

	/**
	 * Elimina un autore dato il suo ID.
	 */
	@GetMapping("/admin/deleteAuthor/{id}")
	public String deleteAuthor(@PathVariable("id") Long id) {
		authorService.deleteById(id);
		return "redirect:/admin/manageAuthors";
	}

	/**
	 * Mostra la pagina di gestione degli autori per l’amministratore.
	 */
	@GetMapping("/admin/manageAuthors")
	public String manageAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("authors", authorService.getAllAuthors());
		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/manageAuthors.html";
	}

	/**
	 * Pagina indice della sezione amministratore per autori.
	 */
	@GetMapping("/admin/indexAuthors")
	public String indexAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/indexAuthors.html";
	}

	/**
	 * Mostra il form per effettuare una ricerca filtrata tra gli autori.
	 */
	@GetMapping("/formSearchAuthors")
	public String formSearchAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("filtro", new AuthorSearchDTO());
		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "formSearchAuthors.html";
	}

	/**
	 * Elabora la ricerca degli autori in base ai filtri impostati nel DTO.
	 */
	@GetMapping("/foundAuthors")
	public String searchAuthors(@ModelAttribute("filtro") AuthorSearchDTO filtro, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {
		List<Author> authors = authorService.searchAuthors(filtro.getName(), filtro.getSurname(),
				filtro.getNationality());

		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		model.addAttribute("authors", authors);
		model.addAttribute("filtro", filtro);
		return "foundAuthors.html";
	}

	/**
	 * Mostra il form di modifica per un autore esistente, precompilando i dati.
	 */
	@GetMapping("/admin/formUpdateAuthor/{id}")
	public String formUpdateAuthor(@PathVariable("id") Long id, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {
		Author author = authorService.getAuthorById(id);
		if (author == null)
			return "redirect:/admin/manageAuthors";		// Se l'autore non esiste, reindirizza alla pagina di gestione

		UpdateAuthorDTO dto = new UpdateAuthorDTO();	// Inizializza il DTO di aggiornamento con i dati esistenti
		dto.setId(author.getId());
		dto.setName(author.getName());
		dto.setSurname(author.getSurname());
		dto.setBirthDate(author.getBirthDate());
		dto.setDeathDate(author.getDeathDate());
		dto.setNationality(author.getNationality());
		dto.setPhotoPath(author.getPhoto());

		// Aggiunge al model sia il DTO che l'entità originale (utile nella view per debug o dettagli)
		model.addAttribute("authorUpdateDto", dto);
		model.addAttribute("author", author);

		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		return "admin/formUpdateAuthor";
	}

	/**
	 * Aggiorna un autore esistente, gestendo immagine e validazione duplicati.
	 */
	@PostMapping(value = "/admin/author/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String updateAuthor(@Valid @ModelAttribute("authorUpdateDto") UpdateAuthorDTO dto,
			BindingResult bindingResult, @RequestParam(value = "photo", required = false) MultipartFile photoFile,
			Model model) throws IOException {

		Author existing = authorService.getAuthorById(dto.getId());
		if (existing == null)
			return "redirect:/admin/manageAuthors";		// Se non esiste (ad esempio ID manipolato), reindirizza alla gestione autori

		// Verifica se i dati identificativi sono cambiati -> aggiro validator per questione id
		// Se uno dei tre è cambiato, verifica che non esista già un autore duplicato
		if (!existing.getName().equals(dto.getName()) || !existing.getSurname().equals(dto.getSurname())
				|| !existing.getBirthDate().equals(dto.getBirthDate())) {

			if (authorService.existsByNameAndSurnameAndBirthDate(dto.getName(), dto.getSurname(), dto.getBirthDate())) {
				bindingResult.reject("author.duplicate");
			}
		}

		// Se ci sono errori, torna alla view col form, mantenendo l'autore originale
		if (bindingResult.hasErrors()) {
			model.addAttribute("author", existing);
			return "admin/formUpdateAuthor";
		}

		// Aggiorna i campi base
		existing.setName(dto.getName());
		existing.setSurname(dto.getSurname());
		existing.setBirthDate(dto.getBirthDate());
		existing.setDeathDate(dto.getDeathDate());
		existing.setNationality(dto.getNationality());

		// Prepara la cartella per le immagini se serve
		Path folder = Paths.get("uploads/images/authors");
		Files.createDirectories(folder);

		// Se l'utente ha richiesto la cancellazione della foto esistente
		if (dto.isDeletePhoto() && existing.getPhoto() != null) {
			String filename = existing.getPhoto().substring(existing.getPhoto().lastIndexOf('/') + 1);
			Path fileOnDisk = folder.resolve(filename);
			try {
				Files.deleteIfExists(fileOnDisk);
			} catch (IOException e) {
				System.err.println("Non ho potuto cancellare il file: " + fileOnDisk);
			}
			// Rimuove il path della foto dall'autore
			existing.setPhoto(null);
		}

		// Se è stata caricata una nuova immagine, la salva e aggiorna il path
		if (photoFile != null && !photoFile.isEmpty()) {
			String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(photoFile.getOriginalFilename());
			Path target = folder.resolve(filename);
			photoFile.transferTo(target);
			existing.setPhoto("/images/authors/" + filename);
		}

		authorService.saveAuthor(existing);				// Salva le modifiche nel database
		return "redirect:/author/" + existing.getId();
	}
}
