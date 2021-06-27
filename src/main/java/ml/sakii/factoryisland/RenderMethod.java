package ml.sakii.factoryisland;

public enum RenderMethod {
BUFFERED(0),VOLATILE(1),DIRECT(2);
public int id;
private RenderMethod(int id) {
	this.id=id;
}
}
