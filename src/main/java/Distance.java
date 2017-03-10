import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Distance implements SimilarityInterface {

  private static Object lock = new Object();
  private final Set<User> userSet;
  private final Set<Rating> ratingSet;
  private final Set<Movie> movieSet;
  private final Double[][] matrix;

  public Distance(final Set<User> users, final Set<Movie> movies, final Set<Rating> ratings) {
    this.userSet = users;
    this.movieSet = movies;
    this.ratingSet = ratings;

    this.matrix = new Double[this.userSet.size()][this.userSet.size()];

    // parallelism
    computeAllDistances();
  }

  private void setSim(User a, User b, Double value) {
    synchronized (lock) {
      this.matrix[a.Id()][b.Id()] = value;
      this.matrix[b.Id()][a.Id()] = value;
    }
  }

  private void computeAllDistances() {
    Integer totes = getUserSet().size();
    int coreCount = Runtime.getRuntime().availableProcessors();
    Set<Set<User>> partition = new HashSet<>();
    List<User> totalWorkload = new ArrayList<>(getUserSet());
    int count = 0;
    Set<User> part = new HashSet<>();
    for (User a : totalWorkload) {
      part.add(a);
      if (++count == totes / coreCount) {
        count = 0;
        partition.add(part);
        part = new HashSet<>();
      }
    }
    partition.add(part);
    ExecutorService exe = Executors.newFixedThreadPool(coreCount);
    List<Future<Integer>> res = new ArrayList<>();

    for (Set<User> portion : partition) {
      Future<Integer> future = exe.submit(new TinyWorkload(portion, getUserSet()));
      res.add(future);
    }
    int test = 0;
    for (Future<Integer> r : res) {
      int finished = 0;
      try {
        finished = r.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
      test += finished;
    }

    if (test != totes * totes) {
      System.err.println("totes: " + totes + " test: " + test
          + "\nfinished parallel task count does not equal to total workload");
      System.exit(1); // TODO handle gracefully once tested
    }
  }

  @Override
  /* Mean square distance similarity Shardanand & Maes, 1995
   */
  public double computeSimilarity(User a, User b) {
    Set<Movie> commonMovies = a.getCommonMovies(b);
    int numberOfCommonRatings = commonMovies.size();
    if (numberOfCommonRatings > 0) {
      double sumOfDifferences = 0.0;
      for (Movie m : commonMovies) {
        Double diff = a.getRating(m) - b.getRating(m);
        sumOfDifferences += diff * diff;
      }
      Double msd = sumOfDifferences / (double) numberOfCommonRatings;
      return 1 - (msd / Math.pow((Rating.MAX_RATING - Rating.MIN_RATING), 2));
    } else {
      return .0;
    }
  }

  @Override
  public double predictRating(User u, Movie m, int threshold) {
    Set<User> neighbours = computeNeighbours(u, threshold);
    double top = 0;
    double bottom = 0;
    for (User n : neighbours) {
      if (n.getRating(m) != Rating.NO_RATING) {
        top += getSim(u, n) * u.getRating(m);
        bottom += getSim(n, u);
      }
    }
    if (bottom > 0) {
      double prediction = top / bottom;
      if (prediction < Rating.MIN_RATING) {
        prediction = Rating.MIN_RATING;
      }
      if (prediction > Rating.MAX_RATING) {
        prediction = Rating.MAX_RATING;
      }
      return prediction;
    } else {
      return -1;
    }
  }

  private double getSim(User u, User v) {
    return this.matrix[u.Id()][v.Id()];
  }

  private Set<User> computeNeighbours(User u, int threshold) {
    Set<User> res = new HashSet<>();
    TreeMap<Double, User> sorted = new TreeMap<>(Collections.reverseOrder());
    for (User v : getUserSet()) {
      if (u.getId() != v.getId()) {
        sorted.put(getSim(u, v), v);
      }
    }
    int count = 0;
    while (sorted.size() > 0) {
      User v = sorted.pollFirstEntry().getValue();
      res.add(v);
      if (++count == threshold) {
        break;
      }
    }
    return res;
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

  // TODO can refactor this out in super class in template method style, just cba tbh.
  private class TinyWorkload implements Callable<Integer> {

    private final Set<User> workload;
    private final Set<User> full;

    public TinyWorkload(final Set<User> workload, final Set<User> full) {
      this.workload = workload;
      this.full = full;
    }

    @Override
    public Integer call() throws Exception {
      int count = 0;
      for (User a : workload) {
        for (User b : this.full) {
          setSim(a, b, computeSimilarity(a, b));
          count++;
        }
      }
      return count;
    }
  }
}
