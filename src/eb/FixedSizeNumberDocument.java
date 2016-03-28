package eb;

import java.text.NumberFormat;
import java.text.ParseException;

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
@SuppressWarnings("serial")
public class FixedSizeNumberDocument extends PlainDocument {
	/**
	 *
	 */
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

    if (containsOtherThanDigitsOrSeparators(str)) {
			m_owner.getToolkit().beep();
			return;
		}
		if (getLength() + str.length() > m_fixedSize) {
			str = str.substring(0, m_fixedSize - getLength());
			m_owner.getToolkit().beep();
		}

		//@@@: use NumberFormat instead of parseDouble.
		// see http://www.ibm.com/developerworks/library/j-numberformat/
		NumberFormat floatingPointFormat = NumberFormat.getNumberInstance();
		NumberFormat integerFormat = NumberFormat.getIntegerInstance();
		
		String candidateText = m_owner.getText() + str;
		try {
			if (m_sizeOfFractionalPart > 0) {
				floatingPointFormat.parse(candidateText);
			} else {
				integerFormat.parse(candidateText);
			}
		} catch (ParseException e) {
			// inserted text is not a number
			m_owner.getToolkit().beep();
			e.printStackTrace();
			return;
		
	}
		super.insertString(offs, str, a);
	}
}