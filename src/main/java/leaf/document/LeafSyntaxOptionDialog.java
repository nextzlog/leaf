/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.document;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import leaf.dialog.LeafDialog;
import leaf.dialog.LeafColorDialog;
import leaf.manager.LeafArrayManager;
import leaf.manager.LeafLangManager;

/**
*キーワード強調設定の設定ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月15日
*@see LeafSyntaxManager
*@see KeywordSet
*/

public final class LeafSyntaxOptionDialog extends LeafDialog{
	
	private boolean isChanged = CANCEL_OPTION;
	private DefaultComboBoxModel setmodel;
	private List<KeywordSet> sets;
	
	private LeafSyntaxManager manager;
	
	private KeywordSet set = null;
	private List<String> keywords = null;
	
	private JPanel listpanel,companel;
	private JLabel setlb;
	private JComboBox setcomb;
	private JButton bsadd,bsdel,bsext,bkwadd,bkwedit,bkwdel;
	private JButton bcstart,bcend,bcline,bcol,bok,bcan;
	private JScrollPane kwscroll;
	private JList kwlist;
	
	private Map<String, Color> colors;
	private final LeafColorDialog dialog;
	
	private final int BLOCK_COMMENT_START = 0;
	private final int BLOCK_COMMENT_END   = 1;
	private final int LINE_COMMENT_START  = 2;
	
	/**
	*親フレームとシンタックスマネージャを指定して設定画面を生成します。
	*@param owner 親フレーム
	*@param manager 設定保存先のシンタックスマネージャ
	*/
	public LeafSyntaxOptionDialog(Frame owner, LeafSyntaxManager manager){
		super(owner, null, true);
		
		getContentPane().setPreferredSize(new Dimension(370,315));
		pack();
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		colors = LeafSyntaxManager.getColorMap();
		dialog = new LeafColorDialog(this, colors);
		init(manager);
	}
	/**
	*親ダイアログとシンタックスマネージャを指定して設定画面を生成します。
	*@param owner 親ダイアログ
	*@param manager 設定保存先のシンタックスマネージャ
	*/
	public LeafSyntaxOptionDialog(Dialog owner, LeafSyntaxManager manager){
		super(owner, null, true);
		
		getContentPane().setPreferredSize(new Dimension(370,315));
		pack();
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		colors = LeafSyntaxManager.getColorMap();
		dialog = new LeafColorDialog(this, colors);
		init(manager);
	}
	public void init(){}
	/**
	*シンタックスマネージャを指定してダイアログを初期化します。
	*@param manager 設定保存先のシンタックスマネージャ
	*/
	public void init(LeafSyntaxManager manager){
		if(manager!=null&&manager.getKeywordSetCount()>0){
			sets = (this.manager = manager).getKeywordSets();
			setmodel = new DefaultComboBoxModel(sets.toArray());
		}else{
			this.manager = new LeafSyntaxManager();
			sets = new ArrayList<KeywordSet>();
			setmodel = new DefaultComboBoxModel();
		}
		
		getContentPane().removeAll();
		setTitle(LeafLangManager.get("Syntax Options", "キーワードの設定"));
		
		/*セット*/
		setlb = new JLabel(LeafLangManager.get("Set","セット"),JLabel.RIGHT);
		setlb.setBounds(0,5,45,20);
		add(setlb);
		
		setcomb  = new JComboBox(setmodel);
		setcomb.setBounds(45,5,105,20);
		add(setcomb);
		set = (KeywordSet)setcomb.getSelectedItem();
		
		setcomb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				set = (KeywordSet)setcomb.getSelectedItem();
				update();
			}
		});
		
		/*セット追加ボタン*/
		bsadd = new JButton(LeafLangManager.get("Add","追加"));
		bsadd.setBounds(155,5,60,20);
		add(bsadd);
		
		bsadd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addSet();
			}
		});
		
		/*セット削除ボタン*/
		bsdel = new JButton(LeafLangManager.get("Del","削除"));
		bsdel.setBounds(220,5,60,20);
		add(bsdel);
		
		bsdel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSet();
			}
		});
		
		/*拡張子設定ボタン*/
		bsext = new JButton(LeafLangManager.get("Ext.","拡張子"));
		bsext.setBounds(285,5,80,20);
		add(bsext);
		
		bsext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setExtension();
			}
		});
		
		/*キーワード一覧パネル*/
		listpanel = new JPanel(null);
		listpanel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Keywords","キーワード")
		));
		listpanel.setBounds(0,30,370,260);
		add(listpanel);
		
		/*リスト*/
		kwlist = new JList();
		kwlist.setLayoutOrientation(JList.VERTICAL_WRAP);
		kwlist.setFixedCellWidth(120);
		kwlist.setVisibleRowCount(7);
		
		kwlist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				bkwedit.setEnabled(true);
				bkwdel.setEnabled(true);
			}
		});
		
		kwscroll = new JScrollPane(kwlist);
		kwscroll.setBounds(5,25,360,145);
		listpanel.add(kwscroll);
		
		/*キーワード追加ボタン*/
		bkwadd = new JButton(LeafLangManager.get("Add","追加"));
		bkwadd.setBounds(10,175,80,20);
		listpanel.add(bkwadd);
		
		bkwadd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addKeyword();
			}
		});
		
		/*キーワード編集ボタン*/
		bkwedit = new JButton(LeafLangManager.get("Edit","編集"));
		bkwedit.setBounds(100,175,80,20);
		listpanel.add(bkwedit);
		
		bkwedit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				editKeyword();
			}
		});
		
		/*キーワード削除ボタン*/
		bkwdel = new JButton(LeafLangManager.get("Delete","削除"));
		bkwdel.setBounds(190,175,80,20);
		listpanel.add(bkwdel);
		
		bkwdel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeKeyword();
			}
		});
		
		/*コメント符号入力*/
		companel = new JPanel(null);
		companel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("CommentMarks","コメント符号")
		));
		companel.setBounds(5,200,360,50);
		listpanel.add(companel);
		
		/*ブロック開始符号ボタン*/
		bcstart = new JButton(LeafLangManager.get("Start","開始符号"));
		bcstart.setBounds(20,20,100,20);
		companel.add(bcstart);
		
		bcstart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setMark("Comment Block Start","コメントブロック開始",BLOCK_COMMENT_START);
			}
		});
		
		/*ブロック終了符号ボタン*/
		bcend = new JButton(LeafLangManager.get("End","終了符号"));
		bcend.setBounds(130,20,100,20);
		companel.add(bcend);
		
		bcend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setMark("Comment Block End","コメントブロック終了",BLOCK_COMMENT_END);
			}
		});
		
		/*行コメント開始符号ボタン*/
		bcline = new JButton(LeafLangManager.get("Line","行コメント"));
		bcline.setBounds(240,20,100,20);
		companel.add(bcline);
		
		bcline.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setMark("Line Comment Start","行コメント開始",LINE_COMMENT_START);
			}
		});
		
		/*配色設定ボタン*/
		bcol = new JButton(LeafLangManager.get("Colors","配色"));
		bcol.setBounds(25,295,100,20);
		add(bcol);
		
		bcol.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(dialog.showDialog() == OK_OPTION){
					colors = dialog.getResult();
				}
			}
		});
		
		/*OKボタン*/
		bok = new JButton("OK");
		bok.setBounds(135,295,100,20);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		/*キャンセルボタン*/
		bcan = new JButton(LeafLangManager.get("Cancel","キャンセル"));
		bcan.setBounds(245,295,100,20);
		add(bcan);
		
		bcan.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		
		update();
	}
	/**
	*新しいキーワードを追加します。
	*/
	private void addKeyword(){
		String word = JOptionPane.showInputDialog(this,"",
			LeafLangManager.get("Add Keyword","キーワードを追加"),
			JOptionPane.PLAIN_MESSAGE
		);
		if(word!=null)addKeyword(word);
	}
	/**
	*選択されているキーワードを編集します。
	*/
	private void editKeyword(){
		String word = (String)kwlist.getSelectedValue();
		keywords.remove(word);
		word = (String)JOptionPane.showInputDialog(this,"",
			LeafLangManager.get("Edit Keyword","キーワードを編集"),
			JOptionPane.PLAIN_MESSAGE,null,null,word
		);
		if(word!=null)addKeyword(word);
	}
	/**
	*指定されたキーワードをマップに追加します。
	*すでに同じキーワードがある場合キーは追加されません。
	*@param word 追加するキーワード
	*/
	private void addKeyword(String word){
		if(!keywords.contains(word)){
			keywords.add(word);
			try{
				Collections.sort(keywords);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			kwlist.setListData(keywords.toArray());
		}else{
			JOptionPane.showMessageDialog(this,LeafLangManager.get(
				"Exists the same keyword","同一のキーワードが登録されています")
			);
		}
		kwlist.setSelectedValue(word,true);
		kwlist.repaint();
	}
	/**
	*選択されているキーワードを削除します。
	*/
	private void removeKeyword(){
		
		keywords.remove((String)kwlist.getSelectedValue());
		int index = kwlist.getSelectedIndex();
		kwlist.setListData(keywords.toArray());
		kwlist.setSelectedIndex(Math.min(index,keywords.size()-1));
		kwlist.ensureIndexIsVisible(index);
		bkwdel.setEnabled(keywords.size()>0);
		bkwedit.setEnabled(bkwdel.isEnabled());
	}
	/**
	*キーワード符号を設定します。
	*@param eng 英語での設定項目の表現
	*@param jpn 日本語での設定項目の表現
	*@param type 種別
	*/
	private void setMark(String eng, String jpn, int type){
		
		String mark;
		switch(type){
			case BLOCK_COMMENT_START: mark = set.getCommentBlockStart();break;
			case BLOCK_COMMENT_END  : mark = set.getCommentBlockEnd();break;
			default : mark = set.getCommentLineStart();break;
		}
		mark = (String)JOptionPane.showInputDialog(this,"",
			LeafLangManager.get(eng,jpn),JOptionPane.PLAIN_MESSAGE,null,null,mark);
		if(mark==null)return;
		switch(type){
			case BLOCK_COMMENT_START : set.setCommentBlockStart(mark);break;
			case BLOCK_COMMENT_END   : set.setCommentBlockEnd(mark);break;
			default: set.setCommentLineStart(mark);break;
		}
	}
	/**
	*キーワードセットを追加します。
	*/
	private void addSet(){
		
		String name = (String)JOptionPane.showInputDialog(this,"",
			LeafLangManager.get("Name","名前"),JOptionPane.PLAIN_MESSAGE,null,null,"");
		if(name==null)return;
		for(KeywordSet set : sets){
			if(set.getName().equals(name)){
				JOptionPane.showMessageDialog(this,LeafLangManager.get(
					"Exists the same name keyword set",
					"同名キーワードセットが登録されています"));
				return;
			}
		}
		set = new KeywordSet(name);
		sets.add(set);
		setmodel.addElement(set);
		setcomb.setSelectedItem(set);
		update();
	}
	/**
	*選択されているキーワードセットを削除します。
	*/
	private void removeSet(){
		
		int ok = JOptionPane.showConfirmDialog(this,
			LeafLangManager.get("Are you sure?","削除してもよろしいですか？"),
			LeafLangManager.get("Delete Keyword Set","キーワードセットを削除"),
			JOptionPane.YES_NO_OPTION);
		if(ok==JOptionPane.YES_OPTION){
			sets.remove(set);
			setmodel.removeElement(set);
			update();
		}
	}
	/**
	*拡張子を設定します。
	*/
	private void setExtension(){
		
		String exts = LeafArrayManager.toString(";",set.getExtensions());
		exts = (String)JOptionPane.showInputDialog(this,
			LeafLangManager.get("Separator : semicolon","区切り文字：セミコロン"),
			LeafLangManager.get("Extensions","拡張子"),
			JOptionPane.PLAIN_MESSAGE,null,null,exts);
		if(exts!=null)
			set.setExtensions(LeafArrayManager.toList(";",exts));
	}
	/**
	*表示を更新します。
	*/
	private void update(){
		if(set!=null)
			keywords = set.getKeywords();
		else
			keywords = new ArrayList<String>();
		kwlist.setListData(keywords.toArray());
		bsdel.setEnabled(sets.size()>0);
		bsext.setEnabled(bsdel.isEnabled());
		boolean enable = (set!=null);
		bkwadd.setEnabled(enable);
		bkwedit.setEnabled(false);
		bkwdel.setEnabled(false);
		bcstart.setEnabled(enable);
		bcend.setEnabled(enable);
		bcline.setEnabled(enable);
	}
	/**
	*設定ダイアログを表示します。OKボタンで閉じられた場合設定をマネージャに適用します。
	*@return OKボタンで閉じられた場合true
	*/
	public boolean showDialog(){
		setVisible(true);
		if(isChanged){
			manager.setKeywordSets(sets);
			manager.setColorMap(colors);
		}
		return isChanged;
	}
	/**
	*ユーザーにより編集されたキーワードセットのリストを返します。
	*/
	public List<KeywordSet> getKeywordSets(){
		return sets;
	}
}