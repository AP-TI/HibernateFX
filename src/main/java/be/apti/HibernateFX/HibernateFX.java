package be.apti.HibernateFX;

import be.apti.HibernateFX.model.Laptop;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HibernateFX extends Application {
    private static SessionFactory factory;


    @Override
    public void init() throws Exception {
        try {
            factory = new Configuration().configure().addPackage("be.apti.HibernateFX").addAnnotatedClass(Laptop.class).buildSessionFactory();
        } catch (HibernateException exception) {
            throw new ExceptionInInitializerError(exception);
        }
        addData(new Laptop("MSI", "Die van de Kobe", LocalDate.of(2018, 04, 02)));
        addData(new Laptop("Apple", "Die van Turpal", LocalDate.of(2018, 04, 02)));
        addData(new Laptop("Apple", "MacBook Pro van Maxim", LocalDate.of(2018, 04, 02)));
        getAllLaptops().forEach(System.out::println);
        //deleteLaptopByType("MacBook Pro van Maxim");
        updateLaptopByType("MacBook Pro van Maxim", "MBP van Maxim");
        getAllLaptops().forEach(System.out::println);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane gridPane = new GridPane();
        javafx.scene.control.Button button = new javafx.scene.control.Button("Zoek");
        button.setPrefWidth(200.0);
        javafx.scene.control.TextField textField = new TextField();
        textField.setPromptText("Zoeken");
        gridPane.add(textField, 1, 1);
        ListView<Laptop> listView = new ListView<Laptop>();



        listView.setPrefWidth(500);
        gridPane.add(listView, 0, 0);


        button.setOnAction(actionEvent -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Geklikt");
//            alert.setHeaderText("U heeft gezocht");
            List<Laptop> result = getLaptopByVendor(textField.getText());
            ObservableList<Laptop> observableList = FXCollections.observableList(result);
            listView.setItems(observableList);
            ComboBox comboBox = new ComboBox();
            comboBox.setItems(observableList);
            comboBox.valueProperty().addListener((ChangeListener<Laptop>) (observableValue, oud, nieuw) -> {
                ObservableList<Laptop> searchResult = FXCollections.observableList(List.of(nieuw));
                listView.setItems(searchResult);
            });
            gridPane.add(comboBox, 10, 10);
//            StringBuilder stringBuilder = new StringBuilder();
//            result.forEach(laptop -> {
//                stringBuilder.append(laptop.toString() + "\n");
//            });
//            System.out.println(result);
//            alert.setContentText(stringBuilder.toString());
//            alert.show();
        });
        gridPane.add(button, 1, 2);
        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane, 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("Hallo wereld!");
        stage.show();
    }

    private static void addData(Laptop laptop) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(laptop);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static List<Laptop> getAllLaptops() {
        List<Laptop> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);
                criteriaQuery.from(Laptop.class);
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }

    private static List<Laptop> getLaptopByVendor(String vendor) {
        List<Laptop> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("vendor"), vendor));
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }

    private static void deleteLaptopByType(String type) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("type"), type));
                session.createQuery(criteriaQuery).getResultList().forEach(session::delete);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static void updateLaptopByType(String type, String newType) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("type"), type));
                session.createQuery(criteriaQuery).getResultList().forEach(laptop -> {
                    laptop.setType(newType);
                    session.update(laptop);
                });
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
    }
}
