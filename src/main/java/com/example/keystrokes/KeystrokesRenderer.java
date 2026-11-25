package com.example.keystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class KeystrokesRenderer {
	// Unscaled size of the whole overlay (keys + mouse)
	public static final int WIDTH = 162;
	public static final int HEIGHT = 102;

	private static final int K = 18;   // key size
	private static final int G = 3;    // gap

	// Colors (ARGB) - blue theme
	private static final int BORDER = 0xFF2FB4E8;
	private static final int IDLE = 0xB00A2E5C;
	private static final int PRESSED = 0xF07FE9FF;
	private static final int TEXT_IDLE = 0xFFFFFFFF;
	private static final int TEXT_PRESSED = 0xFF062447;

	private record Key(String label, int row, int col, int span, int code) {}

	private static final Key[] KEYS = {
		new Key("1", 0, 0, 1, GLFW.GLFW_KEY_1), new Key("2", 0, 1, 1, GLFW.GLFW_KEY_2),
		new Key("3", 0, 2, 1, GLFW.GLFW_KEY_3), new Key("4", 0, 3, 1, GLFW.GLFW_KEY_4),
		new Key("5", 0, 4, 1, GLFW.GLFW_KEY_5),

		new Key("Q", 1, 0, 1, GLFW.GLFW_KEY_Q), new Key("W", 1, 1, 1, GLFW.GLFW_KEY_W),
		new Key("E", 1, 2, 1, GLFW.GLFW_KEY_E), new Key("R", 1, 3, 1, GLFW.GLFW_KEY_R),
		new Key("T", 1, 4, 1, GLFW.GLFW_KEY_T),

		new Key("A", 2, 0, 1, GLFW.GLFW_KEY_A), new Key("S", 2, 1, 1, GLFW.GLFW_KEY_S),
		new Key("D", 2, 2, 1, GLFW.GLFW_KEY_D), new Key("F", 2, 3, 1, GLFW.GLFW_KEY_F),
		new Key("G", 2, 4, 1, GLFW.GLFW_KEY_G),

		new Key("Shift", 3, 0, 1, GLFW.GLFW_KEY_LEFT_SHIFT), new Key("Z", 3, 1, 1, GLFW.GLFW_KEY_Z),
		new Key("X", 3, 2, 1, GLFW.GLFW_KEY_X), new Key("C", 3, 3, 1, GLFW.GLFW_KEY_C),

		new Key("Ctrl", 4, 0, 1, GLFW.GLFW_KEY_LEFT_CONTROL),
		new Key("", 4, 1, 4, GLFW.GLFW_KEY_SPACE),
	};

	public static void render(DrawContext ctx) {
		MinecraftClient mc = MinecraftClient.getInstance();
		KeystrokesConfig cfg = KeystrokesConfig.get();
		MatrixStack m = ctx.getMatrices();
		long win = mc.getWindow().getHandle();
		boolean active = mc.currentScreen == null || mc.currentScreen instanceof EditScreen;

		m.push();
		m.translate(cfg.x, cfg.y, 0);
		m.scale(cfg.scale, cfg.scale, 1f);

		for (Key k : KEYS) {
			boolean down = active && InputUtil.isKeyPressed(win, k.code);
			int x = k.col * (K + G);
			int y = k.row * (K + G);
			int w = k.span * K + (k.span - 1) * G;
			box(ctx, x, y, w, K, down);
			if (!k.label.isEmpty()) label(ctx, mc.textRenderer, k.label, x, y, w, K, down);
		}

		// Mouse (right side)
		int mx = 5 * (K + G) + 7;
		boolean l = active && GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		boolean r = active && GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		boolean mid = active && GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
		box(ctx, mx, 0, 24, 36, l);
		box(ctx, mx + 26, 0, 24, 36, r);
		box(ctx, mx + 21, 6, 8, 16, mid);
		box(ctx, mx, 38, 50, 46, false);

		m.pop();
	}

	public static void box(DrawContext ctx, int x, int y, int w, int h, boolean pressed) {
		ctx.fill(x, y, x + w, y + h, BORDER);
		ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, pressed ? PRESSED : IDLE);
	}

	private static void label(DrawContext ctx, TextRenderer tr, String s, int x, int y, int w, int h, boolean down) {
		int tw = tr.getWidth(s);
		float sc = tw > w - 4 ? (w - 4f) / tw : 1f;
		MatrixStack m = ctx.getMatrices();
		m.push();
		m.translate(x + w / 2f, y + h / 2f - 4f * sc, 0);
		m.scale(sc, sc, 1f);
		ctx.drawText(tr, s, -tw / 2, 0, down ? TEXT_PRESSED : TEXT_IDLE, false);
		m.pop();
	}
}
