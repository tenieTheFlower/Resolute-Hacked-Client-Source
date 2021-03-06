package disabler.mc.protocol.packet.ingame.server.world;

import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerUpdateTimePacket extends MinecraftPacket {
    private long age;
    private long time;

    @SuppressWarnings("unused")
    private ServerUpdateTimePacket() {
    }

    public ServerUpdateTimePacket(long age, long time) {
        this.age = age;
        this.time = time;
    }

    public long getWorldAge() {
        return this.age;
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.age = in.readLong();
        this.time = in.readLong();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeLong(this.age);
        out.writeLong(this.time);
    }
}
