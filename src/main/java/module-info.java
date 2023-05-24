module com.example.atm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;


    opens com.example.atm to javafx.fxml;
    exports com.example.atm;
}