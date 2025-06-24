package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    /** ----------------------------------------
     * Aggiungi nuova recensione
     * ---------------------------------------- */
    @PostMapping("/book/{bookId}/review")
    @PreAuthorize("isAuthenticated()")
    public String addReview(@PathVariable Long bookId,
                            @Valid @ModelAttribute("reviewForm") Review review,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails currentUser,
                            Model model) {

        Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
        User user = credentials.getUser();
        Book book = bookService.getBookById(bookId);

        if (reviewService.existsByUserAndBook(user, book)) {
            bindingResult.reject("duplicate", "Hai già recensito questo libro.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            model.addAttribute("myReview", null); // per evitare conflitti in HTML
            return "book.html";
        }

        review.setUser(user);
        review.setLibro(book);
        reviewService.saveReview(review);

        return "redirect:/book/" + bookId;
    }

    /** ----------------------------------------
     * Modifica recensione
     * ---------------------------------------- */
    @PostMapping("/book/{bookId}/review/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editReview(@PathVariable Long bookId,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("reviewForm") Review review,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails currentUser,
                             Model model) {

        Review existingReview = reviewService.getReviewById(id);
        Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());

        if (existingReview == null || !existingReview.getUser().getId().equals(credentials.getUser().getId())) {
            return "redirect:/book/" + bookId;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("book", existingReview.getLibro());
            model.addAttribute("myReview", existingReview);
            return "book.html";
        }

        existingReview.setTitle(review.getTitle());
        existingReview.setText(review.getText());
        existingReview.setRating(review.getRating());
        reviewService.saveReview(existingReview);

        return "redirect:/book/" + bookId;
    }

    /** ----------------------------------------
     * Elimina recensione - Utente o Admin
     * ---------------------------------------- */
    @PostMapping("/review/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteReview(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails currentUser) {

        Review review = reviewService.getReviewById(id);
        if (review == null) return "redirect:/";

        Long bookId = review.getLibro().getId();

        Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());

        // Se admin, elimina sempre. Se user, solo se è il proprietario
        if (credentials.getRole().equals(Credentials.ADMIN_ROLE) ||
            review.getUser().getId().equals(credentials.getUser().getId())) {
            reviewService.deleteById(id);
        }

        return "redirect:/book/" + bookId;
    }
}
