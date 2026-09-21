package se.lnu.cli;

/**
 * Main application class and execution entry point.
 */
public class App {

    /**
     * Extracts the name argument from the command line.
     *
     * @param args command-line arguments passed to {@link #main}
     * @return the first positional argument, or {@code null} if none was given
     */
    public static String parseArgs(String[] args) {
        return args.length > 0 ? args[0] : null;
    }

    /**
     * Generates a formatted greeting message.
     *
     * @param name the name of the person to greet; blank falls back to a guest greeting
     * @return the complete greeting message
     */
    public static String generateGreeting(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, Guest!";
        }
        return "Hello, " + name + "!";
    }

    /**
     * Execution entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("🚀 CLI Application is up and running!");
        System.out.println("Edit App.java and run './gradlew run' to see your changes.");

        String name = parseArgs(args);
        String greeting = generateGreeting(name != null ? name : "Brian Kernighan");

        System.out.println();
        System.out.println("Message of the day: " + greeting);
    }
}
