package eu.h2020.sc.domain;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class Question {

    private String text;
    private Integer rating;

    public Question(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
