import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryParser {

	private final TreeMap<String, ArrayList<SearchResult>> results;
	private final InvertedIndex index;

	/**
	 * Constructor for QueryParser, initializes the index as the InvertedIndex
	 *
	 * @param index the InvertedIndex we will use
	 */
	QueryParser(InvertedIndex index) {
		this.index = index;
		results = new TreeMap<String, ArrayList<SearchResult>>();

	}

	/**
	 * Reads a file line by line, and searches for the words given in the file.
	 * calls partialSearch or exactsearch based on the boolean condition exact
	 *
	 * @param inputFile the input file to parse
	 * @param exact     boolean condition used to call partialSearch/exactSearch
	 * @throws IOException if unable to read or write to file
	 *
	 */
	public void parse(Path inputFile, boolean exact) throws IOException {

		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
			String line = reader.readLine();

			while (line != null) {
				TreeSet<String> queries = new TreeSet<String>();
				String[] lineList = TextParser.parse(line);

				for (var lines : lineList) {
					String stemmedQuery = stemmer.stem(lines.toString()).toString();
					if (!queries.contains(stemmedQuery)) {
						queries.add(stemmedQuery);
					}
				}

				String query = String.join(" ", queries);

				if (!results.containsKey(query)) {
					if (exact) {
						var searchResults = index.exactSearch(queries);
						if (query.length() >= 1) {
							results.put(query, searchResults);
						}
					} else {
						var searchResults = index.partialSearch(queries);
						if (query.length() >= 1) {
							results.put(query, searchResults);
						}
					}
				}
				line = reader.readLine();
			}
		}

	}

	/**
	 * Calls TreeJSONWriter and sends in the results
	 *
	 * @param outputFile the file to write to
	 */
	public void toJson(Path outputFile) throws IOException {
		TreeJSONWriter.asSearchResult(results, outputFile);
	}
}