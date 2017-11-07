package com.rabbitmq1.test;

import java.io.IOException;  

import com.rabbitmq.client.Channel;  
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;  
  


/**
 * 
 * RabbitMQ基础概念详细介绍
 * http://blog.csdn.net/whycold/article/details/41119807
 * 
 * 在 Windows 上安装Rabbit MQ 指南
 * http://www.cnblogs.com/shanyou/p/4067250.html
 * 
 * RabbitMQ入门教程之一--》Windows环境搭建RabbitMQ服务
 * http://blog.csdn.net/wwhrestarting/article/details/52882191
 * 
 * RabbitMQ使用场景练习
 * http://sheungxin.iteye.com/category/366754
 * 
 * 
 * RabbitMQ用户角色及权限控制
 * http://blog.csdn.net/zyz511919766/article/details/42292655
 * 
 * **/


/**
 * @author Administrator
 * 
 * RabbitMQ实例详解
 * http://blog.csdn.net/u012592062/article/details/51910955
 *
 */
public class Send {  
  
    private final static String QUEUE_NAME = "hello";  
      
    public static void main(String[] args) throws IOException {  
        // AMQP的连接其实是对Socket做的封装, 注意以下AMQP协议的版本号，不同版本的协议用法可能不同。  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("localhost");  
        Connection connection = factory.newConnection();  
        // 下一步我们创建一个channel, 通过这个channel就可以完成API中的大部分工作了。  
        Channel channel = connection.createChannel();  
          
        // 为了发送消息, 我们必须声明一个队列，来表示我们的消息最终要发往的目的地。  
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  
        String message = "Hello World!";  
        // 然后我们将一个消息发往这个队列。  
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());  
        System.out.println("[" + message + "]");  
          
        // 最后，我们关闭channel和连接，释放资源。  
        channel.close();  
        connection.close();  
    }  
}  
