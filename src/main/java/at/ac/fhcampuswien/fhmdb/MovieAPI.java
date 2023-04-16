package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static at.ac.fhcampuswien.fhmdb.models.Movie.mapGenres;

public class MovieAPI {

    //Makes an API request and returns the result in a JSONArray
    @Nullable
    private static JSONArray apiRequest(String urlString) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlString)
                        .addHeader("User-Agent", "http.agent")
                        .build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    throw new RuntimeException("Error: " + response.code());
                }

                String responseString = response.body().string();
                JSONArray moviesJSONArray = new JSONArray(responseString);

                return moviesJSONArray;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    //Gets a URL from a request-Method, makes API-Request, builds the Movies and returns them as a list.
    @NotNull
    private static List<Movie> createMovies(String url) {
        JSONArray moviesJSONArray = apiRequest(url);
        List<Movie> movies = new ArrayList<>();

        for (int i = 0; i < moviesJSONArray.length(); i++) {
            JSONObject movie = moviesJSONArray.getJSONObject(i);

            movies.add(new Movie(
                    movie.getString("title"),
                    movie.getString("description"),
                    List.of(mapGenres(movie.getJSONArray("genres"))),
                    String.valueOf(movie.getInt("releaseYear")),
                    String.valueOf(movie.getDouble("rating")),
                    movie.getJSONArray("mainCast"),
                    movie.getJSONArray("writers"),
                    movie.getJSONArray("directors")
            ));
        }
        return movies;
    }

    //The request-methods call the createMovies method with a URL

    //Request Movies without any parameters
    public static List<Movie> requestMovies() {
        String url = "http://prog2.fh-campuswien.ac.at/movies";
        return createMovies(url);

    }

    //Request Movies with parameters
    public static List<Movie> requestMovies(String queryText, String genre, String releaseYear, String rating) throws IOException {

        String url = "http://prog2.fh-campuswien.ac.at/movies";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (!queryText.equals("")) {
            urlBuilder.addQueryParameter("query", queryText);
        }

        if (!genre.equals("null") && !genre.equals("No filter") && !genre.equals("Filter by Genre") && !genre.equals("")) {
            urlBuilder.addQueryParameter("genre", genre);
        }

        if (!releaseYear.equals("null") && !releaseYear.equals("Filter by Release Year") && !releaseYear.equals("")) {
            urlBuilder.addQueryParameter("releaseYear", releaseYear);
        }

        if (!rating.equals("null") && !rating.equals("Filter by Rating")) {
            urlBuilder.addQueryParameter("ratingFrom", rating);
        }

        String urlNew = urlBuilder.build().toString();

        apiRequest(urlNew);

        return createMovies(urlNew);
    }

    public static List<Movie> requestMoviesByGenre(Genre genre) {
        String url = "http://prog2.fh-campuswien.ac.at/movies?genre=" + genre;
        List<Movie> movies = createMovies(url);
        return movies;
    }
}