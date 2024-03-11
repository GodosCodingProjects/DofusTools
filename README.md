
# Dofus Tools

Dofus tools is a Java project containing a GUI app to help organize crafting, buying and selling of items in a MMORPG called Dofus.

This project uses a couple of external dependencies like the [Tess4J library](https://github.com/nguyenq/tess4j) for image recognition (analyzes what the game displays on screen to gather price information). It also uses the [JNativeHook library](https://github.com/kwhat/jnativehook) to synchronize the internal state of the application to the player key presses. For its GUI, this project relies on the [Java Swing library](https://docs.oracle.com/javase/tutorial/uiswing/index.html). Finally, it uses [FlatLaf](https://github.com/JFormDesigner/FlatLaf) look-and-feel for default appearance of the GUI.