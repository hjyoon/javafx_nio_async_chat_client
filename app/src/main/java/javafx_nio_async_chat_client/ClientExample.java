package javafx_nio_async_chat_client;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.*;
import java.text.*;
import java.net.*;
import java.net.http.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;

import com.google.gson.*;

import javafx.application.Platform;

class ClientExample {
    AsynchronousChannelGroup acg;
    AsynchronousSocketChannel asc;

    String ip_address;
    String port_number;
    String nickname;

    ClientExample(String ip_address, String port_number, String nickname) {
        this.ip_address = ip_address;
        this.port_number = port_number;
        this.nickname = nickname;
    }

    void startClient() {
        try {
            acg = AsynchronousChannelGroup.withFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                Executors.defaultThreadFactory()
            );

            asc = AsynchronousSocketChannel.open(acg);

            // connect(SocketAddress remote, A attachment, new CompletionHandler<Void, ? super A> handler)
            asc.connect(new InetSocketAddress(ip_address, Integer.parseInt(port_number)), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    try {
                        System.out.println("[success connection : "  + asc.getRemoteAddress() + "]");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                MyDatabase.setConnectMode();
                                MyDatabase.sendSetNickname();
                            }
                        }); 
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    receive();
                }

                @Override
                public void failed(Throwable e, Void attachment) {
                    System.out.println("[fail to connect to server]");
                    if(asc.isOpen()) {
                        stopClient();
                    }
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stopClient() {
        try {
            if(acg != null && !acg.isShutdown()) {
                System.out.println("[stopClient -> shutdownNow]");
                acg.shutdownNow();
                //asc.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // read(ByteBuffer dst, A attachment, new CompletionHandler<Integer, ? super A> handler)
        asc.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                try {
                    attachment.flip();
                    Charset charset = Charset.forName("utf-8");
                    String data = charset.decode(attachment).toString();

                    System.out.println("[success to receive] "  + data);
                    MyDatabase.receiveMsg(data);

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    asc.read(byteBuffer, byteBuffer, this);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("[fail to read(receive) from server]");
                stopClient();
            }
        });
    }

    void send(String data) {
        Charset charset = Charset.forName("utf-8");
        ByteBuffer byteBuffer = charset.encode(data);   // .encode() : String -> ByteBuffer

        // write(ByteBuffer src, A attachment, new CompletionHandler<Integer, ? super A> handler);
        asc.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                System.out.println("[success to send] "  + data);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("[fail to write(send) to server]");
                stopClient();
            }
        });
    }
}