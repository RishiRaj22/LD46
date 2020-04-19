package me.rishiraj.notnpc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.rishiraj.notnpc.DisplayConstants;
import me.rishiraj.notnpc.LDGame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height += 2 * DisplayConstants.PADDING;
		config.width += 2 * DisplayConstants.PADDING;
		new LwjglApplication(new LDGame(), config);
	}
}
