/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebanotify;

import java.awt.Dimension;
import java.io.File;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import nicon.notify.core.NiconEvent;
import nicon.notify.core.Notification;
import nicon.notify.core.server.ServerOSD;
import nicon.notify.gui.desktopNotify.DesktopNotify;
import nicon.notify.gui.themes.NiconDarkTheme;

/**
 *
 * @author LAB-315
 */
public class PruebaNotify {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//            UIManager.setLookAndFeel(new NimbusLookAndFeel());
//            int respuesta=Notification.showConfirm("Titulo", "Mensaje",Notification.NICON_DARK_THEME);

//        ServerOSD serverOSD = ServerOSD.getInstance();
//        NiconEvent ne = new NiconEvent("Criminal", "Rise and fall\n(3:00)", Notification.DEFAULT_MESSAGE);
//        DesktopNotify dn = new DesktopNotify(ne, new File("01.jpg").getAbsolutePath());
//        
//        serverOSD.send(dn, 5000);

        
        Notification.show("Criminal", "Rise and fall", new File("01.jpg"), 10000, new Dimension(100 ,100));

        
//        Notification.show("Titulo", "Mensaje", Notification.NICON_LIGHT_THEME, Notification.IMAGE_ICON, 2000);
        
//            if (respuesta == 1) {
//                System.out.println("NO");
//            } else {
//                System.out.println("SI");
//            }

    }

}
