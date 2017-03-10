public interface EvaluationInterface {

  double getRootMeanSquareError(int threshold);

  EvalResult getEfficiency(int threshold);

  double getCoverage(int threshold);
}
