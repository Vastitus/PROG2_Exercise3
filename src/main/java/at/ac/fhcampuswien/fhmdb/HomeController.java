package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import sun.net.www.http.KeepAliveCache;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {
    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public JFXListView movieListView;

    @FXML
    public JFXComboBox genreComboBox;

    @FXML
    public JFXComboBox ratingComboBox;

    @FXML TextField releaseYearTextField;

    @FXML
    public JFXButton sortBtn;

    public List<Movie> allMovies;

    protected ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    protected SortedState sortedState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeState();
        initializeLayout();
    }

    public void initializeState() {
        allMovies = MovieAPI.requestMovies();
        observableMovies.clear();
        observableMovies.addAll(allMovies); // add all movies to the observable list
        sortedState = SortedState.NONE;
        sortMovies();
    }

    public void initializeLayout() {
        movieListView.setItems(observableMovies);   // set the items of the listview to the observable list
        movieListView.setCellFactory(movieListView -> new MovieCell()); // apply custom cells to the listview

        Object[] genres = Genre.values();   // get all genres
        genreComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        genreComboBox.getItems().addAll(genres);    // add all genres to the combobox
        genreComboBox.setPromptText("Filter by Genre");

        ratingComboBox.getItems().add("0");
        ratingComboBox.getItems().add("0.5");
        ratingComboBox.getItems().add("1");
        ratingComboBox.getItems().add("1.5");
        ratingComboBox.getItems().add("2");
        ratingComboBox.getItems().add("2.5");
        ratingComboBox.getItems().add("3");
        ratingComboBox.getItems().add("3.5");
        ratingComboBox.getItems().add("4");
        ratingComboBox.getItems().add("4.5");
        ratingComboBox.getItems().add("5");
        ratingComboBox.getItems().add("5.5");
        ratingComboBox.getItems().add("6");
        ratingComboBox.getItems().add("6.5");
        ratingComboBox.getItems().add("7");
        ratingComboBox.getItems().add("7.5");
        ratingComboBox.getItems().add("8");
        ratingComboBox.getItems().add("8.5");
        ratingComboBox.getItems().add("9");
        ratingComboBox.getItems().add("9.5");

        ratingComboBox.setPromptText("Filter by Rating");
    }

    // sort movies based on sortedState
    // by default sorted state is NONE
    // afterwards it switches between ascending and descending
    public void sortMovies() {
        if (sortedState == SortedState.NONE || sortedState == SortedState.DESCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle));
            sortedState = SortedState.ASCENDING;
        } else if (sortedState == SortedState.ASCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
            sortedState = SortedState.DESCENDING;
        }
    }

    public List<Movie> filterByQuery(List<Movie> movies, String query){
        if(query == null || query.isEmpty()) return movies;

        if(movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream()
                .filter(Objects::nonNull)
                .filter(movie ->
                    movie.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    movie.getDescription().toLowerCase().contains(query.toLowerCase())
                )
                .toList();
    }

    public List<Movie> filterByGenre(List<Movie> movies, Genre genre){
        if(genre == null) return movies;

        if(movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream()
                .filter(Objects::nonNull)
                .filter(movie -> movie.getGenres().contains(genre))
                .toList();
    }

    public void applyAllFilters(String searchQuery, Object genre) {
        List<Movie> filteredMovies = allMovies;

        if (!searchQuery.isEmpty()) {
            filteredMovies = filterByQuery(filteredMovies, searchQuery);
        }

        if (genre != null && !genre.toString().equals("No filter")) {
            filteredMovies = filterByGenre(filteredMovies, Genre.valueOf(genre.toString()));
        }

        if (searchQuery.equals("") &&  genreComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Choose Filter");
            alert.setContentText("Please choose a Filter!");
            alert.show();
        }

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);
    }

    public void searchBtnClicked(ActionEvent actionEvent) throws IOException {


        if (!releaseYearTextField.getText().matches("[0-9]*")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wrong input");
            alert.setContentText("Please enter a Number in the Release Year and Rating Text field!");
            alert.show();
        }  else
            {
                try {

                    observableMovies.clear();
                    observableMovies.addAll(MovieAPI.requestMovies(searchField.getText().trim().toLowerCase(), String.valueOf(genreComboBox.getSelectionModel().getSelectedItem()),
                            String.valueOf(releaseYearTextField.getText()), String.valueOf(ratingComboBox.getValue())));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if(sortedState != SortedState.NONE) {
                    sortMovies();
                }

            }

        //Test of Stream Methods
        getMostPopularActor(observableMovies);
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }

    String getMostPopularActor(List<Movie> movies) {

       return "";
    }

    int getLongestMovieTitle(List<Movie> movies) {

        return 0;
    }

    long countMoviesFrom (List<Movie> movies, int startYear,int endYear) {

        return 0;
    }

    List <Movie> getMoviesBetweenYears (List<Movie> movies, int startYear, int endYear) {

        return movies;
    }
}