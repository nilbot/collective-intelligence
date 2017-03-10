import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

  public static void main(String[] args) {
    String INPUT = Main.class.getResource("/100k.dat").getFile();
    String OUTPUT_STATS_PER_USER = "stats_user.csv";
    String OUTPUT_STATS_PER_MOVIE = "stats_movie.csv";
    String OUTPUT_BASELINE = "baseline.csv";
    String OUTPUT_MSD = "msd.csv";
    String OUTPUT_RESNICK = "resnick.csv";

    System.out.println("Started...");

    DataReader dr = new DataReader(INPUT);
    Set<Movie> movieSet = new HashSet<>(dr.getMovieMap().values());
    Set<User> userSet = new HashSet<>(dr.getUserMap().values());
    Set<Rating> ratingSet = dr.getRatingSet();

    System.out.println("User count: " + userSet.size());
    System.out.println("Movie count: " + movieSet.size());
    System.out.println("Rating count: " + ratingSet.size());
    System.out.println("Rating Density: " + DataSetStatistics.densityRatings(userSet, movieSet));
    Map<Double, Integer> map = DataSetStatistics.ratingBin(ratingSet);
    System.out.println(
        "Rating bin count for 1.0: " + map.get(1.) + " 2.0: " + map.get(2.) + " 3.0: " + map.get(3.)
            + " 4.0: " + map.get(4.) + " 5.0: " + map.get(5.));

    DataWriter statsWriter = new DataWriter(OUTPUT_STATS_PER_USER);
    List<StatsPerXXObj> objs = DataSetStatistics.PerUser(userSet);
    statsWriter.writeData(StatsPerXXObj.header, new ArrayList<>(objs));

    statsWriter = new DataWriter(OUTPUT_STATS_PER_MOVIE);
    objs = DataSetStatistics.PerMovie(movieSet, ratingSet);
    statsWriter.writeData(StatsPerXXObj.header, new ArrayList<>(objs));

    // baseLine
    BaseLine baseline = new BaseLine(userSet, movieSet, ratingSet);

    // msd
    Distance msd = new Distance(userSet, movieSet, ratingSet);

    // msdresnick
    DistanceResnick msdresnick = new DistanceResnick(userSet, movieSet, ratingSet);

    // evaluation L1O on different threshold n

    int[] N = {10, 20, 30, 40, 50};
    for (int n : N) {
      EvaluationInterface evalBaseLine = new L1OEvaluation(baseline);
      EvaluationInterface evalMSD = new L1OEvaluation(msd);
      EvaluationInterface evalMSDResnick = new L1OEvaluation(msdresnick);

      EvalResult evalBaseLineRes = evalBaseLine.getEfficiency(n);

      DataWriter baselineWriter = new DataWriter(OUTPUT_BASELINE);
      baselineWriter.writeData(EvalObj.header, new ArrayList<>(evalBaseLineRes.getEvalObjs()));
      double evalBaseLineCoverage = evalBaseLine.getCoverage(n);
      System.out.println(
          "BaseLine\n====\nN: " + n + "\nRMSE: " + evalBaseLineRes.getRmse()
              + "\nCoverage: " + evalBaseLineCoverage + "\nEfficiency: " + evalBaseLineRes
              .getRuntime() + " ms.\n");

      EvalResult evalMSDRes = evalMSD.getEfficiency(n);

      DataWriter msdWriter = new DataWriter(OUTPUT_MSD);
      msdWriter.writeData(EvalObj.header, new ArrayList<>(evalMSDRes.getEvalObjs()));
      double evalMSDCoverage = evalMSD.getCoverage(n);
      System.out.println(
          "MSD\n====\nN: " + n + "\nRMSE: " + evalMSDRes.getRmse() + "\nCoverage: "
              + evalMSDCoverage + "\nEfficiency: " + evalMSDRes.getRuntime() + " ms.\n");

      EvalResult evalResnick = evalMSDResnick.getEfficiency(n);

      DataWriter resnickWriter = new DataWriter(OUTPUT_RESNICK);
      resnickWriter.writeData(EvalObj.header, new ArrayList<>(evalResnick.getEvalObjs()));
      double evalResnickCoverage = evalMSDResnick.getCoverage(n);
      System.out.println(
          "MSD + Resnick\n====\nN: " + n + "\nRMSE: " + evalResnick.getRmse()
              + "\nCoverage: " + evalResnickCoverage + "\nEfficiency: " + evalResnick.getRuntime()
              + " ms.\n");
    }
  }
}

