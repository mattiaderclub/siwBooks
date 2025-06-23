package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import it.uniroma3.siw.controller.validator.BookValidator;
import it.uniroma3.siw.dto.BookSearchDTO;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@Controller
public class BookController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private BookService bookService;

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
        	model.addAttribute("currentUser", currentUser);

            Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
            model.addAttribute("credentials", credentials);
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
	public String manageBooks(Model model) {
		model.addAttribute("books", bookService.getAllBooks());
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
	public String searchBooks(@ModelAttribute("filtro") BookSearchDTO filtro, Model model) {
		List<Book> books = bookService.searchBooks(filtro.getTitle(), filtro.getAnnoMin(), filtro.getAnnoMax(),
				filtro.getMinRating());

		model.addAttribute("books", books);
		model.addAttribute("filtro", filtro);
		return "foundBooks.html";
	}

	// Form per aggiungere autori a un libro
	/*
	 * @GetMapping("/admin/formAddAuthorsToBook/{id}") public String
	 * formAddAuthors(@PathVariable("id") Long id, Model model) { Book book =
	 * bookService.getBookById(id); model.addAttribute("book", book);
	 * model.addAttribute("allAuthors", authorService.getAllAuthors()); return
	 * "admin/formAddAuthorsToBook.html"; }
	 * 
	 * // Salva l'associazione degli autori a un libro
	 * 
	 * @PostMapping("/admin/addAuthorsToBook/{id}") public String
	 * addAuthorsToBook(@PathVariable("id") Long id, @RequestParam Set<Long>
	 * authorIds) { Book book = bookService.getBookById(id); Iterable<Author>
	 * authorsToAdd = authorService.getAuthorsByIds(authorIds);
	 * 
	 * Set<Author> autori = book.getAutori(); for (Author a : authorsToAdd) {
	 * autori.add(a); } bookService.saveBook(book); return "redirect:/book/" + id; }
	 * 
	 * @PostMapping("/admin/removeAuthorFromBook/{bookId}") public String
	 * removeAuthorFromBook(@PathVariable Long bookId, @RequestParam Long authorId)
	 * { Book book = bookService.getBookById(bookId); Author author =
	 * authorService.getAuthorById(authorId);
	 * 
	 * book.getAutori().remove(author);
	 * 
	 * bookService.saveBook(book); return "redirect:/book/" + bookId; }
	 */

}
