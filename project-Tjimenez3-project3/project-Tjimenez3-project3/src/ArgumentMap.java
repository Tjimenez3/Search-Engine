import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ArgumentMap {

	private final Map<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() {
		this.map = new HashMap<String, String>(); // Initializes a HashMap of <String, String>
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten.
	 *
	 * @param args
	 * @throws IOException
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the arguments to see if we have a Flag and if the flag is a path or an
	 * index then calls traverseDirectory. If we don't have a path then it will just
	 * return
	 *
	 * @param args the command line arguments to parse
	 * @throws IOException
	 */
	public void parse(String[] args) {
		if (args.length == 0) { // checks if there is a string
			return;
		}

		for (int i = 0; i < args.length; i++) { // iterates through the array

			if (isFlag(args[i]) == true) {// checks to see if we have a flag

				if (i + 1 < args.length && isValue(args[i + 1])) { // if we have a value we put it in the map
					map.put(args[i], args[i + 1]);
				} else {
					this.map.put(args[i], null); // else we put null
				}
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-whitespace character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#trim()
	 * @see String#isEmpty()
	 * @see String#length()
	 */
	public static boolean isFlag(String arg) {

		if (arg == null) { // returns if null
			return false;
		}

		// checks to see if the string starts with "-" but does not have a whitespace
		// after the dash

		return arg.startsWith("-") && arg.trim().length() > 1;

	}

	/**
	 * Determines whether the argument is a value. Values do not start with a dash
	 * "-" character, and must consist of at least one non-whitespace character.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#trim()
	 * @see String#isEmpty()
	 * @see String#length()
	 */
	public static boolean isValue(String arg) {

		if (arg == null) {
			return false;
		}

		return (!(arg.startsWith("-") && (arg.trim().length() > 0)));
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {

		return this.map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag to search for
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return this.map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to search for
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return (this.map.get(flag) != null);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or null if there is no mapping for the flag.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping for the flag
	 */
	public String getString(String flag) {
		return this.map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or the default value if there is no mapping for the flag.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping for
	 *                     the flag
	 * @return the value to which the specified flag is mapped, or the default value
	 *         if there is no mapping for the flag
	 */
	public String getString(String flag, String defaultValue) {

		return this.map.getOrDefault(flag, defaultValue);

	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping for any reason (including
	 * being unable to convert the value to a {@link Path} or no value existing for
	 * this flag).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping for any reason
	 *
	 * @see Paths#get(String, String...)
	 */
	public Path getPath(String flag) {

		if (getString(flag) == null || !hasFlag(flag)) {
			return null;
		} else {
			return Paths.get(this.map.get(flag));
		}

	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * the default value if unable to retrieve this mapping for any reason
	 * (including being unable to convert the value to a {@link Path} or no value
	 * existing for this flag).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping for
	 *                     the flag
	 * @return the value to which the specified flag is mapped as a {@link Path}, or
	 *         the default value if there is no mapping for the flag
	 */
	public Path getPath(String flag, Path defaultValue) {

		if (getString(flag) == null || !hasFlag(flag)) {
			return defaultValue;
		} else {
			return Paths.get(this.map.get(flag));
		}

	}
	public int getThread(String flag, int defaultValue) {
		if (getString(flag) == null || !hasFlag(flag)) {
			return defaultValue;
		} else {
			return Integer.parseInt(this.map.get(flag));
		}
	}
	/**
	 * Returns a toString value
	 * 
	 * @return a string of the argumentmap
	 */
	@Override
	public String toString() {
		return this.map.toString();
	}
}
