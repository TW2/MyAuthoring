package org.wingate.myauth;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.EventQueue;

/**
 *
 * @author util2
 */
public class Myauth {

    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            FlatLightLaf.setup();
            MainFrame mf = new MainFrame();
            mf.setSize(1900, 1000);
            mf.setLocationRelativeTo(null);
            mf.setTitle("MyAuthoring");
            mf.setVisible(true);
        });
    }
}
