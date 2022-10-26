package com.adryd.cauldronHijack.mixin.keybind;

import com.adryd.cauldronHijack.Jason;
import com.adryd.cauldronHijack.KeybindInternals;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions {
	@Redirect(
			method = "accept",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/option/KeyBinding;getBoundKeyTranslationKey()Ljava/lang/String;"
					),
					to = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/option/KeyBinding;setBoundKey(Lnet/minecraft/client/util/InputUtil$Key;)V"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"
			)
	)
	private boolean equals(String instance, Object comparison) {
		return false;
	}

	@Redirect(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setBoundKey(Lnet/minecraft/client/util/InputUtil$Key;)V"))
	private void loadKeybindsOwnConfig(KeyBinding keyBinding, InputUtil.Key code) {
		if (KeybindInternals.root.has(keyBinding.getTranslationKey())) {
			keyBinding.setBoundKey(InputUtil.fromTranslationKey(KeybindInternals.root.get(keyBinding.getTranslationKey()).getAsString()));
		} else {
			KeybindInternals.root.addProperty(keyBinding.getTranslationKey(), code.getTranslationKey());
			keyBinding.setBoundKey(code);
		}
	}

	@Inject(method = "setKeyCode", at = @At(value = "HEAD"))
	private void setKeyInConfig(KeyBinding keyBinding, InputUtil.Key code, CallbackInfo ci) {
		KeybindInternals.root.addProperty(keyBinding.getTranslationKey(), code.getTranslationKey());
		Jason.write();
	}
}
