package disabler.mc.protocol.packet.ingame.server;

import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerTabCompletePacket extends MinecraftPacket {
    private String matches[];

    @SuppressWarnings("unused")
    private ServerTabCompletePacket() {
    }

    public ServerTabCompletePacket(String matches[]) {
        this.matches = matches;
    }

    public String[] getMatches() {
        return this.matches;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.matches = new String[in.readVarInt()];
        for (int index = 0; index < this.matches.length; index++) {
            this.matches[index] = in.readString();
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.matches.length);
        for (String match : this.matches) {
            out.writeString(match);
        }
    }
}
