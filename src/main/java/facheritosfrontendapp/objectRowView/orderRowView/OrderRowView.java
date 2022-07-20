package facheritosfrontendapp.objectRowView.orderRowView;
import facheritosfrontendapp.views.Main;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class OrderRowView {

    private Integer idOrder;

    private String workerName;

    private String seat;

    private String idCustomer;

    private String dueDate;

    private String status;

    private VBox options;

    public OrderRowView(Integer idOrder, String workerName, String seat, String idCustomer, Date dueDate, String status){
        this.idOrder = idOrder;
        this.workerName = workerName;
        this.seat = seat;
        this.idCustomer = idCustomer;
        this.dueDate = dueDate.toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        this.status = status;
        this.options = new VBox();
        setOptions();
        getOptionsLabel().setCursor(Cursor.HAND); //Cursor hand when mouse hover
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.seat = workerName;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VBox getOptions() {
        return options;
    }

    public HBox getOptionsHBox(){
        return (HBox) options.getChildren().get(0);
    }

    public Label getOptionsLabel(){
        return (Label) getOptionsHBox().getChildren().get(1);
    }

    public void setOptions() {
        URL iconEditURL = Main.class.getResource("/facheritosfrontendapp/icons/icon-edit.png");
        Image editImage = new Image(String.valueOf(iconEditURL));
        HBox hboxOptions = new HBox(new ImageView(editImage), new Label("Ver más"));
        hboxOptions.setSpacing(5);
        hboxOptions.setAlignment(Pos.CENTER);
        options.getChildren().add(hboxOptions);
        options.setAlignment(Pos.CENTER);
    }
}
