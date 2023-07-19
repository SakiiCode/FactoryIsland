package ml.sakii.factoryisland;

public interface BufferRenderable {

	public void drawToBuffer(PixelData[][] ZBuffer, Game game, UVZ[] bufferUVZmin, UVZ[] bufferUVZmax);

}
