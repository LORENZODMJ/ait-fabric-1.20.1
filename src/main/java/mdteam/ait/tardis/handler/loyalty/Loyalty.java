package mdteam.ait.tardis.handler.loyalty;

public enum Loyalty { // fixme someone like loqor who had that hour long rant on how this will work can change all this. hahaha, i love it - Loqor
    NONE("none", 0),
    LOW("low", 5),
    MEDIUM("medium", 10),
    HIGH("high", 20);

    public final String id;
    public final int level;
    Loyalty(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public static Loyalty get(String id) {
        for (Loyalty loyalty : Loyalty.values()) {
            if (loyalty.id.equalsIgnoreCase(id)) return loyalty;
        }
        return null;
    }
    public static Loyalty get(int level) {
        Loyalty best = null;

        for (Loyalty loyalty : Loyalty.values()) {
            if (loyalty.level >= level) {
                if (best == null) best = loyalty;

                if (loyalty.level >= best.level) best = loyalty;
            }
        }
        return best;
    }
}
