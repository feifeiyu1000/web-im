package test.netty.socketio.client;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.reed.webim.netty.socketio.handler.BaseAbstractHandler;
import com.reed.webim.netty.socketio.pojo.MessageInfo;
import com.reed.webim.netty.socketio.sdk.NettySocketIOClient;
import com.reed.webim.netty.socketio.sdk.NettySocketIOClientBuilder;
import com.reed.webim.netty.socketio.sdk.inner.ClientTypeEnum;
import com.reed.webim.netty.socketio.sdk.inner.InnerSocketIOClientConfig;
import com.reed.webim.netty.socketio.sdk.listener.TestMsgGetterListener;

/**
 * 测试通过SDK建立client
 */
public class TestSdkClient {

	private static final String url = "http://localhost:8080";
	public static final String clientId = "4";

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		MessageInfo msg = new MessageInfo(clientId, "1", null, "hello, I am " + clientId);
		InnerSocketIOClientConfig config = new InnerSocketIOClientConfig(ClientTypeEnum.TARGETSERVICE);
		config.setUrl(url);
		config.setBrokerEndpoint(BaseAbstractHandler.ENDPOINT_P2P);
		config.setClientType(ClientTypeEnum.TARGETSERVICE);
		config.setNameSpace("/ns1");
		// config.setChannel(ClientTypeEnum.TARGETSERVICE.getChannelName());

		NettySocketIOClient client = NettySocketIOClientBuilder.INSTANCE.buildClient(clientId, config,
				new TestMsgGetterListener());

		client.start();

		for (int i = 0; i < 100; i++) {
			Future<Boolean> r = client.sendMsg(BaseAbstractHandler.ENDPOINT_P2P, msg, true);
			printInfo(r);
			System.out.println("---wait for some time---");
			Thread.sleep(3000);
			printInfo(r);
			Thread.sleep(1000);
		}
	}

	public static void printInfo(Future<Boolean> r) throws InterruptedException, ExecutionException {
		System.out.println(new Date() + "---status is " + r.isDone() + "---result is " + r.get());
	}
}
