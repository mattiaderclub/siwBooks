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
    public String showAuthors(Model model) {
        Iterable<Author> authors = authorService.getAllAuthors();
        
        // Calcola numero libri e voto medio per ogni autore
        Map<Long, Integer> authorNumBooks = new HashMap<>();
        Map<Long, Double> authorAverageRatings = new HashMap<>();

        for (Author author : authors) {
            int numBooks = author.getLibri() != null ? author.getLibri().size() : 0;
            Double avgRating = authorService.getAverageRatingOfAuthor(author);

            authorNumBooks.put(author.getId(), numBooks);
            authorAverageRatings.put(author.getId(), avgRating);
        }
        
        model.addAttribute("authors", authors);
        model.addAttribute("authorNumBooks", authorNumBooks);
        model.addAttribute("authorAverageRatings", authorAverageRatings);
        return "authors.html";
    }

    // Visualizza il dettaglio di un autore
    @GetMapping("/author/{id}")
    public String getAuthorById(@PathVariable("id") Long id, Model model) {
        Author author = authorService.getAuthorById(id);
        int numBooks = author.getLibri() != null ? author.getLibri().size() : 0;
        Double avgRating = authorService.getAverageRatingOfAuthor(author);

        model.addAttribute("author", author);
        model.addAttribute("numBooks", numBooks);
        model.addAttribute("averageRating", avgRating);
        return "author.html";
    }

    // Mostra il form per inserire un nuovo autore
    @GetMapping("/admin/formNewAuthor")
    public String formNewAuthor(Model model) {
        model.addAttribute("author", new Author());
        return "admin/formNewAuthor.html";
    }

    // Salva un nuovo autore con eventuale foto
    @PostMapping(value = "/admin/author", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String newAuthor(@Valid @ModelAttribute("author") Author author,
                            BindingResult bindingResult,
                            @RequestParam("photo") MultipartFile photo,
                            Model model) throws IOException {

    	this.authorValidator.validate(author, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/formNewAuthor";
        }

        // Salva la foto se presente
        if (!photo.isEmpty()) {
            Path folder = Paths.get("uploads/images/authors");
            Files.createDirectories(folder);
            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(photo.getOriginalFilename());
            Path target = folder.resolve(filename);
            photo.transferTo(target);
            author.setPhoto("/images/authors/" + filename);
        }

        authorService.saveAuthor(author);
        return "redirect:/author/" + author.getId();
    }

    // Mostra il form per la ricerca avanzata degli autori
    @GetMapping("/formSearchAuthors")
    public String formSearchAuthors(Model model, @AuthenticationPrincipal UserDetails currentUser) {
    	model.addAttribute("filtro", new AuthorSearchDTO());

		if (currentUser != null) {
			Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
			model.addAttribute("credentials", credentials);
		}
        return "formSearchAuthors.html";
    }

    // Esegue la ricerca avanzata
    @GetMapping("/foundAuthors")
    public String searchAuthors(@ModelAttribute("filtro") AuthorSearchDTO filtro, 
                                Model model) {

    	List<Author> authors = authorService.searchAuthors(
                filtro.getName(),
                filtro.getSurname(),
                filtro.getNationality(),
                filtro.getBornBefore(),
                filtro.getBornAfter()
            );
        
        Map<Long, Integer> authorNumBooks = new HashMap<>();
        Map<Long, Double> authorAverageRatings = new HashMap<>();

        for (Author author : authors) {
            int numBooks = author.getLibri() != null ? author.getLibri().size() : 0;
            Double avgRating = authorService.getAverageRatingOfAuthor(author);

            authorNumBooks.put(author.getId(), numBooks);
            authorAverageRatings.put(author.getId(), avgRating);
        }
        model.addAttribute("authors", authors);
        model.addAttribute("authorNumBooks", authorNumBooks);
        model.addAttribute("authorAverageRatings", authorAverageRatings);
        model.addAttribute("filtro", filtro); // per riempire il form
        return "foundAuthors.html";
    }

    // Pagina di index per la gestione admin
    @GetMapping("/admin/indexAuthors")
    public String indexAuthors() {
        return "admin/indexAuthors.html";
    }

    // Mostra la pagina di gestione admin degli autori
    @GetMapping("/admin/manageAuthors")
    public String manageAuthors(Model model) {
        Iterable<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "admin/manageAuthors.html";
    }

    // Elimina un autore
    @GetMapping("/admin/deleteAuthor/{id}")
    public String deleteAuthor(@PathVariable("id") Long id) {
        authorService.deleteById(id);
        return "redirect:/admin/manageAuthors";
    }
}
