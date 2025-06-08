package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    
    // Form per nuova recensione
    @GetMapping("/reviews/new/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public String newReviewForm(@PathVariable Long bookId, Model model) {
        Review review = new Review();
        Book book = bookService.getBookById(bookId);

        model.addAttribute("review", review);
        model.addAttribute("book", book);

        return "review/formNewReview.html";  // il tuo form per nuova recensione
    }
    
    @GetMapping("/reviews/new/{bookId}")
    public String newReviewForm(@PathVariable Long bookId,
                                @AuthenticationPrincipal UserDetails currentUser,
                                Model model) {

        // Recupera l’utente loggato
        Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
        User user = credentials.getUser();

        // Verifica se ha già recensito questo libro
        Book book = bookService.getBookById(bookId);
        if (reviewService.existsByUserAndBook(user, book)) {
            // Se già recensito, reindirizza alla pagina del libro
            return "redirect:/book/" + bookId;
        }

        model.addAttribute("review", new Review());
        model.addAttribute("book", book);
        return "review/formNewReview.html";
    }

    // Salva nuova recensione
    @PostMapping("/reviews/{bookId}")
    public String saveReview(@PathVariable Long bookId,
                              @Valid @ModelAttribute("review") Review review,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails currentUser,
                              Model model) {

        Book book = bookService.getBookById(bookId);
        Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
        User user = credentials.getUser();

        // Controllo duplicato (anche qui per sicurezza) se si vuole fare POST a mano
        if (reviewService.existsByUserAndBook(user, book)) {
            bindingResult.rejectValue("title", "duplicate", "Hai già recensito questo libro.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            return "review/formNewReview.html";
        }

        review.setLibro(book);
        review.setUser(user);
        reviewService.saveReview(review);

        return "redirect:/book/" + bookId;
    }

    // Elimina recensione (solo admin)
    @PostMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        Long bookId = review.getLibro().getId();

        reviewService.deleteById(id);

        return "redirect:/book/" + bookId;
    }
}

