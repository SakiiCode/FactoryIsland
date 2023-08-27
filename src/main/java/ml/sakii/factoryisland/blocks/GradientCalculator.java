package ml.sakii.factoryisland.blocks;

import java.awt.geom.Point2D;

import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vector2D;

public class GradientCalculator {
	
	public static Point2D.Float[] getGradientOf(int x, int y, int z, BlockFace nearby, BlockFace target, Game game){
		
		float[][] values = calculate(nearby, target);
		
		float[] begin1 = values[0];
		float[] begin = values[1];
		float[] end = values[2];
		
		Vector[] input = {
				new Vector(x+begin1[0], y+begin1[1], z+begin1[2]),
				new Vector(x+begin[0], y+begin[1], z+begin[2]),
				new Vector(x+end[0], y+end[1], z+end[2])};
		
		Point2D.Float[] output = new Point2D.Float[] {
				new Point2D.Float(),
				new Point2D.Float(),
				new Point2D.Float()};
		
		return game.convert3Dto2D(input, output, 3);
		
	}
	
	private static int calcRemaining(BlockFace nearby, BlockFace target){
		boolean azonos = (nearby.id % 2 == target.id % 2);
		if(nearby.id > target.id && (nearby.id<4 || target.id>1)){
			if(azonos){
				return 1;
			}
			return 0;
		}else if(nearby.id > target.id && (nearby.id >=4 && target.id <= 1)){
			if(azonos){
				return 0;
			}
			return 1;
		}else if(nearby.id<target.id && (target.id<4 || nearby.id>1)){
			if(azonos){
				return 0;
			}
			return 1;
		}else{
			if(azonos){
				return 1;
			}
			return 0;
		}
		
	}
	
	public static float[][] calculate(BlockFace nearby, BlockFace target){
		
		
		int begin1x = 0;

		if(nearby.direction[0] != 0){
			begin1x = Math.max(0, nearby.direction[0]);
		}else if(target.direction[0] !=0){
			begin1x = Math.max(0, target.direction[0]);
		}else{
			begin1x = calcRemaining(nearby, target);
		}
		
		
		
		
		int begin1y = 0;
		if(nearby.direction[1] != 0){
			begin1y = Math.max(0, nearby.direction[1]);
		}else if(target.direction[1] !=0){
			begin1y = Math.max(0, target.direction[1]);
		}else{
			begin1y = calcRemaining(nearby, target);
		}
		
		int begin1z = 0;
		if(nearby.direction[2] != 0){
			begin1z = Math.max(0, nearby.direction[2]);
		}else if(target.direction[2] !=0){
			begin1z = Math.max(0, target.direction[2]);
		}else{
			begin1z = calcRemaining(nearby, target);
		}
		
		int beginx2=5;
		if(target.direction[0] != 0){
			beginx2 = Math.max(0, target.direction[0]);
		}else if(nearby.direction[0] !=0){
			beginx2 = Math.max(0, nearby.direction[0]);
		}
		
		int beginy2=5;
		if(target.direction[1] != 0){
			beginy2 = Math.max(0, target.direction[1]);
		}else if(nearby.direction[1] !=0){
			beginy2 = Math.max(0, nearby.direction[1]);
		}
		
		int beginz2=5;
		if(target.direction[2] != 0){
			beginz2 = Math.max(0, target.direction[2]);
		}else if(nearby.direction[2] !=0){
			beginz2 = Math.max(0, nearby.direction[2]);
		}
		
		float endx=0;
		if(beginx2 == 5){
			endx=0.5f;
		}else if(nearby.direction[0] != 0){
			endx = Math.max(0, nearby.direction[0]);
		}else{
			endx = (beginx2 == 0 ? 1 : 0);
		}
		
		float endy=0;
		if(beginy2 == 5){
			endy=0.5f;
		}else if(nearby.direction[1] != 0){
			endy = Math.max(0, nearby.direction[1]);
		}else{
			endy = (beginy2 == 0 ? 1 : 0);
		}
		
		float endz=0;
		if(beginz2 == 5){
			endz=0.5f;
		}else if(nearby.direction[2] != 0){
			endz = Math.max(0, nearby.direction[2]);
		}else{
			endz = (beginz2 == 0 ? 1 : 0);
		}
		
		float beginx=beginx2;
		if(beginx2 ==5){
			beginx=0.5f;
		}
		
		float beginy=beginy2;
		if(beginy2 ==5){
			beginy=0.5f;
		}
		
		float beginz=beginz2;
		if(beginz2 ==5){
			beginz=0.5f;
		}
		
		float[] begin1 = new float[]{begin1x, begin1y, begin1z};
		float[] begin = new float[]{beginx, beginy, beginz};
		float[] end = new float[]{endx, endy, endz};
		
		return new float[][]{begin1, begin, end};
		
		
	}
	
	public static void getPerpendicular(Point2D.Float[] values, Vector2D pointVec, Vector2D lineVec) {
		Point2D.Float begin1 = values[0];
		Point2D.Float begin2 = values[1];
		Point2D.Float end = values[2];
		pointVec.set(end).substract(begin2);
		lineVec.set(begin1)
			.substract(begin2)
			.normalize()
			.multiply(lineVec.DotProduct(pointVec))
			.add(begin2);
	}
	
	

	
}
