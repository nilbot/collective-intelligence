import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

class DataReader {

  private final Map<Integer, User> userMap;
  private final Map<Integer, Movie> movieMap;
  private final Set<Rating> ratingSet;

  public DataReader(final String filename) {
    userMap = new HashMap<>();
    movieMap = new HashMap<>();
    ratingSet = new HashSet<>();

    try {
      loadDataset(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Set<Rating> getRatingSet() {
    return ratingSet;
  }

  public Map<Integer, Movie> getMovieMap() {

    return movieMap;
  }

  public Map<Integer, User> getUserMap() {

    return userMap;
  }

  private void loadDataset(final String filename) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
    String line;
    while ((line = in.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(line, ",");
      Integer userId = Integer.valueOf(st.nextToken());
      Integer movieId = Integer.valueOf(st.nextToken());
      Integer rating = Integer.valueOf(st.nextToken());
      Integer timeEpoch = Integer.valueOf(st.nextToken());
      User u;
      Movie m;
      Rating r;
      if (userMap.containsKey(userId)) {
        u = userMap.get(userId);
      } else {
        u = new User(userId);
      }
      if (movieMap.containsKey(movieId)) {
        m = movieMap.get(movieId);
      } else {
        m = new Movie(movieId);
      }
      r = new Rating(u, rating, m, timeEpoch);
      u.addRating(r);
      ratingSet.add(r);
      userMap.put(userId, u);
      movieMap.put(movieId, m);
    }
    in.close();
  }

}
