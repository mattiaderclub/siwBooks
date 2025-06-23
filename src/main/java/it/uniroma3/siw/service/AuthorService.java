package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;

@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private ReviewService reviewService;

	public Iterable<Author> getAllAuthors() {
		return authorRepository.findAll();
	}

	public Author getAuthorById(Long id) {
		return authorRepository.findById(id).orElse(null);
	}

	public void saveAuthor(Author author) {
		authorRepository.save(author);
	}

	public void deleteById(Long id) {
		this.authorRepository.deleteById(id);
	}

	/**
	 * Ricerca avanzata autori con parametri opzionali.
	 *
	 * @param name        nome (parziale o completo)
	 * @param surname     cognome (parziale o completo)
	 * @param nationality nazionalità (parziale o completa)
	 * @param bornAfter   natoDopo
	 * @param bornBefore  natoPrima
	 * @return lista di autori che corrispondono ai filtri
	 */
	public List<Author> searchAuthors(String name, String surname, String nationality, LocalDate bornAfter,
			LocalDate bornBefore) {
		return authorRepository.findByFilters(name, surname, nationality, bornAfter, bornBefore);
	}

	/**
	 * Ricava gli autori da una lista di ID.
	 */
	public Iterable<Author> getAuthorsByIds(Set<Long> ids) {
		return authorRepository.findAllById(ids);
	}

	public boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate) {
		return this.authorRepository.existsByNameAndSurnameAndBirthDate(name, surname, birthDate);
	}

	public Double getAverageRatingOfAuthor(Author author) {
		Set<Book> books = author.getLibri();
		if (books == null || books.isEmpty())
			return null;

		double sum = 0;
		int count = 0;
		for (Book b : books) {
			Double bookAvg = reviewService.getAverageRatingForBook(b);
			if (bookAvg != null) {
				sum += bookAvg;
				count++;
			}
		}
		return (count == 0) ? null : (sum / count);
	}
}
