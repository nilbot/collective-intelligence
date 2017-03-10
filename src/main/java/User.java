import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {

  private static int instanceID = 0;
  private static Object lock = new Object();
  private Integer id; // user id as given by the dataset
  private Map<Movie, Rating> myRatings; // holds this user's ratings
  private Integer iid; // this id is for matrix index, internal only

  public User(final Integer id) {
    this.id = id;
    incrementIID();
    // building infrastructure
    this.myRatings = new HashMap<>();
  }

  protected void incrementIID() {
    synchronized (lock) {
      this.iid = instanceID++;
    }
  }

  // API(draft) method for retrieving a set of common movies given an (foreign) User
  public Set<Movie> getCommonMovies(User otherUser) {
    Set<Movie> res = new HashSet<>();
    for (Rating r : getRatings()) {
      if (otherUser.getRating(r.getMovie()) != Rating.NO_RATING) {
        res.add(r.getMovie());
      }
    }
    return res;
  }

  public Set<Rating> getRatings() {
    return new HashSet<>(myRatings.values());
  }

  public double getRating(final Movie m) {
    Rating r = this.myRatings.get(m);
    if (r != null) {
      return r.getRating();
    }
    return Rating.NO_RATING;
  }

  public void addRating(Rating r) {
    this.myRatings.put(r.getMovie(), r);
  }

  public int getId() {
    return this.id;
  }

  public int Id() {
    return this.iid;
  }

  public double getMeanRating() {
    Double res = .0;
    for (Rating r : this.myRatings.values()) {
      res += r.getRating();
    }
    return res / this.myRatings.size();
  }
}
