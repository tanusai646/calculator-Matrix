/*
 * 「結果」が行列である電卓. 
 * 行列クラスを定義し, とりあえず演算としては加算や単位行列等を定義している. 
 * コンパイル & 実行：
 * javac Calculator.java IntCalc.java MemoCalc.java MatrixCalc.java
 * java MatrixCalc
 */

import java.util.*;
import java.io.*;
//import java.math.*;

/**
 * 電卓の「結果」として使う行列を表すクラス. 
 * 行列の要素は {@code double} の2次元配列で保持する. 
 * 加算や単位行列生成などの演算や, 行列を文字列から読み込む機能を提供する. 
 */
class Matrix {
    /**
     * 行列の行数. 
     */
    final int m;
    /**
     * 行列の列数. 
     */
    final int n;
    /**
     * 行列の要素. 
     * 並びは自然な並びで： {@code vals[i][j]} が (i, j) 要素. 
     */
    double [][] vals;
    /**
     * {@code m}×{@code n} のゼロ行列を作るコンストラクタ. 
     * @param m 行数 
     * @param n 列数 
     */
    Matrix(int m, int n) {
        this.m = m;
        this.n = n;
        vals = new double[m][n];
    }
    /**
     * 与えられた行列をコピーするコンストラクタ. 
     * @param mat コピー元の行列. 
     */
    Matrix(Matrix mat) {
        this(mat.m, mat.n);
        copy(mat.vals);
    }
    /**
     * 与えられた2次元配列の内容を自身の要素としてコピーする. 
     * 次元は矛盾しないとする（与えられた2次元配列の方が大きければ良い）. 
     * @param vals コピー元の2次元配列. 
     */
    void copy(double [][] vals) {
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                this.vals[i][j] = vals[i][j];
            }
        }
    }
    /**
     * 与えら得た行列がサイズ違いで自身に加減算できないときに {@code true} を返す. 
     * @param mat 行列. 
     * @return {@code mat} と自身のサイズが同じでないときに {@code true}. 
     */
    boolean sizeMismatch(Matrix mat) {
        return (mat.m != m) || (mat.n != n);
    }
    /**
     * 与えられた行列と自身の加算結果の行列を新たに生成して返す. 
     * @param mat 加算する行列
     * @return 行列加算 {@code this} + {@code mat} の結果となる行列. 
     *         サイズ違いなどで計算不可能な場合には {@code null}. 
     */
    Matrix add(Matrix mat) {
        // 計算できないときには null を返す. 
        if(mat == null || sizeMismatch(mat)) return null;
        // あとは単純な加算
        Matrix ret = new Matrix(m, n);
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                ret.vals[i][j] = this.vals[i][j] + mat.vals[i][j];
            }
        }
        return ret;
    }
    /**
     * 自身を与えられたdouble型の実数でスカラー倍した結果の行列を新たに生成して返す. 
     * @param a 行列をスカラー倍する実数
     * @return 行列のスカラー倍 {@code this} * {@code a} の結果となる行列.  
     */
    Matrix smul(double a) {
        // あとは単純なスカラー倍
        Matrix ret = new Matrix(m, n);
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                ret.vals[i][j] = this.vals[i][j] * a;
            }
        }
        return ret;
    }
    /**
     * 与えられた行列と自身の乗算結果の行列を新たに生成して返す. 
     * @param mat 乗算する行列
     * @return 行列乗算 {@code this} * {@code mat} の結果となる行列. 
     *         サイズ違いなどで計算不可能な場合には {@code null}. 
     */
    Matrix mul(Matrix mat) {
        // 計算できないときには null を返す. 
        if(mat == null || this.n != mat.m) return null;
        // あとは公式どおり乗算
        Matrix ret = new Matrix(this.m, mat.n);
        for(int i = 0; i < this.m; i++) {
            for(int j = 0; j < mat.n; j++) {
		for(int k = 0; k < this.n; k++){
		    ret.vals[i][j] += this.vals[i][k] * mat.vals[k][j];
		}
            }
        }
        return ret;
    }
    
    /**
     * 与えられたサイズの単位行列を新たに生成して返す. 
     * @param n 生成する行列のサイズ
     * @return {@code n}×{@code n} の単位行列
     */
    public static Matrix eye(int n) {
        Matrix ret = new Matrix(n, n); // これは nxn のゼロ行列
        for(int i = 0; i < n; i++) {
            ret.vals[i][i] = 1;  // 対角に 1 を入れる
        }
        return ret;
    }

    /**
     * 現在の結果である行列の逆行列を新たに生成して返す.
     * @return {@code this}の逆行列となる行列．
     * 正方行列でない場合には {@code null}. 
     */
    Matrix inv(){
	// 正方行列でないときには null を返す. 
        if(this.m != this.n) return null;
	Matrix ret = new Matrix(this);//現在の行列
        Matrix res = new Matrix(this.m, this.m);
	res = Matrix.eye(this.m); // これは mxm の単位行列,最終的に逆行列になる
	double buf;  //一時的にデータを保存する変数
	//掃き出し法で逆行列を導出
        for(int i = 0; i < this.m; i++) {
	    buf = 1 / ret.vals[i][i];
	    for(int j = 0; j < this.m; j++){
		ret.vals[i][j] *= buf;
		res.vals[i][j] *= buf;
	    }
	    for(int j = 0; j < this.m; j++){
		if(i != j){
		    buf = ret.vals[j][i];
		    for(int k = 0; k < this.m; k++){
			ret.vals[j][k] -= ret.vals[i][k] * buf;
			res.vals[j][k] -= res.vals[i][k] * buf;
		    }
		}
	    }
        }
        return res;
    }

     /**
     * 現在の結果である行列の上三角行列を新たに生成して返す.
     * @return {@code this}の上三角行列となる行列．
     * 正方行列でない場合には {@code null}. 
     */
    Matrix umat(){
	// 正方行列でないときには null を返す. 
        if(this.m != this.n) return null;
        Matrix ret = new Matrix(this); // 現在の行列
	double buf;  //一時的にデータを保存する変数
	//上三角行列を導出
        for(int i = 0; i < this.m; i++) {
	    for(int j = 0; j < this.m; j++){
		if(i < j){
		    buf = ret.vals[j][i] / ret.vals[i][i];
		    for(int k = 0; k < this.m; k++){
			ret.vals[j][k] -= ret.vals[i][k]*buf;
		    }
		}
	    }

	}
        return ret;
    }

    /**
     * 現在の結果である行列の下三角行列を新たに生成して返す.
     * @return {@code this}の下三角行列となる行列．
     * 正方行列でない場合には {@code null}. 
     */
    Matrix lmat(){
	//正方行列でないときには null を返す. 
        if(this.m != this.n) return null;
	//上三角行列と下三角行列の積がもとの行列になることを使って，元の行列を上三角行列で割って下三角行列を求める
        Matrix v = new Matrix(this);
	v = v.umat();
	Matrix w = v.inv();
	return this.mul(w);
    }

    /**
     * 現在の結果である行列の行列式の値を返す.
     * @return {@code this}の行列式の値．
     * 正方行列でない場合には {@code 0}. 
     */
    double determ(){
	// 正方行列でないときには 0 を返す. 
        if(this.m != this.n) return 0;
	//上三角行列の対角成分の積が行列式の値であることを使っている
        double x = 1;
	Matrix v = new Matrix(this);
	v = v.umat();
	for(int i = 0; i < v.m;i++){
	    x *= v.vals[i][i];
	}
	return x;
    }

     /**
     * 現在の行列が正則でないときに {@code true} を返す. 
     * @return {@code this} の行列式の値が０である場合 {@code true}. 
     */
    boolean nonregular() {
        return this.determ() == 0;
    }

    
    /**
     * 現在の結果である行列を固有値分解しできた対角行列を返す.
     * @return {@code this}を固有値分解してできた対角行列．
     * 正方行列でない場合には {@code null}. 
     */
    Matrix eigen(){
	// 正方行列でないときには null を返す. 
        if(this.m != this.n) return null;
	//行列をLU分解し導出した上三角行列と下三角行列を逆順に掛ける
	//これを新たな行列として，収束するまでこの動作を繰り返す
        Matrix res = new Matrix(this);
	for(int i = 0; i < 1000; i++){
	    Matrix l = new Matrix(res);
	    Matrix u = new Matrix(res);
	    l = l.lmat();
	    u = u.umat();
	    res = u.mul(l);
	    double e = 0.0;
	    for(int j = 1; j < res.m;j++){
		for(int k = 0; k < j; k++){
		    e += Math.abs(res.vals[j][k]);
		}
	    }
	    if(e < 0.00000000001) break;//収束条件
	}
	//収束後対角行列を作成
	Matrix ret = new Matrix(this.m,this.n);
	for(int a = 0; a < this.m; a++){
	    ret.vals[a][a] = res.vals[a][a];
	}
	return ret;
    }
    
    /**
     * 「ブロック」から与えられたサイズの単位行列を新たに生成して返す. 
     * @param block 電卓から受け取る「ブロック」.
     * @return {@code n}×{@code} の単位行列. 行のサイズの食い違いなどで生成に失敗したら {@code null}
     */
    public static Matrix read(final List<String> block) {
        try {
            int m = block.size() - 1;  // 一行目は行列の中身ではないので無視して行数を決める
            int n = -1;                // 列数は, 最初の行を見て決める
            Matrix ret = null;
            for(int i = 0; i < m; i++) {
                StringTokenizer st = new StringTokenizer(block.get(i+1));
                ArrayList<String> vs = new ArrayList<String>();
                while(st.hasMoreTokens()) vs.add(st.nextToken());
                if(n < 0) {  // 最初の行でサイズ確定 → 決定したサイズの行列をここで生成
                    n = vs.size();
                    ret = new Matrix(m, n);
                } else if(n != vs.size()) {// 行の間でサイズの食い違いがあったら null
                    return null;
                }
                // 要素をコピー
                int j = 0;
                for(String s : vs) {
                    ret.vals[i][j++] = Double.parseDouble(s);
                }
            }
            return ret;
        } catch(Exception e) { // なにか変な例外が生じた際にも生成失敗
        }
        return null;
    }
    /**
     * 行列を表す文字列を返す. 
     * 例えば次のような文字列となる. 
     * <p><blockquote><pre>{@code
     * [   2.000    3.000    4.000]
     * [   5.000    6.000    7.000]
     * }</pre></blockquote><p>
     * （各行の開始と終わりに [ と ] が置かれ, 各要素は固定幅（8.3f）で表示. 
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < m; i++) {
            sb.append("[");
            for(int j = 0; j < n; j++) {
                if(j > 0) sb.append(" ");
                sb.append(String.format("%1$8.3f", vals[i][j]));
            }
            sb.append("]");
            if(i < m - 1) sb.append("\n");
        }
        return sb.toString();
    }
}

/**
 * 行列加算を入力して現在の「結果」をその行列にする「コマンド」. 
 * <p><blockquote><pre>{@code
 * mat :
 *  TAB  a_11 ... a_1m
 *  TAB  a_21 ... a_2m
 *    ...
 *  TAB  a_n1 ... a_nm
 * }</pre></blockquote><p>
 * という, 1行目が {@code mat} である複数行「ブロック」を受け付け, 入力された行列を「結果」として返す. 
 * 行列の入力は, 各行の要素を TAB 始まりの各行に空白区切りで並べて入力する. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2行目以降は TAB を先頭に入力すること）. 
 * <p><blockquote><pre>{@code
 * mat :
 *      2 3 4
 *      5 6 7
 * }</pre></blockquote><p>
 */
class MatrixValue implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix r) {
        if(block.size() <= 1) return null;
        if(ts.length == 1 && "mat".equals(ts[0])) {
            // 実際の読み込みは Matrix クラスに任せる
            return Matrix.read(block);
        }
        return null;
    }
}

/**
 * 単位行列を現在の「結果」にする「コマンド」. 
 * {@code eye} の後に整数値が並ぶ 1行の「ブロック」を受け付け, その整数値のサイズの単位行列を「結果」として返す. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2x2 の単位行列になる）. 
 * <p><blockquote><pre>{@code
 * eye 2
 * }</pre></blockquote><p>
 */
class IdentityMatrix implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix r) {
        if(block.size() != 1) return null;
        if(ts.length == 2 && "eye".equals(ts[0])) {
            // 単位行列の実際の生成は Matrix クラスにまかせる
            return Matrix.eye(Integer.parseInt(ts[1]));
        }
        return null;
    }
}

/**
 * ゼロ行列を現在の「結果」にする「コマンド」. 
 * <p><blockquote><pre>{@code
 * zero n
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, その整数値 {@code n} のサイズのゼロ行列を「結果」として返す. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2x2 のゼロ行列になる）. 
 * <p><blockquote><pre>{@code
 * zero 2
 * }</pre></blockquote><p>
 */
class ZeroMatrix implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix r) {
        if(block.size() != 1) return null;
        if(ts.length == 2 && "zero".equals(ts[0])) {
            int n = Integer.parseInt(ts[1]);
            return new Matrix(n, n); // コンストラクタで生成した状態がゼロ行列なので
        }
        return null;
    }
}

/**
 * 行列加算の「コマンド」. 
 * <p><blockquote><pre>{@code
 * add :
 *  TAB  a_11 ... a_1m
 *  TAB  a_21 ... a_2m
 *    ...
 *  TAB  a_n1 ... a_nm
 * }</pre></blockquote><p>
 * という, 1行目が {@code add} である複数行「ブロック」を受け付け, 入力された行列を足した「結果」を返す. 
 * 行列の入力は, 各行の要素を TAB 始まりの各行に空白区切りで並べて入力する. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2行目以降は TAB を先頭に入力すること）. 
 * <p><blockquote><pre>{@code
 * add :
 *      1 0 1
 *      0 1 0
 * }</pre></blockquote><p>
 * <br />
 * もしくは, {@code add} の後ろに変数名を書いた 1行の「ブロック」を受け付け, 
 * 変数に保存された行列を足した「結果」を返す.  
 */
class MatrixAdd extends CommandWithMemory<Matrix> {
    /**
     * 変数の情報を保持する {@code Memory} オブジェクトを受け取るコンストラクタ. 
     * @param mem 変数の情報を保持するオブジェクト. 
     */
    MatrixAdd(Memory<Matrix> mem) {
        super(mem); // 親のコンストラクタをそのまま呼ぶだけ
    }
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        // 行列の値を直接書く場合
        if(block.size() > 1 && ts.length == 1 && "add".equals(ts[0])){
            // 実際の読み込みと加算は Matrix クラスに任せる
            Matrix v = Matrix.read(block);
            return res.add(v);
        }
        // 行列を保存した変数が指定された場合
        if(block.size() == 1 && ts.length == 2 && "add".equals(ts[0])) {
            // 変数の値をメモリから取得
            Matrix v = mem.get(ts[1]);
            return res.add(v); // 実際の加算は Matrix クラス任せ
        }
        return null;
    }
}

/**
 * 行列スカラー倍の「コマンド」. 
 * <p><blockquote><pre>{@code
 * smul a
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, 現在の結果に実数値 {@code a} をスカラー倍した行列を「結果」として返す. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（行列の全成分が2倍される）. 
 * <p><blockquote><pre>{@code
 * smul 2
 * }</pre></blockquote><p>
 */
class MatrixScalarMul implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        if(block.size() == 1 && ts.length == 2 && "smul".equals(ts[0])) {
            // 変数の値をメモリから取得
            double a = Double.parseDouble(ts[1]);
            return res.smul(a); // 実際の加算は Matrix クラス任せ
        }
        return null;
    }
}

/**
 * 行列減算の「コマンド」. 
 * <p><blockquote><pre>{@code
 * sub :
 *  TAB  a_11 ... a_1m
 *  TAB  a_21 ... a_2m
 *    ...
 *  TAB  a_n1 ... a_nm
 * }</pre></blockquote><p>
 * という, 1行目が {@code sub} である複数行「ブロック」を受け付け, 入力された行列を引いた「結果」を返す. 
 * 行列の入力は, 各行の要素を TAB 始まりの各行に空白区切りで並べて入力する. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2行目以降は TAB を先頭に入力すること）. 
 * <p><blockquote><pre>{@code
 * sub :
 *      1 0 1
 *      0 1 0
 * }</pre></blockquote><p>
 * <br />
 * もしくは, {@code sub} の後ろに変数名を書いた 1行の「ブロック」を受け付け, 
 * 変数に保存された行列を引いた「結果」を返す.  
 */
class MatrixSub extends CommandWithMemory<Matrix> {
    /**
     * 変数の情報を保持する {@code Memory} オブジェクトを受け取るコンストラクタ. 
     * @param mem 変数の情報を保持するオブジェクト. 
     */
    MatrixSub(Memory<Matrix> mem) {
        super(mem); // 親のコンストラクタをそのまま呼ぶだけ
    }
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        // 行列の値を直接書く場合
        if(block.size() > 1 && ts.length == 1 && "sub".equals(ts[0])){
            // 実際の読み込みと減算は Matrix クラスに任せる
            Matrix v = Matrix.read(block);
	    v = v.smul(-1);
            return res.add(v);
        }
        // 行列を保存した変数が指定された場合
        if(block.size() == 1 && ts.length == 2 && "sub".equals(ts[0])) {
            // 変数の値をメモリから取得
            Matrix v = mem.get(ts[1]);
	    v = v.smul(-1);
            return res.add(v); // 実際の減算は Matrix クラス任せ
        }
        return null;
    }
}

/**
 * 行列乗算の「コマンド」. 
 * <p><blockquote><pre>{@code
 * mul :
 *  TAB  a_11 ... a_1m
 *  TAB  a_21 ... a_2m
 *    ...
 *  TAB  a_n1 ... a_nm
 * }</pre></blockquote><p>
 * という, 1行目が {@code mul} である複数行「ブロック」を受け付け, 入力された行列を掛けた「結果」を返す. 
 * 行列の入力は, 各行の要素を TAB 始まりの各行に空白区切りで並べて入力する. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2行目以降は TAB を先頭に入力すること）. 
 * <p><blockquote><pre>{@code
 * mul :
 *      1 0 1
 *      0 1 0
 * }</pre></blockquote><p>
 * <br />
 * もしくは, {@code mul} の後ろに変数名を書いた 1行の「ブロック」を受け付け, 
 * 変数に保存された行列を掛けた「結果」を返す.  
 */
class MatrixMul extends CommandWithMemory<Matrix> {
    /**
     * 変数の情報を保持する {@code Memory} オブジェクトを受け取るコンストラクタ. 
     * @param mem 変数の情報を保持するオブジェクト. 
     */
    MatrixMul(Memory<Matrix> mem) {
        super(mem); // 親のコンストラクタをそのまま呼ぶだけ
    }
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        // 行列の値を直接書く場合
        if(block.size() > 1 && ts.length == 1 && "mul".equals(ts[0])){
            // 実際の読み込みと乗算は Matrix クラスに任せる
            Matrix v = Matrix.read(block);
            return res.mul(v);
        }
        // 行列を保存した変数が指定された場合
        if(block.size() == 1 && ts.length == 2 && "mul".equals(ts[0])) {
            // 変数の値をメモリから取得
            Matrix v = mem.get(ts[1]);
            return res.mul(v); // 実際の乗算は Matrix クラス任せ
        }
        return null;
    }
}

/**
 * 行列除算の「コマンド」. 
 * <p><blockquote><pre>{@code
 * div :
 *  TAB  a_11 ... a_1m
 *  TAB  a_21 ... a_2m
 *    ...
 *  TAB  a_n1 ... a_nm
 * }</pre></blockquote><p>
 * という, 1行目が {@code div} である複数行「ブロック」を受け付け, 入力された行列を割った「結果」を返す. 
 * 行列の入力は, 各行の要素を TAB 始まりの各行に空白区切りで並べて入力する. 
 * 例えば, 次のような「ブロック」を入力として受け付ける（2行目以降は TAB を先頭に入力すること）. 
 * <p><blockquote><pre>{@code
 * div :
 *      1 0 1
 *      0 1 0
 * }</pre></blockquote><p>
 * <br />
 * もしくは, {@code div} の後ろに変数名を書いた 1行の「ブロック」を受け付け, 
 * 変数に保存された行列を掛けた「結果」を返す.  
 */
class MatrixDiv extends CommandWithMemory<Matrix> {
    /**
     * 変数の情報を保持する {@code Memory} オブジェクトを受け取るコンストラクタ. 
     * @param mem 変数の情報を保持するオブジェクト. 
     */
    MatrixDiv(Memory<Matrix> mem) {
        super(mem); // 親のコンストラクタをそのまま呼ぶだけ
    }
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        // 行列の値を直接書く場合
        if(block.size() > 1 && ts.length == 1 && "div".equals(ts[0])){
	    // 割る側の行列の逆行列を求め,それを割られる側の行列に掛ける
	    // 実際の読み込みと逆行列の導出,逆行列との乗算は Matrix クラスに任せる
            Matrix v = Matrix.read(block);
	    Matrix w = v.inv();
            return res.mul(w);
        }
        // 行列を保存した変数が指定された場合
        if(block.size() == 1 && ts.length == 2 && "div".equals(ts[0])) {
            // 変数の値をメモリから取得
            Matrix v = mem.get(ts[1]);
	    // 割る側の行列の逆行列を求め,それを割られる側の行列に掛ける
	    // 逆行列の導出,逆行列との乗算は Matrix クラスに任せる
	    Matrix w = v.inv();
            return res.mul(w);        
        }
        return null;
    }
}

/**
 * 「結果」の逆行列を現在の「結果」にする「コマンド」. 
 * <p><blockquote><pre>{@code
 * inv
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, 現在の結果の逆行列を「結果」として返す. 
 */
class InverseMatrix implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        if(block.size() == 1 && ts.length == 1 && "inv".equals(ts[0])) {
	    //行列式の値の導出，それを用いて正則かどうか判定
	    double v = res.determ();
	    System.out.println(String.format("行列式：%1$8.3f\n", v));
	    if(res.nonregular()){
		//正則でない場合
		//メッセージを出し，現在の行列をそのまま返す
		System.out.println("逆行列は存在しません");
		return res;
	    }else{
		//正則である場合
		return res.inv(); // 実際の計算は Matrix クラス任せ
	    }
        }
        return null;
    }
}

/**
 * 「結果」の上三角行列を現在の「結果」にする「コマンド」. 
 * <p><blockquote><pre>{@code
 * umat
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, 現在の結果の上三角行列を「結果」として返す. 
 */
class UMatrix implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        if(block.size() == 1 && ts.length == 1 && "umat".equals(ts[0])) {
            return res.umat(); // 実際の計算は Matrix クラス任せ
        }
        return null;
    }
}

/**
 * 「結果」の下三角行列を現在の「結果」にする「コマンド」. 
 * <p><blockquote><pre>{@code
 * lmat
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, 現在の結果の下三角行列を「結果」として返す. 
 */
class LMatrix implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        if(block.size() == 1 && ts.length == 1 && "lmat".equals(ts[0])) {
	    // 実際の計算は Matrix クラス任せ
	    return res.lmat();
        }
        return null;
    }
}

/**
 * 「結果」を固有値分解して求めた対角行列を現在の「結果」にする「コマンド」. 
 * <p><blockquote><pre>{@code
 * eigen
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, 現在の結果を固有値分解し求めた対角行列を「結果」として返す. 
 */
class EigenValue implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        if(block.size() == 1 && ts.length == 1 && "eigen".equals(ts[0])) {
	    // 実際の計算は Matrix クラス任せ
	    return res.eigen();
        }
        return null;
    }
}

/**
 * 線形方程式演算の「コマンド」. 
 * <p><blockquote><pre>{@code
 * equation
 * }</pre></blockquote><p>
 * のような 1行「ブロック」を受け付け, 現在の結果を拡大係数行列とみて線形方程式を解いた時の解を「結果」として返す. 
 */
class LinearEquation implements Command<Matrix> {
    public Matrix tryExec(final String [] ts, final List<String> block, final Matrix res) {
        if(block.size() == 1 && ts.length == 1 && "equation".equals(ts[0])){
	    if(res.n - res.m != 1) return null;//解が一意に決まらない場合はnullを返す
	    //行列を係数行列と定数項のベクトルに分ける
	    Matrix w = new Matrix(res.m,res.m);
	    Matrix x = new Matrix(res.m, 1);
	    for(int i = 0;i< res.m; i++){
		for(int j = 0; j < res.m ; j++){
		    w.vals[i][j] = res.vals[i][j];
		}
		x.vals[i][0] = res.vals[i][res.m]; 
	    }
	    if(w.nonregular())return null;//係数行列が正則でないならばnullを返す
            // 実際の計算は Matrix クラスに任せる
	    w = w.inv();
            return w.mul(x);
        }
        return null;
    }
}


/**
 * 行列電卓を作成して動作させるクラス. 
 * 例えば, ターミナルで次のような実行ができる. 
 * <p><blockquote><pre>{@code
 * $ javac Calculator.java IntCalc.java MemoCalc.java MatrixCalc.java
 * $ java MatrixCalc
 * [   0.000    0.000]
 * [   0.000    0.000]
 * >> mat :
 * ..      2 3 4
 * ..      5 6 7
 * ..
 * [   2.000    3.000    4.000]
 * [   5.000    6.000    7.000]
 * >> store x
 * [   2.000    3.000    4.000]
 * [   5.000    6.000    7.000]
 * >> add :
 * ..      1 0 1
 * ..      0 1 0
 * ..
 * [   3.000    3.000    5.000]
 * [   5.000    7.000    7.000]
 * >> add x
 * [   5.000    6.000    9.000]
 * [  10.000   13.000   14.000]
 * >> eye 2
 * [   1.000    0.000]
 * [   0.000    1.000]
 * >>
 * }</pre></blockquote><p>
 * これは, 最初に「結果」が 2x2 のゼロ行列で電卓が動き始め, 
 * まずは最初のプロンプトの後ろで {@code mat : } と打って複数行の「ブロック」の入力を始め, 
 * 続く TAB 始まりの 2行に 2x3 行列の各行の要素を書き, 続く空行で「ブロック」の入力を終え, 
 * その「ブロック」に対して {@code MatrixValue} が実行されて「結果」がその行列になり, 
 * 続いて {@code store x} と入力し, 
 * {@code LoadStore} が動作してその行列が変数 x に保存され, 
 * 続いて {@code add :} からの数行で同様に 2x3 行列を入力し, 
 * それに対して {@code MatrixAdd} が動作して「結果」が行列の和になり, 
 * さらに {@code add x} と入力し, 
 * それに対して {@code MatrixAdd} が動作して変数 x に保存した行列が加算され, 
 * 最後に {@code eye 2} と入力し, 
 * それに対して {@code IdentityMatrix} が動いて「結果」が 2x2 の単位行列となった. 
 */
class MatrixCalc {
    /**
     * 電卓を作って実行する. 
     */
    public static void main(String [] args) throws Exception {
        // 行列を記憶する変数のための Memory インスタンス
        Memory<Matrix> mem = new Memory<Matrix>();
        // コマンドリストの作成
        ArrayList<Command<Matrix>> comms = new ArrayList<Command<Matrix>>();
        comms.add(new EmptyCommand<Matrix>());
        comms.add(new MatrixValue());
        comms.add(new IdentityMatrix());
        comms.add(new ZeroMatrix());
        comms.add(new MatrixAdd(mem));
	comms.add(new MatrixScalarMul());
	comms.add(new MatrixSub(mem));
	comms.add(new MatrixMul(mem));
	comms.add(new MatrixDiv(mem));
	comms.add(new InverseMatrix());
	comms.add(new UMatrix());
	comms.add(new LMatrix());
	comms.add(new EigenValue());
        comms.add(new LinearEquation());
	comms.add(new LoadStore<Matrix>(mem));
        comms.add(mem);
        // 入力は標準入力から
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // 電卓の生成と実行
        Calculator<Matrix> c = new Calculator<Matrix>(br, comms);
        // 初期値は 2x2 のゼロ行列
        c.run(new Matrix(2,2));
    }
}
