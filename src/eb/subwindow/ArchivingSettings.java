package eb.subwindow;

import java.io.File;
import java.io.Serializable;

public class ArchivingSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File m_archivingDirectory;

	public ArchivingSettings() {
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
		if (!directory.exists()) {
			directory.mkdir();
		}
		m_archivingDirectory = directory;
	}

}
