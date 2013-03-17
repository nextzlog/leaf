/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.protocol;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * LeafNetAPIで送受信されるデータのコンテナです。
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年3月7日
 */
public class LeafNetData {
	private String agentName;
	private Object data;
	
	/**
	 * デコーダが使用するデフォルトコンストラクタです。
	 */
	public LeafNetData() {
		agentName = null;
		this.data = null;
	}
	
	/**
	 * データを指定してコンテナを生成します。
	 *
	 * @param data データ
	 */
	public LeafNetData(Object data) {
		agentName = null;
		this.data = data;
	}
	
	/**
	 * エージェントとデータを指定してコンテナを生成します。
	 * 
	 * @param agent エージェントの識別名
	 * @param data  データ
	 */
	public LeafNetData(String agent, Object data){
		agentName = agent;
		this.data = data;
	}
	
	/**
	 * データを受け取るエージェントを指定します。
	 *
	 * @param agent エージェントの識別名
	 */
	public void setAgentName(String agent) {
		agentName = agent;
	}
	
	/**
	 * データを受け取るエージェントの識別名を返します。
	 *
	 * @return エージェントの識別名
	 */
	public String getAgentName() {
		return agentName;
	}
	
	/**
	 * データを添付します。
	 *
	 * @param data データ
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * 添付データを返します。
	 *
	 * @return データ
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * XMLデータをデータコンテナに変換します。
	 *
	 * @param xml XML文字列
	 * @return データコンテナ 例外発生時はnull
	 */
	public static LeafNetData decode(Object xml) {
		ByteArrayInputStream bstream = null;
		XMLDecoder decoder = null;
		try {
			byte[] bytes = xml.toString().getBytes("UTF8");
			bstream = new ByteArrayInputStream(bytes);
			decoder = new XMLDecoder(bstream);
			return (LeafNetData)decoder.readObject();
		} catch(Exception ex) {
			return null;
		} finally {
			decoder.close();
		}
	}
	
	/**
	 * データコンテナをXMLデータに変換します。
	 *
	 * @param data データコンテナ
	 * @return XMLデータ 例外発生時はnull
	 */
	public static String encode(LeafNetData data) {
		ByteArrayOutputStream bstream = null;
		XMLEncoder encoder = null;
		try {
			bstream = new ByteArrayOutputStream();
			encoder = new XMLEncoder(bstream);
			encoder.writeObject(data);
			return bstream.toString("UTF8");
		} catch(Exception ex) {
			return null;
		} finally {
			encoder.close();
		}
	}

}
