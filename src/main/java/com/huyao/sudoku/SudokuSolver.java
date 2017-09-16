/**
 * SudokuSolver Copyright (C) 2014 ,Yao , All rights reserved.
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 */

package com.huyao.sudoku;

import com.huyao.sudoku.model.Permutation;
import com.huyao.sudoku.model.Plane;
import com.huyao.sudoku.model.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * The Abstract Class SudokuSolver.
 */
public abstract class SudokuSolver {

    /**
     * The candidate matrix.
     */
    protected List<int[]>[][] _candiMatrix = null;

    /**
     * Prepare candidate.
     *
     * @param p {@link Plane} initial sudoku plane
     */
    @SuppressWarnings("unchecked")
    protected void prepareCandidate(Plane p) {
        this._candiMatrix = new List[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int[] oriData = p.get(j, i);
                int[] restArray = Util.findRestArray(oriData);
                int[] oriCopy = Arrays.copyOf(oriData, oriData.length);
                this._candiMatrix[j][i] = new ArrayList<>();
                this._candiMatrix[j][i].add(Util.fillUpArray(oriCopy, restArray));
                Permutation per = new Permutation();
                per.init(restArray);
                while (per.hasNext()) {
                    int[] nextPer = per.nextPer();
                    oriCopy = Arrays.copyOf(oriData, oriData.length);
                    oriCopy = Util.fillUpArray(oriCopy, nextPer);
                    int[] horad1 = p.get((j + 1) % 3, i);
                    int[] horad2 = p.get((j + 2) % 3, i);
                    int[] verad1 = p.get(j, (i + 1) % 3);
                    int[] verad2 = p.get(j, (i + 2) % 3);
                    if (Validator.validateHor3Grid(oriCopy, horad1, horad2)
                            && Validator.validateVer3Grid(oriCopy, verad1, verad2))
                        this._candiMatrix[j][i].add(oriCopy);
                }

            }
        }
    }

    public abstract Map<String, Plane> solve(Plane p);
}
