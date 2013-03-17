/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

/**
 * 中間言語の命令セットです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public enum InstructionSet {
	EXIT,
	PUSH,

	ITOD,
	DTOI,
	
	NEG,
	AND,
	OR,
	XOR,
	EQ,
	NEQ,
	
	IADD,
	ISUB,
	IMUL,
	IDIV,
	IREM,
	ILT,
	IGT,
	ILE,
	IGE,
	INEG,
	IAND,
	IOR,
	IXOR,

	DADD,
	DSUB,
	DMUL,
	DDIV,
	DREM,
	DLT,
	DGT,
	DLE,
	DGE,
	DNEG,

	LASSN,
	LPUSH,
	AASSN,
	APUSH,
	JUMP,
	JUMPF,
	JUMPT,
	CALL,
	FRAME,
	RET,
	ADEL,
	DEL,

	PRINT,
	
	JNEW,
	JINV,
	JASSN,
	JPUSH
}
