package test.fujitsu.videostore.backend.reciept;


import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import static java.time.temporal.ChronoUnit.DAYS;

public class Calculator {

    public Calculator() {
    }

    public int orderItemPriceSum(RentOrder.Item orderItem, int days) {
        int price = 0;
        MovieType movieType = orderItem.getMovieType();
        int PREMIUM_PRICE = 4;
        int BASIC_PRICE = 3;
        switch (movieType) {
            case NEW:
                price = PREMIUM_PRICE * days;
                break;
            case REGULAR:
                price = calculatePriceMovieType(BASIC_PRICE, days, 3);
                break;
            case OLD:
                price = calculatePriceMovieType(BASIC_PRICE, days, 5);
                break;
        }
        return price;
    }

    public int orderItemPaidBonus(RentOrder.Item orderItem) {
        return orderItem.getDays() * 25;
    }

    public int calculateExtraDays(ReturnOrder order, RentOrder.Item item) {
        int daysBetween = (int) DAYS.between(order.getRentOrder().getOrderDate(), order.getReturnDate());
        if (daysBetween > item.getDays()) {
            return daysBetween - item.getDays();
        }
        return 0;
    }

    private int calculatePriceMovieType(int priceType, int days, int limit) {
        int price = 0;
        for (int i = 1; i <= days; i++) {
            if (i <= limit) price += priceType;
            else {
                price += i * priceType;
            }
        }
        return price;
    }
}
