/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.net;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import leaf.net.client.*;

/**
*Leafのネットワーク通信機能で、サーバー上の全てのルームを表示するコンポーネントです。
*<br>クライアント側で使用します。また、自動で一覧を更新する機能を持ちます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafNetMemberPane
*/
public class LeafNetRoomPane extends JPanel implements LeafNetListener{
	
	/**秘匿フィールド*/
	private final DefaultListModel model;
	private final LeafNetClient client;
	private final ArrayList<RoomSelectionListener> listeners;
	
	/**GUI部品*/
	private final JScrollPane scroll;
	private final JList list;
	
	/**
	*このコンポーネントを生成します。
	*@param client クライアント
	*/
	public LeafNetRoomPane(LeafNetClient client){
		super(new BorderLayout());
		model = new DefaultListModel();
		list = new JList(model);
		scroll = new JScrollPane(list,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setMinimumSize(new Dimension(200,50));
		this.add(scroll,BorderLayout.CENTER);
		list.addMouseListener(new LeafMouseAdapter());
		
		listeners = new ArrayList<RoomSelectionListener>(1);
		this.client = client;
		client.addListener(this);
	}
	/**
	*ルームの一覧からルームを検索します。
	*@param name 検索するルームの名前
	*/
	public boolean contains(String name){
		return model.contains(name);
	}
	/**ルームの一覧情報を消去します。*/
	public void clear(){
		model.clear();
	}
	/**
	*ルームの一覧情報を設定します。
	*@param rooms ルームの一覧情報
	*/
	public void setRooms(ArrayList<String> rooms){
		try{
			Collections.sort(rooms);
		}catch(Exception ex){ex.printStackTrace();}
		model.clear();
		for(String room:rooms){
			model.addElement(room);
		}
	}
	/**
	*選択されているルームの名前を返します。
	*@return 選択状態にあるルーム名
	*/
	public String getSelectedRoomName(){
		return (String)list.getSelectedValue();
	}
	/**MouseAdapter*/
	private class LeafMouseAdapter extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount() >= 2){
				int index = list.locationToIndex(e.getPoint());
				String roomname = (String)model.get(index);
				for(RoomSelectionListener lis:listeners){
					lis.roomSelected(roomname);
				}
			}
		}
	}
	/**
	*RoomSelectionListenerを登録します。
	*@param lis 追加する{@link RoomSelectionListener}
	*/
	public void addRoomSelectionListener(RoomSelectionListener lis){
		listeners.add(lis);
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
	*ルームの一覧を自動で更新します。
	*/
	public void messageReceived(LeafNetEvent e){
		String msg = e.getMessage();
		if(e.getCommand().equals("rooms")){
			if(msg.length()==0){
				clear();
			}else{
				setRooms(client.getRooms());
			}
		}
	}
}