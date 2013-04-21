package personal.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;


public class MatrixReader {
	public static final String FS = System.getProperty("file.separator");
	public static void main(String[] args) throws IOException {
		File f = new File(String.format("dat%sMatrices", FS));
		TreeSet<NamedMatrix> set = new TreeSet<NamedMatrix>(new Comparator<NamedMatrix>() {
			@Override
			public int compare(NamedMatrix o1, NamedMatrix o2) {
				int first = Math.max(o1.rows(), o1.cols());
				int second = Math.max(o2.rows(), o2.cols());
				if(first != second)
					return first - second;
				return o1.name.compareTo(o2.name);
			}
		});
		for (String matrixName : f.list()) {
			File matrixF = new File(String.format("%s%s%s",
					f.getAbsolutePath(), FS, matrixName));
			NamedMatrix matrix = getMatrix(matrixF);
			set.add(matrix);
		}
		int counter = 0;
		for (NamedMatrix matrix : set) {
			System.out.println(matrix.name);
			System.out.printf("%sx%s%n", matrix.rows(), matrix.cols());
			App.pprint(matrix.matrix);
			RealMatrix optimized = App.optimize(matrix.matrix);
			App.pprint(optimized);
			if(++counter > 3)
				break;
		}
		System.out.println("yatta");
	}
	private static NamedMatrix getMatrix(File matrixF) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(matrixF));
		String line = null;
		List<double[]> acc = new ArrayList<double[]>();
		while((line = br.readLine()) != null) {
//			System.out.println(line);
			double[] row = new double[line.length()];
			for(int i = 0, n = line.length(); i < n; i++) {
				char c = line.charAt(i);
				if(c == '0')
					row[i] = 0;
				else if(c == '1')
					row[i] = 1;
				else
					throw new RuntimeException(String.format("bad format '%s'", c));
			}
			acc.add(row);
		}
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(acc.toArray(new double[0][]));
		return new NamedMatrix(matrixF.getName(), matrix);
	}
	public static class NamedMatrix {
		public final String name;
		public final RealMatrix matrix;
		public NamedMatrix(String name, RealMatrix matrix) {
			super();
			this.name = name;
			this.matrix = matrix;
		}
		public void print() {
			System.out.printf("NamedMatrix [name=%s, matrix=%n", name);
			App.pprint(matrix);
		}
		public int cols() {
			return matrix.getColumnDimension();
		}
		public int rows() {
			return matrix.getRowDimension();
		}
	}
}
