import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LibraryView {
    TableView<Book> borrowedBookView;
    TableView<Book> downloadedBookView;
    TableView<Book> tableView;
    TableView<Book> helpUserView;
    UserModel model;
    VBox view;
    Stage primaryStage;
    Scene scene;
    private Button userNeedHelp;

    LibraryView(Stage primaryStage, UserModel model) {
        this.primaryStage = primaryStage;
        this.model = model;
        createAndConfigurePane();
        createAndLayoutControls();
        updateControllerFromListeners();
        observeModelAndUpdateControls();
    }

    public Button getHelp() {
        return this.userNeedHelp;
    }

    public Scene getScene() {
        return scene;
    }

    public void createAndConfigurePane() {
        view = new VBox(10);
        view.setAlignment(Pos.CENTER);

        scene = new Scene(view, 500, 500);
    };

    public void createAndLayoutControls() {
        Label headingLabel = new Label("Welcom to the Library App");
        headingLabel.setFont(new Font("Arial", 32));
        Button option1 = new Button("1. View all books");
        Button option2 = new Button("2. Search for a book");
        Button option3 = new Button("3. View your books");
        // Button option4 = new Button("4. Return a print book");

        Label titleLabel = new Label("Select Option from 1-3");
        titleLabel.setFont(new Font("Arial", 20));

        VBox optionBox = new VBox(10, option1, option2, option3);
        optionBox.setAlignment(Pos.CENTER);

        option1.setOnAction(e -> primaryStage.setScene(createSceneOpt1()));
        option2.setOnAction(e -> primaryStage.setScene(createSceneOpt2()));
        option3.setOnAction(e -> primaryStage.setScene(createSceneOpt3()));
        // option4.setOnAction(e -> primaryStage.setScene(createSceneOpt4()));

        view.getChildren().addAll(headingLabel, titleLabel, optionBox);

    }

    public Scene createSceneOpt1() {
        Label heading = new Label("List of Books: ");
        heading.setFont(new Font("Arial", 20));
        tableView = new TableView<>();
        TableColumn<Book, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col1.setMinWidth(220);
        TableColumn<Book, String> col2 = new TableColumn<>("Author");
        col2.setCellValueFactory(cell -> cell.getValue().authorProperty());

        TableColumn<Book, Genre> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col4 = new TableColumn<>("Page Count");

        col4.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        tableView.getColumns().addAll(col1, col2, col3, col4);

        tableView.setItems(model.getLibrary().libraryProperty());

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });

        Button checkoutButton = new Button("Check-out");
        checkoutButton.setOnAction(e -> {
            // int index = tableView.getSelectionModel().getSelectedIndex();
            Book selectedBook = tableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                this.checkOutForm(selectedBook);
            }
        });
        Button filterButton = new Button("Filter");
        filterButton.setOnAction(e -> filterForm());
        Button sorButton = new Button("Sort");
        sorButton.setOnAction(e -> sortForm());
        HBox buttonRow = new HBox(10, checkoutButton, filterButton, sorButton, backButton);
        // buttonRow.setPadding(new Insets(2.0, 30.0, 19.0, 30.0));

        VBox viewOpt1 = new VBox(20, heading, tableView, buttonRow);
        Scene scene1 = new Scene(viewOpt1, 600, 500);
        return scene1;
    }

    public void filterForm() {
        Stage filter = new Stage();
        filter.initOwner(primaryStage);
        filter.initModality(Modality.APPLICATION_MODAL);

        Label heading = new Label("Filter Library By: ");

        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton filter1 = new RadioButton("Print Books");
        RadioButton filter2 = new RadioButton("Digital Books");

        filter1.setToggleGroup(toggleGroup);
        filter2.setToggleGroup(toggleGroup);

        HBox filterRow = new HBox(10, filter1, filter2);
        filterRow.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Choose");
        submitBtn.setOnAction(e -> {
            if (filter1.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook();
                tableView.setItems(printLibrary.libraryProperty());
                filter.close();

            } else if (filter2.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook();
                tableView.setItems(digitalLibrary.libraryProperty());
                filter.close();
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> filter.close());
        HBox btnRow = new HBox(10, submitBtn, cancelBtn);
        VBox root = new VBox(10, heading, filterRow, btnRow);
        Scene filterScene = new Scene(root, 300, 100);
        filter.setScene(filterScene);
        filter.show();
    }

    public void sortForm() {
        Stage sort = new Stage();
        sort.initOwner(primaryStage);
        sort.initModality(Modality.APPLICATION_MODAL);

        Label heading = new Label("Sort Library by: ");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton sort1 = new RadioButton("Title");
        RadioButton sort2 = new RadioButton("Author");

        sort1.setToggleGroup(toggleGroup);
        sort2.setToggleGroup(toggleGroup);

        HBox sortRow = new HBox(10, sort1, sort2);

        sortRow.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Choose");
        submitBtn.setOnAction(e -> {
            if (sort1.isSelected()) {
                model.getLibrary().sortByTitle();
                tableView.setItems(model.getLibrary().libraryProperty());
                sort.close();

            } else if (sort2.isSelected()) {
                model.getLibrary().sortByAuthor();
                tableView.setItems(model.getLibrary().libraryProperty());
                sort.close();
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> sort.close());
        HBox btnRow = new HBox(10, submitBtn, cancelBtn);

        VBox root = new VBox(10, heading, sortRow, btnRow);
        Scene sortScene = new Scene(root, 300, 100);
        sort.setScene(sortScene);
        sort.show();
    }

    public void checkOutForm(Book selectedBook) {
        Stage checkOut = new Stage();
        checkOut.initOwner(primaryStage);
        checkOut.initModality(Modality.APPLICATION_MODAL);

        // Book selectedBook = model.getLibrary().libraryProperty().get(index);

        Label heading = new Label("Book Details: ");
        heading.setFont(new Font("Arial", 20));
        String bookName = selectedBook.getTitle();
        String bookAuthor = selectedBook.getAuthor();
        String bookType = selectedBook.getGenre().name();

        Label nameLabel = new Label(bookName);
        Label authorLabel = new Label(bookAuthor);
        Label typeLabel = new Label(bookType);

        HBox nameRow = new HBox(10, new Label("Title: "), nameLabel);
        HBox authorRow = new HBox(10, new Label("Author: "), authorLabel);
        HBox typeRow = new HBox(10, new Label("Type: "), typeLabel);

        Button checkoutBtn;
        Label successLabel = new Label();
        successLabel.setAlignment(Pos.CENTER);
        if (selectedBook instanceof DigitalBook) {
            checkoutBtn = new Button("Download ");
            checkoutBtn.setOnAction(e -> {
                model.checkoutBook(selectedBook);
                checkOut.close();
                this.createPopUpForm(primaryStage, "You have successfully downloaded the Book!");
            });
        } else {
            checkoutBtn = new Button("Borrow ");
            checkoutBtn.setOnAction(e -> {
                model.checkoutBook(selectedBook);
                checkOut.close();
                this.createPopUpForm(primaryStage, "You have successfully borrowed the Book!");
            });
        }

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> checkOut.close());

        HBox buttonRow = new HBox(10, checkoutBtn, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);
        VBox root = new VBox(10, heading, nameRow, authorRow, typeRow, buttonRow, successLabel);
        Scene checkoutScene = new Scene(root, 300, 300);
        checkOut.setScene(checkoutScene);
        checkOut.show();
    }

    public void findBookForm() {
        Stage findBook = new Stage();
        findBook.initOwner(primaryStage);
        findBook.initModality(Modality.APPLICATION_MODAL);

        Stage tableBook = new Stage();
        tableBook.initOwner(primaryStage);
        tableBook.initModality(Modality.APPLICATION_MODAL);

        helpUserView = new TableView<>();
        TableColumn<Book, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col1.setMinWidth(220);
        TableColumn<Book, String> col2 = new TableColumn<>("Author");
        col2.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col4 = new TableColumn<>("Page Count");
        col4.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        helpUserView.getColumns().addAll(col1, col2, col3, col4);

        Label findBook1 = new Label("Is it Print Book or Digital Book?");
        ToggleGroup toggleGroup1 = new ToggleGroup();
        RadioButton printBook = new RadioButton("Print Book");
        printBook.setToggleGroup(toggleGroup1);
        RadioButton digitalBook = new RadioButton("Digital Book");
        digitalBook.setToggleGroup(toggleGroup1);
        RadioButton iDontKnowMan = new RadioButton("I'm not sure");
        iDontKnowMan.setToggleGroup(toggleGroup1);

        Label findBook2 = new Label("Choose the genre of book you want to read");
        ToggleGroup toggleGroup2 = new ToggleGroup();
        RadioButton fiction = new RadioButton("Fiction");
        fiction.setToggleGroup(toggleGroup2);
        RadioButton nonfiction = new RadioButton("Non-Fiction");
        nonfiction.setToggleGroup(toggleGroup2);
        RadioButton mystery = new RadioButton("Mystery");
        mystery.setToggleGroup(toggleGroup2);
        RadioButton scienceFiction = new RadioButton("Science Fiction");
        scienceFiction.setToggleGroup(toggleGroup2);
        RadioButton romance = new RadioButton("Romance");
        romance.setToggleGroup(toggleGroup2);
        RadioButton poetry = new RadioButton("Poetry");
        poetry.setToggleGroup(toggleGroup2);
        RadioButton fantasy = new RadioButton("Fantasy");
        fantasy.setToggleGroup(toggleGroup2);

        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(e -> {
            if (fiction.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.FICTION);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (nonfiction.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.NON_FICTION);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (fantasy.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.FANTASY);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (mystery.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.MYSTERY);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (scienceFiction.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.SCIENCE_FICTION);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (romance.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.ROMANCE);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (poetry.isSelected() && printBook.isSelected()) {
                LibraryModel printLibrary = model.getLibrary().filterPrintBook(Genre.POETRY);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (fiction.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.FICTION);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (nonfiction.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.NON_FICTION);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (fantasy.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.FANTASY);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (mystery.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.MYSTERY);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (scienceFiction.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.SCIENCE_FICTION);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (romance.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.ROMANCE);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (poetry.isSelected() && digitalBook.isSelected()) {
                LibraryModel digitalLibrary = model.getLibrary().filterDigitalBook(Genre.POETRY);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else {
            }
            Button checkOutButton = new Button("Check-out");
            checkOutButton.setOnAction(event -> {
                Book selectedBook = helpUserView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    this.checkOutForm(selectedBook);
                }
            });
            Button backToMenu = new Button("Back");
            backToMenu.setOnAction(event->{
                tableBook.close();
            });
            HBox optionRow = new HBox(100, backToMenu, checkOutButton);
            optionRow.setAlignment(Pos.BOTTOM_CENTER);
            VBox view = new VBox(5, helpUserView, optionRow);
            view.setAlignment(Pos.CENTER);
            Scene scene = new Scene(view, 600, 500);
            tableBook.setScene(scene);
            tableBook.show();
            findBook.close();

        });

        HBox checkBookRow = new HBox(5, printBook, digitalBook, iDontKnowMan);
        checkBookRow.setAlignment(Pos.CENTER);
        HBox row1 = new HBox(5, fiction, romance, nonfiction);
        row1.setAlignment(Pos.CENTER);
        HBox row2 = new HBox(5, fantasy, poetry, mystery, scienceFiction);
        row2.setAlignment(Pos.CENTER);
        VBox view = new VBox(20, findBook1, checkBookRow, findBook2, row1, row2, submitBtn);
        view.setAlignment(Pos.CENTER);

        Scene scene1 = new Scene(view, 400, 300);
        findBook.setScene(scene1);
        findBook.show();

        // VBox root = new VBox(20, );

    }

    public void helpUserToFindBook() {
        Stage helpUser = new Stage();
        helpUser.initOwner(primaryStage);
        helpUser.initModality(Modality.APPLICATION_MODAL);
        Label message = new Label("Sorry, the book title is not exist");
        message.setFont(new Font("Arial", 20));
        Label helpUserFindBook = new Label("Do you need help to search for you book?");
        helpUserFindBook.setFont(new Font("Arial", 20));
        Button yes = new Button("Yes");
        yes.setMinWidth(60);
        yes.setOnAction(e -> {
            helpUser.close();
            findBookForm();
        });
        Button no = new Button("No");
        no.setMinWidth(60);
        no.setOnAction(e -> {
            helpUser.close();
        });

        HBox answer = new HBox(50, yes, no);
        answer.setAlignment(Pos.BOTTOM_CENTER);
        VBox helpBox = new VBox(5, message, helpUserFindBook);
        helpBox.setAlignment(Pos.CENTER);
        VBox view = new VBox(5, helpBox, answer);
        view.setAlignment(Pos.CENTER);
        Scene scene = new Scene(view, 400, 200);
        helpUser.setScene(scene);
        helpUser.show();

    }

    public Scene createSceneOpt2() {
        Label heading = new Label("Searching for Books");
        heading.setFont(new Font("Arial", 20));
        TextField inputField = new TextField();
        inputField.setPromptText("Enter Book Title: ");
        inputField.setMaxWidth(300);
        Button searchBtn = new Button("Search");
        Label message = new Label();
        Label helpUser = new Label();
        searchBtn.setOnAction(e -> {
            String bookTitle = inputField.getText().toLowerCase().trim();
            Book foundedBook = model.getLibrary().getBook(bookTitle);

            if (foundedBook == null) {
                helpUserToFindBook();
            } else {
                // String bookName = foundedBook.getTitle().toLowerCase();
                // String bookAuthor = foundedBook.getAuthor();
                // String bookType = foundedBook.getGenre().name();
                // message.setText(
                //         "Book Information: " + "\nTitle: " + bookName + "\nAuthor: " + bookAuthor + "\nType: "
                //                 + bookType);

                checkOutForm(foundedBook);
            }
        });
        Button backBtn = new Button("Back to Menu");
        backBtn.setOnAction(e -> {
            primaryStage.setScene(scene);
        });

        HBox buttonRow = new HBox(10, searchBtn, backBtn);
        buttonRow.setAlignment(Pos.CENTER);

        VBox viewOpt2 = new VBox(20, heading, inputField, buttonRow, message, helpUser);
        viewOpt2.setAlignment(Pos.CENTER);
        Scene scene2 = new Scene(viewOpt2, 500, 500);
        return scene2;
    }

    public void updateControllerFromListeners() {

    }

    public void observeModelAndUpdateControls() {

    }

    public Scene createSceneOpt3() {
        Label headingBorrowed = new Label("List of Your Borrowed Books: ");
        headingBorrowed.setFont(new Font("Arial", 20));
        borrowedBookView = new TableView<>();

        TableColumn<Book, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col1.setMinWidth(220);
        TableColumn<Book, String> col2 = new TableColumn<>("Author");
        col2.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col4 = new TableColumn<>("Page Count");
        col4.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        borrowedBookView.getColumns().addAll(col1, col2, col3, col4);
        borrowedBookView.setItems(model.getBorrowedBooks());

        Label headingDownloaded = new Label("List of Your Downloaded Books: ");
        headingDownloaded.setFont(new Font("Arial", 20));
        downloadedBookView = new TableView<>();
        TableColumn<Book, String> col5 = new TableColumn<>("Title");
        col5.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col5.setMinWidth(220);
        TableColumn<Book, String> col6 = new TableColumn<>("Author");
        col6.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col7 = new TableColumn<>("Genre");
        col7.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col8 = new TableColumn<>("Page Count");
        col8.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        downloadedBookView.getColumns().addAll(col5, col6, col7, col8);
        downloadedBookView.setItems(model.getDownloadedBooks());

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });

        Button returnBtn = new Button("Return");
        returnBtn.setOnAction(e -> {
            Book selectedBook = borrowedBookView.getSelectionModel().getSelectedItem();
            if (!(selectedBook instanceof PrintBook) || selectedBook == null ) {
                this.createPopUpForm(primaryStage,"Please select a borrowed book to return.");
            } else {
                model.returnBook(selectedBook);
            }
        });

        VBox root = new VBox(10, headingBorrowed, borrowedBookView, headingDownloaded, downloadedBookView, backButton,
                returnBtn);
        Scene userBookScene = new Scene(root, 500, 500);
        return userBookScene;
    }

    public void createPopUpForm(Stage ownerStage, String message) {
        Stage form = new Stage();
        form.initOwner(ownerStage);
        form.initModality(Modality.APPLICATION_MODAL);

        Label messageLabel = new Label(message);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> form.close());
        VBox root = new VBox(20,messageLabel, cancelBtn);
        root.setAlignment(Pos.CENTER);
        Scene formScene = new Scene(root, 300, 100);
        form.setScene(formScene);
        form.show();
    }

    // private void configTextFieldForInts(TextField field) {
    //     field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
    //         if (c.getControlNewText().matches("-?\\d*")) {
    //             return c;
    //         }
    //         return null;
    //     }));
    // }
}