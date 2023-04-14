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
import java.util.*;

import java.io.IOException;
import java.net.URL;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        //Fill the Rating ComboBox
        for (double i = 0; i <= 10; i = i + 0.5) {
            ratingComboBox.getItems().add(i);
        }
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
        List <Movie> moviesList;
        if(genre == null) return movies;

        if(movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        moviesList = MovieAPI.requestMoviesByGenre(genre);

        return moviesList;
    }

    private static void createAlert(Alert.AlertType alertType, String title, String contentText) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.show();
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
            createAlert(Alert.AlertType.WARNING, "Choose Filter", "Please choose a Filter!");
        }

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);
    }

    public void searchBtnClicked(ActionEvent actionEvent) throws IOException {
        List<Movie> filteredMovies;

        //If Genre is chosen, call filterByGenre and add result into observableMovies List
        if (genreComboBox.getValue() != null && genreComboBox.getValue() != "Filter by Genre" && genreComboBox.getValue() != "No filter") {
            filteredMovies = filterByGenre((List<Movie>) observableMovies, (Genre) genreComboBox.getSelectionModel().getSelectedItem());
            observableMovies.clear();
            observableMovies.addAll(filteredMovies);
        }

        //If "no filter", fill the List with all Movies
        if (genreComboBox.getValue() == "No filter") {
            observableMovies.clear();
            observableMovies.addAll(allMovies);
        }

        /*if (!releaseYearTextField.getText().matches("[0-9]*")) {
            createAlert(Alert.AlertType.WARNING, "Wrong Input", "Please enter a Number in the Release Year and Rating Text field!");
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

            }*/
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }

    String getMostPopularActor(List<Movie> movies) {

        Map<Object, Long> actorCounts = movies.stream()
                .flatMap(movie -> movie.getMainCast().toList().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Optional<Long> maxCount = actorCounts.values().stream().max(Long::compare);

        String mostPopularActor = actorCounts.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCount.orElse(null)))
                .map(entry -> (String) entry.getKey())
                .collect(Collectors.joining(", "));

        return mostPopularActor;
    }

    int getLongestMovieTitle(List<Movie> movies) {
        OptionalInt longestTitleLength = movies.stream()
                .mapToInt(movie -> movie.getTitle().replaceAll("\\s", "").length())
                .max();

        return longestTitleLength.orElse(0);
    }

    //Additional Method to get the Title of the longest movie
    String getTitleOfLongestMovie(List<Movie> movies) {
        Optional<Movie> longestTitleMovie = movies.stream()
                .max(Comparator.comparingInt(movie -> movie.getTitle().length()));

        return longestTitleMovie.map(Movie::getTitle).orElse("");
    }

    long countMoviesFrom (List<Movie> movies, String director) {
        return movies.stream()
                .filter(movie -> movie.getDirectors().toList().contains(director))
                .count();
    }

    List <Movie> getMoviesBetweenYears (List<Movie> movies, int startYear, int endYear) {
        return movies.stream()
                .filter(movie -> Integer.parseInt(movie.getReleaseYear()) >= startYear && Integer.parseInt(movie.getReleaseYear()) <= endYear)
                .collect(Collectors.toList());
    }

    public void mostPopularActorButtonClicked(ActionEvent actionEvent) {

        createAlert(Alert.AlertType.INFORMATION, "Most popular Actor", "The most popular Actor/s is/are: " + getMostPopularActor(observableMovies));
    }


    public void longestMovieTitleButtonClicked(ActionEvent actionEvent) {
        createAlert(Alert.AlertType.INFORMATION, "The longest Movie Title", "The longest Movie Title is '" + getTitleOfLongestMovie(observableMovies) + "' and has " + getLongestMovieTitle(observableMovies) + " characters.");
    }

    public void filterMoviesBetweenYearsButtonClicked(ActionEvent actionEvent) {

        if (!releaseYearTextField.getText().matches("[0-9]*") || releaseYearTextField.getText() == "" || releaseYearTextField.getText() == null) {
            createAlert(Alert.AlertType.WARNING, "Wrong Input", "Please enter a valid release year!");
        }  else if (Integer.parseInt(releaseYearTextField.getText()) > 2023) {
            createAlert(Alert.AlertType.WARNING, "Wrong Input", "The release year can't be in the future!");
        } else if (Integer.parseInt(releaseYearTextField.getText()) < 1900) {
            createAlert(Alert.AlertType.WARNING, "Wrong Input", "Please don't choose a release year before 1900.");
        }
        else {

            List<Movie> moviesFiltered = getMoviesBetweenYears(observableMovies, Integer.parseInt(releaseYearTextField.getText()), 2023);

            if (moviesFiltered.size() == 1) {
                createAlert(Alert.AlertType.INFORMATION, "Movie released between...", moviesFiltered.size() +
                        " Movie has been released between " + releaseYearTextField.getText() + " and 2023.");
            } else {
                createAlert(Alert.AlertType.INFORMATION, "Movies released between...", moviesFiltered.size() +
                        " Movies have been released between " + releaseYearTextField.getText() + " and 2023.");
            }
            System.out.println("Movies released: ");
            for (int i = 0; i < moviesFiltered.size(); i++) {
                System.out.println((moviesFiltered.get(i).getTitle() + ", " + moviesFiltered.get(i).getReleaseYear()));
            }
        }
    }

    public void countMoviesFromButtonClicked(ActionEvent actionEvent) {
        createAlert(Alert.AlertType.INFORMATION, "Count Movies from...", "There are " + countMoviesFrom(observableMovies, "Steven Spielberg") + " movies from Steven Spielberg.");
    }
}
