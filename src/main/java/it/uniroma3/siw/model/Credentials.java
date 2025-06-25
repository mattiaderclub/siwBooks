package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

/**
 * Entità che rappresenta le credenziali di accesso di un utente. Contiene
 * informazioni come username, password e ruolo associato. È associata in
 * maniera univoca a un'entità User.
 */
@Entity
public class Credentials {

	/* Costanti String per il ruolo */
	public static final String DEFAULT_ROLE = "DEFAULT";
	public static final String ADMIN_ROLE = "ADMIN";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/* Variabili d'istanza private */
	@NotBlank(message = "{credentials.username.notblank}")
	private String username;

	@NotBlank(message = "{credentials.password.notblank}")
	@jakarta.validation.constraints.Size(min = 6, message = "{credentials.password.short}")
	private String password;

	private String role;

	/**
	 * Associazione 1 a 1 verso l'entità User. Viene caricata eager e cascata tutte
	 * le operazioni (persist, remove, etc.).
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private User user;

	// === Getter e Setter ===
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, password, role, user, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Credentials other = (Credentials) obj;
		return Objects.equals(id, other.id) && Objects.equals(password, other.password)
				&& Objects.equals(role, other.role) && Objects.equals(user, other.user)
				&& Objects.equals(username, other.username);
	}
}
