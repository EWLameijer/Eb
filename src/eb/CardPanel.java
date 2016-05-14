package eb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * A CardPanel shows a side of a card (either the front or the back). It is
 * basically a graphical display used during the review process, unlike the
 * TextAreas used for input.
 * 
 * @author Eric-Wubbo Lameijer
 */
@SuppressWarnings("serial")
public class CardPanel extends JPanel {

	// the area on which the text is to be displayed
	private final JTextPane m_textPane;

	/**
	 * CardPanel constructor.
	 */
	CardPanel() {
		super();
		setLayout(new BorderLayout());
		m_textPane = new JTextPane();
		m_textPane.setEditable(false);
		m_textPane.setFont(new Font("Arial", Font.PLAIN, 30));
		m_textPane.setBorder(BorderFactory.createLineBorder(Color.black));

		// from
		// http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment

		m_textPane.setEditorKit(new MyEditorKit());
		StyledDocument doc = m_textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		add(new JScrollPane(m_textPane), BorderLayout.CENTER);
	}

	/**
	 * Sets the text to be displayed in this panel. Can be an empty string (if the
	 * panel must yet remain empty).
	 * 
	 * @param text
	 *          the text to display in this panel.
	 */
	void setText(String text) {
		Utilities.require(text != null,
		    "CardPanel.setText() error: the text cannot be null.");
		m_textPane.setText(text);
		repaint();
	}

}

@SuppressWarnings("serial")
class MyEditorKit extends StyledEditorKit {

	@Override
	public ViewFactory getViewFactory() {
		return new StyledViewFactory();
	}

	static class StyledViewFactory implements ViewFactory {

		@Override
		public View create(Element elem) {
			String kind = elem.getName();
			View view = null;
			if (kind != null) {
				switch (kind) {
				case AbstractDocument.ContentElementName:
					view = new LabelView(elem);
					break;
				case AbstractDocument.ParagraphElementName:
					view = new ParagraphView(elem);
					break;
				case AbstractDocument.SectionElementName:
					view = new CenteredBoxView(elem, View.Y_AXIS);
					break;
				case StyleConstants.ComponentElementName:
					view = new ComponentView(elem);
					break;
				case StyleConstants.IconElementName:
					view = new IconView(elem);
					break;
				default:
					Utilities.require(false,
					    "StyledViewFactory.create error: " + "unknown element name");
				}
			}
			if (view == null) {
				view = new LabelView(elem);
			}
			return view;
		}

	}
}

class CenteredBoxView extends BoxView {
	public CenteredBoxView(Element elem, int axis) {

		super(elem, axis);
	}

	@Override
	protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
	    int[] spans) {

		super.layoutMajorAxis(targetSpan, axis, offsets, spans);
		int textBlockHeight = 0;

		for (int i = 0; i < spans.length; i++) {

			textBlockHeight = spans[i];
		}
		if (textBlockHeight * offsets.length < targetSpan) {
			int offset = (targetSpan - textBlockHeight) / 2;
			for (int i = 0; i < offsets.length; i++) {
				offsets[i] += offset;
			}
		}
	}
}
