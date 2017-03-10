import java.util.Set;

public class BaseLine implements SimilarityInterface {

  private final Set<User> userSet;
  private final Set<Movie> movieSet;
  private final Set<Rating> ratingSet;

  public BaseLine(final Set<User> users, final Set<Movie> movies, final Set<Rating> ratings) {
    this.userSet = users;
    this.movieSet = movies;
    this.ratingSet = ratings;
  }

  @Override
  public double computeSimilarity(User a, User b) {
    // should never call this
    return .0;
  }

  @Override
  public double predictRating(User u, Movie m, int threshold) {
    double mean = 0;
    for (Rating r : this.ratingSet) {
      if (r.getMovie().getId() != m.getId() && r.getUser().getId() != u.getId()) {
        mean += r.getRating();
      }
    }
    return mean / (double) (this.ratingSet.size() - 1);
  }

  @Override
  public Set<User> getUserSet() {
    return this.userSet;
  }

  @Override
  public Set<Movie> getMovieSet() {
    return this.movieSet;
  }

  @Override
  public Set<Rating> getRatingSet() {
    return this.ratingSet;
  }
}
