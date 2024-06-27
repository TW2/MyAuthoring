package org.wingate.myauth;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.EventQueue;

/**
 *
 * @author util2
 */
public class Myauth {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        EventQueue.invokeLater(()->{
            FlatDarkLaf.setup();
            Viewer v = new Viewer();
            v.setSize(1280, 720);
            v.setLocationRelativeTo(null);
            v.setTitle("MyAuth");
            v.setVisible(true);
        });
    }
}
