/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import javax.script.ScriptException;

/**
 * LISPの組み込み関数を環境に導入します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012年10月11日
 *
 */
public final class Builtin {
	private Builtin() {}
	
	/**
	 * 指定された環境にビルトイン関数を導入します。
	 * 
	 * @param bindings 環境
	 */
	public static void build(Bindings bindings) {
		bindings.put(new FuncAppend());
		bindings.put(new FuncAtomp());
		bindings.put(new FuncCar());
		bindings.put(new FuncCdr());
		bindings.put(new FuncCond());
		bindings.put(new FuncCons());
		bindings.put(new FuncConsp());
		bindings.put(new FuncDisplay());
		bindings.put(new FuncDefun());
		bindings.put(new FuncEq());
		bindings.put(new FuncEval());
		bindings.put(new FuncIf());
		bindings.put(new FuncLambda());
		bindings.put(new FuncLength());
		bindings.put(new FuncList());
		bindings.put(new FuncListp());
		bindings.put(new FuncNullp());
		bindings.put(new FuncNumberp());
		bindings.put(new FuncQuote());
		bindings.put(new FuncSetq());
		bindings.put(new FuncSymbolp());
		
		bindings.put(new FuncAdd());
		bindings.put(new FuncSubtract());
		bindings.put(new FuncMultiply());
		bindings.put(new FuncDivide());
		bindings.put(new FuncMod());
		bindings.put(new FuncGreatEq());
		bindings.put(new FuncGreat());
		bindings.put(new FuncLess());
		bindings.put(new FuncLessEq());
	}
	
	// Basic built-in functions
	
	private static class FuncAppend extends Function {
		@Override
		public String name() { return "append";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List mtop = new List(), mcell = mtop;
			List atop = args.asList(), acell = atop;
			while(!acell.isEmpty()) {
				List list = eval.eval(acell.car()).asList();
				while(!list.isEmpty()) {
					mcell.car(list.car());
					if(list.isTerminal()) break;
					list = list.cdr().asList();
					mcell = (List) mcell.cdr(new List());
				}
				if(list.hasDotPair()) {
					if(!acell.isTerminal()) {
						mcell = (List) mcell.cdr(new List());
						mcell.car(list.cdr());
					}
					else mcell.cdr(list.cdr());
				}
				if(acell.isTerminal()) break;
				acell = acell.cdr().asList();
				mcell = (List) mcell.cdr(new List());
			}
			return mtop;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize >= 1? 0 : -1;
		}
	}
	
	private static class FuncAtomp extends Predicate{
		@Override
		public String name() { return "atom?";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()) instanceof Atom;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncCar extends Function {
		@Override
		public String name() { return "car";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()).asList().car();
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncCdr extends Function {
		@Override
		public String name() { return "cdr";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()).asList().cdr();
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncCond extends Function {
		@Override
		public String name() { return "cond";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List conds = args.asList();
			while(conds != (Sexp) Nil.NIL) {
				List cond = conds.car().asList();
				if(eval.eval(cond.car()).asBoolean()) {
					return eval.eval(cond.cdr());
				}
				conds = conds.cdr().asList();
			}
			return Nil.NIL;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return 0;
		}
	}
	
	private static class FuncCons extends Function {
		@Override
		public String name() { return "cons";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Sexp arg1 = eval.eval(list.car());
			Sexp arg2 = eval.eval(list.cdr().asList().car());
			return new List(arg1, arg2);
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncConsp extends Predicate{
		@Override
		public String name() { return "cons?";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()) instanceof List;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncDisplay extends Function {
		@Override
		public String name() { return "display";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			Sexp value = eval.eval(args.asList().car());
			System.out.print(value.toDisplayString());
			return value;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncDefun extends Function {
		@Override
		public String name() { return "defun";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			Symbol name = args.asList().car().asSymbol();
			Sexp define = args.asList().cdr();
			List lambda = new Lambda();
			lambda.cdr(define);
			name.bind(lambda);
			return name;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 3;
		}
	}
	
	private static class FuncEq extends Predicate{
		@Override
		public String name() { return "eq";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			List list = args.asList();
			Sexp arg1 = eval.eval(list.car());
			Sexp arg2 = eval.eval(list.cdr().asList().car());
			return arg1.isEqual(arg2);
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncEval extends Function {
		@Override
		public String name() { return "eval";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car());
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncIf extends Function {
		@Override
		public String name() { return "if";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			boolean cond = eval.eval(args.asList().car()).asBoolean();
			Sexp sexps = args.asList().cdr();
			Sexp tsexp = sexps.asList().car();
			Sexp fsexp = sexps.asList().cdr().asList().car();
			return eval.eval(cond? tsexp : fsexp);
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 3;
		}
	}
	
	private static class FuncLambda extends Function {
		@Override
		public String name() { return "lambda";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List lambda = new Lambda();
			lambda.cdr(args);
			return lambda;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncLength extends Function {
		@Override
		public String name() { return "length";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			Sexp sexp = eval.eval(args.asList().car());
			if(sexp == Nil.NIL) return Real.valueOf(0);
			return Real.valueOf(sexp.asList().size());
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncList extends Function {
		@Override
		public String name() { return "list";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			if(args == Nil.NIL) return Nil.NIL;
			List list = args.asList();
			List copy = new List(), top = copy;
			while (!list.isEmpty()) {
				copy.car(eval.eval(list.car()));
				if(list.isTerminal()) break;
				list = list.cdr().asList();
				copy = (List) copy.cdr(new List());
			}
			if(list.hasDotPair()) copy.cdr(list.cdr());
			return top;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return 0;
		}
	}
	
	private static class FuncListp extends Predicate{
		@Override
		public String name() { return "list?";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()) instanceof Listp;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncNullp extends Predicate{
		@Override
		public String name() { return "null?";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()) == Nil.NIL;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncNumberp extends Predicate{
		@Override
		public String name() { return "number?";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()) instanceof Num;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncQuote extends Function {
		@Override
		public String name() { return "quote";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			return args.asList().car();
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	private static class FuncSetq extends Function {
		@Override
		public String name() { return "setq";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Symbol symbol = list.car().asSymbol();
			Sexp value = eval.eval(list.cdr().asList().car());
			return symbol.bind(value);
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncSymbolp extends Predicate{
		@Override
		public String name() { return "symbol?";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			return eval.eval(args.asList().car()) instanceof Symbol;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 1;
		}
	}
	
	// Math built-in functions
	
	private static class FuncAdd extends Function {
		@Override
		public String name() { return "+";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Num result = null;
			while (!list.isEmpty()) {
				Num next = eval.eval(list.car()).asNum();
				if (result == null) result = next;
				else result = result.add(next);
				if (list.isTerminal()) break;
				list = list.cdr().asList();
			}
			return result;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize >= 2? 0 : -1;
		}
	}
	
	private static class FuncSubtract extends Function {
		@Override
		public String name() { return "-";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Num result = null;
			while (!list.isEmpty()) {
				Num next = eval.eval(list.car()).asNum();
				if (result == null) result = next;
				else result = result.subtract(next);
				if (list.isTerminal()) break;
				list = list.cdr().asList();
			}
			return result;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize >= 2? 0 : -1;
		}
	}
	
	private static class FuncMultiply extends Function {
		@Override
		public String name() { return "*";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Num result = null;
			while (!list.isEmpty()) {
				Num next = eval.eval(list.car()).asNum();
				if (result == null) result = next;
				else result = result.multiply(next);
				if (list.isTerminal()) break;
				list = list.cdr().asList();
			}
			return result;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize >= 2? 0 : -1;
		}
	}
	
	private static class FuncDivide extends Function {
		@Override
		public String name() { return "/";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Num result = null;
			while (!list.isEmpty()) {
				Num next = eval.eval(list.car()).asNum();
				if (result == null) result = next;
				else result = result.divide(next);
				if (list.isTerminal()) break;
				list = list.cdr().asList();
			}
			return result;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize >= 2? 0 : -1;
		}
	}
	
	private static class FuncMod extends Function {
		@Override
		public String name() { return "mod";}
		
		@Override
		public Sexp invoke(Sexp args) throws ScriptException {
			List list = args.asList();
			Num result = null;
			while (!list.isEmpty()) {
				Num next = eval.eval(list.car()).asNum();
				if (result == null) result = next;
				else result = result.mod(next);
				if (list.isTerminal()) break;
				list = list.cdr().asList();
			}
			return result;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize >= 2? 0 : -1;
		}
	}
	
	private static class FuncGreatEq extends Predicate{
		@Override
		public String name() { return ">=";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			List list = args.asList();
			Sexp cadr = list.cdr().asList().car();
			Num arg1 = eval.eval(list.car()).asNum();
			Num arg2 = eval.eval(cadr).asNum();
			return arg1.compareTo(arg2) >= 0;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncGreat extends Predicate{
		@Override
		public String name() { return ">";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			List list = args.asList();
			Sexp cadr = list.cdr().asList().car();
			Num arg1 = eval.eval(list.car()).asNum();
			Num arg2 = eval.eval(cadr).asNum();
			return arg1.compareTo(arg2) > 0;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncLess extends Predicate{
		@Override
		public String name() { return "<";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			List list = args.asList();
			Sexp cadr = list.cdr().asList().car();
			Num arg1 = eval.eval(list.car()).asNum();
			Num arg2 = eval.eval(cadr).asNum();
			return arg1.compareTo(arg2) < 0;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}
	
	private static class FuncLessEq extends Predicate{
		@Override
		public String name() { return "<=";}
		
		@Override
		public boolean predicate(Sexp args) throws ScriptException {
			List list = args.asList();
			Sexp cadr = list.cdr().asList().car();
			Num arg1 = eval.eval(list.car()).asNum();
			Num arg2 = eval.eval(cadr).asNum();
			return arg1.compareTo(arg2) <= 0;
		}
		
		@Override
		public int verifyArgumentSize(int argSize) {
			return argSize - 2;
		}
	}

}
