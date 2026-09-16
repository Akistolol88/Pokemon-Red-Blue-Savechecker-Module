package se.lnu.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the closely coupled application logic in {@link App}.
 */
@DisplayName("App")
class AppTest {

  @Nested
  @DisplayName("parseArgs()")
  class ParseArgsTest {

    @Test
    @DisplayName("should return the first positional argument")
    void returnsFirstArgument() {
      assertEquals("Ada Lovelace", App.parseArgs(new String[] {"Ada Lovelace"}));
    }

    @Test
    @DisplayName("should return null when no arguments are given")
    void returnsNullWhenNoArguments() {
      assertNull(App.parseArgs(new String[0]));
    }
  }

  @Nested
  @DisplayName("generateGreeting()")
  class GenerateGreetingTest {

    @Test
    @DisplayName("should return a personalized greeting when a valid name is provided")
    void returnsPersonalizedGreeting() {
      assertEquals("Hello, Ada Lovelace!", App.generateGreeting("Ada Lovelace"));
    }

    @Test
    @DisplayName("should return a guest greeting when the input is null")
    void returnsGuestGreetingForNull() {
      assertEquals("Hello, Guest!", App.generateGreeting(null));
    }

    @Test
    @DisplayName("should return a guest greeting when the input is an empty or blank string")
    void returnsGuestGreetingForBlank() {
      assertEquals("Hello, Guest!", App.generateGreeting(""));
      assertEquals("Hello, Guest!", App.generateGreeting("   "));
    }
  }
}
