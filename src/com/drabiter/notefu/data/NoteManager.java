package com.drabiter.notefu.data;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;

import com.drabiter.notefu.utils.DataWriterReader;

public class NoteManager {

	public static String defaultPath = System.getProperty("user.home")
			.concat(System.getProperty("file.separator"))
			.concat("myndata")
			.concat(System.getProperty("file.separator"));

	private HashMap<String, String> notes;
	
//	private ArrayList<String> deletes;

	private DefaultListModel listModel;

	public NoteManager(HashMap<String, String> notes) {
		this.notes = notes;
		listModel = new DefaultListModel();
//		deletes = new ArrayList<String>();

		for (String key : this.notes.keySet())
			listModel.add(0, key);
	}

//	public void persist() {
//		for (String delete : deletes){
//			if (!DataWriterReader.deleteIfExists(defaultPath, delete))
//				System.out.println("cant-del! ");
//		}
//		deletes.clear();
//		
//		for (Entry<String, String> entry : notes.entrySet()){
//			if ("null".equals(entry.getKey())){
//				notes.remove(entry.getKey());
//				continue;
//			}
//			DataWriterReader.saveNote(entry.getKey(), entry.getValue(), defaultPath);
//		}
//	}

	public static NoteManager load() {
		System.out.println("load notes!");
		return new NoteManager(DataWriterReader.loadNotes(defaultPath));
	}

	public DefaultListModel getListModel() {
		return listModel;
	}

	public String getContent(int index) {
		// TODO Auto-generated method stub
		return notes.get(listModel.get(index));
	}

	public int addNewNote(String newNoteName) {
		// TODO Auto-generated method stub
		int indexForNew = 0;
		listModel.add(indexForNew, newNoteName);
		notes.put(newNoteName, "");
		return indexForNew;
	}

	public void saveContent(String selectedValue, String text) {
		// TODO Auto-generated method stub
		if (selectedValue == null) return;
		if (notes.containsKey(selectedValue)){
			notes.remove(selectedValue);
			notes.put(selectedValue, text);
			DataWriterReader.saveNote(selectedValue, text, defaultPath);
		}
	}

	public boolean delete(String selectedValue) {
		if (notes.containsKey(selectedValue)) {
			notes.remove(selectedValue);
			listModel.removeElement(selectedValue);
//			deletes.add(selectedValue);
			DataWriterReader.deleteIfExists(defaultPath, selectedValue);
			return true;
		}
		return false;
	}
	
	public int getSize(){
		return listModel.size();
	}

	public Point search(char[] charArray) {
		// TODO Auto-generated method stub
		Point ret = new Point();
		int index;
		for (Map.Entry<String, String> entry : notes.entrySet()) {
			index = indexOf(notes.get(entry.getKey()).toCharArray(), charArray);
			if (index >= 0) {
				ret.x = listModel.indexOf(entry.getKey());
				ret.y = index;
				return ret;
			}
		}
		return null;
	}

	/**
	 * All methods below this comment block were copied from wikipedia,
	 * demonstrating Boyer–Moore string search algorithm.
	 */

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. If it is not a substring, return -1.
	 * 
	 * @param haystack
	 *            The string to be scanned
	 * @param needle
	 *            The target string to search
	 * @return The start index of the substring
	 */
	public static int indexOf(char[] haystack, char[] needle) {
		if (needle.length == 0) {
			return 0;
		}
		int charTable[] = makeCharTable(needle);
		int offsetTable[] = makeOffsetTable(needle);
		for (int i = needle.length - 1, j; i < haystack.length;) {
			for (j = needle.length - 1; needle[j] == haystack[i]; --i, --j) {
				if (j == 0) {
					return i;
				}
			}
			// i += needle.length - j; // For naive method
			i += Math.max(offsetTable[needle.length - 1 - j],
					charTable[haystack[i]]);
		}
		return -1;
	}

	/**
	 * Makes the jump table based on the mismatched character information.
	 */
	private static int[] makeCharTable(char[] needle) {
		final int ALPHABET_SIZE = 256;
		int[] table = new int[ALPHABET_SIZE];
		for (int i = 0; i < table.length; ++i) {
			table[i] = needle.length;
		}
		for (int i = 0; i < needle.length - 1; ++i) {
			table[needle[i]] = needle.length - 1 - i;
		}
		return table;
	}

	/**
	 * Makes the jump table based on the scan offset which mismatch occurs.
	 */
	private static int[] makeOffsetTable(char[] needle) {
		int[] table = new int[needle.length];
		int lastPrefixPosition = needle.length;
		for (int i = needle.length - 1; i >= 0; --i) {
			if (isPrefix(needle, i + 1)) {
				lastPrefixPosition = i + 1;
			}
			table[needle.length - 1 - i] = lastPrefixPosition - i
					+ needle.length - 1;
		}
		for (int i = 0; i < needle.length - 1; ++i) {
			int slen = suffixLength(needle, i);
			table[slen] = needle.length - 1 - i + slen;
		}
		return table;
	}

	/**
	 * Is needle[p:end] a prefix of needle?
	 */
	private static boolean isPrefix(char[] needle, int p) {
		for (int i = p, j = 0; i < needle.length; ++i, ++j) {
			if (needle[i] != needle[j]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the maximum length of the substring ends at p and is a suffix.
	 */
	private static int suffixLength(char[] needle, int p) {
		int len = 0;
		for (int i = p, j = needle.length - 1; i >= 0 && needle[i] == needle[j]; --i, --j) {
			len += 1;
		}
		return len;
	}

}
