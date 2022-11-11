package cn.nukkit.entity.ai.controller;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityPanda;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.assertj.core.api.Assertions.assertThat;

@PowerNukkitOnly
@ExtendWith(PowerNukkitExtension.class)
class WalkControllerTest {

    private final Vector3 spawnPosition = new Vector3(0, 64, 0);

    @MockLevel
    Level level;

    EntityPanda panda;

    WalkController controller;

    @BeforeEach
    public void setup() {
        level.setBlockStateAt(0, 63, 0, BlockState.of(BlockID.STONE));

        panda = new EntityPanda(level.getChunk(0, 0),
                Entity.getDefaultNBT(spawnPosition)
                        .putString("NameTag", "A Cute panda").copy());

        controller = new WalkController();
    }

    @Test
    public void should_set_move_flag_to_false_when_has_no_move_direction() {
        controller.control(panda);
        assertThat(panda.getDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_MOVING)).isFalse();
    }

    @Test
    public void should_only_change_z_when_moving_on_z_axis() {
        panda.setMoveDirectionStart(spawnPosition);
        panda.setMoveDirectionEnd(spawnPosition.south());

        controller.control(panda);

        assertThat(panda.motionX).isEqualTo(0);
        assertThat(panda.motionY).isEqualTo(0);
        assertThat(panda.motionZ).isCloseTo(0.033, Offset.offset(0.001));
    }

    @Test
    public void should_only_change_x_when_moving_on_x_axis() {
        panda.setNeedUpdateMoveDirection(false);
        panda.setMoveDirectionStart(spawnPosition);
        panda.setMoveDirectionEnd(spawnPosition.east());

        controller.control(panda);

        assertThat(panda.motionX).isCloseTo(0.033, Offset.offset(0.001));
        assertThat(panda.motionY).isEqualTo(0);
        assertThat(panda.motionZ).isEqualTo(0);
    }

}