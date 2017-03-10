import java.util.Set;

public interface SimilarityInterface {

  public double computeSimilarity(User a, User b);

  public double predictRating(User u, Movie m, int threshold);

  public Set<User> getUserSet();

  public Set<Movie> getMovieSet();

  public Set<Rating> getRatingSet();
}
