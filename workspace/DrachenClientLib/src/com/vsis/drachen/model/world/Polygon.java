package com.vsis.drachen.model.world;

public class Polygon implements IShape {

	Point[] points;

	public Point[] getPoints() {
		return points;
	}

	/**
	 * 
	 * @param points
	 *            : ordered pointlist
	 */
	public Polygon(Point[] points) {
		assert points.length >= 3;

		this.points = points;
	}

	/**
	 * 
	 * @param points
	 *            : ordered pointlist
	 */
	public Polygon() {
		// assert points.length >= 3;

		this.points = null;
	}

	/* (non-Javadoc)
	 * @see com.vsis.drachen.model.world.IShape#Contains(com.vsis.drachen.model.world.Point)
	 */
	@Override
	public boolean Contains(Point q) {
		int count = 0;
		for (int i = 0; i < points.length; i++) {
			Point p1 = points[i];
			Point p2 = points[(i + 1) % points.length];

			if ((p2.getY() - p1.getY()) == 0.0)
				if ((q.getY() - p1.getY()) == 0.0) {
					if ((q.getX() >= p1.getX() && q.getX() <= p2.getX())
							|| (q.getX() >= p2.getX() && q.getX() <= p1.getX()))
						return true; // q is on line L
					else
						// if(q.getX() <= p1.getX() || q.getX() <= p2.getX())
						// lines overlap, don't count this
						// or don't overlap, don't count this either
						continue;
				} else
					continue; // no intersection (are parallel)
			else {
				double mu = (q.getY() - p1.getY()) / (p2.getY() - p1.getY());
				double lambda = p1.getX() - q.getX() + mu
						* (p2.getX() - p1.getX());
				if (lambda == 0.0)
					return true; // q is on line L
				if (mu == 0.0)
					continue; // Ray R goes through p1, it was count as p2 (mu
								// ==1) [if lambda > 0]
				if (mu >= 0 && mu <= 1 && lambda >= 0)
					count++;
			}

		}
		return count % 2 == 1;
	}

	/* (non-Javadoc)
	 * @see com.vsis.drachen.model.world.IShape#getCenter()
	 */
	@Override
	public Point getCenter() {
		int n = points.length;
		double b = points[n - 1].getX() * points[0].getY() //
				- points[0].getX() * points[n - 1].getY();
		double a = b; // area
		double x = (points[n - 1].getX() + points[0].getX()) * b;
		double y = (points[n - 1].getY() + points[0].getY()) * b;
		for (int i = 1; i < n; i++) {
			Point pi = points[i - 1];
			Point pj = points[i];
			b = pi.getX() * pj.getY() //
					- pj.getX() * pi.getY();
			x += (pi.getX() + pj.getX()) * b;
			y += (pi.getY() + pj.getY()) * b;
			a += b;
		}
		a /= 2;
		return new Point(x / (6 * a), y / (6 * a));
	}

	/* (non-Javadoc)
	 * @see com.vsis.drachen.model.world.IShape#getArea()
	 */
	@Override
	public double getArea() {
		int n = points.length;
		double a = points[n - 1].getX() * points[0].getY() //
				- points[0].getX() * points[n - 1].getY();
		for (int i = 1; i < n; i++) {
			Point pi = points[i - 1];
			Point pj = points[i];
			a += pi.getX() * pj.getY() - pj.getX() * pi.getY();
		}
		return a / 2;
	}

	// /**
	// * tests intersection of: L_1: q + lambda * e_1 lambda >= 0 L_2: p_1 + mu
	// * *(p2_2 - p_1) mu in [0, 1]
	// *
	// *
	// * @param p1
	// * @param p2
	// * @param q
	// * @return
	// */
	// private boolean intersectLine(Point p1, Point p2, Point q) {
	// double mu = (q.getY() - p1.getY()) / (p2.getY() - p1.getY());
	// if (mu < 0 || mu > 1)
	// return false;
	// double lambda = p1.getX() - q.getX() + mu * (p2.getX() - p1.getX());
	// return lambda >= 0;
	// }

}
