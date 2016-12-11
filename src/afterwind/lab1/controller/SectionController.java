package afterwind.lab1.controller;

import afterwind.lab1.Utils;
import afterwind.lab1.entity.Section;
import afterwind.lab1.exception.ValidationException;
import afterwind.lab1.service.SectionService;
import afterwind.lab1.ui.control.StateButton;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


public class SectionController {

    @FXML
    public Button buttonAdd, buttonUpdate, buttonClear, buttonDelete, buttonRefresh, buttonSave, clearFilterButton;
    @FXML
    public TextField nameFilterTextField, nrLocFilterTextField;
    @FXML
    public StateButton<String> nrLocState;
    @FXML
    private TableView<Section> tableView;
    @FXML
    private TableColumn<Section, String> idColumn, nameColumn, nrLocColumn;
    @FXML
    private TextField nameTextField, nrLocTextField;

    private Predicate<Section> filter = (s) -> true;
    private Predicate<Integer> compTester = (diff) -> diff < 0;

    private SectionService service;
    private ObservableList<Section> model;

    /**
     * Constructor pentru controllerul de sectiuni
     */
    public SectionController() { }

    /**
     * Afiseaza toate sectiunile
     */
    public void showAll() {
        tableView.setItems(FXCollections.observableArrayList(service.filter(filter)));
    }

    /**
     * Seteaza service-ul
     * @param service service-ul
     */
    public void setService(SectionService service) {
        this.service = service;
        this.model = service.getRepo().getData();
        showAll();
    }

    /**
     * Sterge textul din fiecare TextField
     */
    public void clearTextFields() {
        nameTextField.setText("");
        nrLocTextField.setText("");
        tableView.getSelectionModel().clearSelection();
    }

    private void clearFilterTextFields() {
        nameFilterTextField.setText("");
        nrLocFilterTextField.setText("");
        tableView.getSelectionModel().clearSelection();
    }

    /**
     * Verifica datele din TextField-uri
     * @param name Numele sectiunii
     * @param nrLoc Numarul de locuri in acea sectiune
     * @return daca datele sunt valide
     */
    public boolean checkFields(String name, String nrLoc) {
        boolean errored = false;
        if (name.equals("")) {
            Utils.setErrorBorder(nameTextField);
            errored = true;
        }
        if (!Utils.tryParseInt(nrLoc)) {
            Utils.setErrorBorder(nrLocTextField);
            errored = true;
        }
        return errored;
    }

    /**
     * Afiseaza detalii despre o sectiune
     * @param s Sectiunea
     */
    public void showDetails(Section s) {
        nameTextField.setText(s.getName());
        nrLocTextField.setText(s.getNrLoc() + "");
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nrLocColumn.setCellValueFactory(new PropertyValueFactory<>("nrLoc"));
        tableView.getSelectionModel().selectedItemProperty().addListener(this::handleSelectionChanged);

        nrLocState.addState("<");
        nrLocState.addState("≤");
        nrLocState.addState("=");
        nrLocState.addState("≥");
        nrLocState.addState(">");

        nameFilterTextField.textProperty().addListener(this::updateFilter);
        nrLocFilterTextField.textProperty().addListener(this::updateFilter);
    }

    /**
     * Apelat daca selectia se modifica
     * @param oldValue valoarea veche
     * @param newValue valoarea noua
     */
    public void handleSelectionChanged(ObservableValue<? extends Section> o, Section oldValue, Section newValue) {
        if (newValue != null) {
            showDetails(newValue);
        }
    }

    /**
     * Apelat cand se apasa pe butonul Add
     * @param ev evenimentul
     */
    public void handleAdd(ActionEvent ev) {
        String name = nameTextField.getText();
        String nrLocString = nrLocTextField.getText();
        if (checkFields(name, nrLocString)) {
            return;
        }
        int nrLoc = Integer.parseInt(nrLocString);
        int id = service.getNextId();
        try {
            Section s = new Section(id, name, nrLoc);
            service.add(s);
            if (filter.test(s)) {
                tableView.getItems().add(s);
            }
        } catch (ValidationException e) {
            Utils.showErrorMessage(e.getMessage());
        }
    }

    /**
     * Apelat cand se apasa pe butonul Delete
     * @param ev evenimentul
     */
    public void handleDelete(ActionEvent ev) {
        Section s = tableView.getSelectionModel().getSelectedItem();
        if (s == null) {
            Utils.showErrorMessage("Nu a fost selectata nicio sectie!");
            return;
        }
        service.remove(s);
        clearTextFields();
    }

    public void handleUpdate(ActionEvent actionEvent) {
        Section s = tableView.getSelectionModel().getSelectedItem();
        if (s == null) {
            Utils.showErrorMessage("Nu a fost selectata nicio sectie!");
            return;
        }
        String name = nameTextField.getText();
        String nrLocString = nrLocTextField.getText();
        if (checkFields(name, nrLocString)) {
            return;
        }
        int nrLoc = Integer.parseInt(nrLocString);
        service.updateSection(s, name, nrLoc);
        for (int i = 0; i < 3; i++) {
            tableView.getColumns().get(i).setVisible(false);
            tableView.getColumns().get(i).setVisible(true);
        }
    }

    /**
     * Apelat cand se apasa pe butonul Clear
     * @param ev evenimentul
     */
    public void handleClear(ActionEvent ev) {
        clearTextFields();
    }

    /**
     * Apelat cand se apasa pe butonul Refresh
     * @param ev evenimentul
     */
    public void handleRefresh(ActionEvent ev) {
        showAll();
    }

    /**
     * Apelat cand se apasa pe butonul Save
     * @param ev evenimentul
     */
    public void handleSave(ActionEvent ev) {
        service.getRepo().updateLinks();
        Utils.showInfoMessage("Totul a fost salvat in fisier!");
    }

    public void updateFilter(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        boolean filtered = false;
        filter = (c) -> true;
        if (!nameFilterTextField.getText().equals("")) {
            filter = filter.and((s) -> s.getName().toLowerCase().startsWith(nameFilterTextField.getText().toLowerCase()));
            filtered = true;
        }
        if (!nrLocFilterTextField.getText().equals("") && Utils.tryParseInt(nrLocFilterTextField.getText())) {
            int actualNrLoc = Integer.parseInt(nrLocFilterTextField.getText());
            filter = filter.and((s) -> compTester.test(s.getNrLoc() - actualNrLoc));
            filtered = true;
        }

        List<Section> filteredList = service.filter(filter);
        tableView.setItems(FXCollections.observableArrayList(filteredList));
        clearFilterButton.setDisable(!filtered);
    }

    public void handleClearFilter(ActionEvent ev) {
        tableView.setItems(model);
        clearFilterTextFields();
        clearFilterButton.setDisable(true);
    }

    public void handleStateChange(ActionEvent ev) {
        nrLocState.changeState();
        switch (nrLocState.getState()) {
            case "<":
                compTester = (diff) -> diff < 0;
                break;
            case "≤":
                compTester = (diff) -> diff <= 0;
                break;
            case "=":
                compTester = (diff) -> diff == 0;
                break;
            case "≥":
                compTester = (diff) -> diff >= 0;
                break;
            case ">":
                compTester = (diff) -> diff > 0;
                break;
        }
        updateFilter(null, null, null);
    }
}
