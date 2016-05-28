package eb.utilities.ui_elements;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import eb.utilities.Utilities;

@SuppressWarnings("serial")
public class LabelledComboBox extends InputPanel {
	private final JLabel m_label;
	private final JComboBox<String> m_comboBox;

	public LabelledComboBox(String labelText, String[] comboBoxContents) {
		Utilities.require(Utilities.isStringValidIdentifier(labelText),
		    "LabelledComboBox constructor error: the text for the label cannot be empty.");
		for (String comboBoxElement : comboBoxContents) {
			Utilities.require(Utilities.isStringValidIdentifier(comboBoxElement),
			    "LabelledComboBox constructor error: the text for a combo box element "
			        + "cannot be empty.");
		}
		m_label = new JLabel(labelText);
		m_comboBox = new JComboBox<>(comboBoxContents);
		m_comboBox.addActionListener(e -> notifyOfChange());
		add(m_label);
		add(m_comboBox);
	}

	@Override
	boolean equals(InputPanel otherInputPanel) {
		if (this == otherInputPanel) {
			return true;
		} else if (otherInputPanel == null) {
			return false;
		} else if (this.getClass() != otherInputPanel.getClass()) {
			return false;
		} else {
			LabelledComboBox otherLabelledComboBox = (LabelledComboBox) otherInputPanel;
			return m_comboBox.getSelectedItem()
			    .equals(otherLabelledComboBox.m_comboBox.getSelectedItem());
		}
	}

	public void setTo(String item) {
		m_comboBox.setSelectedItem(item);
	}

	public String getValue() {
		return (String) m_comboBox.getSelectedItem();
	}
}
