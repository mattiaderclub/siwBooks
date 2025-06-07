package it.uniroma3.siw.repository;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

	/**
	 * Cerca le credenziali associate a uno username specifico.
	 * @param username lo username da cercare
	 * @return un Optional che contiene le credenziali se esistono, altrimenti vuoto
	 */
	public Optional<Credentials> findByUsername(String username);
}