package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank(message = "{book.title.notblank}")
	@Column(nullable = false)
	private String title;
	
	@NotNull(message = "{book.annoPubblicazione.notnull}")
	@Min(0)
	@Column(nullable = false)
	private Integer annoPubblicazione;
	
	@ElementCollection
    @CollectionTable(name = "book_image_paths", joinColumns = { @JoinColumn(name = "book_id") })
    @Column(name = "path")
    private List<String> imagePaths;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Author> autori;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "libro")
	private List<Review> recensioni;
	
	// === Getter e Setter ===
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getAnnoPubblicazione() {
		return annoPubblicazione;
	}
	public void setAnnoPubblicazione(Integer annoPubblicazione) {
		this.annoPubblicazione = annoPubblicazione;
	}
	public List<String> getImagePaths() {
		return imagePaths;
	}
	public void setImagePaths(List<String> imagePaths) {
		this.imagePaths = imagePaths;
	}
	public Set<Author> getAutori() {
		return autori;
	}
	public void setAutori(Set<Author> autori) {
		this.autori = autori;
	}
	public List<Review> getRecensioni() {
		return recensioni;
	}
	public void setRecensioni(List<Review> recensioni) {
		this.recensioni = recensioni;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Book other = (Book) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
