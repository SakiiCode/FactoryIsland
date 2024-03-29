package ml.sakii.factoryisland.blocks;

import java.io.FileReader;
import java.io.IOException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class ModBlock extends Block {

    private Surface[] surfaces;
	
    @SuppressWarnings("unused")
	private Invocable invocable;
    private ScriptEngine engine;
    private final BlockDescriptor descriptor = new BlockDescriptor() {
    	@Override
    	public boolean isSolid() {
    		try {
    			return Boolean.parseBoolean(engine.eval("solid").toString());
    		} catch (ScriptException e) {
    			e.printStackTrace();
    			return true;
    		}
    	}
    	
    	@Override
    	public boolean isTransparent() {
    		try {
    			return Boolean.parseBoolean(engine.eval("transparent").toString());
    		} catch (ScriptException e) {
    			e.printStackTrace();
    			return false;
    		}
    	}
	};
    
	
	public ModBlock(String name, int x, int y, int z, GameEngine gengine) {
		super(x, y, z, gengine);
			    try {
			NashornScriptEngineFactory nashorn = new NashornScriptEngineFactory();
	    	engine = nashorn.getScriptEngine();
	    	if(engine == null) {
	    		throw new NullPointerException("Cannot retrieve script engine");
	    	}
	    	invocable = (Invocable) engine;
	    	
	    }catch(Exception | Error e) {
	    	e.printStackTrace();
	    	throw new NullPointerException("Nashorn is unavailable, mod support will be disabled. "+e.getClass().getSimpleName()+":"+e.getMessage());
	    }	    

		
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
        	this.name=name;
			surfaces = new Surface[] {top, bottom, north, south, east, west};
			generate(xscale, yscale, zscale);
		} catch (ScriptException e) {
			Main.err("Problem loading " + name + " textures: " + e.getMessage());
		}
		
		
	}
	
	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor;
	}
	
	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
/*
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
	public void work() {
		try {
			invocable.invokeFunction("work");
		} catch (NoSuchMethodException | ScriptException e) {
			Main.err(e.getMessage());
		}
		
	}

	private HashMap<BlockFace, Integer> signals = new HashMap<>();

	@Override
	public HashMap<BlockFace, Integer> getSignals() {
		return signals;
	}*/

}
