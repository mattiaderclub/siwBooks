package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Entità che rappresenta un utente del sistema. Contiene informazioni personali
 * come nome, cognome, email e cellulare.
 */
@Entity
@Table(name = "users") // "user" è parola riservata in Postgres, quindi si usa "users"
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// === Dati utente ===
	@NotBlank(message = "{user.name.notblank}")
	private String name;

	@NotBlank(message = "{user.surname.notblank}")
	private String surname;

	@NotBlank(message = "{user.email.notblank}")
	@jakarta.validation.constraints.Email(message = "{user.email.invalid}")
	private String email;

	@NotBlank(message = "{user.cell.notblank}")
	private String cell;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	private List<Review> review;

	// === Getter e Setter ===
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	public List<Review> getReview() {
		return review;
	}

	public void setReview(List<Review> review) {
		this.review = review;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cell, email, id, name, review, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(cell, other.cell) && Objects.equals(email, other.email) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(review, other.review)
				&& Objects.equals(surname, other.surname);
	}
}
