package com.trainBooking;
/*I declare that my work contains no examples of misconduct, such as plagiarism, or collusion.
Any code taken from other sources is referenced within my code solution.
Student ID: W1761197
Student IIT ID: 2019166
Date: 2020/03/19 */

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Main extends Application {
    private static final int SEAT_CAPACITY = 42;
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        launch();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String[][][][] mainArray = new String[2][30][SEAT_CAPACITY][3]; //create 4D array.[[],[],[].........[]].
        System.out.println("***********************************");
        System.out.println("*          WELCOME TO             *");
        System.out.println("*       Denuwara Manike           *");
        System.out.println("*        Express Train            *");
        System.out.println("***********************************");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n---------------------------------------");
            System.out.println("Enter \"A\" to add a customer");
            System.out.println("Enter \"V\" to view all the seats");
            System.out.println("Enter \"E\" to view empty seats ");
            System.out.println("Enter \"D\" to delete a booked seats");
            System.out.println("Enter \"F\" find a seat by customer");
            System.out.println("Enter \"O\" Sort the seats");
            System.out.println("Enter \"Q\" to quit the program");
            System.out.println("Enter \"S\" Save data in database");
            System.out.println("Enter \"L\" Load data from database");
            System.out.println("---------------------------------------");
            String option = sc.next();
            switch (option) {
                case "A":
                case "a":
                    addCustomer(mainArray);
                    break;

                case "V":
                case "v":
                    viewAllSeats(mainArray);
                    break;

                case "E":
                case "e":
                    viewEmptySeats(mainArray);
                    break;

                case "D":
                case "d":
                    deleteBookedSeats(mainArray);
                    break;

                case "S":
                case "s":
                    saveData(mainArray);
                    break;

                case "L":
                case "l":
                    loadData(mainArray);
                    break;
                case "O":
                case "o":
                    sortbyName(mainArray);
                    break;
                case "F":
                case "f":
                    findSeatByCustomer(mainArray);
                    break;

                case "Q":
                case "q":
                    quit();
                    break;

                default:
                    System.out.println("Invalid input..Try again....");
            }
        }
    }

    //Add customer
    private static String[][][][] addCustomer(String[][][][] mainArray) {
        System.out.println("\n************************************************************");
        System.out.println("                        Add Customer                          ");
        System.out.println("************************************************************\n");
        ArrayList<Integer> temp = new ArrayList(); //tempory array list
        Scanner sc = new Scanner(System.in);
        System.out.println("\n---------Please enter your first name:---------");
        String fname = nameValidate(scan);
        System.out.println("\n--------Please enter your second name:--------");
        String sname = nameValidate(scan);

        System.out.println("\n***Please select Date and Destination from the GUI***");
        // That GUI stage for select date and destination
        Stage stageForDate = new Stage();
        AnchorPane AnchorPaneforDate = new AnchorPane();
        DatePicker datePicker = new DatePicker();
        Button button = new Button("Confirm");
        RadioButton C2B = new RadioButton("Colombo to Badulla");
        RadioButton B2C = new RadioButton("Badulla to Colombo");
        ToggleGroup radioGroup = new ToggleGroup();
        C2B.setToggleGroup(radioGroup);
        B2C.setToggleGroup(radioGroup);
        Label ForDate = new Label("Please Select your Date");
        Label ForDestination = new Label("Select your Destination");
        Alert messageBox = new Alert(Alert.AlertType.NONE);  //https://www.geeksforgeeks.org/javafx-alert-with-examples/
        int[] destination = new int[1];
        int[] date = new int[1];
        LocalDate lastDate = LocalDate.now().plusDays(29); //set Disable datepicker for month
        LocalDate currentDate = LocalDate.now().plusDays(1);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(lastDate) || date.isBefore(currentDate));  //https://stackoverflow.com/questions/28914579/setdisable-inside-datepickers-setdaycellfactory-not-working-if-hijrahchronolo
            }
        });

        button.setOnAction(action -> {
            LocalDate selected = datePicker.getValue();
            if (C2B.isSelected()) {
                destination[0] = 1;
            } else if (B2C.isSelected()) {
                destination[0] = 2;
            }
            if (selected == null || destination[0] == 0) {
                messageBox.setAlertType(Alert.AlertType.ERROR);
                messageBox.setContentText("Please Select the Date and Destination");
                messageBox.show();
            } else {
                date[0] = Period.between(LocalDate.now(), selected).getDays();  //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/DatePicker.html
                stageForDate.close();
            }
        });

        datePicker.setLayoutX(91);
        datePicker.setLayoutY(82);
        C2B.setLayoutX(82);
        C2B.setLayoutY(240);
        B2C.setLayoutX(82);
        B2C.setLayoutY(282);
        button.setLayoutX(169);
        button.setLayoutY(413);
        ForDate.setLayoutX(141);
        ForDate.setLayoutY(37);
        ForDestination.setLayoutX(132);
        ForDestination.setLayoutY(173);

        AnchorPaneforDate.getChildren().add(datePicker);
        AnchorPaneforDate.getChildren().add(C2B);
        AnchorPaneforDate.getChildren().add(B2C);
        AnchorPaneforDate.getChildren().add(button);
        AnchorPaneforDate.getChildren().add(ForDestination);
        AnchorPaneforDate.getChildren().add(ForDate);

        Scene sceneForDate = new Scene(AnchorPaneforDate, 400, 500);
        stageForDate.setScene(sceneForDate);
        stageForDate.showAndWait();

        //Seat booking GUI
        Stage stage = new Stage();
        stage.setTitle("Denuwara Manike Train Ticket Booking System");
        CheckBox[] checkBox = new CheckBox[42];
        GridPane gridPane = new GridPane();
        gridPane.setId("grid");

        Button btnbook = new Button("Book");
        gridPane.add(btnbook, 1, 8);
        btnbook.setPrefHeight(70.0);
        btnbook.setPrefWidth(120.0);

        Button btn_cancel = new Button("Cancel");
        gridPane.add(btn_cancel, 3, 8);
        btn_cancel.setPrefHeight(70.0);
        btn_cancel.setPrefWidth(120.0);
        btn_cancel.setStyle("-fx-background-color: #c70000");

        gridPane.setHgap(15);
        gridPane.setVgap(15);

        int count = 0;
        while (count < SEAT_CAPACITY) { //create the check boxes
            checkBox[count] = new CheckBox("seat " + (count + 1));
            checkBox[count].setId("UnSelectedSeats");
            checkBox[count].setPrefSize(195, 180);
            count++;
        }
        int num = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                gridPane.add(checkBox[num++], j, i);//allign the created checkboxes in GridPane.
            }
        }
        if (destination[0] == 1) {
            for (int j = 0; j < 42; j++) { //disable the checkbox
                if (mainArray[0][date[0] - 1][j][0] != null) {
                    checkBox[j].setMouseTransparent(true);
                    checkBox[j].setId("SelectedSeats");//to add images set the checkBox id's.
                }
            }
        } else if (destination[0] == 2) {
            for (int j = 0; j < 42; j++) { //disable the checkbox
                if (mainArray[1][date[0] - 1][j][0] != null) {
                    checkBox[j].setMouseTransparent(true);
                    checkBox[j].setId("SelectedSeats"); //to add images set the checkBox id's.
                }
            }
        }
        btnbook.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    for (int j = 0; j < 42; j++) {
                        if (checkBox[j].isSelected()) {
                            temp.add(j + 1); //add booking seat numbers to tempory variable.

                        }
                    }
                    if (destination[0] == 1) { //in that part check the user input trip
                        for (int i = 0; i < temp.size(); i++) {
                            mainArray[0][date[0] - 1][temp.get(i) - 1][0] = String.valueOf(date[0]);//append the values to the mainArray
                            mainArray[0][date[0] - 1][temp.get(i) - 1][1] = fname;
                            mainArray[0][date[0] - 1][temp.get(i) - 1][2] = sname;
                        }
                    } else if (destination[0] == 2) {
                        for (int i = 0; i < temp.size(); i++) {
                            mainArray[1][date[0] - 1][temp.get(i) - 1][0] = String.valueOf(date[0]);//append the values to the mainArray
                            mainArray[1][date[0] - 1][temp.get(i) - 1][1] = fname;
                            mainArray[1][date[0] - 1][temp.get(i) - 1][2] = sname;
                        }
                    }
                    stage.close();
                });
        btn_cancel.setOnAction(action -> { //close button
            stage.close();
        });
        Scene scene = new Scene(gridPane, 1100, 1000);
        scene.getStylesheets().add("com/trainBooking/Style.css"); //add image to the GUI which using CSS file.
        stage.setScene(scene);
        stage.showAndWait();
        System.out.println("\n--------Succefully booked your seats--------");
        System.out.println("\nPassenger Name : " + fname + "\nDate : " + datePicker.getValue());
        System.out.println("Your booked seats numbers are:" + Arrays.toString(temp.toArray()));//convert ArrayList for Array and print that tempory Array for prompt seat numbers
        //System.out.println(Arrays.deepToString(mainArray));
        return mainArray;
    }

    //name validations
    private static String nameValidate(Scanner scan) { // for validations
        while (!scan.hasNext()) {
            System.out.println("\n--------Please re enter your name.---------\n");
            scan.next();
        }
        return scan.next();
    }

    //View All seats
    private static void viewAllSeats(String[][][][] mainArray) {
        System.out.println("\n************************************************************");
        System.out.println("                     View Seats by date                       ");
        System.out.println("************************************************************\n");
        System.out.println("\n***Please select Date and Destination from the GUI***");
        // That GUI stage for select date and destination
        Stage stageForDate = new Stage();
        AnchorPane AnchorPaneforDate = new AnchorPane();
        DatePicker datePicker = new DatePicker();
        Button button = new Button("Confrim");
        RadioButton C2B = new RadioButton("Colombo to Badulla");
        RadioButton B2C = new RadioButton("Badulla to Colombo");
        ToggleGroup radioGroup = new ToggleGroup();
        C2B.setToggleGroup(radioGroup);
        B2C.setToggleGroup(radioGroup);
        Label ForDate = new Label("Please Select your Date");
        Label ForDestination = new Label("Select your Destination");
        Alert messageBox = new Alert(Alert.AlertType.NONE);
        int[] destination = new int[1];
        int[] date = new int[1];
        LocalDate lastDate = LocalDate.now().plusDays(29); //set Disable datepicker for month
        LocalDate currentDate = LocalDate.now().plusDays(1);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(lastDate) || date.isBefore(currentDate));  //https://stackoverflow.com/questions/28914579/setdisable-inside-datepickers-setdaycellfactory-not-working-if-hijrahchronolo
            }
        });

        button.setOnAction(action -> {
            LocalDate selected = datePicker.getValue();
            if (C2B.isSelected()) {
                destination[0] = 1;
                // System.out.println(C2B.getText());
            } else if (B2C.isSelected()) {
                destination[0] = 2;
                // System.out.println(B2C.getText());
            }
            if(datePicker.getValue() == null || destination[0] == 0){
                messageBox.setAlertType(Alert.AlertType.ERROR);
                messageBox.setContentText("Please Select the Date and Destination");
                messageBox.show();}
            else{
                date[0] = Period.between(LocalDate.now(),selected).getDays();
                stageForDate.close();}
        });
        datePicker.setLayoutX(91);
        datePicker.setLayoutY(82);
        C2B.setLayoutX(82);
        C2B.setLayoutY(240);
        B2C.setLayoutX(82);
        B2C.setLayoutY(282);
        button.setLayoutX(169);
        button.setLayoutY(413);
        ForDate.setLayoutX(141);
        ForDate.setLayoutY(37);
        ForDestination.setLayoutX(132);
        ForDestination.setLayoutY(173);

        AnchorPaneforDate.getChildren().add(datePicker);
        AnchorPaneforDate.getChildren().add(C2B);
        AnchorPaneforDate.getChildren().add(B2C);
        AnchorPaneforDate.getChildren().add(button);
        AnchorPaneforDate.getChildren().add(ForDestination);
        AnchorPaneforDate.getChildren().add(ForDate);

        Scene sceneForDate = new Scene(AnchorPaneforDate, 400, 500);
        stageForDate.setScene(sceneForDate);
        stageForDate.showAndWait();

        //Seat booking GUI
        Stage stage = new Stage();
        stage.setTitle("Denuwara Manike Train Seats View");
        CheckBox[] checkBox = new CheckBox[42];
        GridPane gridPane = new GridPane();

        gridPane.setHgap(15);
        gridPane.setVgap(15);

        int count = 0;
        while (count < SEAT_CAPACITY) {
            checkBox[count] = new CheckBox("seat " + (count + 1));
            checkBox[count].setId("UnSelectedSeats");
            checkBox[count].setPrefSize(195, 180);
            count++;
        }
        int num = 0;
        for (int i = 0; i < 7; i++) { //in the view method mouse will be transparent and it also can't select checkboxes in that stage.
            for (int j = 0; j < 6; j++) {
                checkBox[num].setMouseTransparent(true);
                gridPane.add(checkBox[num++], j, i);
            }
        }
        if (destination[0] == 1) {
            for (int j = 0; j < 42; j++) { //disable the checkbox
                if (mainArray[0][date[0] - 1][j][0] != null) {
                    checkBox[j].setId("SelectedSeats");
                }
            }
        } else if (destination[0] == 2) {
            for (int j = 0; j < 42; j++) { //disable the checkbox
                if (mainArray[1][date[0] - 1][j][0] != null) {
                    checkBox[j].setId("SelectedSeats");
                }
            }
        }
        Scene scene = new Scene(gridPane, 1100, 900);
        scene.getStylesheets().add("com/trainBooking/Style.css");
        stage.setScene(scene);
        stage.showAndWait();
    }

    //View empty seats and didn't show booked seats in this method.
    private void viewEmptySeats(String[][][][] mainArray) {
        System.out.println("\n***Please select Date and Destination from the GUI***");
        // That GUI stage for select date and destination
        Stage stageForDate = new Stage();
        AnchorPane AnchorPaneforDate = new AnchorPane();
        DatePicker datePicker = new DatePicker();
        Button button = new Button("Confrim");
        RadioButton C2B = new RadioButton("Colombo to Badulla");
        RadioButton B2C = new RadioButton("Badulla to Colombo");
        ToggleGroup radioGroup = new ToggleGroup();
        C2B.setToggleGroup(radioGroup);
        B2C.setToggleGroup(radioGroup);
        Label ForDate = new Label("Please Select your Date");
        Label ForDestination = new Label("Select your Destination");
        Alert messageBox = new Alert(Alert.AlertType.NONE);  //https://www.geeksforgeeks.org/javafx-alert-with-examples/
        int[] destination = new int[1];
        int[] date = new int[1];
        LocalDate lastDate = LocalDate.now().plusDays(29); //set Disable datepicker for month
        LocalDate currentDate = LocalDate.now().plusDays(1);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(lastDate) || date.isBefore(currentDate));  //https://stackoverflow.com/questions/28914579/setdisable-inside-datepickers-setdaycellfactory-not-working-if-hijrahchronolo
            }
        });

        button.setOnAction(action -> {
            LocalDate selected = datePicker.getValue();
            if (C2B.isSelected()) {
                destination[0] = 1;
                // System.out.println(C2B.getText());
            } else if (B2C.isSelected()) {
                destination[0] = 2;
                // System.out.println(B2C.getText());
            }
            if(selected == null || destination[0] == 0){
                messageBox.setAlertType(Alert.AlertType.ERROR);
                messageBox.setContentText("Please Select the Date and Destination");
                messageBox.show();}
            else{
                date[0] = Period.between(LocalDate.now(),selected).getDays();
                stageForDate.close();}
        });
        datePicker.setLayoutX(91);
        datePicker.setLayoutY(82);
        C2B.setLayoutX(82);
        C2B.setLayoutY(240);
        B2C.setLayoutX(82);
        B2C.setLayoutY(282);
        button.setLayoutX(169);
        button.setLayoutY(413);
        ForDate.setLayoutX(141);
        ForDate.setLayoutY(37);
        ForDestination.setLayoutX(132);
        ForDestination.setLayoutY(173);

        AnchorPaneforDate.getChildren().add(datePicker);
        AnchorPaneforDate.getChildren().add(C2B);
        AnchorPaneforDate.getChildren().add(B2C);
        AnchorPaneforDate.getChildren().add(button);
        AnchorPaneforDate.getChildren().add(ForDestination);
        AnchorPaneforDate.getChildren().add(ForDate);

        Scene sceneForDate = new Scene(AnchorPaneforDate, 400, 500);
        stageForDate.setScene(sceneForDate);
        stageForDate.showAndWait();
        //for seats
        Stage stage = new Stage();
        stage.setTitle("Denuwara Manike Train Seats View");
        CheckBox[] checkBox = new CheckBox[42];
        GridPane gridPane = new GridPane();

        gridPane.setHgap(15);
        gridPane.setVgap(15);

        int count = 0;
        while (count < SEAT_CAPACITY) {
            checkBox[count] = new CheckBox("seat " + (count + 1));
            checkBox[count].setId("UnSelectedSeats");
            checkBox[count].setGraphic(null);
            checkBox[count].setPrefSize(195, 180);
            count++;
        }
        int num = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                checkBox[num].setMouseTransparent(true);
                gridPane.add(checkBox[num++], j, i);
            }
        }
        if (destination[0] == 1) {
            for (int j = 0; j < 42; j++) { //set visible to unselected check boxes //trip 1
                if (mainArray[0][date[0] - 1][j][0] != null) {
                    checkBox[j].setVisible(false);
                }
            }
        } else if (destination[0] == 2) {
            for (int j = 0; j < 42; j++) { //set visible to unselected check boxes //trip 2
                if (mainArray[1][date[0] - 1][j][0] != null) {
                    checkBox[j].setVisible(false);
                }
            }
        }
        Scene scene = new Scene(gridPane, 1100, 900);
        scene.getStylesheets().add("com/trainBooking/Style.css");
        stage.setScene(scene);
        stage.showAndWait();
    }

    //find seat by customer name
    private void findSeatByCustomer(String[][][][] mainArray) {
        System.out.println("\n************************************************************");
        System.out.println("                 Find Seats by customer Name                  ");
        System.out.println("************************************************************\n");
        HashMap<String, String> customer = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("\nPlease enter your name");
        String name = nameValidate(scan);
        System.out.println("\n***Please select Date and Destination from the GUI***");
        // That GUI stage for select date and destination
        Stage stageForDate = new Stage();
        AnchorPane AnchorPaneforDate = new AnchorPane();
        DatePicker datePicker = new DatePicker();
        Button button = new Button("Confrim");
        RadioButton C2B = new RadioButton("Colombo to Badulla");
        RadioButton B2C = new RadioButton("Badulla to Colombo");
        ToggleGroup radioGroup = new ToggleGroup();
        C2B.setToggleGroup(radioGroup);
        B2C.setToggleGroup(radioGroup);
        Label ForDate = new Label("Please Select your Date");
        Label ForDestination = new Label("Select your Destination");
        Alert messageBox = new Alert(Alert.AlertType.NONE);  //https://www.geeksforgeeks.org/javafx-alert-with-examples/
        int[] destination = new int[1];
        int[] date = new int[1];
        LocalDate lastDate = LocalDate.now().plusDays(29); //set Disable datepicker for month
        LocalDate currentDate = LocalDate.now().plusDays(1);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(lastDate) || date.isBefore(currentDate));  //https://stackoverflow.com/questions/28914579/setdisable-inside-datepickers-setdaycellfactory-not-working-if-hijrahchronolo
            }
        });

        button.setOnAction(action -> {
            LocalDate selected = datePicker.getValue();
            if (C2B.isSelected()) {
                destination[0] = 1;
                // System.out.println(C2B.getText());
            } else if (B2C.isSelected()) {
                destination[0] = 2;
                // System.out.println(B2C.getText());
            }
            if(selected == null || destination[0] == 0){
                messageBox.setAlertType(Alert.AlertType.ERROR);
                messageBox.setContentText("Please Select the Date and Destination");
                messageBox.show();}
            else{
                date[0] = Period.between(LocalDate.now(),selected).getDays();
                stageForDate.close();}
        });
        datePicker.setLayoutX(91);
        datePicker.setLayoutY(82);
        C2B.setLayoutX(82);
        C2B.setLayoutY(240);
        B2C.setLayoutX(82);
        B2C.setLayoutY(282);
        button.setLayoutX(169);
        button.setLayoutY(413);
        ForDate.setLayoutX(141);
        ForDate.setLayoutY(37);
        ForDestination.setLayoutX(132);
        ForDestination.setLayoutY(173);

        AnchorPaneforDate.getChildren().add(datePicker);
        AnchorPaneforDate.getChildren().add(C2B);
        AnchorPaneforDate.getChildren().add(B2C);
        AnchorPaneforDate.getChildren().add(button);
        AnchorPaneforDate.getChildren().add(ForDestination);
        AnchorPaneforDate.getChildren().add(ForDate);

        Scene sceneForDate = new Scene(AnchorPaneforDate, 400, 500);
        stageForDate.setScene(sceneForDate);
        stageForDate.showAndWait();

        String Name;
        if (destination[0] == 1) {
            for (int i = 0; i < SEAT_CAPACITY; i++) {
                Name = mainArray[0][date[0] - 1][i][1];
                if ((mainArray[0][date[0] - 1][i][1] != null) && Name.equalsIgnoreCase(name)) { //check the input name include the main array.
                    String seat = "";
                    for (int j = 0; j < SEAT_CAPACITY; j++) {
                        if (mainArray[0][date[0] - 1][j][1] != null && mainArray[0][date[0] - 1][i][1].equalsIgnoreCase(mainArray[0][date[0]-1][j][1])) { //check equal names in the main array
                            seat = seat + " " + Integer.toString(j + 1); //assign the seat number to seat variable,but the seat no: genarate by "J" variable.
                        }
                    }
                    customer.put(Name, seat); //all the datas stores in the hashmap
                }
            }
            if(customer.isEmpty()){
                System.out.println("\n--------No details to show--------");
            }
            else{
                System.out.println("\nDate : "+datePicker.getValue());
                System.out.println(customer);
                System.out.println("Colombo to Badulla");
            }

        } else if (destination[0] == 2) {
            for (int i = 0; i < SEAT_CAPACITY; i++) {
                Name = mainArray[1][date[0] - 1][i][1];
                if ((mainArray[1][date[0] - 1][i][1] != null) && Name.equalsIgnoreCase(name)) { //check the input name include the main array.
                    String seat = "";
                    for (int j = 0; j < SEAT_CAPACITY; j++) {
                        if (mainArray[1][date[0] - 1][j][1] != null && mainArray[1][date[0] - 1][i][1].equalsIgnoreCase(mainArray[0][date[0]-1][j][1])){ //check equal names in the main array
                            seat = seat + " " + Integer.toString(j + 1); //assign the seat number to seat variable,but the seat no: genarate by "J" variable.
                        }
                    }
                    customer.put(Name, seat);
                }
            }
            if(customer.isEmpty()){
                System.out.println("\n--------No details to show--------");
            }
            else{
                System.out.println("\nDate : "+datePicker.getValue());
                System.out.println(customer);
                System.out.println("Badulla to Colombo");
            }
        }
    }

    //Sort the seats by name
    private static ArrayList<String> sortbyName(String[][][][] mainArray) {
        System.out.println("\n***Please select Date and Destination from the GUI***");
        // That GUI stage for select date and destination
        Stage stageForDate = new Stage();
        AnchorPane AnchorPaneforDate = new AnchorPane();
        DatePicker datePicker = new DatePicker();
        Button button = new Button("Confrim");
        RadioButton C2B = new RadioButton("Colombo to Badulla");
        RadioButton B2C = new RadioButton("Badulla to Colombo");
        ToggleGroup radioGroup = new ToggleGroup();
        C2B.setToggleGroup(radioGroup);
        B2C.setToggleGroup(radioGroup);
        Label ForDate = new Label("Please Select your Date");
        Label ForDestination = new Label("Select your Destination");
        Alert messageBox = new Alert(Alert.AlertType.NONE);  //https://www.geeksforgeeks.org/javafx-alert-with-examples/
        int[] destination = new int[1];
        int[] date = new int[1];
        LocalDate lastDate = LocalDate.now().plusDays(29); //set Disable datepicker for month
        LocalDate currentDate = LocalDate.now().plusDays(1);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(lastDate) || date.isBefore(currentDate));  //https://stackoverflow.com/questions/28914579/setdisable-inside-datepickers-setdaycellfactory-not-working-if-hijrahchronolo
            }
        });

        button.setOnAction(action -> {
            LocalDate selected = datePicker.getValue();
            if (C2B.isSelected()) {
                destination[0] = 1;
                // System.out.println(C2B.getText());
            } else if (B2C.isSelected()) {
                destination[0] = 2;
                // System.out.println(B2C.getText());
            }
            if(selected == null || destination[0] == 0){
                messageBox.setAlertType(Alert.AlertType.ERROR);
                messageBox.setContentText("Please Select the Date and Destination");
                messageBox.show();}
            else{
                date[0] = Period.between(LocalDate.now(),selected).getDays();
                stageForDate.close();}
        });
        datePicker.setLayoutX(91);
        datePicker.setLayoutY(82);
        C2B.setLayoutX(82);
        C2B.setLayoutY(240);
        B2C.setLayoutX(82);
        B2C.setLayoutY(282);
        button.setLayoutX(169);
        button.setLayoutY(413);
        ForDate.setLayoutX(141);
        ForDate.setLayoutY(37);
        ForDestination.setLayoutX(132);
        ForDestination.setLayoutY(173);

        AnchorPaneforDate.getChildren().add(datePicker);
        AnchorPaneforDate.getChildren().add(C2B);
        AnchorPaneforDate.getChildren().add(B2C);
        AnchorPaneforDate.getChildren().add(button);
        AnchorPaneforDate.getChildren().add(ForDestination);
        AnchorPaneforDate.getChildren().add(ForDate);

        Scene sceneForDate = new Scene(AnchorPaneforDate, 400, 500);
        stageForDate.setScene(sceneForDate);
        stageForDate.showAndWait();

        ArrayList<String> name = new ArrayList();
        ArrayList<String> sorting = new ArrayList();
        if (destination[0] == 1) {
            for (int i = 0; i < SEAT_CAPACITY; i++) {
                if ((mainArray[0][date[0] - 1][i][1] != null) && !(name.contains(mainArray[0][date[0] - 1][i][1]))) {
                    String user = (mainArray[0][date[0] - 1][i][1] + "-");
                    String seat = "";
                    name.add(mainArray[0][date[0] - 1][i][1]);
                    for (int j = 0; j < SEAT_CAPACITY; j++) {
                        if (mainArray[0][date[0] - 1][j][1] != null && mainArray[0][date[0] - 1][i][1].equalsIgnoreCase(mainArray[0][date[0]-1][j][1])){        //check equal names in the main array
                            seat = seat + " " + Integer.toString(j + 1);   //assign the seat number to seat variable,but the seat no: genarate by "J" variable.
                        }
                    }
                    sorting.add(user + seat);
                    String temp;
                    for (int j =0; j<sorting.size()-1;j++){
                        for (int k = j+1;k<sorting.size();k++){
                            if (sorting.get(j).compareToIgnoreCase(sorting.get(k)) > 0){
                                temp = sorting.get(j);
                                sorting.set(j,sorting.get(k));
                                sorting.set(k,temp);

                            }
                        }
                    }
                }
            }
        } else if (destination[0] == 2) {
            for (int i = 0; i < SEAT_CAPACITY; i++) {
                if ((mainArray[1][date[0] - 1][i][1] != null) && !(name.contains(mainArray[1][date[0] - 1][i][1]))) {
                    String user = (mainArray[1][date[0] - 1][i][1] + "-");
                    String seat = "";
                    name.add(mainArray[1][date[0] - 1][i][1]);
                    for (int j = 0; j < SEAT_CAPACITY; j++) {
                        if (mainArray[1][date[0] - 1][j][1] != null && mainArray[1][date[0] - 1][i][1].equalsIgnoreCase(mainArray[1][date[0]-1][j][1])){        //check equal names in the main array
                            seat = seat + " " + Integer.toString(j + 1);   //assign the seat number to seat variable,but the seat no: genarate by "J" variable.
                        }
                    }
                    sorting.add(user + seat);
                    String temp;
                    for (int j =0; j<sorting.size()-1;j++){
                        for (int k = j+1;k<sorting.size();k++){
                            if (sorting.get(j).compareToIgnoreCase(sorting.get(k)) > 0){
                                temp = sorting.get(j);
                                sorting.set(j,sorting.get(k));
                                sorting.set(k,temp);

                            }
                        }
                    }
                }
            }
        }

        System.out.println("\n************************************************************");
        System.out.println("             Sorting Customer by their Name                 ");
        System.out.println("************************************************************\n");
        if (destination[0] == 1){
            System.out.println("Selected date : "+datePicker.getValue()+"\n"+"Selected trip : Colombo to Badulla");}
        else if (destination[0] == 2){
            System.out.println("Selected date : "+datePicker.getValue()+"\n"+"Selected trip : Badulla to Colombo");}

        System.out.println(sorting);

        return sorting;
    }

    //delete booked seats
    private static String[][][][] deleteBookedSeats(String[][][][] mainArray) {
        System.out.println("\n***Please select Date and Destination from the GUI***");
        // That GUI stage for select date and destination
        Stage stageForDate = new Stage();
        AnchorPane AnchorPaneforDate = new AnchorPane();
        DatePicker datePicker = new DatePicker();
        Button button = new Button("Confrim");
        RadioButton C2B = new RadioButton("Colombo to Badulla");
        RadioButton B2C = new RadioButton("Badulla to Colombo");
        ToggleGroup radioGroup = new ToggleGroup();
        C2B.setToggleGroup(radioGroup);
        B2C.setToggleGroup(radioGroup);
        Label ForDate = new Label("Please Select your Date");
        Label ForDestination = new Label("Select your Destination");
        Alert messageBox = new Alert(Alert.AlertType.NONE);  //https://www.geeksforgeeks.org/javafx-alert-with-examples/
        int[] destination = new int[1];
        int[] date = new int[1];
        LocalDate lastDate = LocalDate.now().plusDays(29); //set Disable datepicker for month
        LocalDate currentDate = LocalDate.now().plusDays(1);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(lastDate) || date.isBefore(currentDate));  //https://stackoverflow.com/questions/28914579/setdisable-inside-datepickers-setdaycellfactory-not-working-if-hijrahchronolo
            }
        });

        button.setOnAction(action -> {
            LocalDate selected = datePicker.getValue();
            if (C2B.isSelected()) {
                destination[0] = 1;
                // System.out.println(C2B.getText());
            } else if (B2C.isSelected()) {
                destination[0] = 2;
                // System.out.println(B2C.getText());
            }
            if(datePicker.getValue() == null || destination[0] == 0){
                messageBox.setAlertType(Alert.AlertType.ERROR);
                messageBox.setContentText("Please Select the Date and Destination");
                messageBox.show();}
            else{
                date[0] = Period.between(LocalDate.now(),selected).getDays();
                stageForDate.close();}
        });
        datePicker.setLayoutX(91);
        datePicker.setLayoutY(82);
        C2B.setLayoutX(82);
        C2B.setLayoutY(240);
        B2C.setLayoutX(82);
        B2C.setLayoutY(282);
        button.setLayoutX(169);
        button.setLayoutY(413);
        ForDate.setLayoutX(141);
        ForDate.setLayoutY(37);
        ForDestination.setLayoutX(132);
        ForDestination.setLayoutY(173);

        AnchorPaneforDate.getChildren().add(datePicker);
        AnchorPaneforDate.getChildren().add(C2B);
        AnchorPaneforDate.getChildren().add(B2C);
        AnchorPaneforDate.getChildren().add(button);
        AnchorPaneforDate.getChildren().add(ForDestination);
        AnchorPaneforDate.getChildren().add(ForDate);

        Scene sceneForDate = new Scene(AnchorPaneforDate, 400, 500);
        stageForDate.setScene(sceneForDate);
        stageForDate.showAndWait();
        Scanner sc = new Scanner(System.in);
        System.out.println("\n************************************************************");
        System.out.println("                    Delete booked seats                       ");
        System.out.println("************************************************************\n");
        System.out.println("\n--------Please enter your booked seat number--------");
        int seat = sc.nextInt();
        if (destination[0] == 1 && mainArray[0][date[0]-1][seat-1][0] != null) {
            for (int j = 0; j < 3; j++) {
                mainArray[0][date[0] - 1][seat - 1][j] = (null);
            }
            System.out.println("Seat No : " + seat + "\nChoosen date : "+ datePicker.getValue() + "\n" + "Trip : Colombo to Badulla.");
            System.out.println("\n--------Deleted succesful--------");
        } else if (destination[0] == 2 && mainArray[1][date[0]-1][seat-1][0] != null) {
            for (int j = 0; j < 3; j++) {
                mainArray[1][date[0] - 1][seat - 1][j] = (null);
            }
            System.out.println("Seat No : " + seat + "\nChoosen date : "+ datePicker.getValue() + "\n" + "Trip : Badulla to Colombo.");
            System.out.println("\n--------Deleted succesful--------");
        } else {
            System.out.println("\nInvalid selection,Your entered seat is empty , invalid date or you didn,t select correct Destination.");
        }

        return mainArray;
    }

    //Store data in MongoDB
    private void saveData(String[][][][] mainArray) {
        MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");
        MongoDatabase database = mongoClient.getDatabase("TrainBooking");
        MongoCollection<Document> collection = database.getCollection("Tickets");
        BasicDBObject document = new BasicDBObject();
        collection.deleteMany(document); //delete previous datas
        int x;
        int i;
        int j;
        for ( x = 0; x < 2; x++) {
            for ( i = 0; i < 30; i++) {
                for ( j = 0; j < SEAT_CAPACITY; j++) {
                    if (mainArray[x][i][j][0] != null) {
                        Document record = new Document("Title","name")           ////create new document for upload
                                .append("Trip", x)                            //get datas from mainArray and append to the database
                                .append("Seat", j)
                                .append("Date", i)
                                .append("F_Name", mainArray[x][i][j][1])
                                .append("S_Name", mainArray[x][i][j][2]);
                        collection.insertOne(record);                       ////export one record
                    }
                }
            }
        }
        System.out.println("\n**********************Succesfully Saved your datas in the Database**********************");
    }

    //load data from MongoDB
    private String[][][][] loadData(String[][][][] mainArray) {
        MongoClient mongoClient = MongoClients.create("mongodb://LocalHost:27017");
        MongoDatabase database = mongoClient.getDatabase("TrainBooking");
        MongoCollection<Document> collection = database.getCollection("Tickets");
        BasicDBObject document = new BasicDBObject();
        FindIterable<Document> data = collection.find();
        for (Document record : data) {
            int trip = Integer.parseInt(record.get("Trip").toString());
            int seat = Integer.parseInt(record.get("Seat").toString());
            int date = Integer.parseInt(record.get("Date").toString());
            mainArray[trip][date][seat][0] = record.get("Date").toString(); //append datas to the main array
            mainArray[trip][date][seat][1] = record.get("F_Name").toString();
            mainArray[trip][date][seat][2] = record.get("S_Name").toString();
        }
        System.out.println("\n***********************************Loading succecfull***********************************");
        return mainArray;
    }

    //quit method
    private static void quit() {
        System.out.println("************************************************************");
        System.out.println("\n    Thank you for using Railway ticket booking system   \n");
        System.out.println("************************************************************");
        System.exit(0);
    }
}


