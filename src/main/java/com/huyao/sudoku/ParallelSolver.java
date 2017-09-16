package com.huyao.sudoku;

import com.huyao.sudoku.model.Plane;
import com.huyao.sudoku.model.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class ParallelSolver extends SudokuSolver {
    /*
     * (non-Javadoc)
     *
     * @see
     * com.hy.sudoku.solver.core.SudokuSolver#solve(com.hy.sudoku.solver.core
     * .model.Plane)
     */
    @Override
    public Map<String, Plane> solve(Plane plane) {
        int cores = Runtime.getRuntime().availableProcessors();
        prepareCandidate(plane);
        Plane p = new Plane();
        ForkJoinPool pool = new ForkJoinPool(cores * 2);
        RecursiveSolver rs = new RecursiveSolver(p, 0, 0, this._candiMatrix,
                true);
        RecursiveSolver.getResultMap().clear();
        pool.invoke(rs);
        pool.shutdown();
        return RecursiveSolver.getResultMap();
    }

    private static class RecursiveSolver extends RecursiveAction {
        /**
         *
         */
        private static final long serialVersionUID = 3241001768161132097L;
        private Plane p;
        private int tx;
        private int ty;
        private List<int[]>[][] candiMatrix;
        private boolean invokeMore = false;
        /**
         *
         */
        static final ConcurrentHashMap<String, Plane> resultMap = new ConcurrentHashMap<>();

        /**
         * @return the resultMap
         */
        public static ConcurrentHashMap<String, Plane> getResultMap() {
            return RecursiveSolver.resultMap;
        }

        public RecursiveSolver(Plane p, int tx, int ty,
                               List<int[]>[][] candiMatrix, boolean invokeMore) {
            this.p = p;
            this.tx = tx;
            this.ty = ty;
            this.candiMatrix = candiMatrix;
            this.invokeMore = invokeMore;
        }

        @Override
        protected void compute() {
            List<int[]> candiList = this.candiMatrix[this.tx][this.ty];
            if (this.invokeMore) {
                List<RecursiveAction> forks = new ArrayList<>();
                for (int[] candi : candiList) {
                    if (Validator.validateHor3Grid(candi,
                            this.p.get((this.tx + 1) % 3, this.ty),
                            this.p.get((this.tx + 2) % 3, this.ty))
                            && Validator.validateVer3Grid(candi,
                            this.p.get(this.tx, (this.ty + 1) % 3),
                            this.p.get(this.tx, (this.ty + 2) % 3))) {
                        Plane newPlane = this.p.clone();
                        newPlane.set(this.tx, this.ty, candi);
                        if (newPlane.getGridCount() == 9) {
                            RecursiveSolver.resultMap.put(
                                    String.valueOf(newPlane.computeHash()),
                                    newPlane);
                            return;
                        }

                        if (this.ty - 1 >= 0
                                && newPlane.get(this.tx, this.ty - 1) == null) {
                            RecursiveSolver fs = new RecursiveSolver(newPlane,
                                    this.tx, this.ty - 1, this.candiMatrix,
                                    false);
                            forks.add(fs);
                        } else if (this.tx + 1 <= 2
                                && newPlane.get(this.tx + 1, this.ty) == null) {
                            RecursiveSolver fs = new RecursiveSolver(newPlane,
                                    this.tx + 1, this.ty, this.candiMatrix,
                                    false);
                            forks.add(fs);
                        } else if (this.ty + 1 <= 2
                                && newPlane.get(this.tx, this.ty + 1) == null) {
                            RecursiveSolver fs = new RecursiveSolver(newPlane,
                                    this.tx, this.ty + 1, this.candiMatrix,
                                    false);
                            forks.add(fs);
                        } else if (this.tx - 1 >= 0
                                && newPlane.get(this.tx - 1, this.ty) == null) {
                            RecursiveSolver fs = new RecursiveSolver(newPlane,
                                    this.tx - 1, this.ty, this.candiMatrix,
                                    false);
                            forks.add(fs);
                        }
                    }

                }
                invokeAll(forks);
            } else {
                for (int[] candi : candiList) {
                    if (Validator.validateHor3Grid(candi,
                            this.p.get((this.tx + 1) % 3, this.ty),
                            this.p.get((this.tx + 2) % 3, this.ty))
                            && Validator.validateVer3Grid(candi,
                            this.p.get(this.tx, (this.ty + 1) % 3),
                            this.p.get(this.tx, (this.ty + 2) % 3))) {
                        Plane newPlane = this.p.clone();
                        newPlane.set(this.tx, this.ty, candi);
                        if (newPlane.getGridCount() == 9) {
                            RecursiveSolver.resultMap.put(
                                    String.valueOf(newPlane.computeHash()),
                                    newPlane);
                            return;
                        }
                        if (this.ty - 1 >= 0
                                && newPlane.get(this.tx, this.ty - 1) == null) {
                            findSolution(newPlane, this.tx, this.ty - 1,
                                    this.candiMatrix);
                        } else if (this.tx + 1 <= 2
                                && newPlane.get(this.tx + 1, this.ty) == null) {
                            findSolution(newPlane, this.tx + 1, this.ty,
                                    this.candiMatrix);
                        } else if (this.ty + 1 <= 2
                                && newPlane.get(this.tx, this.ty + 1) == null) {
                            findSolution(newPlane, this.tx, this.ty + 1,
                                    this.candiMatrix);
                        } else if (this.tx - 1 >= 0
                                && newPlane.get(this.tx - 1, this.ty) == null) {
                            findSolution(newPlane, this.tx - 1, this.ty,
                                    this.candiMatrix);
                        }
                    }
                }
            }
        }

    }

    static void findSolution(Plane p, int tx, int ty,
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
                    RecursiveSolver.resultMap.put(
                            String.valueOf(newPlane.computeHash()), newPlane);
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
