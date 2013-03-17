/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * LISPのリスト型を実装するクラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public class List extends Sexp implements Listp{
	private Sexp car = null, cdr = null;
	
	/**
	 * 空のリストを構築します。
	 */
	public List(){}
	
	/**
	 * CAR部とCDR部を指定してリストを構築します。
	 * 
	 * @param car CAR部
	 * @param cdr CDR部
	 */
	public List(Sexp car, Sexp cdr){
		this.car = car;
		this.cdr = cdr;
	}
	
	@Override public Sexp car(){
		return car != null? car : Nil.NIL;
	}
	
	/**
	 * 指定されたS式をリストのCAR部に設定し、評価せずに返します。
	 * 
	 * @param sexp CAR部
	 * @return CAR部に設定された値
	 */
	public Sexp car(Sexp sexp){
		car = sexp; return car();
	}
	
	@Override public Sexp cdr(){
		return cdr != null? cdr : Nil.NIL;
	}
	
	/**
	 * 指定されたS式をリストのCDR部に設定し、評価せずに返します。
	 * 
	 * @param sexp CDR部
	 * @return CDR部に設定された値
	 */
	public Sexp cdr(Sexp sexp){
		cdr = sexp; return cdr();
	}
	
	/**
	 * このリストの終端までの長さを返します。
	 * 
	 * @return 終端まで辿った長さ
	 */
	public int size(){
		List cell = this;
		for(int i=1; ; i++){
			if(cell.isTerminal()) return i;
			else cell = (List) cell.cdr();
		}
	}
	
	/**
	 * このリストが空リストであるか返します。
	 * 
	 * @return CAR部が空であればtrue
	 */
	public boolean isEmpty(){
		return car == null;
	}
	
	/**
	 * このリストが終端であるか返します。
	 * 
	 * @return CDR部が{@link List}でなければtrue
	 */
	public boolean isTerminal(){
		return cdr == null || !(cdr instanceof List);
	}
	
	/**
	 * このリストがドット対であるか返します。
	 * 
	 * @return CDR部がアトムであればtrue
	 */
	public boolean hasDotPair(){
		return cdr instanceof Atom;
	}
	
	@Override public String toString(){
		StringBuilder sb = new StringBuilder("(");
		List cell = this;
		while(!cell.isEmpty()){
			sb.append(cell.car());
			if(cell.hasDotPair()){
				sb.append(" . ");
				sb.append(cell.cdr());
				break;
			}else if(!cell.isTerminal()) {
				sb.append(' ');
				cell = (List) cell.cdr();
			}else break;
		}
		return new String(sb.append(")"));
	}
	
	@Override public boolean isEqual(Sexp sexp){
		return sexp == this;
	}
}
