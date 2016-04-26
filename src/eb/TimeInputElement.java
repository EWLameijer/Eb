package eb;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.checkerframework.checker.initialization.qual.UnknownInitialization;

/**
 * A TimeInputElement contains a textfield and combobox that allow the user to
 * input say "5.5 minutes" or "3 hours".
 *
 * @author Eric-Wubbo Lameijer
 */
public class TimeInputElement extends JPanel {

	private static final long serialVersionUID = 4711963487097757055L;

	// Label indicating the identity of the interval (for example "interval
	// before new card is first shown)
	private final JLabel m_label;

	// Text field that sets the quantity (the '3' in 3 hours), the combo box
	// selects the unit (the hours in "3 hours").
	private final JTextField m_scalarField;

	// Combo box used (in combination with a text field) to set the value for the
	// initial study interval. Contains the hours of "3 hours".
	private final JComboBox<String> m_unitComboBox;

	private List<DataFieldChangeListener> m_dataFieldChangeListeners = new ArrayList<>();

	/**
	 * Constructs the TimeInputElement, given a name and a TimeInterval (which can
	 * contain something like "3 hour(s)".
	 *
	 * @param name
	 *          the name of the interval, which is shown on the label
	 * @param timeInterval
	 *          the time interval to be displayed, like "3 hour(s)".
	 */
	private TimeInputElement(String name, TimeInterval timeInterval) {
		// preconditions: name should be valid; the timeInterval will be checked
		// by setInterval.
		super();

		Utilities.require(Utilities.isStringValidIdentifier(name),
		    "TimeInputElement constructor error: the name should be a valid "
		        + "identifier");

		m_label = new JLabel(name);
		m_scalarField = new JTextField();

		m_scalarField.setDocument(new FixedSizeNumberDocument(m_scalarField, 5, 2));
		m_scalarField.getDocument().addDocumentListener(
		    new DelegatingDocumentListener(() -> notifyDataFieldChangeListeners()));

		m_scalarField.setPreferredSize(new Dimension(40, 20));
		m_unitComboBox = new JComboBox<>();
		m_unitComboBox
		    .setModel(new DefaultComboBoxModel<String>(TimeUnit.getUnitNames()));
		m_unitComboBox.addActionListener(e ->

		notifyDataFieldChangeListeners());
		setInterval(timeInterval);
		// postconditions: none. Ordinary constructor.
	}

	/**
	 * Initializes the TimeInputElement - this needs to be separate from the
	 * constructor, or else the nullness checker will complain.
	 */
	private void init() {
		add(m_label);
		add(m_scalarField);
		add(m_unitComboBox);
	}

	/**
	 * Factory method to create a TimeInputElement, which is basically a small
	 * JPanel that can be used to display a time. Factory method needed to handle
	 * the separation between object construction and initialization 'stimulated'
	 * by the nullness checker.
	 *
	 * @param name
	 *          the name of the interval, which is shown on the label
	 * @param timeInterval
	 *          the time interval to be displayed, like "3 hour(s)".
	 */
	public static TimeInputElement createInstance(String name,
	    TimeInterval timeInterval) {
		final TimeInputElement timeInputElement = new TimeInputElement(name,
		    timeInterval);
		timeInputElement.init();
		return timeInputElement;
	}

	/**
	 * Sets the interval (displayed in the text field and combo box) to the given
	 * time interval.
	 *
	 * @param timeInterval
	 *          the time interval that should be displayed by this
	 *          TimeInputElement (text field and combo box).
	 */
	public void setInterval(
	    @UnknownInitialization(TimeInputElement.class) TimeInputElement this,
	    TimeInterval timeInterval) {
		// preconditions: timeInterval should not be null
		Utilities.require(timeInterval != null,
		    "TimeInputElement.setInterval error: the provided time interval "
		        + "may not be null.");
		m_scalarField.setText(
		    Utilities.doubleToMaxPrecisionString(timeInterval.getScalar(), 2));
		System.out.println(
		    Utilities.doubleToMaxPrecisionString(timeInterval.getScalar(), 2));
		m_unitComboBox
		    .setSelectedItem(timeInterval.getUnit().getUserInterfaceName());
		notifyDataFieldChangeListeners();
		// postconditions: none. I assume that all goes well.
	}

	private void notifyDataFieldChangeListeners() {
		for (DataFieldChangeListener dataFieldChangeListener : m_dataFieldChangeListeners) {
			dataFieldChangeListener.respondToChangedDataField();
		}
	}

	/**
	 * Returns the time interval encapsulated by this TimeInputElement.
	 *
	 * @return the time interval encapsulated by this TimeInputElement
	 */
	public TimeInterval getInterval() {
		final Optional<TimeUnit> timeUnit = TimeUnit
		    .parseUnit(m_unitComboBox.getSelectedItem().toString());
		Utilities.require(timeUnit.isPresent(), "TimeInterval.getInterval() error: "
		    + " the time unit is wrong for some reason.");

		Optional<Double> parsedNumber = Utilities
		    .stringToDouble(m_scalarField.getText());
		double timeIntervalScalar = parsedNumber.orElse(0.01);
		return new TimeInterval(timeIntervalScalar, timeUnit.get());
	}

	public void addDataFieldChangeListener(
	    DataFieldChangeListener dataFieldChangeListener) {
		// preconditions: should not already be in the list
		Utilities.require(
		    !m_dataFieldChangeListeners.contains(dataFieldChangeListener),
		    "TimeInputElement.addDataFieldChangeListener() error: "
		        + "you should not register the same object twice.");

		m_dataFieldChangeListeners.add(dataFieldChangeListener);

		// postconditions: none. Simple addition
	}

}