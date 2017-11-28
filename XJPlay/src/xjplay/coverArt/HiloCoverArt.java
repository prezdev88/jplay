package xjplay.coverArt;

import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import xjplay.model.rules.Rules;
/**
 *
 * @author pperezp
 */
public class HiloCoverArt extends Thread {
    private JLabel lbl1;
    private JLabel lbl2;
    private List<ImageIcon> imagenes;

    public HiloCoverArt(JLabel lbl1, JLabel lbl2, List<ImageIcon> imagenes) {
        this.lbl1 = lbl1;
        this.lbl2 = lbl2;
        this.imagenes = imagenes;
    }
    
    @Override
    public void run() {
        
        // <editor-fold defaultstate="collapsed" desc="Se comenta esto por el error de open gl en windows 7">  
//        int cont; // contador de ciclo para mover la foto
//        int x;// variable para mover el segundo label
//        int x2; // variable para poder mover el primer label (esta comentado)
//        int anchoFoto = 130;
        // </editor-fold >  
        try {
//            lbl1.setIcon(imagenes.get(0));
            while (true) {
                for (int i = 0; i < imagenes.size(); i++) {
                    lbl1.setIcon(imagenes.get(i));
                    Thread.sleep(Rules.PAUSE_ENTRE_FOTOS);
                    
                    // <editor-fold defaultstate="collapsed" desc="Se comenta esto por el error de open gl en windows 7">  
                    
                    // si eñ siguiente es mayor o igual al limite
                    // quiere decir que me salgo del rango
                    // por ende, dejo la primera foto
//                    if ((i + 1) >= imagenes.size()) {
//                        lbl2.setIcon(imagenes.get(0));
//                    } else {
//                        // de lo contrario, dejo la foto siguiente en 
//                        // el label 2
//                        lbl2.setIcon(imagenes.get(i + 1));
//                    }
//
//                    x = anchoFoto;
//                    x2 = 0;
//                    cont = 0;
//
//                    while (cont < x) {
//                        // descomentar para ver el efecto
////                                lbl1.setBounds(x2--, lbl1.getY(), 168, 168);
////                                lbl2.setBounds(x--, lbl2.getY(), 168, 168);
//                        lbl2.setLocation(x--, lbl2.getY());
////                                NewJFrame.this.repaint();
////                        Thread.sleep(Rules.PAUSE_MOVER);
//                    }

                    /*Se comenta esto por el error de open gl en windows 7*/
                    //</editor-fold>
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("Hilo covert art interrumpido");
        }
    }
}
