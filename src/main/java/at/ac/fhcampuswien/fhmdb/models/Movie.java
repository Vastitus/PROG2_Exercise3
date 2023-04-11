package at.ac.fhcampuswien.fhmdb.models;

import java.util.List;

import org.json.JSONArray;


public class Movie {
    private final String title;
    private final String description;
    private final List<Genre> genres;

    private final String releaseYear;

    private final String rating;
    private final JSONArray mainCast;
    private final JSONArray directors;


    public Movie(String title, String description, List<Genre> genres, String releaseYear, String rating, JSONArray mainCast, JSONArray directors) {

        this.title = title;
        this.description = description;
        this.genres = genres;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.mainCast = mainCast;
        this.directors = directors;
    }


    public JSONArray getMainCast() {
        return mainCast;
    }

    public JSONArray getDirectors() {
        return directors;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Movie other)) {
            return false;
        }
        return this.title.equals(other.title) && this.description.equals(other.description) && this.genres.equals(other.genres);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getRating() {
        return rating;
    }

    public static Genre[] mapGenres(JSONArray genresArray) {
        Genre[] genres = new Genre[genresArray.length()];
        for (int i = 0; i < genresArray.length(); i++) {
            String genreString = genresArray.getString(i).toUpperCase();
            genres[i] = Genre.valueOf(genreString);
        }
        return genres;
    }


}
