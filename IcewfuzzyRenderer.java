package de.icewfuzzy.client;

import de.icewfuzzy.IcewfuzzyConfig;
import de.icewfuzzy.IcewfuzzyEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

public class IcewfuzzyRenderer extends BipedEntityRenderer<IcewfuzzyEntity, BipedEntityRenderState, BipedEntityModel<BipedEntityRenderState>> {
    private static final Identifier[] SKINS = {
        Identifier.of("icewfuzzy", "textures/entity/icewfuzzy_0.png"),
        Identifier.of("icewfuzzy", "textures/entity/icewfuzzy_1.png"),
        Identifier.of("icewfuzzy", "textures/entity/icewfuzzy_2.png"),
        Identifier.of("icewfuzzy", "textures/entity/icewfuzzy_3.png")
    };

    public IcewfuzzyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public BipedEntityRenderState createRenderState() { return new BipedEntityRenderState(); }

    @Override
    public Identifier getTexture(BipedEntityRenderState state) {
        return SKINS[Math.floorMod(IcewfuzzyConfig.get().skin, SKINS.length)];
    }
}
