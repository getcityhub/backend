package nyc.getcityhub.models;

/**
 * Created by carol on 3/10/17.
 */
public enum ReportReason {
    OTHER(0),
    INAPPROPRIATE(1);

    private int id;

    ReportReason(int id) {
        this.id = id;
    }

    public static ReportReason fromId(int id) {
        switch (id) {
            case 1:
                return INAPPROPRIATE;
            default:
                return OTHER;
        }
    }

    public int getId() {
        return id;
    }
}
