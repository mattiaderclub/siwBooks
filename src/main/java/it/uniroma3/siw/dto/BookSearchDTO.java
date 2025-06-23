package it.uniroma3.siw.dto;

public class BookSearchDTO {

    private String title;
    private Integer annoMin;
    private Integer annoMax;
    private Double minRating;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = (title != null && !title.trim().isEmpty()) ? title.trim() : null;
    }

    public Integer getAnnoMin() {
        return annoMin;
    }

    public void setAnnoMin(Integer annoMin) {
        this.annoMin = annoMin;
    }

    public Integer getAnnoMax() {
        return annoMax;
    }

    public void setAnnoMax(Integer annoMax) {
        this.annoMax = annoMax;
    }

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }
}
