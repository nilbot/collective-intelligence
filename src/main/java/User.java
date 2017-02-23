import java.util.*;

public class User {
    private Integer id; // user id as given by the dataset
    private Map<Movie, Rating> myRatings; // holds this user's ratings
    private Map<Movie, Rating> holdbackSet; // for prediction error metrics

    private static int instanceID = 0;
    private static Object lock = new Object();

    private Integer iid; // this id is for matrix index, internal only

    protected void incrementIID() {
        synchronized (lock) {
            this.iid = instanceID++;
        }
    }

    public User(final Integer id) {
        this.id = id;
        incrementIID();
        // building infrastructure
        this.myRatings = new HashMap<>();
        this.holdbackSet = new HashMap<>();
    }

    // API(draft) method for retrieving a set of common movies given an (foreign) User
    public Set<Movie> getCommonMovies(User otherUser) {
        Set<Movie> res = new HashSet<>();
        for (Rating r: getRatings()) {
            if (otherUser.getRating(r.getMovie()) != Rating.NO_RATING) {
                res.add(r.getMovie());
            }
        }
        return res;
    }

    public Set<Rating> getRatings() {
        return new HashSet<>(myRatings.values());
    }

    public Double getRating(final Movie m) {
        Rating r = this.myRatings.get(m);
        if (r != null) {
            return r.getRating();
        }
        return Rating.NO_RATING;
    }

    public void addRating(Rating r) {
        this.myRatings.put(r.getMovie(),r);
    }

    public Integer getId() {
        return this.id;
    }

    public Integer Id() { return this.iid; }

    public Double getHoldbackSetRating(Movie m) {
        return this.holdbackSet.get(m).getRating();
    }

    public void holdingBack(final double percentage) {
        this.holdbackSet.clear(); // should the test set is not empty, erase and redo
        int holdbackSetSize = (int)(myRatings.size() * percentage);
        int count = 0;

        // safe remove item
        for (Iterator<Rating> i = getRatings().iterator(); i.hasNext() && count < holdbackSetSize; count++) {
            Rating r = i.next();
            this.holdbackSet.put(r.getMovie(),r);
            i.remove();
        }
    }

    public Set<Movie> getHoldbackSetMovies() {
        return this.holdbackSet.keySet();
    }

    public Double getMeanRating() {
        Double res = .0;
        for (Rating r : this.myRatings.values()) {
            res += r.getRating();
        }
        return res / this.myRatings.size();
    }
}
