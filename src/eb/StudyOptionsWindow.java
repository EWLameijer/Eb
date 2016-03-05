package eb;

import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The window in which the user can set how he/she wants to study; like which
 * time to take before the initial repetition, or what scheme repetitions will
 * take (every day, or with increasing intervals, or whatever).
 * 
 * @author Eric-Wubbo Lameijer
 */
public class StudyOptionsWindow extends JFrame {

	// Automatically generated serialVersionUID.
	private static final long serialVersionUID = -907266672997684012L;

	// Label indicating that the boxes following can be used to set the interval
	// after which a freshly created card is to be reviewed.
	private JLabel m_initialIntervalLabel;

	// Input element that allows users to view and set the interval between the
	// creation of the card and the first time it is studied.
	private TimeInputElement m_initialIntervalBox;

	// Button that closes this window, not saving any changes made.
	private JButton m_cancelButton;

	// Button that restores the defaults to those of Eb.
	private JButton m_loadEbDefaultsButton;

	// Button that reloads the current settings of the deck (undoing non-saved
	// changes).
	private JButton m_loadCurrentDeckSettingsButton;

	// Button that sets the study settings of this deck to the current values.
	private JButton m_setToTheseValuesButton;

	/**
	 * A TimeInputElement contains a textfield and combobox that allow the user to
	 * input say "5.5 minutes" or "3 hours".
	 * 
	 * @author Eric-Wubbo Lameijer
	 */
	class TimeInputElement extends JPanel {

		// Textfield that sets the quantity (the '3' in 3 hours), the combobox
		// selects the unit (the hours in "3 hours").
		private JTextField m_scalarField;

		// Combobox used (in combination with a text field) to set the value for the
		// initial study interval. Contains the hours of "3 hours".
		private JComboBox<String> m_unitComboBox;

		/**
		 * Sets the interval (displayed in the textfield + combobox) to the given
		 * time interval.
		 * 
		 * @param timeInterval
		 *          the time interval that should be displayed by this
		 *          TimeInputElement (textfield + combobox).
		 */
		public void setInterval(TimeInterval timeInterval) {
			// preconditions: timeInterval should be null
			Utilities.require(timeInterval != null,
			    "TimeInputElement.setInterval error: the provided time interval "
			        + "may not be null.");
			m_scalarField.setText(
			    Utilities.doubleToMaxPrecisionString(timeInterval.getScalar(), 2));
			m_unitComboBox
			    .setSelectedItem(timeInterval.getUnit().getUserInterfaceName());
			// postconditions: none. I assume that all goes well.
		}

		/**
		 * Constructs the TimeInputElement, given a TimeInterval (that can contain
		 * something like "3 hour(s)".
		 */
		public TimeInputElement(TimeInterval timeInterval) {
			// preconditions: none. Let the setter check the interval
			super();
			m_scalarField = new JTextField();
			add(m_scalarField);
			m_unitComboBox = new JComboBox<String>();
			m_unitComboBox
			    .setModel(new DefaultComboBoxModel<String>(TimeUnit.getUnitNames()));
			add(m_unitComboBox);

			setInterval(timeInterval);
			// postconditions: none. Ordinary constructor.
		}

	}

	/**
	 * [CPPRCCC] Closes the frame, removing all its contents. NOTE: EVIL
	 * DUPLICATION. CAN I AVOID THAT?
	 */
	private void close() {
		// preconditions: none
		this.dispose();
		// postconditions: none
	}

	/**
	 * @@@[CPPRC Loads Eb's default values.
	 */
	private void loadEbDefaults() {
		// preconditions: none
		StudyOptions defaultOptions = StudyOptions.getDefault();
		TimeInterval defaultInitialInterval = defaultOptions.getInitialInterval();

		// postconditions: none (should have worked)
	}

	/**
	 * Loads the study options settings of the current deck.
	 */
	private void loadCurrentDeckSettings() {
		// preconditions: none

		// postconditions: none
	}

	/**
	 * Creates a new Study Options window.
	 */
	public StudyOptionsWindow() {
		// preconditions: none (default constructor...)
		super("Study Options");

		setLayout(new FlowLayout());
		m_initialIntervalLabel = new JLabel("Initial review after");
		add(m_initialIntervalLabel);
		// m_initialIntervalBox = new TimeInputElement();

		m_cancelButton = new JButton("Discard changes and close");
		m_cancelButton.addActionListener(e -> close());
		add(m_cancelButton);
		m_loadEbDefaultsButton = new JButton("Load Eb's default values");
		m_loadEbDefaultsButton.addActionListener(e -> loadEbDefaults());
		add(m_loadEbDefaultsButton);
		m_loadCurrentDeckSettingsButton = new JButton(
		    "Load settings of current deck");
		m_loadCurrentDeckSettingsButton
		    .addActionListener(e -> loadCurrentDeckSettings());
		m_setToTheseValuesButton = new JButton(
		    "Set study parameters of this " + "deck to these values");
		add(m_setToTheseValuesButton);

		setSize(400, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		// postconditions: none
	}
}
