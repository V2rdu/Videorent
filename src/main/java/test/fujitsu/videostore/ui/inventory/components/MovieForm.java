package test.fujitsu.videostore.ui.inventory.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;

import test.fujitsu.videostore.backend.domain.Movie;
import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.inventory.VideoStoreInventoryLogic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Movie form
 */
public class MovieForm extends Div {

    private VerticalLayout content;

    private TextField name;
    private TextField stockCount;
    private ComboBox<MovieType> type;
    private Button save;
    private Button cancel;
    private Button delete;

    private VideoStoreInventoryLogic viewLogic;
    private Binder<Movie> binder;
    private Movie currentMovie;

    public MovieForm(VideoStoreInventoryLogic videoStoreInventoryLogic) {
        setId("edit-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = videoStoreInventoryLogic;

        name = new TextField("Movie name");
        name.setId("movie-name");
        name.setWidth("100%");
        name.setRequired(true);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(name);

        type = new ComboBox<>("Movie type");
        type.setId("movie-type");
        type.setWidth("100%");
        type.setRequired(true);
        type.setItems(MovieType.values());
        type.setItemLabelGenerator(MovieType::getTextualRepresentation);
        content.add(type);

        stockCount = new TextField("In stock");
        stockCount.setId("stock-count");
        stockCount.setWidth("100%");
        stockCount.setRequired(true);
        stockCount.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        stockCount.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(stockCount);

        // Binding field to domain
        binder = new Binder<>(Movie.class);
        binder.forField(name)
                .bind("name");
        binder.forField(type)
                .bind("type");
        binder.forField(stockCount).withConverter(new StockCountConverter())
                .bind("stockCount");

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
        });

        save = new Button("Save");
        save.setId("save");
        save.setWidth("100%");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentMovie != null) {
                Text text = new Text("");
                NativeButton buttonInside = new NativeButton("OK");
                Notification notification = new Notification(text, buttonInside);
                buttonInside.addClickListener(event1 -> notification.close());
                notification.setPosition(Notification.Position.TOP_END);

                try {
                    int x = Integer.parseInt(stockCount.getValue());
                    if (x < 0) {
                        throw new Exception();
                    } else if (type.isEmpty()) {
                        text.setText("Please select movie type! ");
                        notification.open();
                    } else if (CurrentDatabase.get().getMovieTable().getAll().stream()
                            .anyMatch(movie -> name.getValue().equals(movie.getName()))) {
                        text.setText("A movie with that name already exists in database! ");
                        notification.open();
                    } else {
                        binder.writeBeanIfValid(currentMovie);
                        viewLogic.saveMovie(currentMovie);
                    }
                } catch (Exception e) {
                    text.setText("Please make sure that stock count value is integer and not negative! ");
                    notification.open();
                }
            }
        });

        cancel = new Button("Cancel");
        cancel.setId("cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelMovie());
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelMovie())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setId("delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentMovie != null) {
                viewLogic.deleteMovie(currentMovie);
            }
        });

        content.add(save, delete, cancel);
    }

    public void editMovie(Movie movie) {
        if (movie == null) {
            movie = new Movie();
        }
        currentMovie = movie;
        binder.readBean(movie);

        delete.setEnabled(true);
        if (!currentMovie.isNewObject()) {
            for (RentOrder order : CurrentDatabase.get().getOrderTable().getAll()) {
                if (order.getItems().stream().anyMatch(item -> item.getMovie().getId() == currentMovie.getId()))
                    delete.setEnabled(false);
            }
        } else delete.setEnabled(false);
    }

    private static class StockCountConverter extends StringToIntegerConverter {

        public StockCountConverter() {
            super(0, "Could not convert value to " + Integer.class.getName()
                    + ".");
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            format.setDecimalSeparatorAlwaysShown(false);
            format.setParseIntegerOnly(true);
            format.setGroupingUsed(false);
            return format;
        }
    }
}
