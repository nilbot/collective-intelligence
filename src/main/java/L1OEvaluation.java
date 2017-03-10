import java.util.ArrayList;
import java.util.Iterator;

public class L1OEvaluation implements EvaluationInterface {

  private final ArrayList<EvalObj> csv;
  private SimilarityInterface similarity;

  public L1OEvaluation(SimilarityInterface similarity) {
    this.similarity = similarity;
    this.csv = new ArrayList<>();
  }

  @Override
  public double getRootMeanSquareError(int threshold) {
    double errorSquareSum = 0.0;
    csv.clear();
    for (Rating r : this.similarity.getRatingSet()) {
      double actual = r.getRating();
      double predicted = this.similarity.predictRating(r.getUser(), r.getMovie(), threshold);
      double error = Math.abs(actual - predicted);
      csv.add(new EvalObj(r.getUser().getId(), r.getMovie().getId(), actual, predicted, error));
      errorSquareSum += error * error;
    }
    return Math.sqrt(errorSquareSum / (double) this.similarity.getRatingSet().size());
  }

  @Override
  public EvalResult getEfficiency(int threshold) {
    long start = System.currentTimeMillis();
    double rmse = getRootMeanSquareError(threshold);
    long end = System.currentTimeMillis();
    return new EvalResult(rmse, end - start, csv);
  }

  @Override
  public double getCoverage(int threshold) {
    int total = 0;
    int predicted = 0;
    for (Iterator<User> it = this.similarity.getUserSet().iterator(); it.hasNext(); ) {
      User u = it.next();
      for (Rating r : u.getRatings()) {
        Double predictionScore = this.similarity.predictRating(u, r.getMovie(), threshold);
        if (predictionScore > -1) {
          predicted++;
        }
        total++;
      }
    }
    return (total > 0) ? predicted / (double) total : .0;
  }


}
