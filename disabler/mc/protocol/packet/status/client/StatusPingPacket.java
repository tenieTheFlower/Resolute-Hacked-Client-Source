package disabler.mc.protocol.packet.status.client;

import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class StatusPingPacket extends MinecraftPacket {
    private long time;

    @SuppressWarnings("unused")
    private StatusPingPacket() {
    }

    public StatusPingPacket(long time) {
        this.time = time;
    }

    public long getPingTime() {
        return this.time;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.time = in.readLong();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeLong(this.time);
    }
}
