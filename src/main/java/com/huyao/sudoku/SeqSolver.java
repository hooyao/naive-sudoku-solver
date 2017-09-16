/**
 * SudokuSolver Copyright (C) 2014 ,Yao , All rights reserved.
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 */
package com.huyao.sudoku;

import com.huyao.sudoku.model.Plane;
import com.huyao.sudoku.model.Validator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SeqSolver extends SudokuSolver {

    private ConcurrentHashMap<String, Plane> resultMap = new ConcurrentHashMap<>();

    /**
     * @return the resultMap
     */
    public ConcurrentHashMap<String, Plane> getResultMap() {
        return this.resultMap;
    }

    public Map<String, Plane> solve(Plane plane) {
        this.resultMap.clear();
        prepareCandidate(plane);
        Plane p = new Plane();
        findSolution(p, 0, 0, this._candiMatrix);
        return this.resultMap;
    }

    private void findSolution(Plane p, int tx, int ty,
                              List<int[]>[][] candiMatrix) {
        List<int[]> candiList = candiMatrix[tx][ty];
        for (int[] candi : candiList) {
            if (Validator.validateHor3Grid(candi, p.get((tx + 1) % 3, ty),
                    p.get((tx + 2) % 3, ty))
                    && Validator.validateVer3Grid(candi,
                    p.get(tx, (ty + 1) % 3), p.get(tx, (ty + 2) % 3))) {
                Plane newPlane = p.clone();
                newPlane.set(tx, ty, candi);
                if (newPlane.getGridCount() == 9) {
                    this.resultMap.put(String.valueOf(newPlane.computeHash()),
                            newPlane);
                    return;
                }
                if (ty - 1 >= 0 && newPlane.get(tx, ty - 1) == null) {
                    findSolution(newPlane, tx, ty - 1, candiMatrix);
                } else if (tx + 1 <= 2 && newPlane.get(tx + 1, ty) == null) {
                    findSolution(newPlane, tx + 1, ty, candiMatrix);
                } else if (ty + 1 <= 2 && newPlane.get(tx, ty + 1) == null) {
                    findSolution(newPlane, tx, ty + 1, candiMatrix);
                } else if (tx - 1 >= 0 && newPlane.get(tx - 1, ty) == null) {
                    findSolution(newPlane, tx - 1, ty, candiMatrix);
                }
            }
        }
    }
}
