package com.yb.socket;

import com.alibaba.fastjson.JSONObject;
import com.yb.socket.listener.DefaultMqttMessageEventListener;
import com.yb.socket.pojo.MqttRequest;
import com.yb.socket.service.SocketType;
import com.yb.socket.service.WrappedChannel;
import com.yb.socket.service.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

/**
 * @author daoshenzzg@163.com
 * @date 2018/12/30 13:36
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Robot MQTT Server Start---");

        Server server = new Server();
        server.setPort(40005);
        server.setOpenCount(true);
        server.setCheckHeartbeat(true);
        server.setOpenStatus(true);
        server.addEventListener(new EchoMessageEventListener());
        server.setSocketType(SocketType.MQTT);
        server.bind();

        //模拟推送
//        JSONObject message = new JSONObject();
//        message.put("action", "echo");
//        message.put("message", "this is yb push message!");
//
//        MqttRequest mqttRequest = new MqttRequest((message.toString().getBytes()));
//        while (true) {
//            if (server.getChannels().size() > 0) {
////                logger.info("模拟推送消息");
//                for (WrappedChannel channel : server.getChannels().values()) {
//                    server.send(channel, "yb/notice/", mqttRequest);
//                }
//            }
//            Thread.sleep(1000L);
//        }

    }

    public static class EchoMessageEventListener extends DefaultMqttMessageEventListener {

        @Override
        public void publish(WrappedChannel channel, MqttPublishMessage msg) {
            String topic = msg.variableHeader().topicName();
            ByteBuf buf = msg.content().duplicate();
            byte[] tmp = new byte[buf.readableBytes()];
            buf.readBytes(tmp);
            String content = new String(tmp);

            MqttPublishMessage sendMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttPublishVariableHeader(topic, 0),
                    Unpooled.buffer().writeBytes(new String(content.toUpperCase()).getBytes()));
            channel.writeAndFlush(sendMessage);
        }
    }

}
