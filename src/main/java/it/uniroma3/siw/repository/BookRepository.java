package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

	@Query("""
			    SELECT b FROM Book b
			    WHERE (:title 		IS NULL OR b.title 				= :title)
			      AND (:annoMin 	IS NULL OR b.annoPubblicazione >= :annoMin)
			      AND (:annoMax 	IS NULL OR b.annoPubblicazione <= :annoMax)
			      AND (:minRating 	IS NULL OR (SELECT AVG(r.rating) FROM Review r WHERE r.libro = b) >= :minRating)
			""")
	List<Book> findByFilters(@Param("title") String title, @Param("annoMin") Integer annoMin,
			@Param("annoMax") Integer annoMax, @Param("minRating") Double minRating);

	public boolean existsByTitleAndAnnoPubblicazione(String title, Integer annoPubblicazione);
}
