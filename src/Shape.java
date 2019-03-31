
public class Shape implements Comparable {
    public static final double THRESHOLD = 0.01;
	
	public enum ShapeType {
		UNKNOWN (0, 0), TRIANGLE (0, 0.7), SQUARE(0.7, 0.8), CIRCLE (0.8, 1);
		
		private double minShapeFactor;
		private double maxShapeFactor;
		
		private ShapeType (double minShapeFactor, double maxShapeFactor) {
			this.minShapeFactor = minShapeFactor;
			this.maxShapeFactor = maxShapeFactor;
		}
		
		public static ShapeType getShape (double area, double perimeter) {
			double factor = getShapeFactor (area, perimeter);
			for (ShapeType s : ShapeType.values()) {
				if (factor > s.minShapeFactor && factor <= s.maxShapeFactor) {
					return s;
				}
			}
			return UNKNOWN;
		}
		
		public static double getShapeFactor (double area, double perimeter) {
			return 4 * Math.PI * area / (perimeter * perimeter);
		}
	}
	
	private double area;
	private double perimeter;
	private int x;
	private int y;
	private ShapeType type;
	

	public Shape(double area, double perimeter) {
		this(area, perimeter, -1, -1);
	}
	
	public Shape(double area, double perimeter, int x, int y) {
		this.area = area;
		this.perimeter = perimeter;
		this.x = x;
		this.y = y;
		this.type = ShapeType.getShape(area, perimeter);
	}


	
	public double getArea() {
		return area;
	}

	public double getPerimeter() {
		return perimeter;
	}
	
	public ShapeType getType() {
		return type;
	}
	
	public boolean equals (Object s) {
		System.out.println(this + " equals " + s +  "?");
		if (!(s instanceof Shape)) {return false;}
		Shape shape = (Shape) s;
		return Math.abs(shape.perimeter - perimeter) < THRESHOLD && Math.abs(shape.area - area) < THRESHOLD &&
				shape.x == x && shape.y == y;
	}
	
	public String toString() {
		return type + " (P: " + perimeter + ", A: " + area + ", x: " + x + ", y: " + y +")";
	}
	
	public int compareTo(Object s) {
		Shape shape = (Shape) s;
		return 1000 * ((int)(area - shape.area + perimeter - shape.perimeter));// + ((x - shape.x) + (y - shape.y));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
