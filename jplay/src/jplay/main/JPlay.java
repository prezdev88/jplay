package jplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import jplay.model.tree.CellRender;
import jplay.model.tree.CellRenderCancionLista;

public class JPlay extends javax.swing.JFrame implements BasicPlayerListener {

    public static Reproductor reproductor;
    private List<Cancion> canciones;
    private List<Cancion> biblioteca;
    private int indiceActual;
    private Thread hiloRep;
    private Thread hiloCargar;
    private JPopupMenu popUpTree;
    private JPopupMenu popUpBiblio;
    private final String NOMBRE = "JPlay";
    private final String VERSION = "0.2a";
    private final boolean save = true;
    private List<Cancion> lFiltrada;
    private boolean isPlay;
    private boolean isStop;
    private boolean isRandom;
    private boolean repetirCancion;
    private long bytesActual;
    private int nextRandom;
    private Image icono;

    public JPlay() {
        initComponents();
        canciones = new ArrayList<>();
        biblioteca = new ArrayList<>();

        isRandom = false;

        crearArbol();
        listenerClickDerechoSobreArbol();
        crearPopUpTree();
        crearPopUpBiblioteca();

        btnCancelarCarga.setEnabled(false);
        indiceActual = -1;
        if (save) {
            cargarSave();
        }

        setTitle(NOMBRE + " - " + VERSION);
        isPlay = false;
        isStop = true;
        repetirCancion = opRepetirCancion.isSelected();
        slideVol.setVisible(false);

        crearListenerTitulosTabla();
        createKeybindings(tablaCanciones);
        createKeybindings(tablaBiblioteca);

        // esto es para que no se pueda mover las columnas
        tablaBiblioteca.getTableHeader().setReorderingAllowed(false);

        cargarArbolConCanciones();

        /*Se hace inisible la tabla antigua de temas y el boton limpiar*/
        jScrollPane2.setVisible(false);
        jLabel2.setVisible(false);
        /*Se hace inisible la tabla antigua de temas y el boton limpiar*/

        this.setIconImage(icono);

        /*VALIDAR SI ESTAS TODAS LAS COSAS NECESARIAS*/
//        File f = new File("res");
//        
//        if(!f.exists()){
//            f.mkdir();
//            
//            
//        }
        icono = Recurso.ICONO_JPLAY;

        icono = icono.getScaledInstance(
                (int) Recurso.CARATULA.getWidth(),
                (int) Recurso.CARATULA.getHeight(),
                Image.SCALE_SMOOTH);
        lblCaratula.setIcon(new ImageIcon(icono));

        this.setBounds(0, 0, 1024, 600);
        this.setLocationRelativeTo(null);
    }

    // http://stackoverflow.com/questions/13516730/disable-enter-key-from-moving-down-a-row-in-jtable
    // este método es porque cuando apretaba enter en la tabla de canciones, se veia feo el que
    // el cursor bajara y despues subiera. Este método sobre escribe eso hecho por java automáticamente
    private void createKeybindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCanciones = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeSong = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaBiblioteca = new javax.swing.JTable();
        slideTime = new javax.swing.JSlider();
        slideVol = new javax.swing.JSlider();
        togVol = new javax.swing.JToggleButton();
        lblInfoCarga = new javax.swing.JLabel();
        btnCancelarCarga = new javax.swing.JButton();
        lblCaratula = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnPause = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnPause1 = new javax.swing.JButton();
        btnExaminar = new javax.swing.JButton();
        opAleatorio = new javax.swing.JCheckBox();
        opRepetirCancion = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        lblTema = new javax.swing.JLabel();
        lblArtista = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(254, 254, 254));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(254, 254, 254));

        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setOneTouchExpandable(true);

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

        jSplitPane2.setDividerLocation(600);
        jSplitPane2.setOneTouchExpandable(true);

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
        tablaCanciones.setShowHorizontalLines(false);
        tablaCanciones.setShowVerticalLines(false);
        tablaCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaCancionesMouseReleased(evt);
            }
        });
        tablaCanciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tablaCancionesKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tablaCanciones);

        jLabel2.setBackground(new java.awt.Color(63, 81, 181));
        jLabel2.setForeground(new java.awt.Color(254, 254, 254));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Limpiar");
        jLabel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel2.setOpaque(true);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });

        treeSong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeSongMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(treeSong);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane2.setRightComponent(jPanel3);

        jPanel1.setBackground(new java.awt.Color(254, 254, 254));

        txtBuscar.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(153, 153, 153));
        txtBuscar.setText("Buscar aquí tus canciones");
        txtBuscar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                txtBuscarMouseDragged(evt);
            }
        });
        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBuscarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBuscarFocusLost(evt);
            }
        });
        txtBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtBuscarMouseReleased(evt);
            }
        });
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
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
        tablaBiblioteca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tablaBibliotecaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tablaBiblioteca);

        slideTime.setBackground(new java.awt.Color(254, 254, 254));
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

        slideVol.setBackground(new java.awt.Color(254, 254, 254));
        slideVol.setMaximum(40);
        slideVol.setOrientation(javax.swing.JSlider.VERTICAL);
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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                slideVolMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                slideVolMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                slideVolMouseReleased(evt);
            }
        });

        togVol.setBackground(new java.awt.Color(255, 152, 0));
        togVol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/material/ic_volume_up_white_24dp_1x.png"))); // NOI18N
        togVol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togVolActionPerformed(evt);
            }
        });

        lblInfoCarga.setBackground(new java.awt.Color(254, 254, 254));
        lblInfoCarga.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCancelarCarga.setBackground(new java.awt.Color(244, 67, 54));
        btnCancelarCarga.setForeground(new java.awt.Color(254, 254, 254));
        btnCancelarCarga.setText("Cancelar carga");
        btnCancelarCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCargaActionPerformed(evt);
            }
        });

        lblCaratula.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel4.setBackground(new java.awt.Color(254, 254, 254));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnPause.setBackground(new java.awt.Color(76, 175, 80));
        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/material/ic_play_arrow_white_24dp_1x.png"))); // NOI18N
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(63, 81, 181));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/material/ic_skip_previous_white_24dp_1x.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(63, 81, 181));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/material/ic_skip_next_white_24dp_1x.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnPause1.setBackground(new java.awt.Color(244, 67, 54));
        btnPause1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/material/ic_stop_white_24dp_1x.png"))); // NOI18N
        btnPause1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPause1ActionPerformed(evt);
            }
        });

        btnExaminar.setBackground(new java.awt.Color(63, 81, 181));
        btnExaminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jplay/recursos/iconos/material/ic_folder_open_white_24dp_1x.png"))); // NOI18N
        btnExaminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExaminarActionPerformed(evt);
            }
        });

        opAleatorio.setBackground(new java.awt.Color(254, 254, 254));
        opAleatorio.setText("Shuffle");
        opAleatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opAleatorioActionPerformed(evt);
            }
        });

        opRepetirCancion.setBackground(new java.awt.Color(254, 254, 254));
        opRepetirCancion.setText("Repetir esta canción");
        opRepetirCancion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opRepetirCancionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnPause)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPause1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExaminar))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(opAleatorio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(opRepetirCancion)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPause, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExaminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPause1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(opRepetirCancion)
                    .addComponent(opAleatorio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(254, 254, 254));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTema.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        lblTema.setText("Artista / Canción");

        lblArtista.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        lblArtista.setText("Artista / Canción");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblArtista, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTema, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(lblArtista)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelarCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtBuscar)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblCaratula, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(togVol, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(slideTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(togVol, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCaratula, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(slideTime, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelarCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jSplitPane1.setRightComponent(jSplitPane2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void slideTimeMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideTimeMouseDragged
        bytesActual = slideTime.getValue();
    }//GEN-LAST:event_slideTimeMouseDragged

    private void slideTimeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideTimeMouseReleased
        try {
            reproductor.seek(bytesActual);
            setVolumen(slideVol.getValue());
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

        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File[] ar = jfc.getSelectedFiles();

//                cargarCancionesEnLista(ar, canciones);
                canciones = new ArrayList<>();
                for (File f : ar) {
                    try {
                        cargarCanciones(f, canciones);
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
            if (reproductor != null) {
                if (isPlay) {
                    isPlay = false;
                    btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PLAY)));
                    reproductor.pause();
                } else {
                    isPlay = true;
                    btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
                    if (isStop) {
                        reproductor.play();
                        isPlay = true;
                        isStop = false;
                    } else {
                        reproductor.resume();
                    }
                }
            } else if (indiceActual != -1) {
//                r = new Reproductor(canciones.get(indiceActual), this);
//                r.play();      
                reproducir(canciones.get(indiceActual));
                isPlay = true;
                isStop = false;
                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
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
        if (evt.getClickCount() == 2) {
            procesarCancionArbol();
        }
    }//GEN-LAST:event_treeMouseReleased

    private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeValueChanged
        // acá cargo los subdirectorios cuando hago click
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node != null) {
            if (node.getChildCount() == 0) {
                /*Si no tengo hijos proceso*/
                Object o = node.getUserObject();

                if (o instanceof File) {
                    File f = (File) o;
                    if (f.isDirectory()) {
                        cargarArchivosEnNodoArbol(node, f);
                    }

                }
            }
        }
    }//GEN-LAST:event_treeValueChanged

    private void btnCancelarCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCargaActionPerformed
        hiloCargar.stop();
        btnCancelarCarga.setEnabled(false);
        cargarCancionesAListaGrafica();
        if (reproductor != null) {
            indiceActual = getIndice(reproductor.getCancion());
        } else {
            indiceActual = 0;
        }
        System.out.println("Indice actual : " + indiceActual);
    }//GEN-LAST:event_btnCancelarCargaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (!canciones.isEmpty()) {
            if (isRandom) {
                indiceActual = getRandom();
                nextRandom = getRandom();
                if (reproductor != null) {
                    reproducirCancionActual();
                }
            } else if (indiceActual != 0) {
                indiceActual--;
                if (reproductor != null) {
                    reproducirCancionActual();
                }
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (!canciones.isEmpty()) {
            if (isRandom) {
                indiceActual = getRandom();
                nextRandom = getRandom();
                if (reproductor != null) {
                    reproducirCancionActual();
                }
            } else if (indiceActual != (canciones.size() - 1)) {
                indiceActual++;
                if (reproductor != null) {
                    reproducirCancionActual();
                }
            }

        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (save) {
            try {
                Guardar g = new Guardar();

                g.canciones = canciones;
                g.indiceActual = indiceActual;

                IO.escribirObjetoEn(g, Ruta.SAVE);
                IO.escribirObjetoEn(biblioteca, Ruta.BIBLIOTECA);
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void tablaCancionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCancionesMouseReleased
        if (evt.getClickCount() == 2) {
            TMCancion model = (TMCancion) tablaCanciones.getModel();
            indiceActual = tablaCanciones.getSelectedRow();
            Cancion c = (Cancion) model.getValueAt(indiceActual, 0);

            reproducir(c);
        }
    }//GEN-LAST:event_tablaCancionesMouseReleased

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        String filtro = txtBuscar.getText().toLowerCase().trim();
//        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
        lFiltrada = new ArrayList<>();

        int i = 1;
        for (Cancion c : biblioteca) {
            if (c.getAutor().toLowerCase().contains(filtro)
                    || c.getAlbum().toLowerCase().contains(filtro)
                    || c.getNombre().toLowerCase().contains(filtro)) {
                lFiltrada.add(c);
            }
        }

        cargarCancionesABiblioteca(lFiltrada);

//        }else if(filtro.equals("")){
//            cargarCancionesABiblioteca(biblioteca);
//        }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void tablaBibliotecaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMouseReleased
        if (evt.getClickCount() == 2) {
            tocarCancionSeleccionadaEnTablaBiblioteca();
        }
    }//GEN-LAST:event_tablaBibliotecaMouseReleased

    private void btnPause1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPause1ActionPerformed
        if (reproductor != null) {
            try {
                reproductor.stop();
                isPlay = false;
                isStop = true;
                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PLAY)));
                setSlideValue(0);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnPause1ActionPerformed

    private void tablaBibliotecaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            popUpBiblio.show(tablaBiblioteca, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaBibliotecaMousePressed

    private void opRepetirCancionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opRepetirCancionActionPerformed
        repetirCancion = opRepetirCancion.isSelected();
    }//GEN-LAST:event_opRepetirCancionActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void togVolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togVolActionPerformed
        slideVol.setVisible(togVol.isSelected());
    }//GEN-LAST:event_togVolActionPerformed

    private void slideVolMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseReleased
        togVol.setSelected(false);
        slideVol.setVisible(togVol.isSelected());

        if (slideVol.getValue() == 0) {
            togVol.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.VOL_OFF)));
        } else {
            togVol.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.VOL)));
        }
    }//GEN-LAST:event_slideVolMouseReleased

    private void opAleatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opAleatorioActionPerformed
        isRandom = opAleatorio.isSelected();
    }//GEN-LAST:event_opAleatorioActionPerformed

    private void jLabel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MousePressed
        jLabel2.setBackground(new java.awt.Color(26, 35, 126));
    }//GEN-LAST:event_jLabel2MousePressed

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseReleased
        jLabel2.setBackground(new java.awt.Color(63, 81, 181));
        canciones = new ArrayList<>();
        cargarCancionesAListaGrafica();
    }//GEN-LAST:event_jLabel2MouseReleased

    private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
        jLabel2.setBackground(new java.awt.Color(63, 81, 181));
    }//GEN-LAST:event_jLabel2MouseExited

    private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
        jLabel2.setBackground(new java.awt.Color(92, 107, 192));
    }//GEN-LAST:event_jLabel2MouseEntered

    private void tablaCancionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaCancionesKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            TMCancion model = (TMCancion) tablaCanciones.getModel();
            indiceActual = tablaCanciones.getSelectedRow();
            Cancion c = (Cancion) model.getValueAt(indiceActual, 0);

            reproducir(c);
//            tablaCanciones.getSelectionModel().clearSelection();
            tablaCanciones.getSelectionModel().setSelectionInterval(indiceActual, indiceActual);
        }
    }//GEN-LAST:event_tablaCancionesKeyReleased

    private void tablaBibliotecaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaBibliotecaKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tocarCancionSeleccionadaEnTablaBiblioteca(); // xD
        }
    }//GEN-LAST:event_tablaBibliotecaKeyReleased

    private void txtBuscarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBuscarMouseReleased
//        txtBuscar.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 11)); // NOI18N
//        txtBuscar.setForeground(Color.black);
//        txtBuscar.setText("");
    }//GEN-LAST:event_txtBuscarMouseReleased

    private void txtBuscarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBuscarFocusLost
        if (txtBuscar.getText().trim().equals("")) {
            txtBuscar.setFont(new java.awt.Font("Tahoma", Font.ITALIC, 12)); // NOI18N
            txtBuscar.setForeground(new java.awt.Color(153, 153, 153));
            txtBuscar.setText("Buscar aquí tus canciones");
        }
    }//GEN-LAST:event_txtBuscarFocusLost

    private void txtBuscarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBuscarFocusGained
        txtBuscar.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 12)); // NOI18N
        txtBuscar.setForeground(Color.black);
        txtBuscar.setText("");
//        txtBuscar.setSelectionStart(0);
//        txtBuscar.setSelectionEnd(0);
    }//GEN-LAST:event_txtBuscarFocusGained

    private void txtBuscarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBuscarMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarMouseDragged

    private void treeSongMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeSongMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSong.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Cancion) {
                    Cancion c = (Cancion) o;
                    indiceActual = canciones.indexOf(c);
                    reproducir(c);
                }
            }
//            TMCancion model = (TMCancion) tablaCanciones.getModel();
//            indiceActual = tablaCanciones.getSelectedRow();
//            Cancion c = (Cancion) model.getValueAt(indiceActual, 0);
//
//            reproducir(c);

        }
    }//GEN-LAST:event_treeSongMouseReleased

    private void cargarSave() {
        if (new File(Ruta.SAVE).exists()) {
            try {

                Guardar g = (Guardar) IO.leerObjetoDesde(Ruta.SAVE);

                canciones = g.canciones;
                indiceActual = g.indiceActual;
                cargarCancionesAListaGrafica();
                imprimirTemaActual(0);

                biblioteca = (List<Cancion>) IO.leerObjetoDesde(Ruta.BIBLIOTECA);
                cargarCancionesABiblioteca(biblioteca);

            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cargarDefault();
        }
    }

    private void cargarArchivosEnNodoArbol(DefaultMutableTreeNode raiz, File ar) {
        List<File> ordenada = new ArrayList<>();
        if (ar.listFiles() != null) {
            File arConNombre;
            for (File a : ar.listFiles()) {
                if (Validar.isArchivoCorrecto(a)) {
                    try {
                        if (a.isDirectory() || Validar.isCancion(a)) {
                            /*Esto es solo para que se vea el nombre, por ende
                             tuve que sobre escribir el método toString*/
                            arConNombre = new File(a.getPath()) {

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
            Collections.sort(ordenada, new Comparator<File>() {

                @Override
                public int compare(File f1, File f2) {
                    return f1.compareTo(f2);
                }
            });

            for (File f : ordenada) {
                raiz.add(new DefaultMutableTreeNode(f));
            }
        }
    }

    /**
     * Método para cargar canciones cuando el usuario las quiera escoger desde
     * el arbol con el click secundario
     *
     * @param raiz
     * @param ar
     */
    private void cargarCanciones(File ar, List<Cancion> lista) throws IOException, InterruptedException {
        if (ar.listFiles() != null) {
            for (File a : ar.listFiles()) {
                if (a.isDirectory()) {
//                    cargarCancionesEnLista(ar.listFiles(), lista);
                    cargarCanciones(a, lista);
                } else if (Validar.isCancion(a)) {
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
            @Override
            public void run() {
                new JPlay().setVisible(true);
            }
        });
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedLookAndFeelException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarCarga;
    private javax.swing.JButton btnExaminar;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPause1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JLabel lblArtista;
    private javax.swing.JLabel lblCaratula;
    private javax.swing.JLabel lblInfoCarga;
    private javax.swing.JLabel lblTema;
    private javax.swing.JCheckBox opAleatorio;
    private javax.swing.JCheckBox opRepetirCancion;
    private javax.swing.JSlider slideTime;
    private javax.swing.JSlider slideVol;
    private javax.swing.JTable tablaBiblioteca;
    private javax.swing.JTable tablaCanciones;
    private javax.swing.JToggleButton togVol;
    private javax.swing.JTree tree;
    private javax.swing.JTree treeSong;
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
     * properties map includes audio format dependant features such as bitrate,
     * duration, frequency, channels, number of frames, vbr flag, ...
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
        int milis = (int) microseconds / 1000;

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
        System.out.println("STATE UPDATED: " + bpe.toString());
        if (bpe.getCode() == BasicPlayerEvent.EOM) {
            if (!repetirCancion) {
                if (isRandom) {
                    indiceActual = getRandom();
                    nextRandom = getRandom();
                } else {
                    indiceActual++;
                }

            }
            if (indiceActual < canciones.size()) {
                reproducir(canciones.get(indiceActual));
            }
        } else if (bpe.getCode() == BasicPlayerEvent.STOPPED) {
//            imprimirTemaActual(0);
//            slideTime.setValue(0);
        } else if (bpe.getCode() == BasicPlayerEvent.SEEKED) {
//            setSlideTime(bpe.getPosition());
        } else if (bpe.getCode() == BasicPlayerEvent.OPENED) {
            // el indice es -1 cuando cargo desde la biblioteca
            if (indiceActual != -1) {
                setSlideTime((int) canciones.get(indiceActual).length());
            }
        }
    }

    @Override
    public void setController(BasicController bc) {
        System.out.println("SET CONTROLLER: " + bc);

    }

    private void setVolumen(int vol) {
        try {
            if (reproductor != null) {
                reproductor.setVol(vol);
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearArbol() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");
        File[] discos = File.listRoots();

        for (File disco : discos) {
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

    private void cargarArbolConCanciones() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<String> discos = getNombreDiscos();

        for (String nombreDisco : discos) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(nombreDisco);

            for (Cancion cancion : canciones) {
                if (nombreDisco.equalsIgnoreCase(cancion.getAutor() + " - " + cancion.getAlbum())) {
                    disco.add(new DefaultMutableTreeNode(cancion));
                }
            }

            raiz.add(disco);
        }

        treeSong.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeSong.setRootVisible(false);
//        treeSong.expandRow(0);

        treeSong.setCellRenderer(
                new CellRenderCancionLista(
                        CellRender.crearIcono("/jplay/recursos/iconos/1453631419_icon-play.png"),
                        CellRender.crearIcono("/jplay/recursos/iconos/1453541047_emblem-cd.png")
                )
        );
    }

    private void listenerClickDerechoSobreArbol() {
        /*Este codigo es para que cuando el usuario haga click secundario
         se seleccione la fila del arbol*/
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {

                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(selPath);
                    if (selRow > - 1) {
                        tree.setSelectionRow(selRow);
                        popUpTree.show(tree, e.getX() + 10, e.getY() + 10);
//                       popup.show(tree, e.getX(), e.getY());
                    }
                }
            }
        };
        tree.addMouseListener(ml);
    }

    private void crearPopUpTree() {
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
                canciones = new ArrayList<>();
                hiloCargar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        btnCancelarCarga.setEnabled(true);
                        try {
                            cargarCanciones(f, canciones);
                            cargarCancionesAListaGrafica();
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
                                cargarCanciones(f, canciones);
                                cargarCancionesAListaGrafica();
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
                                cargarCanciones(f, biblioteca);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cargarCancionesABiblioteca(biblioteca);
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                hiloCargar.start();
            }
        });
    }

    private void crearPopUpBiblioteca() {
        popUpBiblio = new JPopupMenu();
        JMenuItem itemRemoverDeBiblioteca = new JMenuItem("Remover");

        popUpBiblio.add(itemRemoverDeBiblioteca);

        itemRemoverDeBiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Cancion> canciones = new ArrayList<>();

                for (int fila : selRows) {
                    canciones.add((Cancion) tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO));
                }

                for (Cancion c : canciones) {
                    biblioteca.remove(c);
                }

                cargarCancionesABiblioteca(biblioteca);
            }
        });

    }

    private File getSelectedTreeFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            Object o = node.getUserObject();
            if (o instanceof File) {
                return (File) o;
            }
        }
        return null;
    }

    private void cargarCancionesAListaGrafica() {
        //ordenar acá
        System.out.println("Se cargaron " + canciones.size() + " canciones a la lista principal");
        Collections.sort(canciones, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
        tablaCanciones.getTableHeader().setUI(null);
        tablaCanciones.setRowHeight(20);
        tablaCanciones.setModel(new TMCancion(canciones));

        cargarArbolConCanciones();

        tablaCanciones.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tablaCanciones.getColumnModel().getColumn(0).setPreferredWidth(300);
        /**/
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaCanciones.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/

        btnCancelarCarga.setEnabled(false);
        lblInfoCarga.setText("Se cargaron " + canciones.size() + " canciones a la lista principal");
    }

    private void cargarCancionesABiblioteca(List<Cancion> lista) {
        //ordenar acá
        Collections.sort(lista, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
//        tablaBiblioteca.getTableHeader().setUI(null);
//        tablaBiblioteca.setRowHeight(20);
        tablaBiblioteca.setModel(new TMCancionBiblioteca(lista));
        System.out.println("Se cargaron " + lista.size() + " canciones en biblioteca");
        lblInfoCarga.setText("Se cargaron " + lista.size() + " canciones en biblioteca");
//        tablaBiblioteca.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tablaBiblioteca.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaBiblioteca.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaBiblioteca.getColumnModel().getColumn(2).setPreferredWidth(200);
        /**/
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        tablaBiblioteca.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/

        btnCancelarCarga.setEnabled(false);
    }

    private void reproducir(Cancion cancion) {
        try {
//            Cancion c = new Cancion(f.getPath());

            if (!cancion.hasCover()) {
                System.out.println("La cación no tiene caratula!");
                List<File> fotos = Recurso.getFotos(cancion);
                System.out.println("Se han encontrado " + fotos.size() + " foto");

                Collections.sort(fotos, new Comparator<File>() {

                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.length() < o2.length()) {
                            return 1;
                        } else if (o1.length() > o2.length()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

                if (!fotos.isEmpty()) {
                    File arPrimeraFoto = fotos.get(0);
                    cancion.setCoverFile(arPrimeraFoto);
                    System.out.println("Se añadió una caratula desde la ruta de la canción");
                } else {
                    icono = icono.getScaledInstance(
                            (int) Recurso.CARATULA.getWidth(),
                            (int) Recurso.CARATULA.getHeight(),
                            Image.SCALE_SMOOTH);
                    cancion.setCaratulaIcon(icono);
                    System.out.println("Se añadió una caratula POR DEFECTO");
                }
            } else {
                System.out.println("La canción tiene caratula!");
            }

            lblCaratula.setIcon(cancion.getCoverImage());
//            lblCaratula.setIcon(new ImageIcon(icono)); 

            if (reproductor != null) {
                reproductor.stop();
            }

            reproductor = new Reproductor(cancion, this);

            reproductor.play();
            setTitle(NOMBRE + " - " + VERSION + " [" + cancion.getAutor() + " - " + cancion.getNombre() + "]");
            setVolumen(slideVol.getValue());
//            lblTemaActual.setText(c.getAutor()+" / "+c.getNombre() + " ("+c.getDuracionAsString()+")");

//            btnPause.setText("Pause");
            btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
            isPlay = true;
            isStop = false;

//            treeSong.setCellRenderer(
//                new CellRenderCancionLista(
//                        CellRender.crearIcono("/jplay/recursos/iconos/1443349568_music.png"),
//                        CellRender.crearIcono("/jplay/recursos/iconos/1453541047_emblem-cd.png")
//                )
//            );
            treeSong.updateUI();

        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Este método reproduce la canción actual según el índiceActual El indice
     * debe ser válido
     */
    private void reproducirCancionActual() {
        reproducir(canciones.get(indiceActual));
    }

    private int getIndice(File f) {
        return canciones.indexOf(f);
    }

    private void agregarCancion(Cancion cancion, List<Cancion> lista) {
        lista.add(cancion);
        lblInfoCarga.setText("Agregando " + cancion);
//        System.out.println("Agregando "+cancion);
    }

    private void imprimirTemaActual(int milis) {
        String durActual = "0:00";
        String durTotal = "()";

        if (reproductor != null) {
//            if(milis != 0){
//                durActual = r.getCancion().getDuracionAsString(milis) + " / "+ r.getCancion().getDuracionAsString();
//            }else{
//                durActual = "0:00 / "+ r.getCancion().getDuracionAsString();
//            }

            lblArtista.setText(reproductor.getCancion().getAutor());
            lblTema.setText(reproductor.getCancion().getNombre() + " (" + reproductor.getCancion().getDuracionAsString() + ")");
        } else if (indiceActual != -1) {
            Cancion c = canciones.get(indiceActual);
            durActual = "0:00 / " + c.getDuracionAsString();
            lblArtista.setText(c.getAutor());
            lblTema.setText(c.getNombre() + " (" + durActual + ")");
        }
    }

    private void cargarDefault() {
        cargarCancionesABiblioteca(biblioteca);
        cargarCancionesAListaGrafica();
    }

    private void crearListenerTitulosTabla() {
        tablaBiblioteca.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaBiblioteca.columnAtPoint(e.getPoint());
                String name = tablaBiblioteca.getColumnName(col);
                System.out.println("Column index selected " + col + " " + name);
            }
        });
    }

    public int getRandom() {
        return new Random().nextInt(canciones.size());
    }

    private void tocarCancionSeleccionadaEnTablaBiblioteca() {
        int fila = tablaBiblioteca.getSelectedRow();
        Cancion c = (Cancion) tablaBiblioteca.getValueAt(fila, TMCancionBiblioteca.OBJETO_COMPLETO);

        TMCancionBiblioteca model = (TMCancionBiblioteca) tablaBiblioteca.getModel();

        canciones = model.canciones;
        indiceActual = fila;

        cargarCancionesAListaGrafica();

        tablaCanciones.getSelectionModel().setSelectionInterval(fila, fila);

        reproducir(c);
    }

    // Método que se llama cuando hago doble click en un tema musical
    // o cuando apreto enter en el arbol
    private void procesarCancionArbol() {
        final File f = getSelectedTreeFile();
        if (f != null) {
            try {
                if (Validar.isCancion(f)) {
                    canciones = new ArrayList<>();
//                        System.out.println(f.getParentFile());
                    hiloCargar = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            btnCancelarCarga.setEnabled(true);
                            try {
                                cargarCanciones(f.getParentFile(), canciones);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cargarCancionesAListaGrafica();
                            indiceActual = getIndice(f);
                            System.out.println("Indice actual : " + indiceActual);
                        }
                    });

                    hiloCargar.start();
                    reproducir(new Cancion(f.getPath()));
                }
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private List<String> getNombreDiscos() {
        boolean discoExiste;
        List<String> discos = new ArrayList<>();

        for (Cancion c : canciones) {
            if (discos.isEmpty()) {
                discos.add(c.getAutor() + " - " + c.getAlbum());
            } else {
                // tengo que buscar ese album
                discoExiste = false;

                for (String disco : discos) {
                    if (disco.equalsIgnoreCase(c.getAutor() + " - " + c.getAlbum())) {
                        discoExiste = true;
                        break;
                    }
                }

                if (!discoExiste) {
                    discos.add(c.getAutor() + " - " + c.getAlbum());
                }
            }
        }
        return discos;
    }
}
