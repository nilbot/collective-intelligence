import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {
    private Integer id;
    private Map<Movie, Rating> myRatings;
    private Map<Movie, Rating> holdbackSet;

    public User(final Integer id) {
        this.id = id;
        // building infrastructure
        this.myRatings = new HashMap<>();
        this.holdbackSet = new HashMap<>();
    }

    // API(draft) method for retrieving a set of common movies given an (foreign) User
    public Set<Movie> getCommonMovies(User otherUser) {
        Set<Movie> res = new HashSet<>();
        for (Rating r: getRatings()) {
            if (otherUser.hasRated(r.getMovie())) {
                res.add(r.getMovie());
            }
        }
        return res;
    }

    public boolean hasRated(Movie movie) {
        return myRatings.containsKey(movie);
    }

    public Set<Rating> getRatings() {
        return new HashSet<>(myRatings.values());
    }

    public void addRating(Rating r) {
        this.myRatings.put(r.getMovie(),r);
    }

    public Integer getId() {
        return this.id;
    }

    public Double getHoldbackSetRating(Movie m) {
        return this.holdbackSet.get(m).getRating();
    }

    public void holdingBack(final double percentage) {
        this.holdbackSet.clear(); // should the test set is not empty, erase and redo
        int holdbackSetSize = (int)(myRatings.size() * percentage);
        int count = 0;
        for (Rating r : getRatings()) {
            if (count == holdbackSetSize) {
                break;
            }
            this.holdbackSet.put(r.getMovie(),r);
            count++;
        }
    }

    public Set<Movie> getHoldbackSetMovies() {
        return this.holdbackSet.keySet();
    }
}
