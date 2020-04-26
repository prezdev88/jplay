package cl.prezdev.xjplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons
import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.MusicLibrary;
import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.MusicPlayer;
import cl.prezdev.jplay.common.ImageProcessor;
import cl.prezdev.jplay.common.Util;
import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import cl.prezdev.xjplay.artist.list.BackAlbum;
import cl.prezdev.xjplay.cover.art.CoverArtThread;
import cl.prezdev.xjplay.list.cellrenderer.AlbumListCellRenderer;
import cl.prezdev.xjplay.list.cellrenderer.ArtistListCellRenderer;
import cl.prezdev.xjplay.list.model.AlbumListModel;
import cl.prezdev.xjplay.list.model.ArtistListModel;
import cl.prezdev.xjplay.model.lastFM.LastFM;
import cl.prezdev.xjplay.model.progress.WorkerStringProgress;
import cl.prezdev.xjplay.model.scan.MusicLibraryUiUpdate;
import cl.prezdev.xjplay.model.scan.ScanThread;
import cl.prezdev.xjplay.model.search.SearchDialog;
import cl.prezdev.xjplay.model.search.SearchListener;
import cl.prezdev.xjplay.recursos.Path;
import cl.prezdev.xjplay.recursos.Resource;
import cl.prezdev.xjplay.rules.Rule;
import cl.prezdev.xjplay.save.IO;
import cl.prezdev.xjplay.save.Save;
import cl.prezdev.xjplay.table.model.MusicLabrarySongTableModel;
import cl.prezdev.xjplay.table.model.SongTableModel;
import cl.prezdev.xjplay.tree.cell.renderer.ExplorerTreeCellRenderer;
import cl.prezdev.xjplay.tree.cell.renderer.FavoritesTreeCellRenderer;
import cl.prezdev.xjplay.tree.cell.renderer.SongListTreeCellRenderer;
import cl.prezdev.xjplay.tree.cell.renderer.MostPlayedSongsTreeCellRenderer;
import cl.prezdev.xjplay.utils.Validate;
import java.awt.Color;
import java.awt.Dimension;
import static java.awt.EventQueue.invokeLater;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class JPlay extends javax.swing.JFrame implements
        BasicPlayerListener, SearchListener, MusicLibraryUiUpdate {

    public static MusicPlayer musicPlayer;
    private MusicLibrary biblioteca;
    private List<Song> canciones; // son las canciones de la lista de reproducción actual
    private Thread hiloCargar;
    private JPopupMenu popUpExplorerTree;
    private JPopupMenu popUpBiblio;
    private JPopupMenu popCover;

    private final boolean SAVE = true; // ESTO ES SOLO PARA DEBUGGING
    private List<Song> lFiltrada;
    private List<ArtistCoverArt> coversArtistas;
    private boolean isPlay;
    private boolean isStop;
    private boolean isRandom;
    private boolean repetirCancion;
    private Image icono;
    private CoverArtThread hCover; // hilo para animación de caratulas

    private int totalBytes; // GUARDA EL TOTAL DE DURACIÓN DE LA CANCION EN MILIS

    private WorkerStringProgress workerStringProgress; // para pintar los minutos en la barra
    private boolean imprimirBarraDeProgreso;

    private SearchDialog dialogBuscar;

    private int tabActual; // esto es para el drag and drop

    private List<Album> albums;
    /*
        Esta lista la utilizo cuando guardo en SAVE.
        Ya que si solo guardo las canciones, el icono
        no se carga cuando por ejemplo, estoy escuchando
        las canciones favoritas.
     */

    private long msListaActual;            // Son los milisegundos totales de la lista de
    // reproducción actual.

    public JPlay() {
        initComponents();

        Rule.COVER_ART_DIMENSION = new Dimension(lblCover.getWidth(), lblCover.getHeight());

        icono = Resource.JPLAY_ICON;

        icono = icono.getScaledInstance(
            (int) Rule.COVER_ART_DIMENSION.getWidth(),
            (int) Rule.COVER_ART_DIMENSION.getHeight(),
            Image.SCALE_SMOOTH
        );

        canciones = new ArrayList<>();
        albums = new ArrayList<>();
        biblioteca = new MusicLibrary();

        isRandom = false;

        crearArbolExplorer();
        initExplorerTreePopUp();
        crearPopUpExplorerTree();
        crearPopUpBiblioteca();
        crearPopUpCover();

        btnCancelarCarga.setEnabled(false);
//        indiceActual = -1;

        if (SAVE) {
            cargarSave();
        }

        // @TODO: Crear Rule.APP_TITLE
        this.setTitle(Rule.NAME + " - " + Rule.VERSION);
        isPlay = false;
        isStop = true;
        repetirCancion = opRepetirCancion.isSelected();

        crearListenerTitulosTabla();
        createKeybindings(tablaCanciones);
        createKeybindings(tablaBiblioteca);

        // esto es para que no se pueda mover las columnas
        tablaBiblioteca.getTableHeader().setReorderingAllowed(false);

//        cargarArbolConCanciones(getDiscos(canciones));

        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/
        jScrollPane2.setVisible(false);
        lblLimpiar.setVisible(false);
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/

        setIconImage(icono);

        lblCover.setText(null);

        setBounds(0, 0, 800, 600);
        setLocationRelativeTo(null);
        hCover = null;

        lblCover.requestFocus();
        cargarArbolConCancionesMasEscuchadas();
        cargarArbolConFavoritos();
        
        biblioteca.printAlbumsToLog();
        imprimirBarraDeProgreso = true;

        initBuscar();
        initDragDropTabbedPane();
        initIconosTabs();
        initListaCoversArtistas();
        initIcons();

        // @TODO: cambiar a mainPrincipal (pero ya existe, ver con Netbeans)
        panelPrincipal.setBackground(Color.white);

        setBounds(0, 0, Rule.WIDTH, Rule.HEIGHT);
        setLocationRelativeTo(null);
        //@TODO: Intentar ordenar este constructor, pensar bien la forma de agrupar
    }

    private void initIcons() {
        lblAnterior.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_BACK_ICON)));
        lblSiguiente.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_NEXT_ICON)));
        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
        btnFav.setIcon(new ImageIcon(getClass().getResource(Path.FAVORITES_TAB_ICON)));
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
        mainPanel = new javax.swing.JPanel();
        panelPrincipal = new javax.swing.JPanel();
        lblCover = new javax.swing.JLabel();
        tabbedPrincipal = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeExplorer = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaBiblioteca = new javax.swing.JTable();
        panelListaActual = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCanciones = new javax.swing.JTable();
        lblLimpiar = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeSong = new javax.swing.JTree();
        panelMasEscuchadas = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        treeMasTocadas = new javax.swing.JTree();
        panelFavoritos = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        treeFavoritos = new javax.swing.JTree();
        panelLogger = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableLogger = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        listArtistas = new javax.swing.JList();
        progress = new javax.swing.JProgressBar();
        lblInfoCarga = new javax.swing.JLabel();
        btnCancelarCarga = new javax.swing.JButton();
        lblNombreCancion = new javax.swing.JLabel();
        lblArtista = new javax.swing.JLabel();
        slideVol = new javax.swing.JSlider();
        btnFav = new javax.swing.JToggleButton();
        opRepetirCancion = new javax.swing.JCheckBox();
        opAleatorio = new javax.swing.JCheckBox();
        lblDuracion = new javax.swing.JLabel();
        lblAnterior = new javax.swing.JLabel();
        lblPlay = new javax.swing.JLabel();
        lblSiguiente = new javax.swing.JLabel();

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

        lblCover.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCover.setText("[cv]");

        tabbedPrincipal.setToolTipText("");
        tabbedPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tabbedPrincipalMouseReleased(evt);
            }
        });

        treeExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeExplorerMouseReleased(evt);
            }
        });
        treeExplorer.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeExplorerValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(treeExplorer);

        tabbedPrincipal.addTab("Explorer", jScrollPane3);

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

        tabbedPrincipal.addTab("Biblioteca", jScrollPane1);

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

        treeSong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeSongMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(treeSong);

        javax.swing.GroupLayout panelListaActualLayout = new javax.swing.GroupLayout(panelListaActual);
        panelListaActual.setLayout(panelListaActualLayout);
        panelListaActualLayout.setHorizontalGroup(
                panelListaActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelListaActualLayout.setVerticalGroup(
                panelListaActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelListaActualLayout.createSequentialGroup()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPrincipal.addTab("Lista actual", panelListaActual);

        panelMasEscuchadas.setLayout(new java.awt.BorderLayout());

        treeMasTocadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeMasTocadasMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(treeMasTocadas);

        panelMasEscuchadas.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("+ escuchadas", panelMasEscuchadas);

        panelFavoritos.setLayout(new java.awt.BorderLayout());

        treeFavoritos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeFavoritosMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(treeFavoritos);

        panelFavoritos.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("Favoritos", panelFavoritos);

        panelLogger.setLayout(new java.awt.BorderLayout());

        tableLogger.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane6.setViewportView(tableLogger);

        panelLogger.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("Logger (DEV)", panelLogger);

        jPanel1.setLayout(new java.awt.BorderLayout());

        listArtistas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listArtistasMouseReleased(evt);
            }
        });
        jScrollPane8.setViewportView(listArtistas);

        jPanel1.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        tabbedPrincipal.addTab("Artistas", jPanel1);

        progress.setBackground(new java.awt.Color(254, 254, 254));
        progress.setForeground(new java.awt.Color(255, 255, 255));
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

        lblInfoCarga.setBackground(new java.awt.Color(254, 254, 254));
        lblInfoCarga.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        lblInfoCarga.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblInfoCarga.setText("[lblInfo]");

        btnCancelarCarga.setText("X");
        btnCancelarCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarCargaActionPerformed(evt);
            }
        });

        lblNombreCancion.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblNombreCancion.setText("Artista / Canción");

        lblArtista.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        lblArtista.setText("Artista / Canción");

        slideVol.setBackground(new java.awt.Color(255, 255, 255));
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

        btnFav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFavActionPerformed(evt);
            }
        });

        opRepetirCancion.setBackground(new java.awt.Color(254, 254, 254));
        opRepetirCancion.setText("Repetir Canción");
        opRepetirCancion.setOpaque(false);
        opRepetirCancion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opRepetirCancionActionPerformed(evt);
            }
        });

        opAleatorio.setBackground(new java.awt.Color(254, 254, 254));
        opAleatorio.setText("Shuffle");
        opAleatorio.setOpaque(false);
        opAleatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opAleatorioActionPerformed(evt);
            }
        });

        lblDuracion.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        lblDuracion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDuracion.setText("0:00 - 0:00");

        lblAnterior.setText("A");
        lblAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAnteriorMouseReleased(evt);
            }
        });

        lblPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblPlayMouseReleased(evt);
            }
        });

        lblSiguiente.setText("A");
        lblSiguiente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblSiguienteMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
                panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelPrincipalLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(tabbedPrincipal)
                                        .addGroup(panelPrincipalLayout.createSequentialGroup()
                                                .addComponent(lblAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblCover, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelPrincipalLayout.createSequentialGroup()
                                                                .addComponent(lblArtista, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(lblDuracion, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(lblNombreCancion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(5, 5, 5))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnCancelarCarga)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(opAleatorio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(opRepetirCancion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(slideVol, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFav, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
                panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(panelPrincipalLayout.createSequentialGroup()
                                                .addComponent(lblNombreCancion, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(lblArtista)
                                                        .addComponent(lblDuracion))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblPlay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblSiguiente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblAnterior, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblCover, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(slideVol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnCancelarCarga)
                                                        .addComponent(lblInfoCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(opAleatorio, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(opRepetirCancion, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(btnFav, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // @TODO: Desacoplar los listeners de volumeSlides
    private void slideVolMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseDragged
        setVolumen(slideVol.getValue());
    }//GEN-LAST:event_slideVolMouseDragged
    // @TODO: Eliminar
    private void slideVolMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseClicked
//        System.out.println("Clicked");
    }//GEN-LAST:event_slideVolMouseClicked
    // @TODO: Eliminar
    private void slideVolMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMousePressed
//        System.out.println("Pressed");
    }//GEN-LAST:event_slideVolMousePressed
    // @TODO: Eliminar
    private void slideVolMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_slideVolMouseWheelMoved
//        System.out.println("Wheel");
    }//GEN-LAST:event_slideVolMouseWheelMoved
    // @TODO: Desacoplar los listeners de explorerTree
    private void treeExplorerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeExplorerMouseReleased
        if (evt.getClickCount() == 2) {
            procesarCancionArbol();
        }
    }//GEN-LAST:event_treeExplorerMouseReleased

    private void treeExplorerValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeExplorerValueChanged
        // acá cargo los subdirectorios cuando hago click
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExplorer.getLastSelectedPathComponent();

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
    }//GEN-LAST:event_treeExplorerValueChanged

    private void btnCancelarCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarCargaActionPerformed
        // @TODO: intentar llamar a interrupt
        hiloCargar.stop();
        btnCancelarCarga.setEnabled(false);
        cargarArbolConCanciones(getDiscos(canciones));
    }//GEN-LAST:event_btnCancelarCargaActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (SAVE) {
            try {
                Save g = new Save();

                g.songs = canciones;
                g.indexTab = tabbedPrincipal.getSelectedIndex();
                g.cover = lblCover.getIcon();
                g.albums = albums;
                g.volume = slideVol.getValue();
                g.artistCoversArt = coversArtistas;

                IO.escribirObjetoEn(g, Path.SAVE);
                IO.escribirObjetoEn(biblioteca, Path.MUSIC_LIBRARY);
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    /*
    * @TODO: Se me ocurrió una idea:
    *   1.- Crear proyecto para poner datos (model) en vista (o algo asi)
    *   2.- Crear proyecto que se encargue de los listeners de la vista (o algo asi)
    * */
    private void tablaBibliotecaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMouseReleased
        if (evt.getClickCount() == 2) {
            tocarCancionSeleccionadaEnTablaBiblioteca();
        }
    }//GEN-LAST:event_tablaBibliotecaMouseReleased

    private void tablaBibliotecaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBibliotecaMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            popUpBiblio.show(tablaBiblioteca, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tablaBibliotecaMousePressed

    private void opRepetirCancionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opRepetirCancionActionPerformed
        repetirCancion = opRepetirCancion.isSelected();
    }//GEN-LAST:event_opRepetirCancionActionPerformed
// @TODO: Eliminar
    private void slideVolMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_slideVolMouseReleased

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
                if (o instanceof Song) {
                    Song c = (Song) o;

                    reproducir(c);
                }
            }

        }
    }//GEN-LAST:event_treeSongMouseReleased

    private void progressMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseReleased
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), true);
        // seteo el volumen por un bug descubierto hoy (9 de marzo de 2018)
        setVolumen(slideVol.getValue());
        imprimirBarraDeProgreso = true;
    }//GEN-LAST:event_progressMouseReleased

    private void progressMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseDragged
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), false);
        imprimirBarraDeProgreso = false;
    }//GEN-LAST:event_progressMouseDragged

    private void lblLimpiarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseReleased
        // @TODO: Colocar color donde estan todos los colores
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
        canciones = new ArrayList<>();
        cargarArbolConCanciones(getDiscos(canciones));
    }//GEN-LAST:event_lblLimpiarMouseReleased

    private void lblLimpiarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMousePressed
        // @TODO: Colocar color donde estan todos los colores
        lblLimpiar.setBackground(new java.awt.Color(26, 35, 126));
    }//GEN-LAST:event_lblLimpiarMousePressed

    private void lblLimpiarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseExited
        // @TODO: Colocar color donde estan todos los colores
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
    }//GEN-LAST:event_lblLimpiarMouseExited

    private void lblLimpiarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseEntered
        // @TODO: Colocar color donde estan todos los colores
        lblLimpiar.setBackground(new java.awt.Color(92, 107, 192));
    }//GEN-LAST:event_lblLimpiarMouseEntered

    private void tablaCancionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaCancionesKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            SongTableModel model = (SongTableModel) tablaCanciones.getModel();
            int index = tablaCanciones.getSelectedRow();
            // @TODO: index = 0 ?
            Song c = (Song) model.getValueAt(index, 0);

            reproducir(c);
            //            tablaCanciones.getSelectionModel().clearSelection();
//            tablaCanciones.getSelectionModel().setSelectionInterval(indiceActual, indiceActual);
        }
    }//GEN-LAST:event_tablaCancionesKeyReleased

    private void tablaCancionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCancionesMouseReleased
        if (evt.getClickCount() == 2) {
            SongTableModel model = (SongTableModel) tablaCanciones.getModel();
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
                if (o instanceof Song) {
                    // @TODO: Una vez que lo de abajo pase a clase Album, cambiar nonbre a song
                    Song c = (Song) o;

                    canciones = biblioteca.getMostPlayedSongs();

                    /*Ahora debo poner las canciones en un album*/
                    // @TODO: Crear constructor sin año en Album, y pasándole una lista de canciones
                    Album a = new Album(Rule.NAME, "Canciones más escuchadas!", null);

                    // @TODO: Esto irá en el constructor de album
                    for (Song can : canciones) {
                        a.addSong(can);
                    }

                    List<ImageIcon> covers = new ArrayList<>();
                    covers.add(SongListTreeCellRenderer.getImageIcon(Path.HEART_ICON));
                    a.setCoversArt(covers);

                    List<Album> albums = new ArrayList<>();
                    albums.add(a);
                    /*Ahora debo poner las canciones en un album*/

                    cargarArbolConCanciones(albums);

                    reproducir(c);

                    tabbedPrincipal.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
                }
            }
        }
    }//GEN-LAST:event_treeMasTocadasMouseReleased

    private void tabbedPrincipalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabbedPrincipalMouseReleased
        if (evt.getClickCount() == 2) {
            if (tabbedPrincipal.getSelectedIndex() == 1) {
                cargarCancionesABiblioteca(biblioteca.getSongs());
            }
        }

        switch (tabbedPrincipal.getSelectedIndex()) {
            case Rule.TabIndex.MUSIC_LIBRARY:
                lblInfoCarga.setText(biblioteca.getLibraryDuration());
                break;
            case Rule.TabIndex.EXPLORER:
                lblInfoCarga.setText(biblioteca.getLibraryDuration());
                break;
            case Rule.TabIndex.FAVORITES:
                lblInfoCarga.setText(biblioteca.getFavoritesDuration());
                break;
            case Rule.TabIndex.CURRENT_SONGS_LIST:
                lblInfoCarga.setText("Lista actual --> " + Util.getDurationAsString(msListaActual));
                break;
            case Rule.TabIndex.LOGGER:
                lblInfoCarga.setText(biblioteca.getLibraryDuration());
                break;
            case Rule.TabIndex.MOST_PLAYED:
                lblInfoCarga.setText(biblioteca.getMostPlayedDuration());
                break;
        }
    }//GEN-LAST:event_tabbedPrincipalMouseReleased

    private void btnFavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFavActionPerformed
        if (musicPlayer.getCurrentSong() != null) {
            if (btnFav.isSelected()) {
                biblioteca.addFavoriteSong(musicPlayer.getCurrentSong());
            } else {
                biblioteca.removeFavoriteSong(musicPlayer.getCurrentSong());
            }
            cargarArbolConFavoritos();
        }
    }//GEN-LAST:event_btnFavActionPerformed

    private void treeFavoritosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeFavoritosMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeFavoritos.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                
                // @TODO: Una vez que lo de abajo pase a clase Album, cambiar nonbre a song
                if (o instanceof Song) {
                    Song c = (Song) o;

                    canciones = biblioteca.getFavoritesSongs();

                    /*Ahora debo poner las canciones en un album*/
                    Album a = new Album(Rule.NAME, "Favoritas!", null);

                    // @TODO: Esto irá en el constructor de album
                    for (Song can : canciones) {
                        a.addSong(can);
                    }

                    // @TODO: esto esta en el método mostPlayedSongTreeMouseReleased()
                    List<ImageIcon> covers = new ArrayList<>();
                    covers.add(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON));
                    a.setCoversArt(covers);

                    List<Album> albums = new ArrayList<>();
                    albums.add(a);
                    /*Ahora debo poner las canciones en un album*/

                    cargarArbolConCanciones(albums);

                    reproducir(c);

                    tabbedPrincipal.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
                }
            }
        }
    }//GEN-LAST:event_treeFavoritosMouseReleased

    private void lblPlayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPlayMouseReleased
        try {
            if (musicPlayer != null) {
                if (isPlay) {
                    isPlay = false;
                    if (Rule.FOREGROUND_COLOR == Color.black) {
                        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
                    } else {
                        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));
                    }
                    musicPlayer.pause();

                } else {
                    isPlay = true;
                    if (Rule.FOREGROUND_COLOR == Color.black) {
                        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PAUSE_ICON)));
                    } else {
                        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PAUSE_ICON)));
                    }
                    if (isStop) {
                        reproducirCancionActual();
                        isPlay = true;
                        isStop = false;
                    } else {
                        musicPlayer.resume();
                    }
                }
            }
//            else if (indiceActual != -1) {
////                r = new Reproductor(canciones.get(indiceActual), this);
////                r.play();
//                reproducir(canciones.get(indiceActual));
//                isPlay = true;
//                isStop = false;
//                btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(Ruta.IC_PAUSE_BLANCO)));
//            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lblPlayMouseReleased

    private void lblAnteriorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAnteriorMouseReleased
        if (!canciones.isEmpty()) {
            // @TODO: Idea: Métodos play en model (jplay) y llamar a listeners (xjplay)
            if (isRandom) {
                reproducirRandom();
            } else if (repetirCancion) {
                reproducirCancionActual();
            } else {
                reproducirAnterior();
            }
        }
    }//GEN-LAST:event_lblAnteriorMouseReleased

    private void lblSiguienteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSiguienteMouseReleased
        if (!canciones.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repetirCancion) {
                reproducirCancionActual();
            } else {
                reproducirSiguiente();
            }
        }
    }//GEN-LAST:event_lblSiguienteMouseReleased

    private void listArtistasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listArtistasMouseReleased
        if (evt.getClickCount() == 2) {
            Object ob = listArtistas.getSelectedValue();
            
            ListCellRenderer listCellRenderer = null;
            ListModel listModel = null;

            if (ob instanceof ArtistCoverArt) {
                ArtistCoverArt ca = (ArtistCoverArt) ob;
                List<Album> albumsByArtista = biblioteca.getAlbumsByArtist(ca.getArtistName());
                albumsByArtista.add(0, new BackAlbum());

                listCellRenderer = new AlbumListCellRenderer(albumsByArtista);
                listModel = new AlbumListModel(albumsByArtista);
            } else if (ob instanceof BackAlbum) {
                // quiere ir atrás, o sea a los artistas
                listCellRenderer = new ArtistListCellRenderer(coversArtistas);
                listModel = new ArtistListModel(coversArtistas);
            }
            
            listArtistas.setCellRenderer(listCellRenderer);
            listArtistas.setModel(listModel);
        }
    }//GEN-LAST:event_listArtistasMouseReleased

    private void cargarSave() {
        if (new File(Path.SAVE).exists()) {
            try {
                Save g = (Save) IO.readObject(Path.SAVE);

                canciones = g.songs;
                coversArtistas = g.artistCoversArt;

                /*Recuperando el volumen del usuario*/
                slideVol.setValue(g.volume);
                setVolumen(slideVol.getValue());

                /*Recuperando el volumen del usuario*/
                tabbedPrincipal.setSelectedIndex(g.indexTab);

                tabbedPrincipal.setTitleAt(Rule.TabIndex.LOGGER, "Logger (" + tableLogger.getRowCount() + ")");

                setCoverArt(g.cover);

                biblioteca = (MusicLibrary) IO.readObject(Path.MUSIC_LIBRARY);

                cargarArbolConCanciones(g.albums);
                cargarCancionesABiblioteca(biblioteca.getSongs());
                imprimirTemaActual();

            } catch (InvalidClassException ex) {
                biblioteca = new MusicLibrary();
                canciones = biblioteca.getSongs();
                coversArtistas = new ArrayList<>();
                cargarDefault();
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cargarDefault();
        }

        switch (tabbedPrincipal.getSelectedIndex()) {
            case Rule.TabIndex.MUSIC_LIBRARY:
                lblInfoCarga.setText(biblioteca.getLibraryDuration());
                break;
            case Rule.TabIndex.EXPLORER:
                lblInfoCarga.setText(biblioteca.getLibraryDuration());
                break;
            case Rule.TabIndex.FAVORITES:
                lblInfoCarga.setText(biblioteca.getFavoritesDuration());
                break;
            case Rule.TabIndex.CURRENT_SONGS_LIST:
                lblInfoCarga.setText("Lista actual --> " + Util.getDurationAsString(msListaActual));
                break;
            case Rule.TabIndex.LOGGER:
                lblInfoCarga.setText(biblioteca.getLibraryDuration());
                break;
            case Rule.TabIndex.MOST_PLAYED:
                lblInfoCarga.setText(biblioteca.getMostPlayedDuration());
                break;
        }
    }

    private void cargarArchivosEnNodoArbol(DefaultMutableTreeNode raiz, File ar) {
        List<File> ordenada = new ArrayList<>();
        if (ar.listFiles() != null) {
            File arConNombre;
            for (File a : ar.listFiles()) {
                if (!Validate.isHiddenFile(a)) {
                    try {
                        if (a.isDirectory() || Validate.isSong(a)) {
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
                } else if (Validate.isSong(a)) {
                    Song c = new Song(a.getPath());
                    biblioteca.addSong(c);
                    lblInfoCarga.setText("Agregando " + c);
                }
            }
        } else {
            Song c = new Song(raiz.getPath());
            biblioteca.addSong(c);
            lblInfoCarga.setText("Agregando " + c);
        }
    }

    private void cargarCancionesALista(File raiz) throws IOException, InterruptedException {
        if (raiz.listFiles() != null) {
            for (File a : raiz.listFiles()) {
                if (a.isDirectory()) {
                    cargarCancionesALista(a);
                } else if (Validate.isSong(a)) {
                    Song c = new Song(a.getPath());
                    canciones.add(c);
                }
            }
        }
    }

    public static void main(String args[]) {
        invokeLater(() -> new JPlay().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarCarga;
    private javax.swing.JToggleButton btnFav;
    private javax.swing.JDialog dialogCanciones;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lblAnterior;
    private javax.swing.JLabel lblArtista;
    private javax.swing.JLabel lblCover;
    private javax.swing.JLabel lblDuracion;
    private javax.swing.JLabel lblInfoCarga;
    private javax.swing.JLabel lblLimpiar;
    private javax.swing.JLabel lblNombreCancion;
    private javax.swing.JLabel lblPlay;
    private javax.swing.JLabel lblSiguiente;
    private javax.swing.JList listArtistas;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox opAleatorio;
    private javax.swing.JCheckBox opRepetirCancion;
    private javax.swing.JPanel panelFavoritos;
    private javax.swing.JPanel panelListaActual;
    private javax.swing.JPanel panelLogger;
    private javax.swing.JPanel panelMasEscuchadas;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JProgressBar progress;
    private javax.swing.JSlider slideVol;
    private javax.swing.JTabbedPane tabbedPrincipal;
    private javax.swing.JTable tablaBiblioteca;
    private javax.swing.JTable tablaCanciones;
    private javax.swing.JTable tableLogger;
    private javax.swing.JTree treeExplorer;
    private javax.swing.JTree treeFavoritos;
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

    // @TODO: BasicPlayerListener no debiese implementar acá
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
    public void opened(Object stream, Map properties) {}

    /**
     *
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        setSlideValue(bytesread);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
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

                break;
            case BasicPlayerEvent.STOPPED:
                break;

            case BasicPlayerEvent.RESUMED:
                workerStringProgress.resume();
                break;
            case BasicPlayerEvent.PAUSED:
                workerStringProgress.pause();
                break;
//            setSlideTime(bpe.getPosition());
            case BasicPlayerEvent.SEEKED:
                break;
            case BasicPlayerEvent.OPENED:
                // el indice es -1 cuando cargo desde la biblioteca
//            if (indiceActual != -1) {
                setSlideTime((int) musicPlayer.getCurrentSong().length());
//            }
                break;
            default:
                break;
        }
    }

    @Override
    public void setController(BasicController bc) {}

    private void setVolumen(int vol) {
        try {
            if (musicPlayer != null) {
                musicPlayer.setVolume(vol);
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

        treeExplorer.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeExplorer.setRootVisible(false);

        treeExplorer.setCellRenderer(new ExplorerTreeCellRenderer(
                        new ImageIcon(
                                ExplorerTreeCellRenderer.getImageIcon(
                                        Path.MUSIC_ICON).getImage().
                                        getScaledInstance(Rule.ICON_EXPLORER_MUSIC_SIZE,
                                                Rule.ICON_EXPLORER_MUSIC_SIZE,
                                                Image.SCALE_SMOOTH
                                        )
                        ),
                        new ImageIcon(
                                ExplorerTreeCellRenderer.getImageIcon(
                                        Path.FOLDER_ICON).getImage().
                                        getScaledInstance(Rule.ICON_EXPLORER_SIZE,
                                                Rule.ICON_EXPLORER_SIZE,
                                                Image.SCALE_SMOOTH
                                        )
                        )
                )
        );
    }

    /**
     * Este método carga los discos al arbol de lista actual
     *
     * @param disco
     */
    // @TODO: Pensar en separar la lógica de cargas de models en vistas
    // quizás en otro proyecto
    private void cargarArbolConCanciones(List<Album> albums) {
        this.albums = albums;
        //ordenar acá

        Collections.sort(canciones, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });

        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        if (albums != null) {
            DefaultMutableTreeNode disco;
            for (Album a : albums) {
                disco = new DefaultMutableTreeNode(a);

                for (Song cancion : a.getSongs()) {
                    disco.add(new DefaultMutableTreeNode(cancion));
                }

                raiz.add(disco);
            }
        }

        treeSong.setModel(new DefaultTreeModel(raiz));
        treeSong.setRootVisible(false);
        treeSong.expandRow(0);
        treeSong.setCellRenderer(new SongListTreeCellRenderer());

        btnCancelarCarga.setEnabled(false);
        msListaActual = 0;
        
        // @TODO: desacoplar esto
        for (Song c : canciones) {
            msListaActual += c.getMilliSeconds();
        }

        lblInfoCarga.setText("Lista actual --> " + Util.getDurationAsString(msListaActual));
    }

    private void cargarArbolConCancionesMasEscuchadas() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Song> masRepro = biblioteca.getMostPlayedSongs();

        for (Song c : masRepro) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeMasTocadas.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeMasTocadas.setRootVisible(false);
//        treeSong.expandRow(0);

        treeMasTocadas.setCellRenderer(new MostPlayedSongsTreeCellRenderer());

        tabbedPrincipal.setTitleAt(Rule.TabIndex.MOST_PLAYED, "+ escuchadas (" + masRepro.size() + ")");
    }

    private void cargarArbolConFavoritos() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Song> favoritos = biblioteca.getFavoritesSongs();

        for (Song c : favoritos) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeFavoritos.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeFavoritos.setRootVisible(false);
//        treeSong.expandRow(0);

        treeFavoritos.setCellRenderer(
            new FavoritesTreeCellRenderer()
        );

        tabbedPrincipal.setTitleAt(Rule.TabIndex.FAVORITES, "Favoritos (" + favoritos.size() + ")");
    }

    private void initExplorerTreePopUp() {
        /*Este codigo es para que cuando el usuario haga click secundario
         se seleccione la fila del arbol*/
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {

                    int selRow = treeExplorer.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = treeExplorer.getPathForLocation(e.getX(), e.getY());
                    treeExplorer.setSelectionPath(selPath);
                    if (selRow > - 1) {
                        treeExplorer.setSelectionRow(selRow);
                        popUpExplorerTree.show(treeExplorer, e.getX() + 10, e.getY() + 10);
//                       popup.show(tree, e.getX(), e.getY());
                    }
                }
            }
        };
        treeExplorer.addMouseListener(ml);
    }

    private void crearPopUpExplorerTree() {
        popUpExplorerTree = new JPopupMenu();
        JMenuItem itemAlistaNueva = new JMenuItem("A lista nueva");
        JMenuItem itemAlistaExistente = new JMenuItem("Añadir a existente");
        JMenuItem itemABiblioteca = new JMenuItem("Añadir a biblioteca");

        JPopupMenu.Separator sep = new JPopupMenu.Separator();

        popUpExplorerTree.add(itemAlistaNueva);
        popUpExplorerTree.add(itemAlistaExistente);
        popUpExplorerTree.add(sep);
        popUpExplorerTree.add(itemABiblioteca);

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
                            cargarArbolConCanciones(getDiscos(canciones));
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            // lblInfoCarga.setText("Cancelado");
//                            lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
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
                            cargarArbolConCanciones(getDiscos(canciones));
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

        itemABiblioteca.addActionListener((ActionEvent event) -> {
            hiloCargar = new Thread(() -> {
                btnCancelarCarga.setEnabled(true);
                try {
                    File f = getSelectedTreeFile();

                    cargarCancionesABiblioteca(f);
                    cargarCancionesABiblioteca(biblioteca.getSongs());
                    biblioteca.addSongsToAlbums();
                    biblioteca.addPath(f);
                    initListaCoversArtistas();
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("HILO CARGAR BIBLIOTECA TERMINADO!");
            });

            hiloCargar.start();
        });
    }

    private void crearPopUpBiblioteca() {
        popUpBiblio = new JPopupMenu();
        JMenuItem itemRemoverDeBiblioteca = new JMenuItem("Remover");
        JMenuItem itemTocarDisco = new JMenuItem("Reproducir Disco");
        JMenuItem itemAnadirDisco = new JMenuItem("Añadir Disco");

        popUpBiblio.add(itemTocarDisco);
        popUpBiblio.add(itemAnadirDisco);
        popUpBiblio.add(new Separator());
        popUpBiblio.add(itemRemoverDeBiblioteca);

        itemTocarDisco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Album> albums = new ArrayList<>();
                Song c;
                Album a;

                canciones = new ArrayList<>();

                // @TODO: Analizar esto (no lo he hecho)
                // Me tinca que se puede mejorar (y se debe mejorar)
                for (int fila : selRows) {
                    c = (Song) tablaBiblioteca.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);
                    a = biblioteca.getAlbum(c);

                    if (!albums.contains(a)) {
                        albums.add(a);

                        for (Song can : a.getSongs()) {
                            canciones.add(can);
                        }
                    }
                }

                cargarArbolConCanciones(getDiscos(canciones));
                reproducir(canciones.get(0));
                tabbedPrincipal.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
            }
        });

        itemAnadirDisco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Album> albums = new ArrayList<>();
                Song c;
                Album a;

                for (int fila : selRows) {
                    c = (Song) tablaBiblioteca.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);
                    a = biblioteca.getAlbum(c);

                    if (!albums.contains(a)) {
                        albums.add(a);

                        for (Song can : a.getSongs()) {
                            canciones.add(can);
                        }
                    }
                }

                cargarArbolConCanciones(getDiscos(canciones));
            }
        });

        itemRemoverDeBiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selRows = tablaBiblioteca.getSelectedRows();

                List<Song> canciones = new ArrayList<>();

                for (int fila : selRows) {
                    canciones.add((Song) tablaBiblioteca.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX));
                }

                for (Song c : canciones) {
                    biblioteca.removeSong(c);
                }

                cargarCancionesABiblioteca(biblioteca.getSongs());
            }
        });

    }

    private File getSelectedTreeFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExplorer.getLastSelectedPathComponent();
        if (node != null) {
            Object o = node.getUserObject();
            if (o instanceof File) {
//                return (File) o;
                return new File(((File) o).getPath());
                /*POr una razon que desconozco, el objeto File guardaba una referencia a Jplay
                por ende, cuando lo serializaba, enviaba un error.*/
            }
        }
        return null;
    }

    private void cargarCancionesABiblioteca(List<Song> canciones) {
        //ordenar acá
        Collections.sort(canciones, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.compareTo(f2);
            }
        });
        tablaBiblioteca.setModel(new MusicLabrarySongTableModel(canciones));
        tabbedPrincipal.setTitleAt(Rule.TabIndex.MUSIC_LIBRARY, "Biblioteca (" + canciones.size() + ")");
        tablaBiblioteca.getColumnModel().getColumn(0).setPreferredWidth(Rule.TRACK_NUMBER_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(1).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(2).setPreferredWidth(Rule.ALBUM_COLUMN_SIZE);
        tablaBiblioteca.getColumnModel().getColumn(3).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        
        btnCancelarCarga.setEnabled(false);
        biblioteca.addSongsToAlbums();
    }

    /*
    * @TODO: Primera idea:
    *   Llamar a play del model (jplay) y desde ahí, con listeners, cambiar el gui (xjplay)
    * */
    private void reproducir(Song cancion) {

        btnFav.setSelected(biblioteca.isFavoriteSong(cancion));

        try {
            new Thread(() -> {
                setCover(cancion);
            }).start();
//            lblCaratula.setIcon(new ImageIcon(icono));

//            pnlCoverArt.updateUI();
            if (musicPlayer != null) {
                musicPlayer.stop();
            }

            musicPlayer = new MusicPlayer(cancion, this);

            musicPlayer.play();

            cancion.increasePlayCount();

            setTitle(
                Rule.NAME + " - "
                + Rule.VERSION + " ["
                + cancion.getAuthor() + " - "
                + cancion.getName() + " (" + cancion.getPlayCount() + ")]"
            );
            
            setVolumen(slideVol.getValue());
//            lblTemaActual.setText(c.getAutor()+" / "+c.getNombre() + " ("+c.getDuracionAsString()+")");

//            btnPause.setText("Pause");
            if (Rule.FOREGROUND_COLOR == Color.white) {
                lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PAUSE_ICON)));
            } else {
                lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PAUSE_ICON)));
            }

            isPlay = true;
            isStop = false;

            cargarArbolConCancionesMasEscuchadas();

            if (this.workerStringProgress != null) {
                this.workerStringProgress.cancel(true);
            }

            String durationAsString = Util.getDurationAsString(cancion.getMicroseconds());
            this.workerStringProgress = new WorkerStringProgress(lblDuracion, durationAsString);

            this.workerStringProgress.execute();

            imprimirTemaActual();

        } catch (BasicPlayerException ex) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void reproducirCancionActual() {
        reproducir(canciones.get(canciones.indexOf(musicPlayer.getCurrentSong())));
    }

    private void imprimirTemaActual() {
        if (musicPlayer != null) {
            Song cancionActual = musicPlayer.getCurrentSong();
            
            lblArtista.setText(
                cancionActual.getAuthor()+ " - " + 
                cancionActual.getAlbum() + " (" + 
                cancionActual.getYear()+ ")"
            );
            
            String durationAsString = Util.getDurationAsString(cancionActual.getMicroseconds());
            lblNombreCancion.setText(cancionActual.getName()+ " (" + durationAsString + ")");
        }
    }

    private void cargarDefault() {
        cargarCancionesABiblioteca(biblioteca.getSongs());
        cargarArbolConCanciones(null);
//        lblCover.setIcon(new ImageIcon(icono));
        setCover(icono);
    }

    private void crearListenerTitulosTabla() {
        tablaBiblioteca.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaBiblioteca.columnAtPoint(e.getPoint());
                String name = tablaBiblioteca.getColumnName(col);
            }
        });
    }

    public int getRandom() {
        return new Random().nextInt(canciones.size());
    }

    private void tocarCancionSeleccionadaEnTablaBiblioteca() {
        int fila = tablaBiblioteca.getSelectedRow();
        Song c = (Song) tablaBiblioteca.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);

//        System.out.println(biblioteca.getAlbum(c));
        MusicLabrarySongTableModel model = (MusicLabrarySongTableModel) tablaBiblioteca.getModel();

        canciones = model.songs;
//        indiceActual = fila;

        cargarArbolConCanciones(getDiscos(canciones));

//        tablaCanciones.getSelectionModel().setSelectionInterval(fila, fila);
        reproducir(c);
    }

    // Método que se llama cuando hago doble click en un tema musical
    // o cuando apreto enter en el arbol
    private void procesarCancionArbol() {
        final File f = getSelectedTreeFile();
        if (f != null) {
            try {
                if (Validate.isSong(f)) {
                    canciones = new ArrayList<>();
                    hiloCargar = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            btnCancelarCarga.setEnabled(true);
                            try {
                                cargarCancionesALista(f.getParentFile());
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            cargarArbolConCanciones(getDiscos(canciones));
                        }
                    });

                    hiloCargar.start();
                    reproducir(new Song(f.getPath()));
                }
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // @TODO: Desacoplar
    private List<Album> getDiscos(List<Song> lista) {
        List<Album> discos = new ArrayList<>();
        Album a;

        for (Song c : lista) {
            a = biblioteca.getAlbum(c);

            if (a != null) {
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
                this.workerStringProgress.changeProgressBarValue(porc);
                musicPlayer.seek((long) value);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setCover(Song cancion) {
        setCover(icono);
        Album album = biblioteca.getAlbum(cancion);

        if (!cancion.exists()) { // si canción no existe
            if (JOptionPane.showConfirmDialog(
                    this,
                    cancion.exists() + "[" + cancion.getName()+ "] no encontrada. "
                            + "¿Desea analizar la lista completa para eliminar los no encontrados?", "Error",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Iterator<Song> iterator = canciones.iterator();

                Song c;
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
        } else if (!album.hasCoversArt()) { // si el Album NO tiene una lista de imagenes
            List<ImageIcon> fotos = Resource.getCoversArt(cancion);

            if (!fotos.isEmpty()) {
                /*
                si la lista de fotos no esta vacía por lo menos hay una
                para poder comenzar el hilo de las caratulas
                 */
                album.setCoversArt(fotos);
            } else { // no hay imagenes en la carpeta de la canción
                List<ImageIcon> covers = new ArrayList<>();
                try {
                    Image imLastFM = LastFM.getCoverArt(cancion.getAuthor(), cancion.getAlbum());
                    imLastFM = imLastFM.getScaledInstance((int) Rule.COVER_ART_DIMENSION.getWidth(),
                            (int) Rule.COVER_ART_DIMENSION.getHeight(),
                            Image.SCALE_SMOOTH);

                    covers.add(new ImageIcon(imLastFM));
                } catch (Exception ex) {
                    /*Establezco la caratula por defecto (el disco)*/
                    covers.add(new ImageIcon(icono));
                }
                album.setCoversArt(covers);
            }
        } 

        if (hCover != null) {
            hCover.interrupt();
        }

        setCoverArt(album.getCoverArt());

        setColorFondo(album);

        hCover = new CoverArtThread(lblCover, album.getCoversArt());
        hCover.start();

        setIconImage(album.getCoverArt().getImage());

        treeSong.setCellRenderer(
            new SongListTreeCellRenderer()
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

    /**
     * Reproduce el siguiente, si es el último reproduce el primero
     */
    private void reproducirSiguiente() {
        int indexActual = canciones.indexOf(musicPlayer.getCurrentSong());
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
        int indexActual = canciones.indexOf(musicPlayer.getCurrentSong());
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
        for (Song c : biblioteca.getSongs()) {
            if (c.getAuthor().toLowerCase().contains(filtro)
                    || c.getAlbum().toLowerCase().contains(filtro)
                    || c.getName().toLowerCase().contains(filtro)) {
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
                tabbedPrincipal.setSelectedIndex(Rule.TabIndex.MUSIC_LIBRARY);
                
                if (dialogBuscar == null) {
                    dialogBuscar = new SearchDialog(JPlay.this, false);
                    dialogBuscar.setSearchable(JPlay.this);
                    dialogBuscar.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), dialogBuscar.getHeight());
                    dialogBuscar.setVisible(true);
                } else {
                    dialogBuscar.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), dialogBuscar.getHeight());
                    dialogBuscar.setVisible(!dialogBuscar.isVisible());
                }
            }
        });
        /*Código para escuchar a un boton para todos los componentes*/
        // </editor-fold>
    }

    // este metodo se llama cuando apreta enter en buscar
    @Override
    public void focusOn(String filtro) {
        try {
            if (filtro.startsWith("/")) {

                // si es un comando, despues cargo de nuevo la biblioteca
                cargarCancionesABiblioteca(biblioteca.getSongs());

                String text = "";

                if (filtro.equalsIgnoreCase("/rutas")) {
                    text += "RUTAS:\n";

                    for (File ruta : biblioteca.getPaths()) {
                        text += ruta.getPath() + "\n";
                    }

                    JOptionPane.showMessageDialog(this, text);
                } else if (filtro.equalsIgnoreCase("/scan")) {
                    ScanThread s = new ScanThread(biblioteca, this);
                    s.start();
                } else if (filtro.equalsIgnoreCase("/favs")) {}
            }
            tablaBiblioteca.setRowSelectionInterval(0, 0);
        } catch (HeadlessException e) {
            // cae aca cuando no hay canciones en la tabla biblioteca
        }

    }

    @Override
    public void loadSearchComboBox(JComboBox cbo) {

        cbo.removeAllItems();
        cbo.addItem("");

        for (String artista : biblioteca.getArtistNames()) {
            cbo.addItem(artista);
        }

        for (Album album : biblioteca.getAlbums()) {
            if (!album.getName().trim().equals("")) {
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
                    if (hCover.isAlive()) {
                        Image actualCover = hCover.getCurrentCoverArt();

                        hCover.interrupt();

                        Album a = biblioteca.getAlbum(musicPlayer.getCurrentSong());

                        a.removeCoverArt(new ImageIcon(actualCover));

                        hCover = new CoverArtThread(lblCover, a.getCoversArt());
                        hCover.start();
                    }
                } catch (NullPointerException ex) {}
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
            popCover.show(e.getComponent(), e.getX() + 8, e.getY() + 8);
        }
    }

    private void initDragDropTabbedPane() {
        tabbedPrincipal.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dtde) {

                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    final List<File> archs = (List) o;

                    hiloCargar = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (File f : archs) {
                                try {
//                                    Log.add(f.toString());

                                    cargarCancionesABiblioteca(f);
                                    cargarCancionesABiblioteca(biblioteca.getSongs());
                                    biblioteca.addSongsToAlbums();
                                    biblioteca.addPath(f);
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            System.out.println("HILO CARGAR DROP TERMINADO!");
                        }
                    });

                    hiloCargar.start();
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                tabActual = tabbedPrincipal.getSelectedIndex();
                tabbedPrincipal.setSelectedIndex(Rule.TabIndex.MUSIC_LIBRARY);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                tabbedPrincipal.setSelectedIndex(tabActual);
            }
        }));
    }

    /*Este método sirve para actualizar la tabla de biblioteca despues del scan (Clase Scan)*/
    @Override
    public void updateMusicLibraryUI(boolean huboCambios) {
        if (huboCambios) {
            cargarCancionesABiblioteca(biblioteca.getSongs());
            biblioteca.addSongsToAlbums();
        }
    }

    private void initIconosTabs() {
        tabbedPrincipal.setIconAt(Rule.TabIndex.EXPLORER, SongListTreeCellRenderer.getImageIcon(Path.EXPLORER_TAB_ICON));
        tabbedPrincipal.setIconAt(Rule.TabIndex.MUSIC_LIBRARY, SongListTreeCellRenderer.getImageIcon(Path.MUSIC_LIBRARY_TAB_ICON));
        tabbedPrincipal.setIconAt(Rule.TabIndex.CURRENT_SONGS_LIST, SongListTreeCellRenderer.getImageIcon(Path.LIST_TAB_ICON));
        tabbedPrincipal.setIconAt(Rule.TabIndex.MOST_PLAYED, SongListTreeCellRenderer.getImageIcon(Path.MOST_PLAYED_TAB_ICON));
        tabbedPrincipal.setIconAt(Rule.TabIndex.LOGGER, SongListTreeCellRenderer.getImageIcon(Path.LOG_TAB_ICON));
        //@TODO: WTF, mejorar
        tabbedPrincipal.setIconAt(Rule.TabIndex.FAVORITES, new ImageIcon(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    private void setColorFondo(Album album) {
        /* Colores de fondo */
        if (album.hasCoversArt()) {
            Rule.BACKGROUND_COLOR = ImageProcessor.getAverageColor(album.getCoverArt());
        } else {
            Rule.BACKGROUND_COLOR = Color.white;
        }
        
        Rule.FOREGROUND_COLOR = ImageProcessor.getForeGroundColorBasedOnBGBrightness(Rule.BACKGROUND_COLOR);

        panelPrincipal.setBackground(Rule.BACKGROUND_COLOR);
        slideVol.setBackground(Rule.BACKGROUND_COLOR);
        lblNombreCancion.setForeground(Rule.FOREGROUND_COLOR);
        lblArtista.setForeground(Rule.FOREGROUND_COLOR);
        opRepetirCancion.setForeground(Rule.FOREGROUND_COLOR);
        opAleatorio.setForeground(Rule.FOREGROUND_COLOR);
        lblInfoCarga.setForeground(Rule.FOREGROUND_COLOR);
        lblDuracion.setForeground(Rule.FOREGROUND_COLOR);

        progress.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Rule.BACKGROUND_COLOR;
            }

            @Override
            protected Color getSelectionForeground() {
                return Rule.FOREGROUND_COLOR;
            }
        });

        progress.setForeground(new Color(76, 175, 80));

        Color color = Rule.BACKGROUND_COLOR.darker().darker();
        tabbedPrincipal.setBackground(color);
        tabbedPrincipal.setForeground(ImageProcessor.getForeGroundColorBasedOnBGBrightness(color));

        // Acá cambio los iconos según color
        if (Rule.FOREGROUND_COLOR == Color.white) {
            lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));
            lblSiguiente.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_NEXT_ICON)));
            lblAnterior.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_BACK_ICON)));
        } else {
            lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
            lblSiguiente.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_NEXT_ICON)));
            lblAnterior.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_BACK_ICON)));
        }
    }

    // @TODO: No se como arreglar estos métodos (setCoverArt) pero me suena a que se puede hacer mejor
    private void setCoverArt(Icon cover) {

        BufferedImage image = new BufferedImage(cover.getIconWidth(), cover.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        cover.paintIcon(null, image.getGraphics(), 0, 0);

        lblCover.setIcon(new ImageIcon(
                image.getScaledInstance(
                (int) Rule.COVER_ART_DIMENSION.getWidth(),
                (int) Rule.COVER_ART_DIMENSION.getHeight(),
                Image.SCALE_SMOOTH)
            )
        );
    }

    private void setCover(Image cover) {
        lblCover.setIcon(new ImageIcon(
                        cover.getScaledInstance(
                            (int) Rule.COVER_ART_DIMENSION.getWidth(),
                            (int) Rule.COVER_ART_DIMENSION.getHeight(),
                            Image.SCALE_SMOOTH)
                )
        );
    }

    private void initListaCoversArtistas() {

        if (coversArtistas == null) {
            coversArtistas = new ArrayList<>();
            biblioteca.getArtistNames().forEach((artista) -> {
                try {
                    coversArtistas.add(new ArtistCoverArt(artista));
                } catch (Exception ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            for (String artista : biblioteca.getArtistNames()) {
                if (!estaArtista(artista)) {
                    try {
                        coversArtistas.add(new ArtistCoverArt(artista));
                    } catch (Exception ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /*Ordena descendente los artistas*/
        Collections.sort(coversArtistas, (ArtistCoverArt c1, ArtistCoverArt c2) -> 
            c1.getArtistName().compareTo(c2.getArtistName())
        );

        try {
            listArtistas.setCellRenderer(new ArtistListCellRenderer(coversArtistas));
            listArtistas.setModel(new ArtistListModel(coversArtistas));
            listArtistas.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            listArtistas.setVisibleRowCount(-1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean estaArtista(String artistName) {
        boolean anyMatch = coversArtistas.stream().anyMatch(
            (artistCoverArt) -> (artistCoverArt.getArtistName().equals(artistName))
        );

        System.out.println("ARTISTA [" + artistName + "] --> " + anyMatch);

        return anyMatch;
    }

}
