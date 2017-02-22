public class Movie {
    private String name;
    private Integer id;

    public Movie(final String name, final Integer id) {
        this.name = name;
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
