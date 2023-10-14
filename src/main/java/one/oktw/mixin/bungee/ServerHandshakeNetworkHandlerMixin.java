package one.oktw.mixin.bungee;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UndashedUuid;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oktw.interfaces.BungeeClientConnection;
import one.oktw.mixin.ClientConnectionAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    @Unique
    private static final Gson gson = new Gson();

    @Shadow
    @Final
    private ClientConnection connection;

    /**
     * onProcessHandshakeStart<br>
     * <br>
     * For context:<br>
     * > BungeeCord IP forwarding is simply a special injection after the "address" in the handshake,<br>
     * > separated by \0 (the null byte). In order, you send the original host, the player's IP, their<br>
     * > UUID (undashed), and if you are in online-mode, their login properties (from Mojang).<br>
     * <a href="https://github.com/PaperMC/Velocity/blob/f884e049c006f5fae14abaab9332ccaad9f001e0/proxy/src/main/java/com/velocitypowered/proxy/connection/backend/VelocityServerConnection.java#L143">VelocityServerConnection.java</a>
     */
    @Inject(method = "onHandshake", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;)V"))
    private void onHandshake_ServerLoginNetworkHandler$init(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (packet.intendedState().getState().equals(NetworkState.LOGIN)) {
            String[] split = packet.address().split("\00");
            if (split.length == 3 || split.length == 4) {
                // override/insert forwarded IP into connection
                ((ClientConnectionAccessor) connection).setAddress(
                        new InetSocketAddress(split[1], ((InetSocketAddress) connection.getAddress()).getPort())
                );

                // extract and save forwarded profile information
                ((BungeeClientConnection) connection).fabricproxylegacy$setSpoofedUUID(UndashedUuid.fromString(split[2]));

                if (split.length == 4) {
                    ((BungeeClientConnection) connection).fabricproxylegacy$setSpoofedProfile(gson.fromJson(split[3], Property[].class));
                }
            } else {
                // no extra information found in the address; disconnect player
                Text disconnectMessage = MutableText.of(new LiteralTextContent("This server requires you to connect with Velocity."));
                connection.send(new LoginDisconnectS2CPacket(disconnectMessage));
                connection.disconnect(disconnectMessage);
            }
        }
    }
}
