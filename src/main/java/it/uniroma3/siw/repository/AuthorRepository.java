package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {

	@Query("""
			    SELECT a FROM Author a
			    WHERE (:name 		IS NULL OR a.name 			= :name)
			      AND (:surname 	IS NULL OR a.surname 		= :surname)
			      AND (:nationality IS NULL OR a.nationality 	= :nationality)
			""")
	List<Author> findByFilters(@Param("name") String name, @Param("surname") String surname,
			@Param("nationality") String nationality);

	public boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate);
}
