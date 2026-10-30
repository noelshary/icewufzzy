package de.icewfuzzy.client;

import de.icewfuzzy.IcewfuzzyConfig;
import de.icewfuzzy.MenuPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Locale;

public class IcewfuzzyScreen extends Screen {
    public IcewfuzzyScreen() { super(Text.literal("Icewfuzzy – Einstellungen")); }

    private void set(String key, String value) {
        IcewfuzzyConfig.apply(key, value);
        ClientPlayNetworking.send(new MenuPayload(key, value));
        this.clearAndInit();
    }

    private void act(String key) { ClientPlayNetworking.send(new MenuPayload(key, "")); }

    private static String onOff(boolean b) { return b ? "AN" : "AUS"; }

    private void row(int y, String label, Runnable minus, Runnable plus) {
        int cx = this.width / 2;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> minus.run()).dimensions(cx - 100, y, 20, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> plus.run()).dimensions(cx - 78, y, 156, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> plus.run()).dimensions(cx + 80, y, 20, 20).build());
    }

    @Override
    protected void init() {
        IcewfuzzyConfig c = IcewfuzzyConfig.get();
        int y = this.height / 2 - 100;
        int cx = this.width / 2;
        int step = 24;

        row(y, "Aussehen: " + IcewfuzzyConfig.SKIN_NAMES[Math.floorMod(c.skin, IcewfuzzyConfig.SKIN_NAMES.length)],
            () -> set("skin", String.valueOf(c.skin - 1)), () -> set("skin", String.valueOf(c.skin + 1))); y += step;

        row(y, "Folgt mir: " + onOff(c.follow),
            () -> set("follow", String.valueOf(!c.follow)), () -> set("follow", String.valueOf(!c.follow))); y += step;

        row(y, String.format(Locale.ROOT, "Folge-Abstand: %.0f Blöcke", c.followDistance),
            () -> set("followDistance", String.valueOf(c.followDistance - 1)), () -> set("followDistance", String.valueOf(c.followDistance + 1))); y += step;

        row(y, String.format(Locale.ROOT, "Teleport ab: %.0f Blöcke", c.teleportDistance),
            () -> set("teleportDistance", String.valueOf(c.teleportDistance - 4)), () -> set("teleportDistance", String.valueOf(c.teleportDistance + 4))); y += step;

        row(y, "Selbstgespräche: " + onOff(c.chatter),
            () -> set("chatter", String.valueOf(!c.chatter)), () -> set("chatter", String.valueOf(!c.chatter))); y += step;

        row(y, "Spruch-Abstand: " + c.chatterSeconds + " s",
            () -> set("chatterSeconds", String.valueOf(c.chatterSeconds - 10)), () -> set("chatterSeconds", String.valueOf(c.chatterSeconds + 10))); y += step;

        row(y, "Charakter: " + (c.tsundereLevel ? "Tsundere" : "Nur freundlich"),
            () -> set("tsundere", String.valueOf(!c.tsundereLevel)), () -> set("tsundere", String.valueOf(!c.tsundereLevel))); y += step + 8;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Icewfuzzy beschwören"), b -> act("summon")).dimensions(cx - 100, y, 200, 20).build()); y += step;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Icewfuzzy entfernen"), b -> act("remove")).dimensions(cx - 100, y, 200, 20).build()); y += step;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Fertig"), b -> this.close()).dimensions(cx - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 125, 0xFF9AD0FF);
    }
}
