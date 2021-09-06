import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadSafeInvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	public static final Logger log = LogManager.getLogger(Driver.class);
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locationsIndex;
	private ReadWriteLock lock;

	/**
	 * Initializes the index.
	 */
	public ThreadSafeInvertedIndex() {
		this.index = new TreeMap<>();
		this.locationsIndex = new TreeMap<>();
		this.lock = new ReadWriteLock();
	}

	/**
	 * Calls add with the default position 1
	 */
	public boolean add(String word, String location) {
		lock.lockReadWrite();
		try {
			return add(word, 1, location);
		} finally {
			lock.unlockReadWrite();
		}
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
		lock.lockReadWrite();
		try {
			this.index.putIfAbsent(word, new TreeMap<>());
			this.index.get(word).putIfAbsent(location, new TreeSet<>());

			this.locationsIndex.putIfAbsent(location, 0);
			this.locationsIndex.put(location, this.locationsIndex.get(location) + 1);

			return this.index.get(word).get(location).add(position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * sends the index to TreeJSONWriter to get written
	 *
	 * @param path the path to write the file to
	 */
	public void locationsToJson(Path path) throws IOException {
		lock.lockReadOnly();
		try {
			TreeJSONWriter.asObject(this.locationsIndex, path);
		} finally {
			lock.unlockReadOnly();
		}
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
		lock.lockReadWrite();
		try {
			return addAll(words, 1, location);
		} finally {
			lock.unlockReadWrite();
		}
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
		lock.lockReadWrite();
		try {
			int counter = 0;
			boolean returning = false;
			for (String word : words) {
				boolean check = add(word, start + counter, location);
				if (check == true)
					returning = true;
				counter++;
			}

			return returning;
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * Returns the number of locations this word appears in
	 *
	 * @param word word to look for
	 * @return number of times the word was found
	 */
	public int locations(String word) {
		lock.lockReadOnly();
		try {
			if (this.index.containsKey(word)) {
				return this.index.get(word).size();
			}
			return 0;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the number of times the word appears at a specific location
	 * 
	 * @param word     word to look for
	 * @param location path to look for
	 * @return number of times the word was found
	 */
	public int positions(String word, String location) {
		lock.lockReadOnly();
		try {
			if (this.index.containsKey(word) && this.index.get(word).containsKey(location)) {
				return this.index.get(word).get(location).size();
			}
			return 0;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		lock.lockReadOnly();
		try {
			return this.index.size();
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return this.index.containsKey(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified word at a specific location.
	 *
	 * @param word     word to look for
	 * @param location location to look for
	 * @return true if the word and location are stored in the index
	 */
	public boolean contains(String word, String location) {
		lock.lockReadOnly();
		try {
			return contains(word) && this.index.get(word).containsKey(location);
		} finally {
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try {
			return contains(word, location) && this.index.get(word).get(location).contains(position);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Passes the index to TreeJSONWriter so that it can start writing the index
	 *
	 * @param path where to write the file at
	 */
	public void toJson(Path path) throws IOException {
		lock.lockReadOnly();
		try {
			TreeJSONWriter.asNestedObject(this.index, path);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return this.index.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * method for adding all the results to fullResults
	 * 
	 * @param results     the results of the search
	 * @param fullResults where we add in all the results
	 * @param query       the query associated with those results
	 */
	public TreeMap<String, ArrayList<SearchResult>> addResults(TreeMap<String, ArrayList<SearchResult>> fullResults,
			String Query, ArrayList<SearchResult> result) {
		lock.lockReadWrite();
		try {
			fullResults.put(Query, result);
			return fullResults;
		} finally {
			lock.unlockReadWrite();
		}

	}

	/**
	 * Class for running our minions with Exact Search
	 * 
	 * @param results     the results of the search
	 * @param fullResults where we add in all the results
	 * @param lookup      how we easily lookup the query
	 * @param queries     the words we are looking for
	 */
	public class ExactSearchMinion implements Runnable {
		private final ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		private final TreeMap<String, ArrayList<SearchResult>> fullResults;
		private final HashMap<String, SearchResult> lookup;
		private final TreeSet<String> queries;

		public ExactSearchMinion(TreeSet<String> queries, TreeMap<String, ArrayList<SearchResult>> fullResults) {
			this.lookup = new HashMap<String, SearchResult>();
			this.fullResults = fullResults;
			this.queries = queries;
		}

		@Override
		public void run() {
			// System.out.println("Thread Check in Partial SearchMinion");
			log.debug("Thread Check in Exact SearchMinion");
			if (queries.size() < 1) {
				return;
			}
			log.debug("Working on queries: " + queries.toString());
			for (var word : queries.descendingSet()) {
				if (index.containsKey(word)) { // see if the index contains the specific word we are looking for word
					searchHelper(word, lookup, results);
				}
			}
			// System.out.println("Done with the results");
			Collections.sort(results);
			addResults(fullResults, String.join(" ", queries), results);
			// System.out.println("Done with the results");
			// System.out.println("Done with everything in RUN");
		}

	}

	/**
	 * Class for running our minions with Partial Search
	 * 
	 * @param results     the results of the search
	 * @param fullResults where we add in all the results
	 * @param lookup      how we easily lookup the query
	 * @param queries     the words we are looking for
	 */
	public class PartialSearchMinion implements Runnable {
		private final HashMap<String, SearchResult> lookup;
		private final ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		private final TreeMap<String, ArrayList<SearchResult>> fullResults;
		private final TreeSet<String> queries;

		public PartialSearchMinion(TreeSet<String> queries, TreeMap<String, ArrayList<SearchResult>> fullResults) {
			lookup = new HashMap<String, SearchResult>();
			this.fullResults = fullResults;
			this.queries = queries;
		}

		@Override
		public void run() {
			//System.out.println("Thread Check in Partial SearchMinion");
			log.debug("Thread Check in Partial SearchMinion");
			if (queries.size() < 1) {
				return;
			}
			log.debug("Working on queries: " + queries.toString());
			for (var word : queries.descendingSet()) {
				var keySet = index.navigableKeySet();
				for (var keyword : keySet.tailSet(word)) {
					if (keyword.startsWith(word)) { // checks to see if the word inside the InvertedIndex starts with
													// the
													// query word
						searchHelper(keyword, lookup, results);
					} else {
						break;
					}
				}
			}
//			System.out.println("Done with the results");
			Collections.sort(results);
			addResults(fullResults, String.join(" ", queries), results);
		}
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
		lock.lockReadWrite();
		try {
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
		} finally {
			lock.unlockReadWrite();
		}
	}
}
