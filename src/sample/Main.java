package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 程序的入口类，主要执行窗口的建立，以及加载布局
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 加载布局文件
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        // 设置窗口标题
        primaryStage.setTitle("文本相似度");
        // 设置窗口图标
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("res/images/compare.png"))));
        // 设置窗口初始大小
        Scene scene = new Scene(root, 800, 600);
        // 加载样式表
        scene.getStylesheets().add(getClass().getResource("sample.css").toExternalForm());
        // 应用以上设置并显示窗口
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
