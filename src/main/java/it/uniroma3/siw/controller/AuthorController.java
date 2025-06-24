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

	// Mostra la lista di tutti gli autori
	@GetMapping("/authors")
	public String showAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		Iterable<Author> authors = authorService.getAllAuthors();

		Map<Long, Double> authorAverageRatings = new HashMap<>();

		for (Author author : authors) {
			Double avgRating = authorService.getAverageRatingOfAuthor(author);
			authorAverageRatings.put(author.getId(), avgRating);
		}

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		model.addAttribute("authors", authors);
		model.addAttribute("authorAverageRatings", authorAverageRatings);
		return "authors.html";
	}

	// Visualizza il dettaglio di un autore
	@GetMapping("/author/{id}")
	public String getAuthorDetails(@PathVariable("id") Long id, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {
		Author author = authorService.getAuthorById(id);
		Double avgRating = authorService.getAverageRatingOfAuthor(author);
		model.addAttribute("author", author);
		model.addAttribute("averageRating", avgRating);

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "author.html";
	}

	// Mostra il form per inserire un nuovo autore
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

	// Salva un nuovo autore con eventuale foto
	@PostMapping(value = "/admin/author", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String saveNewAuthor(@Valid @ModelAttribute("author") Author author, BindingResult bindingResult,
			@RequestParam("photoFile") MultipartFile photoFile, Model model) throws IOException {

		authorValidator.validate(author, bindingResult);
		if (bindingResult.hasErrors()) {
			System.out.println("ERRORI NEL FORM: " + bindingResult.getAllErrors());
			return "admin/formNewAuthor";
		}

		if (photoFile != null && !photoFile.isEmpty()) {
			Path folder = Paths.get("uploads/images/authors");
			Files.createDirectories(folder);
			String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(photoFile.getOriginalFilename());
			Path target = folder.resolve(filename);
			photoFile.transferTo(target);
			author.setPhoto("/images/authors/" + filename);
		}

		System.out.println("SALVO AUTORE: " + author.getName());
		authorService.saveAuthor(author);
		return "redirect:/author/" + author.getId();
	}

	// Elimina un autore
	@GetMapping("/admin/deleteAuthor/{id}")
	public String deleteAuthor(@PathVariable("id") Long id) {
		authorService.deleteById(id);
		return "redirect:/admin/manageAuthors";
	}

	@GetMapping("/admin/manageAuthors")
	public String manageAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("authors", authorService.getAllAuthors());
		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/manageAuthors.html";
	}

	@GetMapping("/admin/indexAuthors")
	public String indexAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/indexAuthors.html";
	}

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
}
