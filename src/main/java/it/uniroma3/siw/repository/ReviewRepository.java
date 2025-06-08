package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByUserAndLibro(User user, Book book);
}
