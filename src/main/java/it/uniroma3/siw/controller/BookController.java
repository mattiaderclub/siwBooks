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
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
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
	public String showBooks(Model model) {
		Iterable<Book> books = this.bookService.getAllBooks();

		Map<Long, Double> bookAverageRatings = new HashMap<>();

		for (Book book : books) {
			bookAverageRatings.put(book.getId(), reviewService.getAverageRatingForBook(book));
		}

		model.addAttribute("books", books);
		model.addAttribute("bookAverageRatings", bookAverageRatings);
		return "books.html";
	}

	// Visualizza il dettaglio di un libro
	@GetMapping("/book/{id}")
	public String getBookById(@PathVariable("id") Long id, Model model) {
		Book book = bookService.getBookById(id);
		// 1) Media delle valutazioni, tramite query JPQL o stream interno al service
		Double averageRating = reviewService.getAverageRatingForBook(book);

		model.addAttribute("book", book);
		model.addAttribute("averageRating", averageRating);
		return "book.html";
	}

	// Mostra il form per inserire un nuovo libro
	@GetMapping("/admin/formNewLibro")
	public String formNewLibro(Model model) {
		model.addAttribute("book", new Book());
		return "admin/formNewLibro.html";
	}

	// Salva un nuovo libro con immagini
	@PostMapping(value = "/admin/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String newBook(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult,
			@RequestParam("images") List<MultipartFile> images, Model model) throws IOException {

		this.bookValidator.validate(book, bindingResult);
		if (bindingResult.hasErrors()) {
			return "admin/formNewLibro";
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

	// Form per aggiungere autori a un libro
	@GetMapping("/admin/formAddAuthorsToBook/{id}")
	public String formAddAuthors(@PathVariable("id") Long id, Model model) {
		Book book = bookService.getBookById(id);
		model.addAttribute("book", book);
		model.addAttribute("allAuthors", authorService.getAllAuthors());
		return "admin/formAddAuthorsToBook.html";
	}

	// Salva l'associazione degli autori a un libro
	@PostMapping("/admin/addAuthorsToBook/{id}")
	public String addAuthorsToBook(@PathVariable("id") Long id, @RequestParam Set<Long> authorIds) {
		Book book = bookService.getBookById(id);
		Iterable<Author> authorsToAdd = authorService.getAuthorsByIds(authorIds);

		Set<Author> autori = book.getAutori();
		for (Author a : authorsToAdd) {
			autori.add(a);
		}
		bookService.saveBook(book);
		return "redirect:/book/" + id;
	}

	@PostMapping("/admin/removeAuthorFromBook/{bookId}")
	public String removeAuthorFromBook(@PathVariable Long bookId, @RequestParam Long authorId) {
		Book book = bookService.getBookById(bookId);
		Author author = authorService.getAuthorById(authorId);

		book.getAutori().remove(author);

		bookService.saveBook(book);
		return "redirect:/book/" + bookId;
	}

	// Mostra il form per la ricerca avanzata dei libri
	@GetMapping("/formSearchLibri")
	public String formSearchLibri(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		model.addAttribute("filtro", new BookSearchDTO());

		if (currentUser != null) {
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("credentials", credentialsService.getCredentials(currentUser.getUsername()));
		}

		return "formSearchLibri.html";
	}

	@GetMapping("/admin/indexBooks")
	public String indexBooks(Model model, @AuthenticationPrincipal UserDetails currentUser) {

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("credentials", credentialsService.getCredentials(currentUser.getUsername()));
		return "admin/indexBooks.html";
	}

	@GetMapping("/foundLibri")
	public String searchLibri(@ModelAttribute("filtro") BookSearchDTO filtro, Model model) {

		List<Book> books = bookService.searchBooks(filtro.getTitle(), filtro.getAnnoMin(), filtro.getAnnoMax(),
				filtro.getMinRating());

		Map<Long, Double> bookAverageRatings = bookService.mediaRecensioniPerLibro(books);

		model.addAttribute("books", books);
		model.addAttribute("bookAverageRatings", bookAverageRatings);
		model.addAttribute("filtro", filtro); // per riempire il form

		return "foundLibri.html";
	}

	// Mostra la pagina di gestione admin
	@GetMapping("/admin/manageBooks")
	public String manageBooks(Model model) {
		Iterable<Book> books = bookService.getAllBooks();
		model.addAttribute("books", books);
		return "admin/manageBooks.html";
	}

	// Elimina un libro
	@GetMapping("/admin/deleteBook/{id}")
	public String deleteBook(@PathVariable("id") Long id) {
		bookService.deleteById(id);
		return "redirect:/admin/manageBooks";
	}
}
