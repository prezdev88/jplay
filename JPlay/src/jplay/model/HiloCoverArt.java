/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jplay.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
/**
 *
 * @author pperezp
 */
public class HiloCoverArt extends Thread {
    private JLabel lbl1;
    private JLabel lbl2;
    private List<ImageIcon> imagenes;
    private int pauseMover;// el pause para mover la x de la foto
    private int pauseEntreFotos; // pause entre cada fotos

    public HiloCoverArt(JLabel lbl1, JLabel lbl2, List<ImageIcon> imagenes, int pauseMover, int pauseEntreFotos) {
        this.lbl1 = lbl1;
        this.lbl2 = lbl2;
        this.imagenes = imagenes;
        this.pauseMover = pauseMover;
        this.pauseEntreFotos = pauseEntreFotos;
    }
    
    @Override
    public void run() {
        
        int cont; // contador de ciclo para mover la foto
        int x;// variable para mover el segundo label
        int x2; // variable para poder mover el primer label (esta comentado)
        int anchoFoto = 130;

        try {
            lbl1.setIcon(imagenes.get(0));
            while (true) {
                for (int i = 0; i < imagenes.size(); i++) {
                    Thread.sleep(pauseEntreFotos);

                    lbl1.setIcon(imagenes.get(i));

                    // si eñ siguiente es mayor o igual al limite
                    // quiere decir que me salgo del rango
                    // por ende, dejo la primera foto
                    if ((i + 1) >= imagenes.size()) {
                        lbl2.setIcon(imagenes.get(0));
                    } else {
                        // de lo contrario, dejo la foto siguiente en 
                        // el label 2sjkdfhalskj sdklf s
                        lbl2.setIcon(imagenes.get(i + 1));
                    }

                    x = anchoFoto;
                    x2 = 0;
                    cont = 0;

                    while (cont < x) {

                        // descomentar para ver el efecto
                                lbl1.setBounds(x2--, lbl1.getY(), 168, 168);
                                lbl2.setBounds(x--, lbl2.getY(), 168, 168);
                        lbl2.setLocation(x--, lbl2.getY());
//                                NewJFrame.this.repaint();
                        Thread.sleep(pauseMover);
                    }

                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Hilo covert art interrumpido");
        }
    }
}
