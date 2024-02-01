package ml.sakii.factoryisland;

public class Globals {
	public static final int MP_PACKET_EACH = 50;
	public static final int TICKSPEED = 20; // 20 tick every second
	public static final int ENTITYSYNCRATE = 3; //every 3 ticks (=0.15s)
	public static final int AUTOSAVE_INTERVAL = 60*TICKSPEED; //every 3 ticks (=0.15s)
	public static final float GravityAcceleration=9.81f;
	public static final float JumpForce = 7f;
	public static final float WALK_SPEED = 4.8f;
	public static final float FLY_SPEED = 8f;

	public static final int MAXLIGHT=10;
	public static final int DEFAULT_PORT = 1420;
	
	public static final String CONTROLS_TEXT = """
			<html><body><center>Controls</center><ul>
			<li>WASD - move</li>
			<li>Mouse Move - look around</li>
			<li>Left Click - break block / attack</li>
			<li>Right Click - place block</li>
			<li>Space - jump / fly up</li>
			<li>Shift - fly down</li>
			<li>Ctrl - toggle fly</li>
			<li>Escape - pause</li>
			<li>Shift + Right Click - interact</li>
			<li>Q - switch cursor between inventories</li>
			<li>Middle Mouse - swap 1 item between inventories</li>
			<li>Mouse Scroll - select item in inventory</li>
			<li>F1 - hide HUD</li>
			<li>F2 - take screenshot</li>
			<li>F3 - debug info</li>
			</ul>""";
	
	public static final long TICKS_PER_DAY = 72000;


}
