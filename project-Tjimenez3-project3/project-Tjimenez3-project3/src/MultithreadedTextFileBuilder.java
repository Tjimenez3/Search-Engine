import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class MultithreadedTextFileBuilder {
	public static final Logger log = LogManager.getLogger(Driver.class);
	private WorkQueue minions;

	public ThreadSafeInvertedIndex multithreadedTraverse(Path path, ThreadSafeInvertedIndex index, int threads) {
		minions = new WorkQueue(threads);
		minions.execute(new DirectoryMinion(path, index));
		minions.finish();
		return index;
	}

	/**
	 * Class for calling our stemFileMinion
	 * 
	 * @param directory the path we are traversing
	 * @param index     the index we are adding to
	 */
	private class DirectoryMinion implements Runnable {
		private final Path directory;
		private final ThreadSafeInvertedIndex index;

		public DirectoryMinion(Path directory, ThreadSafeInvertedIndex index) {
			this.directory = directory;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				Files.walk(directory, FileVisitOption.FOLLOW_LINKS).filter(p -> !Files.isDirectory(p)).filter(p -> {
					String name = p.toString().toLowerCase();
					boolean name1 = name.endsWith(".txt") || name.endsWith(".text");
					return name1;
				}).forEach(p -> {
					minions.execute(new stemFileMinion(p, index));
				});
			} catch (IOException e) {
				log.debug("our traversing of Directories failed:" + e);
			}
		}
	}

	/**
	 * Class for running our stemFile code
	 * 
	 * @param inputFile the file we are looking at
	 * @param index     the index we are adding to
	 */
	private class stemFileMinion implements Runnable {
		private final Path inputFile;
		private final ThreadSafeInvertedIndex index;

		public stemFileMinion(Path inputFile, ThreadSafeInvertedIndex index) {
			// log.debug("Thread Check!");
			this.inputFile = inputFile;
			this.index = index;
		}

		@Override
		public void run() {
			log.debug("Thread Check!");
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {

				String line = reader.readLine();
				int position_counter = 1; // keeps track of the position of the word

				String name = inputFile.toString();

				while (line != null) {
					String[] lineList = TextParser.parse(line);

					for (int i = 0; i < lineList.length; i++) { // iterates through all the words in the line
						String word = stemmer.stem(lineList[i]).toString();

						this.index.add(word, position_counter, name); // adds the word to the index
						position_counter++;

					}
					line = reader.readLine(); // reads next line
				}
			} catch (IOException e) {
				log.debug("Our Try with Resources block had an IOexception", e);
			}
		}
	}
}
