package eb;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * [CC] The window in which the user can set how he/she wants to study; like
 * which time to take before the initial repetition, or what scheme repetitions
 * will take (every day, or with increasing intervals, or whatever).
 * 
 * @author Eric-Wubbo Lameijer
 */
public class StudyOptionsWindow extends JFrame {

  // [CCCC] The time units used by Eb
  private static String[] TIME_UNITS = { "second(s)", "minute(s)", "hour(s)",
      "day(s)", "week(s)", "month(s)", "year(s)" };

  // [CCCC] Automatically generated serialVersionUID.
  private static final long serialVersionUID = -907266672997684012L;

  // [CCCC] Label indicating that the boxes following can be used to set the
  // interval
  // after which a freshly created card is to be reviewed.
  private JLabel m_initialIntervalLabel;

  // [CCCC] Textfield used in combination with a combobox to set the value for
  // the initial study interval. The textfield sets the quantity (the '3' in 3
  // hours), the combobox the unit (the hours in "3 hours").
  private JTextField m_initialIntervalQuantityField;

  // @@@[CCC Combobox used (in combination with a text field) to set the value
  // for
  // the initial study interval. Contains the hours of "3 hours"
  private JComboBox<String> m_initialIntervalUnitField;

  /**
   * [CPPRCCC] Creates a new Study Options window.
   */
  public StudyOptionsWindow() {
    // preconditions: none (default constructor...)
    super("Study Options");

    setLayout(new FlowLayout());
    m_initialIntervalLabel = new JLabel("Initial review after");
    add(m_initialIntervalLabel);
    m_initialIntervalQuantityField = new JTextField("10");
    add(m_initialIntervalQuantityField);
    m_initialIntervalUnitField = new JComboBox<String>(TIME_UNITS);
    add(m_initialIntervalUnitField);

    setSize(400, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
    // postconditions: none

  }
}
