package ml.sakii.factoryisland;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

public class TextureRenderThread extends ForkJoinTask<Integer> {

	private static final long serialVersionUID = -3786171665982151847L;

	// drawToBuffer parameters
	UVZ[] bufferUVZmin, bufferUVZmax;
	int[] bufferXmin, bufferXmax;
	UVZ tmpUVZ1, tmpUVZ2;
	Renderer renderer;
	Game game;
	
	
	List<Object3D> Objects;
	private ArrayList<Object3D> result = new ArrayList<>();
	private int visibleCount;
	

	private int threadIndex;
	private int threadCount;
	
	public TextureRenderThread(Game game, Renderer renderer, List<Object3D> Objects, int threadIndex, int threadCount) {

		this.game = game;
		this.threadIndex = threadIndex;
		this.threadCount = threadCount;
		this.Objects = Objects;
		this.renderer = renderer;
		this.tmpUVZ1 = new UVZ();
		this.tmpUVZ2 = new UVZ();
				
		resizeScreen();
	}

	@Override
	public Integer getRawResult() {
		return visibleCount;
	}

	@Override
	protected void setRawResult(Integer value) {
		this.visibleCount = value;
	}

	@Override
	protected boolean exec() {

		result.clear();
		visibleCount = 0;

		int minObjects = Objects.size() / threadCount;
		int remainder = Objects.size() % threadCount;
		
		int startIndex, currentObjects;
		if(threadIndex < remainder) {                // process the remainder as well, +1 object per thread
			currentObjects = minObjects+1;
			startIndex = (minObjects+1)*threadIndex;
		}else {                                      // only process minObjects amount, no remainder 
			currentObjects = minObjects;
			startIndex = (minObjects+1)*remainder+minObjects*(threadIndex-remainder);
		}
		
		for (int objectIndex = startIndex; objectIndex < startIndex+currentObjects; objectIndex++) {
			if (Objects.size() > objectIndex) {
				Object3D obj = Objects.get(objectIndex);
				if (obj instanceof BufferRenderable br) {
					br.drawToBuffer(this);

					if (obj instanceof Polygon3D p) {

						if (p.polygon.contains(game.centerX, game.centerY)
								&& ((renderer.SelectedPolygon == null && p.AvgDist < 5)
										|| (renderer.SelectedPolygon != null
												&& p.AvgDist < renderer.SelectedPolygon.AvgDist))) {
							renderer.SelectedPolygon = p;
						}
						visibleCount++;
					}
				}
			}
		}

		return true;
	}
	
	public void resizeScreen() {


		bufferUVZmin = new UVZ[Config.getHeight()];
		bufferUVZmax = new UVZ[Config.getHeight()];

		for (int i = 0; i < bufferUVZmin.length; i++) {
			bufferUVZmin[i] = new UVZ();
		}

		for (int i = 0; i < bufferUVZmax.length; i++) {
			bufferUVZmax[i] = new UVZ();
		}
		
		bufferXmin = new int[Config.getHeight()];
		bufferXmax = new int[Config.getHeight()];
	}

}
