package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.models.Movie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import java.io.IOException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.HttpUrl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static at.ac.fhcampuswien.fhmdb.models.Movie.mapGenres;

public class MovieAPI {
    private static final String API_BASE_URL = "https://prog2.fh-campuswien.ac.at/movies";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String USER_AGENT_VALUE = "http.agent";
    private static OkHttpClient client;
    private static Gson gson;

    public MovieAPI() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public List<Movie> getMovies() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_BASE_URL).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader(USER_AGENT_HEADER, USER_AGENT_VALUE)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            TypeToken<List<Movie>> movieListType = new TypeToken<List<Movie>>() {};
            List<Movie> movies = gson.fromJson(responseBody, movieListType.getType());

            return movies;
        }
    }


    public static List<Movie> apiRequest (String queryText, String genre, String releaseYear, String rating) throws IOException {

        String urlString = "http://prog2.fh-campuswien.ac.at/movies";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlString).newBuilder();
        /*urlBuilder.addQueryParameter("query", query);
        urlBuilder.addQueryParameter("genre", genre);*/

        if (!queryText.equals("")) {
            urlBuilder.addQueryParameter("query", queryText);

        }

        if (!genre.equals("null") && !genre.equals("No filter") && !genre.equals("Filter by Genre") && !queryText.equals("")) {
            urlBuilder.addQueryParameter("genre", genre);
        }

        if (!releaseYear.equals("null") && !genre.equals("Filter by Release Year")) {
            urlBuilder.addQueryParameter("releaseYear", releaseYear);
        }

        if (!rating.equals("null") && !rating.equals("Filter by Rating")) {
            urlBuilder.addQueryParameter("ratingFrom", rating);
        }

        String urlStringBuild = urlBuilder.build().toString();

        System.out.println("Built URL: " + urlStringBuild);
        System.out.println(urlString);

        List<Movie> movies = new ArrayList<>();
        try {
            URL url = new URL(urlStringBuild);
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

            JSONArray moviesJSONArray = new JSONArray(response.toString());


            for (int i = 0; i < moviesJSONArray.length(); i++) {
                JSONObject movie = moviesJSONArray.getJSONObject(i);


                movies.add(new Movie(
                        movie.getString("title"),
                        movie.getString("description"),
                        List.of(mapGenres(movie.getJSONArray("genres")))));

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
}