import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locationsIndex;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.locationsIndex = new TreeMap<>();
	}

	/**
	 * Calls add with the default position 1
	 */
	public boolean add(String word, String location) {
		return add(word, 1, location);
	}

	/**
	 * Adds the word and the position it was found and the path it was found in to
	 * the index.
	 *
	 * @param word     word to clean and add to index
	 * @param position position word was found
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, int position, String location) {
		this.index.putIfAbsent(word, new TreeMap<>());
		this.index.get(word).putIfAbsent(location, new TreeSet<>());

		this.locationsIndex.putIfAbsent(location, 0);
		this.locationsIndex.put(location, this.locationsIndex.get(location) + 1);

		return this.index.get(word).get(location).add(position);
	}

	/**
	 * sends the index to TreeJSONWriter to get written
	 *
	 * @param path the path to write the file to
	 */
	public void locationsToJson(Path path) throws IOException {
		TreeJSONWriter.asObject(this.locationsIndex, path);
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * position 1.
	 *
	 * @param words array of words to add
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 *
	 * @see #addAll(String[], int)
	 */
	public boolean addAll(String[] words, String location) {
		return addAll(words, 1, location);
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * the provided starting position
	 *
	 * @param words array of words to add
	 * @param start starting position
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 */
	public boolean addAll(String[] words, int start, String location) {

		int counter = 0;
		boolean returning = false;
		for (String word : words) {
			boolean check = add(word, start + counter, location);
			if (check == true)
				returning = true;
			counter++;
		}

		return returning;
	}

	/**
	 * Returns the number of locations this word appears in
	 *
	 * @param word word to look for
	 * @return number of times the word was found
	 */
	public int locations(String word) {
		if (this.index.containsKey(word)) {
			return this.index.get(word).size();
		}
		return 0;
	}

	/**
	 * Returns the number of times the word appears at a specific location
	 * 
	 * @param word     word to look for
	 * @param location path to look for
	 * @return number of times the word was found
	 */
	public int positions(String word, String location) {
		if (this.index.containsKey(word) && this.index.get(word).containsKey(location)) {
			return this.index.get(word).get(location).size();
		}
		return 0;
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		return this.index.size();
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		return this.index.containsKey(word);
	}

	/**
	 * Tests whether the index contains the specified word at a specific location.
	 *
	 * @param word     word to look for
	 * @param location location to look for
	 * @return true if the word and location are stored in the index
	 */
	public boolean contains(String word, String location) {
		return contains(word) && this.index.get(word).containsKey(location);
	}

	/**
	 * Tests whether the index contains the specified word at the specified
	 * position.
	 *
	 * @param word     word to look for
	 * @param position position to look for word
	 * @return true if the word is stored in the index at the specified position
	 */
	public boolean contains(String word, int position, String location) {
		return contains(word, location) && this.index.get(word).get(location).contains(position);
	}

	/**
	 * Passes the index to TreeJSONWriter so that it can start writing the index
	 *
	 * @param path where to write the file at
	 */
	public void toJson(Path path) throws IOException {
		TreeJSONWriter.asNestedObject(this.index, path);
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

	/**
	 * Does an exact search on the Index checks to see if the word is in the
	 * InvertedIndex
	 *
	 * @param queries word to look for
	 * @param result  where we store results
	 */
	public ArrayList<SearchResult> exactSearch(TreeSet<String> queries) {

		HashMap<String, SearchResult> lookup = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> results = new ArrayList<>();

		if (queries.size() < 1) {
			return null;
		}

		for (var key : queries) { // look at all the words in the query

			if (this.index.containsKey(key)) { // see if the index contains the specific word we are looking for word
				searchHelper(key, lookup, results);

			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Does an partial search on the Index checks to see if any word in the index
	 * starts with query
	 *
	 * @param queries word to look for
	 * @param result  where we store results
	 */
	public ArrayList<SearchResult> partialSearch(TreeSet<String> queries) {
		HashMap<String, SearchResult> lookup = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> results = new ArrayList<>();

		if (queries.size() < 1) {
			return null;
		}

		for (var word : queries.descendingSet()) {

			var keySet = this.index.navigableKeySet();
			for (var keyword : keySet.tailSet(word)) {

				if (keyword.startsWith(word)) { // checks to see if the word inside the InvertedIndex starts with the
												// query word
					searchHelper(keyword, lookup, results);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * is useful for having the shared code of partialSearch and ExactSearch, does
	 * the actual search on the index
	 *
	 * @param keyword  the word we are searching for
	 * @param location the location we are searching at
	 * @param lookup   the Hashmap too lookup everything
	 */
	private void searchHelper(String keyword, HashMap<String, SearchResult> lookup, ArrayList<SearchResult> results) {

		for (var location : this.index.get(keyword).keySet()) {
			int count = this.index.get(keyword).get(location).size();
			int locationCount = locationsIndex.get(location);

			if (lookup.containsKey(location)) { // checks to see if we already have that location
				lookup.get(location).updateResult(count);
			} else { // adds to lookup if we don't have the location
				SearchResult result = new SearchResult(location, locationCount);
				result.updateResult(count);
				lookup.put(location, result);
				results.add(result);

			}
		}

	}
}
