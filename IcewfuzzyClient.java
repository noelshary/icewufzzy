package de.icewfuzzy.client;

import de.icewfuzzy.IcewfuzzyMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class IcewfuzzyClient implements ClientModInitializer {
    private static KeyBinding openMenu;

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(IcewfuzzyMod.ICEWFUZZY, IcewfuzzyRenderer::new);

        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.icewfuzzy.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,
            KeyBinding.Category.create(Identifier.of("icewfuzzy", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.player != null) client.setScreen(new IcewfuzzyScreen());
            }
        });
    }
}
