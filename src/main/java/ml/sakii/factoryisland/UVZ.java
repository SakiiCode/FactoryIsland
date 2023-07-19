package ml.sakii.factoryisland;

import java.awt.Point;

public class UVZ {
	double iz, uz, vz, ao;
	
	static UVZ interp(Point p1, Point p2, Point pos, UVZ uvz1, UVZ uvz2, UVZ result) {
		double distanceratio = p1.distance(pos.x, pos.y) / p1.distance(p2.x, p2.y);
		result.iz = Util.interp(0, 1, distanceratio, uvz1.iz, uvz2.iz);
		result.uz = Util.interp(0, 1, distanceratio, uvz1.uz, uvz2.uz);
		result.vz = Util.interp(0, 1, distanceratio, uvz1.vz, uvz2.vz);
		result.ao = Util.interp(0, 1, distanceratio, uvz1.ao, uvz2.ao);
		return result;
	}
	
	static double[] interpUV(Vector p1, Vector pos, Vector p2, double[] uv1, double[] uv2) {
		double distanceratio = p1.distance(pos) / p1.distance(p2);
		double u = Util.interp(0, 1, distanceratio, uv1[0], uv2[0]);
		double v = Util.interp(0, 1, distanceratio, uv1[1], uv2[1]);
		double ao = Util.interp(0, 1, distanceratio, uv1[2], uv2[2]);
		return new double[] {u,v,ao};
	}
	
	@Override
	public String toString() {
		return "UVZ [invZ=" + iz + ", uz=" + uz + ", vz=" + vz + ", ao=" + ao + "]";
	}
	
	
}