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
        WHERE (:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:surname IS NULL OR LOWER(a.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
          AND (:nationality IS NULL OR LOWER(a.nationality) LIKE LOWER(CONCAT('%', :nationality, '%')))
          AND (:bornAfter   IS NULL OR a.birthDate >= :bornAfter)
          AND (:bornBefore  IS NULL OR a.birthDate <= :bornBefore)
	    """)
    List<Author> findByFilters(
        @Param("name") String name,
        @Param("surname") String surname,
        @Param("nationality") String nationality,
        @Param("bornAfter") LocalDate bornAfter,
        @Param("bornBefore") LocalDate bornBefore
    );
	
	public boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate);
}
