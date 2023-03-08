package seedu.address.ui;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.tag.GroupTag;
import seedu.address.model.tag.ModuleTag;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on EduMate level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;

    @FXML
    private FlowPane groupTags;

    @FXML
    private FlowPane moduleTags;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().getValue());
        phone.setText(person.getPhone().getValue());
        address.setText(person.getAddress().getValue());
        email.setText(person.getEmail().getValue());
        person.getImmutableGroupTags().stream()
                .sorted(GroupTag::compareTo)
                .forEach(groupTag -> {
                    Label temp = new Label(groupTag.tagName);
                    temp.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #000000; "
                            + "-fx-padding: 2 5 2 5; -fx-background-radius: 5;");
                    groupTags.getChildren().add(temp);
                });
        person.getImmutableCommonModuleTags().stream()
                .sorted(ModuleTag::compareTo)
                .forEach(moduleTag -> {
                    Label temp = new Label(moduleTag.tagName);
                    temp.setStyle("-fx-text-fill: #FFFFFF; -fx-background-color: #000000; "
                            + "-fx-padding: 2 5 2 5; -fx-background-radius: 5;");
                    moduleTags.getChildren().add(temp);
                });
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonCard)) {
            return false;
        }

        // state check
        PersonCard card = (PersonCard) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }
}
