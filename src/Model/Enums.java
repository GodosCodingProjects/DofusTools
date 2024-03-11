package Model;

public class Enums {
    public enum Preference {
        UNFAVORED,
        NEUTRAL,
        FAVORED,
        N_PREFERENCES
    }

    public enum ItemType {
        RESOURCE,
        CONSUMABLE,
        EQUIPMENT,
        RUNE,
        N_TYPES
    }
    public static String[] itemTypeNames = {
        "Ressource",
        "Consommable",
        "Équipement",
        "Rune"
    };

    public enum Job {
        NONE,
        HUNTER,
        TAILOR,
        SCULPTOR,
        MODELER,
        FISHER,
        JEWELER,
        ALCHIMIST,
        BAKER,
        SHOEMAKER,
        SMITH,
        LUMBERJACK,
        MINER,
        TWEAKER,
        N_JOBS
    }
    public static String[] jobNames = {
        "Aucun",
        "Chasseur",
        "Tailleur",
        "Sculpteur",
        "Façonneur",
        "Pêcheur",
        "Bijoutier",
        "Alchimiste",
        "Paysan",
        "Cordonnier",
        "Forgeron",
        "Bûcheron",
        "Mineur",
        "Bricoleur",
    };
}
