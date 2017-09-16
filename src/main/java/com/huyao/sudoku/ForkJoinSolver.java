package com.huyao.sudoku;

import com.huyao.sudoku.model.Plane;
import com.huyao.sudoku.model.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinSolver extends SudokuSolver{

	/* (non-Javadoc)
	 * @see com.hy.sudoku.solver.core.SudokuSolver#solve(com.hy.sudoku.solver.core.model.Plane)
	 */
	@Override
	public Map<String, Plane> solve(Plane plane) {
		RecursiveSolver.getResultMap().clear();
		prepareCandidate(plane);
		Plane p = new Plane();
		ForkJoinPool pool = new ForkJoinPool();
		RecursiveSolver rs = new RecursiveSolver(p, 0, 0,  this._candiMatrix);
		RecursiveSolver.getResultMap().clear();
		pool.invoke(rs);
		pool.shutdown();
		return RecursiveSolver.getResultMap();
	}
	
	private static class RecursiveSolver extends RecursiveAction{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3834345707764422319L;
		private static final ConcurrentHashMap<String, Plane> resultMap = new ConcurrentHashMap<>();

		/**
		 * @return the resultMap
		 */
		public static ConcurrentHashMap<String, Plane> getResultMap() {
			return RecursiveSolver.resultMap;
		}
		private Plane p;
		private int tx;
		private int ty;
		private List<int[]>[][] candiMatrix;
		
		public RecursiveSolver(Plane p, int tx, int ty,
				List<int[]>[][] candiMatrix){
			this.p = p;
			this.tx = tx;
			this.ty = ty;
			this.candiMatrix = candiMatrix;
		}
		
		@Override
		protected void compute() {
			List<int[]> candiList = this.candiMatrix[this.tx][this.ty];
			List<RecursiveAction> forks =
	                new ArrayList<>();
			for (int[] candi : candiList) {
				if(Validator.validateHor3Grid(candi, this.p.get((this.tx+1)%3, this.ty), this.p.get((this.tx+2)%3, this.ty))
						&& Validator.validateVer3Grid(candi, this.p.get(this.tx,(this.ty+1)%3), this.p.get(this.tx, (this.ty+2)%3))){
					Plane newPlane = this.p.clone();
					newPlane.set(this.tx, this.ty, candi);
					if(newPlane.getGridCount() == 9 ){
						RecursiveSolver.resultMap.put(String.valueOf(newPlane.computeHash()), newPlane);
						return;
					}
					if (this.ty - 1 >= 0 && newPlane.get(this.tx, this.ty - 1) == null) {
						RecursiveSolver fs = new RecursiveSolver(newPlane, this.tx, this.ty - 1, this.candiMatrix);
						forks.add(fs);
					} else if (this.tx + 1 <= 2 && newPlane.get(this.tx + 1, this.ty) == null) {
						RecursiveSolver fs = new RecursiveSolver(newPlane, this.tx + 1, this.ty, this.candiMatrix);
						forks.add(fs);
					} else if (this.ty + 1 <= 2 && newPlane.get(this.tx, this.ty + 1) == null) {
						RecursiveSolver fs = new RecursiveSolver(newPlane, this.tx, this.ty + 1, this.candiMatrix);
						forks.add(fs);
					} else if (this.tx - 1 >= 0 && newPlane.get(this.tx - 1, this.ty) == null) {
						RecursiveSolver fs = new RecursiveSolver(newPlane, this.tx - 1, this.ty, this.candiMatrix);
						forks.add(fs);
					}
				}
			}
			invokeAll(forks);
		}
	}
}
