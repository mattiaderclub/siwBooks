package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.BookService;

@Component
public class BookValidator implements Validator{

	@Autowired
	private BookService bookService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Book.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Book book = (Book) o;
		
		if (book.getTitle() != null && book.getAnnoPubblicazione() != null
				&& bookService.existsByTitleAndAnnoPubblicazione(
					book.getTitle(), book.getAnnoPubblicazione())) {
				
			errors.rejectValue("title", "duplicate", "Libro già esistente");
		}		
	}
}
