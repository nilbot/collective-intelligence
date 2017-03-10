import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class DataWriter {

  private String dataFilename;

  public DataWriter(final String dataFile) {
    this.dataFilename = dataFile;
  }

  public void writeData(String header, List<Object> objs) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(dataFilename));
      writer.write(header);
      writer.newLine();
      for (Object obj : objs) {
        writer.write(obj.toString());
        writer.newLine();
      }
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

class StatsPerXXObj {

  static String header = "id,mean,median,sd,max,min";
  private final double mean;
  private final double median;
  private final double sd;
  private final double max;
  private final double min;
  private final int id;

  public StatsPerXXObj(int id, double mean, double median, double sd, double max, double min) {
    this.id = id;
    this.mean = mean;
    this.median = median;
    this.sd = sd;
    this.max = max;
    this.min = min;
  }

  @Override
  public String toString() {
    return "" + id + "," + mean + "," + median + "," + sd + "," + max + "," + min;
  }
}

class EvalObj {

  static String header = "user_id,item_id,actual_rating,predicted_rating,rmse";
  private final int uid;
  private final int mid;
  private final double ar;
  private final double pr;
  private final double rmse;

  public EvalObj(int uid, int mid, double ar, double pr, double rmse) {
    this.uid = uid;
    this.mid = uid;
    this.ar = ar;
    this.pr = pr;
    this.rmse = rmse;
  }

  @Override
  public String toString() {
    return "" + uid + "," + mid + "," + ar + "," + pr + "," + rmse;
  }
}