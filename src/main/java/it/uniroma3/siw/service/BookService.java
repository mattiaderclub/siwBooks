package it.uniroma3.siw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.BookRepository;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	public Book getBookById(Long id) {
		return bookRepository.findById(id).orElse(null);
	}
	
	public Iterable<Book> getAllBooks(){
		return this.bookRepository.findAll();
	}
	
	public void saveBook(Book book) {
        this.bookRepository.save(book);
    }

    public void deleteById(Long id) {
        this.bookRepository.deleteById(id);
    }
    
    /**
     * Ricerca i libri applicando i filtri opzionali.
     *
     * @param title       parte del titolo
     * @param annoMin     anno di pubblicazione minimo
     * @param annoMax     anno di pubblicazione massimo
     * @param minRating   voto medio minimo
     * @return lista dei libri corrispondenti
     */
    public List<Book> searchBooks(String title, Integer annoMin, Integer annoMax, Double minRating) {
        return bookRepository.findByFilters(title, annoMin, annoMax, minRating);
    }
    
    public boolean existsByTitleAndAnnoPubblicazione(String title, Integer annoPubblicazione) {
    	return bookRepository.existsByTitleAndAnnoPubblicazione(title, annoPubblicazione);
	}
    
    /*
     * Recupera valutazione media per un libro
     */
    public Double getAverageRating(Book book) {
        List<Review> recensioni = book.getRecensioni();		// Recupera recensioni del libro
        if (recensioni == null || recensioni.isEmpty()) {
            return null;
        }

        double somma = 0;
        for (Review r : recensioni) {
            somma += r.getRating();
        }

        return somma / recensioni.size();
    }

    /**
     * Calcola la media delle valutazioni (rating) per ogni libro nella lista.
     * 
     * Se un libro non ha recensioni, la media viene impostata a {@code null} (oppure {@code 0.0} se preferito).
     * La mappa risultante associa l'ID del libro alla sua media voto.
     *
     * @param books lista di libri di cui calcolare le medie delle recensioni
     * @return mappa ID libro → media delle valutazioni (Double, può essere null)
     */
    public Map<Long, Double> mediaRecensioniPerLibro(List<Book> books) {
        Map<Long, Double> mediaMap = new HashMap<>();

        for (Book book : books) {
            List<Review> recensioni = book.getRecensioni();

            if (recensioni != null && !recensioni.isEmpty()) {
                double media = recensioni.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
                mediaMap.put(book.getId(), media);
            } else {
                mediaMap.put(book.getId(), null); // oppure 0.0 se preferisci
            }
        }

        return mediaMap;
    }

    public List<Book> getBooksReviewedByUser(User user) {
	    List<Review> reviews = reviewRepository.findAllByUser(user);
	    return reviews.stream().map(Review::getLibro).distinct().toList();
	}

	public List<Book> getBooksNotReviewedByUser(User user) {
	    List<Book> allBooks = bookRepository.findAll();
	    List<Book> reviewed = getBooksReviewedByUser(user);
	    return allBooks.stream()
	                   .filter(b -> !reviewed.contains(b))
	                   .toList();
	}
}
