import java.util.Set;

public interface SimilarityInterface {
    public Double computeSimilarity(User a, User b);
    public Double predictRating(User u, Movie m, Double threshold);
    public Set<User> getUserSet();
}
