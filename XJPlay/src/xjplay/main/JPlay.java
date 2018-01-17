package xjplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons
import xjplay.save.Guardar;
import xjplay.recursos.Ruta;
import jplay.model.Reproductor;
import xjplay.coverArt.HiloCover;
import jplay.model.Biblioteca;
import jplay.model.Cancion;
import xjplay.utils.Validar;
import xjplay.save.IO;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import xjplay.model.tableModel.TMCancion;
import xjplay.model.tableModel.TMCancionBiblioteca;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import jplay.model.Album;
import xjplay.model.busqueda.DgBuscar;
import xjplay.model.busqueda.IBuscar;
import xjplay.model.lastFM.LastFM;
import xjplay.model.progress.WorkerStringProgress;
import xjplay.model.rules.Rules;
import xjplay.model.tree.CellRenderExplorer;
import xjplay.model.tree.CellRenderCancionLista;
import xjplay.model.tree.CellRenderCancionMasTocada;
import xjplay.recursos.Recurso;
//import nicon.notify.core.Notification;

public class JPlay extends javax.swing.JFrame implements BasicPlayerListener, IBuscar {

    public static Reproductor reproductor;
    private Biblioteca biblioteca;
    private List<Cancion> canciones; // son las canciones de la lista de reproducción actual
    private Thread hiloRep;
    private Thread hiloCargar;
    private JPopupMenu popUpTree;
    private JPopupMenu popUpBiblio;
    private JPopupMenu popCover;

    private final boolean SAVE = true; // ESTO ES SOLO PARA DEBUGGING
    private List<Cancion> lFiltrada;
    private boolean isPlay;
    private boolean isStop;
    private boolean isRandom;
    private boolean repetirCancion;
    private int nextRandom;
    private Image icono;
    private HiloCover hCover; // hilo para animación de caratulas

    private int totalBytes; // GUARDA EL TOTAL DE DURACIÓN DE LA CANCION EN MILIS

    private int porcentaje;
    private WorkerStringProgress workerStringProgress; // para pintar los minutos en la barra
    private boolean imprimirBarraDeProgreso;
    
    private DgBuscar dialogBuscar;
    
    private int tabActual; // esto es para el drag and drop

    public JPlay() {
        initComponents();
        canciones = new ArrayList<>();
        biblioteca = new Biblioteca();

        isRandom = false;

        crearArbolExplorer();
        listenerClickDerechoSobreArbol();
        crearPopUpTree();
        crearPopUpBiblioteca();
        crearPopUpCover();

        btnCancelarCarga.setEnabled(false);
//        indiceActual = -1;
        if (SAVE) {
            cargarSave();
        }

        this.setTitle(Rules.NOMBRE + " - " + Rules.VERSION);
        isPlay = false;
        isStop = true;
        repetirCancion = opRepetirCancion.isSelected();
        slideVol.setVisible(false);

        crearListenerTitulosTabla();
        createKeybindings(tablaCanciones);
        createKeybindings(tablaBiblioteca);

        // esto es para que no se pueda mover las columnas
        tablaBiblioteca.getTableHeader().setReorderingAllowed(false);

        cargarArbolConCanciones(false);

        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/
        jScrollPane2.setVisible(false);
        lblLimpiar.setVisible(false);
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/

        setIconImage(icono);

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
                (int) Rules.COVER_DIMENSION.getWidth(),
                (int) Rules.COVER_DIMENSION.getHeight(),
                Image.SCALE_SMOOTH);
//        lblCaratula.setIcon(new ImageIcon(icono));
        lblCover.setIcon(new ImageIcon(icono));
        lblCover.setText(null);
//        lbl2.setText(null);

        setBounds(0, 0, 1200, 700);
        setLocationRelativeTo(null);
        hCover = null;

        progress.setStringPainted(true);
        lblCover.requestFocus();
//        jSplitPane1.setDividerLocation(0.0);
//        jSplitPane2.setDividerLocation(1);
//        inicializarBarraProgreso();
        cargarArbolConCancionesMasEscuchadas();
        initFonts();
//        Properties properties = System.getProperties();
//        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
//   
//        for (Map.Entry<Object, Object> entry : entrySet) {
//            System.out.println(entry);
//        }
//        togVol.setVisible(false);
        biblioteca.printAlbums();
        imprimirBarraDeProgreso = true;
        
        initBuscar();
        initDragDropTabbedPane();
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

        dialogCanciones = new javax.swing.JDialog();
        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        pnlCoverArt = new javax.swing.JPanel();
        lblCover = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaBiblioteca = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCanciones = new javax.swing.JTable();
        lblLimpiar = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeSong = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        treeMasTocadas = new javax.swing.JTree();
        lblInfoCarga = new javax.swing.JLabel();
        btnCancelarCarga = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        progress = new javax.swing.JProgressBar();
        jPanel5 = new javax.swing.JPanel();
        lblTema = new javax.swing.JLabel();
        lblArtista = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnPause = new javax.swing.JButton();
        btnTocarAnterior = new javax.swing.JButton();
        btnTocarSiguiente = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnExaminar = new javax.swing.JButton();
        opAleatorio = new javax.swing.JCheckBox();
        opRepetirCancion = new javax.swing.JCheckBox();
        togVol = new javax.swing.JToggleButton();
        slideVol = new javax.swing.JSlider();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(254, 254, 254));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(254, 254, 254));

        pnlCoverArt.setBackground(new java.awt.Color(254, 254, 254));
        pnlCoverArt.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlCoverArt.setLayout(new java.awt.BorderLayout());

        lblCover.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCover.setText("[COVER]");
        pnlCoverArt.add(lblCover, java.awt.BorderLayout.CENTER);

        jPanel6.setBackground(new java.awt.Color(254, 254, 254));
        jPanel6.setOpaque(false);

        tabbedPane.setToolTipText("");
        tabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tabbedPaneMouseReleased(evt);
            }
        });

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

        tabbedPane.addTab("Explorer", jScrollPane3);

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

        tabbedPane.addTab("Biblioteca", jScrollPane1);

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

        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
        lblLimpiar.setForeground(new java.awt.Color(254, 254, 254));
        lblLimpiar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLimpiar.setText("Limpiar");
        lblLimpiar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblLimpiar.setOpaque(true);
        lblLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblLimpiarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLimpiarMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLimpiarMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLimpiarMouseEntered(evt);
            }
        });

        treeSong.setToolTipText("Doble click en título para cargar la biblioteca");
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
            .addComponent(lblLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPane.addTab("Lista actual", jPanel3);

        jPanel2.setLayout(new java.awt.BorderLayout());

        treeMasTocadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeMasTocadasMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(treeMasTocadas);

        jPanel2.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("+ escuchadas", jPanel2);

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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarCarga))
            .addComponent(tabbedPane)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelarCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(254, 254, 254));
        jPanel7.setOpaque(false);

        progress.setBackground(new java.awt.Color(254, 254, 254));
        progress.setOpaque(false);
        progress.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                progressMouseDragged(evt);
            }
        });
        progress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                progressMouseReleased(evt);
            }
        });

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
                    .addComponent(lblTema, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jPanel4.setBackground(new java.awt.Color(254, 254, 254));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnPause.setBackground(new java.awt.Color(76, 175, 80));
        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_play_arrow_white_24dp_1x.png"))); // NOI18N
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        btnTocarAnterior.setBackground(new java.awt.Color(63, 81, 181));
        btnTocarAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_skip_previous_white_24dp_1x.png"))); // NOI18N
        btnTocarAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTocarAnteriorActionPerformed(evt);
            }
        });

        btnTocarSiguiente.setBackground(new java.awt.Color(63, 81, 181));
        btnTocarSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_skip_next_white_24dp_1x.png"))); // NOI18N
        btnTocarSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTocarSiguienteActionPerformed(evt);
            }
        });

        btnStop.setBackground(new java.awt.Color(244, 67, 54));
        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_stop_white_24dp_1x.png"))); // NOI18N
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        btnExaminar.setBackground(new java.awt.Color(63, 81, 181));
        btnExaminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_folder_open_white_24dp_1x.png"))); // NOI18N
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
                        .addComponent(btnTocarAnterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTocarSiguiente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStop)
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
                    .addComponent(btnTocarSiguiente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPause, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTocarAnterior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExaminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(opRepetirCancion)
                    .addComponent(opAleatorio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        togVol.setBackground(new java.awt.Color(255, 152, 0));
        togVol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/xjplay/recursos/iconos/material/ic_volume_up_white_24dp_1x.png"))); // NOI18N
        togVol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togVolActionPerformed(evt);
            }
        });

        slideVol.setBackground(new java.awt.Color(254, 254, 254));
        slideVol.setMaximum(40);
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
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                slideVolMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                slideVolMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(togVol, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slideVol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(togVol)
                    .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlCoverArt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlCoverArt, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                        cargarCancionesALista(f);
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
                cargarCancionesAListaGrafica(false);
//            if(r == null){
//                try {
//                    r = new Reproductor(canciones.get(0), this);
//                    r.play();
//                } catch (BasicPlayerException ex) {
//                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }

//            setSlideTime(canciones.get(0).getDuracionEnMilis());
//                indiceActual = 0;
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
//    //                                System.out.println("Aún esta la canción "+r.getCancionActual()+" ["+r.getPosition()/1000+"]");
//                                    jLabel1.setText(r.getCancionActual().getDuracionAsString(r.getPosition()) + " / "+ r.getCancionActual().getDuracionAsString());
//                                    System.out.println("Aún esta la canción ["+r.getCancionActual().getNombre()+"] "+r.getCancionActual().getDuracionAsString(r.getPosition()));
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
                        reproducirCancionActual();
                        isPlay = true;
                        isStop = false;
                    } else {
                        reproductor.resume();
                    }
                }
            }
//            else if (indiceActual != -1) {
////                r = new Reproductor(canciones.get(indiceActual), this);
////                r.play();      
//                reproducir(canciones.get(indiceActual));
//                isPlay = true;
//                isStop = false;
//                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
//            }
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
        cargarCancionesAListaGrafica(false);
//        if (reproductor != null) {
//            indiceActual = getIndice(reproductor.getCancionActual());
//        } else {
//            indiceActual = 0;
//        }
//        System.out.println("Indice actual : " + indiceActual);
    }//GEN-LAST:event_btnCancelarCargaActionPerformed

    private void btnTocarAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTocarAnteriorActionPerformed
        if (!canciones.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repetirCancion) {
                reproducirCancionActual();
            } else {
                reproducirAnterior();
            }
        }
    }//GEN-LAST:event_btnTocarAnteriorActionPerformed

    private void btnTocarSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTocarSiguienteActionPerformed
        if (!canciones.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repetirCancion) {
                reproducirCancionActual();
            } else {
                reproducirSiguiente();
            }
        }
    }//GEN-LAST:event_btnTocarSiguienteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (SAVE) {
            try {
                Guardar g = new Guardar();

                g.canciones = canciones;
//                g.indiceActual = indiceActual;

                IO.escribirObjetoEn(g, Ruta.SAVE);
                IO.escribirObjetoEn(biblioteca, Ruta.BIBLIOTECA);
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void tablaBibliotecaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMouseReleased
        if (evt.getClickCount() == 2) {
            tocarCancionSeleccionadaEnTablaBiblioteca();
        }
    }//GEN-LAST:event_tablaBibliotecaMouseReleased

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        if (reproductor != null) {
            try {
                reproductor.stop();
                isPlay = false;
                isStop = true;
                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PLAY)));
                setSlideValue(0);
                workerStringProgress.cancel(true);
//                workerStringProgress = null;
                progress.setString("0:00");
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private void tablaBibliotecaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            popUpBiblio.show(tablaBiblioteca, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaBibliotecaMousePressed

    private void opRepetirCancionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opRepetirCancionActionPerformed
        repetirCancion = opRepetirCancion.isSelected();
    }//GEN-LAST:event_opRepetirCancionActionPerformed

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

    private void tablaBibliotecaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaBibliotecaKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tocarCancionSeleccionadaEnTablaBiblioteca(); // xD
        }
    }//GEN-LAST:event_tablaBibliotecaKeyReleased

    private void treeSongMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeSongMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSong.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Cancion) {
                    Cancion c = (Cancion) o;

//                    indiceActual = canciones.indexOf(c);
//                    System.out.println("Índice actual: " + indiceActual);
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

    private void progressMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseReleased
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), true);
        imprimirBarraDeProgreso = true;
    }//GEN-LAST:event_progressMouseReleased

    private void progressMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseDragged
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), false);
        imprimirBarraDeProgreso = false;
    }//GEN-LAST:event_progressMouseDragged

    private void lblLimpiarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseReleased
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
        canciones = new ArrayList<>();
        cargarCancionesAListaGrafica(false);
    }//GEN-LAST:event_lblLimpiarMouseReleased

    private void lblLimpiarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMousePressed
        lblLimpiar.setBackground(new java.awt.Color(26, 35, 126));
    }//GEN-LAST:event_lblLimpiarMousePressed

    private void lblLimpiarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseExited
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
    }//GEN-LAST:event_lblLimpiarMouseExited

    private void lblLimpiarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseEntered
        lblLimpiar.setBackground(new java.awt.Color(92, 107, 192));
    }//GEN-LAST:event_lblLimpiarMouseEntered

    private void tablaCancionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaCancionesKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            TMCancion model = (TMCancion) tablaCanciones.getModel();
            int index = tablaCanciones.getSelectedRow();
            Cancion c = (Cancion) model.getValueAt(index, 0);

            reproducir(c);
            //            tablaCanciones.getSelectionModel().clearSelection();
//            tablaCanciones.getSelectionModel().setSelectionInterval(indiceActual, indiceActual);
        }
    }//GEN-LAST:event_tablaCancionesKeyReleased

    private void tablaCancionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCancionesMouseReleased
        if (evt.getClickCount() == 2) {
            TMCancion model = (TMCancion) tablaCanciones.getModel();
//            indiceActual = tablaCanciones.getSelectedRow();
//            Cancion c = (Cancion) model.getValueAt(indiceActual, 0);
//
//            reproducir(c);
        }
    }//GEN-LAST:event_tablaCancionesMouseReleased

    private void treeMasTocadasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMasTocadasMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMasTocadas.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Cancion) {
                    Cancion c = (Cancion) o;

                    canciones = biblioteca.getCancionesMasReproducidas();
                    // true es que quiero reproducir las canciones mas tocadas
                    // esto hace que se cree sólo un disco gráfico en la lista actual
                    cargarCancionesAListaGrafica(true);

                    reproducir(c);

                    tabbedPane.setSelectedIndex(2);
                }
            }
        }
    }//GEN-LAST:event_treeMasTocadasMouseReleased

    private void tabbedPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabbedPaneMouseReleased
        if(evt.getClickCount() == 2){
            if(tabbedPane.getSelectedIndex() == 1){
                cargarCancionesABiblioteca(biblioteca.getCanciones());
            }
        }
    }//GEN-LAST:event_tabbedPaneMouseReleased

    private void cargarSave() {
        if (new File(Ruta.SAVE).exists()) {
            try {

                Guardar g = (Guardar) IO.leerObjetoDesde(Ruta.SAVE);

                canciones = g.canciones;
                biblioteca = (Biblioteca) IO.leerObjetoDesde(Ruta.BIBLIOTECA);

                boolean e = true;

                for (Cancion c : biblioteca.getCanciones()) {
                    if (!c.exists()) {
                        e = false;
                        break;
                    }
                }

                if (!e) {
                    int cont = 0;
                    if (JOptionPane.showConfirmDialog(this, "Se ha encontrado por lo menos 1 canción que no existe en la biblioteca. ¿Desea analizar la biblioteca completa?") == JOptionPane.YES_OPTION) {

                        List<Cancion> cancBiblio = biblioteca.getCanciones();
                        cargarCancionesABiblioteca(cancBiblio);
                        for (Cancion c : cancBiblio) {
                            if (!c.exists()) {
                                cont++;
                            }
                        }

                        if (JOptionPane.showConfirmDialog(this, "Se han encontrado " + cont + " canciones que no existen. ¿Desea eliminarlas?") == JOptionPane.YES_OPTION) {
                            int cant = biblioteca.removerNoExistentes();
                            JOptionPane.showMessageDialog(this, "Se han eliminado " + cant + " canciones de la biblioteca!", "Info", JOptionPane.INFORMATION_MESSAGE);

                            Iterator<Cancion> iterator = canciones.iterator();

                            Cancion c;
                            cant = 0;
                            while (iterator.hasNext()) {
                                c = iterator.next();

                                if (!biblioteca.estaCancion(c)) {
                                    iterator.remove();
                                    cant++;
                                }
                            }

                            if (cant != 0) {
                                JOptionPane.showMessageDialog(this, "Se han eliminado " + cant + " canciones de la lista principal!", "Info", JOptionPane.INFORMATION_MESSAGE);
                            }

                        }
                    }
                }

                System.out.println("Cantidad de canciones cargadas del save: " + canciones.size());
//                indiceActual = g.indiceActual;
//                System.out.println("Índice actual: " + indiceActual);
                cargarCancionesAListaGrafica(false);
                cargarCancionesABiblioteca(biblioteca.getCanciones());
                imprimirTemaActual();

            } catch (InvalidClassException ex) {
                System.out.println("EX: " + ex.getMessage());
                biblioteca = new Biblioteca();
                canciones = biblioteca.getCanciones();
                cargarDefault();
            } catch (ClassNotFoundException | IOException ex) {
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
     */
    private void cargarCancionesABiblioteca(File raiz) throws IOException, InterruptedException {
        if (raiz.listFiles() != null) {
            for (File a : raiz.listFiles()) {
                if (a.isDirectory()) {
                    cargarCancionesABiblioteca(a);
                } else if (Validar.isCancion(a)) {
                    Cancion c = new Cancion(a.getPath());
                    biblioteca.add(c);
                    lblInfoCarga.setText("Agregando " + c);
                }
            }
        }else{
            Cancion c = new Cancion(raiz.getPath());
            biblioteca.add(c);
            lblInfoCarga.setText("Agregando " + c);
        }
    }

    private void cargarCancionesALista(File raiz) throws IOException, InterruptedException {
        if (raiz.listFiles() != null) {
            for (File a : raiz.listFiles()) {
                if (a.isDirectory()) {
                    cargarCancionesALista(a);
                } else if (Validar.isCancion(a)) {
                    Cancion c = new Cancion(a.getPath());
                    canciones.add(c);
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
    private javax.swing.JButton btnStop;
    private javax.swing.JButton btnTocarAnterior;
    private javax.swing.JButton btnTocarSiguiente;
    private javax.swing.JDialog dialogCanciones;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblArtista;
    private javax.swing.JLabel lblCover;
    private javax.swing.JLabel lblInfoCarga;
    private javax.swing.JLabel lblLimpiar;
    private javax.swing.JLabel lblTema;
    private javax.swing.JCheckBox opAleatorio;
    private javax.swing.JCheckBox opRepetirCancion;
    private javax.swing.JPanel pnlCoverArt;
    private javax.swing.JProgressBar progress;
    private javax.swing.JSlider slideVol;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tablaBiblioteca;
    private javax.swing.JTable tablaCanciones;
    private javax.swing.JToggleButton togVol;
    private javax.swing.JTree tree;
    private javax.swing.JTree treeMasTocadas;
    private javax.swing.JTree treeSong;
    // End of variables declaration//GEN-END:variables

    private void setSlideTime(int totalBytes) {
        this.totalBytes = totalBytes;
        progress.setMaximum(totalBytes);
    }

    private void setSlideValue(int readedBytes) {
//        System.out.println("cambio al valor: "+valor);
//        int ancho = progress.getWidth();;
//        int pixActual = (valorEnMilis * ancho) / totalMilis;
//        progress.setValue(readedBytes);W
//        System.out.println(readedBytes);
        if (imprimirBarraDeProgreso) {
//            progress.setStringPainted(true);
            progress.setValue(readedBytes);
//            progress.setString(readedBytes + " bytes / " + totalBytes + " bytes");
        }

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
//        imprimirTemaActual(milis);
//        System.out.println("BYTES LEIDOS: "+bytesread);
//        System.out.println(properties);
//        System.out.println("MS: "+microseconds);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
        System.out.println("STATE UPDATED: " + bpe.toString());
        switch (bpe.getCode()) {
            case BasicPlayerEvent.EOM:
                /*
                End of music: Se ejecuta esto cuando finaliza de tocar un tema
                 */
                if (!repetirCancion) {
                    if (isRandom) {
                        reproducirRandom();
                    } else {
                        reproducirSiguiente();
                    }
                } else {
                    reproducirCancionActual();
                }

                System.out.println("END_OF_MUSIC");
                break;
            case BasicPlayerEvent.STOPPED:
                break;

            case BasicPlayerEvent.RESUMED:
                workerStringProgress.resume();
                break;
            case BasicPlayerEvent.PAUSED:
                workerStringProgress.pausar();
                break;
//            setSlideTime(bpe.getPosition());
            case BasicPlayerEvent.SEEKED:
                break;
            case BasicPlayerEvent.OPENED:
                // el indice es -1 cuando cargo desde la biblioteca
//            if (indiceActual != -1) {
                setSlideTime((int) reproductor.getCancionActual().length());
//            }
                break;
            default:
                break;
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

    private void crearArbolExplorer() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");
        File[] discos = File.listRoots();

        for (File disco : discos) {
            raiz.add(new DefaultMutableTreeNode(disco));
        }

        tree.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        tree.setRootVisible(false);

        tree.setCellRenderer(new CellRenderExplorer(
                new ImageIcon(
                        CellRenderExplorer.crearIcono(
                                Ruta.ICONO_MUSIC).getImage().
                                getScaledInstance(
                                        Rules.ICON_EXPLORER_MUSIC_SIZE,
                                        Rules.ICON_EXPLORER_MUSIC_SIZE,
                                        Image.SCALE_SMOOTH
                                )
                ),
                new ImageIcon(
                        CellRenderExplorer.crearIcono(
                                Ruta.ICONO_FOLDER).getImage().
                                getScaledInstance(
                                        Rules.ICON_EXPLORER_SIZE,
                                        Rules.ICON_EXPLORER_SIZE,
                                        Image.SCALE_SMOOTH
                                )
                )
        )
        );
    }

    private void cargarArbolConCanciones(boolean isMasTocadas) {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        if (isMasTocadas) {
            Album a = new Album("JPLAY", "Canciones más escuchadas!");

            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(a);
            for (Cancion c : canciones) {
                a.addCancion(c);

                disco.add(new DefaultMutableTreeNode(c));
            }
            
            
//            a.setLastFMImageCover(new ImageIcon(Ruta.ICONO_JPLAY));
            raiz.add(disco);
        } else {
            List<Album> albumes = getDiscos(canciones);

            for (Album a : albumes) {
                DefaultMutableTreeNode disco = new DefaultMutableTreeNode(a);

                for (Cancion cancion : a.getCanciones()) {
                    disco.add(new DefaultMutableTreeNode(cancion));
                }

                raiz.add(disco);
            }
        }

        treeSong.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeSong.setRootVisible(false);

        if (isMasTocadas) {
            treeSong.expandRow(0);
        }

        String rutaIcon = (isMasTocadas ? Ruta.ICONO_CORAZON : Ruta.ICONO_JPLAY);

        System.out.println(rutaIcon);

        treeSong.setCellRenderer(
                new CellRenderCancionLista(
                        CellRenderExplorer.crearIcono(Ruta.ICONO_PLAY_ARBOL),
                        CellRenderExplorer.crearIcono(rutaIcon)
                )
        );
    }

    private void cargarArbolConCancionesMasEscuchadas() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Cancion> masRepro = biblioteca.getCancionesMasReproducidas();

        for (Cancion c : masRepro) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeMasTocadas.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeMasTocadas.setRootVisible(false);
//        treeSong.expandRow(0);

        treeMasTocadas.setCellRenderer(
                new CellRenderCancionMasTocada(
                        CellRenderExplorer.crearIcono(Ruta.ICONO_PLAY_ARBOL),
                        CellRenderExplorer.crearIcono(Ruta.ICONO_CD_ARBOL)
                )
        );

        tabbedPane.setTitleAt(3, "+ escuchadas (" + masRepro.size() + ")");
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
                            cargarCancionesALista(f);
                            cargarCancionesAListaGrafica(false);
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
                            cargarCancionesALista(f);
                            cargarCancionesAListaGrafica(false);
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
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
                            
                            cargarCancionesABiblioteca(f);
                            cargarCancionesABiblioteca(biblioteca.getCanciones());
                            biblioteca.procesarAlbums();
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
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
                    biblioteca.remover(c);
                }

                cargarCancionesABiblioteca(biblioteca.getCanciones());
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

    /**
     *
     * @param isMasTocadas Si es verdadero, quiero cargar la lista de canciones
     * mas tocadas
     */
    private void cargarCancionesAListaGrafica(boolean isMasTocadas) {

        //ordenar acá
        System.out.println("Se cargaron " + canciones.size() + " canciones a la lista principal");
        if (!isMasTocadas) {
            Collections.sort(canciones, new Comparator<File>() {

                @Override
                public int compare(File f1, File f2) {
                    return f1.compareTo(f2);
                }
            });
        }
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
//        tablaCanciones.getTableHeader().setUI(null);
//        tablaCanciones.setRowHeight(20);
//        tablaCanciones.setModel(new TMCancion(canciones));

        cargarArbolConCanciones(isMasTocadas);
//
//        tablaCanciones.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//        tablaCanciones.getColumnModel().getColumn(0).setPreferredWidth(300);
        /**/
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        tablaCanciones.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
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

//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(0).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(1).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(2).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(3).getMinWidth());
        tablaBiblioteca.getColumnModel().getColumn(0).setPreferredWidth(Rules.TRACK_NUMBER_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(1).setPreferredWidth(Rules.ARTIST_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(2).setPreferredWidth(Rules.ALBUM_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(3).setPreferredWidth(Rules.ARTIST_COLUMN_SIZE);
        /**/
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        tablaBiblioteca.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/

        btnCancelarCarga.setEnabled(false);
    }

    private void reproducir(final Cancion cancion) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    procesarCover(cancion);
                }
            }).start();
//            lblCaratula.setIcon(new ImageIcon(icono)); 

//            pnlCoverArt.updateUI();
            if (reproductor != null) {
                reproductor.stop();
            }

            reproductor = new Reproductor(cancion, this);

            reproductor.play();

            cancion.aumentarContadorReproducciones();

            setTitle(Rules.NOMBRE + " - " + Rules.VERSION + " [" + cancion.getAutor() + " - " + cancion.getNombre() + " (" + cancion.getCantidadReproducciones() + ")]");
            setVolumen(slideVol.getValue());
//            lblTemaActual.setText(c.getAutor()+" / "+c.getNombre() + " ("+c.getDuracionAsString()+")");

//            btnPause.setText("Pause");
            btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.PAUSE)));
            isPlay = true;
            isStop = false;

//            treeSong.setCellRenderer(
//                new CellRenderCancionLista(
//                        CellRenderExplorer.crearIcono("/jplay/recursos/iconos/1443349568_music.png"),
//                        CellRenderExplorer.crearIcono("/jplay/recursos/iconos/1453541047_emblem-cd.png")
//                )
//            );
//            System.out.println("Esta en biblioteca: "+biblioteca.estaCancion(cancion));
//            System.out.println("-----------------------------------------");
//            System.out.println("LISTADO DE MÁS REPRODUCCIONES");
//            System.out.println("-----------------------------------------");
//            List<Cancion> lista = biblioteca.getCancionesMasReproducidas();
//            for (Cancion c : lista) {
//                System.out.println("[" + c.getCantidadReproducciones() + "]" + c.getNombre());
//            }
//            System.out.println("-----------------------------------------");
            cargarArbolConCancionesMasEscuchadas();

            if (this.workerStringProgress != null) {
                this.workerStringProgress.cancel(true);
            }

            this.workerStringProgress = new WorkerStringProgress(progress, cancion.getDuracionAsString());

            this.workerStringProgress.execute();

            imprimirTemaActual();
            
            
        } catch (BasicPlayerException ex) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } 

    }

    private void reproducirCancionActual() {
        reproducir(canciones.get(canciones.indexOf(reproductor.getCancionActual())));
    }

//    private int getIndice(File f) {
//        return canciones.indexOf(f);
//    }
//    private void agregarCancion(Cancion cancion, List<Cancion> lista) {
//        lista.add(cancion);
//        lblInfoCarga.setText("Agregando " + cancion);
////        System.out.println("Agregando "+cancion);
//    }
    private void imprimirTemaActual() {
        String durActual = "0:00";
        String durTotal = "()";

        if (reproductor != null) {
//            if(milis != 0){
//                durActual = r.getCancionActual().getDuracionAsString(milis) + " / "+ r.getCancionActual().getDuracionAsString();
//            }else{
//                durActual = "0:00 / "+ r.getCancionActual().getDuracionAsString();
//            }

            lblArtista.setText(reproductor.getCancionActual().getAutor());
            lblTema.setText(reproductor.getCancionActual().getNombre() + " (" + reproductor.getCancionActual().getDuracionAsString() + ")");
        }
//        else if (indiceActual != -1) {
//            Cancion c = canciones.get(indiceActual);
//            durActual = "0:00 / " + c.getDuracionAsString();
//            lblArtista.setText(c.getAutor());
//            lblTema.setText(c.getNombre() + " (" + durActual + ")");
//        }
    }

    private void cargarDefault() {
        cargarCancionesABiblioteca(biblioteca.getCanciones());
        cargarCancionesAListaGrafica(false);
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

//        System.out.println(biblioteca.getAlbum(c));
        TMCancionBiblioteca model = (TMCancionBiblioteca) tablaBiblioteca.getModel();

        canciones = model.canciones;
//        indiceActual = fila;

        cargarCancionesAListaGrafica(false);

//        tablaCanciones.getSelectionModel().setSelectionInterval(fila, fila);
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
                                cargarCancionesALista(f.getParentFile());
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cargarCancionesAListaGrafica(false);
//                            indiceActual = getIndice(f);
//                            System.out.println("Indice actual : " + indiceActual);
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

    private List<Album> getDiscos(List<Cancion> lista) {
        List<Album> discos = new ArrayList<>();
        Album a;

        for (Cancion c : lista) {
            a = biblioteca.getAlbum(c);

            if(a != null){
                if (!discos.contains(a)) {
                    discos.add(a);
                }
            }
        }

        return discos;
    }

//    private void inicializarBarraProgreso() {
//        int ancho = progress.getWidth();
//        progress.setMaximum(ancho);
//        progress.setValue(ancho);
//    }
    private void cambiarProgress(int porc, boolean seek) {

        final float value = totalBytes * ((float) porc / (float) 100);
        progress.setValue((int) value);
//        progress.setString(value + " bytes");

//        progress.setString(porc + "%");
        if (seek) {
            try {
                this.workerStringProgress.cambiar(porc);
                reproductor.seek((long) value);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void procesarCover(Cancion cancion) {
        Album album = biblioteca.getAlbum(cancion);
        if (!cancion.exists()) { // si canción no existe
            if (JOptionPane.showConfirmDialog(
                    this,
                    cancion.exists() + "[" + cancion.getNombre() + "] no encontrada. "
                    + "¿Desea analizar la lista completa para eliminar los no encontrados?", "Error",
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                Iterator<Cancion> iterator = canciones.iterator();

                Cancion c;
                int cont = 0;
                while (iterator.hasNext()) {
                    c = iterator.next();

                    if (!c.exists()) {
                        canciones.remove(c);
                        cont++;
                    }
                }

                JOptionPane.showMessageDialog(this, "Se han eliminado " + cont + " canciones de la lista.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (!album.hasImagenes()) { // si el Album NO tiene una lista de imagenes
            System.out.println("La canción no tiene imágenes asociadas!");
            List<ImageIcon> fotos = Recurso.getFotos(cancion);
            System.out.println("Se han encontrado " + fotos.size() + " foto");

            if (!fotos.isEmpty()) {
                /*
                si la lista de fotos no esta vacía por lo menos hay una
                para poder comenzar el hilo de las caratulas
                 */
                album.setCovers(fotos);
                System.out.println("Se añadió una lista de fotos a la cancion [" + fotos.size() + " fotos]");
            } else if (!album.hasLastFMImage()) { // si el album no tiene una imagen desde LastFM
                try {
                    Image imLastFM = LastFM.getImage(cancion.getAutor(), cancion.getAlbum());
                    imLastFM = imLastFM.getScaledInstance(
                            (int) Rules.COVER_DIMENSION.getWidth(),
                            (int) Rules.COVER_DIMENSION.getHeight(),
                            Image.SCALE_SMOOTH);
                    album.setLastFMImageCover(new ImageIcon(imLastFM));
                    System.out.println("Se añadió una image desde LastFM!");
                } catch (Exception ex) {
                    /*Establezco la caratula por defecto (el disco)*/
//                        icono = icono.getScaledInstance(
//                                (int) Rules.COVER_DIMENSION.getWidth(),
//                                (int) Rules.COVER_DIMENSION.getHeight(),
//                                Image.SCALE_SMOOTH);
                    album.setDefaultCover(icono);
                    System.out.println("Se añadió una caratula POR DEFECTO --> " + ex.getMessage());
                }

            }
        } else {
            System.out.println("La canción tiene caratula!");
        }

        if (album.getDefaultCover() != null) {
//                lblCaratula.setIcon(cancion.getDefaultCover());
            if (hCover != null) {
                hCover.interrupt();
            }
//            lbl2.setIcon(album.getDefaultCover());
            lblCover.setIcon(album.getDefaultCover());
            
        } else if (album.hasLastFMImage()) {
            if (hCover != null) {
                hCover.interrupt();
            }
            ImageIcon image = album.getLastFMImageCover();
//            lbl2.setIcon(image);
            lblCover.setIcon(image);
            
        } else {
            //quiere decir que la cancion tiene una lista de fotos

            //por ahora sólo cargo la primera foto
//                lblCaratula.setIcon(cancion.getImagenes().get(0));
            if (hCover != null) {
                hCover.interrupt();
            }

            hCover = new HiloCover(
                    lblCover,
                    album.getCovers());

            hCover.start();
        }

        ImageIcon cover;
        if (album.getDefaultCover() != null) { // si la cancion tiene un default cover
            cover = album.getDefaultCover();
        } else if (album.hasLastFMImage()) {
            cover = album.getLastFMImageCover();
        } else {// si no, pongo la primera imagen que encontro
            cover = album.getCovers().get(0);
        }
        
        setIconImage(cover.getImage());

//        treeSong.updateUI();
        treeSong.setCellRenderer(
                new CellRenderCancionLista(
                        CellRenderExplorer.crearIcono(Ruta.ICONO_PLAY_ARBOL),
                        CellRenderExplorer.crearIcono(Ruta.ICONO_JPLAY)
                )
        );

//        Notification.show(
//            cancion.getAutor(),
//            cancion.getNombre(),
//            cover,
//            8000, // Segundos en milis
//            new Dimension(100, 100),
//            soloUno
//        );
    }

    private void initFonts() {
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);

            lblTema.setFont(fuente.deriveFont(Font.BOLD, Rules.FONT_SIZE_NORMAL));
            lblArtista.setFont(fuente.deriveFont(Font.PLAIN, 11));
            opAleatorio.setFont(fuente.deriveFont(Font.PLAIN, 13));
            opRepetirCancion.setFont(fuente.deriveFont(Font.PLAIN, 13));
//            txtBuscar.setFont(fuente.deriveFont(Font.PLAIN, 13));
            tabbedPane.setFont(fuente.deriveFont(Font.PLAIN, 13));
            tablaBiblioteca.setFont(fuente.deriveFont(Font.PLAIN, 14));
            lblInfoCarga.setFont(fuente.deriveFont(Font.BOLD, 13));
            btnCancelarCarga.setFont(fuente.deriveFont(Font.PLAIN, 13));

            /*
            TAMBIEN CAMBIAR FUENTES EN LOS CELL RENDERERS (xjplay.model.tree)
             */
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reproduce el siguiente, si es el último reproduce el primero
     */
    private void reproducirSiguiente() {
        int indexActual = canciones.indexOf(reproductor.getCancionActual());
        indexActual++;
        if (indexActual < canciones.size()) {
            reproducir(canciones.get(indexActual));
        } else {
            reproducir(canciones.get(0));
        }
    }

    /**
     * Reproduce el anterior, si es el primero reproduce el último
     */
    private void reproducirAnterior() {
        int indexActual = canciones.indexOf(reproductor.getCancionActual());
        indexActual--;
        if (indexActual >= 0) {
            reproducir(canciones.get(indexActual));
        } else {
            // reproduce el último
            reproducir(canciones.get(canciones.size() - 1));
        }
    }

    private void reproducirRandom() {
        reproducir(canciones.get(getRandom()));
    }

    @Override
    public void search(String filtro) {
        
        lFiltrada = new ArrayList<>();

        int i = 1;
        for (Cancion c : biblioteca.getCanciones()) {
            if (c.getAutor().toLowerCase().contains(filtro)
                    || c.getAlbum().toLowerCase().contains(filtro)
                    || c.getNombre().toLowerCase().contains(filtro)) {
                lFiltrada.add(c);
            }
        }

        cargarCancionesABiblioteca(lFiltrada);
    }

    private void initBuscar() {
        //<editor-fold defaultstate="collapsed" desc="Código para escuchar a un boton para todos los componentes" >
        /*CON CTRL + F y f3 funciona el buscar*/
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK), "buscar");
        this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0), "buscar");
        /*CON CTRL + F y f3 funciona el buscar*/

        this.getRootPane().getActionMap().put("buscar", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(1);
                if (dialogBuscar == null) {
                    dialogBuscar = new DgBuscar(JPlay.this, false);
                    dialogBuscar.setIbuscar(JPlay.this);
                    dialogBuscar.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(),dialogBuscar .getHeight());
                    dialogBuscar.setVisible(true);
                } else {
//                    dialogBuscar.resetTextField();
                    dialogBuscar.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(),dialogBuscar .getHeight());
                    dialogBuscar.setVisible(!dialogBuscar.isVisible());
                }
            }
        });
        /*Código para escuchar a un boton para todos los componentes*/
        // </editor-fold>
    }

    @Override
    public void focusOn() {
        tablaBiblioteca.setRowSelectionInterval(0, 0);
    }

    @Override
    public void cargarComboDeBusqueda(JComboBox cbo) {
        
        cbo.removeAllItems();
        cbo.addItem("");
        
        for (String artista : biblioteca.getArtistas()) {
            cbo.addItem(artista);
        }
        
        for (Album album : biblioteca.getAlbums()) {
            if(!album.getName().trim().equals("")){
                cbo.addItem(album.getName());
            }
        }
    }

    private void crearPopUpCover() {
        popCover = new JPopupMenu();
        
        JMenuItem itemEliminarCover = new JMenuItem("Eliminar Cover");

        //JPopupMenu.Separator sep = new JPopupMenu.Separator();

        
        
        itemEliminarCover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(hCover.isAlive()){
                        ImageIcon actualCover = hCover.getActualCover();

                        hCover.interrupt();

                        Album a = biblioteca.getAlbum(reproductor.getCancionActual());

                        a.removeImage(actualCover);

                        hCover = new HiloCover(lblCover, a.getCovers());
                        hCover.start();
                    }
                } catch (NullPointerException ex) {
                    System.out.println("Objeto HCOVER es nulo");
                }
            }
        });
        
        popCover.add(itemEliminarCover);
        
        lblCover.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarPopUpCover(e);
            }
        });
    }
    
    private void mostrarPopUpCover(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popCover.show(e.getComponent(), e.getX()+8, e.getY()+8);
        }
    }

    private void initDragDropTabbedPane() {
        tabbedPane.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
                    final List<File> archs = (List)o;
                    
                    hiloCargar = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (File f : archs) {
                                try {
                                    System.out.println(f);
                                    cargarCancionesABiblioteca(f);
                                    cargarCancionesABiblioteca(biblioteca.getCanciones());
                                    biblioteca.procesarAlbums();
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    });
                    
                    hiloCargar.start();
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                tabActual = tabbedPane.getSelectedIndex();
                tabbedPane.setSelectedIndex(1);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                tabbedPane.setSelectedIndex(tabActual);
            }
        }));
    }
}
