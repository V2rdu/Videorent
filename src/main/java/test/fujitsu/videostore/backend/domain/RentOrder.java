package test.fujitsu.videostore.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import test.fujitsu.videostore.backend.domain.jsonDeserializers.LocalDateDeserializer;
import test.fujitsu.videostore.backend.domain.jsonDeserializers.MovieTypeDeserializer;
import test.fujitsu.videostore.backend.domain.jsonSerializers.CustomerSerializer;
import test.fujitsu.videostore.backend.domain.jsonSerializers.LocalDateSerializer;
import test.fujitsu.videostore.backend.domain.jsonSerializers.MovieSerializer;
import test.fujitsu.videostore.backend.domain.jsonSerializers.MovieTypeSerializer;

import java.time.LocalDate;
import java.util.List;

/**
 * One rent by customer
 */
@JsonPropertyOrder({"id", "customer", "orderDate", "items"})
public class RentOrder {

    /**
     * Rent ID
     */
    private int id = -1;

    /**
     * Customer
     */
    @JsonSerialize(using = CustomerSerializer.class)
    private Customer customer;

    /**
     * Rent date
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate orderDate = LocalDate.now();

    /**
     * List of rented movies
     */
    private List<Item> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonSerialize(using = CustomerSerializer.class)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * New object for database or not
     *
     * @return boolean
     */
    @JsonIgnore
    public boolean isNewObject() {
        return id == -1;
    }

    /**
     * Rented movie entry
     */
    @JsonPropertyOrder({"movie", "type", "paidByBonus", "days", "returnedDay"})
    public static class Item {
        /**
         * Selected movie
         */

        @JsonSerialize(using = MovieSerializer.class)
        private Movie movie;

        /**
         * Movie type on a moment of renting
         */
        @JsonDeserialize(using = MovieTypeDeserializer.class)
        @JsonSerialize(using = MovieTypeSerializer.class)
        private MovieType movieType;

        /**
         * Number of renting days
         */
        private int days;

        /**
         * Paid by bonus points
         */
        private boolean paidByBonus;

        /**
         * Return date. NULL if not returned yet.
         */
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        private LocalDate returnedDay;

        @JsonIgnore
        public Movie getMovie() {
            return movie;
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
        }

        public MovieType getMovieType() {
            return movieType;
        }

        @JsonSetter("type")
        public void setMovieType(MovieType movieType) {
            this.movieType = movieType;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        @JsonDeserialize(using = LocalDateDeserializer.class)
        public LocalDate getReturnedDay() {
            return returnedDay;
        }

        @JsonSerialize(using = LocalDateSerializer.class)
        public void setReturnedDay(LocalDate returnedDay) {
            this.returnedDay = returnedDay;
        }

        public boolean isPaidByBonus() {
            return paidByBonus;
        }

        public void setPaidByBonus(boolean paidByBonus) {
            this.paidByBonus = paidByBonus;
        }
    }
}
