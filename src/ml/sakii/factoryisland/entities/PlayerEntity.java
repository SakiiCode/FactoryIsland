package ml.sakii.factoryisland.entities;

import java.util.Random;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;

public class PlayerEntity extends Entity
{

	public PlayerEntity(GameEngine engine) {
		super("PlayerEntity",new Vector(19.5f, 19.5f, 15.0f), new EAngle(-135, 0), "",new Random().nextLong(), engine, Surface.EMPTY, Surface.EMPTY);
	}
}
