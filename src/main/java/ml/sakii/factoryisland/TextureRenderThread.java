package ml.sakii.factoryisland;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinTask;

public class TextureRenderThread extends ForkJoinTask<Integer> {

	private static final long serialVersionUID = -3786171665982151847L;

	private UVZ[] bufferUVZmin;
	private UVZ[] bufferUVZmax;

	private Game game;
	private ArrayList<Object3D> Objects;
	private ArrayList<Object3D> result = new ArrayList<>();
	private Renderer renderer;
	private int visibleCount;
	
	private Vector tmpVector2;

	private int threadIndex;
	private int threadCount;
	private Vector[][] clip2;
	private double[][][] clipUV2;

	public TextureRenderThread(Game game, Renderer renderer, int threadIndex, int threadCount) {

		this.game = game;
		this.threadIndex = threadIndex;
		this.threadCount = threadCount;
		this.Objects = game.Objects;
		this.renderer = renderer;
		
		this.tmpVector2 = new Vector();

		clip2 = new Vector[6][8];
		clipUV2 = new double[6][8][3];

		for (int i = 0; i < clip2.length; i++) {
			for (int j = 0; j < clip2[0].length; j++) {
				clip2[i][j] = new Vector();
			}
		}
		
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
				if (obj.update(game, clip2, clipUV2, tmpVector2) && obj instanceof BufferRenderable br) {
					br.drawToBuffer(renderer.ZBuffer, game, bufferUVZmin, bufferUVZmax);

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
	}

}
