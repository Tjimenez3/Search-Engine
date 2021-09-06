import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;


public class TextFileBuilder {
	/**
	 * Traverses the directory with the given path and collects all the matching
	 * files calls stemFile() for each match we have, so it will call stemFile for
	 * each file we find that matches our specifications
	 *
	 * @see Files#walk()
	 *
	 */
	public static void traverseDirectory(Path path, InvertedIndex index) throws IOException {
		Files.walk(path, FileVisitOption.FOLLOW_LINKS).filter(p -> !Files.isDirectory(p)).filter(p -> {
			String name = p.toString().toLowerCase();
			boolean name1 = name.endsWith(".txt") || name.endsWith(".text");
			return name1;
		}).forEach(p -> {
			try {
				stemFile(p, index);
			} catch (IOException e) {
				System.out.println("Unable to build index from: " + p);
			}
		});

	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then writes that line to a new file.
	 *
	 * @param inputFile  the input file to parse
	 * @param outputFile the output file to write the cleaned and stemmed words
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static void stemFile(Path inputFile, InvertedIndex index) throws IOException {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {

			String line = reader.readLine();
			int position_counter = 1; // keeps track of the position of the word
			
			String name = inputFile.toString();
			
			while (line != null) {
				String[] lineList = TextParser.parse(line);

				for (int i = 0; i < lineList.length; i++) { // iterates through all the words in the line
					String word = stemmer.stem(lineList[i]).toString();

					index.add(word, position_counter, name); // adds the word to the index
					position_counter++;
					
				}
				line = reader.readLine(); // reads next line
			}
		}
	}
}
