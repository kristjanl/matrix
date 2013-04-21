package personal.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class App {
	private static enum RC{
		Row,Col;
	}
	public static void main(String[] args) {
		RealMatrix matrix = new Array2DRowRealMatrix(new double[][]{
				{1,0,1,1,0},
				{0,1,0,0,0},
				{1,0,1,0,0},
				{0,1,0,0,0},
				{0,1,0,0,0}
		});
		RealMatrix matrix2 = new Array2DRowRealMatrix(new double[][]{{1,0,0},{0,0,0},{0,0,1}});
//		RealMatrix start = swapRow(swapCol(matrix, 0, 3), 1, 2);
		RealMatrix start = matrix;
		pprint(start);
		RealMatrix optimize = optimize(start);
		pprint(optimize);
		System.out.println(permutate(Arrays.asList(1,2,3,4)).size());
	}

	public static RealMatrix optimize(RealMatrix start) {
		RealMatrix optimizeCols = optimizeCols(start);
//		pprint(optimizeCols);
		RealMatrix optimize = optimizeRows(optimizeCols);
		return optimize;
	}
	
	private static RealMatrix optimizeRows(RealMatrix optimizeCols) {
		int rowDimension = optimizeCols.getRowDimension();
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < rowDimension; i++) {
			list.add(i);
		}
		int max = -1;
		List<Integer> maxPerm = null;
		for (List<Integer> permutation : permutate(list)) {
			RealMatrix temp = swapRow(optimizeCols, permutation);
			int value = calcValue(temp, RC.Col);
			if(value > max) {
				max = value;
				maxPerm = permutation;
			}
		}
		return swapRow(optimizeCols, maxPerm);
	}

	private static RealMatrix optimizeCols(RealMatrix matrix) {
		int columnDimension = matrix.getColumnDimension();
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < columnDimension; i++) {
			list.add(i);
		}
		int max = -1;
		List<Integer> maxPerm = null;
		for (List<Integer> permutation : permutate(list)) {
//			System.out.println(permutation);
			RealMatrix temp = swapCol(matrix, permutation);
			int value = calcValue(temp, RC.Row);
			if(value > max) {
				max = value;
				maxPerm = permutation;
			}
//			System.out.println(value);
		}
		return swapCol(matrix, maxPerm);
	}

	private static RealMatrix swapCol(RealMatrix matrix, List<Integer> permutation) {
		return swapRow(matrix.transpose(), permutation).transpose();
	}
	private static RealMatrix swapRow(RealMatrix matrix, List<Integer> permutation) {
		if(new HashSet<Integer>(permutation).size() != permutation.size())
			throw new RuntimeException();
		double[][] d = new double[matrix.getRowDimension()][];
//		pprint(matrix);
//		System.out.println(permutation);
		for (int i = 0; i < permutation.size(); i++) {
			Integer aft = permutation.get(i);
//			System.out.println("i: " + i);
//			System.out.println("aft: " + aft);
//			System.out.println(aft);
//			System.out.println(matrix.getRow(2));
//			System.out.println(d.length);
			d[i] = matrix.getRow(aft);
		}
		return new Array2DRowRealMatrix(d);
	}

	private static int calcValue(RealMatrix matrix, RC rc) {
		if(rc == RC.Row)
			return calcValue(matrix.transpose(), RC.Col);
		int value = 0;
		for(int col = 0, n = matrix.getColumnDimension(); col < n; col++) {
			double[] column = matrix.getColumn(col);
			for(int i = 0; i < column.length - 1; i++) {
				if(column[i] == column[i+1]) //also good if two whites are close
					value++;
			}
		}
		return value;
	}

	public static List<List<Integer>> permutate(final List<Integer> list) {
		if(list.size() == 1) {
			return new ArrayList<List<Integer>>(){{add(list);}};
		}
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		for(int i = 0; i < list.size(); i++) {
			ArrayList<Integer> copy = new ArrayList<Integer>(list);
			Integer elm = copy.get(i);
			copy.remove(i);
			List<List<Integer>> permutate = permutate(copy);
			for (List<Integer> list2 : permutate) {
				list2.add(0, elm);
				ret.add(list2);
			}
		}
		return ret;
	}

	private static RealMatrix swapRow(RealMatrix matrix, int i, int j) {
		RealMatrix transpose = matrix.transpose();
		return swapCol(transpose, i, j).transpose();
	}

	private static RealMatrix swapCol(RealMatrix matrix, int i, int j) {
		RealMatrix ret = matrix.copy();
		double[] first = ret.getColumn(i);
		double[] second = ret.getColumn(j);
		ret.setColumn(i, second);
		ret.setColumn(j, first);
		return ret;
	}

	public static void pprint(RealMatrix matrix) {
		System.out.println("------------");
		for(int row = 0, n = matrix.getRowDimension(); row < n; row++) {
			double[] r = matrix.getRow(row);
			for(int column = 0, cn = r.length; column < cn; column++) {
				System.out.print("|");
				if(r[column] > 0)
					System.out.print("#");
				else
					System.out.print(" ");
				System.out.print("|");
			}
			System.out.println();
		}
	}
}
