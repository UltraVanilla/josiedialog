package josie.dialog.fabric.mixin;

import josie.dialog.fabric.PlatformFabric;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class CustomClickActionMixin {
    @Inject(method = "handleCustomClickAction", at = @At("HEAD"))
    private void handleCustomClickAction(final ServerboundCustomClickActionPacket packet, final CallbackInfo ci) {
        final var profile = ((ServerCommonPacketListenerImpl) (Object) this).getOwner();

        PlatformFabric.receiveClickAction(
                profile.getId(), packet.id().toString(), packet.payload().get());
    }
}
