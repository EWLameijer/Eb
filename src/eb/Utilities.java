package eb;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.KeyStroke;

/**
 * Contains some tools/generic methods that are not application-domain specific
 * but are nevertheless useful.
 *
 * @author Eric-Wubbo Lameijer
 */
public class Utilities {
	// Line separator that, unlike \n, consistently works when displaying
	// output
	public static final String EOL = System.getProperty("line.separator");

	/**
	 * Hide constructor, as this is a static utility class which should not be
	 * constructed at all.
	 */
	private Utilities() {
		Utilities.require(false, "Utilities constructor error: don't call the "
		    + " constructor, this is a static class");
	}

	/**
	 * Returns whether a certain string is a valid identifier - meaning that it is
	 * not null, and contains things other than whitespace.
	 *
	 * @param string
	 *          the string to be checked
	 *
	 * @return whether the string is a valid identifier
	 */
	public static boolean isStringValidIdentifier(String string) {
		// preconditions: none: all possible conditions of the input
		// are handled in this function

		// code
		if (string == null) {
			return false;
		} else {
			final String trimmedString = string.trim();
			return trimmedString.length() > 0;
		}

		// postconditions: none: a boolean has been returned, the caller
		// should handle that
	}

	/**
	 * If condition is false, exit the program while writing the specified error
	 * message to standard error output.
	 *
	 * @param condition
	 *          the condition which needs to be true if the program is to be
	 *          allowed to continue.
	 * @param errorMessage
	 *          the error message being sent to the standard error output if the
	 *          condition is false
	 */
	public static void require(boolean condition, String errorMessage) {
		// preconditions: none
		// postconditions: none
		if (!condition) {
			System.err.println(errorMessage);
			System.exit(1);
		}
	}

	/**
	 * Transfer focus when the user presses the tab key, overriding default
	 * behavior in components where tab adds a tab to the contents. After applying
	 * this function to a component, TAB transfers focus to the next focusable
	 * component, SHIFT+TAB transfers focus to the previous focusable component.
	 *
	 * @param component
	 *          the component to be patched
	 */
	public static void makeTabTransferFocus(Component component) {
		// preconditions: none
		Set<KeyStroke> strokes = new HashSet<>(
		    Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
		    strokes);
		strokes = new HashSet<>(
		    Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
		component.setFocusTraversalKeys(
		    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
		// postconditions: none
	}

	/**
	 * Transfers focus when the user presses the tab or enter keys, overriding
	 * default behavior in components where pressing the tab key adds a tab and
	 * pressing the enter key adds a newline to the contents.
	 *
	 * @param component
	 *          the component to be patched.
	 */
	public static void makeTabAndEnterTransferFocus(Component component) {
		// preconditions: none
		Set<KeyStroke> strokes = new HashSet<>(
		    Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		strokes.addAll(Arrays.asList(KeyStroke.getKeyStroke("pressed ENTER")));
		component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
		    strokes);
		strokes = new HashSet<>(
		    Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
		component.setFocusTraversalKeys(
		    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
		// postconditions: none
	}

	/**
	 * Converts a floating point number to a string with a maximum precision, but
	 * does so in a display-friendly way, so that, if the precision is 2, for
	 * example, not 10.00 is displayed, but 10.
	 *
	 * @param number
	 *          the number that is to be converted to a string.
	 * @param maxPrecision
	 *          the maximum number of digits after the period, fewer may be
	 *          displayed if the last digits would be 0.
	 *
	 * @return the number, with given maximum precision, in String format.
	 */
	public static String doubleToMaxPrecisionString(double number,
	    int maxPrecision) {
		// preconditions: maxPrecision should be 0 or greater
		Utilities.require(maxPrecision >= 0,
		    "Utilities.doubleToMaxPrecisionString error: the given precision "
		        + "should be 0 or positive.");

		// 1. Build the format String
		final DecimalFormat numberFormatter = new DecimalFormat();
		numberFormatter.setMaximumFractionDigits(maxPrecision);
		numberFormatter.setRoundingMode(RoundingMode.HALF_UP);
		return numberFormatter.format(number);

		// postconditions: none. Should simply return the String, and I trust that
		// that works.
	}

	/**
	 * CPPRCC Is this character the decimal separator of the current locale?
	 * 
	 * @param ch
	 *          the character to be tested as being this locale's decimal
	 *          separator
	 * 
	 * @return whether the character is this locale's decimal separator
	 */
	public static boolean isDecimalSeparator(char ch) {
		// preconditions: none. 'ch' can't be null, for example.
		DecimalFormat currentNumberFormat = new DecimalFormat();
		char decimalSeparator = currentNumberFormat.getDecimalFormatSymbols()
		    .getDecimalSeparator();
		return (ch == decimalSeparator);
		// postconditions: none. Simple return of boolean.
	}

	/**
	 * Returns the decimal separator of this locale.
	 * 
	 * @return this locale's decimal separator.
	 */
	public static char getDecimalSeparator() {
		DecimalFormat currentNumberFormat = new DecimalFormat();
		return currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator();
	}

	/**
	 * Whether the given string is fully filled with a valid integer
	 * (...-2,-1,0,1,2,...). Note that this method does not accept leading or
	 * trailing whitespace, nor a '+' sign.
	 * 
	 * @param string
	 *          the string to be tested
	 * @return whether the string is a string representation of an integer.
	 */
	public static boolean representsInteger(String string) {
		// preconditions: string should not be null or empty.
		return Pattern.matches("-?\\d+", string);
		// postconditions: none: simple return of boolean.
	}

	/**
	 * @@@CPPRC Whether the given string is fully filled with a valid fractional
	 *          number of a given maximum precision (like -2.1, or 5.17 or 10, or
	 *          .12). Note that this method does not accept leading or trailing
	 *          whitespace, nor a '+' sign.
	 * 
	 * @param string
	 *          the string to be tested
	 * @param maxPrecision
	 *          the maximum precision (maximum number of digits) in the fractional
	 *          part.
	 * 
	 * @return whether the string is a string representation of a fractional
	 *         number.
	 */
	public static boolean representsFractionalNumber(String string,
	    int maxPrecision) {
		// preconditions: string should not be null or empty.
		if (!isStringValidIdentifier(string)) {
			return false;
		}
		String fractionalNumberRegex = 
		
		// @@@Replace by Regex!
		int pos = 0;
		if (string.charAt(pos) == '-') {
			pos++;
		}
		boolean digitSeparatorFound = false;
		int digitsInFractionalPart = 0;
		boolean digitFound = false;
		while (pos < string.length()) {
			char currentChar = string.charAt(pos);
			if (Character.isDigit(currentChar)) {
				digitFound = true;
				if (digitSeparatorFound) {
					digitsInFractionalPart++;
				} // else: do nothing - only invoke this for the fractional part
				if (digitsInFractionalPart > maxPrecision) {
					return false;
				} // else: still below max precision; number is acceptable.
			} else if (isDecimalSeparator(currentChar)) {
				if (!digitSeparatorFound) {
					digitSeparatorFound = true;
				} else {
					return false; // you can't have two decimal separators in a number!
				}
			} else {
				// neither a digit or a separator
				return false;
			}
		}
		return digitFound;
		// postconditions: none: simple return of boolean.
	}

	/**
	 * @@@ Whether a given string represents a positive fractional number.
	 * 
	 * @param string
	 * @param maxPrecision
	 * @return
	 */
	/*
	 * public static boolean representsPositiveFractionalNumber(String string, int
	 * maxPrecision) {
	 * 
	 * }
	 */
}
