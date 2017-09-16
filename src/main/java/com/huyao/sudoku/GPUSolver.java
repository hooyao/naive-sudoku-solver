package com.huyao.sudoku;

import com.aparapi.Kernel;
import com.huyao.sudoku.model.Permutation;
import com.huyao.sudoku.model.Plane;
import com.huyao.sudoku.model.Validator;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GPUSolver extends SudokuSolver {
    //static AtomicInteger counter = new AtomicInteger(0);

    private int[][][] _candiMatrix2 = new int[3][3][];

    private ConcurrentHashMap<String, Plane> resultMap = new ConcurrentHashMap<>();

    /**
     * @return the resultMap
     */
    public ConcurrentHashMap<String, Plane> getResultMap() {
        return this.resultMap;
    }


    private void prepareCandidate2(Plane p) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int[] oriData = p.get(j, i);
                int[] restArray = Util.findRestArray(oriData);
                Permutation per = new Permutation();
                int perCount = per.initToGetPerCount(restArray);
                this._candiMatrix2[j][i] = new int[perCount * 9];
                int[] oriCopy = Arrays.copyOf(oriData, oriData.length);
                int idx = 0;
                System.arraycopy(Util.fillUpArray(oriCopy, restArray), 0, (this._candiMatrix2[j][i]), idx * 9, 9);

                while (per.hasNext()) {
                    int[] nextPer = per.nextPer();
                    ++idx;
                    oriCopy = Arrays.copyOf(oriData, oriData.length);
                    oriCopy = Util.fillUpArray(oriCopy, nextPer);
                    int[] horad1 = p.get((j + 1) % 3, i);
                    int[] horad2 = p.get((j + 2) % 3, i);
                    int[] verad1 = p.get(j, (i + 1) % 3);
                    int[] verad2 = p.get(j, (i + 2) % 3);
                    if (Validator.validateHor3Grid(oriCopy, horad1, horad2)
                            && Validator.validateVer3Grid(oriCopy, verad1,
                            verad2))
                        System.arraycopy(oriCopy, 0, (this._candiMatrix2[j][i]), idx * 9, 9);
                }

            }
        }
    }

    @Override
    public Map<String, Plane> solve(Plane plane) {
        this.resultMap.clear();
        prepareCandidate2(plane);
        //Plane p = new Plane();
        int[] p = new int[81];
        /*for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
				System.arraycopy(plane.get(i, j), 0, p, (j*3+i)*9, 9);
			}
		}*/
        findSolution(p, 0, 0, this._candiMatrix2);
        return this.resultMap;
    }


    private void findSolution(int[] p, int tx, int ty,
                              int[][][] candiMatrix) {
        final int x = tx;
        final int y = ty;
        final int[] plane = p;
        final int[] candiList = candiMatrix[tx][ty];
        int size = candiList.length / 9;
        final boolean[] done = new boolean[size];
        Arrays.fill(done, false);
        Kernel kernel = new Kernel() {
            int[] count = new int[9];

            @Override
            public void run() {
                int id = getGlobalId();
                /*if(id ==0 ){
                    id = 0;
				}*/
                if (shallProceed(candiList, id * 9)) {
                    if (validateHor3Grid(candiList, id * 9, plane,
                            ((x + 1) % 3 + y * 3) * 9, plane,
                            ((x + 2) % 3 + y * 3) * 9)
                            && validateVer3Grid(candiList, id * 9, plane,
                            (x + (y + 1) % 3 * 3) * 9, plane,
                            (x + (y + 2) % 3 * 3) * 9)) {
                        //System.out.print("id:"+id);
                        done[id] = true;
                    }
                }
            }

            public boolean validateHor3Grid(int[] grid1, int pos1, int[] grid2, int pos2, int[] grid3, int pos3) {

                for (int rowNum = 0; rowNum < 3; ++rowNum) {
                    zeroCount(count);
                    // Arrays.fill(count, 0);
                    int tmp = 0;
                    int pos = 0;
                    for (int i = 0; i < 3; ++i) {
                        pos = rowNum * 3 + i;
                        tmp = grid1[pos1 + pos];
                        if (tmp > 0)
                            ++count[tmp - 1];
                        tmp = grid2[pos2 + pos];
                        if (tmp > 0)
                            ++count[tmp - 1];
                        tmp = grid3[pos3 + pos];
                        if (tmp > 0)
                            ++count[tmp - 1];

                    }
                    for (int i = 0; i < 9; ++i) {
                        if (count[i] > 1) {
                            return false;
                        }
                    }
                }
                return true;
            }

            public boolean validateVer3Grid(int[] grid1, int pos1, int[] grid2, int pos2, int[] grid3, int pos3) {

                for (int colNum = 0; colNum < 3; ++colNum) {
                    zeroCount(count);
                    //Arrays.fill(count, 0);
                    int tmp = 0;
                    int pos = 0;
                    for (int i = 0; i < 3; ++i) {
                        pos = 3 * i + colNum;
                        tmp = grid1[pos1 + pos];
                        if (tmp > 0)
                            ++count[tmp - 1];
                        tmp = grid2[pos2 + pos];
                        if (tmp > 0)
                            ++count[tmp - 1];
                        tmp = grid3[pos3 + pos];
                        if (tmp > 0)
                            ++count[tmp - 1];
                    }
                    for (int i = 0; i < 9; ++i) {
                        if (count[i] > 1) {
                            return false;
                        }
                    }
                }
                return true;
            }

            public void zeroCount(int[] count) {
                for (int i = 0; i < 9; ++i) {
                    count[i] = 0;
                }
            }

            public boolean shallProceed(int[] candiList, int pos) {
                int sum = 0;
                for (int i = 0; i < 9; i++) {
                    sum += candiList[pos + i];
                }
                return sum != 0;
            }
        };
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
        kernel.setExplicit(true); // we take control of all transfers
        kernel.put(candiList);
        kernel.put(plane);
        kernel.put(done);
        kernel.execute(size);

        kernel.get(done);
        System.out.println("Execution mode=" + kernel.getExecutionMode());
        for (int i = 0; i < done.length; ++i) {
            boolean isDone = done[i];
            if (isDone) {
                int[] newPlane = Arrays.copyOf(plane, 81);
                System.arraycopy(candiList, i * 9, newPlane, (x + y * 3) * 9, 9);
                //System.out.println(ifAllFound(newPlane));
                if (ifAllFound(newPlane) == 0) {
                    Plane np = new Plane();
                    for (int j = 0; j < 3; ++j) {
                        for (int k = 0; k < 3; k++) {
                            int tem[] = new int[9];
                            System.arraycopy(newPlane, (k * 3 + j) * 9, tem, 0, 9);
                            np.set(j, k, tem);
                        }
                    }
                    this.resultMap.put(String.valueOf(np.computeHash()), np);
                    return;
                }
                if (ty - 1 >= 0 && !ifBeenThere(newPlane, tx, ty - 1)) {
                    findSolution(newPlane, tx, ty - 1, candiMatrix);
                } else if (tx + 1 <= 2 && !ifBeenThere(newPlane, tx + 1, ty)) {
                    findSolution(newPlane, tx + 1, ty, candiMatrix);
                } else if (ty + 1 <= 2 && !ifBeenThere(newPlane, tx, ty + 1)) {
                    findSolution(newPlane, tx, ty + 1, candiMatrix);
                } else if (tx - 1 >= 0 && !ifBeenThere(newPlane, tx - 1, ty)) {
                    findSolution(newPlane, tx - 1, ty, candiMatrix);
                }
            }
        }

    }

    public int ifAllFound(int[] plane) {
        int zeroCount = 0;
        for (int i : plane) {
            if (i == 0) {
                zeroCount++;
            }
        }
        return zeroCount;
    }

    public boolean ifBeenThere(int[] plane, int x, int y) {
        int sum = 0;
        for (int i = 0; i < 9; ++i) {
            sum += plane[(3 * y + x) * 9 + i];
        }
        return sum != 0;
    }

}
