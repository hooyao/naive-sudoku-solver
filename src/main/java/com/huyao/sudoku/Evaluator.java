/**
 * 
SudokuSolver Copyright (C) 2014 ,Hu Yao , All rights reserved.
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.
 *
 */
package com.huyao.sudoku;

import com.huyao.sudoku.model.Plane;

import java.util.Arrays;
import java.util.Map;


/**
 * @author Hu Yao
 * 
 */
public class Evaluator {

	private static final int RUN_ITR = 10;

	private static final String INPUT_FILE = "hard.txt"; //$NON-NLS-1$

	private static final String[] solverNames = { 	"SeqSolver",//$NON-NLS-1$
													"ForkJoinSolver",  //$NON-NLS-1$
													"ParallelSolver",
													//"GPUSolver"
												 }; //$NON-NLS-1$

	private static final String packagePath = "com.huyao.sudoku"; //$NON-NLS-1$

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Plane p = Reader.readFromFile(INPUT_FILE);
		for (String name : solverNames) {
			String className = packagePath + "." + name; //$NON-NLS-1$
			try {
				Class<?> c = Class.forName(className);
				SudokuSolver solver = (SudokuSolver) c.newInstance();
				Map<String, Plane> map = null;
				double[] runTime = new double[RUN_ITR];
				for (int i = 0; i < RUN_ITR; i++) {
					long startTime = System.currentTimeMillis();
					map = solver.solve(p);
					long endTime = System.currentTimeMillis();
					runTime[i] = (endTime - startTime) / 1000.0;
				}
				Arrays.sort(runTime);
				double avgTime = 0.0;
				for (int i = 0; i < runTime.length - 1; i++) {// remove the
																// largest time
					avgTime += runTime[i];
				}
				avgTime = avgTime / (RUN_ITR - 1.0);
				System.out.println("Solver:" + name + "\navg solve time:" //$NON-NLS-1$ //$NON-NLS-2$
						+ String.format("%.4f", avgTime) + "s" + "\nIteration:" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						+ RUN_ITR);
				Util.printSolution(map);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
