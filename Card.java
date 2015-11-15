package learning_software;

import java.io.Serializable;

class Card implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_front;
	private String m_back;
	// okay... after this will need to come the history
	// Created
	// List of reviews
	
	Card(String front, String back) {
		m_front = front;
		m_back = back;
	}
	
	String getFront() {
		return m_front;
	}
	
	String getBack() {
		return m_back;
	}
	
	public boolean hasFront(String front) {
		return ( front.equals(m_front) ); 
	}
}
