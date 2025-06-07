package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String title;
	private Integer annoPubblicazione;
	
	@ElementCollection
    @CollectionTable(name = "book_image_paths", joinColumns = { @JoinColumn(name = "book_id") })
    @Column(name = "path")
    private List<String> imagePaths;
	
	@OneToMany
	private List<Author> autori;
	@OneToMany
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
	public List<Author> getAutori() {
		return autori;
	}
	public void setAutori(List<Author> autori) {
		this.autori = autori;
	}
	public List<Review> getRecensioni() {
		return recensioni;
	}
	public void setRecensioni(List<Review> recensioni) {
		this.recensioni = recensioni;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(annoPubblicazione, autori, id, imagePaths, recensioni, title);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(annoPubblicazione, other.annoPubblicazione) && Objects.equals(autori, other.autori)
				&& Objects.equals(id, other.id) && Objects.equals(imagePaths, other.imagePaths)
				&& Objects.equals(recensioni, other.recensioni) && Objects.equals(title, other.title);
	}
}
