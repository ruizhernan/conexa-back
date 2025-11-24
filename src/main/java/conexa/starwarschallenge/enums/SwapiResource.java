package conexa.starwarschallenge.enums;

public enum SwapiResource {
    FILMS("/films", "films"),
    PEOPLE("/people", "people"),
    STARSHIPS("/starships", "starships"),
    VEHICLES("/vehicles", "vehicles");

    private final String path;
    private final String resourceName;

    SwapiResource(String path, String resourceName) {
        this.path = path;
        this.resourceName = resourceName;
    }

    public String getPath() {
        return path;
    }

    public String getResourceName() {
        return resourceName;
    }
}
