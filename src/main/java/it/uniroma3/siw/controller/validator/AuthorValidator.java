package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;

/**
 * Validatore personalizzato per l'entità {@link Author}.
 * 
 * Questo validator verifica che non venga inserito un autore già presente nel sistema,
 * basandosi sulla combinazione di nome, cognome e data di nascita.
 */
@Component
public class AuthorValidator implements Validator {

	@Autowired
	private AuthorService authorService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Author.class.equals(clazz);
	}

	/**
	 * Esegue la validazione dell'oggetto {@code Author}.
	 * Se esiste già un autore con lo stesso nome, cognome e data di nascita,
	 * registra un errore sul campo "name" (può essere visualizzato nel form).
	 *
	 * @param o      oggetto da validare (deve essere un {@code Author})
	 * @param errors oggetto per registrare eventuali errori di validazione
	 */
	@Override
	public void validate(Object o, Errors errors) {
		Author author = (Author) o;

		// Verifica che i campi necessari non siano nulli prima di accedere al database
		if (author.getName() != null && author.getSurname() != null && author.getBirthDate() != null
				&& this.authorService.existsByNameAndSurnameAndBirthDate(author.getName(), author.getSurname(),
						author.getBirthDate())) {

			errors.rejectValue("name", "duplicate", "Autore già esistente");
		}
	}
}
