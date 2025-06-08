package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
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
    
    public Double getAverageRating(Book book) {
        List<Review> reviews = book.getRecensioni();
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }
        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        return sum / reviews.size();
    }

}
