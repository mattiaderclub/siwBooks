package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByUserAndLibro(User user, Book book);

	// tutte le recensioni di un dato libro
	List<Review> findByLibro(Book libro);

	// media delle valutazioni (rating) per un dato libro
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.libro = :libro")
	Double averageRatingByLibro(@Param("libro") Book libro);
	
	Optional<Review> findByUserAndLibro(User user, Book libro);
}
