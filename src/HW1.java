import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;



/**
 * @author Zelun Jiang
 *
 */

public class HW1 {

	// The file that contains stop words
	public static String stopWords_file = "D:\\UWT\\554\\HW1\\stop-words.txt";

	// The folder that contains files to read
	public static final File file_folder = new File("D:\\UWT\\554\\HW1\\transcripts");

	public static ArrayList<String> stopWords = new ArrayList<String>();
	public static Map<String, Integer> database_words = new HashMap<String, Integer>();
	public static Map<String, Integer> database_docs = new HashMap<String, Integer>();
	public static int wordTokens = 0;
	public static int fileNum = 0;

	/**
	 * Read the files in the target folder
	 * 
	 * @param folder target folder
	 * @throws IOException
	 */
	public static void listFilesForFolder(final File folder) throws IOException {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
				fileNum++;
				readFile(fileEntry.getPath());
			} else {
				fileNum++;
				// System.out.print(fileEntry.getName() + " ");
				ArrayList<String> helper = readFile(fileEntry.getPath());
				updateDocs(helper, database_docs);

			}
		}
		System.out.println("Total Number of Files: " + fileNum);
	}

	
	
	/**
	 * Update the number of appearing documents 
	 * of each word token to a map
	 *  
	 * @param tmp the list of all word tokens in a single doc
	 * @param database_docs target map with the word token as key and doc number as value
	 */
	public static void updateDocs(ArrayList<String> tmp, Map<String, Integer> database_docs) {
		for (int i = 0; i < tmp.size(); i++) {
			String word = tmp.get(i);
			putWords(word, database_docs);

		}
	}

	/**
	 * Import stop words from target text file
	 * 
	 * 
	 * @param stopWordsFile The text file that contains wtop words
	 * @throws FileNotFoundException
	 */
	public static void importStopWords(String stopWordsFile) throws FileNotFoundException {
		Scanner sc = new Scanner(new FileInputStream(stopWordsFile));
		while (sc.hasNext()) {
			stopWords.add(sc.next());
		}

		sc.close();
	}

	/**
	 * Remove a word if it is a stop word
	 * 
	 * @param s the string that needs to be scanned
	 * @return
	 */
	public static String removeStopWords(String s) {
		if (stopWords.contains(s)) {
			return "";
		} else {
			return s;
		}

	}

	
	/**
	 * Count the words that only appear once from a map 
	 * and return the number
	 * 
	 * @param m resource map 
	 * @return
	 */
	public static int wordsOnce(Map<String, Integer> m) {
		int count = 0;
		int size = m.size();
		Object[] al = m.values().toArray();
		for (int i = 0; i < size; i++) {
			if (al[i].equals(1)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Stem a word
	 * 
	 * @param s
	 * @return
	 */
	public static String PorterStemmer(String s) {
		opennlp.tools.stemmer.PorterStemmer ps = new opennlp.tools.stemmer.PorterStemmer();

		String word = ps.stem(s);

		return word;
	}

	
	/**
	 * Put a word to the map
	 * 
	 * @param s
	 * @param m
	 */
	public static void putWords(String s, Map<String, Integer> m) {
		if (m.containsKey(s)) {
			int i = (int) m.get(s);
			i++;
			m.put(s, i);
		} else {
			m.put(s, 1);
		}
	}

	/**
	 * Read a single doc and scan the words
	 * Remove stop words
	 * Remove special characters
	 * Porter stem the word
	 * 
	 * 
	 * @param pathToFile
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> readFile(String pathToFile) throws IOException {
		ArrayList<String> tmp = new ArrayList<String>();
		try (Scanner sc = new Scanner(new FileInputStream(pathToFile))) {

			while (sc.hasNext()) {

				// remove stop words
				String word = removeStopWords(sc.next().toLowerCase());

				// remove special characters
				word = word.replaceAll("\\W+", "");

				if (!word.equals("")) {

					// Porter Stem
					word = PorterStemmer(word);

					if (!word.equals("")) {
						// System.out.println(word);
						wordTokens++;
						putWords(word, database_words);

						if (!tmp.contains(word)) {
							tmp.add(word);
						}

					}

				}

			}
			// System.out.println("Number of words: " + count);
		}

		return tmp;
	}

	/**
	 * Sort the map in descending order
	 * 
	 * 
	 * @param map
	 * @param num
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> entriesSortedByValues(Map<K, V> map, int num) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (int i = 0; i < num; i++) {
			result.put(sortedEntries.get(i).getKey(), sortedEntries.get(i).getValue());
		}

		return result;
	}

	
	/**
	 * Caculate TF, IDF, TF*IDF and the probablity
	 * 
	 * @param m
	 */
	public static void TFIDF(Map<String, Integer> m) {
		for (Object s : m.keySet().toArray()) {
			double TF = m.get(s);
			double dft = database_docs.get(s);

			double IDF = Math.log10(fileNum / dft);
			double TFIDF = TF * IDF;
			double pro = TF / wordTokens;
			// System.out.println(fileNum / dft);

			System.out.println("Word= " + s + " TF= " + TF + " dft=" + dft + " IDF= " + IDF + " TF*IDF= " + TFIDF
					+ " Probability= " + pro);
		}
	}


	public static void main(String[] args) throws IOException {

		importStopWords(stopWords_file);

		listFilesForFolder(file_folder);

		System.out.println("1. The number of word tokens in the database (after all text processing steps).");
		System.out.println("Word Token = " + wordTokens);

		System.out.println("2. The number of unique words in the database;");
		System.out.println("Unique Words= " + database_words.size());

		System.out.println("3. The number of words that occur only once in the database;");
		System.out.println(wordsOnce(database_words));

		System.out.println("4. The average number of word tokens per document.");
		System.out.println(wordTokens / fileNum);

		System.out.println("30 most frequent words in the database");
		TFIDF(entriesSortedByValues(database_words, 30));
	}

}
