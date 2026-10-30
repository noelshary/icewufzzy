package de.icewfuzzy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Einstellungen, gespeichert in config/icewfuzzy.json. Änderbar per /icewfuzzy ... */
public class IcewfuzzyConfig {
    public boolean follow = true;
    public double followDistance = 3.0;     // ab hier läuft sie zu dir
    public double teleportDistance = 16.0;  // ab hier teleportiert sie sich
    public boolean chatter = true;          // spricht ab und zu von allein
    public int chatterSeconds = 45;         // Abstand zwischen Sprüchen
    public boolean tsundereLevel = true;    // true = zickig, false = nur freundlich
    public String displayName = "Icewfuzzy";
    public int skin = 0;                    // Aussehen (0-3), nur visuell/clientseitig

    public static final String[] SKIN_NAMES = {"Eisblau", "Sakura-Rosa", "Silber-Weiß", "Nacht-Lila"};

    /** Wird vom Menü (lokal) und vom Server (per Paket) benutzt. */
    public static void apply(String key, String value) {
        IcewfuzzyConfig c = get();
        try {
            switch (key) {
                case "follow" -> c.follow = Boolean.parseBoolean(value);
                case "followDistance" -> c.followDistance = Math.max(2, Math.min(32, Double.parseDouble(value)));
                case "teleportDistance" -> c.teleportDistance = Math.max(6, Math.min(128, Double.parseDouble(value)));
                case "chatter" -> c.chatter = Boolean.parseBoolean(value);
                case "chatterSeconds" -> c.chatterSeconds = Math.max(5, Math.min(600, Integer.parseInt(value)));
                case "tsundere" -> c.tsundereLevel = Boolean.parseBoolean(value);
                case "skin" -> c.skin = Math.floorMod(Integer.parseInt(value), SKIN_NAMES.length);
                default -> { return; }
            }
        } catch (NumberFormatException ignored) { return; }
        save();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("icewfuzzy.json");
    private static IcewfuzzyConfig instance;

    public static IcewfuzzyConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader r = Files.newBufferedReader(FILE)) {
                    instance = GSON.fromJson(r, IcewfuzzyConfig.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (instance == null) { instance = new IcewfuzzyConfig(); save(); }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(instance, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
