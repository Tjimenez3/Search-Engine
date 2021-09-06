import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class MultiThreadedQueryParser {

	private final TreeMap<String, ArrayList<SearchResult>> results;
	private final ArrayList<TreeSet<String>> allQueries = new ArrayList<>();
	private final ThreadSafeInvertedIndex threadIndex;
	public static final Logger log = LogManager.getLogger(Driver.class);
	private WorkQueue minions;

	/**
	 * Constructor for QueryParser, initializes the index as the InvertedIndex
	 *
	 * @param index the InvertedIndex we will use
	 */
	MultiThreadedQueryParser(ThreadSafeInvertedIndex index) {
		this.threadIndex = index;
		results = new TreeMap<String, ArrayList<SearchResult>>();

	}

	/**
	 * Reads a file line by line, and searches for the words given in the file.
	 * calls partialSearchMinion or exactsearchMinion based on the boolean condition
	 * exact this function will start up threads
	 *
	 * @param inputFile the input file to parse
	 * @param exact     boolean condition used to call partialSearch/exactSearch
	 * @throws IOException if unable to read or write to file
	 *
	 */
	public void parse(Path inputFile, boolean exact, int threads) throws IOException {

		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
			String line = reader.readLine();

			while (line != null) {
				TreeSet<String> queries = new TreeSet<String>();
				String[] lineList = TextParser.parse(line);

				for (var lines: lineList) {
					String stemmedQuery = stemmer.stem(lines.toString()).toString();
					if (!queries.contains(stemmedQuery)) {
						queries.add(stemmedQuery);
					}
				}
				if (!allQueries.contains(queries)) {
					allQueries.add(queries);
				}

				line = reader.readLine();
			}
			if (exact) {
				minions = new WorkQueue(threads);
				log.debug("There are " + allQueries.size() + " queries");
				// System.out.println("There are " + allQueries.size() + " queries");
				for (var queries : allQueries) {
					log.debug("Sending in: " + queries.toString());
					// System.out.println("Sending in: " + queries.toString());
					minions.execute(threadIndex.new ExactSearchMinion(queries, results));
				}
				log.debug("We are starting to shut down");
				// System.out.println("We are Shutting down");
				minions.finish();
				// System.out.println("Shut Down Completed");
				log.debug("We are Shutting down");
			} else {
				minions = new WorkQueue(threads);
				log.debug("There are " + allQueries.size() + " queries");
				for (var queries : allQueries) {
					log.debug("Sending in: " + queries.toString());
					// System.out.println("Sending in: " + queries.toString());
					minions.execute(threadIndex.new PartialSearchMinion(queries, results));
				}
				log.debug("We are starting to shut down");
				// System.out.println("We are Shutting down");
				minions.finish();
				// System.out.println("Shut Down Completed");
				log.debug("We have Shutting down");
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