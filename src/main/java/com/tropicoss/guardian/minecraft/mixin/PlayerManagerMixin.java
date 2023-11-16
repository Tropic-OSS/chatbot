package com.tropicoss.guardian.minecraft.mixin;

import com.tropicoss.guardian.events.EventHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Unique
    private final EventHandler eventHandler = new EventHandler();
  // Called when the server sends a message (player join/leave, death messages, advancements)
  @Inject(
      at = @At("HEAD"),
      method = "broadcast(Lnet/minecraft/text/Text;Z)V")
  private void broadcast(Text message, boolean overlay, CallbackInfo ci) {
    MinecraftServer server = ((PlayerManager) (Object) this).getServer();
    eventHandler.onServerChat(server, message);
  }

  // Called when a player sends a chat message
  @Inject(
      at = @At("HEAD"),
      method =
              "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V")
  private void broadcast(
      SignedMessage message,
      ServerPlayerEntity sender,
      MessageType.Parameters params,
      CallbackInfo ci) {
    MinecraftServer server = ((PlayerManager) (Object) this).getServer();
    eventHandler.onPlayerChat(server, message.getContent(), sender);
  }
}