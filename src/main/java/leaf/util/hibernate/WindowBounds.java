/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.hibernate;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;

import leaf.util.hibernate.Properties;

/**
 * ウィンドウの位置と大きさを永続化します。
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012年10月21日
 */
public class WindowBounds {
	private Window window;
	private String name;
	private Properties prop;
	
	/**
	 * 指定されたウィンドウに関連付けられたオブジェクトを生成します。
	 * 
	 * @param window 関連付けるウィンドウ
	 * @param name ウィンドウの名前
	 */
	public WindowBounds(Window window, String name){
		prop = Properties.getInstance(getClass());
		this.window = window;
		this.name = name;
	}
	
	private String key(String key){
		return window.getClass().getName() + "." + name + "." + key;
	}
	
	/**
	 * ウィンドウに位置と大きさの情報を適用します。
	 */
	public void applyBounds(){
		Dimension size = new Dimension(720, 480);
		window.setSize(prop.get(key("size"), Dimension.class, size));
		
		Point location = prop.get(key("location"), Point.class, null);
		
		if(location != null) window.setLocation(location);
		else window.setLocationRelativeTo(null);
		
		if(window instanceof Frame) applyExtendedState((Frame) window);
	}
	
	private void applyExtendedState(Frame frame) {
		Integer extended = prop.get(key("extendedState"), Integer.class, null);
		frame.setExtendedState(extended != null? extended : Frame.NORMAL);
	}
	
	/**
	 * ウィンドウの位置と大きさの情報を保存します。
	 */
	public void saveBounds(){
		if(window instanceof Frame) {
			int state = ((Frame) window).getExtendedState();
			prop.put(key("extendedState"), state);
			if(state == Frame.MAXIMIZED_BOTH) return;
		}
		
		prop.put(key("size"),     window.getSize());
		prop.put(key("location"), window.getLocation());
	}
}
