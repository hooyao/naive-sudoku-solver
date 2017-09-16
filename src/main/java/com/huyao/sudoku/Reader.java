package com.huyao.sudoku;

import com.huyao.sudoku.model.Plane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class Reader {
	public static Plane readFromFile(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(path))){
			String line = ""; //$NON-NLS-1$
			Plane plane = new Plane();
			int[] data = new int[81];
			int y = 0;
			while ((line = br.readLine()) != null && line.trim().length() > 0) {
				StringTokenizer tk = new StringTokenizer(line);
				int x = 0;
				while (tk.hasMoreTokens()) {
					data[9 * y + x] = Integer.valueOf(tk.nextToken()).intValue();
					++x;
				}
				++y;
			}
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					int[] d = new int[9];
					for (int k = 0; k < 3; ++k) {
						System.arraycopy(data, 3 * j + i * 9 * 3 + 9 * k, d,
								3 * k, 3);
					}
					plane.set(j, i, d);
				}
			}
			br.close();
			return plane;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
