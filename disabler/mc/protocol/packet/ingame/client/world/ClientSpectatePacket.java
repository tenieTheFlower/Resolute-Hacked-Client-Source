package disabler.mc.protocol.packet.ingame.client.world;

import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;
import java.util.UUID;

public class ClientSpectatePacket extends MinecraftPacket {
    private UUID target;

    @SuppressWarnings("unused")
    private ClientSpectatePacket() {
    }

    public ClientSpectatePacket(UUID target) {
        this.target = target;
    }

    public UUID getTarget() {
        return this.target;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.target = in.readUUID();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeUUID(this.target);
    }
}
