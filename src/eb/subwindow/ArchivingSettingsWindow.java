package eb.subwindow;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import eb.data.Deck;
import eb.utilities.ProgrammableAction;

@SuppressWarnings("serial")
public class ArchivingSettingsWindow extends JFrame {

	JLabel m_archivingLocation;
	JButton m_changeLocationButton;

	ArchivingSettingsWindow() {
		super("Deck archiving settings");
		String archivingDirectoryName = Deck.getArchivingDirectoryName();
		String displayedDirectoryName;
		if (archivingDirectoryName.isEmpty()) {
			displayedDirectoryName = "[default]";
		} else {
			displayedDirectoryName = archivingDirectoryName;
		}
		m_archivingLocation = new JLabel(
		    "Location for archive files: " + displayedDirectoryName);
		m_changeLocationButton = new JButton("Change location for archive files");
		m_changeLocationButton.addActionListener(e -> changeArchivingLocation());
	}

	private void changeArchivingLocation() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		} else {
			File selectedDirectory = chooser.getSelectedFile();
			Deck.setArchivingDirectory(selectedDirectory);
			m_archivingLocation.setText(selectedDirectory.getAbsolutePath());
		}

	}

	private void init() {
		Container box = Box.createHorizontalBox();
		box.add(m_archivingLocation);
		box.add(Box.createHorizontalStrut(10));
		box.add(m_changeLocationButton);
		add(box);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
		getRootPane().getActionMap().put("Cancel",
		    new ProgrammableAction(() -> this.dispose()));
		setSize(700, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public static void display() {
		final ArchivingSettingsWindow archivingSettingsWindow = new ArchivingSettingsWindow();
		archivingSettingsWindow.init();
	}

}
