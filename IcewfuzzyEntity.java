package de.icewfuzzy;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.EnumSet;

public class IcewfuzzyEntity extends PathAwareEntity {
    /** 0 = genervt, 1 = verlegen, 2 = froh. In-Memory, steigt durch Interaktion, sinkt mit der Zeit. */
    private int affection = 0;
    private int chatterTimer = 20 * 30;
    private int lastTeleportTick = 0;

    public IcewfuzzyEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.32)
            .add(EntityAttributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FollowPlayerGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    private int mood() {
        if (affection >= 8) return 2;
        if (affection >= 3) return 1;
        return 0;
    }

    private void say(PlayerEntity player, String line) {
        if (player instanceof ServerPlayerEntity sp) {
            String name = IcewfuzzyConfig.get().displayName;
            sp.sendMessage(Text.literal("<" + name + "> ").formatted(Formatting.AQUA)
                .append(Text.literal(line).formatted(Formatting.WHITE)), false);
        }
    }

    private String lineForMood() {
        if (!IcewfuzzyConfig.get().tsundereLevel) return Dialogue.pick(Dialogue.CLINGY_FRIENDLY);
        return switch (mood()) {
            case 2 -> Dialogue.pick(Dialogue.HAPPY);
            case 1 -> Dialogue.pick(Dialogue.EMBARRASSED);
            default -> Dialogue.pick(Dialogue.ANNOYED);
        };
    }

    /** Rechtsklick = Interaktion. */
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        if (player instanceof ServerPlayerEntity) {
            affection = Math.min(affection + 1, 12);
            say(player, lineForMood());
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        boolean r = super.damage(world, source, amount);
        if (r && source.getAttacker() instanceof PlayerEntity p) {
            affection = Math.max(0, affection - 3);
            say(p, Dialogue.pick(Dialogue.HURT));
        }
        return r;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        World w = this.getEntityWorld();
        if (w.isClient()) return;

        // Zuneigung sinkt langsam mit der Zeit -> Tsundere kehrt zurück
        if (this.age % (20 * 90) == 0 && affection > 0) affection--;

        IcewfuzzyConfig cfg = IcewfuzzyConfig.get();
        if (cfg.chatter && --chatterTimer <= 0) {
            chatterTimer = 20 * Math.max(5, cfg.chatterSeconds) + this.random.nextInt(200);
            PlayerEntity p = w.getClosestPlayer(this, 10.0);
            if (p != null) {
                if (!cfg.tsundereLevel) say(p, Dialogue.pick(Dialogue.CLINGY_FRIENDLY));
                else say(p, mood() == 0 ? Dialogue.pick(Dialogue.IDLE_ANNOYED) : lineForMood());
            }
        }
    }

    void onTeleported(PlayerEntity p) {
        if (this.age - lastTeleportTick > 20 * 20) {
            lastTeleportTick = this.age;
            if (IcewfuzzyConfig.get().tsundereLevel) say(p, Dialogue.pick(Dialogue.TELEPORT));
        }
    }

    @Override
    public boolean cannotDespawn() { return true; }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) { return false; }

    /** Folgt dem nächsten Spieler; teleportiert sich bei großer Distanz. */
    static class FollowPlayerGoal extends Goal {
        private final IcewfuzzyEntity mob;
        private PlayerEntity target;

        FollowPlayerGoal(IcewfuzzyEntity mob) {
            this.mob = mob;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            IcewfuzzyConfig cfg = IcewfuzzyConfig.get();
            if (!cfg.follow) return false;
            target = mob.getEntityWorld().getClosestPlayer(mob, 96.0);
            if (target == null || target.isSpectator()) return false;
            return mob.squaredDistanceTo(target) > cfg.followDistance * cfg.followDistance;
        }

        @Override
        public boolean shouldContinue() {
            IcewfuzzyConfig cfg = IcewfuzzyConfig.get();
            if (!cfg.follow || target == null || !target.isAlive()) return false;
            double stop = Math.max(1.5, cfg.followDistance - 1.0);
            return mob.squaredDistanceTo(target) > stop * stop;
        }

        @Override
        public void stop() { target = null; mob.getNavigation().stop(); }

        @Override
        public void tick() {
            if (target == null) return;
            IcewfuzzyConfig cfg = IcewfuzzyConfig.get();
            double tp = cfg.teleportDistance;
            if (mob.squaredDistanceTo(target) > tp * tp) {
                mob.setPosition(target.getX() + 1.0, target.getY(), target.getZ() + 1.0);
                mob.getNavigation().stop();
                mob.onTeleported(target);
                return;
            }
            mob.getLookControl().lookAt(target, 10.0f, mob.getMaxLookPitchChange());
            if (mob.age % 10 == 0) {
                mob.getNavigation().startMovingTo(target, 1.15);
            }
        }
    }
}
