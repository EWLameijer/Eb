package eb;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	public static boolean isDecimalSeparator(char ch) {
		DecimalFormat currentNumberFormat = new DecimalFormat();
		char decimalSeparator = currentNumberFormat.getDecimalFormatSymbols().getDecimalSeparator();
		return (ch == decimalSeparator);
	}

}
