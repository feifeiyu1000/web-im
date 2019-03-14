package com.reed.webim.netty.socketio.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.reed.webim.netty.socketio.config.NettySocketioServerConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NettySocketioServer {

	@Autowired
	private NettySocketioServerConfig nettyServerconfig;

	private SocketIOServer server;

	@Bean
	public SocketIOServer socketIOServer() {
		Configuration config = new Configuration();
		// config.setHostname(nettyServerconfig.host);
		config.setPort(nettyServerconfig.port);
		config.setBossThreads(nettyServerconfig.bossThreadNum);
		config.setWorkerThreads(nettyServerconfig.workerThreadNum);
		//config.setStoreFactory(new HazelcastStoreFactory());
		// 该处可以用来进行身份验证
		config.setAuthorizationListener(new AuthorizationListener() {
			@Override
			public boolean isAuthorized(HandshakeData data) {
				// http://localhost:8081?username=test&password=test
				// 例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
				// String username = data.getSingleUrlParam("username");
				// String password = data.getSingleUrlParam("password");
				return true;
			}
		});

		server = new SocketIOServer(config);
		log.info("=======Netty config is:{}=========", nettyServerconfig);
		return server;
	}

	@Bean
	public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
		return new SpringAnnotationScanner(socketIOServer);
	}

}
