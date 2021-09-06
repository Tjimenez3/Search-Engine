import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class TextFileStemmer {

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English {@link SnowballStemmer.ALGORITHM} for stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemWord(String, Stemmer)
	 */
	public static String stemLine(String line) {
		// This is provided for you.
		return stemWord(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param word    the line of words to stem
	 * @param stemmer the stemmer to use
	 * @return list of stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 */
	public static String stemWord(String word, Stemmer stemmer) {
		return stemmer.stem(word).toString();
	}
}
