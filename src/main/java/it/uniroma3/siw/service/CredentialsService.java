package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

/**
 * Service che gestisce la logica di business relativa alle credenziali.
 * Funziona come intermediario tra il controller e il repository.
 */
@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

	@Autowired
	protected CredentialsRepository credentialsRepository;
	
	/**
     * Recupera le credenziali in base all'ID.
     * @param id ID delle credenziali
     * @return le credenziali trovate, o null se non esistono
     */
	@Transactional
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	/**
     * Recupera le credenziali in base allo username.
     * @param username username dell'utente
     * @return le credenziali trovate, o null se non esistono
     */
	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
		return result.orElse(null);
	}
		
	/**
     * Salva le credenziali, impostando il ruolo di default e cifrando la password.
     * @param credentials le credenziali da salvare
     * @return le credenziali salvate
     */
    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }
    
    /**
     * Verifica se esistono credenziali per uno username specifico.
     * @param username username da controllare
     * @return true se esistono, false altrimenti
     */
    public boolean existsByUsername(String username) {
        return credentialsRepository.findByUsername(username).isPresent();
    }
    
    @Transactional
    public Credentials saveWithoutEncoding(Credentials credentials) {
        return this.credentialsRepository.save(credentials);
    }
}
