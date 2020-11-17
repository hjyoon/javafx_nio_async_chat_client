package javafx_nio_async_chat_client;

import java.util.*;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;

import javafx.application.Platform;

import com.google.gson.*;

class MyDatabase {
    public static TextField msg_tf;
    public static TextField ip_tf;
    public static TextField port_tf;
    public static TextField nick_tf;
    public static TextArea ta;
    public static ListView lv;
    //public static ObservableList user_list;
    public static Label user_num_label;
    public static Button send_btn;
    public static Button connect_btn;
    public static Button disconnect_btn;

    public static ClientExample client;
    public static String ip_address;
    public static String port_number;
    public static String nickname;

    public static List<Node> enable_after_connect;
    public static List<Node> enable_after_disconnect;

    public static Gson gson;

    public static void init() {
        // user_list = FXCollections.observableArrayList();
        // user_list.add("kim");
        // user_list.add("lee");
        // user_list.add("park");
        // user_list.add("choi");

        enable_after_connect = new ArrayList<>();
        enable_after_disconnect = new ArrayList<>();

        enable_after_connect.add(disconnect_btn);
        enable_after_connect.add(msg_tf);
        enable_after_connect.add(ta);
        enable_after_connect.add(send_btn);
        enable_after_connect.add(lv);
        enable_after_connect.add(user_num_label);

        enable_after_disconnect.add(ip_tf);
        enable_after_disconnect.add(port_tf);
        enable_after_disconnect.add(nick_tf);
        enable_after_disconnect.add(connect_btn);

        gson = new Gson();
    }

    public static void displayText(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ta.setText(ta.getText()+str);
            }
        });
    }

    public static void receiveMsg(String msg) {
        MyMessage from = gson.fromJson(msg, MyMessage.class);
        if(from.type.equals("print")) {
            MyDatabase.displayText(from.data);
        }
        else if(from.type.equals("newJoin")) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    MyDatabase.lv.getItems().add(from.data);
                }
            });
        }
        else if(from.type.equals("userList")) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    MyDatabase.lv.getItems().addAll(from.dataArr);
                }
            });
        }
        else if(from.type.equals("userLeave")) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    MyDatabase.lv.getItems().remove(from.data);
                }
            });
        }
    }

    public static void sendMsg(String msg) {
        if(msg.equals("")) {
            return;
        }
        msg_tf.setText("");

        MyMessage to = new MyMessage("userTalk", msg);
        MyDatabase.client.send(gson.toJson(to));
    }

    public static void sendSetNickname() {
        MyMessage to = new MyMessage("setNickname", nick_tf.getText());
        MyDatabase.client.send(gson.toJson(to));
    }

    public static void setConnectMode() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(Node i : enable_after_connect) {
                    i.setDisable(false);
                }
                for(Node i : enable_after_disconnect) {
                    i.setDisable(true);
                }
            }
        });
    }

    public static void setDisconnectMode() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(Node i : enable_after_connect) {
                    i.setDisable(true);
                }
                for(Node i : enable_after_disconnect) {
                    i.setDisable(false);
                }
            }
        });
    }
}