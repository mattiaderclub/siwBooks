package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "{author.name.notblank}")
	@Column(nullable = false)
	private String name;
	@NotBlank(message = "{author.surname.notblank}")
	@Column(nullable = false)
	private String surname;

	@PastOrPresent(message = "{author.birthDate.pastorpresent}")
	@NotNull(message = "{author.birthDate.notnull}")
	@Column(nullable = false)
	private LocalDate birthDate;
	@PastOrPresent(message = "{author.deathDate.pastorpresent}")
	private LocalDate deathDate;

	@NotBlank(message = "{author.nationality.notblank}")
	@Column(nullable = false)
	private String nationality;

	private String photo;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "autori")
	private Set<Book> libri;

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

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Set<Book> getLibri() {
		return libri;
	}

	public void setLibri(Set<Book> libri) {
		this.libri = libri;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return Objects.equals(id, other.id);
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
