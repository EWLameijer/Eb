package eb.utilities.ui_elements;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eb.eventhandling.BlackBoard;
import eb.eventhandling.DelegatingDocumentListener;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.utilities.FixedSizeNumberDocument;

@SuppressWarnings("serial")
public class LabelledTextField extends JPanel {
	private final JLabel m_label;
	private final JTextField m_textField;

	public LabelledTextField(String labelText, String textFieldContents, int size,
	    int precision) {
		m_label = new JLabel(labelText);
		m_textField = new JTextField();
		m_textField.setPreferredSize(new Dimension(40, 20));
		m_textField
		    .setDocument(new FixedSizeNumberDocument(m_textField, size, precision));
		m_textField.getDocument()
		    .addDocumentListener(new DelegatingDocumentListener(
		        () -> BlackBoard.post(new Update(UpdateType.INPUTFIELD_CHANGED))));
		m_textField.setText(textFieldContents);
		add(m_label);
		add(m_textField);
	}

	/**
	 * Constructor for making a non-numeric text field (that is, a text field that
	 * contains an ordinary string)
	 * 
	 * @param labelText
	 *          the text of the label
	 */
	public LabelledTextField(String labelText) {
		m_label = new JLabel(labelText);
		m_textField = new JTextField("EMPTY");
		m_textField.setPreferredSize(new Dimension(40, 20));
		add(m_label);
		add(m_textField);
	}

	public String getContents() {
		return m_textField.getText();
	}

	public void setContents(String text) {
		m_textField.setText(text);
	}

	public void setContents(int i) {
		setContents(String.valueOf(i));
	}

	public void setContents(double d) {
		setContents(String.valueOf(d));
	}
}
