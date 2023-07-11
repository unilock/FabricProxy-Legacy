package one.oktw.interfaces;

import com.mojang.authlib.properties.Property;

import java.util.UUID;

public interface BungeeClientConnection {
    UUID fabricproxylegacy$getSpoofedUUID();

    void fabricproxylegacy$setSpoofedUUID(UUID uuid);

    Property[] fabricproxylegacy$getSpoofedProfile();

    void fabricproxylegacy$setSpoofedProfile(Property[] profile);
}
