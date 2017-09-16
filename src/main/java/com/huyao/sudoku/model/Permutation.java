package com.huyao.sudoku.model;

public class Permutation {
    private int i, j, tmp;
    private int N;
    int a[];
    int p[];

    private int fullPerCount(int n) {
        int count = 1;
        for (int t = 1; t <= n; ++t) {
            count *= t;
        }
        return count;
    }

    public void init(int[] data) {
        this.N = data.length;
        this.a = new int[this.N];
        System.arraycopy(data, 0, this.a, 0, this.N);
        this.p = new int[this.N + 1];

        for (this.i = 0; this.i < this.N; this.i++) // initialize arrays; a[N]
        // can be any type
        {
            this.p[this.i] = this.i;
        }
        this.p[this.N] = this.N; // p[N] > 0 controls iteration and the index
        // boundary for i
        // display(a, 0, 0); // remove comment to display array a[]
        this.i = 1; // setup first swap points to be 1 and 0 respectively (i &
        // j)

    }

    public int initToGetPerCount(int[] data) {
        this.N = data.length;
        this.a = new int[this.N];
        System.arraycopy(data, 0, this.a, 0, this.N);
        this.p = new int[this.N + 1];

        for (this.i = 0; this.i < this.N; this.i++) // initialize arrays; a[N]
        // can be any type
        {
            this.p[this.i] = this.i;
        }
        this.p[this.N] = this.N; // p[N] > 0 controls iteration and the index
        // boundary for i
        // display(a, 0, 0); // remove comment to display array a[]
        this.i = 1; // setup first swap points to be 1 and 0 respectively (i &
        // j)
        return fullPerCount(this.N);
    }

    public boolean hasNext() {
        return this.i < this.N;
    }

    public int[] nextPer() {
        int[] per = new int[this.N];
        this.p[this.i]--; // decrease index "weight" for i by one
        this.j = this.i % 2 * this.p[this.i]; // IF i is odd then j = p[i]
        // otherwise j = 0
        this.tmp = this.a[this.j]; // swap(a[j], a[i])
        this.a[this.j] = this.a[this.i];
        this.a[this.i] = this.tmp;
        per = new int[this.N];
        System.arraycopy(this.a, 0, per, 0, this.N);
        // display(a, j, i); // remove comment to display target array a[]
        this.i = 1; // reset index i to 1 (assumed)
        while (this.p[this.i] == 0) // while (p[i] == 0)
        {
            this.p[this.i] = this.i; // reset p[i] zero value
            this.i++; // set new index value for i (increase by one)
        } // while(!p[i])
        return per;
    }

	/*
     * public static void main(String[] args) { Permutation per = new
	 * Permutation(); int data[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	 * per.init(data); long start = System.currentTimeMillis(); List<int[]>
	 * perList = new ArrayList<int[]>(); while (per.hasNext()) {
	 * perList.add(per.nextPer()); }
	 * 
	 * long end = System.currentTimeMillis(); System.out.println(perList.size()
	 * + ":" + (end - start));
	 * 
	 * for (int[] is : perList) { for (int i : is) { System.out.print(i); }
	 * System.out.println(); }
	 * 
	 * }
	 */
}
