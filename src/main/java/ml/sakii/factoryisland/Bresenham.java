package ml.sakii.factoryisland;

public class Bresenham {

	private static void plotLineLow(int x0, int y0, int x1, int y1, PixelData[][] ZBuffer, double depth1,  double depth2) {
		//System.out.println("plotlinelow "+x0+","+y0+","+x1+","+y1);
	    int dx = x1 - x0;
	    int dy = y1 - y0;
	    int yi = 1;
	    if (dy < 0) {
	        yi = -1;
	        dy = -dy;
	    }
	    int D = (2 * dy) - dx;
	    int y = y0;
	
	    for(int x=x0;x<=x1;x++) {
	        plot(x, y, ZBuffer, Util.interp(x0, x1, x, depth1, depth2));
	        if(D > 0) {
	            y = y + yi;
	            D = D + (2 * (dy - dx));
	        }else {
	            D = D + 2*dy;
	        }
	
		}
	}
	
	private static void plotLineHigh(int x0, int y0, int x1, int y1, PixelData[][] ZBuffer, double depth1, double depth2) {
		//System.out.println("plotlinehigh "+x0+","+y0+","+x1+","+y1);
	    int dx = x1 - x0;
	    int dy = y1 - y0;
	    int xi = 1;
	    if(dx < 0) {
	        xi = -1;
	        dx = -dx;
	    }
	    
	    int D = (2 * dx) - dy;
	    int x = x0;
	
	    for(int y=y0;y<=y1;y++) {
	        plot(x, y, ZBuffer, Util.interp(y0, y1, y, depth1, depth2));
	        if(D > 0) {
	            x = x + xi;
	            D = D + (2 * (dx - dy));
	        }else {
	            D = D + 2*dx;
	        }
	    }
	}
	
	public static void plotLine(int x0, int y0, int x1, int y1, PixelData[][] ZBuffer, double depth1, double depth2) {
	    if(Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
	        if(x0 > x1) {
	            plotLineLow(x1, y1, x0, y0, ZBuffer, depth1, depth2);
		    }else {
	            plotLineLow(x0, y0, x1, y1, ZBuffer, depth1, depth2);
		    }
		}else {
	        if(y0 > y1) {
	            plotLineHigh(x1, y1, x0, y0, ZBuffer, depth1, depth2);
	        }else {
	            plotLineHigh(x0, y0, x1, y1, ZBuffer, depth1, depth2);
	        }
		}
	}
	
	private static void plot(int x, int y, PixelData[][] ZBuffer, double depth) {
		if(x<0 || x>=ZBuffer.length || y < 0 || y>=ZBuffer[0].length) return;
		synchronized(ZBuffer[x][y]) {
			depth = 1/(1/depth-0.1);
			if(ZBuffer[x][y].depth<=depth) {
				ZBuffer[x][y].color=0xFF000000;
				ZBuffer[x][y].depth=depth;
			}
		}
	}
}
