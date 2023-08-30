package ml.sakii.factoryisland;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinTask;

public class ObjectUpdateThread extends ForkJoinTask<ArrayList<Object3D>> {

	private static final long serialVersionUID = -4446511856579332836L;
	private ArrayList<Object3D> Objects;
	private int threadIndex;
	private int threadCount;
	private UpdateContext context;
	private ArrayList<Object3D> result = new ArrayList<>();
	

	public ObjectUpdateThread(Game game, int threadIndex, int threadCount) {
		this.threadIndex = threadIndex;
		this.threadCount = threadCount;
		this.Objects = game.Objects;
		this.context = new UpdateContext(game);
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
				if (obj.update(context)) {
					result.add(obj);
				}
			}
		}
		return true;
	}

}
