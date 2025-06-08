package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // Mostra la lista di tutti gli autori
    @GetMapping("/authors")
    public String showAuthors(Model model) {
        Iterable<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "authors.html";
    }

    // Visualizza il dettaglio di un autore
    @GetMapping("/author/{id}")
    public String getAuthorById(@PathVariable("id") Long id, Model model) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
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
    public String formSearchAuthors(Model model) {
        model.addAttribute("name", "");
        model.addAttribute("surname", "");
        model.addAttribute("nationality", "");
        model.addAttribute("bornAfter", null);
        model.addAttribute("bornBefore", null);
        return "formSearchAuthors.html";
    }

    // Esegue la ricerca avanzata
    @GetMapping("/foundAuthors")
    public String searchAuthors(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String surname,
                                @RequestParam(required = false) String nationality,
                                @RequestParam(required = false) LocalDate bornAfter,
                                @RequestParam(required = false) LocalDate bornBefore,
                                Model model) {

        // Normalizza input (trim e gestione vuoto)
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }
        if (surname != null) {
            surname = surname.trim();
            if (surname.isEmpty()) {
                surname = null;
            }
        }
        if (nationality != null) {
            nationality = nationality.trim();
            if (nationality.isEmpty()) {
                nationality = null;
            }
        }

        List<Author> authors = authorService.searchAuthors(name, surname, nationality, bornAfter, bornBefore);
        model.addAttribute("authors", authors);
        model.addAttribute("name", name);
        model.addAttribute("surname", surname);
        model.addAttribute("nationality", nationality);
        model.addAttribute("bornAfter", bornAfter);
        model.addAttribute("bornBefore", bornBefore);
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
