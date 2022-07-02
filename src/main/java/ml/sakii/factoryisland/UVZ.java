package ml.sakii.factoryisland;

import java.awt.Point;

public class UVZ {
	double iz, uz, vz, ao;
	
	static UVZ interp(Point p1, Point p2, Point pos, UVZ uvz1, UVZ uvz2) {
		UVZ result = new UVZ();
		double distanceratio = p1.distance(pos.x, pos.y) / p1.distance(p2.x, p2.y);
		result.iz = Util.interp(0, 1, distanceratio, uvz1.iz, uvz2.iz);
		result.uz = Util.interp(0, 1, distanceratio, uvz1.uz, uvz2.uz);
		result.vz = Util.interp(0, 1, distanceratio, uvz1.vz, uvz2.vz);
		result.ao = Util.interp(0, 1, distanceratio, uvz1.ao, uvz2.ao);
		return result;
	}
	
	@Override
	public String toString() {
		return "UVZ [invZ=" + iz + ", uz=" + uz + ", vz=" + vz + ", ao=" + ao + "]";
	}
	
	
}