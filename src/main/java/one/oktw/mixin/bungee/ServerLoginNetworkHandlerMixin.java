package one.oktw.mixin.bungee;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.interfaces.BungeeClientConnection;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    @Nullable
    GameProfile profile;

    /**
     * initUuid
     */
    @Inject(method = "onHello", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;", shift = At.Shift.AFTER))
    private void onHello_ServerLoginNetworkHandler$profile(CallbackInfo ci) {
        // override game profile with saved information
        this.profile = new GameProfile(((BungeeClientConnection) connection).fabricproxylegacy$getSpoofedUUID(), this.profile.getName());

        if (((BungeeClientConnection) connection).fabricproxylegacy$getSpoofedProfile() != null) {
            for (Property property : ((BungeeClientConnection) connection).fabricproxylegacy$getSpoofedProfile()) {
                this.profile.getProperties().put(property.getName(), property);
            }
        }
    }
}
