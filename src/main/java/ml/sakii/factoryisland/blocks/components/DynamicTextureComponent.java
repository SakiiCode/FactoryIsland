package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.blocks.Block;

public abstract class DynamicTextureComponent extends Component {

	public DynamicTextureComponent(Block block) {
		super(block);
		SubComponents.add(new WorldLoadComponent(block) {
			@Override
			public void onLoad(Game game) {
				DynamicTextureComponent.this.updateTexture(new Vector(), game);
			}
		});
	}
	
	public abstract void updateTexture(Vector tmp, Game game);

}
