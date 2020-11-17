package javafx_nio_async_chat_client;

import java.net.URL;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.fxml.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import javafx.collections.ListChangeListener;

public class Controller implements Initializable {
    public TextField msg_tf;
    public TextField ip_tf;
    public TextField port_tf;
    public TextField nick_tf;
    public TextArea ta;
    public ListView lv;
    //public ObservableList user_list;
    public Label user_num_label;
    public Button send_btn;
    public Button connect_btn;
    public Button disconnect_btn;

    public void initialize(URL location, ResourceBundle resources) {
        // test
        port_tf.setText("5001");

        MyDatabase.msg_tf = msg_tf;
        MyDatabase.ip_tf = ip_tf;
        MyDatabase.port_tf = port_tf;
        MyDatabase.nick_tf = nick_tf;
        MyDatabase.ta = ta;
        MyDatabase.lv = lv;
        //MyDatabase.user_list = user_list;
        MyDatabase.user_num_label = user_num_label;
        MyDatabase.send_btn = send_btn;
        MyDatabase.connect_btn = connect_btn;
        MyDatabase.disconnect_btn = disconnect_btn;

        MyDatabase.init();
        MyDatabase.setDisconnectMode();

        ta.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                // Platform.runLater(()->{
                //     // use Double.MAX_VALUE to scroll to the bottom
                //     // use Double.MIN_VALUE to scroll to the top
                //     ta.setScrollTop(Double.MAX_VALUE);
                // });
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ta.setScrollTop(Double.MAX_VALUE);
                    }
                });
            }
        });

        lv.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        user_num_label.setText("users online : " + lv.getItems().size());
                    }
                });
            }
        });

        return;
    }

    public void sendMessage(ActionEvent actionEvent) {
        MyDatabase.sendMsg(msg_tf.getText());
    }

    public void enterPressed(ActionEvent actionEvent) {
        MyDatabase.sendMsg(msg_tf.getText());
    }

    public void connect(ActionEvent actionEvent) {
        MyDatabase.ip_address = ip_tf.getText();
        MyDatabase.port_number = port_tf.getText();
        MyDatabase.nickname = nick_tf.getText();
        MyDatabase.client = new ClientExample(MyDatabase.ip_address, MyDatabase.port_number, MyDatabase.nickname);
        MyDatabase.client.startClient();
        return;
    }

    public void disconnect(ActionEvent actionEvent) {
        lv.getItems().clear();
        msg_tf.setText("");
        ta.setText("");

        MyDatabase.setDisconnectMode();

        MyDatabase.client.stopClient();
        return;
    }
}