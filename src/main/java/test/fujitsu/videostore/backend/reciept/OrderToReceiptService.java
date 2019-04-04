package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple receipt creation service
 * <p>
 * Note! All calculations should be in another place. Here we just setting already calculated data. Feel free to refactor.
 */
public class OrderToReceiptService {

    /**
     * Converts rent order to printable receipt
     *
     * @param order rent object
     * @return Printable receipt object
     */
    public PrintableOrderReceipt convertRentOrderToReceipt(RentOrder order) {
        PrintableOrderReceipt printableOrderReceipt = new PrintableOrderReceipt();
        Calculator calculator = new Calculator();

        printableOrderReceipt.setOrderId(order.isNewObject() ? "new" : Integer.toString(order.getId()));
        printableOrderReceipt.setOrderDate(order.getOrderDate());
        printableOrderReceipt.setCustomerName(order.getCustomer().getName());

        List<PrintableOrderReceipt.Item> itemList = new ArrayList<>();
        printableOrderReceipt.setOrderItems(itemList);

        int total = 0;
        int totalBonusPointsUsed = 0;
        for (RentOrder.Item orderItem : order.getItems()) {
            PrintableOrderReceipt.Item item = new PrintableOrderReceipt.Item();
            item.setDays(orderItem.getDays());
            item.setMovieName(orderItem.getMovie().getName());
            item.setMovieType(orderItem.getMovieType());

            if (orderItem.isPaidByBonus()) {
                int price = calculator.orderItemPaidBonus(orderItem);
                item.setPaidBonus(price);
                totalBonusPointsUsed += price;
            } else {
                int price = calculator.orderItemPriceSum(orderItem, orderItem.getDays());
                item.setPaidMoney(BigDecimal.valueOf(price));
                total += price;
            }

            itemList.add(item);
        }

        printableOrderReceipt.setTotalPrice(BigDecimal.valueOf(total));

        printableOrderReceipt.setRemainingBonusPoints(order.isNewObject() ? order.getCustomer().getPoints() - totalBonusPointsUsed : order.getCustomer().getPoints());

        return printableOrderReceipt;
    }

    /**
     * Converts return order to printable receipt
     *
     * @param order return object
     * @return Printable receipt object
     */
    public PrintableReturnReceipt convertRentOrderToReceipt(ReturnOrder order) {
        PrintableReturnReceipt receipt = new PrintableReturnReceipt();
        Calculator calculator = new Calculator();

        receipt.setOrderId(Integer.toString(order.getRentOrder().getId()));
        receipt.setCustomerName(order.getRentOrder().getCustomer().getName());
        receipt.setRentDate(order.getRentOrder().getOrderDate());
        receipt.setReturnDate(order.getReturnDate());

        List<PrintableReturnReceipt.Item> returnedItems = new ArrayList<>();
        int totalExtraCharge = 0;
        if (order.getItems() != null) {
            for (RentOrder.Item rentedItem : order.getItems()) {
                PrintableReturnReceipt.Item item = new PrintableReturnReceipt.Item();
                item.setMovieName(rentedItem.getMovie().getName());
                item.setMovieType(rentedItem.getMovieType());
                item.setExtraDays(calculator.calculateExtraDays(order, rentedItem));

                int extraCharge = calculator.orderItemPriceSum(rentedItem, item.getExtraDays() + rentedItem.getDays()) - calculator.orderItemPriceSum(rentedItem, rentedItem.getDays());
                item.setExtraPrice(BigDecimal.valueOf(extraCharge));
                totalExtraCharge += extraCharge;

                returnedItems.add(item);
            }
        }
        receipt.setReturnedItems(returnedItems);

        receipt.setTotalCharge(BigDecimal.valueOf(totalExtraCharge));

        return receipt;
    }

}
