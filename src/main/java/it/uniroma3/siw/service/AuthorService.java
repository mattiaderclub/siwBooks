package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.repository.AuthorRepository;

@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
	
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
     * @param bornAfter	  natoDopo
     * @param bornBefore  natoPrima
     * @return lista di autori che corrispondono ai filtri
     */
    public List<Author> searchAuthors(
            String name, String surname, String nationality, LocalDate bornAfter, LocalDate bornBefore) {
        return authorRepository.findByFilters(name, surname, nationality, bornAfter, bornBefore);
    }

    /**
     * Ricava gli autori da una lista di ID.
     */
    public Iterable<Author> getAuthorsByIds(Set<Long> ids) {
        return authorRepository.findAllById(ids);
    }
}
