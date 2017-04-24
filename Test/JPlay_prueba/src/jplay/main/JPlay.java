package jplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons

import jplay.model.tableModel.TMCancion;
import jplay.model.tableModel.TMCancionBiblioteca;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import jplay.model.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import jplay.model.tableModel.IListaCanciones;
import jplay.model.tableModel.Tipo;
import jplay.model.tree.CellRender;

public class JPlay extends javax.swing.JFrame implements BasicPlayerListener{
    private Reproductor r;
    private int indiceActual;
    private Thread hiloRep;
    private Thread hiloCargar;
    private JPopupMenu popUpTree;
    private JPopupMenu popUpBiblio;
    private final String NOMBRE = "JPlay";
    private final String VERSION = "0.1a";
    private final boolean save = true;
    private List<Cancion> lFiltrada;
    private boolean isPlay;
    private boolean isStop;
    private boolean repetirCancion;
    private long bytesActual;
    private TMCancion modelCanciones;
    private TMCancionBiblioteca modelBib;
    
    public JPlay() {
        initComponents();
        
        modelCanciones = new TMCancion();
        modelBib = new TMCancionBiblioteca();
//        tablaBiblioteca.setModel(modelBib);
        
      
        crearArbol();
        listenerClickDerechoSobreArbol();
        crearPopUpTree();
        crearPopUpBiblioteca();
        
        btnCancelarCarga.setEnabled(false);
        indiceActual = -1;
        if(save) cargarSave();
        setLocationRelativeTo(null);
        this.setTitle(NOMBRE+" - "+VERSION);
        isPlay = false;
        isStop = true;
        repetirCancion = opRepetirCancion.isSelected();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCanciones = new javax.swing.JTable();
        lblInfoCarga = new javax.swing.JLabel();
        btnCancelarCarga = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaBiblioteca = new javax.swing.JTable();
        btnPause = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnExaminar = new javax.swing.JButton();
        lblArtista = new javax.swing.JLabel();
        slideTime = new javax.swing.JSlider();
        slideVol = new javax.swing.JSlider();
        lblTema = new javax.swing.JLabel();
        btnPause1 = new javax.swing.JButton();
        opRepetirCancion = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tablaCanciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaCanciones.setShowVerticalLines(false);
        tablaCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaCancionesMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tablaCanciones);

        lblInfoCarga.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCancelarCarga.setText("Cancelar carga");
        btnCancelarCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCargaActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(200);

        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeMouseReleased(evt);
            }
        });
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(tree);

        jSplitPane1.setLeftComponent(jScrollPane3);

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        tablaBiblioteca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaBiblioteca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaBibliotecaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaBibliotecaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tablaBiblioteca);

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342918_play.png"))); // NOI18N
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342924_chevron-left.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342930_chevron-right.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnExaminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443343078_folder-close.png"))); // NOI18N
        btnExaminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExaminarActionPerformed(evt);
            }
        });

        lblArtista.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblArtista.setText("Artista / Canción");

        slideTime.setValue(0);
        slideTime.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                slideTimeMouseDragged(evt);
            }
        });
        slideTime.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                slideTimeMouseReleased(evt);
            }
        });

        slideVol.setValue(100);
        slideVol.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                slideVolMouseDragged(evt);
            }
        });
        slideVol.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                slideVolMouseWheelMoved(evt);
            }
        });
        slideVol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                slideVolMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                slideVolMouseClicked(evt);
            }
        });

        lblTema.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        lblTema.setText("Artista / Canción");

        btnPause1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342941_stop.png"))); // NOI18N
        btnPause1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPause1ActionPerformed(evt);
            }
        });

        opRepetirCancion.setText("Repetir esta canción");
        opRepetirCancion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opRepetirCancionActionPerformed(evt);
            }
        });

        jButton1.setText("Cargar biblioteca");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnPause)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPause1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExaminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblArtista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(opRepetirCancion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(slideTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPause, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExaminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblArtista)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTema))
                    .addComponent(btnPause1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(opRepetirCancion)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slideTime, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        jButton4.setText("Limpiar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 839, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelarCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jSplitPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelarCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4))
                    .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void slideTimeMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideTimeMouseDragged
        bytesActual = slideTime.getValue();
    }//GEN-LAST:event_slideTimeMouseDragged

    private void slideTimeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideTimeMouseReleased
        try {
            r.seek(bytesActual);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_slideTimeMouseReleased

    private void btnExaminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExaminarActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.mp3", "mp3");
        jfc.setFileFilter(filter);
        
        if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            try {
                File[] ar = jfc.getSelectedFiles();
                
                modelCanciones.nuevaLista();
                
                for(File f : ar){
                    try {
                        cargarCanciones(f, modelCanciones);
                    } catch (IOException ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                /**/
//                listaCanciones.setModel(new LMCancion(canciones));
                
//                // sin titulos las tabla
//                tablaCanciones.getTableHeader().setUI(null);
//                tablaCanciones.setRowHeight(20);
//                
//                tablaCanciones.setModel(new TMCancion(canciones));
//                
//                tablaCanciones.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//                tablaCanciones.getColumnModel().getColumn(0).setPreferredWidth(700);
//                
//                /**/
//                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//                rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//                tablaCanciones.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
//                /**/
                
                cargarCancionesAListaGrafica();
//            if(r == null){
//                try {
//                    r = new Reproductor(canciones.get(0), this);
//                    r.play();
//                } catch (BasicPlayerException ex) {
//                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }

//            setSlideTime(canciones.get(0).getDuracionEnMilis());
                
                indiceActual = 0;
                
//            hiloRep = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    while(true){
//                        try{
//                            if(r.getPlayer() != null){
//                                if(r.isComplete()){
//                                    System.out.println("Completa");
//                                    indiceActual++;
//
//                                    if(indiceActual < canciones.size()){
//                                        r = new Reproductor(canciones.get(indiceActual));
//                                        r.play();
//                                        setSlideTime(canciones.get(indiceActual).getDuracionEnMilis());
//                                    }else{
//                                        System.out.println("Se ha reproducido toda la lista");
//                                    }
//                                }else{
//                                    setSlideValue(r.getPosition());
//    //                                System.out.println("Aún esta la canción "+r.getCancion()+" ["+r.getPosition()/1000+"]");
//                                    jLabel1.setText(r.getCancion().getDuracionAsString(r.getPosition()) + " / "+ r.getCancion().getDuracionAsString());
//                                    System.out.println("Aún esta la canción ["+r.getCancion().getNombre()+"] "+r.getCancion().getDuracionAsString(r.getPosition()));
//                                }
//                            }else{
//                                System.out.println("Player es null");
//                            }
//                            try {
//                                Thread.sleep(1);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }catch(NullPointerException ex){
//                            System.out.println("Player es null");
//                        }
//                    }
//                }
//            });
//
//            hiloRep.start();
                /**/
            } catch (InterruptedException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnExaminarActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        try {
            if(r != null){
                if (isPlay) {
                    isPlay = false;
                    btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342918_play.png")));
                    r.pause();
                } else {
                    isPlay = true;
                    btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342813_pause.png")));
                    if(isStop){
                        r.play();
                        isPlay = true;
                        isStop = false;
                    }else{
                        r.resume();
                    }
                }
            }else if(indiceActual != -1){
                r = new Reproductor(modelCanciones.getCanciones().get(indiceActual), this);
                r.play();            
                isPlay = true;
                isStop = false;
                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342813_pause.png")));
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPauseActionPerformed

    private void slideVolMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseDragged
        setVolumen(slideVol.getValue());
    }//GEN-LAST:event_slideVolMouseDragged

    private void slideVolMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseClicked
//        System.out.println("Clicked");
    }//GEN-LAST:event_slideVolMouseClicked

    private void slideVolMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMousePressed
//        System.out.println("Pressed");
    }//GEN-LAST:event_slideVolMousePressed

    private void slideVolMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_slideVolMouseWheelMoved
//        System.out.println("Wheel");
    }//GEN-LAST:event_slideVolMouseWheelMoved

    private void treeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseReleased
        if(evt.getClickCount() == 2){
            final File f = getSelectedTreeFile();
            if(f != null){
                try {
                    if(Validar.isCancion(f)){
                        modelCanciones.nuevaLista();
//                        System.out.println(f.getParentFile());
                        hiloCargar = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                btnCancelarCarga.setEnabled(true);
                                try {
                                    cargarCanciones(f.getParentFile(), modelCanciones);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                indiceActual = getIndice(f);
                                System.out.println("Indice actual : "+indiceActual);
                            }
                        });
                        
                        hiloCargar.start();
                        reproducir(f);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_treeMouseReleased

    private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeValueChanged
        // acá cargo los subdirectorios cuando hago click
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        if(node != null){
            if(node.getChildCount()== 0){
                /*Si no tengo hijos proceso*/
                Object o = node.getUserObject();
        
                if (o instanceof File){
                    File f = (File)o;
                    if(f.isDirectory()){
                        cargarArchivosEnNodoArbol(node, f);
                    }
                    
                }
            }
        }
    }//GEN-LAST:event_treeValueChanged

    private void btnCancelarCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCargaActionPerformed
        hiloCargar.stop();
        btnCancelarCarga.setEnabled(false);
        lblInfoCarga.setText("");
        cargarCancionesAListaGrafica();
        if(r != null){
            indiceActual = getIndice(r.getCancion());
        }else{
            indiceActual = 0;
        }
        System.out.println("Indice actual : "+indiceActual);
    }//GEN-LAST:event_btnCancelarCargaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(!modelCanciones.getCanciones().isEmpty()){
            if(indiceActual != 0){
                indiceActual--;
                if(r != null){
                    reproducirCancionActual();
                }
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(!modelCanciones.getCanciones().isEmpty()){
            if(indiceActual != (modelCanciones.getCanciones().size() - 1)){
                indiceActual++;
                if (r != null) {
                    reproducirCancionActual();
                }
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(save){
            try {
                Guardar g = new Guardar();

                g.canciones = modelCanciones.getCanciones();
                g.indiceActual = indiceActual;

                IO.escribirObjetoEn(g, Ruta.SAVE);
                IO.escribirObjetoEn(modelBib.getCanciones(), Ruta.BIBLIOTECA);
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void tablaCancionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCancionesMouseReleased
        if(evt.getClickCount() == 2){
            TMCancion model = (TMCancion) tablaCanciones.getModel();
            indiceActual = tablaCanciones.getSelectedRow();
            Cancion c = (Cancion)model.getValueAt(indiceActual, 0);

            reproducir(c);
        }
    }//GEN-LAST:event_tablaCancionesMouseReleased

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        String filtro = txtBuscar.getText().toLowerCase().trim();
//        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            lFiltrada = new ArrayList<>();

            int i=1;
            for(Cancion c : modelBib.getCanciones()){
                if(c.getAutor().toLowerCase().contains(filtro)
                        || c.getAlbum().toLowerCase().contains(filtro)
                        || c.getNombre().toLowerCase().contains(filtro)){
                    lFiltrada.add(c);
                }
            }

            cargarCancionesABiblioteca(lFiltrada);
            
           
//        }else if(filtro.equals("")){
//            cargarCancionesABiblioteca(biblioteca);
//        }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void tablaBibliotecaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMouseReleased
        if(evt.getClickCount() == 2){
            int fila = tablaBiblioteca.getSelectedRow();
            Cancion c = (Cancion)tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO);
            
            modelCanciones.setCanciones(modelBib.getCanciones());
            indiceActual = fila;
            
            cargarCancionesAListaGrafica();
            
            reproducir(c);
        }
    }//GEN-LAST:event_tablaBibliotecaMouseReleased

    private void btnPause1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPause1ActionPerformed
        if(r != null){
            try {
                r.stop();
                isPlay = false;
                isStop = true;
                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342918_play.png")));
                setSlideValue(0);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnPause1ActionPerformed

    private void tablaBibliotecaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMousePressed
        if(SwingUtilities.isRightMouseButton(evt)){
            popUpBiblio.show(tablaBiblioteca, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaBibliotecaMousePressed

    private void opRepetirCancionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opRepetirCancionActionPerformed
        repetirCancion = opRepetirCancion.isSelected();
    }//GEN-LAST:event_opRepetirCancionActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        cargarCancionesABiblioteca(modelBib.getCanciones());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        modelCanciones.nuevaLista();
        cargarCancionesAListaGrafica();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void cargarSave(){
        if(new File(Ruta.SAVE).exists()){
            try {
                
                Guardar g = (Guardar) IO.leerObjetoDesde(Ruta.SAVE);
                
                modelCanciones.setCanciones(g.canciones);
                indiceActual = g.indiceActual;
                cargarCancionesAListaGrafica();
                imprimirTemaActual(0);
                modelBib.setCanciones((List<Cancion>) IO.leerObjetoDesde(Ruta.BIBLIOTECA));
                cargarCancionesABiblioteca(modelBib.getCanciones());
                
                
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            cargarDefault();
        }
    }
    
    private void cargarArchivosEnNodoArbol(DefaultMutableTreeNode raiz, File ar){
        List<File> ordenada = new ArrayList<>();
        if(ar.listFiles() != null){
            File arConNombre;
            for (File a : ar.listFiles()) {
                if(Validar.isArchivoCorrecto(a)){
                    try {
                        if(a.isDirectory() || Validar.isCancion(a)){
                            /*Esto es solo para que se vea el nombre, por ende
                            tuve que sobre escribir el método toString*/
                            arConNombre = new File(a.getPath()){
                                
                                @Override
                                public String toString() {
                                    return this.getName();
                                }
                                
                            };
                            /*Esto es solo para que se vea el nombre, por ende
                            tuve que sobre escribir el método toString*/
                            
                            ordenada.add(arConNombre);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            //ordenar acá
            ordenarAlfabeticamente(ordenada);
            
            
            for(File f : ordenada){
                raiz.add(new DefaultMutableTreeNode(f));
            }
        }
    }
    
    /**
     * Método para cargar canciones cuando el usuario las quiera
     * escoger desde el arbol con el click secundario
     * @param raiz
     * @param ar 
     */
    private void cargarCanciones(File ar, IListaCanciones lista) throws IOException, InterruptedException{
        if(ar.listFiles() != null){
            for (File a : ar.listFiles()) {
                if(a.isDirectory()){
                    cargarCanciones(a, lista);
                }else if(Validar.isCancion(a)){
                    agregarCancion(new Cancion(a.getPath()), lista);
                }
            }
        }
    }
    
    public static void main(String args[]) {
        
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new JPlay().setVisible(true);
                }
            });
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (º ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarCarga;
    private javax.swing.JButton btnExaminar;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPause1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblArtista;
    private javax.swing.JLabel lblInfoCarga;
    private javax.swing.JLabel lblTema;
    private javax.swing.JCheckBox opRepetirCancion;
    private javax.swing.JSlider slideTime;
    private javax.swing.JSlider slideVol;
    private javax.swing.JTable tablaBiblioteca;
    private javax.swing.JTable tablaCanciones;
    private javax.swing.JTree tree;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables

    private void setSlideTime(int duracionEnMilis) {
        slideTime.setMinimum(0);
        slideTime.setMaximum(duracionEnMilis);
    }
    
    private void setSlideValue(int valorEnMilis) {
//        System.out.println("cambio al valor: "+valor);
        slideTime.setValue(valorEnMilis);
    }

    /*Este metodo se llama cuando hago click
    secundario en una carpeta del arbol y escogo la opcion
    crear lista. llamo a este metodo con la lista de carpetas*/
//    private void cargarCancionesEnLista(File[] ar, List<Cancion> canciones) throws InterruptedException {
//        Thread.sleep(100);
//        for(File f : ar){
//            if(f.isDirectory()){
//                cargarCancionesEnLista(f.listFiles(), canciones);
//            }else {
//                try {
//                    if(Validar.isCancion(f)){
//                        agregarCancion(new Cancion(f.getPath()), canciones);
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
    /**
   * Open callback, stream is ready to play.
   *
   * properties map includes audio format dependant features such as
   * bitrate, duration, frequency, channels, number of frames, vbr flag, ... 
   *
   * @param stream could be File, URL or InputStream
   * @param properties audio stream properties.
   */
    @Override
    public void opened(Object stream, Map properties) {
//        System.out.println("OPENED: "+stream);
//        System.out.println(properties);
//        
//        if(stream instanceof Cancion){
//            
//        }
        
    }

    /**
     * 
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        int milis = (int)microseconds / 1000;
        
//        setSlideValue(milis);
        setSlideValue(bytesread);
        
        // acá esta el error. Ver bien esto!
        imprimirTemaActual(milis);
//        System.out.println("BYTES LEIDOS: "+bytesread);
//        System.out.println(properties);
//        System.out.println("MS: "+microseconds);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
        System.out.println("STATE UPDATED: "+bpe.toString());
        if(bpe.getCode() == BasicPlayerEvent.EOM){
            if(!repetirCancion){
                indiceActual++;
            }
            if(indiceActual < modelCanciones.getCanciones().size()){
                reproducir(modelCanciones.getCanciones().get(indiceActual));
            }
        }else if(bpe.getCode() == BasicPlayerEvent.STOPPED){
//            imprimirTemaActual(0);
//            slideTime.setValue(0);
        }else if(bpe.getCode() == BasicPlayerEvent.SEEKED){
//            setSlideTime(bpe.getPosition());
        }else if(bpe.getCode() == BasicPlayerEvent.OPENED){
            // el indice es -1 cuando cargo desde la biblioteca
            if(indiceActual != -1){
                setSlideTime((int)modelCanciones.getCanciones().get(indiceActual).length());
            }
        }
    }

    @Override
    public void setController(BasicController bc) {
        System.out.println("SET CONTROLLER: "+bc);
        
    }

    private void setVolumen(int vol) {
        System.out.println("setVolumen");
        try {
            if(r != null){
                r.setVol(vol);
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearArbol() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");
        File[] discos = File.listRoots();
        
        for(File disco : discos){
            raiz.add(new DefaultMutableTreeNode(disco));
        }
        
        tree.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        tree.setRootVisible(false);
        
        tree.setCellRenderer(
            new CellRender(
                    CellRender.crearIcono("/jplay/recursos/iconos/1443349568_music.png"), 
                    CellRender.crearIcono("/jplay/recursos/iconos/1443349768_folder.png")
            )
        );
    }

    private void listenerClickDerechoSobreArbol() {
        /*Este codigo es para que cuando el usuario haga click secundario
        se seleccione la fila del arbol*/
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                   
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(selPath); 
                    if (selRow >- 1){
                       tree.setSelectionRow(selRow); 
                        popUpTree.show(tree, e.getX()+10, e.getY()+10);
//                       popup.show(tree, e.getX(), e.getY());
                    }
                }
            }
        };
       tree.addMouseListener(ml);
    }
    
    private void crearPopUpTree(){
        popUpTree = new JPopupMenu();
        JMenuItem itemAlistaNueva = new JMenuItem("A lista nueva");
        JMenuItem itemAlistaExistente = new JMenuItem("Añadir a existente");
        JMenuItem itemABiblioteca = new JMenuItem("Añadir a biblioteca");
        
        JPopupMenu.Separator sep = new JPopupMenu.Separator();
        
        popUpTree.add(itemAlistaNueva);
        popUpTree.add(itemAlistaExistente);
        popUpTree.add(sep);
        popUpTree.add(itemABiblioteca);
        
        itemAlistaNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final File f = getSelectedTreeFile();
                modelCanciones.nuevaLista();
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            cargarCanciones(f, modelCanciones);
                            lblInfoCarga.setText("");
                            
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            lblInfoCarga.setText("Cancelado");
                        }
                    }
                });
                hiloCargar.start();
            }
        });
        
        itemAlistaExistente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            File f = getSelectedTreeFile();
                            try {
                                cargarCanciones(f, modelCanciones);
                                lblInfoCarga.setText("");
//                                cargarCancionesAListaGrafica();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                });
                hiloCargar.start();
                
            }
        });
        
        itemABiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            File f = getSelectedTreeFile();
                            try {
                                cargarCanciones(f, modelBib);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                
                hiloCargar.start();
            }
        });
    }
    
    private void crearPopUpBiblioteca(){
        popUpBiblio = new JPopupMenu();
        JMenuItem itemRemoverDeBiblioteca = new JMenuItem("Remover");
        
        popUpBiblio.add(itemRemoverDeBiblioteca);
        
        itemRemoverDeBiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selRows = tablaBiblioteca.getSelectedRows();
                
                List<Cancion> canciones = new ArrayList<>();
                
                for(int fila : selRows){
                    canciones.add((Cancion)tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO));
                }
                
                for (Cancion c : canciones) {
                    modelBib.remove(c);
                }
                
//                cargarCancionesABiblioteca(biblioteca);
            }
        });
        
    }
    
    private File getSelectedTreeFile(){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                tree.getLastSelectedPathComponent();
        if(node != null){
            Object o = node.getUserObject();
            if (o instanceof File){
                return (File)o;
            }
        }
        return null;
    }

    private void cargarCancionesAListaGrafica() {
        //ordenar acá
        System.out.println("Se cargaron "+modelCanciones.getCanciones().size()+" canciones a la lista principal");
        ordenarAlfabeticamente2(modelCanciones.getCanciones());
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
        tablaCanciones.getTableHeader().setUI(null);
        tablaCanciones.setRowHeight(20);
        tablaCanciones.setModel(modelCanciones);
        tablaCanciones.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tablaCanciones.getColumnModel().getColumn(0).setPreferredWidth(500);
        /**/
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaCanciones.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/
        
        btnCancelarCarga.setEnabled(false);
        lblInfoCarga.setText("Se cargaron "+modelCanciones.getCanciones().size()+" canciones a la lista principal");
    }
    
    private void cargarCancionesABiblioteca(List<Cancion> lista) {
        //ordenar acá
        ordenarAlfabeticamente2(lista);
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
//        tablaBiblioteca.getTableHeader().setUI(null);
//        tablaBiblioteca.setRowHeight(20);
        modelBib.setCanciones(lista);
        tablaBiblioteca.setModel(modelBib);
        System.out.println("Se cargaron "+lista.size()+" canciones en biblioteca");
        lblInfoCarga.setText("Se cargaron "+lista.size()+" canciones en biblioteca");
//        tablaBiblioteca.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//        tablaBiblioteca.getColumnModel().getColumn(0).setPreferredWidth(700);
        /**/
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        tablaBiblioteca.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/
        
        btnCancelarCarga.setEnabled(false);
        lblInfoCarga.setText("");
    }

    private void reproducir(File f) {
        try {
            Cancion c = new Cancion(f.getPath());

            if (r != null) {
                r.stop();
            }
            
            r = new Reproductor(c,this);
            
            r.play();
//            lblTemaActual.setText(c.getAutor()+" / "+c.getNombre() + " ("+c.getDuracionAsString()+")");
            
//            btnPause.setText("Pause");
            btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/1443342813_pause.png")));
            isPlay = true;
            isStop = false;
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Este método reproduce la canción actual según el índiceActual
     * El indice debe ser válido
     */
    private void reproducirCancionActual() {
        reproducir(modelCanciones.getCanciones().get(indiceActual));
    }

    private int getIndice(File f) {
        return modelCanciones.getCanciones().indexOf(f);
    }

    private void imprimirTemaActual(int milis) {
        String durActual = "0:00";
        String durTotal = "()";
        
        if(r != null){
//            if(milis != 0){
//                durActual = r.getCancion().getDuracionAsString(milis) + " / "+ r.getCancion().getDuracionAsString();
//            }else{
//                durActual = "0:00 / "+ r.getCancion().getDuracionAsString();
//            }

            lblArtista.setText(r.getCancion().getAutor());
            lblTema.setText(r.getCancion().getNombre()+" ("+r.getCancion().getDuracionAsString()+")");
        }else if(indiceActual != -1){
            Cancion c = modelCanciones.getCanciones().get(indiceActual);
            durActual = "0:00 / " + c.getDuracionAsString();
            lblArtista.setText(c.getAutor());
            lblTema.setText(c.getNombre() + " (" + durActual + ")");
        }
    }

    private void cargarDefault() {
        cargarCancionesABiblioteca(modelBib.getCanciones());
        cargarCancionesAListaGrafica();
    }

    private void agregarCancion(Cancion cancion, IListaCanciones lista) {
        lista.add(cancion);
        ordenarAlfabeticamente2(lista.getCanciones());
        lblInfoCarga.setText("Agregando "+cancion);
        System.out.println("Agregando "+cancion);
        
        if(lista.getTipo() == Tipo.BIBLIOTECA){
            tablaBiblioteca.updateUI();
        }else if(lista.getTipo() == Tipo.LISTA){
            tablaCanciones.updateUI();
        }
    }

    private void ordenarAlfabeticamente(List<File> lista) {
        Collections.sort(lista, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });
    }
    
    private void ordenarAlfabeticamente2(List<Cancion> lista) {
        Collections.sort(lista, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });
    }
}
