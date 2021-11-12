package disabler.mc.protocol.packet.ingame.client.world;

import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class ClientSteerBoatPacket extends MinecraftPacket {
    private boolean rightPaddleTurning;
    private boolean leftPaddleTurning;

    @SuppressWarnings("unused")
    private ClientSteerBoatPacket() {
    }

    public ClientSteerBoatPacket(boolean rightPaddleTurning, boolean leftPaddleTurning) {
        this.rightPaddleTurning = rightPaddleTurning;
        this.leftPaddleTurning = leftPaddleTurning;
    }

    public boolean isRightPaddleTurning() {
        return this.rightPaddleTurning;
    }

    public boolean isLeftPaddleTurning() {
        return this.leftPaddleTurning;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.rightPaddleTurning = in.readBoolean();
        this.leftPaddleTurning = in.readBoolean();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeBoolean(this.rightPaddleTurning);
        out.writeBoolean(this.leftPaddleTurning);
    }
}
