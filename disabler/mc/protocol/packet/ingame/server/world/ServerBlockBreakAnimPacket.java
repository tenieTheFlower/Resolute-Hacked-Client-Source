package disabler.mc.protocol.packet.ingame.server.world;

import disabler.mc.protocol.data.MagicValues;
import disabler.mc.protocol.data.game.entity.metadata.Position;
import disabler.mc.protocol.data.game.entity.player.BlockBreakStage;
import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.mc.protocol.util.NetUtil;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerBlockBreakAnimPacket extends MinecraftPacket {
    private int breakerEntityId;
    private Position position;
    private BlockBreakStage stage;

    @SuppressWarnings("unused")
    private ServerBlockBreakAnimPacket() {
    }

    public ServerBlockBreakAnimPacket(int breakerEntityId, Position position, BlockBreakStage stage) {
        this.breakerEntityId = breakerEntityId;
        this.position = position;
        this.stage = stage;
    }

    public int getBreakerEntityId() {
        return this.breakerEntityId;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockBreakStage getStage() {
        return this.stage;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.breakerEntityId = in.readVarInt();
        this.position = NetUtil.readPosition(in);
        try {
            this.stage = MagicValues.key(BlockBreakStage.class, in.readUnsignedByte());
        } catch (IllegalArgumentException e) {
            this.stage = BlockBreakStage.RESET;
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.breakerEntityId);
        NetUtil.writePosition(out, this.position);
        out.writeByte(MagicValues.value(Integer.class, this.stage));
    }
}
