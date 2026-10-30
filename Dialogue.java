package de.icewfuzzy;

import java.util.Random;

/** Tsundere-Sprüche je nach Stimmung. */
public class Dialogue {
    private static final Random R = new Random();

    public static final String[] ANNOYED = {
        "Hmpf! Fass mich nicht so einfach an, Baka!",
        "I-ich hab nur zufällig die gleiche Richtung wie du, okay?!",
        "Denk bloß nicht, dass ich auf dich aufpasse!",
        "Was willst du denn schon wieder?! Ich hab zu tun!",
        "Tch... du bist echt nervig. Wirklich.",
        "Geh mir nicht auf die Nerven, sonst... sonst... ach, egal!"
    };
    public static final String[] EMBARRASSED = {
        "W-was?! Ich werde doch nicht rot! Das ist die Kälte!",
        "D-das war kein Kompliment, hörst du?! Vergiss es sofort!",
        "Starr mich nicht so an... Baka...",
        "I-ich freu mich nicht! Nur ein ganz kleines bisschen... vielleicht."
    };
    public static final String[] HAPPY = {
        "...Danke, dass du da bist. Sag das aber niemandem!",
        "Na gut, du darfst bei mir bleiben. Aber nur heute!",
        "Hehe... äh! Ich meine: Hmpf. Ganz okay.",
        "Wenn du in Gefahr bist, beschütze ich dich. A-aber nur, weil es sonst peinlich für mich wäre!"
    };
    public static final String[] CLINGY_FRIENDLY = {
        "Hallo! Schön, dass du da bist.",
        "Ich begleite dich gerne!",
        "Lass uns zusammen was erleben!"
    };
    public static final String[] IDLE_ANNOYED = {
        "Wie lange willst du hier noch rumstehen?!",
        "Mir ist kalt... n-nicht, dass es dich interessiert!",
        "Warte nicht auf mich! ...Aber geh auch nicht zu weit weg.",
        "Pff, ich langweile mich. Das heißt nicht, dass ich dich vermisse!"
    };
    public static final String[] HURT = {
        "Aua! Pass doch auf, du Trampel!",
        "H-hey! Das tat weh!"
    };
    public static final String[] TELEPORT = {
        "Warte auf mich! ...Ich meine: Du bist zu langsam!",
        "Du kannst mich doch nicht einfach zurücklassen, Baka!"
    };

    public static String pick(String[] lines) { return lines[R.nextInt(lines.length)]; }
}
