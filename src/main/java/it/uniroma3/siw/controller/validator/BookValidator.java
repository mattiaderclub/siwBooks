package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

/**
 * Validatore personalizzato per l'entità {@link Book}.
 * 
 * Questo validator viene usato nei form per impedire l'inserimento
 * di libri duplicati con lo stesso titolo e anno di pubblicazione.
 */
@Component
public class BookValidator implements Validator{

	@Autowired
	private BookService bookService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Book.class.equals(clazz);
	}

	/**
	 * Esegue la validazione dell'oggetto {@code Book}.
	 * Se esiste già un libro con lo stesso titolo e anno, 
	 * aggiunge un errore al campo "title".
	 * 
	 * @param o      oggetto da validare (deve essere un Book)
	 * @param errors oggetto usato per registrare eventuali errori
	 */
	@Override
	public void validate(Object o, Errors errors) {
		Book book = (Book) o;
		
		// Verifica che titolo e anno non siano nulli prima di controllare il database
		if (book.getTitle() != null && book.getAnnoPubblicazione() != null
				&& bookService.existsByTitleAndAnnoPubblicazione(
					book.getTitle(), book.getAnnoPubblicazione())) {
				
			errors.rejectValue("title", "duplicate", "Libro già esistente");
		}		
	}
}
