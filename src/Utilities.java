package eb;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.KeyStroke;

/**
 * [CCCC] Contains some tools/generic methods that are not application-domain
 * specific but are nevertheless useful.
 *
 * @author Eric-Wubbo Lameijer
 */
public class Utilities {
  // [CCCC] line separator that, unlike \n, consistently works when displaying
  // output
  public static final String EOL = System.getProperty("line.separator");

  /**
   * [CPPRCCC] Returns whether a certain string is a valid identifier - meaning
   * that it is not null, and contains things other than whitespace.
   * 
   * @param string
   *          the string to be checked
   * @return whether the string is a valid identifier
   */
  public static boolean isStringValidIdentifier(String string) {
    // preconditions: none: all possible conditions of the input
    // are handled in this function

    // code
    if (string == null) {
      return false;
    } else {
      String trimmedString = string.trim();
      return (trimmedString.length() > 0);
    }

    // postconditions: none: a boolean has been returned, the caller
    // should handle that
  }

  /**
   * [CPPRCCC] If condition is false, exit the program while writing the
   * specified error message to stdout.
   * 
   * @param condition
   *          the condition which needs to be true if the program is to be
   *          allowed to continue its operations.
   * @param errorMessage
   *          the error message being sent to stdout if the condition is false
   */
  public static void require(boolean condition, String errorMessage) {
    // preconditions: none
    // postconditions: none
    if (condition == false) {
      System.out.println(errorMessage);
      System.exit(1);
    }
  }

  /**
   * [CPPRCCC] This function transfers focus when the user presses the tab key,
   * overriding default behavior in components where tab adds a tab to the
   * contents. After applying this function to a component, TAB transfers focus
   * to the next focusable component, SHIFT+TAB transfers focus to the previous
   * focusable component.
   * 
   * @param c
   *          The component to be patched.
   */
  public static void makeTabTransferFocus(Component c) {
    // preconditions: none
    Set<KeyStroke> strokes = new HashSet<KeyStroke>(
        Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
    c.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
        strokes);
    strokes = new HashSet<KeyStroke>(
        Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
    c.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
        strokes);
    // postconditions: none
  }

  /**
   * [CPPRCCC] This function transfers focus when the user presses the tab or
   * enter keys, overriding default behavior in components where tab adds a tab
   * and enter adds a newline to the contents.
   * 
   * @param c
   *          the component to be patched.
   */
  public static void makeTabAndEnterTransferFocus(Component c) {
    // preconditions: none
    Set<KeyStroke> strokes = new HashSet<KeyStroke>(
        Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
    strokes.addAll(Arrays.asList(KeyStroke.getKeyStroke("pressed ENTER")));
    c.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
        strokes);
    strokes = new HashSet<KeyStroke>(
        Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
    c.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
        strokes);
    // postconditions: none
  }

}
