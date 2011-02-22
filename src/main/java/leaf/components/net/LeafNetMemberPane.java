/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.components.net;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import leaf.net.client.*;
import leaf.manager.*;

/**
*Leafのネットワーク通信機能で、同室の全てのメンバーを表示するためのコンポーネントです。
*クライアント側で使用します。また、自動で一覧を更新する機能を持ちます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*@see LeafNetClient
*@see LeafNetRoomPane
*/
public class LeafNetMemberPane extends JPanel implements LeafNetListener{
	
	/**秘匿フィールド*/
	private final DefaultListModel model;
	private final LeafNetClient client;
	
	/**GUI部品*/
	private final JScrollPane scroll;
	private final JList list;
	
	/**
	*このコンポーネントを生成します。
	*@param client クライアント
	*/
	public LeafNetMemberPane(LeafNetClient client){
		super(new BorderLayout());
		model = new DefaultListModel();
		list = new JList(model);
		scroll = new JScrollPane(list,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(200,50));
		scroll.setMinimumSize(new Dimension(200,50));
		this.add(scroll,BorderLayout.CENTER);
		this.client = client;
		client.addListener(this);
	}
	/**
	*メンバーの一覧からメンバーを検索します。
	*@param name 検索するメンバーのニックネーム
	*/
	public boolean contains(String name){
		return model.contains(name);
	}
	/**メンバーの一覧情報を消去します。*/
	public void clear(){
		model.clear();
	}
	/**
	*メンバーの一覧情報を設定します。
	*@param members メンバーの一覧情報
	*/
	public void setMembers(ArrayList<String> members){
		try{
			Collections.sort(members);
		}catch(Exception ex){ex.printStackTrace();}
		model.clear();
		for(String member:members){
			model.addElement(member);
		}
	}
	/**
	*選択されているメンバーのニックネームを返します。
	*@return 選択状態にあるメンバーの名前
	*/
	public String getSelectedClientName(){
		return (String)list.getSelectedValue();
	}
	/**
	*メンバーの一覧を表現する文字列を返します。
	*return メンバーの一覧の文字列化表現
	*/
	public String getMembersDescription(){
		Object[] members = model.toArray();
		String ret = "";
		for(int i=0;i<members.length;i++){
			ret += " @" + members[i];
		}
		return ret;
	}
	/**
	*このコンポーネントの表示フォントを設定します。<br>
	*setFont(Font)メソッドは使用しないでください。
	*@param font フォント
	*/
	public void setTextFont(Font font){
		list.setFont(font);
	}
	/**
	*{@link LeafNetListener}を実装します。
	*/
	public void errorOccurred(LeafNetEvent e){}
	/**
	*{@link LeafNetListener}を実装します。<br>
	*メンバーの一覧を自動で更新します。
	*/
	public void messageReceived(LeafNetEvent e){
		String msg = e.getMessage();
		if(e.getCommand().equals("members")){
			if(msg.length()==0){
				clear();
			}else{
				setMembers(client.getMembers());
			}
		}
	}
}