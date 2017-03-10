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
    String OUTPUT_MSD = "msd%d.csv";
    String OUTPUT_RESNICK = "resnick%d.csv";
    String LOGFILE = "output.log";

    DataWriter logWriter = new DataWriter(LOGFILE);

    List<String> logs = new ArrayList<>();

    logs.add(String.format("Started..."));

    DataReader dr = new DataReader(INPUT);
    Set<Movie> movieSet = new HashSet<>(dr.getMovieMap().values());
    Set<User> userSet = new HashSet<>(dr.getUserMap().values());
    Set<Rating> ratingSet = dr.getRatingSet();

    logs.add(String.format("User count: " + userSet.size()));
    logs.add(String.format("Movie count: " + movieSet.size()));
    logs.add(String.format("Rating count: " + ratingSet.size()));
    logs.add(
        String.format("Rating Density: " + DataSetStatistics.densityRatings(userSet, movieSet)));
    Map<Double, Integer> map = DataSetStatistics.ratingBin(ratingSet);
    logs.add(String.format(
        "Rating bin count for 1.0: " + map.get(1.) + " 2.0: " + map.get(2.) + " 3.0: " + map.get(3.)
            + " 4.0: " + map.get(4.) + " 5.0: " + map.get(5.)));

    logs.add(String.format("Mean Rating: " + DataSetStatistics.MeanRatingAll(ratingSet)));

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

    EvaluationInterface evalBaseLine = new L1OEvaluation(baseline);
    EvalResult evalBaseLineRes = evalBaseLine.getEfficiency(userSet.size());

    DataWriter baselineWriter = new DataWriter(OUTPUT_BASELINE);
    baselineWriter.writeData(EvalObj.header, new ArrayList<>(evalBaseLineRes.getEvalObjs()));
    double evalBaseLineCoverage = evalBaseLine.getCoverage(userSet.size());
    logs.add(String.format(
        "BaseLine\n====\nN: " + userSet.size() + "\nRMSE: " + evalBaseLineRes.getRmse()
            + "\nCoverage: " + evalBaseLineCoverage + "\nEfficiency: " + evalBaseLineRes
            .getRuntime() + " ms.\n"));

    // evaluation L1O on different threshold n
    int[] N = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300};
    for (int n : N) {

      EvaluationInterface evalMSD = new L1OEvaluation(msd);
      EvaluationInterface evalMSDResnick = new L1OEvaluation(msdresnick);

      EvalResult evalMSDRes = evalMSD.getEfficiency(n);

      DataWriter msdWriter = new DataWriter(String.format(OUTPUT_MSD, n));
      msdWriter.writeData(EvalObj.header, new ArrayList<>(evalMSDRes.getEvalObjs()));
      double evalMSDCoverage = evalMSD.getCoverage(n);
      logs.add(String.format(
          "MSD\n====\nN: " + n + "\nRMSE: " + evalMSDRes.getRmse() + "\nCoverage: "
              + evalMSDCoverage + "\nEfficiency: " + evalMSDRes.getRuntime() + " ms.\n"));

      EvalResult evalResnick = evalMSDResnick.getEfficiency(n);

      DataWriter resnickWriter = new DataWriter(String.format(OUTPUT_RESNICK, n));
      resnickWriter.writeData(EvalObj.header, new ArrayList<>(evalResnick.getEvalObjs()));
      double evalResnickCoverage = evalMSDResnick.getCoverage(n);
      logs.add(String.format(
          "MSD + Resnick\n====\nN: " + n + "\nRMSE: " + evalResnick.getRmse()
              + "\nCoverage: " + evalResnickCoverage + "\nEfficiency: " + evalResnick.getRuntime()
              + " ms.\n"));
    }
    logWriter.writeData("\n====\n", new ArrayList<>(logs));
  }
}

