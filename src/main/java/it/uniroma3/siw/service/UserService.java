package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

/**
 * Service che gestisce la logica di business relativa agli utenti.
 * Fa da intermediario tra il controller e il repository.
 */
@Service
public class UserService {

    @Autowired
    protected UserRepository userRepository;
    
    /**
     * Recupera un utente a partire dal suo ID.
     * @param id ID dell'utente
     * @return l'utente trovato, o null se non esiste
     */
    @Transactional		// indica che un metodo deve essere eseguito dentro una transazione.
    public User getUser(Long id) {
    	
    	Optional<User> result = this.userRepository.findById(id);
    	return result.orElse(null);
    	
    }
    
    /**
     * Salva o aggiorna un utente nel database.
     * @param user l'utente da salvare
     * @return l'utente salvato
     */
    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }
    
    /**
     * Verifica se esiste un utente con la email specificata.
     * @param email la email da controllare
     * @return true se esiste un utente con questa email, false altrimenti
     */
    public boolean existsByEmail(String email) {
    	return userRepository.findByEmail(email).isPresent();
    }
}
