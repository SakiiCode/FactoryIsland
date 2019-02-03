package ml.sakii.factoryisland.blocks;

import java.awt.geom.Point2D;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;

public class GradientCalculator {


	
	static Point2D[] getGradientOf(int x, int y, int z, BlockFace nearby, BlockFace target){
		
		float[][] values = calculate(nearby, target);
		
		float[] begin1 = values[0];
		float[] begin = values[1];
		float[] end = values[2];
		
		 Point2D begin1p = Main.GAME.convert3Dto2D(new Vector(x+begin1[0], y+begin1[1], z+begin1[2]));
		 Point2D beginp = Main.GAME.convert3Dto2D(new Vector(x+begin[0], y+begin[1], z+begin[2]));
		 Point2D endp= Main.GAME.convert3Dto2D(new Vector(x+end[0], y+end[1], z+end[2]));
		
		return new Point2D[]{begin1p, beginp, endp};
		
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
	
	private static float[][] calculate(BlockFace nearby, BlockFace target){
		//if(nearby.id / 2 == 2){
		//	nearby = nearby.getOpposite();
		//}
		/*if(target.id / 2 == 2){
			target = target.getOpposite();
		}*/
		
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
	
	static Point2D getPerpendicular(Point2D p1, Point2D p2, Point2D interceptPt, double d) {
	    double xdiff = p1.getX() - p2.getX();
	    double ydiff = p1.getY() - p2.getY();
	    double atan = Math.atan2(ydiff, xdiff);
	    atan -= Math.PI/2;
	    double x = interceptPt.getX() + Math.cos(atan) * d;
	    double y = interceptPt.getY() + Math.sin(atan) * d;
	    return new Point2D.Double(x, y);
	}
	
	
	

	
}
