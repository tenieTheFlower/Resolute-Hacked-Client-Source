package disabler.mc.protocol.packet.ingame.server.entity;

import disabler.mc.protocol.data.MagicValues;
import disabler.mc.protocol.data.game.entity.Effect;
import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerEntityRemoveEffectPacket extends MinecraftPacket {
    private int entityId;
    private Effect effect;

    @SuppressWarnings("unused")
    private ServerEntityRemoveEffectPacket() {
    }

    public ServerEntityRemoveEffectPacket(int entityId, Effect effect) {
        this.entityId = entityId;
        this.effect = effect;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Effect getEffect() {
        return this.effect;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.entityId = in.readVarInt();
        this.effect = MagicValues.key(Effect.class, in.readUnsignedByte());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.entityId);
        out.writeByte(MagicValues.value(Integer.class, this.effect));
    }
}
