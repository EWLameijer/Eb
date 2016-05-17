package eb;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabelledComboBox extends JPanel {
	private final JLabel m_label;
	private final JComboBox<String> m_comboBox;
	
	LabelledComboBox(String labelText, String[] comboBoxContents) {
		Utilities.require( Utilities.isStringValidIdentifier(labelText), 
				"LabelledComboBox constructor error: the text for the label cannot be empty.");
		for (String comboBoxElement: comboBoxContents ) {
			Utilities.require( Utilities.isStringValidIdentifier(labelText), 
					"LabelledComboBox constructor error: the text for a combo box element " + 
			"cannot be empty.");
		}
		m_label = new JLabel( labelText );
		m_comboBox = new JComboBox<>(comboBoxContents);
		add( m_label );
		add( m_comboBox );
	}
}
