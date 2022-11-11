package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.BlockCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.*;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

import static cn.nukkit.entity.ai.evaluator.BehaviorEvaluators.*;

public class EntityPanda extends EntityWalkingAnimal {

    public static final int NETWORK_ID = 113;

    public EntityPanda(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        return 1.5f;
    }

    private IBehaviorGroup behaviorGroup;

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }


    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Panda";
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(
                            //用于刷新InLove状态的核心行为
                            new Behavior(
                                    new InLoveExecutor(400),
                                    all(
                                            passBy(PlayerBreedingMemory.class, 0, 400),
                                            passBy(InLoveMemory.class, 6000, Integer.MAX_VALUE, true)
                                    ),
                                    1, 1
                            )
                    ),
                    Set.of(
                            new Behavior(new RandomRoamExecutor(0.25f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator<>(AttackMemory.class, 0, 100), 6, 1),
                            new Behavior(new EntityBreedingExecutor<>(EntitySheep.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(InLoveMemory.class).isInLove(), 5, 1),
                            new Behavior(new MoveToTargetExecutor(NearestFeedingPlayerMemory.class, 0.25f, true, 8, 1.5f), new MemoryCheckNotEmptyEvaluator(NearestFeedingPlayerMemory.class), 4, 1),
                            new Behavior(new EatGrassExecutor(40),
                                    all(
                                            chance(43, 50),
                                            any(
                                                    new BlockCheckEvaluator(Block.GRASS, new Vector3(0, -1, 0)),
                                                    new BlockCheckEvaluator(Block.TALL_GRASS, Vector3.ZERO))
                                    ),
                                    3, 1, 100
                            ),
                            new Behavior(new LookAtTargetExecutor(NearestPlayerMemory.class, 100), chance(4, 10), 1, 1, 100),
                            new Behavior(new RandomRoamExecutor(0.1f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                    ),
                    Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }

}
