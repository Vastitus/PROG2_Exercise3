package at.ac.fhcampuswien.fhmdb;

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
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static at.ac.fhcampuswien.fhmdb.models.Movie.mapGenres;

public class MovieAPI {

    public static List<Movie> requestMovies() {
        List<Movie> movies = new ArrayList<>();
        String urlString = "http://prog2.fh-campuswien.ac.at/movies";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();


            JSONArray moviesJSONArray = new JSONArray(response.toString());


            for (int i = 0; i < moviesJSONArray.length(); i++) {
                JSONObject movie = moviesJSONArray.getJSONObject(i);


                movies.add(new Movie(
                        movie.getString("title"),
                        movie.getString("description"),
                        List.of(mapGenres(movie.getJSONArray("genres"))),
                        String.valueOf(movie.getInt("releaseYear")),
                        String.valueOf(movie.getDouble("rating")),
                        movie.getJSONArray("mainCast")

                        ));
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

    public static List<Movie> apiRequestGetActors() {
        String urlString = "http://prog2.fh-campuswien.ac.at/movies";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlString).newBuilder();
        String urlStringBuild = urlBuilder.build().toString();
        apiRequest(urlStringBuild);
        JSONArray moviesJSONArray = apiRequest(urlStringBuild);
        List<Movie> movies = new ArrayList<>();

        return movies;

    }


    public static List<Movie> requestMovies(String queryText, String genre, String releaseYear, String rating) throws IOException {

        String urlString = "http://prog2.fh-campuswien.ac.at/movies";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlString).newBuilder();


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

        String urlStringBuild = urlBuilder.build().toString();

        apiRequest(urlStringBuild);

        JSONArray moviesJSONArray = apiRequest(urlStringBuild);
        List<Movie> movies = new ArrayList<>();

        for (int i = 0; i < moviesJSONArray.length(); i++) {
            JSONObject movie = moviesJSONArray.getJSONObject(i);


            movies.add(new Movie(
                    movie.getString("title"),
                    movie.getString("description"),
                    List.of(mapGenres(movie.getJSONArray("genres"))),
                    String.valueOf(movie.getInt("releaseYear")),
                    String.valueOf(movie.getDouble("rating")),
                    movie.getJSONArray("mainCast")

                    ));

        }

        return movies;
    }

    @Nullable
    private static JSONArray apiRequest(String urlStringBuild) {
        try {
            URL url = new URL(urlStringBuild);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            JSONArray moviesJSONArray = new JSONArray(response.toString());

            return moviesJSONArray;
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}