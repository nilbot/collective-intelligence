import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Pearson implements SimilarityInterface {

    private static Double MIN_RATING = 1.0;
    private static Double MAX_RATING = 5.0;
    private Double[][] simMatrix;
    private Set<User> userSet;
    private static Object lock = new Object();

    public Pearson(final Set<User> users) {
        this.userSet = users;
        this.simMatrix = new Double[users.size()][users.size()];

        // parallel
        computeAllSimilarity();
    }

    private class TinyWorkload implements Callable<Integer> {
        private final Set<User> workload;

        public TinyWorkload(final Set<User> workload) {
            this.workload = workload;
        }

        @Override
        public Integer call() throws Exception {
            int count = 0;
            for (User a : workload) {
                for (User b : workload) {
                    setSim(a, b, computeSimilarity(a, b));
                    count++;
                }
            }
            return count;
        }
    }

    private void setSim(User a, User b, Double value) {
        synchronized (lock) {
            this.simMatrix[a.Id()][b.Id()] = value;
        }
    }

    private void computeAllSimilarity() {
        Integer totes = getUserSet().size();
        int coreCount = Runtime.getRuntime().availableProcessors();
        List<User> totalWorkLoad = new ArrayList<>(getUserSet());
        Set<Set<User>> divide = new HashSet<>();
        int loadPerCore = totes / coreCount;
        int count = 0;
        for (int core = 0; core < coreCount - 1; core++) {
            Set<User> lst = new HashSet<>();
            for (int c = 0; c < loadPerCore; c++) {
                lst.add(totalWorkLoad.get(c * core + c));
                count++;
            }
            divide.add(lst);
        }
        Set<User> rest = new HashSet<>();
        rest.addAll(totalWorkLoad.subList(count, totes));
        divide.add(rest);

        ExecutorService exe = Executors.newFixedThreadPool(coreCount);
        List<Future<Integer>> res = new ArrayList<>();

        for (Set<User> portion : divide) {
            Future<Integer> future = exe.submit(new TinyWorkload(portion));
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
            System.err.println("finished parallel task count does not equal to total workload");
            System.exit(1); // TODO handle gracefully once tested
        }
    }

    @Override
    public Double computeSimilarity(User a, User b) {
        if (a.Id() == b.Id()) {
            return 1.; //TODO coverage consideration
        }
        Set<Movie> commonMovieSet = a.getCommonMovies(b);
        Double aMeanRating = a.getMeanRating();
        Double bMeanRating = b.getMeanRating();

        Double top, aDenom, bDenom;
        top = aDenom = bDenom = .0;
        for (Movie m : commonMovieSet) {
            Double ad = a.getRating(m) - aMeanRating;
            Double bd = b.getRating(m) - bMeanRating;
            top += (ad * bd);
            aDenom += (ad * ad);
            bDenom += (bd * bd);
        }
        Double denom = Math.sqrt(aDenom * bDenom);
        if (denom > 0) {
            if (commonMovieSet.size() < 50) { // TODO coverage consideration
                return (commonMovieSet.size() * (1 / 50.0)) * (top / denom);
            } else {
                return top / denom;
            }
        } else {
            return .0;
        }
    }

    @Override
    public Double predictRating(User u, Movie m, Double threshold) {
        Set<User> neighbours = computeNeighbours(u, threshold);
        Double top = .0;
        Double denom = .0;
        for (User p : neighbours) {
            if (p.hasRated(m)) {
                top += getPearson(u, p) * (p.getRating(m) - p.getMeanRating());
                denom += Math.abs(getPearson(u, p));
            }
        }
        if (denom > 0) {
            double prediction = u.getMeanRating() + top / denom;
            if (prediction < MIN_RATING) {
                prediction = MIN_RATING;
            }
            if (prediction > MAX_RATING) {
                prediction = MAX_RATING;
            }
            return prediction;
        } else {
            return -1.0;
        }
    }

    private Set<User> computeNeighbours(User u, Double threshold) {
        Set<User> res = new HashSet<>();
        for (User t : getUserSet()) {
            if (u.getId() != t.getId()) {
                if (getPearson(u, t) > threshold) {
                    res.add(t);
                }
            }
        }
        return res;
    }

    @Override
    public Set<User> getUserSet() {
        return this.userSet;
    }

    // retrieval O(1)
    private Double getPearson(User me, User other) {
        return this.simMatrix[me.Id()][other.Id()];
    }
}
