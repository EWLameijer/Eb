package learning_software;

class SettingsParser {
	class VariableData {
		private String m_variableIdentifier; // like "LNF"
		private String m_variableName; // like "m_lookAndFeelClassName"
		private String m_typeName; // like "String"
		// the above may therefore encode a variable called 
		// String m_lookAndFeelClassName, and read/write it as [LNF]...
		public String getVariableIdentifier() {
			return m_variableIdentifier;
		}
		public void setVariableIdentifier(String variableIdentifier) {
			m_variableIdentifier = variableIdentifier;
		}
		public String getVariableName() {
			return m_variableName;
		}
		public void setVariableName(String variableName) {
			m_variableName = variableName;
		}
		public String getTypeName() {
			return m_typeName;
		}
		public void setTypeName(String typeName) {
			m_typeName = typeName;
		}
	}
	
	// now ensure that we also have a link to the name of the parent class
	// (like GlobalSettings for the m_lookAndFeelClassName)
	private String m_parentClassName;
	
	public void initialize(Object object) {
		m_parentClassName = object.getClass().getName();
		System.out.println(m_parentClassName);
	}
	
	@SuppressWarnings("rawtypes")
	public void initializeStatic(Class staticClass) {
		m_parentClassName = staticClass.getName();
		System.out.println(m_parentClassName);
	}
	
	public void addField(String variableIdentifier, String variableName, Class 
			variableType) {
		// check if not already in list
	}
	
}
