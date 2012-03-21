/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.*;

/**
 *アプリケーションが装備するコマンドシステムの中核となるクラスです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
public class LeafShell {
	private Locale locale = Locale.getDefault();
	private final HashMap<String, Command> table;
	private final Logger logger;
	
	/**
	 *シェルを構築します。
	 */
	public LeafShell(){
		table = new HashMap<String, Command>();
		
		ConsoleHandler cons = new ConsoleHandler();
		cons.setFormatter(new ShellLogFormatter());
		
		logger = Logger.getLogger(getClass().getName());
		logger.setUseParentHandlers(false);
		logger.addHandler(cons);
	}
	/**
	 *新しいコマンドをシェルにインストールします。
	 *
	 *@param cmd 追加するコマンド
	 */
	public void install(Command cmd){
		table.put(cmd.getClass().getSimpleName(), cmd);
	}
	/**
	 *指定されたコマンド名に対応するコマンドを返します。
	 *
	 *@param name コマンド名
	 *@return 対応するコマンド 存在しない場合null
	 */
	public Command getCommand(String name){
		return table.get(name);
	}
	/**
	 *コマンド名と引数を指定してコマンドを実行します。
	 *
	 *@param name コマンド名
	 *@param args コマンドに渡す引数
	 */
	public void call(String name, Object... args){
		Command cmd = table.get(name);
		if(cmd != null) try{
			cmd.process(args);
		}catch(Exception ex){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			logger.warning(sw.toString());
		}else logger.warning("Not found : " + name);
	}
	/**
	 *シェルに適用されるロケールを設定します。
	 *
	 *@param locale 適用するロケール
	 *@throws NullPointerException ロケールがnullの場合
	 */
	public void setLocale(Locale locale) throws NullPointerException{
		if(!locale.equals(this.locale)){
			final LocaleEvent e = new LocaleEvent(this, locale);
			for(Command cmd : table.values()){
				if(cmd instanceof LocaleListener){
					((LocaleListener)cmd).localeChanged(e);
				}
			}
		}
	}
	/**
	 *シェルに適用されているロケールを返します。
	 *
	 *@return 適用されているロケール
	 */
	public Locale getLocale(){
		return locale;
	}
	/**
	 *シェルで発生した全ての例外の出力書式を定義します。
	 *
	 *@since 2011年12月11日
	 */
	private final class ShellLogFormatter extends Formatter{
		private final Date date = new Date();
		final DateFormat format = DateFormat.getDateTimeInstance();
		
		@Override public String format(LogRecord record){
			StringBuilder sb = new StringBuilder();
			date.setTime(record.getMillis());
			sb.append(format.format(date)).append(' ');
			sb.append(record.getLevel().getLocalizedName());
			sb.append(" at ");
			sb.append(LeafShell.this.getClass().getSimpleName());
			sb.append('\n');
			sb.append(super.formatMessage(record));
			return sb.append('\n').toString();
		}
	}
}
