package at.ac.fhcampuswien.fhmdb.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
    private final String title;
    private final String description;
    private final List<Genre> genres;

    public Movie(String title, String description, List<Genre> genres) {
        this.title = title;
        this.description = description;
        this.genres = genres;
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

    private static Genre[] mapGenres(JSONArray genresArray) {
        Genre[] genres = new Genre[genresArray.length()];
        for (int i = 0; i < genresArray.length(); i++) {
            String genreString = genresArray.getString(i).toUpperCase();
            genres[i] = Genre.valueOf(genreString);
        }
        return genres;
    }

    public static List<Movie> apiRequestInitial() {
        List<Movie> movies = new ArrayList<>();
        String urlString = "http://prog2.fh-campuswien.ac.at/movies";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            // parse the response JSON and extract the desired fields
            JSONArray moviesJSONArray = new JSONArray(response.toString());
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < moviesJSONArray.length(); i++) {
                JSONObject movie = moviesJSONArray.getJSONObject(i);
                String title = movie.getString("title");
                String descriptionReturned = movie.getString("description");
                String genreReturned = movie.getJSONArray("genres").join(", ");
                String releaseYearReturned = String.valueOf(movie.getInt("releaseYear"));
                String ratingReturned = String.valueOf(movie.getDouble("rating"));

                movies.add(new Movie(
                        movie.getString("title"),
                        movie.getString("description"),
                        List.of(mapGenres(movie.getJSONArray("genres")))));

                result.append(title).append("\n")
                        .append(descriptionReturned).append("\n")
                        .append(genreReturned).append("\n")
                        .append(releaseYearReturned).append("\n")
                        .append(ratingReturned).append("\n\n");
            }

            return movies;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static List<Movie> apiRequest (String queryText, String genre, String releaseYear, String rating) throws IOException {

        String urlString = "http://prog2.fh-campuswien.ac.at/movies?";

        if (!queryText.equals("")) {
            urlString = urlString + "query=" +queryText;
        }

        if (!genre.equals("null") && !genre.equals("No filter") && !genre.equals("Filter by Genre") && !queryText.equals("")) {
            urlString = urlString + "&genre=" + genre;
        }

        if (!releaseYear.equals("null") && !genre.equals("Filter by Release Year")) {
            urlString = urlString + "&releaseYear=" + releaseYear;
        }

        if (!rating.equals("null") && !rating.equals("Filter by Rating")) {
            urlString = urlString + "&ratingFrom=" + rating;
        }

        System.out.println(urlString);

        List<Movie> movies = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            // parse the response JSON and extract the desired fields
            JSONArray moviesJSONArray = new JSONArray(response.toString());
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < moviesJSONArray.length(); i++) {
                JSONObject movie = moviesJSONArray.getJSONObject(i);
                String title = movie.getString("title");
                String descriptionReturned = movie.getString("description");
                String genreReturned = movie.getJSONArray("genres").join(", ");
                String releaseYearReturned = String.valueOf(movie.getInt("releaseYear"));
                String ratingReturned = String.valueOf(movie.getDouble("rating"));

                movies.add(new Movie(
                        movie.getString("title"),
                        movie.getString("description"),
                        List.of(mapGenres(movie.getJSONArray("genres")))));

                result.append(title).append("\n")
                        .append(descriptionReturned).append("\n")
                        .append(genreReturned).append("\n")
                        .append(releaseYearReturned).append("\n")
                        .append(ratingReturned).append("\n\n");
            }

            return movies;


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Movie> initializeMovies(){
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(
                "Life Is Beautiful",
                "When an open-minded Jewish librarian and his son become victims of the Holocaust, he uses a perfect mixture of will, humor, and imagination to protect his son from the dangers around their camp." ,
                Arrays.asList(Genre.DRAMA, Genre.ROMANCE)));
        movies.add(new Movie(
                "The Usual Suspects",
                "A sole survivor tells of the twisty events leading up to a horrific gun battle on a boat, which begin when five criminals meet at a seemingly random police lineup.",
                Arrays.asList(Genre.CRIME, Genre.DRAMA, Genre.MYSTERY)));
        movies.add(new Movie(
                "Puss in Boots",
                "An outlaw cat, his childhood egg-friend, and a seductive thief kitty set out in search for the eggs of the fabled Golden Goose to clear his name, restore his lost honor, and regain the trust of his mother and town.",
                Arrays.asList(Genre.COMEDY, Genre.FAMILY, Genre.ANIMATION)));
        movies.add(new Movie(
                "Avatar",
                "A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.",
                Arrays.asList(Genre.ANIMATION, Genre.DRAMA, Genre.ACTION)));
        movies.add(new Movie(
                "The Wolf of Wall Street",
                "Based on the true story of Jordan Belfort, from his rise to a wealthy stock-broker living the high life to his fall involving crime, corruption and the federal government.",
                Arrays.asList(Genre.DRAMA, Genre.ROMANCE, Genre.BIOGRAPHY)));

        return movies;

    }
}
