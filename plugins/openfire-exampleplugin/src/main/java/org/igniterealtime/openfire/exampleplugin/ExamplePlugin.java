package org.igniterealtime.openfire.exampleplugin;

import java.io.File;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/**
 * 过滤插件:当body中有fuck时，将消息截断，不转发消息。
 *
 */
public class ExamplePlugin implements Plugin ,PacketInterceptor{
    // A：生成一个日志实例，用于打印日志，日志被打印在openfire_src\target\openfire\logs目录中
    private static final Logger Log = LoggerFactory.getLogger(ExamplePlugin.class);
    //B: 消息拦截器
    private InterceptorManager interceptorManager;

    //C: 插件初始化函数
    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {

        Log.info("MessageFilter init");
        // 将当前插件加入到消息拦截管理器（interceptorManager ）中，当消息到来或者发送出去的时候，会触发本插件的interceptPacket方法。
        interceptorManager = InterceptorManager.getInstance();
        interceptorManager.addInterceptor(this);
    }

    //D: 插件销毁函数
    @Override
    public void destroyPlugin() {
        Log.info("MessageFilter destory");
        // 当插件被卸载的时候，主要通过openfire管理控制台卸载插件时，被调用。注意interceptorManager的addInterceptor和removeInterceptor需要成对调用。
        interceptorManager.removeInterceptor(this);
    }

    //E 插件拦截处理函数
    @Override
    public void interceptPacket(Packet packet, Session session,
                                boolean incoming, boolean processed)
        throws PacketRejectedException {
        // incoming表示本条消息刚进入openfire。processed为false，表示本条消息没有被openfire处理过。这说明这是一条处女消息，也就是没有被处理过的消息。
        if (incoming && processed == false) {
            // packet可能是IQ、Presence、Message，这里当packet是message的时候，进行处理。
            if (packet instanceof Message) {
                // 将packet强制转换为Message
                Message msg = (Message)packet;
                // 取得message中的body内容，就是消息正文
                String body = msg.getBody();
                // 如果内容中包含fuck，则拒绝处理消息
                if(body != null  && body.contains("fuck")){
                    // F: 这里通过抛出异常的方式，来阻止程序流程继续执行下去。
                    PacketRejectedException rejectedException =  new PacketRejectedException();

                    rejectedException.setRejectionMessage("fuck is error");

                    throw rejectedException;
                }

            }
        }

    }

}
