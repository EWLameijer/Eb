package eb.utilities;

import java.util.logging.Logger;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * Helps create text fields that only accept numbers and have a certain maximum
 * size.
 *
 * from:
 * http://stackoverflow.com/questions/1313390/is-there-any-way-to-accept-only-
 * numeric-values-in-a-jtextfield
 *
 * @author Terraego
 * @author Eric-Wubbo Lameijer
 *
 */
public class FixedSizeNumberDocument extends PlainDocument {
	private static final long serialVersionUID = 7355097701705745079L;
	private final JTextComponent m_owner;
	private final int m_fixedSize;
	private final int m_sizeOfFractionalPart;

	/**
	 * Constructor.
	 *
	 * @param owner
	 *          the JTextComponent that owns this instance.
	 * @param fixedSize
	 *          the maximum length of the resulting string.
	 * @param sizeOfFractionalPart
	 *          the maximum length of the fractional part (for example, 2.45 has a
	 *          fractional part of length 2)
	 */
	public FixedSizeNumberDocument(JTextComponent owner, int fixedSize,
	    int sizeOfFractionalPart) {
		// preconditions:
		Utilities.require(fixedSize > 0, "FixedSizeNumberDocument constructor "
		    + "error: the number of characters allowed must be greater than zero.");
		Utilities.require(sizeOfFractionalPart >= 0,
		    "FixedSizeNumberDocument constructor error: the number of digits in "
		        + " the fractional part cannot be negative.");
		if (sizeOfFractionalPart > 0) {
			Utilities.require(fixedSize >= (sizeOfFractionalPart + 2),
			    "FixedSizeNumberDocument constructor error: the size allotted to a real "
			        + "number must be at least two greater than the number of digits "
			        + "allotted to the fractional part.");
		}
		m_owner = owner;
		m_fixedSize = fixedSize;
		m_sizeOfFractionalPart = sizeOfFractionalPart;
	}

	/**
	 * Returns whether a string contains non-numerical characters (decimal
	 * separators are allowed, by the way).
	 *
	 * @param string
	 *          the string to be checked for whether it contains characters that
	 *          would make this an invalid number.
	 *
	 * @return whether the string contains non-digit or non-punctuation
	 *         characters.
	 */
	boolean containsOtherThanDigitsOrSeparators(String string) {
		for (final char ch : string.toCharArray()) {
			if (!Character.isDigit(ch) && !Utilities.isDecimalSeparator(ch)) {
				return true;
			}
		}
		return false;
	}

	@Override
	/**
	 * Inserts a new string into this document -unless the string is, for some
	 * reason, invalid or would give other problems.
	 */
	public void insertString(int offs, String str, AttributeSet a)
	    throws BadLocationException {

		String originalText = m_owner.getText();

		// first test: if the original string is long enough, you cannot insert.
		if (originalText.length() >= m_fixedSize) {
			m_owner.getToolkit().beep();
			return;
		}

		// what would the new text look like?
		// Note that (to my knowledge) we need to work with the
		// PlainDocument.insertString() function. This means that extra characters
		// must be removed from the inserted string itself, not from the end of the
		// original.
		StringBuilder textToBeInserted = new StringBuilder(str);
		int candidateLength = originalText.length() + textToBeInserted.length();
		if (candidateLength > m_fixedSize) {
			int numberOfCharactersToRemove = candidateLength - m_fixedSize;
			int newInsertionLength = textToBeInserted.length()
			    - numberOfCharactersToRemove;
			textToBeInserted.setLength(newInsertionLength);
			m_owner.getToolkit().beep();
		}
		StringBuilder candidateText = new StringBuilder(originalText);
		candidateText.insert(offs, textToBeInserted);

		if (representsValidContents(candidateText.toString())) {
			super.insertString(offs, textToBeInserted.toString(), a);
		} else {
			m_owner.getToolkit().beep();
			Logger.getGlobal().info("problems inserting " + str);
		}
	}

	/**
	 * Returns whether the contents of this FixedSizeNumberDocument should
	 * represent an integer, as opposed to a fractional number.
	 * 
	 * @return whether the contents of this FixedSizeNumberDocument should
	 *         represent an integer.
	 */
	private boolean contentsShouldRepresentInteger() {
		// preconditions: none. Should work when the object exists.
		return m_sizeOfFractionalPart == 0;
		// postconditions: none. Simple return of boolean.
	}

	/**
	 * Returns whether this string would return valid text box contents, so either
	 * the empty string (users must be able to clear the text box), or an integer
	 * or fractional number.
	 * 
	 * @param candidateText
	 *          the string to be checked for being a valid state of the text box.
	 * 
	 * @return whether the candidate text would be valid contents for this text
	 *         box
	 */
	private boolean representsValidContents(String candidateText) {
		// preconditions: candidateText should not be null
		Utilities.require(candidateText != null, "FixedSizeNumberDocument."
		    + "representsValidContents() error: candidateText should not be null.");
		if (candidateText == "") {
			return true; // after all, "" is a valid state for a text box.
		} else if (contentsShouldRepresentInteger()) {
			return Utilities.representsInteger(candidateText, m_fixedSize);
		} else {
			return Utilities.representsPositiveFractionalNumber(candidateText,
			    m_sizeOfFractionalPart);
		}
		// postconditions: none. Simple return of boolean.
	}
}