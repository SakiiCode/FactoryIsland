package ml.sakii.factoryisland;

import java.awt.Point;

public class UVZ {
	double iz, uz, vz;
	public UVZ(double Zinv,double  uz,double  vz) {
		this.iz = Zinv;
		this.uz = uz;
		this.vz = vz;
	}
	
	public UVZ() {
		
	}
	
	/*// a három változót távolság alapján interpoláljuk
	public static UVZ interp(Point p1, Point p2, Point pos, UVZ uvz1, UVZ uvz2) {
		UVZ result = new UVZ();
		double distanceratio = p1.distance(pos.x, pos.y) / p1.distance(p2.x, p2.y);
		//Main.log("distanceratio_jp:"+distanceratio);
		/*result.iz = Util.interp(0, 1, distanceratio, uvz1.iz, uvz2.iz);
		result.uz = Util.interp(0, 1, distanceratio, uvz1.uz, uvz2.uz);
		result.vz = Util.interp(0, 1, distanceratio, uvz1.vz, uvz2.vz);*/
		/*double izm = uvz2.iz-uvz1.iz;
		result.iz = Util.interpSlope(0, distanceratio, uvz1.iz, izm);
		double uzm = uvz2.uz-uvz1.uz;
		result.uz = Util.interpSlope(0, distanceratio, uvz1.uz, uzm);
		double vzm = uvz2.vz-uvz1.vz;
		result.vz = Util.interpSlope(0, distanceratio, uvz1.vz, vzm);
		/**/
		//return result;
	/*}*/

	
	public static UVZ interp(Point p1, Point p2, Point pos, UVZ uvz1, UVZ uvz2) {
		UVZ result = new UVZ();
		double distanceratio = p1.distance(pos.x, pos.y) / p1.distance(p2.x, p2.y);
		result.iz = Util.interp(0, 1, distanceratio, uvz1.iz, uvz2.iz);
		result.uz = Util.interp(0, 1, distanceratio, uvz1.uz, uvz2.uz);
		result.vz = Util.interp(0, 1, distanceratio, uvz1.vz, uvz2.vz);
				
		return result;
	}
	
	@Override
	public String toString() {
		return "UVZ [invZ=" + iz + ", uz=" + uz + ", vz=" + vz + "]";
	}
	
	
	public static UVZ interpSlope(double ratio, UVZ uvzmin, double izm, double uzm, double vzm) {
		
		double iz = Util.interpSlope(0, ratio, uvzmin.iz, izm);
		double uz = Util.interpSlope(0, ratio, uvzmin.uz, uzm);
		double vz = Util.interpSlope(0, ratio, uvzmin.vz, vzm);
		return new UVZ(iz, uz, vz);
	}
	
	
}