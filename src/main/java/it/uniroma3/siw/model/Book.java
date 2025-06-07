package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

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
}
