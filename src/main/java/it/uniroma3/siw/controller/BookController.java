package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

import it.uniroma3.siw.controller.validator.BookValidator;
import it.uniroma3.siw.dto.BookSearchDTO;
import it.uniroma3.siw.dto.UpdateBookDTO;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@Controller
public class BookController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private BookService bookService;

	@Autowired
	private AuthorService authorService;

	@Autowired
	private BookValidator bookValidator;

	@Autowired
	private ReviewService reviewService;

	// Mostra la lista dei libri
	@GetMapping("/books")
	public String showBooks(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		Iterable<Book> books = this.bookService.getAllBooks();

		Map<Long, Double> bookAverageRatings = new HashMap<>();

		for (Book book : books) {
			bookAverageRatings.put(book.getId(), reviewService.getAverageRatingForBook(book));
		}

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);

			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		model.addAttribute("books", books);
		model.addAttribute("bookAverageRatings", bookAverageRatings);
		return "books.html";
	}

	// Visualizza il dettaglio di un libro
	@GetMapping("/book/{id}")
	public String getBookDetails(@PathVariable("id") Long id, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {

		Book book = bookService.getBookById(id);
		model.addAttribute("book", book);
		model.addAttribute("averageRating", bookService.getAverageRating(book));

		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			User user = credentials.getUser();

			model.addAttribute("credentials", credentials);
			model.addAttribute("currentUser", currentUser);

			Review myReview = reviewService.findByUserAndBook(user, book); // da aggiungere al service

			model.addAttribute("myReview", myReview);

			if (myReview != null) {
				model.addAttribute("reviewForm", myReview); // per modifica
			} else {
				Review reviewForm = new Review();
				reviewForm.setRating(1);
				model.addAttribute("reviewForm", reviewForm);
			}
		}

		return "book.html";
	}

	@GetMapping("/admin/formNewBook")
	public String formNewBook(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("book", new Book());

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/formNewBook.html";
	}

	// Salva un nuovo libro con immagini
	@PostMapping(value = "/admin/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String saveNewBook(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult,
			@RequestParam("images") List<MultipartFile> images, Model model) throws IOException {

		bookValidator.validate(book, bindingResult);
		if (bindingResult.hasErrors()) {
			return "admin/formNewBook";
		}

		// Salva immagini in "uploads/images/books"
		List<String> savedPaths = new ArrayList<>();
		Path folder = Paths.get("uploads/images/books");
		Files.createDirectories(folder);

		for (MultipartFile file : images) {
			if (!file.isEmpty()) {
				String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
				Path target = folder.resolve(filename);
				file.transferTo(target);
				savedPaths.add("/images/books/" + filename);
			}
		}

		book.setImagePaths(savedPaths);
		bookService.saveBook(book);
		return "redirect:/book/" + book.getId();
	}

	@GetMapping("/admin/indexBooks")
	public String indexBooks(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/indexBooks.html";
	}

	// Mostra la pagina di gestione admin
	@GetMapping("/admin/manageBooks")
	public String manageBooks(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("books", bookService.getAllBooks());
		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "admin/manageBooks.html";
	}

	// Elimina un libro
	@GetMapping("/admin/deleteBook/{id}")
	public String deleteBook(@PathVariable("id") Long id) {
		bookService.deleteById(id);
		return "redirect:/admin/manageBooks";
	}

	@GetMapping("/formSearchBooks")
	public String formSearchBooks(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("filtro", new BookSearchDTO());

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
		return "formSearchBooks.html";
	}

	@GetMapping("/foundBooks")
	public String searchBooks(@ModelAttribute("filtro") BookSearchDTO filtro, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {
		List<Book> books = bookService.searchBooks(filtro.getTitle(), filtro.getAnnoMin(), filtro.getAnnoMax(),
				filtro.getMinRating());

		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		model.addAttribute("books", books);
		model.addAttribute("filtro", filtro);
		return "foundBooks.html";
	}

	@GetMapping("/admin/book/{id}/manageBookAuthors")
	public String manageBookAuthors(@PathVariable("id") Long id, @RequestParam(required = false) Boolean authorized,
			Model model, @AuthenticationPrincipal UserDetails currentUser) {

		Book book = bookService.getBookById(id);
		if (book == null) {
			return "redirect:/admin/manageBooks";
		}

		List<Author> allAuthors = StreamSupport.stream(authorService.getAllAuthors().spliterator(), false).toList();

		Set<Author> bookAuthors = book.getAutori();

		List<Author> availableAuthors = allAuthors.stream().filter(a -> !bookAuthors.contains(a)).toList();

		model.addAttribute("book", book);
		model.addAttribute("bookAuthors", bookAuthors);
		model.addAttribute("availableAuthors", availableAuthors);

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		return "admin/manageBookAuthors.html";
	}

	@PostMapping("/admin/book/{bookId}/addAuthor/{authorId}")
	public String addAuthorToBook(@PathVariable Long bookId, @PathVariable Long authorId) {
		Book book = bookService.getBookById(bookId);
		Author author = authorService.getAuthorById(authorId);

		book.getAutori().add(author);
		author.getLibri().add(book); // aggiorna anche l’autore

		bookService.saveBook(book); // salva solo uno dei due: Hibernate aggiornerà l’altro

		return "redirect:/admin/book/" + bookId + "/manageBookAuthors";
	}

	@PostMapping("/admin/book/{bookId}/removeAuthor/{authorId}")
	public String removeAuthorFromBook(@PathVariable Long bookId, @PathVariable Long authorId) {
		Book book = bookService.getBookById(bookId);
		Author author = authorService.getAuthorById(authorId);

		book.getAutori().remove(author);
		author.getLibri().remove(book); // aggiorna anche l’autore

		bookService.saveBook(book);

		return "redirect:/admin/book/" + bookId + "/manageBookAuthors";
	}

	@GetMapping("/admin/formUpdateBook/{id}")
	public String formUpdateBook(@PathVariable("id") Long id, Model model,
			@AuthenticationPrincipal UserDetails currentUser) {
		Book book = bookService.getBookById(id);
		if (book == null)
			return "redirect:/admin/manageBooks";

		UpdateBookDTO dto = new UpdateBookDTO();
		dto.setId(book.getId());
		dto.setTitle(book.getTitle());
		dto.setAnnoPubblicazione(book.getAnnoPubblicazione());
		dto.setImagePaths(book.getImagePaths());

		model.addAttribute("bookUpdateDto", dto);
		model.addAttribute("book", book);

		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}

		return "admin/formUpdateBook";
	}

	@PostMapping(value = "/admin/book/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String updateBook(@Valid @ModelAttribute("bookUpdateDto") UpdateBookDTO dto, BindingResult bindingResult,
			@RequestParam(value = "images", required = false) List<MultipartFile> images, Model model)
			throws IOException {

		Book existing = bookService.getBookById(dto.getId());
		if (existing == null)
			return "redirect:/admin/manageBooks";

		// Custom validator per aggiornamento: evita falso duplicato
		if (!existing.getTitle().equals(dto.getTitle())
				|| !existing.getAnnoPubblicazione().equals(dto.getAnnoPubblicazione())) {
			if (bookService.existsByTitleAndAnnoPubblicazione(dto.getTitle(), dto.getAnnoPubblicazione())) {
				bindingResult.reject("book.duplicate");
			}
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("book", existing);
			return "admin/formUpdateBook";
		}

		// === aggiorna titolo e anno ===
		existing.setTitle(dto.getTitle());
		existing.setAnnoPubblicazione(dto.getAnnoPubblicazione());

		// === gestione immagini ===
		Path folder = Paths.get("uploads/images/books");
		Files.createDirectories(folder);

		List<String> savedPaths = new ArrayList<>(
				existing.getImagePaths() != null ? existing.getImagePaths() : List.of());

		// Ordina immagini
		if (dto.getImageOrder() != null && dto.getImageOrder().length > 0) {
			List<String> reordered = new ArrayList<>();
			for (String path : dto.getImageOrder()) {
				if (savedPaths.contains(path))
					reordered.add(path);
			}
			savedPaths = reordered;
		}

		// Rimuove immagini selezionate
		if (dto.getImagesToDelete() != null && !dto.getImagesToDelete().isEmpty()) {
			for (String pathToDelete : dto.getImagesToDelete()) {
				savedPaths.remove(pathToDelete);

				String filename = pathToDelete.substring(pathToDelete.lastIndexOf('/') + 1);
				Path fileOnDisk = folder.resolve(filename);
				try {
					Files.deleteIfExists(fileOnDisk);
				} catch (IOException e) {
					System.err.println("Non ho potuto cancellare il file: " + fileOnDisk);
				}
			}
		}

		// Aggiunge nuove immagini
		if (images != null) {
			for (MultipartFile file : images) {
				if (!file.isEmpty()) {
					String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
					Path target = folder.resolve(filename);
					file.transferTo(target);
					savedPaths.add("/images/books/" + filename);
				}
			}
		}

		existing.setImagePaths(savedPaths);
		bookService.saveBook(existing);

		return "redirect:/book/" + existing.getId();
	}

}
