package ml.sakii.factoryisland;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinTask;

public class TextureRenderThread extends ForkJoinTask<Integer> {

	private static final long serialVersionUID = -3786171665982151847L;

	// drawToBuffer parameters
	UVZ[] bufferUVZmin, bufferUVZmax;
	int[] bufferXmin, bufferXmax;
	UVZ tmpUVZ1, tmpUVZ2;
	Vector tmpVector;
	Renderer renderer;
	Game game;
	
	
	private ArrayList<Object3D> Objects;
	private ArrayList<Object3D> result = new ArrayList<>();
	private int visibleCount;
	

	private int threadIndex;
	private int threadCount;
	
	private UpdateContext context;

	public TextureRenderThread(Game game, Renderer renderer, int threadIndex, int threadCount) {

		this.game = game;
		this.threadIndex = threadIndex;
		this.threadCount = threadCount;
		this.Objects = game.Objects;
		this.renderer = renderer;
		this.tmpUVZ1 = new UVZ();
		this.tmpUVZ2 = new UVZ();
		
		this.tmpVector = new Vector();
		
		this.context = new UpdateContext(game);
		
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

		int objectsPerThread = Objects.size() / threadCount + 1;
		for (int j = 0; j < objectsPerThread; j++) {
			int objectIndex = threadIndex * objectsPerThread + j;
			if (Objects.size() > objectIndex) {
				Object3D obj = Objects.get(objectIndex);
				if (obj.update(context) && obj instanceof BufferRenderable br) {
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
