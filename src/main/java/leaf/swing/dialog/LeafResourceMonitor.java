/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import javax.swing.*;
import java.util.List;

import leaf.swing.com.LeafMonitor;

/**
 * 実行環境のメモリ使用量を監視するダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年5月22日
 *
 */
@SuppressWarnings("serial")
public final class LeafResourceMonitor extends LeafDialog {
	private ResourceMonitor monitor;
	private List<MemoryPoolMXBean> mpools;
	private JComboBox<String> combo_pools;
	private DefaultComboBoxModel<String> model_pools;
	private JLabel label_usage;
	
	/**
	 * 親フレームを指定してダイアログを構築します。
	 * 
	 * @param owner ダイアログの親
	 */
	public LeafResourceMonitor(Frame owner) {
		super(owner, false);
		setContentSize(new Dimension(400, 240));
		setResizable(false);
		setLayout(new BorderLayout());
		
		mpools = ManagementFactory.getMemoryPoolMXBeans();
		
		initialize();
	}
	
	/**
	 * 親ダイアログを指定してダイアログを構築します。
	 * 
	 * @param owner ダイアログの親
	 */
	public LeafResourceMonitor(Dialog owner) {
		super(owner, false);
		setContentSize(new Dimension(400, 240));
		setResizable(false);
		setLayout(new BorderLayout());
		
		mpools = ManagementFactory.getMemoryPoolMXBeans();
		
		initialize();
	}
	
	@Override
	public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		model_pools = new DefaultComboBoxModel<>();
		combo_pools = new JComboBox<>(model_pools);
		
		for(MemoryPoolMXBean bean : mpools) {
			model_pools.addElement(bean.getName());
		}
		
		add(combo_pools, BorderLayout.NORTH);
		
		combo_pools.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int index = combo_pools.getSelectedIndex();
				monitor.setMXBean(mpools.get(index));
			}
		});
		
		monitor = new ResourceMonitor();
		add(monitor, BorderLayout.CENTER);
		monitor.setAutoSamplingEnabled(true);
		
		label_usage = new JLabel("", JLabel.RIGHT);
		label_usage.setPreferredSize(new Dimension(200, 30));
		add(label_usage, BorderLayout.SOUTH);
		
		if(!mpools.isEmpty()) monitor.setMXBean(mpools.get(0));
	}
	
	private class ResourceMonitor extends LeafMonitor {
		private MemoryPoolMXBean bean;
		
		public ResourceMonitor() {
			super(500);
		}
		
		public void setMXBean(MemoryPoolMXBean bean) {
			this.bean = bean;
			super.clear();
		}
		
		@Override
		public int sample() {
			if(bean == null) return 0;
			float used = bean.getUsage().getUsed();
			float total = bean.getUsage().getCommitted();
			
			int pc = (int) (100f * used / total);
			long k = (long) total / 1024;
			label_usage.setText(String.format("%d%%  %6d kB  ", pc, k));
			
			return (int)(500 * used / total);
		}
	}

}
