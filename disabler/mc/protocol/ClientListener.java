package disabler.mc.protocol;

import disabler.mc.auth.data.GameProfile;
import disabler.mc.auth.exception.request.InvalidCredentialsException;
import disabler.mc.auth.exception.request.RequestException;
import disabler.mc.auth.exception.request.ServiceUnavailableException;
import disabler.mc.auth.service.SessionService;
import disabler.mc.protocol.data.SubProtocol;
import disabler.mc.protocol.data.handshake.HandshakeIntent;
import disabler.mc.protocol.data.status.ServerStatusInfo;
import disabler.mc.protocol.data.status.handler.ServerInfoHandler;
import disabler.mc.protocol.data.status.handler.ServerPingTimeHandler;
import disabler.mc.protocol.packet.handshake.client.HandshakePacket;
import disabler.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import disabler.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import disabler.mc.protocol.packet.ingame.server.ServerSetCompressionPacket;
import disabler.mc.protocol.packet.login.client.EncryptionResponsePacket;
import disabler.mc.protocol.packet.login.client.LoginStartPacket;
import disabler.mc.protocol.packet.login.server.EncryptionRequestPacket;
import disabler.mc.protocol.packet.login.server.LoginDisconnectPacket;
import disabler.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import disabler.mc.protocol.packet.login.server.LoginSuccessPacket;
import disabler.mc.protocol.packet.status.client.StatusPingPacket;
import disabler.mc.protocol.packet.status.client.StatusQueryPacket;
import disabler.mc.protocol.packet.status.server.StatusPongPacket;
import disabler.mc.protocol.packet.status.server.StatusResponsePacket;
import disabler.mc.protocol.util.CryptUtil;
import disabler.packetlib.event.session.ConnectedEvent;
import disabler.packetlib.event.session.PacketReceivedEvent;
import disabler.packetlib.event.session.SessionAdapter;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.Proxy;

public class ClientListener extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
        if (protocol.getSubProtocol() == SubProtocol.LOGIN) {
            if (event.getPacket() instanceof EncryptionRequestPacket) {
                EncryptionRequestPacket packet = event.getPacket();
                SecretKey key = CryptUtil.generateSharedKey();

                Proxy proxy = event.getSession().<Proxy>getFlag(MinecraftConstants.AUTH_PROXY_KEY);
                if (proxy == null) {
                    proxy = Proxy.NO_PROXY;
                }

                GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                String serverHash = new BigInteger(CryptUtil.getServerIdHash(packet.getServerId(), packet.getPublicKey(), key)).toString(16);
                String accessToken = event.getSession().getFlag(MinecraftConstants.ACCESS_TOKEN_KEY);
                try {
                    new SessionService(proxy).joinServer(profile, accessToken, serverHash);
                } catch (ServiceUnavailableException e) {
                    event.getSession().disconnect("Login failed: Authentication service unavailable.", e);
                    return;
                } catch (InvalidCredentialsException e) {
                    event.getSession().disconnect("Login failed: Invalid login session.", e);
                    return;
                } catch (RequestException e) {
                    event.getSession().disconnect("Login failed: Authentication error: " + e.getMessage(), e);
                    return;
                }

                event.getSession().send(new EncryptionResponsePacket(key, packet.getPublicKey(), packet.getVerifyToken()));
                protocol.enableEncryption(key);
            } else if (event.getPacket() instanceof LoginSuccessPacket) {
                LoginSuccessPacket packet = event.getPacket();
                event.getSession().setFlag(MinecraftConstants.PROFILE_KEY, packet.getProfile());
                protocol.setSubProtocol(SubProtocol.GAME, true, event.getSession());
            } else if (event.getPacket() instanceof LoginDisconnectPacket) {
                LoginDisconnectPacket packet = event.getPacket();
                event.getSession().disconnect(packet.getReason().getFullText());
            } else if (event.getPacket() instanceof LoginSetCompressionPacket) {
                event.getSession().setCompressionThreshold(event.<LoginSetCompressionPacket>getPacket().getThreshold());
            }
        } else if (protocol.getSubProtocol() == SubProtocol.STATUS) {
            if (event.getPacket() instanceof StatusResponsePacket) {
                ServerStatusInfo info = event.<StatusResponsePacket>getPacket().getInfo();
                ServerInfoHandler handler = event.getSession().getFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY);
                if (handler != null) {
                    handler.handle(event.getSession(), info);
                }

                event.getSession().send(new StatusPingPacket(System.currentTimeMillis()));
            } else if (event.getPacket() instanceof StatusPongPacket) {
                long time = System.currentTimeMillis() - event.<StatusPongPacket>getPacket().getPingTime();
                ServerPingTimeHandler handler = event.getSession().getFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY);
                if (handler != null) {
                    handler.handle(event.getSession(), time);
                }

                event.getSession().disconnect("Finished");
            }
        } else if (protocol.getSubProtocol() == SubProtocol.GAME) {
            if (event.getPacket() instanceof ServerKeepAlivePacket) {
//                event.getSession().send(new ClientKeepAlivePacket(event.<ServerKeepAlivePacket>getPacket().getPingId()));
            } else if (event.getPacket() instanceof ServerDisconnectPacket) {
                event.getSession().disconnect(event.<ServerDisconnectPacket>getPacket().getReason().getFullText());
            } else if (event.getPacket() instanceof ServerSetCompressionPacket) {
                event.getSession().setCompressionThreshold(event.<ServerSetCompressionPacket>getPacket().getThreshold());
            }
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
        MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
        if (protocol.getSubProtocol() == SubProtocol.LOGIN) {
            GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
            protocol.setSubProtocol(SubProtocol.HANDSHAKE, true, event.getSession());
            event.getSession().send(new HandshakePacket(MinecraftConstants.PROTOCOL_VERSION, event.getSession().getHost(), event.getSession().getPort(), HandshakeIntent.LOGIN));
            protocol.setSubProtocol(SubProtocol.LOGIN, true, event.getSession());
            event.getSession().send(new LoginStartPacket(profile != null ? profile.getName() : ""));
        } else if (protocol.getSubProtocol() == SubProtocol.STATUS) {
            protocol.setSubProtocol(SubProtocol.HANDSHAKE, true, event.getSession());
            event.getSession().send(new HandshakePacket(MinecraftConstants.PROTOCOL_VERSION, event.getSession().getHost(), event.getSession().getPort(), HandshakeIntent.STATUS));
            protocol.setSubProtocol(SubProtocol.STATUS, true, event.getSession());
            event.getSession().send(new StatusQueryPacket());
        }
    }
}
