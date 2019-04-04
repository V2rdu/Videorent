package test.fujitsu.videostore.ui.customer.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;

import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.ui.customer.CustomerListLogic;
import test.fujitsu.videostore.ui.database.CurrentDatabase;

/**
 * Customer edit/creation form
 */
public class CustomerForm extends Div {

    private VerticalLayout content;

    private TextField name;
    private TextField points;
    private Button save;
    private Button cancel;
    private Button delete;

    private CustomerListLogic viewLogic;
    private Binder<Customer> binder;
    private Customer currentCustomer;

    public CustomerForm(CustomerListLogic customerListLogic) {
        setId("edit-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        viewLogic = customerListLogic;

        name = new TextField("Customer name");
        name.setId("customer-name");
        name.setWidth("100%");
        name.setRequired(true);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(name);

        points = new TextField("Bonus points");
        points.setId("bonus-points");
        points.setWidth("100%");
        points.setRequired(true);
        points.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(points);

        binder = new Binder<>(Customer.class);
        binder.forField(name)
                .bind("name");
        binder.forField(points)
                .withConverter(new StringToIntegerConverter("Invalid bonus points format"))
                .bind("points");

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
            if (currentCustomer != null) {
                Text text = new Text("");
                NativeButton buttonInside = new NativeButton("OK");
                Notification notification = new Notification(text, buttonInside);
                buttonInside.addClickListener(event1 -> notification.close());
                notification.setPosition(Notification.Position.TOP_END);

                try {
                    int x = Integer.parseInt(points.getValue());
                    if (x <= 0) {
                        throw new Exception();
                    } else if (name.getValue().equals("")) {
                        text.setText("Name field can not be empty! ");
                        notification.open();
                    } else if (CurrentDatabase.get().getCustomerTable().getAll().stream()
                            .anyMatch(customer -> name.getValue().equals(customer.getName()))) {
                        text.setText("A customer with that name already exists in database! ");
                        notification.open();
                    } else {
                        binder.writeBeanIfValid(currentCustomer);
                        viewLogic.saveCustomer(currentCustomer);
                    }
                } catch (Exception e) {
                    text.setText("Please make sure that points value is integer and not negative! ");
                    notification.open();
                }
            }
        });

        cancel = new Button("Cancel");
        cancel.setWidth("100%");
        cancel.setId("cancel");
        cancel.addClickListener(event -> viewLogic.cancelCustomer());
        getElement()
                .addEventListener("keydown", event -> viewLogic.cancelCustomer())
                .setFilter("event.key == 'Escape'");

        delete = new Button("Delete");
        delete.setWidth("100%");
        delete.setId("delete");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentCustomer != null) {
                viewLogic.deleteCustomer(currentCustomer);
            }
        });

        content.add(save, delete, cancel);
    }

    public void editCustomer(Customer customer) {
        if (customer == null) {
            customer = new Customer();
        }
        currentCustomer = customer;
        binder.readBean(customer);

        delete.setEnabled(true);
        if (currentCustomer.isNewObject() ||
                (!currentCustomer.isNewObject() &&
                        CurrentDatabase.get().getOrderTable().getAll().stream()
                                .anyMatch(rentOrder -> rentOrder.getCustomer().getId() == currentCustomer.getId()) &&
                        CurrentDatabase.get().getOrderTable().getAll().stream()
                                .filter(rentOrder -> rentOrder.getCustomer().getId() == currentCustomer.getId())
                                .anyMatch(rentOrder -> rentOrder.getItems().stream().anyMatch(item -> item.getReturnedDay() == null)))) {
            delete.setEnabled(false);
        }
    }
}
