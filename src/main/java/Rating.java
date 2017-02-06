import com.sun.istack.internal.Nullable;

/**
 * Created by nilbot on 2/1/17.
 */
public class Rating {
    private User user;
    private Double rating;
    private Movie movie;

    public Rating(final User user, final double rating, final Movie movie) {
        this.user = user;
        this.rating = rating;
        this.movie = movie;
    }

    /**
     * Constructor overload for accommodate rating object without mapped user
     * @param rating
     * @param movie
     */
    public Rating(final double rating, final Movie movie) {
        this.rating = rating;
        this.movie = movie;
        this.user = null;
    }

    @Nullable
    public User getUser() {
        return this.user;
    }


    public Movie getMovie() {
        return this.movie;
    }

    public void setUser(User newUser) {
        this.user = newUser;
    }

    public Double getRating() {
        return this.rating;
    }

    @Override
    public String toString() { // serializable
        try {
            return this.user.getId() + "\t" + this.movie.getId() + "\t" + this.rating + "\t";
        } catch (NullPointerException ne) {
            System.err.println("rating object has no user mapping.");
            return -1 + "\t" + this.movie.getId() + "\t" + this.rating + "\t";
        }
    }
}
