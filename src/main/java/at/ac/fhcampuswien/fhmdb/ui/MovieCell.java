package at.ac.fhcampuswien.fhmdb.ui;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class MovieCell extends ListCell<Movie> {
    private final Label title = new Label();
    private final Label detail = new Label();
    private final Label genre = new Label();
    private final Label releaseYear = new Label();
    private final Label rating = new Label();
    private final Label mainCast = new Label();
    private final Label directors = new Label();
    private final VBox layout = new VBox(title, detail, genre, releaseYear, rating, mainCast, directors);

    @Override
    protected void updateItem(Movie movie, boolean empty) {
        super.updateItem(movie, empty);

        if (empty || movie == null) {
            setGraphic(null);
            setText(null);
        } else {
            this.getStyleClass().add("movie-cell");
            title.setText(movie.getTitle());
            detail.setText(
                    movie.getDescription() != null
                            ? movie.getDescription()
                            : "No description available"
            );


            String genres = movie.getGenres()
                    .stream()
                    .map(Enum::toString)
                    .collect(Collectors.joining(", "));
            genre.setText(genres);

            releaseYear.setText("Release Year: " + movie.getReleaseYear());

            rating.setText("Rating: " + movie.getRating());
            JSONArray mainCastList = movie.getMainCast();
            StringJoiner mainCastJoiner = new StringJoiner(", ");
            for(int i=0; i<mainCastList.length(); i++) {
                mainCastJoiner.add(mainCastList.getString(i));
            }
            String mainCastString = mainCastJoiner.toString();
            mainCast.setText("Main Cast: " + mainCastString);

            JSONArray directorsList = movie.getDirectors();
            List<String> directorsArrayList = new ArrayList<>();
            for (int i = 0; i < directorsList.length(); i++) {
                directorsArrayList.add(directorsList.getString(i));
            }
            String directorsString = String.join(", ", directorsArrayList);
            directors.setText("Directors: " + directorsString);



            // color scheme
            title.getStyleClass().add("text-yellow");
            detail.getStyleClass().add("text-white");
            genre.getStyleClass().add("text-white");
            genre.setStyle("-fx-font-style: italic");
            releaseYear.getStyleClass().add("text-white");
            releaseYear.setStyle("-fx-font-style: italic");
            rating.getStyleClass().add("text-white");
            rating.setStyle("-fx-font-style: italic");
            mainCast.getStyleClass().add("text-white");
            mainCast.setStyle("-fx-font-style: italic");
            directors.getStyleClass().add("text-white");
            directors.setStyle("-fx-font-style: italic");

            layout.setBackground(new Background(new BackgroundFill(Color.web("#454545"), null, null)));

            // layout
            title.fontProperty().set(title.getFont().font(20));
            detail.setMaxWidth(this.getScene().getWidth() - 30);
            detail.setWrapText(true);
            layout.setPadding(new Insets(10));
            layout.spacingProperty().set(10);
            layout.alignmentProperty().set(javafx.geometry.Pos.CENTER_LEFT);
            setGraphic(layout);
        }
    }
}

