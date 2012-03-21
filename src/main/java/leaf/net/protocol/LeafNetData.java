/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.protocol;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.List;

/**
*LeafNetAPIで送受信されるデータのコンテナです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年3月7日
*/
public class LeafNetData{
	
	private String agentName, command;
	private InetAddress source;
	private List<InetAddress> path;
	private Object data;
	
	/**
	*空のコンテナを生成します。
	*/
	public LeafNetData(){
		agentName = null;
		command   = null;
		this.data = null;
	}
	/**
	*データを指定してコンテナを生成します。
	*@param data データ
	*/
	public LeafNetData(Object data){
		this(null, null, data);
	}
	/**
	*エージェントとコマンド、データを指定してコンテナを生成します。
	*@param agent エージェントの識別名
	*@param cmd   コマンド
	*@param data  データ
	*/
	public LeafNetData(String agent, String cmd, Object data){
		agentName = agent;
		command   = cmd;
		this.data = data;
	}
	/**
	*データを受け取るエージェントを指定します。
	*@param agent エージェントの識別名
	*/
	public void setAgentName(String agent){
		agentName = agent;
	}
	/**
	*データを受け取るエージェントの識別名を返します。
	*@return エージェントの識別名
	*/
	public String getAgentName(){
		return agentName;
	}
	/**
	*P2P通信用にデータの流通経路を指定します。
	*@param path 流通経路
	*/
	public void setPath(List<InetAddress> path){
		this.path = path;
	}
	/**
	*P2P通信用のデータの流通経路を返します。
	*@return 流通経路
	*/
	public List<InetAddress> getPath(){
		return path;
	}
	/**
	*エージェントの実行するコマンドを指定します。
	*@param cmd コマンド
	*/
	public void setCommand(String cmd){
		command = cmd;
	}
	/**
	*エージェントの実行するコマンドを返します。
	*@return コマンド
	*/
	public String getCommand(){
		return command;
	}
	/**
	*データを添付します。
	*@param data データ
	*/
	public void setData(Object data){
		this.data = data;
	}
	/**
	*添付データを返します。
	*@return データ
	*/
	public Object getData(){
		return data;
	}
	/**
	*XMLデータをデータコンテナに変換します。
	*@param xml XML文字列
	*@return データコンテナ 例外発生時はnull
	*/
	public static LeafNetData decode(Object xml){
		ByteArrayInputStream bstream = null;
		XMLDecoder decoder = null;
		try{
			byte[] bytes = xml.toString().getBytes("UTF8");
			bstream = new ByteArrayInputStream(bytes);
			decoder = new XMLDecoder(bstream);
			decoder.close(); //必須
			return (LeafNetData)decoder.readObject();
		}catch(Exception ex){
			return null;
		}
	}
	/**
	*データコンテナをXMLデータに変換します。
	*@param data データコンテナ
	*@return XMLデータ 例外発生時はnull
	*/
	public static String encode(LeafNetData data){
		ByteArrayOutputStream bstream = null;
		XMLEncoder encoder = null;
		try{
			bstream = new ByteArrayOutputStream();
			encoder = new XMLEncoder(bstream);
			encoder.writeObject(data);
			encoder.close(); //必須
			return bstream.toString("UTF8");
		}catch(Exception ex){
			return null;
		}
	}
}
