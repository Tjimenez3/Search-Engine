import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Driver {
	public static final Logger log = LogManager.getLogger(Driver.class);

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		InvertedIndex index = new InvertedIndex();
		ThreadSafeInvertedIndex threadIndex = new ThreadSafeInvertedIndex();
		QueryParser parser = new QueryParser(index);
		MultiThreadedQueryParser threadParser = new MultiThreadedQueryParser(threadIndex);
		final int threads;

		if (args.length == 0) {
			System.out.println("You did not enter any arguments");
		}

		ArgumentMap map = new ArgumentMap();
		map.parse(args);
		if (map.hasFlag("-threads")) {
			threads = map.getThread("-threads", 5);
			MultithreadedTextFileBuilder builder = new MultithreadedTextFileBuilder();

			if (map.hasFlag("-path")) {
				Path path = map.getPath("-path");

				if (path != null) {
					log.debug("Starting to call multithreadedTraverse");
					threadIndex = builder.multithreadedTraverse(path, threadIndex, threads);
					log.debug("We are out of MultithreadedTraverse");
					log.debug("Size of the index after Traversing Directories : " + threadIndex.words());
				} else {
					System.out.println("There was no path entered");
				}
			}
			if (map.hasFlag("-index")) {
				Path path = map.getPath("-index", Paths.get("index.json"));
				try {
					log.debug("Starting to call index.toJSON");
					threadIndex.toJson(path);
					log.debug("We are out of index.toJSON");
				} catch (IOException e) {
					System.out.println("Could not build index");
				}
			}

			if (map.hasFlag("-search")) {
				Path path = map.getPath("-search", Paths.get("results.json"));
				try {
					log.debug("Starting to call multithreadedParse");
					threadParser.parse(path, map.hasFlag("-exact"), threads);
					log.debug("We are out of MultithreadedParse");
				} catch (IOException e) {
					System.out.println("Could not search the queries");
				}

			}

			if (map.hasFlag("-results")) {
				Path filepath = map.getPath("-results", Paths.get("results.json"));
				try {
					log.debug("Starting to call Parser.JSON");
					threadParser.toJson(filepath);
					log.debug("We are out of Parser.JSON");
				} catch (IOException e) {
					System.out.println("Could not write the results");
				}
			}

			if (map.hasFlag("-locations")) {
				Path locationsPath = map.getPath("-locations", Paths.get("locations.json"));
				try {
					index.locationsToJson(locationsPath);
				} catch (IOException e) {
					System.out.println("Could not build the locations index");
				}
			}

		} else {
			if (map.hasFlag("-path")) {
				Path path = map.getPath("-path");
				if (path != null) {
					try {
						TextFileBuilder.traverseDirectory(path, index);
					} catch (IOException e) {
						System.out.println("Could not build from path: " + path);
					}
				} else {
					System.out.println("There was no path entered");
				}
			}

			if (map.hasFlag("-index")) {
				Path path = map.getPath("-index", Paths.get("index.json"));
				try {
					index.toJson(path);
				} catch (IOException e) {
					System.out.println("Could not build index");
				}
			}

			if (map.hasFlag("-search")) {
				Path path = map.getPath("-search", Paths.get("results.json"));
				try {
					parser.parse(path, map.hasFlag("-exact"));
				} catch (IOException e) {
					System.out.println("Could not search the queries");
				}

			}

			if (map.hasFlag("-results")) {
				Path filepath = map.getPath("-results", Paths.get("results.json"));
				try {
					parser.toJson(filepath);
				} catch (IOException e) {
					System.out.println("Could not write the results");
				}
			}

			if (map.hasFlag("-locations")) {
				Path locationsPath = map.getPath("-locations", Paths.get("locations.json"));
				try {
					index.locationsToJson(locationsPath);
				} catch (IOException e) {
					System.out.println("Could not build the locations index");
				}
			}
		}
	}
}
