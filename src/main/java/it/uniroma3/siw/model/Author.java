package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private LocalDate birthDate;
    private LocalDate deathDate;
    @Column(nullable = false)
    private String nationality;

    private String photo;
    
    @OneToMany
    private List<Book> libri;

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
	public List<Book> getLibri() {
		return libri;
	}
	public void setLibri(List<Book> libri) {
		this.libri = libri;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(birthDate, deathDate, id, libri, name, nationality, photo, surname);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return Objects.equals(birthDate, other.birthDate) && Objects.equals(deathDate, other.deathDate)
				&& Objects.equals(id, other.id) && Objects.equals(libri, other.libri)
				&& Objects.equals(name, other.name) && Objects.equals(nationality, other.nationality)
				&& Objects.equals(photo, other.photo) && Objects.equals(surname, other.surname);
	}
}
