package ml.sakii.factoryisland;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinTask;

public class ObjectUpdateThread extends ForkJoinTask<ArrayList<Object3D>> {

	private static final long serialVersionUID = -4446511856579332836L;
	private Game game;
	private ArrayList<Object3D> Objects;
	private int threadIndex;
	private int threadCount;
	ArrayList<Object3D> result = new ArrayList<>();
	private Vector[][] clip2;
	private double[][][] clipUV2;

	public ObjectUpdateThread(Game game, int threadIndex, int threadCount) {
		this.game = game;
		this.threadIndex = threadIndex;
		this.threadCount = threadCount;
		this.Objects = game.Objects;
		
		clip2 = new Vector[6][8];
		clipUV2=new double[6][8][3];

		for(int i=0;i<clip2.length;i++) {
			for(int j=0;j<clip2[0].length;j++) {
				clip2[i][j]=new Vector();
			}
		}
	}

	@Override
	public ArrayList<Object3D> getRawResult() {
		return result;
	}

	@Override
	protected void setRawResult(ArrayList<Object3D> value) {
		this.result = value;

	}

	@Override
	protected boolean exec() {
		result.clear();
		int objectsPerThread = Objects.size() / threadCount+1;
		for (int j = 0; j < objectsPerThread; j++) {
			int objectIndex = threadIndex * objectsPerThread + j;
			if(Objects.size() > objectIndex) {
				Object3D obj = Objects.get(objectIndex); 
				if (obj.update(game, clip2, clipUV2)) {
					result.add(obj);
				}
			}
		}
		return true;
	}

}
