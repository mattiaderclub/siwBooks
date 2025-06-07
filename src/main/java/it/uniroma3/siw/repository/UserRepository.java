package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.User;

public interface UserRepository extends CrudRepository<User, Long>{

	/**
	 * Cerca un utente associato a una email specifica.
	 * @param email la email da cercare
	 * @return un Optional che contiene l'utente se esiste, altrimenti vuoto
	 */
	public Optional<User> findByEmail(String email);
}
