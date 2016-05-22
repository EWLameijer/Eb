package eb.subwindow;

import java.awt.Container;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import eb.utilities.ui_elements.LabelledTextField;

public class ArchivingSettingsWindow extends JFrame {

	LabelledTextField m_archivingLocation;
	JButton m_changeLocationButton;

	ArchivingSettingsWindow() {
		super("Deck archiving settings");
		m_archivingLocation = new LabelledTextField("Location for archive files: ");
		m_changeLocationButton = new JButton("Change location for archive files");
		m_changeLocationButton.addActionListener(e -> changeArchivingLocation());
	}

	private void changeArchivingLocation() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		} else {

		}

	}

	private void init() {
		Container box = Box.createHorizontalBox();
		box.add(m_archivingLocation);
		box.add(m_changeLocationButton);
		add(box);
		setSize(700, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public static void display() {
		final ArchivingSettingsWindow archivingSettingsWindow = new ArchivingSettingsWindow();
		archivingSettingsWindow.init();
	}

}
