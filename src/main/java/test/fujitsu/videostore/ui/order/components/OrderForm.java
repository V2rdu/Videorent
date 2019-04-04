package test.fujitsu.videostore.ui.order.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;

import test.fujitsu.videostore.backend.database.DBTableRepository;
import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.ui.database.CurrentDatabase;
import test.fujitsu.videostore.ui.order.OrderListLogic;

public class OrderForm extends Div {

    private VerticalLayout content;

    private ComboBox<Customer> customerComboBox;
    private DatePicker orderDate;
    private OrderedVideos orderedVideos;

    private Button save;
    private Button cancel;
    private Button delete;
    private Button returnButton;

    private OrderListLogic viewLogic;
    private Binder<RentOrder> binder;
    private RentOrder currentOrder;

    public OrderForm(OrderListLogic orderListLogic) {
        setId("edit-form");
        setSizeFull();

        content = new VerticalLayout();
        content.setId("order-list-form-container");
        content.setSizeUndefined();
        content.setMargin(false);

        add(content);

        viewLogic = orderListLogic;

        customerComboBox = new ComboBox<>("Customer");
        customerComboBox.setId("customer");
        customerComboBox.setWidth("100%");
        customerComboBox.setRequired(true);
        customerComboBox.setItems(CurrentDatabase.get().getCustomerTable().getAll());
        customerComboBox.setItemLabelGenerator(Customer::getName);
        content.add(customerComboBox);

        orderDate = new DatePicker("Order date");
        orderDate.setId("order-date");
        orderDate.setWidth("100%");
        orderDate.setReadOnly(true);
        orderDate.setVisible(false);
        content.add(orderDate);

        orderedVideos = new OrderedVideos();
        content.add(orderedVideos);

        binder = new Binder<>(RentOrder.class);
        binder.forField(customerComboBox)
                .asRequired()
                .bind("customer");
        binder.forField(orderDate)
                .bind("orderDate");
        binder.forField(orderedVideos)
                .withValidator(items -> items != null && items.size() > 0, "Add at least one movie")
                .bind("items");

        save = new Button();
        save.setId("save");
        save.setWidth("100%");
        save.setDisableOnClick(false);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            BinderValidationStatus<RentOrder> validationStatus = binder.validate();
            if (validationStatus.hasErrors()) {
                ValidationResult firstError = validationStatus.getValidationErrors().iterator().next();
                Notification.show(firstError.getErrorMessage(), 5000, Notification.Position.MIDDLE);
                return;
            }

            binder.writeBeanIfValid(currentOrder);
            if (currentOrder.isNewObject() && currentOrder.getCustomer().getPoints() >= currentOrder.getItems().stream().filter(RentOrder.Item::isPaidByBonus).mapToInt(RentOrder.Item::getDays).sum() * 25) {
                new ReceiptWindow(viewLogic.getOrderToReceiptService().convertRentOrderToReceipt(currentOrder).print(), currentOrder.isNewObject(), () -> {
                    DBTableRepository<Customer> customers = CurrentDatabase.get().getCustomerTable();
                    Customer updated = currentOrder.getCustomer();
                    updated.setPoints(viewLogic.getOrderToReceiptService().convertRentOrderToReceipt(currentOrder).getRemainingBonusPoints());
                    customers.createOrUpdate(updated);

                    viewLogic.saveOrder(currentOrder);
                });
            } else if (currentOrder.isNewObject()) {
                Text text = new Text("Not enough bonus points! ");
                NativeButton buttonInside = new NativeButton("OK");
                Notification notification = new Notification(text, buttonInside);
                buttonInside.addClickListener(event1 -> notification.close());
                notification.setPosition(Notification.Position.TOP_END);
                notification.open();
            } else if (!currentOrder.isNewObject()) {
                new ReceiptWindow(viewLogic.getOrderToReceiptService().convertRentOrderToReceipt(currentOrder).print(), currentOrder.isNewObject(), () -> viewLogic.saveOrder(currentOrder));
            }
        });

        cancel = new Button("Cancel");
        cancel.setId("cancel");
        cancel.setWidth("100%");
        cancel.addClickListener(event -> viewLogic.cancelOrder());
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelOrder())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setId("delete");
        delete.setWidth("100%");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentOrder != null) {
                viewLogic.deleteOrder(currentOrder);
            }
        });

        returnButton = new Button("Return movies");
        returnButton.setId("return");
        returnButton.setWidth("100%");
        returnButton.addClickListener(event -> {
            ReturnMovieWindow returnMovieWindow = new ReturnMovieWindow(currentOrder, orderListLogic.getOrderToReceiptService(), viewLogic.getRepository(), () -> viewLogic.editOrder(currentOrder));
            returnMovieWindow.open();
        });

        content.add(save, returnButton, delete, cancel);
    }

    public void editOrder(RentOrder order) {
        boolean isNew = order.isNewObject();
        if (isNew) {
            order = new RentOrder();
            orderedVideos.setReadOnly(false);
            delete.setVisible(false);
            returnButton.setVisible(false);
        } else {
            orderedVideos.setReadOnly(true);
            delete.setVisible(true);
            returnButton.setVisible(true);
        }

        setSaveButtonCaption(!isNew);
        currentOrder = order;
        save.setEnabled(true);
        binder.readBean(currentOrder);
        binder.setReadOnly(!isNew);
        orderDate.setVisible(!isNew);
        orderDate.setReadOnly(true);

        returnButton.setEnabled(false);
        if (!currentOrder.isNewObject()) {
            delete.setEnabled(true);
            for (RentOrder.Item item : currentOrder.getItems()) {
                if (item.getReturnedDay() == null) {
                    returnButton.setEnabled(true);
                    delete.setEnabled(false);
                }
            }
        }
    }

    private void setSaveButtonCaption(boolean isReadOnly) {
        save.setText(isReadOnly ? "View receipt" : "Review and Print receipt");
    }

    public RentOrder getCurrentOrder() {
        return currentOrder;
    }
}
