package nyc.getcityhub.models;

public enum Topic {
    OTHER(0, "Other"),
    PUBLIC_HEALTH_AND_SAFETY(1, "Public Health/Safety"),
    TRANSPORTATION(2, "Transportation"),
    VEHICLES_AND_PARKING(3, "Vehicles and Parking"),
    TAXES(4, "Taxes"),
    NOISE(5, "Noise"),
    BUSINESS(6, "Business"),
    EDUCATION(7, "Education"),
    CIVIC_SERVICES(8, "Civic Services"),
    HOUSING_AND_DEVELOPMENT(9, "Housing and Development"),
    RECREATION(10, "Recreation"),
    SOCIAL_SERVICES(11, "Social Services");

    private int id;
    private String name;

    Topic(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static Topic fromId(int id) {
        switch(id) {
            case 0: return OTHER;
            case 1: return PUBLIC_HEALTH_AND_SAFETY;
            case 2: return TRANSPORTATION;
            case 3: return VEHICLES_AND_PARKING;
            case 4: return TAXES;
            case 5: return NOISE;
            case 6: return BUSINESS;
            case 7: return EDUCATION;
            case 8: return CIVIC_SERVICES;
            case 9: return HOUSING_AND_DEVELOPMENT;
            case 10: return RECREATION;
            case 11: return SOCIAL_SERVICES;
            default: return null;
        }
    }

    public int getId() {
        return id;
    }

    public String getName(Language language) {
        return Translation.getTranslation(name, language);
    }
}