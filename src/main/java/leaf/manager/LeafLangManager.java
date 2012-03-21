/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
*言語セットを管理し、多言語化を実現するクラスです。
*Leafの全てのクラスはこのクラスを利用して多言語化に対応します。
*<br><br>
*<b>このクラスはレガシーです。</b>LeafAPIの全てのクラスは
*{@link LeafLocalizeManager}への迅速な移行が勧告されています。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月2日
*@see LeafLocalizeManager
*/
//@Deprecated
public class LeafLangManager{
	
	/**使用する言語として英語を表します。*/
	public static final int LANG_ENGLISH = 0;
	/**使用する言語として日本語を表します。*/
	public static final int LANG_JAPANESE = 1;
	/**英語・日本語以外の言語を表します。*/
	public static final int LANG_ANOTHER = 2;
	
	private static Properties prop = new Properties();
	private static File file;
	private static int lang = LANG_ENGLISH;
	
	private static final Pattern parg = Pattern.compile("[arg]", Pattern.LITERAL);
	
	private LeafLangManager(){}
	
	/**
	*使用する言語を設定します。
	*@param lang 言語を表すこのクラスの定数
	*/
	public static void setLanguage(int lang){
		LeafLangManager.lang = lang;
	}
	/**
	*現在使用されている言語を返します。
	*@return 言語を表すこのクラスの定数
	*/
	public static int getLanguage(){
		return lang;
	}
	/**
	*言語セットを取得します。使用できる言語セットファイルは、<br>
	*「英語表現=その言語による表現」の形で言語間の対応を表したプロパティファイルです。
	*英語・日本語のみの場合は取得する必要はありません。
	*@param file 言語セットのプロパティファイル名
	*/
	public static void load(File file){
		try{
			load(new FileInputStream(file));
		}catch(Exception ex){ex.printStackTrace();}
	}
	/**設定をXMLから取得*/
	private static void load(InputStream stream){
		try{
			prop.load(stream);
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try{
				stream.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	/**
	*英語と日本語に対し、使用言語に対応した訳文を返します。
	*@param eng 英語による表現
	*@param jpn 日本語による表現
	*@return 英文に対する訳文
	*/
	public static String get(String eng, String jpn){
		return (lang==LANG_ENGLISH)?eng:((lang==LANG_JAPANESE)?jpn:prop.getProperty(eng));
	}
	/**
	*英語と日本語に対し、使用言語に対応した訳文を返します。
	*取得される訳文は、引数の英語表現をキーとしたプロパティによって得られます。
	*訳文には置換要素"[arg]"を埋め込むことができます。
	*@param eng 英語による表現
	*@param jpn 日本語による表現
	*@param args 置換文字列
	*@return 英文に対する訳文
	*/
	public static String translate(String eng, String jpn, Object... args){
		Matcher m = parg.matcher(get(eng, jpn));
		StringBuffer sb = new StringBuffer();
		for(Object arg : args){
			String repl = Matcher.quoteReplacement(String.valueOf(arg));
			if(m.find()){
				m.appendReplacement(sb, repl);
				continue;
			}
			break;
		}
		m.appendTail(sb);
		return sb.toString();
	}
}