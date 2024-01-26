package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.BreakComponent;
import ml.sakii.factoryisland.blocks.components.SignalPropagatorComponent;
import ml.sakii.factoryisland.blocks.components.TickUpdateComponent;
import ml.sakii.factoryisland.blocks.components.WorldLoadComponent;
import ml.sakii.factoryisland.items.ItemStack;

public class WoodBlock extends Block{
	
	SignalPropagatorComponent spc;

	public WoodBlock(int x, int y, int z, GameEngine engine) {
		super("Wood", x, y, z,engine);
		spc = new SignalPropagatorComponent(this) {
			@Override
			public void onSignalUpdate() {
				Main.log("Wood signal update");
				recalcPaints();
			}
		};
		addComponent(spc);
		
		addComponent(new WorldLoadComponent(this) {
			@Override
			public void onLoad(Game game) {
				recalcPaints();
			}
		});
		
		addComponent(new TickUpdateComponent(this,1) {
			
			@Override
			public boolean onTick() {
				for(Map.Entry<BlockFace, Integer> entry : spc.signals.entrySet()) {
					spc.spreadSignal(entry.getValue(), entry.getKey());
				}
				return false;
			}
		});
		
		addComponent(new BreakComponent(this) {
			@Override
			public List<ItemStack> onBreak() {
				for(Map.Entry<BlockFace, Block> entry : WoodBlock.this.Engine.world.get6Blocks(WoodBlock.this,false).entrySet()) {
					for(SignalPropagatorComponent spc : entry.getValue().getComponents(SignalPropagatorComponent.class)) {
						spc.spreadSignal(0, entry.getKey().getOpposite());
					}
				}
				ArrayList<ItemStack> result = new ArrayList<>();
				result.add(new ItemStack(Main.Items.get("Wood"),1));
				return result;
			}
		});
	}


	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(AssetLibrary.wood.Texture,Color4.TRANSPARENT));
	}
	
	private void recalcPaints() {
		int charge = spc.getCharge();

		for(Object3D obj : Objects){
			if(obj instanceof Polygon3D p) {
				if(charge == 0){
					p.s.paint = false;
				}else{
					p.s.p = new Color(AssetLibrary.fire.c.getRed()/255f,
							AssetLibrary.fire.c.getGreen()/255f,
							AssetLibrary.fire.c.getBlue()/255f,
							Math.max(0,Math.min(10, charge))/10f);
					p.s.paint = true;
				}
			}
		}
	}
	
}
