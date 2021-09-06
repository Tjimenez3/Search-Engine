public class SearchResult implements Comparable<SearchResult> {

	private final String location;
	private int count;
	private double score;
	private final int fileWordCount;

	/**
	 * initializes searchResults to null;
	 */
	public SearchResult(String location, int fileWordCount) {
		this.location = location;
		this.fileWordCount = fileWordCount;
	}

	/**
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @return score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * updates the count, and score in the SearchResult
	 * 
	 * @param count         the amount of times a word showed up at that location we
	 *                      will add it to the current count;
	 * @param fileWordCount the total number of words in that file, used to
	 *                      calculate the score
	 */
	public void updateResult(int count) {
		this.count = this.count + count;
		this.score = (double) this.count / fileWordCount;
	}

	/**
	 * compares this SearchResult to another SearchResult first by score, then by
	 * count, and finally by location
	 * 
	 * @param other the searchResult to compare to
	 * @return negative value if other is less than this
	 */
	@Override
	public int compareTo(SearchResult other) {
		int retval = Double.compare(this.score, other.score);
		if (retval == 0) {
			retval = Integer.compare(this.count, other.count);
			if (retval == 0) {
				return this.location.compareTo(other.location);
			}
		}
		return -retval;
	}
}
