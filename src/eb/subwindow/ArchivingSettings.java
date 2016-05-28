package eb.subwindow;

import java.io.File;

public class ArchivingSettings {

	private File m_archivingDirectory;

	private ArchivingSettings() {
		m_archivingDirectory = null;
	}

	public String getDirectoryName() {
		if (m_archivingDirectory == null) {
			return "";
		} else {
			return m_archivingDirectory.getAbsolutePath();
		}
	}

	public static ArchivingSettings getDefault() {
		return new ArchivingSettings();
	}

	public void setDirectory(File directory) {
		m_archivingDirectory = directory;
	}

}
