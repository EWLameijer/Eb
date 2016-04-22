package eb;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CardPanel extends JPanel {
	String m_contents;

	CardPanel() {
		super();
		m_contents = "";
	}

	void setText(String text) {
		Utilities.require(text != null,
		    "CardPanel.setText() error: the text cannot be null.");
		m_contents = text;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Font font = new Font("Arial", Font.PLAIN, 30);
		g2.setFont(font);
		int panelHeight = getHeight();
		int panelWidth = getWidth();
		System.out.println("PanelWidth: " + panelWidth);
		FontMetrics fontMetrics = g.getFontMetrics(font);
		int stringWidth = fontMetrics.stringWidth(m_contents);
		int stringHeight = fontMetrics.getHeight();
		g2.drawString(m_contents, (panelWidth - stringWidth) / 2,
		    (panelHeight - stringHeight) / 2 + fontMetrics.getAscent());
	}

}
