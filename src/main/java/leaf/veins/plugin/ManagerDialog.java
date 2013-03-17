/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.swing.dialog.LeafDialog;

/**
 * モジュールの管理を行うためのダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/09/27 
 *
 */
public class ManagerDialog extends LeafDialog {
	private static final long serialVersionUID = 1L;
	private DefaultListModel<ModuleInfo> model_modules;
	private JList<ModuleInfo> list_modules;
	private JScrollPane scroll_modules;
	private JButton button_cfg;
	private JButton button_add;
	private JButton button_del;
	private JButton button_close;
	private ModuleManager manager;
	private JFileChooser chooser;
	
	/**
	 * 所有者となるFrameとManagerを指定してダイアログを構築します。
	 * 
	 * @param owner ダイアログを所有するFrame
	 * @param manager モジュールを管理するManager
	 */
	public ManagerDialog(Frame owner, ModuleManager manager) {
		super(owner, null, true);
		setLayout(null);
		setResizable(false);
		this.manager = manager;
		chooser = new JFileChooser();
		initialize();
	}
	
	/**
	 * 所有者となるDialogとManagerを指定してダイアログを構築します。
	 * 
	 * @param owner ダイアログを所有するDialog
	 * @param manager モジュールを管理するManager
	 */
	public ManagerDialog(Dialog owner, ModuleManager manager) {
		super(owner, null, true);
		setLayout(null);
		setResizable(false);
		this.manager = manager;
		chooser = new JFileChooser();
		initialize();
	}
	
	@Override
	public void initialize() {
		getContentPane().removeAll();
		setTitle(translate("title"));
		
		model_modules = new DefaultListModel<>();
		list_modules = new JList<>(model_modules);
		scroll_modules = new JScrollPane(list_modules,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		add(scroll_modules);
		list_modules.addListSelectionListener(new ListHandler());
		
		button_cfg = new JButton(new ConfigAction());
		button_add = new JButton(new AddAction());
		button_del = new JButton(new DelAction());
		button_close = new JButton(new CloseAction());
		
		add(button_cfg);
		add(button_add);
		add(button_del);
		add(button_close);
		
		layoutComponents();
		updateModuleList();
		
		String d = translate("jar_archive");
		FileFilter filter = new FileNameExtensionFilter(d, "jar");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.resetChoosableFileFilters();
		chooser.addChoosableFileFilter(filter);
	}
	
	private void layoutComponents() {
		int h_button = button_close.getPreferredSize().height;
		int y_comp = 10;
		
		scroll_modules.setBounds(10, y_comp, 330, 200);
		
		y_comp += scroll_modules.getHeight() - h_button;
		button_close.setBounds(350, y_comp, 100, h_button);
		
		y_comp -= h_button + 5;
		button_del.setBounds(350, y_comp, 100, h_button);
		
		y_comp -= h_button + 5;
		button_add.setBounds(350, y_comp, 100, h_button);
		
		y_comp -= h_button + 5;
		button_cfg.setBounds(350, y_comp, 100, h_button);
		
		y_comp = button_close.getY() + h_button + 20;
		setContentSize(new Dimension(460, y_comp));
	}
	
	private void updateModuleList() {
		model_modules.clear();
		for(ModuleInfo info : manager.getAllModuleInfo()) {
			model_modules.addElement(info);
		}
	}
	
	private class ListHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			ModuleInfo info = list_modules.getSelectedValue();
			if(info != null) {
				Module g = info.getModule();
				button_cfg.setEnabled(g.hasConfigurationDialog());
				button_del.setEnabled(true);
			} else {
				button_cfg.setEnabled(false);
				button_del.setEnabled(false);
			}
		}
	}
	
	private class ConfigAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public ConfigAction() {
			super(translate("button_cfg"));
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ModuleInfo info = list_modules.getSelectedValue();
			Module g = info.getModule();
			JDialog o = ManagerDialog.this;
			JDialog d = g.createConfigurationDialog(o);
			if(d != null) d.setVisible(true);
		}
	}
	
	private class AddAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public AddAction() {
			super(translate("button_add"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog o = ManagerDialog.this;
			int result = chooser.showOpenDialog(o);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				try {
					URL url = file.toURI().toURL();
					manager.findAndLoadModules(url);
					updateModuleList();
				} catch (MalformedURLException ex) {
				} catch (IOException ex) {
				}
			}
		}
	}
	
	private class DelAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public DelAction() {
			super(translate("button_del"));
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ModuleInfo info = list_modules.getSelectedValue();
			manager.removeModuleInfo(info);
			updateModuleList();
		}
	}
	
	private class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public CloseAction() {
			super(translate("button_close"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ManagerDialog.this.dispose();
		}
	}

}
