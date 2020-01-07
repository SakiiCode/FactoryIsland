package ml.sakii.factoryisland.blocks;

import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.items.ItemStack;

public class ModBlock extends PowerConsumer implements BreakListener, InteractListener, PlaceListener, TickListener, WorldGenListener{

    public Surface[] surfaces;
	
	ScriptEngineManager factory = new ScriptEngineManager();

    ScriptEngine engine = factory.getEngineByName("nashorn");
    
    Invocable invocable = (Invocable) engine;
	
	public ModBlock(String name, int x, int y, int z, GameEngine gengine) {
		super(x, y, z, gengine);

        try(FileReader fr = new FileReader("mods/"+name+"/mod.js")) {
			engine.eval(fr);
			fr.close();
		} catch (ScriptException | IOException e) {
			Main.err("Problem loading " + name + " mod: " + e.getMessage());
			return;
		}
        
        try {
        	engine.eval("x="+x+";y="+y+";z="+z);
        	Surface top = new Surface(engine.eval("top"));
        	Surface bottom = new Surface(engine.eval("bottom"));
        	Surface north = new Surface(engine.eval("north"));
        	Surface south = new Surface(engine.eval("south"));
        	Surface east = new Surface(engine.eval("east"));
        	Surface west = new Surface(engine.eval("west"));
        	surfaces= new Surface[]{top,bottom,north,south,east,west};
        	float xscale = Float.parseFloat(engine.eval("xscale").toString());
        	float yscale = Float.parseFloat(engine.eval("yscale").toString());
        	float zscale = Float.parseFloat(engine.eval("zscale").toString());
        	/*if(!Main.ModRegistry.contains(name)) {
        		Main.ModRegistry.add(name);
        	}*/
        	this.name=name;
        	this.solid=Boolean.parseBoolean(engine.eval("solid").toString());
        	this.transparent=Boolean.parseBoolean(engine.eval("transparent").toString());
			surfaces = new Surface[] {top, bottom, north, south, east, west};
			generate(xscale, yscale, zscale);
		} catch (ScriptException e) {
			Main.err("Problem loading " + name + " textures: " + e.getMessage());
			//e.printStackTrace();
		}
		
		
	}

	@Override
	public void generateWorld() {
		try {
			invocable.invokeFunction("generateWorld");
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
		}
	}
	
	@Override
	public boolean tick(long tickCount) {
		try {
			return (boolean) invocable.invokeFunction("tick", tickCount);
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
			return false;
		}
	}


	@Override
	public void placed(BlockFace SelectedFace) {
		try {
			invocable.invokeFunction("placed", SelectedFace.id);
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
		}

		
	}

	@Override
	public void interact(BlockFace target) {
		try {
			invocable.invokeFunction("interact", target.id);
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
		}
		
	}

	@Override
	public ItemStack[] breaked(String username) {
		try {
			if((boolean)invocable.invokeFunction("breaked")) {
				return null;
			}
			return new ItemStack[] {};
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
			return null;
		}
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

	@Override
	void work() {
		try {
			invocable.invokeFunction("work");
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
		}
		
	}


	
	
	
	

}
