package one.oktw.mixin.bungee;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import one.oktw.interfaces.BungeeClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements BungeeClientConnection {
    @Unique
    private UUID fabricproxylegacy$spoofedUUID;
    @Unique
    private Property[] fabricproxylegacy$spoofedProfile;

    @Override
    public UUID fabricproxylegacy$getSpoofedUUID() {
        return this.fabricproxylegacy$spoofedUUID;
    }

    @Override
    public void fabricproxylegacy$setSpoofedUUID(UUID uuid) {
        this.fabricproxylegacy$spoofedUUID = uuid;
    }

    @Override
    public Property[] fabricproxylegacy$getSpoofedProfile() {
        return this.fabricproxylegacy$spoofedProfile;
    }

    @Override
    public void fabricproxylegacy$setSpoofedProfile(Property[] profile) {
        this.fabricproxylegacy$spoofedProfile = profile;
    }
}
