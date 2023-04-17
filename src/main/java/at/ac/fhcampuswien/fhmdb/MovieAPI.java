package at.ac.fhcampuswien.fhmdb;

import java.io.IOException;
import java.util.List;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;


public class MovieAPI {

    public static final String URL = "http://prog2.fh-campuswien.ac.at/movies?";

    //Create a URL with the standard URL (urlBuilder has methods to add and remove parameters etc.)
    public static HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();

    public static HttpUrl.Builder getUrlBuilder() {
        return urlBuilder;
    }

    //Returns the default URL
    public static void setURLDefault() {
        urlBuilder = HttpUrl.parse(URL).newBuilder();
    }

    //Makes an API request and returns the result in a JSONArray
    @Nullable
    public static JSONArray apiRequest(String urlString) {
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

    //Get movies without parameter
    public static List<Movie> requestMoviesWithoutParameter() {
        return Movie.createMovies(URL);
    }

    //Add a parameter to the URL
    public static void addURLParameter(String parameter, String value) {
        urlBuilder.addQueryParameter(parameter, value);
    }

    //Delete a parameter from the URL
    public static void deleteURLParameter(String parameter) {
        urlBuilder.removeAllQueryParameters(parameter);
    }

    //Change a value in the API
    public static void changeURLQuery(String parameter, String value) {
        urlBuilder.setQueryParameter(parameter, value);
    }


}