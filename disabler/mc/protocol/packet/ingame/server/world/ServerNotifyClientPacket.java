package disabler.mc.protocol.packet.ingame.server.world;

import disabler.mc.protocol.data.MagicValues;
import disabler.mc.protocol.data.game.entity.player.GameMode;
import disabler.mc.protocol.data.game.world.notify.*;
import disabler.mc.protocol.packet.MinecraftPacket;
import disabler.packetlib.io.NetInput;
import disabler.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerNotifyClientPacket extends MinecraftPacket {
    private ClientNotification notification;
    private ClientNotificationValue value;

    @SuppressWarnings("unused")
    private ServerNotifyClientPacket() {
    }

    public ServerNotifyClientPacket(ClientNotification notification, ClientNotificationValue value) {
        this.notification = notification;
        this.value = value;
    }

    public ClientNotification getNotification() {
        return this.notification;
    }

    public ClientNotificationValue getValue() {
        return this.value;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.notification = MagicValues.key(ClientNotification.class, in.readUnsignedByte());
        float value = in.readFloat();
        if (this.notification == ClientNotification.CHANGE_GAMEMODE) {
            this.value = MagicValues.key(GameMode.class, (int) value);
        } else if (this.notification == ClientNotification.DEMO_MESSAGE) {
            this.value = MagicValues.key(DemoMessageValue.class, (int) value);
        } else if (this.notification == ClientNotification.ENTER_CREDITS) {
            this.value = MagicValues.key(EnterCreditsValue.class, (int) value);
        } else if (this.notification == ClientNotification.RAIN_STRENGTH) {
            this.value = new RainStrengthValue(value);
        } else if (this.notification == ClientNotification.THUNDER_STRENGTH) {
            this.value = new ThunderStrengthValue(value);
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(MagicValues.value(Integer.class, this.notification));
        float value = 0;
        if (this.value instanceof Enum<?>) {
            value = MagicValues.value(Integer.class, (Enum<?>) this.value);
        } else if (this.value instanceof RainStrengthValue) {
            value = ((RainStrengthValue) this.value).getStrength();
        } else if (this.value instanceof ThunderStrengthValue) {
            value = ((ThunderStrengthValue) this.value).getStrength();
        }

        out.writeFloat(value);
    }
}
