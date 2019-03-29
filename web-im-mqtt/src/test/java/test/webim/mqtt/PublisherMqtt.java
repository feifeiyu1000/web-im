package test.webim.mqtt;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 
 * Title:Server Description: 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 * 
 */
public class PublisherMqtt {

	// tcp://MQTT安装的服务器地址:MQTT定义的端口号
	public static final String HOST = "tcp://localhost:1883";
	// 定义一个主题
	public static final String TOPIC = "test/t1";
	//共享订阅topic,注：1.EMQ3集群模式下是否支持?EMQ3.0之后已支持：https://github.com/emqx/emqx/issues/1689
	public static final String SHARE_TOPIC = "$queue/" + TOPIC;

	// 定义MQTT的ID，可以在MQTT服务配置中指定
	private static final String clientid = "test-pub";

	private MqttClient client;
	private MqttTopic topic11;
	private String userName = "mosquitto";
	private String passWord = "mosquitto";

	private MqttMessage message;

	/**
	 * 构造函数
	 * 
	 * @throws MqttException
	 */
	public PublisherMqtt() throws MqttException {
		// MemoryPersistence设置clientid的保存形式，默认为以内存保存
		client = new MqttClient(HOST, clientid, new MemoryPersistence());
		connect();
	}

	/**
	 * 用来连接服务器
	 */
	private void connect() {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName(userName);
		options.setPassword(passWord.toCharArray());
		// 设置超时时间
		options.setConnectionTimeout(10);
		// 设置会话心跳时间
		options.setKeepAliveInterval(20);
		try {
			client.setCallback(new PushCallback());
			client.connect(options);

			topic11 = client.getTopic(TOPIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param topic
	 * @param message
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
		MqttDeliveryToken token = topic.publish(message);
		token.waitForCompletion();
		System.out.println("message is published completely! " + token.isComplete() + "---" + message.toString());
	}

	/**
	 * 启动入口
	 * 
	 * @param args
	 * @throws MqttException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MqttException, InterruptedException {
		PublisherMqtt server = new PublisherMqtt();
		for (int i = 0; i < 100; i++) {
			server.message = new MqttMessage();
			server.message.setQos(2);
			server.message.setRetained(true);
			server.message.setPayload(("hello:" + i + " at " + new Date()).getBytes());
			server.publish(server.topic11, server.message);
			System.out.println(server.message.isRetained() + "------ratained状态");
			Thread.sleep(10000);
		}
	}
}