package it.uniroma3.siw.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.dto.UpdateReviewDTO;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private BookService bookService;

	@Autowired
	private CredentialsService credentialsService;

	/**
	 * ---------------------------------------- Aggiungi nuova recensione
	 * ----------------------------------------
	 */
	@GetMapping("/reviews/new/{bookId}")
	@PreAuthorize("isAuthenticated()")
	public String showNewReviewForm(@PathVariable Long bookId, @AuthenticationPrincipal UserDetails currentUser,
			Model model) {

		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
		User user = credentials.getUser();
		Book book = bookService.getBookById(bookId);

		if (reviewService.existsByUserAndBook(user, book)) {
			return "redirect:/book/" + bookId;
		}

		UpdateReviewDTO dto = new UpdateReviewDTO();
		dto.setBookId(bookId);
		dto.setRating(1); // valore minimo iniziale

		model.addAttribute("reviewDto", dto);
		model.addAttribute("formAction", "/reviews/new/" + bookId);
		model.addAttribute("formTitle", "Scrivi una recensione");

		return "cliente/formNewReview.html";
	}

	@PostMapping("/reviews/new/{bookId}")
	@PreAuthorize("isAuthenticated()")
	public String saveNewReview(@PathVariable Long bookId, @Valid @ModelAttribute("reviewDto") UpdateReviewDTO dto,
			BindingResult bindingResult, @AuthenticationPrincipal UserDetails currentUser, Model model) {

		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
		User user = credentials.getUser();
		Book book = bookService.getBookById(bookId);

		if (reviewService.existsByUserAndBook(user, book)) {
			return "redirect:/book/" + bookId;
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("reviewDto", dto);
			model.addAttribute("formAction", "/reviews/new/" + bookId);
			model.addAttribute("formTitle", "Scrivi una recensione");
			return "cliente/formNewReview.html";
		}

		Review review = new Review();
		review.setTitle(dto.getTitle());
		review.setText(dto.getText());
		review.setRating(dto.getRating());
		review.setLibro(book);
		review.setUser(user);

		reviewService.saveReview(review);
		return "redirect:/book/" + bookId;
	}

	/**
	 * ---------------------------------------- Modifica recensione
	 * ----------------------------------------
	 */
	@GetMapping("/reviews/edit/{id}")
	@PreAuthorize("isAuthenticated()")
	public String showEditReviewForm(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser,
			Model model) {

		Review review = reviewService.getReviewById(id);
		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());

		if (review == null || !review.getUser().getId().equals(credentials.getUser().getId())) {
			return "redirect:/";
		}

		UpdateReviewDTO dto = new UpdateReviewDTO();
		dto.setId(review.getId());
		dto.setTitle(review.getTitle());
		dto.setText(review.getText());
		dto.setRating(review.getRating());
		dto.setBookId(review.getLibro().getId());

		model.addAttribute("reviewDto", dto);
		model.addAttribute("formAction", "/reviews/edit/" + id);
		model.addAttribute("formTitle", "Modifica la recensione");

		return "cliente/formEditReview.html";
	}

	@PostMapping("/reviews/edit/{id}")
	@PreAuthorize("isAuthenticated()")
	public String updateReview(@PathVariable Long id, @Valid @ModelAttribute("reviewDto") UpdateReviewDTO dto,
			BindingResult bindingResult, @AuthenticationPrincipal UserDetails currentUser, Model model) {

		Review existingReview = reviewService.getReviewById(id);
		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());

		if (existingReview == null || !existingReview.getUser().getId().equals(credentials.getUser().getId())) {
			return "redirect:/";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("reviewDto", dto);
			model.addAttribute("formAction", "/reviews/edit/" + id);
			model.addAttribute("formTitle", "Modifica la recensione");
			return "cliente/formEditReview.html";
		}

		existingReview.setTitle(dto.getTitle());
		existingReview.setText(dto.getText());
		existingReview.setRating(dto.getRating());

		reviewService.saveReview(existingReview);
		return "redirect:/book/" + existingReview.getLibro().getId();
	}

	/**
	 * ---------------------------------------- Elimina recensione - Utente o Admin
	 * ----------------------------------------
	 */
	@PostMapping("/review/delete/{id}")
	@PreAuthorize("isAuthenticated()")
	public String deleteReview(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {

		Review review = reviewService.getReviewById(id);
		if (review == null)
			return "redirect:/";

		Long bookId = review.getLibro().getId();

		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());

		// Se admin, elimina sempre. Se user, solo se è il proprietario
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)
				|| review.getUser().getId().equals(credentials.getUser().getId())) {
			reviewService.deleteById(id);
		}

		return "redirect:/book/" + bookId;
	}

	@GetMapping("/cliente/recensioni")
	@PreAuthorize("hasAuthority('DEFAULT')")
	public String getUserReviewPage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
		Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
		User user = credentials.getUser();

		List<Book> libriRecensiti = bookService.getBooksReviewedByUser(user);
		List<Book> libriNonRecensiti = bookService.getBooksNotReviewedByUser(user);

		// Costruisci una mappa libro -> recensione dell'utente
		Map<Book, Review> recensioniMie = new LinkedHashMap<>();
		for (Book libro : libriRecensiti) {
			for (Review r : libro.getRecensioni()) {
				if (r.getUser().equals(user)) {
					recensioniMie.put(libro, r);
					break; // uno per libro
				}
			}
		}

		model.addAttribute("recensioniMie", recensioniMie);
		model.addAttribute("libriNonRecensiti", libriNonRecensiti);
		model.addAttribute("credentials", credentials);
		model.addAttribute("currentUser", currentUser);

		return "cliente/recensioni.html";
	}

	@GetMapping("/admin/recensioni")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String getAllReviewsGroupedByBook(Model model) {
		Iterable<Book> books = bookService.getAllBooks();

		// Forza il caricamento delle recensioni se sono LAZY
		for (Book book : books) {
			book.getRecensioni().size(); // inizializza la lista
		}

		model.addAttribute("books", books);
		return "admin/recensioni.html";
	}
}
