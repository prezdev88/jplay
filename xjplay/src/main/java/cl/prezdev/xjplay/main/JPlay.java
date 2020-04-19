package cl.prezdev.xjplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons
import cl.prezdev.jlog.Log;
import cl.prezdev.jlog.LogEntry;
import cl.prezdev.jlog.LogTableModel;
import cl.prezdev.jlog.UpdateLogUI;
import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.MusicLibrary;
import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.MusicPlayer;
import cl.prezdev.xjplay.cover.art.CoverArtThread;
import cl.prezdev.xjplay.artist.list.BackAlbum;
import cl.prezdev.xjplay.list.cellrenderer.AlbumListCellRenderer;
import cl.prezdev.xjplay.list.cellrenderer.ArtistListCellRenderer;
import cl.prezdev.xjplay.artist.list.ArtistCoverArt;
import cl.prezdev.xjplay.list.model.AlbumListModel;
import cl.prezdev.xjplay.list.model.ArtistListModel;
import cl.prezdev.xjplay.model.search.SearchDialog;
import cl.prezdev.xjplay.model.lastFM.LastFM;
import cl.prezdev.xjplay.model.progress.WorkerStringProgress;
import cl.prezdev.xjplay.model.scan.ScanThread;
import cl.prezdev.xjplay.table.model.SongTableModel;
import cl.prezdev.xjplay.table.model.MusicLabrarySongTableModel;
import cl.prezdev.xjplay.tree.cell.renderer.SongListTreeCellRenderer;
import cl.prezdev.xjplay.tree.cell.renderer.SongMostPlayedTreeCellRenderer;
import cl.prezdev.xjplay.tree.cell.renderer.ExplorerTreeCellRenderer;
import cl.prezdev.xjplay.tree.cell.renderer.FavoritesTreeCellRenderer;
import cl.prezdev.xjplay.recursos.Resource;
import cl.prezdev.xjplay.recursos.Path;
import cl.prezdev.xjplay.rules.Rule;
import cl.prezdev.xjplay.save.Save;
import cl.prezdev.xjplay.save.IO;
import cl.prezdev.xjplay.utils.Util;
import cl.prezdev.xjplay.utils.Validate;
import java.awt.Color;
import java.awt.Dimension;
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
import java.awt.event.*;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JFrame;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import cl.prezdev.xjplay.model.search.SearchListener;
import cl.prezdev.xjplay.model.scan.MusicLibraryUiUpdate;


public class JPlay extends JFrame implements
        BasicPlayerListener, SearchListener, MusicLibraryUiUpdate, UpdateLogUI {

    public static MusicPlayer musicPlayer;
    private MusicLibrary musicLibrary;
    
    // lista de reproducción actual
    private List<Song> currentSongs; 
    private Thread loadThread;
    private JPopupMenu explorerTreePopUp;
    private JPopupMenu musicLibraryPopUp;
    private JPopupMenu covertArtPopUp;

    // ESTO ES SOLO PARA DEBUGGING
    private final boolean SAVE = true; 
    private List<Song> searchedSongs;
    private List<ArtistCoverArt> artistCoversArt;
    private boolean isPlay;
    private boolean isStop;
    private boolean isRandom;
    private boolean repeatSong;
    private Image iconApp;
    private CoverArtThread coverArtThread;

    // GUARDA EL TOTAL DE DURACIÓN DE LA CANCION EN MILIS
    private int totalBytes; 

    // para pintar los minutos en la barra
    private WorkerStringProgress workerStringProgress; 
    private boolean printProgressBar;
    private SearchDialog searchDialog;

    // esto es para el drag and drop
    private int currentTabIndex; 
    private LogTableModel logTableModel;
    
    /*
    Esta lista la utilizo cuando guardo en SAVE.
    Ya que si solo guardo las canciones, el icono
    no se carga cuando por ejemplo, estoy escuchando
    las canciones favoritas.
    */
    private List<Album> albums;
    
    // Son los milisegundos totales de la lista de reproducción actual.
    private long millisecondsOfCurrentSongs;

    public JPlay() {
        initComponents();

        Rule.COVER_ART_DIMENSION = new Dimension(
            coverArtLabel.getWidth(), 
            coverArtLabel.getHeight()
        );

        iconApp = Resource.JPLAY_ICON;

        iconApp = iconApp.getScaledInstance(
            (int) Rule.COVER_ART_DIMENSION.getWidth(),
            (int) Rule.COVER_ART_DIMENSION.getHeight(),
            Image.SCALE_SMOOTH
        );

        initLog();

        currentSongs = new ArrayList<>();
        albums = new ArrayList<>();
        musicLibrary = new MusicLibrary();
        musicLibrary.setUpdateLogUI(this);

        isRandom = false;

        initExplorerTree();
        initExplorerTreePopUp();
        initMusicLibraryPopUp();
        initCoverArtPopUp();

        cancelLoadingButton.setEnabled(false);

        if (SAVE) {
            loadSave();
        }

        // @TODO: Crear Rule.APP_TITLE
        this.setTitle(Rule.NAME + " - " + Rule.VERSION);
        isPlay = false;
        isStop = true;
        repeatSong = repeatSongCheckbox.isSelected();

        initTitleMusicLibraryClickListener();
        setNoActionEnter(songsTable);
        setNoActionEnter(musicLibraryTable);

        // esto es para que no se pueda mover las columnas
        musicLibraryTable.getTableHeader().setReorderingAllowed(false);
        
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/
        jScrollPane2.setVisible(false);
        lblLimpiar.setVisible(false);
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/

        setIconImage(iconApp);

        coverArtLabel.setText(null);

        setBounds(0, 0, 800, 600);
        setLocationRelativeTo(null);
        coverArtThread = null;

        coverArtLabel.requestFocus();
        initMostPlayerSongsTree();
        initFavoritesSongsTree();
        musicLibrary.printAlbums();
        printProgressBar = true;

        initSearchDialog();
        initDragDropTabbedPane();
        initTabIcons();
        initArtistCoversArt();
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
    // el cursor bajara y después subiera. Este método sobre escribe eso hecho por java automáticamente
    private void setNoActionEnter(JTable table) {
        // Keystroke es pulsación de tecla
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) { }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogCanciones = new javax.swing.JDialog();
        jDialog1 = new javax.swing.JDialog();
        mainPanel = new javax.swing.JPanel();
        panelPrincipal = new javax.swing.JPanel();
        coverArtLabel = new javax.swing.JLabel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeExplorer = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        musicLibraryTable = new javax.swing.JTable();
        panelListaActual = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        songsTable = new javax.swing.JTable();
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
        cancelLoadingButton = new javax.swing.JButton();
        lblNombreCancion = new javax.swing.JLabel();
        lblArtista = new javax.swing.JLabel();
        volumeSlider = new javax.swing.JSlider();
        btnFav = new javax.swing.JToggleButton();
        repeatSongCheckbox = new javax.swing.JCheckBox();
        randomCheckbox = new javax.swing.JCheckBox();
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

        coverArtLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        coverArtLabel.setText("[cv]");

        mainTabbedPane.setToolTipText("");
        mainTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tabbedPrincipalMouseReleased(evt);
            }
        });

        treeExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                explorerTreeMouseReleased(evt);
            }
        });
        treeExplorer.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                explorerTreeValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(treeExplorer);

        mainTabbedPane.addTab("Explorer", jScrollPane3);

        musicLibraryTable.setModel(new javax.swing.table.DefaultTableModel(
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
        musicLibraryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                musicLibraryTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                musicLibraryTableMouseReleased(evt);
            }
        });
        musicLibraryTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                musicLibraryTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(musicLibraryTable);

        mainTabbedPane.addTab("Biblioteca", jScrollPane1);

        songsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        songsTable.setShowHorizontalLines(false);
        songsTable.setShowVerticalLines(false);
        songsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaCancionesMouseReleased(evt);
            }
        });
        songsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tablaCancionesKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(songsTable);

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

        mainTabbedPane.addTab("Lista actual", panelListaActual);

        panelMasEscuchadas.setLayout(new java.awt.BorderLayout());

        treeMasTocadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeMasTocadasMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(treeMasTocadas);

        panelMasEscuchadas.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("+ escuchadas", panelMasEscuchadas);

        panelFavoritos.setLayout(new java.awt.BorderLayout());

        treeFavoritos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeFavoritosMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(treeFavoritos);

        panelFavoritos.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Favoritos", panelFavoritos);

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

        mainTabbedPane.addTab("Logger (DEV)", panelLogger);

        jPanel1.setLayout(new java.awt.BorderLayout());

        listArtistas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listArtistasMouseReleased(evt);
            }
        });
        jScrollPane8.setViewportView(listArtistas);

        jPanel1.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Artistas", jPanel1);

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

        cancelLoadingButton.setText("X");
        cancelLoadingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelLoadingButtonActionPerformed(evt);
            }
        });

        lblNombreCancion.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblNombreCancion.setText("Artista / Canción");

        lblArtista.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        lblArtista.setText("Artista / Canción");

        volumeSlider.setBackground(new java.awt.Color(255, 255, 255));
        volumeSlider.setMaximum(40);
        volumeSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                volumeSlideMouseDragged(evt);
            }
        });
        volumeSlider.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                slideVolMouseWheelMoved(evt);
            }
        });
        volumeSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                volumeSlideMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                slideVolMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                volumeSlideMouseClicked(evt);
            }
        });

        btnFav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFavActionPerformed(evt);
            }
        });

        repeatSongCheckbox.setBackground(new java.awt.Color(254, 254, 254));
        repeatSongCheckbox.setText("Repetir Canción");
        repeatSongCheckbox.setOpaque(false);
        repeatSongCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repeatSongCheckboxActionPerformed(evt);
            }
        });

        randomCheckbox.setBackground(new java.awt.Color(254, 254, 254));
        randomCheckbox.setText("Shuffle");
        randomCheckbox.setOpaque(false);
        randomCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomCheckboxActionPerformed(evt);
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
                    .addComponent(mainTabbedPane)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(lblAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coverArtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(cancelLoadingButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfoCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(randomCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(repeatSongCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(coverArtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(volumeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelLoadingButton)
                            .addComponent(lblInfoCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(randomCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(repeatSongCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
    private void volumeSlideMouseDragged(MouseEvent mouseEvent) {
        setVolumen(volumeSlider.getValue());
    }

    // @TODO: Eliminar
    private void volumeSlideMouseClicked(MouseEvent mouseEvent) {
//        System.out.println("Clicked");
    }

    // @TODO: Eliminar
    private void volumeSlideMousePressed(MouseEvent mouseEvent) {
//        System.out.println("Pressed");
    }

    // @TODO: Eliminar
    private void slideVolMouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
//        System.out.println("Wheel");
    }

    // @TODO: Desacoplar los listeners de explorerTree
    private void explorerTreeMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            procesarCancionArbol();
        }
    }

    private void explorerTreeValueChanged(TreeSelectionEvent treeSelectionEvent) {
        // acá cargo los subdirectorios cuando hago click
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExplorer.getLastSelectedPathComponent();

        if (node != null) {
            if (node.getChildCount() == 0) {
                /*Si no tengo hijos proceso*/
                Object userObject = node.getUserObject();

                if (userObject instanceof File) {
                    File file = (File) userObject;
                    if (file.isDirectory()) {
                        cargarArchivosEnNodoArbol(node, file);
                    }
                }
            }
        }
    }

    private void cancelLoadingButtonActionPerformed(ActionEvent actionEvent) {
        // @TODO: intentar llamar a interrupt
        loadThread.stop();
        cancelLoadingButton.setEnabled(false);
        loadAlbumsInTreeSong(getAlbums(currentSongs));
    }

    private void formWindowClosing(WindowEvent windowEvent) {
        if (SAVE) {
            try {
                Save save = new Save();

                save.songs = currentSongs;
                save.indexTab = mainTabbedPane.getSelectedIndex();
                save.logEntries = Log.getEntries();
                save.cover = coverArtLabel.getIcon();
                save.albums = albums;
                save.volume = volumeSlider.getValue();
                save.artistCoversArt = artistCoversArt;

                IO.escribirObjetoEn(save, Path.SAVE);
                IO.escribirObjetoEn(musicLibrary, Path.MUSIC_LIBRARY);
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
    * @TODO: Se me ocurrió una idea:
    *   1.- Crear proyecto para poner datos (model) en vista (o algo asi)
    *   2.- Crear proyecto que se encargue de los listeners de la vista (o algo asi)
    * */
    private void musicLibraryTableMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            playSelectedMusicLibrarySong();
        }
    }

    private void musicLibraryTableMousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            musicLibraryPopUp.show(musicLibraryTable, mouseEvent.getX(), mouseEvent.getY());
        }
    }

    private void repeatSongCheckboxActionPerformed(ActionEvent actionEvent) {
        repeatSong = repeatSongCheckbox.isSelected();
    }

    // @TODO: Eliminar
    private void slideVolMouseReleased(MouseEvent mouseEvent) {}

    private void randomCheckboxActionPerformed(ActionEvent actionEvent) {
        isRandom = randomCheckbox.isSelected();
    }

    private void musicLibraryTableKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            playSelectedMusicLibrarySong();
        }
    }

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
        setVolumen(volumeSlider.getValue());
        printProgressBar = true;
    }//GEN-LAST:event_progressMouseReleased

    private void progressMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseDragged
        cambiarProgress((evt.getX() * 100) / progress.getWidth(), false);
        printProgressBar = false;
    }//GEN-LAST:event_progressMouseDragged

    private void lblLimpiarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarMouseReleased
        lblLimpiar.setBackground(new java.awt.Color(63, 81, 181));
        currentSongs = new ArrayList<>();
        loadAlbumsInTreeSong(getAlbums(currentSongs));
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

            SongTableModel model = (SongTableModel) songsTable.getModel();
            int index = songsTable.getSelectedRow();
            Song c = (Song) model.getValueAt(index, 0);

            reproducir(c);
            //            tablaCanciones.getSelectionModel().clearSelection();
//            tablaCanciones.getSelectionModel().setSelectionInterval(indiceActual, indiceActual);
        }
    }//GEN-LAST:event_tablaCancionesKeyReleased

    private void tablaCancionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCancionesMouseReleased
        if (evt.getClickCount() == 2) {
            SongTableModel model = (SongTableModel) songsTable.getModel();
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
                    Song c = (Song) o;

                    currentSongs = musicLibrary.getMostListenedSongs();

                    /*Ahora debo poner las canciones en un album*/
                    Album a = new Album(Rule.NAME, "Canciones más escuchadas!", null);

                    for (Song can : currentSongs) {
                        a.addSong(can);
                    }

                    List<ImageIcon> covers = new ArrayList<>();
                    covers.add(SongListTreeCellRenderer.getImageIcon(Path.HEART_ICON));
                    a.setCovers(covers);

                    List<Album> albums = new ArrayList<>();
                    albums.add(a);
                    /*Ahora debo poner las canciones en un album*/

                    loadAlbumsInTreeSong(albums);

                    reproducir(c);

                    mainTabbedPane.setSelectedIndex(Rule.Tabs.CURRENT_LIST);
                }
            }
        }
    }//GEN-LAST:event_treeMasTocadasMouseReleased

    private void tabbedPrincipalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabbedPrincipalMouseReleased
        if (evt.getClickCount() == 2) {
            if (mainTabbedPane.getSelectedIndex() == 1) {
                cargarCancionesABiblioteca(musicLibrary.getSongs());
            }
        }

        switch (mainTabbedPane.getSelectedIndex()) {
            case Rule.Tabs.MUSIC_LIBRARY:
                lblInfoCarga.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.Tabs.EXPLORER:
                lblInfoCarga.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.Tabs.FAVORITES:
                lblInfoCarga.setText(musicLibrary.getFavoritesDuration());
                break;
            case Rule.Tabs.CURRENT_LIST:
                lblInfoCarga.setText("Lista actual --> " + musicLibrary.getFormattedDuration(millisecondsOfCurrentSongs));
                break;
            case Rule.Tabs.LOGGER:
                lblInfoCarga.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.Tabs.MOST_PLAYED:
                lblInfoCarga.setText(musicLibrary.getMostListenedDuration());
                break;
        }
    }//GEN-LAST:event_tabbedPrincipalMouseReleased

    private void btnFavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFavActionPerformed
        if (musicPlayer.getCurrentSong() != null) {
            if (btnFav.isSelected()) {
                musicLibrary.addFavoriteSong(musicPlayer.getCurrentSong());
            } else {
                musicLibrary.removeFavoriteSong(musicPlayer.getCurrentSong());
            }
            initFavoritesSongsTree();
        }
    }//GEN-LAST:event_btnFavActionPerformed

    private void treeFavoritosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeFavoritosMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeFavoritos.getLastSelectedPathComponent();
            if (node != null) {
                Object o = node.getUserObject();
                if (o instanceof Song) {
                    Song c = (Song) o;

                    currentSongs = musicLibrary.getFavoritesSongs();

                    /*Ahora debo poner las canciones en un album*/
                    Album a = new Album(Rule.NAME, "Favoritas!", null);

                    for (Song can : currentSongs) {
                        a.addSong(can);
                    }

                    List<ImageIcon> covers = new ArrayList<>();
                    covers.add(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON));
                    a.setCovers(covers);

                    List<Album> albums = new ArrayList<>();
                    albums.add(a);
                    /*Ahora debo poner las canciones en un album*/

                    loadAlbumsInTreeSong(albums);

                    reproducir(c);

                    mainTabbedPane.setSelectedIndex(Rule.Tabs.CURRENT_LIST);
                }
            }
        }
    }//GEN-LAST:event_treeFavoritosMouseReleased

    private void lblPlayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPlayMouseReleased
        try {
            if (musicPlayer != null) {
                if (isPlay) {
                    isPlay = false;
                    if (Util.FOREGROUND_COLOR == Color.black) {
                        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
                    } else {
                        lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));
                    }
                    musicPlayer.pause();

                } else {
                    isPlay = true;
                    if (Util.FOREGROUND_COLOR == Color.black) {
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
        if (!currentSongs.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repeatSong) {
                reproducirCancionActual();
            } else {
                reproducirAnterior();
            }
        }
    }//GEN-LAST:event_lblAnteriorMouseReleased

    private void lblSiguienteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSiguienteMouseReleased
        if (!currentSongs.isEmpty()) {
            if (isRandom) {
                reproducirRandom();
            } else if (repeatSong) {
                reproducirCancionActual();
            } else {
                reproducirSiguiente();
            }
        }
    }//GEN-LAST:event_lblSiguienteMouseReleased

    private void listArtistasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listArtistasMouseReleased
        if (evt.getClickCount() == 2) {
            Object ob = listArtistas.getSelectedValue();

            if (ob instanceof ArtistCoverArt) {
                ArtistCoverArt ca = (ArtistCoverArt) ob;
                List<Album> albumsByArtista = musicLibrary.getAlbumsByArtist(ca.getArtistName());
                albumsByArtista.add(0, new BackAlbum());

                listArtistas.setCellRenderer(new AlbumListCellRenderer(albumsByArtista));
                listArtistas.setModel(new AlbumListModel(albumsByArtista));
            } else if (ob instanceof BackAlbum) {
                // quiere ir atrás, o sea a los artistas
                listArtistas.setCellRenderer(new ArtistListCellRenderer(artistCoversArt));
                listArtistas.setModel(new ArtistListModel(artistCoversArt));
            }
        }
    }//GEN-LAST:event_listArtistasMouseReleased

    private void loadSave() {
        if (new File(Path.SAVE).exists()) {
            try {
                Save g = (Save) IO.leerObjetoDesde(Path.SAVE);

                currentSongs = g.songs;
                artistCoversArt = g.artistCoversArt;

                /*Recuperando el volumen del usuario*/
                volumeSlider.setValue(g.volume);
                setVolumen(volumeSlider.getValue());

                /*Recuperando el volumen del usuario*/
                mainTabbedPane.setSelectedIndex(g.indexTab);

                Log.setLogEntries(g.logEntries);
                mainTabbedPane.setTitleAt(Rule.Tabs.LOGGER, "Logger (" + tableLogger.getRowCount() + ")");

                setCover(g.cover);

                musicLibrary = (MusicLibrary) IO.leerObjetoDesde(Path.MUSIC_LIBRARY);

//                Scan scan = new Scan(biblioteca, this);
//                scan.scanner();
                //<editor-fold defaultstate="collapsed" desc="Código de scaneo de biblioteca antiguo">
//                boolean e = true;
//
//                for (Cancion c : biblioteca.getCanciones()) {
//                    if (!c.exists()) {
//                        e = false;
//                        break;
//                    }
//                }
//
//                if (!e) {
//                    int cont = 0;
//                    if (JOptionPane.showConfirmDialog(this, "Se ha encontrado por lo menos 1 canción que no existe en la biblioteca. ¿Desea analizar la biblioteca completa?") == JOptionPane.YES_OPTION) {
//
//                        List<Cancion> cancBiblio = biblioteca.getCanciones();
//                        cargarCancionesABiblioteca(cancBiblio);
//                        for (Cancion c : cancBiblio) {
//                            if (!c.exists()) {
//                                cont++;
//                            }
//                        }
//
//                        if (JOptionPane.showConfirmDialog(this, "Se han encontrado " + cont + " canciones que no existen. ¿Desea eliminarlas?") == JOptionPane.YES_OPTION) {
//                            int cant = biblioteca.removerNoExistentes();
//                            JOptionPane.showMessageDialog(this, "Se han eliminado " + cant + " canciones de la biblioteca!", "Info", JOptionPane.INFORMATION_MESSAGE);
//
//                            Iterator<Cancion> iterator = canciones.iterator();
//
//                            Cancion c;
//                            cant = 0;
//                            while (iterator.hasNext()) {
//                                c = iterator.next();
//
//                                if (!biblioteca.estaCancion(c)) {
//                                    iterator.remove();
//                                    cant++;
//                                }
//                            }
//
//                            if (cant != 0) {
//                                JOptionPane.showMessageDialog(this, "Se han eliminado " + cant + " canciones de la lista principal!", "Info", JOptionPane.INFORMATION_MESSAGE);
//                            }
//
//                        }
//                    }
//                }
//              </editor-fold>
                Log.add("Cantidad de canciones cargadas del save: " + currentSongs.size());
//                indiceActual = g.indiceActual;
//                System.out.println("Índice actual: " + indiceActual);

                loadAlbumsInTreeSong(g.albums);
                cargarCancionesABiblioteca(musicLibrary.getSongs());
                imprimirTemaActual();

            } catch (InvalidClassException ex) {
                Log.add("EX: " + ex.getMessage());
                musicLibrary = new MusicLibrary();
                currentSongs = musicLibrary.getSongs();
                artistCoversArt = new ArrayList<>();
                cargarDefault();
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cargarDefault();
        }

        switch (mainTabbedPane.getSelectedIndex()) {
            case Rule.Tabs.MUSIC_LIBRARY:
                lblInfoCarga.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.Tabs.EXPLORER:
                lblInfoCarga.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.Tabs.FAVORITES:
                lblInfoCarga.setText(musicLibrary.getFavoritesDuration());
                break;
            case Rule.Tabs.CURRENT_LIST:
                lblInfoCarga.setText("Lista actual --> " + musicLibrary.getFormattedDuration(millisecondsOfCurrentSongs));
                break;
            case Rule.Tabs.LOGGER:
                lblInfoCarga.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.Tabs.MOST_PLAYED:
                lblInfoCarga.setText(musicLibrary.getMostListenedDuration());
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
                    musicLibrary.addSong(c);
                    lblInfoCarga.setText("Agregando " + c);
                }
            }
        } else {
            Song c = new Song(raiz.getPath());
            musicLibrary.addSong(c);
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
                    currentSongs.add(c);
                }
            }
        }
    }

    public static void main(String args[]) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JPlay().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnFav;
    private javax.swing.JButton cancelLoadingButton;
    private javax.swing.JLabel coverArtLabel;
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
    private javax.swing.JLabel lblDuracion;
    private javax.swing.JLabel lblInfoCarga;
    private javax.swing.JLabel lblLimpiar;
    private javax.swing.JLabel lblNombreCancion;
    private javax.swing.JLabel lblPlay;
    private javax.swing.JLabel lblSiguiente;
    private javax.swing.JList listArtistas;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox randomCheckbox;
    private javax.swing.JPanel panelFavoritos;
    private javax.swing.JPanel panelListaActual;
    private javax.swing.JPanel panelLogger;
    private javax.swing.JPanel panelMasEscuchadas;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JProgressBar progress;
    private javax.swing.JCheckBox repeatSongCheckbox;
    private javax.swing.JSlider volumeSlider;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JTable musicLibraryTable;
    private javax.swing.JTable songsTable;
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
        if (printProgressBar) {
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
        Log.add("STATE UPDATED: " + bpe.toString());
        switch (bpe.getCode()) {
            case BasicPlayerEvent.EOM:
                /*
                End of music: Se ejecuta esto cuando finaliza de tocar un tema
                 */
                if (!repeatSong) {
                    if (isRandom) {
                        reproducirRandom();
                    } else {
                        reproducirSiguiente();
                    }
                } else {
                    reproducirCancionActual();
                }

                Log.add("END_OF_MUSIC");
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
    public void setController(BasicController bc) {
        Log.add("SET CONTROLLER: " + bc);
    }

    private void setVolumen(int vol) {
        try {
            if (musicPlayer != null) {
                musicPlayer.setVolume(vol);
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initExplorerTree() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");
        File[] discos = File.listRoots();

        for (File disco : discos) {
            raiz.add(new DefaultMutableTreeNode(disco));
        }

        treeExplorer.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeExplorer.setRootVisible(false);

        treeExplorer.setCellRenderer(new ExplorerTreeCellRenderer(
                new ImageIcon(
                        ExplorerTreeCellRenderer.crearIcono(Path.MUSIC_ICON).getImage().
                                getScaledInstance(Rule.ICON_EXPLORER_MUSIC_SIZE,
                                        Rule.ICON_EXPLORER_MUSIC_SIZE,
                                        Image.SCALE_SMOOTH
                                )
                ),
                new ImageIcon(
                        ExplorerTreeCellRenderer.crearIcono(Path.FOLDER_ICON).getImage().
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
     * @param albums
     */
    // @TODO: Pensar en separar la lógica de cargas de models en vistas
    // quizás en otro proyecto
    private void loadAlbumsInTreeSong(List<Album> albums) {
        this.albums = albums;
        //ordenar acá
        Log.add("Se cargaron " + currentSongs.size() + " canciones a la lista principal");

        Collections.sort(currentSongs, (Comparator<File>) (file1, file2) -> file1.compareTo(file2));

        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("raiz");

        if (albums != null) {
            DefaultMutableTreeNode albumTreeNode;
            for (Album album : albums) {
                albumTreeNode = new DefaultMutableTreeNode(album);

                for (Song song : album.getSongs()) {
                    albumTreeNode.add(new DefaultMutableTreeNode(song));
                }

                rootTreeNode.add(albumTreeNode);
            }
        }

        treeSong.setModel(new DefaultTreeModel(rootTreeNode));
        treeSong.setRootVisible(false);
        treeSong.expandRow(0);
        treeSong.setCellRenderer(new SongListTreeCellRenderer());

        cancelLoadingButton.setEnabled(false);
        millisecondsOfCurrentSongs = 0;

        // @TODO: desacoplar esto
        for (Song song : currentSongs) {
            millisecondsOfCurrentSongs += song.getMilisDuration();
        }

        lblInfoCarga.setText("Lista actual --> " + musicLibrary.getFormattedDuration(millisecondsOfCurrentSongs));
    }

    private void initMostPlayerSongsTree() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Song> masRepro = musicLibrary.getMostListenedSongs();

        for (Song c : masRepro) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeMasTocadas.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeMasTocadas.setRootVisible(false);
//        treeSong.expandRow(0);

        treeMasTocadas.setCellRenderer(new SongMostPlayedTreeCellRenderer(
                        ExplorerTreeCellRenderer.crearIcono(Path.PLAY_TREE_ICON),
                        ExplorerTreeCellRenderer.crearIcono(Path.ALBUM_TREE_ICON)
                )
        );

        mainTabbedPane.setTitleAt(Rule.Tabs.MOST_PLAYED, "+ escuchadas (" + masRepro.size() + ")");
    }

    private void initFavoritesSongsTree() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("raiz");

        List<Song> favoritos = musicLibrary.getFavoritesSongs();

        for (Song c : favoritos) {
            DefaultMutableTreeNode disco = new DefaultMutableTreeNode(c);

            raiz.add(disco);
        }

        treeFavoritos.setModel(new javax.swing.tree.DefaultTreeModel(raiz));
        treeFavoritos.setRootVisible(false);
//        treeSong.expandRow(0);

        treeFavoritos.setCellRenderer(new FavoritesTreeCellRenderer()
        );

        mainTabbedPane.setTitleAt(Rule.Tabs.FAVORITES, "Favoritos (" + favoritos.size() + ")");
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
                        explorerTreePopUp.show(treeExplorer, e.getX() + 10, e.getY() + 10);
//                       popup.show(tree, e.getX(), e.getY());
                    }
                }
            }
        };
        treeExplorer.addMouseListener(ml);
        
        explorerTreePopUp = new JPopupMenu();
        JMenuItem itemAlistaNueva = new JMenuItem("A lista nueva");
        JMenuItem itemAlistaExistente = new JMenuItem("Añadir a existente");
        JMenuItem itemABiblioteca = new JMenuItem("Añadir a biblioteca");

        JPopupMenu.Separator sep = new JPopupMenu.Separator();

        explorerTreePopUp.add(itemAlistaNueva);
        explorerTreePopUp.add(itemAlistaExistente);
        explorerTreePopUp.add(sep);
        explorerTreePopUp.add(itemABiblioteca);

        itemAlistaNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final File f = getSelectedTreeFile();
                currentSongs = new ArrayList<>();
                loadThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        cancelLoadingButton.setEnabled(true);
                        try {
                            cargarCancionesALista(f);
                            loadAlbumsInTreeSong(getAlbums(currentSongs));
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            // lblInfoCarga.setText("Cancelado");
//                            lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
                        }
                    }
                });
                loadThread.start();
            }
        });

        itemAlistaExistente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loadThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        cancelLoadingButton.setEnabled(true);
                        try {
                            File f = getSelectedTreeFile();
                            cargarCancionesALista(f);
                            loadAlbumsInTreeSong(getAlbums(currentSongs));
                        } catch (IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });
                loadThread.start();

            }
        });

        itemABiblioteca.addActionListener((ActionEvent event) -> {
            loadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);
                try {
                    File f = getSelectedTreeFile();

                    cargarCancionesABiblioteca(f);
                    cargarCancionesABiblioteca(musicLibrary.getSongs());
                    musicLibrary.processAlbum();
                    musicLibrary.addPath(f);
                    initArtistCoversArt();
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("HILO CARGAR BIBLIOTECA TERMINADO!");
            });

            loadThread.start();
        });
    }

    private void initMusicLibraryPopUp() {
        musicLibraryPopUp = new JPopupMenu();
        JMenuItem itemRemoverDeBiblioteca = new JMenuItem("Remover");
        JMenuItem itemTocarDisco = new JMenuItem("Reproducir Disco");
        JMenuItem itemAnadirDisco = new JMenuItem("Añadir Disco");

        musicLibraryPopUp.add(itemTocarDisco);
        musicLibraryPopUp.add(itemAnadirDisco);
        musicLibraryPopUp.add(new Separator());
        musicLibraryPopUp.add(itemRemoverDeBiblioteca);

        itemTocarDisco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selRows = musicLibraryTable.getSelectedRows();

                List<Album> albums = new ArrayList<>();
                Song c;
                Album a;

                currentSongs = new ArrayList<>();

                for (int fila : selRows) {
                    c = (Song) musicLibraryTable.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);
                    a = musicLibrary.getAlbum(c);

                    if (!albums.contains(a)) {
                        albums.add(a);

                        for (Song can : a.getSongs()) {
                            currentSongs.add(can);
                        }
                    }
                }

                loadAlbumsInTreeSong(getAlbums(currentSongs));
                reproducir(currentSongs.get(0));
                mainTabbedPane.setSelectedIndex(Rule.Tabs.CURRENT_LIST);
            }
        });

        itemAnadirDisco.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selRows = musicLibraryTable.getSelectedRows();

                List<Album> albums = new ArrayList<>();
                Song c;
                Album a;

                for (int fila : selRows) {
                    c = (Song) musicLibraryTable.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);
                    a = musicLibrary.getAlbum(c);

                    if (!albums.contains(a)) {
                        albums.add(a);

                        for (Song can : a.getSongs()) {
                            currentSongs.add(can);
                        }
                    }
                }

                loadAlbumsInTreeSong(getAlbums(currentSongs));
            }
        });

        itemRemoverDeBiblioteca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int[] selRows = musicLibraryTable.getSelectedRows();

                List<Song> canciones = new ArrayList<>();

                for (int fila : selRows) {
                    canciones.add((Song) musicLibraryTable.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX));
                }

                for (Song c : canciones) {
                    musicLibrary.removeSong(c);
                }

                cargarCancionesABiblioteca(musicLibrary.getSongs());
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
//        listaCanciones.setModel(new LMCancion(canciones));
        // sin titulos las tabla
//        tablaBiblioteca.getTableHeader().setUI(null);
//        tablaBiblioteca.setRowHeight(20);
        musicLibraryTable.setModel(new MusicLabrarySongTableModel(canciones));
        mainTabbedPane.setTitleAt(Rule.Tabs.MUSIC_LIBRARY, "Biblioteca (" + canciones.size() + ")");
//        Log.add("Se cargaron " + lista.size() + " canciones en biblioteca");
//        lblInfoCarga.setText("Se cargaron " + lista.size() + " canciones en biblioteca");

        Log.add(musicLibrary.getLibraryDuration());
//        lblInfoCarga.setText(biblioteca.getDuracionBiblioteca());
//        tablaBiblioteca.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(0).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(1).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(2).getMinWidth());
//        System.out.println(tablaBiblioteca.getColumnModel().getColumn(3).getMinWidth());
        musicLibraryTable.getColumnModel().getColumn(0).setPreferredWidth(Rule.TRACK_NUMBER_COLUMN_SIZE);
        musicLibraryTable.getColumnModel().getColumn(1).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        musicLibraryTable.getColumnModel().getColumn(2).setPreferredWidth(Rule.ALBUM_COLUMN_SIZE);
        musicLibraryTable.getColumnModel().getColumn(3).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        /**/
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        tablaBiblioteca.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        /**/

        cancelLoadingButton.setEnabled(false);
        musicLibrary.processAlbum();
    }

    private void reproducir(Song cancion) {

        btnFav.setSelected(musicLibrary.isFavoriteSong(cancion));

        try {
            new Thread(() -> {
                procesarCover(cancion);
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
                Rule.NAME + " - " + 
                Rule.VERSION + " [" + 
                cancion.getAuthor() + " - " + 
                cancion.getName() + " (" + cancion.getPlayCount() + ")]"
            );
            
            setVolumen(volumeSlider.getValue());
//            lblTemaActual.setText(c.getAutor()+" / "+c.getNombre() + " ("+c.getDuracionAsString()+")");

//            btnPause.setText("Pause");
            if (Util.FOREGROUND_COLOR == Color.white) {
                lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PAUSE_ICON)));
            } else {
                lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PAUSE_ICON)));
            }

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
            initMostPlayerSongsTree();

            if (this.workerStringProgress != null) {
                this.workerStringProgress.cancel(true);
            }

            this.workerStringProgress = new WorkerStringProgress(lblDuracion, cancion.getDurationAsString());

            this.workerStringProgress.execute();

            imprimirTemaActual();

        } catch (BasicPlayerException ex) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void reproducirCancionActual() {
        reproducir(currentSongs.get(currentSongs.indexOf(musicPlayer.getCurrentSong())));
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
        if (musicPlayer != null) {
            Song cancionActual = musicPlayer.getCurrentSong();
            lblArtista.setText(cancionActual.getAuthor() + " - " + cancionActual.getAlbum() + " (" + cancionActual.getYear() + ")");
            lblNombreCancion.setText(cancionActual.getName() + " (" + cancionActual.getDurationAsString() + ")");
        }
    }

    private void cargarDefault() {
        cargarCancionesABiblioteca(musicLibrary.getSongs());
        loadAlbumsInTreeSong(null);
//        lblCover.setIcon(new ImageIcon(icono));
        setCover(iconApp);
    }

    private void initTitleMusicLibraryClickListener() {
        musicLibraryTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int column = musicLibraryTable.columnAtPoint(mouseEvent.getPoint());
                String columnName = musicLibraryTable.getColumnName(column);
                Log.add("Column index selected " + column + " " + columnName);
            }
        });
    }

    public int getRandom() {
        return new Random().nextInt(currentSongs.size());
    }

    private void playSelectedMusicLibrarySong() {
        int fila = musicLibraryTable.getSelectedRow();
        Song c = (Song) musicLibraryTable.getValueAt(fila, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);

//        System.out.println(biblioteca.getAlbum(c));
        MusicLabrarySongTableModel model = (MusicLabrarySongTableModel) musicLibraryTable.getModel();

        currentSongs = model.songs;
//        indiceActual = fila;

        loadAlbumsInTreeSong(getAlbums(currentSongs));

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
                    currentSongs = new ArrayList<>();
//                        System.out.println(f.getParentFile());
                    loadThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            cancelLoadingButton.setEnabled(true);
                            try {
                                cargarCancionesALista(f.getParentFile());
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            loadAlbumsInTreeSong(getAlbums(currentSongs));
                        }
                    });

                    loadThread.start();
                    reproducir(new Song(f.getPath()));
                }
            } catch (IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // @TODO: Desacoplar
    private List<Album> getAlbums(List<Song> songs) {
        List<Album> albums = new ArrayList<>();
        Album album;

        for (Song song : songs) {
            album = musicLibrary.getAlbum(song);

            if (album != null) {
                if (!albums.contains(album)) {
                    albums.add(album);
                }
            }
        }

        return albums;
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
                this.workerStringProgress.changeProgressBar(porc);
                musicPlayer.seek((long) value);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void procesarCover(Song cancion) {
//        lblCover.setIcon(new ImageIcon(icono));
        setCover(iconApp);
        Album album = musicLibrary.getAlbum(cancion);

//        panelPrincipal.setBackground(Color.white);
        if (!cancion.exists()) { // si canción no existe
            if (JOptionPane.showConfirmDialog(
                    this,
                    cancion.exists() + "[" + cancion.getName() + "] no encontrada. "
                    + "¿Desea analizar la lista completa para eliminar los no encontrados?", "Error",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Iterator<Song> iterator = currentSongs.iterator();

                Song c;
                int cont = 0;
                while (iterator.hasNext()) {
                    c = iterator.next();

                    if (!c.exists()) {
                        currentSongs.remove(c);
                        cont++;
                    }
                }

                JOptionPane.showMessageDialog(this, "Se han eliminado " + cont + " canciones de la lista.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (!album.hasCovers()) { // si el Album NO tiene una lista de imagenes
            Log.add("La canción no tiene imágenes asociadas!");
            List<ImageIcon> fotos = Resource.getCoversArt(cancion);
            Log.add("Se han encontrado " + fotos.size() + " foto");

            if (!fotos.isEmpty()) {
                /*
                si la lista de fotos no esta vacía por lo menos hay una
                para poder comenzar el hilo de las caratulas
                 */
                album.setCovers(fotos);
                Log.add("Se añadió una lista de fotos a la cancion [" + fotos.size() + " fotos]");
            } else { // no hay imagenes en la carpeta de la canción
                List<ImageIcon> covers = new ArrayList<>();
                try {
                    Image imLastFM = LastFM.getCoverArt(cancion.getAuthor(), cancion.getAlbum());
                    imLastFM = imLastFM.getScaledInstance((int) Rule.COVER_ART_DIMENSION.getWidth(),
                            (int) Rule.COVER_ART_DIMENSION.getHeight(),
                            Image.SCALE_SMOOTH);

                    covers.add(new ImageIcon(imLastFM));

                    Log.add("Se añadió una image desde LastFM!");
                } catch (Exception ex) {
                    /*Establezco la caratula por defecto (el disco)*/
//                        icono = icono.getScaledInstance(
//                                (int) Rule.COVER_DIMENSION.getWidth(),
//                                (int) Rule.COVER_DIMENSION.getHeight(),
//                                Image.SCALE_SMOOTH);
                    covers.add(new ImageIcon(iconApp));
                    Log.add("Se añadió una caratula POR DEFECTO --> " + ex.getMessage());
                }
                album.setCovers(covers);
            }
        } else {
            Log.add("La canción tiene caratula!");
        }

        if (coverArtThread != null) {
            coverArtThread.interrupt();
        }

//        lblCover.setIcon(new ImageIcon(album.getCovers().get(0).getImage().getScaledInstance((int) Rule.COVER_DIMENSION.getWidth(),
//                (int) Rule.COVER_DIMENSION.getHeight(),
//                Image.SCALE_SMOOTH)));
        setCover(album.getCovers().get(0).getImage());

        setColorFondo(album);

        coverArtThread = new CoverArtThread(coverArtLabel, album.getCovers());
        coverArtThread.start();

        setIconImage(album.getCovers().get(0).getImage());

        treeSong.setCellRenderer(new SongListTreeCellRenderer()
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

    
//    private void initFonts() {
//        try {
//            Font fuente = Font.createFont(Font.TRUETYPE_FONT, Recurso.FUENTE_ROBOTO);
//
//            lblNombreCancion.setFont(fuente.deriveFont(Font.BOLD, Rule.FONT_SIZE_NORMAL));
//            lblArtista.setFont(fuente.deriveFont(Font.PLAIN, 11));
//            opAleatorio.setFont(fuente.deriveFont(Font.PLAIN, 13));
//            opRepetirCancion.setFont(fuente.deriveFont(Font.PLAIN, 13));
////            txtBuscar.setFont(fuente.deriveFont(Font.PLAIN, 13));
//            tabbedPrincipal.setFont(fuente.deriveFont(Font.PLAIN, 13));
//            tablaBiblioteca.setFont(fuente.deriveFont(Font.PLAIN, 14));
//            lblInfoCarga.setFont(fuente.deriveFont(Font.BOLD, 13));
//            btnCancelarCarga.setFont(fuente.deriveFont(Font.PLAIN, 13));
//
//            /*
//            TAMBIEN CAMBIAR FUENTES EN LOS CELL RENDERERS (xjplay.model.tree)
//             */
//        } catch (FontFormatException | IOException ex) {
//            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    /**
     * Reproduce el siguiente, si es el último reproduce el primero
     */
    private void reproducirSiguiente() {
        int indexActual = currentSongs.indexOf(musicPlayer.getCurrentSong());
        indexActual++;
        if (indexActual < currentSongs.size()) {
            reproducir(currentSongs.get(indexActual));
        } else {
            reproducir(currentSongs.get(0));
        }
    }

    /**
     * Reproduce el anterior, si es el primero reproduce el último
     */
    private void reproducirAnterior() {
        int indexActual = currentSongs.indexOf(musicPlayer.getCurrentSong());
        indexActual--;
        if (indexActual >= 0) {
            reproducir(currentSongs.get(indexActual));
        } else {
            // reproduce el último
            reproducir(currentSongs.get(currentSongs.size() - 1));
        }
    }

    private void reproducirRandom() {
        reproducir(currentSongs.get(getRandom()));
    }

    @Override
    public void search(String filtro) {
        searchedSongs = new ArrayList<>();

        int i = 1;
        for (Song c : musicLibrary.getSongs()) {
            if (c.getAuthor().toLowerCase().contains(filtro)
                    || c.getAlbum().toLowerCase().contains(filtro)
                    || c.getName().toLowerCase().contains(filtro)) {
                searchedSongs.add(c);
            }
        }

        cargarCancionesABiblioteca(searchedSongs);
    }

    // @TODO: Arreglar este método
    private void initSearchDialog() {
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
                mainTabbedPane.setSelectedIndex(Rule.Tabs.MUSIC_LIBRARY);
                if (searchDialog == null) {
                    searchDialog = new SearchDialog(JPlay.this, false);
                    searchDialog.setSearchable(JPlay.this);
                    searchDialog.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), searchDialog.getHeight());
                    searchDialog.setVisible(true);
                } else {
//                    dialogBuscar.resetTextField();
                    searchDialog.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), searchDialog.getHeight());
                    searchDialog.setVisible(!searchDialog.isVisible());
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
                Log.add("comando! " + filtro);

                // si es un comando, despues cargo de nuevo la biblioteca
                cargarCancionesABiblioteca(musicLibrary.getSongs());

                String text = "";

                if (filtro.equalsIgnoreCase("/rutas")) {
                    Log.add("RUTAS:");
                    text += "RUTAS:\n";

                    for (File ruta : musicLibrary.getPaths()) {
                        Log.add("\t" + ruta.getPath());
                        text += ruta.getPath() + "\n";
                    }

                    JOptionPane.showMessageDialog(this, text);
                } else if (filtro.equalsIgnoreCase("/scan")) {
                    ScanThread s = new ScanThread(musicLibrary, this);
                    s.start();
                } else if (filtro.equalsIgnoreCase("/favs")) {
                    Log.add("Canciones favoritas:");
                    for (Song c : musicLibrary.getFavoritesSongs()) {
                        Log.add(c.toString());
                    }
                }
            }
            musicLibraryTable.setRowSelectionInterval(0, 0);
        } catch (HeadlessException e) {
            // cae aca cuando no hay canciones en la tabla biblioteca
            Log.add(e.getMessage());
        }

    }

    @Override
    public void loadSearchCombobox(JComboBox cbo) {

        cbo.removeAllItems();
        cbo.addItem("");

        for (String artista : musicLibrary.getArtistNames()) {
            cbo.addItem(artista);
        }

        for (Album album : musicLibrary.getAlbums()) {
            if (!album.getName().trim().equals("")) {
                cbo.addItem(album.getName());
            }
        }
    }

    private void initCoverArtPopUp() {
        covertArtPopUp = new JPopupMenu();

        JMenuItem itemEliminarCover = new JMenuItem("Eliminar Cover");

        //JPopupMenu.Separator sep = new JPopupMenu.Separator();
        itemEliminarCover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (coverArtThread.isAlive()) {
                        Image actualCover = coverArtThread.getCurrentCoverArt();

                        coverArtThread.interrupt();

                        Album a = musicLibrary.getAlbum(musicPlayer.getCurrentSong());

                        a.removeCover(new ImageIcon(actualCover));

                        coverArtThread = new CoverArtThread(coverArtLabel, a.getCovers());
                        coverArtThread.start();
                    }
                } catch (NullPointerException ex) {
                    Log.add("Objeto HCOVER es nulo");
                }
            }
        });

        covertArtPopUp.add(itemEliminarCover);

        coverArtLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarPopUpCover(e);
            }
        });
    }

    private void mostrarPopUpCover(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            covertArtPopUp.show(e.getComponent(), e.getX() + 8, e.getY() + 8);
        }
    }

    private void initDragDropTabbedPane() {
        mainTabbedPane.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dtde) {

                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    final List<File> archs = (List) o;

                    loadThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (File f : archs) {
                                try {
//                                    Log.add(f.toString());

                                    cargarCancionesABiblioteca(f);
                                    cargarCancionesABiblioteca(musicLibrary.getSongs());
                                    musicLibrary.processAlbum();
                                    musicLibrary.addPath(f);
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            System.out.println("HILO CARGAR DROP TERMINADO!");
                        }
                    });

                    loadThread.start();
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                currentTabIndex = mainTabbedPane.getSelectedIndex();
                mainTabbedPane.setSelectedIndex(Rule.Tabs.MUSIC_LIBRARY);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                mainTabbedPane.setSelectedIndex(currentTabIndex);
            }
        }));
    }

    /*Este método sirve para actualizar la tabla de biblioteca despues del scan (Clase Scan)*/
    @Override
    public void updateMusicLibraryUI(boolean huboCambios) {
        if (huboCambios) {
            cargarCancionesABiblioteca(musicLibrary.getSongs());
            musicLibrary.processAlbum();
        } else {
            Log.add("[No hubo cambios en la biblioteca]");
        }
    }

    @Override
    public void updateLogUI(LogEntry newLogEntry) {
        try {
            tableLogger.updateUI();
        } catch (NullPointerException e) {

        }
        mainTabbedPane.setTitleAt(Rule.Tabs.LOGGER, "Logger (" + tableLogger.getRowCount() + ")");
    }

    private void initLog() {
        Log.setUpdateLogUI(this);
        logTableModel = new LogTableModel();
        tableLogger.setModel(logTableModel);
    }

    private void initTabIcons() {
        mainTabbedPane.setIconAt(Rule.Tabs.EXPLORER, SongListTreeCellRenderer.getImageIcon(Path.EXPLORER_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.Tabs.MUSIC_LIBRARY, SongListTreeCellRenderer.getImageIcon(Path.MUSIC_LIBRARY_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.Tabs.CURRENT_LIST, SongListTreeCellRenderer.getImageIcon(Path.LIST_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.Tabs.MOST_PLAYED, SongListTreeCellRenderer.getImageIcon(Path.MOST_PLAYED_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.Tabs.LOGGER, SongListTreeCellRenderer.getImageIcon(Path.LOG_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.Tabs.FAVORITES, new ImageIcon(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    private void setColorFondo(Album album) {
        /* Colores de fondo */
        Util.BACKGROUND_COLOR = album.getAverageColor();
        Util.FOREGROUND_COLOR = Util.getForeGroundColorBasedOnBGBrightness(Util.BACKGROUND_COLOR);

        panelPrincipal.setBackground(Util.BACKGROUND_COLOR);
        volumeSlider.setBackground(Util.BACKGROUND_COLOR);
        lblNombreCancion.setForeground(Util.FOREGROUND_COLOR);
        lblArtista.setForeground(Util.FOREGROUND_COLOR);
        repeatSongCheckbox.setForeground(Util.FOREGROUND_COLOR);
        randomCheckbox.setForeground(Util.FOREGROUND_COLOR);
        lblInfoCarga.setForeground(Util.FOREGROUND_COLOR);
        lblDuracion.setForeground(Util.FOREGROUND_COLOR);

        progress.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Util.BACKGROUND_COLOR;
            }

            @Override
            protected Color getSelectionForeground() {
                return Util.FOREGROUND_COLOR;
            }
        });

        progress.setForeground(new Color(76, 175, 80));

        Color color = Util.BACKGROUND_COLOR.darker().darker();
        mainTabbedPane.setBackground(color);
        mainTabbedPane.setForeground(Util.getForeGroundColorBasedOnBGBrightness(color));

        // Acá cambio los iconos según color
        if (Util.FOREGROUND_COLOR == Color.white) {
            lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));
            lblSiguiente.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_NEXT_ICON)));
            lblAnterior.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_BACK_ICON)));
        } else {
            lblPlay.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
            lblSiguiente.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_NEXT_ICON)));
            lblAnterior.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_BACK_ICON)));
        }
    }

    private void setCover(Icon cover) {

        BufferedImage image = new BufferedImage(cover.getIconWidth(), cover.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        cover.paintIcon(null, image.getGraphics(), 0, 0);

        coverArtLabel.setIcon(new ImageIcon(
                image.getScaledInstance(
                        (int) Rule.COVER_ART_DIMENSION.getWidth(),
                        (int) Rule.COVER_ART_DIMENSION.getHeight(),
                        Image.SCALE_SMOOTH)
        )
        );
    }

    private void setCover(Image cover) {
        coverArtLabel.setIcon(new ImageIcon(
                cover.getScaledInstance(
                        (int) Rule.COVER_ART_DIMENSION.getWidth(),
                        (int) Rule.COVER_ART_DIMENSION.getHeight(),
                        Image.SCALE_SMOOTH)
        )
        );
    }

    private void initArtistCoversArt() {

        if (artistCoversArt == null) {
            artistCoversArt = new ArrayList<>();
            musicLibrary.getArtistNames().forEach((artista) -> {
                try {
                    artistCoversArt.add(new ArtistCoverArt(artista));
                } catch (Exception ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            for (String artista : musicLibrary.getArtistNames()) {
                if (!estaArtista(artista)) {
                    try {
                        artistCoversArt.add(new ArtistCoverArt(artista));
                    } catch (Exception ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /*Ordena descendente los artistas*/
        Collections.sort(artistCoversArt, new Comparator<ArtistCoverArt>() {
            @Override
            public int compare(ArtistCoverArt c1, ArtistCoverArt c2) {
                return c1.getArtistName().compareTo(c2.getArtistName());
            }
        });

        try {
            listArtistas.setCellRenderer(new ArtistListCellRenderer(artistCoversArt));
            listArtistas.setModel(new ArtistListModel(artistCoversArt));
            listArtistas.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            listArtistas.setVisibleRowCount(-1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean estaArtista(String artista) {
        boolean estado = artistCoversArt.stream().anyMatch((ca) -> (ca.getArtistName().equals(artista)));
        System.out.println("ARTISTA [" + artista + "] --> " + estado);
        return estado;
    }

}
