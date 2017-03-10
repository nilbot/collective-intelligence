import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DataSetStatistics {


  public static List<StatsPerXXObj> PerUser(Set<User> us) {
    List<StatsPerXXObj> res = new ArrayList<>();

    for (User u : us) {
      double sum = 0;
      double[] arr = u.getRatings().parallelStream()
          .mapToDouble((r) -> r.getRating()).sorted().toArray();

      for (double d : arr) {
        sum += d;
      }

      double mean = sum / (double) arr.length;
      double min = arr[0];
      double max = arr[arr.length - 1];
      double median = arr[arr.length / 2];
      double sd = .0;
      for (double d : arr) {
        sd += (d - mean) * (d - mean);
      }
      sd /= (double) arr.length;
      sd = Math.sqrt(sd);
      StatsPerXXObj stat = new StatsPerXXObj(u.getId(), mean, median, sd, max, min);
      res.add(stat);
    }

    return res;
  }

  public static List<StatsPerXXObj> PerMovie(Set<Movie> ms, Set<Rating> rs) {
    List<StatsPerXXObj> res = new ArrayList<>();

    for (Movie m : ms) {
      double sum = 0;
      List<Rating> mrs = new ArrayList<>();

      for (Rating r : rs) {
        if (r.getMovie().getId() == m.getId()) {
          mrs.add(r);
        }
      }

      double[] arr = mrs.parallelStream()
          .mapToDouble((r) -> r.getRating()).sorted().toArray();

      for (double d : arr) {
        sum += d;
      }

      double mean = sum / (double) arr.length;
      double min = arr[0];
      double max = arr[arr.length - 1];
      double median = arr[arr.length / 2];
      double sd = .0;
      for (double d : arr) {
        sd += (d - mean) * (d - mean);
      }
      sd /= (double) arr.length;
      sd = Math.sqrt(sd);
      StatsPerXXObj stat = new StatsPerXXObj(m.getId(), mean, median, sd, max, min);
      res.add(stat);
    }

    return res;
  }

  public static double MeanRatingAll(Set<Rating> rs) {
    double mean = .0;
    for (Rating r : rs) {
      mean += r.getRating();
    }
    return mean / (double) rs.size();
  }

  public static double densityRatings(Set<User> users, Set<Movie> movies) {
    int count = 0;
    for (User u : users) {
      for (Movie m : movies) {
        if (u.getRating(m) != Rating.NO_RATING) {
          count++;
        }
      }
    }
    return count / (double) (users.size() * movies.size());
  }

  public static Map<Double, Integer> ratingBin(Set<Rating> rs) {
    Map<Double, Integer> res = new HashMap<>();
    for (Rating r : rs) {
      int rating = (int) r.getRating();
      switch (rating) {
        case 1:
          if (!res.containsKey(1d)) {
            res.put(1., 1);
          } else {
            res.put(1., res.get(1.) + 1);
          }
          break;
        case 2:
          if (!res.containsKey(2d)) {
            res.put(2., 1);
          } else {
            res.put(2., res.get(2.) + 1);
          }
          break;
        case 3:
          if (!res.containsKey(3d)) {
            res.put(3., 1);
          } else {
            res.put(3., res.get(3.) + 1);
          }
          break;
        case 4:
          if (!res.containsKey(4d)) {
            res.put(4., 1);
          } else {
            res.put(4., res.get(4.) + 1);
          }
          break;
        case 5:
          if (!res.containsKey(5d)) {
            res.put(5., 1);
          } else {
            res.put(5., res.get(5.) + 1);
          }
          break;
      }
    }
    return res;
  }
}
