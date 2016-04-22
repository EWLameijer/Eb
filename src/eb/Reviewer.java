package eb;

import java.util.ArrayList;
import java.util.List;

public class Reviewer {

	private static List<Card> c_cardCollection = new ArrayList<>();
	private static int c_counter = 3;

	public static String getCurrentFront() {
		return "Mijn naam is";
	}

	public static String getCurrentBack() {
		return "Repelsteeltje";
	}

	public static void wasRemembered(boolean remembered) {
		c_counter--;
		if (c_counter <= 0) {

		}
	}

}
