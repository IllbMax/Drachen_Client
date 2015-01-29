package com.vsis.drachen.util;

import java.util.Arrays;

/**
 * Helping function all around String
 * 
 */
public class StringFunction {

	private StringFunction() {
	}

	/**
	 * Calculates the levenstein norm on two strings (char array) it compares
	 * char wise so it counts 1 for every wrong char
	 * 
	 * @param s
	 *            first array of chars you want to compare
	 * @param t
	 *            second array of chars you want to compare
	 * @return the levensteinnorm with all weights 1
	 */
	public static int leventsteinNorm(String s, String t) {
		// degenerate cases
		if (s.equals(t))
			return 0;
		if (s.length() == 0)
			return t.length();
		if (t.length() == 0)
			return s.length();

		// create two work vectors of integer distances
		int[] v0 = new int[t.length() + 1];
		int[] v1 = new int[t.length() + 1];

		// initialize v0 (the previous row of distances)
		// this row is A[0][i]: edit distance for an empty s
		// the distance is just the number of characters to delete from t
		for (int i = 0; i < v0.length; i++)
			v0[i] = i;

		for (int i = 0; i < s.length(); i++) {
			// calculate v1 (current row distances) from the previous row v0

			// first element of v1 is A[i+1][0]
			// edit distance is delete (i+1) chars from s to match empty t
			v1[0] = i + 1;

			// use formula to fill in the rest of the row
			for (int j = 0; j < t.length(); j++) {
				int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
				v1[j + 1] = Minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
			}

			// copy v1 (current row) to v0 (previous row) for next iteration
			for (int j = 0; j < v0.length; j++)
				v0[j] = v1[j];
		}

		return v1[t.length()];

	}

	// private static int Minimum(int i, int j, int k) {
	// return Math.min(i, Math.min(j, k));
	// }

	public static int Minimum(int... ints) {
		if (ints.length == 0)
			throw new RuntimeException();
		int min = ints[0];
		for (int i : ints) {
			if (min > i)
				min = i;
		}
		return min;
	}

	/**
	 * Calculates the levenstein norm on two word lists (string array) it
	 * compares word wise so it counts 1 for every wrong word (two words are not
	 * equals if at least one character isn't maching)
	 * 
	 * @param words1
	 *            first array of words you want to compare
	 * @param words2
	 *            second array of words you want to compare
	 * @return the levensteinnorm with all weights 1
	 */
	public static int leventsteinNorm(String[] words1, String[] words2) {
		// degenerate cases
		if (Arrays.equals(words1, words2))
			return 0;
		if (words1.length == 0)
			return words2.length;
		if (words2.length == 0)
			return words1.length;

		// create two work vectors of integer distances
		int[] v0 = new int[words2.length + 1];
		int[] v1 = new int[words2.length + 1];

		// initialize v0 (the previous row of distances)
		// this row is A[0][i]: edit distance for an empty s
		// the distance is just the number of characters to delete from t
		for (int i = 0; i < v0.length; i++)
			v0[i] = i;

		for (int i = 0; i < words1.length; i++) {
			// calculate v1 (current row distances) from the previous row v0

			// first element of v1 is A[i+1][0]
			// edit distance is delete (i+1) chars from s to match empty t
			v1[0] = i + 1;

			// use formula to fill in the rest of the row
			for (int j = 0; j < words2.length; j++) {
				int cost = (words1[i].equals(words2[j])) ? 0 : 1;
				v1[j + 1] = Minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
			}

			// copy v1 (current row) to v0 (previous row) for next iteration
			for (int j = 0; j < v0.length; j++)
				v0[j] = v1[j];
		}

		return v1[words2.length];
	}

	/**
	 * returns true if the string is null or empty
	 * 
	 * @param string
	 *            tested string
	 * @return true if the string is null or empty
	 */
	public static boolean nullOrEmpty(String string) {
		return string == null || string.equals("");
	}

	/**
	 * returns true if the string is null or empty or whitespace
	 * 
	 * @param string
	 *            tested string
	 * @return true if the string is null or empty or whitespace
	 */
	public static boolean nullOrWhiteSpace(String string) {
		return string == null || string.trim().equals("");
	}
}
