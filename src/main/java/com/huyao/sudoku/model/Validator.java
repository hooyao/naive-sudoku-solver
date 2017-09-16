
package com.huyao.sudoku.model;


public class Validator {
    public static boolean validateHor3GridOnRow(int[] grid1, int[] grid2,
                                                int[] grid3, int rowNum) {
        int[] count = new int[9];
        //Arrays.fill(count, 0);
        int tmp = 0;
        int pos = 0;
        for (int i = 0; i < 3; ++i) {
            pos = rowNum * 3 + i;
            if (grid1 != null && (tmp = grid1[pos]) > 0)
                ++count[tmp - 1];
            if (grid2 != null && (tmp = grid2[pos]) > 0)
                ++count[tmp - 1];
            if (grid3 != null && (tmp = grid3[pos]) > 0)
                ++count[tmp - 1];

        }
        for (int i = 0; i < 9; ++i) {
            if (count[i] > 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateHor3Grid(int[] grid1, int[] grid2, int[] grid3) {

        for (int i = 0; i < 3; ++i) {
            if (!Validator.validateHor3GridOnRow(grid1, grid2, grid3, i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateVer3GridOnCol(int[] grid1, int[] grid2,
                                                int[] grid3, int colNum) {
        int[] count = new int[9];
        //Arrays.fill(count, 0);
        int tmp = 0;
        int pos = 0;
        for (int i = 0; i < 3; ++i) {
            pos = 3 * i + colNum;
            if (grid1 != null && (tmp = grid1[pos]) > 0)
                ++count[tmp - 1];
            if (grid2 != null && (tmp = grid2[pos]) > 0)
                ++count[tmp - 1];
            if (grid3 != null && (tmp = grid3[pos]) > 0)
                ++count[tmp - 1];
        }
        for (int i = 0; i < 9; ++i) {
            if (count[i] > 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateVer3Grid(int[] grid1, int[] grid2, int[] grid3) {

        for (int i = 0; i < 3; ++i) {
            if (!Validator.validateVer3GridOnCol(grid1, grid2, grid3, i)) {
                return false;
            }
        }
        return true;
    }

	/*public static void main(String[] args) {
        int[] data00 = { 9, 8, 7,
				         2, 4, 6, 
				         3, 5, 1 };
		int[] data01 = { 6, 5, 4, 
					     1, 7, 3, 
					     9, 2, 8 };
		int[] data02 = { 3, 2, 1, 
						 9, 8, 5, 
						 7, 4, 6 };

		try {
			Grid grid1 = new Grid(data00, false);
			Grid grid2 = new Grid(data01, false);
			Grid grid3 = new Grid(data02, false);
			//boolean is2Valid = Validator.validateHor2Grid(grid1, grid2);
			boolean is3Valid = Validator.validateVer3Grid(data00, data01, null);
			System.out.println(is3Valid );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
}
