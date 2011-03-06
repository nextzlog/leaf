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
package leaf.cursor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.components.text.LeafTextField;
import leaf.dialog.LeafDialog;
import leaf.dialog.LeafFileChooser;
import leaf.manager.LeafLangManager;

/**
*写真付きのカーソルに関する設定を行うためのモーダルダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月28日
*@see LeafCursorFactory
*/
public final class LeafCursorOptionDialog extends LeafDialog{
	
	/**秘匿フィールド*/
	private boolean isChanged = CANCEL_OPTION;
	private File file;
	private BufferedImage image,origin;
	private Rectangle trim,rect;
	
	/**GUI*/
	private JPanel borderpanel,viewpanel;
	private JCheckBox ch1;
	private ExLabel lbview;
	private JLabel lbsam;
	private LeafTextField tfpath;
	private JButton bopen,bok,bcancel;
	private final LeafFileChooser chooser;

	/**
	*親フレームを指定して設定ダイアログを生成します。
	*@param owner ダイアログの親フレーム
	*/
	public LeafCursorOptionDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親フレームと、デフォルトの画像ファイルを指定して設定ダイアログを生成します。
	*@param owner ダイアログの親フレーム
	*@param file デフォルトの画像ファイル
	*/
	public LeafCursorOptionDialog(Frame owner,File file){
		
		super(owner, null, true);
		getContentPane().setPreferredSize(new Dimension(350,300));
		pack();
		setResizable(false);
		setLayout(null);
		this.file = file;
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		
		chooser = new LeafFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			LeafLangManager.get("Images","画像"),"jpg","jpeg","png","gif"
		));
		
		init();
	}
	/**
	*ダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Cursor Options","カーソル設定"));
		getContentPane().removeAll();
		
		/*チェックボックス*/
		ch1 = new JCheckBox(LeafLangManager.get("Use a picture","画像を使用する"));
		ch1.setBounds(5,5,200,20);
		add(ch1);
		
		ch1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				enableButtons(ch1.isSelected());
			}
		});
		
		/*枠線*/
		borderpanel = new JPanel(null);
		borderpanel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Picture","カーソル画像")
		));
		borderpanel.setBounds(0,30,350,235);
		add(borderpanel);
		
		/*パス表示フィールド*/
		tfpath = new LeafTextField(100);
		tfpath.setBounds(10,25,200,20);
		tfpath.setEditable(false);
		borderpanel.add(tfpath);
		
		/*参照ボタン*/
		bopen = new JButton(LeafLangManager.get("Open","参照"));
		bopen.setBounds(230,25,80,20);
		borderpanel.add(bopen);
		
		bopen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFile();
			}
		});
		
		/*トリミングパレット*/
		lbview = new ExLabel();
		lbview.setBounds(10,20,180,130);
		ExMouseAdapter adapter = new ExMouseAdapter();
		lbview.addMouseListener(adapter);
		lbview.addMouseMotionListener(adapter);
		viewpanel = new JPanel();
		viewpanel.setLayout(null);
		viewpanel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Trim Palette","トリミングパレット")
		));
		viewpanel.setBounds(10,60,200,160);
		viewpanel.add(lbview);
		borderpanel.add(viewpanel);
		
		/*サンプル表示*/
		lbsam = new JLabel(LeafLangManager.get("Sample","サンプル"),JLabel.CENTER);
		lbsam.setBounds(220,60,115,40);
		lbsam.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		borderpanel.add(lbsam);
		
		/*OK/CNCELボタン*/
		bok = new JButton("OK");
		bok.setBounds(130,275,100,20);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		bcancel = new JButton(LeafLangManager.get("Cancel","キャンセル"));
		bcancel.setBounds(240,275,100,20);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		
		/*初期設定*/
		update();
	}
	/**トリミング*/
	private class ExMouseAdapter extends MouseAdapter{
		private int x = 0, y = 0;
		private double xratio = 1, yratio = 1;
		public void mousePressed(MouseEvent e){
			if(image != null){
				x = e.getX();
				y = e.getY();
				xratio = (double)image.getWidth() /lbview.getWidth();
				yratio = (double)image.getHeight()/lbview.getHeight();
			}
		}
		public void mouseDragged(MouseEvent e){
			if(image != null){
				rect  = new Rectangle(x,y,e.getX()-x,e.getY()-y);
				lbview.repaint();
			}
		}
		public void mouseReleased(MouseEvent e){
			if(image != null){
				try{
					rect  = null;
					trim  = new Rectangle(
						trim.x + (int)(x*xratio),
						trim.y + (int)(y*yratio),
						(int)((e.getX()-x)*xratio),
						(int)((e.getY()-y)*yratio)
					);
					image = origin.getSubimage(trim.x,trim.y,trim.width,trim.height);
					lbview.setIcon(new ImageIcon(image.getScaledInstance
						(lbview.getWidth(),lbview.getHeight(),Image.SCALE_SMOOTH)));
				}catch(Exception ex){ex.printStackTrace();}
				updateCursor();
			}
		}
	}
	/**トリミングパレット*/
	private class ExLabel extends JLabel{
		
		private final float[] pattern = {3,3,3,3};
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if(rect != null){
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(getBackground());
				g2.setXORMode(getForeground());
				BasicStroke stroke = new BasicStroke(1f,
					BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,1f,pattern,0f);
				g2.setStroke(stroke);
				g2.draw(rect);
			}
		}
	}
	/**ファイル参照*/
	private void openFile(){
		chooser.setSelectedFile(file);
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
			update();
		}
	}
	/**ボタン等を使用可能/不可能にする*/
	private void enableButtons(boolean opt){
		tfpath.setEnabled(opt);
		bopen.setEnabled(opt);
		lbview.setEnabled(opt);
	}
	/**新しいファイルに対する表示の更新*/
	private void update(){
		ch1.setSelected(file!=null);
		enableButtons(file!=null);
		tfpath.setText((file!=null)?file.getAbsolutePath():
			LeafLangManager.get("Unusable path","無効なファイルパス")
		);
		if(file!=null&&file.exists()){
			try{
				origin = image = ImageIO.read(file);
				lbview.setIcon(new ImageIcon(image.getScaledInstance
					(lbview.getWidth(),lbview.getHeight(),Image.SCALE_SMOOTH)
				));
				trim  = new Rectangle(0,0,image.getWidth(),image.getHeight());
			}catch(Exception ex){ex.printStackTrace();}
		}
		updateCursor();
	}
	/**トリミングパレットの画像変更時*/
	private void updateCursor(){
		lbsam.setCursor(createCursor(Cursor.DEFAULT_CURSOR));
	}
	/**
	*設定ダイアログを表示します。
	*@return OKボタンで閉じられた場合、{@link LeafDialog#OK_OPTION}を返します。
	*/
	public boolean showDialog(){
		super.setVisible(true);
		return isChanged;
	}
	/**
	*選択されたファイルを返します。「画像を使用しない」場合、nullが返されます。
	*@return 選択された画像ファイル
	*/
	public File getSelectedFile(){
		return (ch1.isSelected())?file:null;
	}
	/**
	*ユーザーにより設定されたトリミング座標を返します。
	*@return トリミング座標
	*/
	public Rectangle getTrimRect(){
		return trim;
	}
	/**
	*設定されたカーソルオブジェクトを生成して返します。
	*@param type カーソルのタイプ
	*@return カーソル
	*/
	public Cursor createCursor(int type){
		return LeafCursorFactory.createCursor(
			(ch1.isSelected())?file:null,trim,type
		);
	}
}
