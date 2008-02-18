package aiml.substitutions;

/**
 * This exception is thrown whenever a substitution with an identical pattern is
 * added to an instance of Substitutions.
 * 
 * @author Kim Sullivan
 * 
 */
public class DuplicateSubstitutionException extends Exception {

  public DuplicateSubstitutionException(String pattern, String replacement) {
    super(
        String.format(
            "Substitution map already contains the replacement \"$s\" for the pattern \"%s\"",
            replacement, pattern));
  }

}
