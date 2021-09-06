import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to the
	 * specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers using
	 * the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {

		if (elements.isEmpty()) { // if there are no elements we just write brackets
			writer.write('[');
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write("]");
			return;
		}

		writer.write('[');
		writer.write(System.lineSeparator());

		// iterates through the elements in the headSet not
		// including the last element
		for (Integer element : elements.headSet(elements.last())) {
			indent(level + 1, writer);
			writer.write(element.toString());
			writer.write(",");
			writer.write(System.lineSeparator());
		}

		// writes out the last element
		indent(level + 1, writer);
		writer.write(elements.last().toString());
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {

		if (elements.isEmpty()) { // creates empty brackets if there are no elements
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}

		writer.write('{');
		writer.write(System.lineSeparator());

		// gets the first element and writes the first element with all the
		// brackets and characters
		var firstElement = elements.firstEntry();
		indent(level + 1, writer);
		writer.write('"' + firstElement.getKey() + '"');
		writer.write(": " + firstElement.getValue().toString());

		// iterates through the rest of the elements
		for (var element : elements.tailMap(elements.firstKey(), false).entrySet()) {

			String key = element.getKey();
			Integer value = element.getValue();

			writer.write(",");
			writer.write(System.lineSeparator());

			indent(level + 1, writer);
			writer.write('"' + key + '"'); // prints out value and key
			writer.write(": " + value.toString());
		}

		writer.write(System.lineSeparator());
		writer.write("}");

	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asDoubleNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asDoubleNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Creates a bufferedwriter to write to the given path calls asNestedObject to
	 * write everything.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {

		if (elements.isEmpty()) { // returns if there is nothing in the treemap
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}

		writer.write(System.lineSeparator());

		var firstElement = elements.firstEntry();

		indent(level + 1, writer);
		writer.write('"' + firstElement.getKey() + '"' + ": ");

		asArray(firstElement.getValue(), writer, level + 1);

		for (var element : elements.tailMap(elements.firstKey(), false).entrySet()) {
			String key = element.getKey();

			writer.write(",");
			writer.write(System.lineSeparator());

			indent(level + 1, writer);
			writer.write('"' + key + '"' + ": ");

			asArray(element.getValue(), writer, level + 1);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {

		if (elements.isEmpty()) { // returns if there is nothing in the TreeMap
			writer.write('{');
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}

		writer.write('{');
		writer.write(System.lineSeparator());

		// gets first entry and writes it
		var firstElement = elements.firstEntry();
		indent(level + 1, writer);
		writer.write('"' + firstElement.getKey() + '"' + ": ");
		writer.write("{");

		asNestedObject(firstElement.getValue(), writer, level + 1);

		writer.write(System.lineSeparator());
		indent(level + 1, writer);
		writer.write("}");

		// iterates through the index
		for (var element : elements.tailMap(elements.firstKey(), false).entrySet()) {

			String key = element.getKey();

			writer.write(",");
			writer.write(System.lineSeparator());

			indent(level + 1, writer);
			writer.write('"' + key + '"' + ": ");
			writer.write("{");

			asNestedObject(element.getValue(), writer, level + 1);

			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write("}");
		}

		writer.write(System.lineSeparator());
		writer.write("}");

	}

	/**
	 * Creates a bufferedwriter to write to the given path calls asNestedObject to
	 * write everything.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asSearchResult(TreeMap<String, ArrayList<SearchResult>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asSearchResult(elements, writer, 0);
		}
	}

	/**
	 * Writes the searchResult of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asSearchResult(TreeMap<String, ArrayList<SearchResult>> elements, Writer writer, int level)
			throws IOException {
		
		if (elements.isEmpty()) { // returns if there is nothing in the TreeMap
			writer.write('[');
			writer.write(System.lineSeparator());
			writer.write("]");
			return;
		}
		writer.write('[');
		writer.write(System.lineSeparator());

		for (var queries : elements.headMap(elements.lastKey(), false).keySet()) {
			indent(level + 1, writer);
			writer.write('{');
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			writer.write('"');
			writer.write("queries");
			writer.write('"');
			writer.write(": ");
			writer.write('"');
			writer.write(queries);

			writer.write('"');
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			writer.write('"');
			writer.write("results");
			writer.write('"');
			writer.write(": [");

			ArrayList<SearchResult> searchResult = new ArrayList<>();
			for (var string2 : elements.get(queries)) {
				searchResult.add(string2);
			}

			Collections.sort(searchResult);
			writeSearchResult(searchResult, writer, level);

			writer.write(System.lineSeparator());
			indent(level + 2, writer);
			writer.write("]");
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			writer.write("},");
			writer.write(System.lineSeparator());
		}

		var queries = elements.lastKey();
		indent(level + 1, writer);
		writer.write('{');
		writer.write(System.lineSeparator());
		indent(level + 2, writer);
		
		writer.write('"');
		writer.write("queries");
		writer.write('"');
		writer.write(": ");
		writer.write('"');
		writer.write(queries);
		writer.write('"');
		writer.write(",");
		writer.write(System.lineSeparator());
		indent(level + 2, writer);
		
		writer.write('"');
		writer.write("results");
		writer.write('"');
		writer.write(": [");

		ArrayList<SearchResult> sResults = new ArrayList<>();
		for (var string2 : elements.get(queries)) {
			sResults.add(string2);
		}

		Collections.sort(sResults);
		writeSearchResult(sResults, writer, level);

		writer.write(System.lineSeparator());
		indent(level + 2, writer);
		writer.write("]");
		writer.write(System.lineSeparator());
		indent(level + 1, writer);
		writer.write("}");
		writer.write(System.lineSeparator());
		writer.write("]");
	}

	/**
	 * Writes the searchResult of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 */
	public static void writeSearchResult(ArrayList<SearchResult> results, Writer writer, int level)
			throws IOException {
		for (var searchResults : results) {
			writer.write(System.lineSeparator());
			indent(level + 3, writer);
			writer.write('{');
			writer.write(System.lineSeparator());
			indent(level + 4, writer);
			writer.write('"');
			writer.write("where");
			writer.write('"');
			writer.write(": ");
			writer.write('"');
			writer.write(searchResults.getLocation());
			writer.write('"');
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(level + 4, writer);
			
			writer.write('"');
			writer.write("count");
			writer.write('"');
			writer.write(": ");
			writer.write(searchResults.getCount().toString());
			writer.write(",");
			writer.write(System.lineSeparator());
			indent(level + 4, writer);
			
			writer.write('"');
			writer.write("score");
			writer.write('"');
			writer.write(": ");
			DecimalFormat FORMATTER = new DecimalFormat("0.000000");
			writer.write(FORMATTER.format(searchResults.getScore()));
			writer.write(System.lineSeparator());
			indent(level + 3, writer);
			writer.write("}");
			
			if (!searchResults.equals(results.get(results.size() - 1))) {
				writer.write(',');
			} 
		}
	}
}