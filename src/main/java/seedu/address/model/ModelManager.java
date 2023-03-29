package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.parser.IndexHandler;
import seedu.address.model.meetup.MeetUp;
import seedu.address.model.meetup.Participants;
import seedu.address.model.person.ContactIndex;
import seedu.address.model.person.Person;
import seedu.address.model.person.User;
import seedu.address.model.recommendation.Recommendation;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final EduMate eduMate;
    private final EduMateHistory eduMateHistory;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> observablePersons;
    private final FilteredList<Recommendation> filteredRecommendations;
    private final SortedList<Recommendation> observableRecommendations;
    private final IndexHandler indexHandler;
    private final FilteredList<MeetUp> filteredMeetUps;
    private final SortedList<MeetUp> observableMeetUps;


    /**
     * Initializes a ModelManager with the given eduMate and userPrefs.
     */
    public ModelManager(ReadOnlyEduMate eduMate, ReadOnlyUserPrefs userPrefs, ReadOnlyEduMateHistory eduMateHistory) {
        requireAllNonNull(eduMate, userPrefs);

        logger.fine("Initializing with address book: " + eduMate + " and user prefs " + userPrefs);

        this.eduMate = new EduMate(eduMate);
        this.userPrefs = new UserPrefs(userPrefs);
        this.eduMateHistory = new EduMateHistory(eduMateHistory);
        indexHandler = new IndexHandler(this);
        filteredPersons = new FilteredList<>(this.eduMate.getPersonList());
        observablePersons = new SortedList<>(filteredPersons);

        filteredMeetUps = new FilteredList<>(this.eduMate.getMeetUpList());
        observableMeetUps = new SortedList<>(filteredMeetUps);

        filteredRecommendations = new FilteredList<>(this.eduMate.getRecommendationList());
        observableRecommendations = new SortedList<>(filteredRecommendations);


    }

    public ModelManager() {
        this(new EduMate(), new UserPrefs(), new EduMateHistory());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getEduMateFilePath() {
        return userPrefs.getEduMateFilePath();
    }

    @Override
    public void setEduMateFilePath(Path eduMateFilePath) {
        requireNonNull(eduMateFilePath);
        userPrefs.setEduMateFilePath(eduMateFilePath);
    }

    //=========== EduMate ================================================================================

    @Override
    public void setEduMate(ReadOnlyEduMate eduMate) {
        this.eduMate.resetData(eduMate);
    }

    @Override
    public ReadOnlyEduMate getEduMate() {
        return eduMate;
    }

    @Override
    public ReadOnlyEduMateHistory getEduMateHistory() {
        return eduMateHistory;
    }

    @Override
    public void addEduMateHistory(String command) {
        eduMateHistory.addCommand(command);
    }

    // person-level methods

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return eduMate.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        eduMate.removePerson(target);
    }

    @Override
    public Person addPerson(Person person) {
        // The only place in the entire code that can set Contact Index.
        ContactIndex contactIndex = indexHandler.assignIndex();
        Person indexedPerson = person.setContactIndex(contactIndex);
        eduMate.addPerson(indexedPerson);
        updateObservablePersonList();
        return indexedPerson;
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        eduMate.setPerson(target, editedPerson);
    }

    @Override
    public void resetPersons() {
        eduMate.resetPersons();
    }

    // recommendation-level methods

    @Override
    public boolean hasRecommendation(Recommendation recommendation) {
        requireNonNull(recommendation);
        return eduMate.hasRecommendation(recommendation);
    }

    @Override
    public void deleteRecommendation(Recommendation target) {
        eduMate.removeRecommendation(target);
    }

    @Override
    public Recommendation addRecommendation(Recommendation recommendation) {
        // The only place in the entire code that can set Contact Index.
        ContactIndex contactIndex = indexHandler.assignRecommendationIndex();
        Recommendation indexedRecommendation = recommendation.setContactIndex(contactIndex);
        eduMate.addRecommendation(indexedRecommendation);
        updateObservableRecommendationList();
        return indexedRecommendation;
    }

    @Override
    public void setRecommendation(Recommendation target, Recommendation editedRecommendation) {
        requireAllNonNull(target, editedRecommendation);

        eduMate.setRecommendation(target, editedRecommendation);
    }

    @Override
    public void setRecommendations(List<Recommendation> recommendations) {
        resetRecommendations();
        for (Recommendation recommendation : recommendations) {
            addRecommendation(recommendation);
        }
    }

    @Override
    public void resetRecommendations() {
        eduMate.resetRecommendations();
    }

    @Override
    public User getUser() {
        return eduMate.getUser();
    }

    @Override
    public void setUser(User user) {
        requireNonNull(user);

        eduMate.setUser(user);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedEduMate}
     */

    @Override
    public Optional<Person> getPersonByIndex(ContactIndex index) {
        return indexHandler.getPersonByIndex(index);
    }
    @Override
    public ObservableList<Person> getObservablePersonList() {
        return observablePersons;
    }

    @Override
    public void updateObservablePersonList(Comparator<Person> comparator) {
        requireNonNull(comparator);
        observablePersons.setComparator(comparator);
    }

    @Override
    public void updateObservablePersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateObservablePersonList() {
        filteredPersons.setPredicate(PREDICATE_SHOW_ALL_PERSONS);
        observablePersons.setComparator(COMPARATOR_CONTACT_INDEX_PERSON);
    }

    @Override
    public ObservableList<Recommendation> getObservableRecommendationList() {
        return observableRecommendations;
    }

    @Override
    public void updateObservableRecommendationList(Comparator<Recommendation> comparator) {
        requireNonNull(comparator);
        observableRecommendations.setComparator(comparator);
    }

    @Override
    public void updateObservableRecommendationList(Predicate<Recommendation> predicate) {
        requireNonNull(predicate);
        filteredRecommendations.setPredicate(predicate);
    }

    @Override
    public void updateObservableRecommendationList() {
        filteredRecommendations.setPredicate(PREDICATE_SHOW_ALL_RECOMMENDATIONS);
        observableRecommendations.setComparator(COMPARATOR_CONTACT_INDEX_RECOMMENDATION);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;

        logger.info("EduMate equality: "
                + eduMate.equals(other.eduMate));

        return eduMate.equals(other.eduMate)
                && userPrefs.equals(other.userPrefs)
                && observablePersons.equals(other.observablePersons);
    }

    @Override
    public Optional<Recommendation> getRecommendationByIndex(ContactIndex index) {
        return indexHandler.getRecommendationByIndex(index);
    }

    @Override
    public void addMeetUp(MeetUp meetUp) {
        ContactIndex contactIndex = indexHandler.assignMeetUpIndex();
        eduMate.addMeetUp(meetUp);
    }

    @Override
    public ContactIndex getMeetUpIndex() {
        return indexHandler.assignMeetUpIndex();
    }

    @Override
    public ObservableList<MeetUp> getObservableMeetUpList() {
        return observableMeetUps;
    }

    @Override
    public Participants getParticipants() {
        return eduMate.getParticipantList();
    }

    @Override
    public void setParticipants(Set<ContactIndex> participants) {
        List<Person> people = new ArrayList<>();
        for (ContactIndex i : participants) {
            Optional<Person> person = indexHandler.getPersonByIndex(i);
            person.ifPresent(people::add);
        }
        eduMate.setParticipants(new Participants(people));
    }

    @Override
    public void updateObservableMeetUpList() {
        filteredMeetUps.setPredicate(PREDICATE_SHOW_ALL_MEETUPS);
        observableMeetUps.setComparator(COMPARATOR_CONTACT_INDEX_MEETUP);
    }

}
