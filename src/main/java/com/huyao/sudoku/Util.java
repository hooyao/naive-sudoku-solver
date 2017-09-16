package com.huyao.sudoku;

import com.huyao.sudoku.model.Plane;

import java.util.Map;


public class Util {
    public static void printSolution(Map<?, ?> map) {
        System.out.println("result count:" + map.size()); //$NON-NLS-1$
        for (Object obj : map.values()) {
            if (obj instanceof Plane) {
                Plane plane = (Plane) obj;
                System.out.println("===================="); //$NON-NLS-1$
                for (int i = 0; i < 9; i++) {
                    if (i != 0 && i % 3 == 0)
                        System.out.println("--------------------"); //$NON-NLS-1$
                    for (int j = 0; j < 9; j++) {
                        if (j != 0 && j % 3 == 0 && j < 8)
                            System.out.print("|"); //$NON-NLS-1$
                        int gridx = j / 3;
                        int gridy = i / 3;
                        int localx = j - 3 * gridx;
                        int localy = i - 3 * gridy;
                        System.out.print(plane.get(gridx, gridy)[localy * 3 + localx] + " "); //$NON-NLS-1$

                    }
                    System.out.println();
                }
            }
        }
        System.out.println("===================="); //$NON-NLS-1$
    }

    public static int[] findRestArray(int[] in) {
        int length = 0;
        for (int ele : in) {
            if (ele == 0)
                ++length;
        }
        int[] result = new int[length];
        boolean bFound = false;
        int idx = 0;
        for (int i = 0; i < result.length; i++) {
            do {
                ++idx;
                bFound = false;
                for (int j = 0; j < in.length; j++) {
                    if (in[j] == idx) {
                        bFound = true;
                    }
                }
            } while (bFound);
            result[i] = idx;
        }
        return result;
    }

    public static int[] fillUpArray(int[] ori, int[] fill) {
        int oriIdx = 0;
        int fillIdx = 0;
        while (oriIdx < 9) {
            if (ori[oriIdx] < 1) {
                ori[oriIdx] = fill[fillIdx];
                ++oriIdx;
                ++fillIdx;
            } else {
                ++oriIdx;
            }
        }
        return ori;
    }

}
