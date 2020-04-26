package cl.prezdev.xjplay.main;

//iconos https://www.iconfinder.com/iconsets/snipicons
import cl.prezdev.jplay.Album;
import cl.prezdev.jplay.MusicLibrary;
import cl.prezdev.jplay.Song;
import cl.prezdev.jplay.MusicPlayer;

import cl.prezdev.jplay.common.ImageProcessor;
import cl.prezdev.jplay.common.Util;
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

import javax.swing.*;
import javax.swing.JPopupMenu.Separator;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import cl.prezdev.xjplay.model.search.SearchListener;
import cl.prezdev.xjplay.model.scan.MusicLibraryUiUpdate;

import static java.awt.EventQueue.invokeLater;

public class JPlay extends JFrame implements
        BasicPlayerListener, SearchListener, MusicLibraryUiUpdate{
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

        currentSongs = new ArrayList<>();
        albums = new ArrayList<>();
        musicLibrary = new MusicLibrary();

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
        cleanLabel.setVisible(false);
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/

        setIconImage(iconApp);

        coverArtLabel.setText(null);

        setBounds(0, 0, 800, 600);
        setLocationRelativeTo(null);
        coverArtThread = null;

        coverArtLabel.requestFocus();
        initMostPlayerSongsTree();
        loadFavoritesSongsTree();
        musicLibrary.printAlbumsToLog();
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
        backSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_BACK_ICON)));
        nextSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_NEXT_ICON)));
        playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
        favoriteToggleButton.setIcon(new ImageIcon(getClass().getResource(Path.FAVORITES_TAB_ICON)));
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
        cleanLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeSong = new javax.swing.JTree();
        panelMasEscuchadas = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        mostPlayedSongTree = new javax.swing.JTree();
        panelFavoritos = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        favoritesTree = new javax.swing.JTree();
        panelLogger = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        loggerTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        artistList = new javax.swing.JList();
        progressBarSong = new javax.swing.JProgressBar();
        loadInfoLabel = new javax.swing.JLabel();
        cancelLoadingButton = new javax.swing.JButton();
        nameSongLabel = new javax.swing.JLabel();
        artistLabel = new javax.swing.JLabel();
        volumeSlider = new javax.swing.JSlider();
        favoriteToggleButton = new javax.swing.JToggleButton();
        repeatSongCheckbox = new javax.swing.JCheckBox();
        randomCheckbox = new javax.swing.JCheckBox();
        durationLabel = new javax.swing.JLabel();
        backSongLabel = new javax.swing.JLabel();
        playSongLabel = new javax.swing.JLabel();
        nextSongLabel = new javax.swing.JLabel();

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
        mainTabbedPane.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainTabbedPaneMouseReleased(evt);
            }
        });

        treeExplorer.addMouseListener(new MouseAdapter() {
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
        musicLibraryTable.addMouseListener(new MouseAdapter() {
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
        songsTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                songsTableMouseReleased(evt);
            }
        });
        songsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                songsTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(songsTable);

        cleanLabel.setBackground(new java.awt.Color(63, 81, 181));
        cleanLabel.setForeground(new java.awt.Color(254, 254, 254));
        cleanLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cleanLabel.setText("Limpiar");
        cleanLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cleanLabel.setOpaque(true);
        cleanLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cleanLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cleanLabelMouseReleased(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cleanLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cleanLabelMouseEntered(evt);
            }
        });

        treeSong.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeSongMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(treeSong);

        javax.swing.GroupLayout panelListaActualLayout = new javax.swing.GroupLayout(panelListaActual);
        panelListaActual.setLayout(panelListaActualLayout);
        panelListaActualLayout.setHorizontalGroup(
            panelListaActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cleanLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(cleanLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainTabbedPane.addTab("Lista actual", panelListaActual);

        panelMasEscuchadas.setLayout(new java.awt.BorderLayout());

        mostPlayedSongTree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mostPlayedSongTreeMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(mostPlayedSongTree);

        panelMasEscuchadas.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("+ escuchadas", panelMasEscuchadas);

        panelFavoritos.setLayout(new java.awt.BorderLayout());

        favoritesTree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                favoritesTreeMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(favoritesTree);

        panelFavoritos.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Favoritos", panelFavoritos);

        panelLogger.setLayout(new java.awt.BorderLayout());

        loggerTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane6.setViewportView(loggerTable);

        panelLogger.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Logger (DEV)", panelLogger);

        jPanel1.setLayout(new java.awt.BorderLayout());

        artistList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                artistListMouseReleased(evt);
            }
        });
        jScrollPane8.setViewportView(artistList);

        jPanel1.add(jScrollPane8, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Artistas", jPanel1);

        progressBarSong.setBackground(new java.awt.Color(254, 254, 254));
        progressBarSong.setForeground(new java.awt.Color(255, 255, 255));
        progressBarSong.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                songProgressBarMouseDragged(evt);
            }
        });
        progressBarSong.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                songProgressBarMouseReleased(evt);
            }
        });

        loadInfoLabel.setBackground(new java.awt.Color(254, 254, 254));
        loadInfoLabel.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        loadInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        loadInfoLabel.setText("[lblInfo]");

        cancelLoadingButton.setText("X");
        cancelLoadingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelLoadingButtonActionPerformed(evt);
            }
        });

        nameSongLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        nameSongLabel.setText("Artista / Canción");

        artistLabel.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        artistLabel.setText("Artista / Canción");

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
        volumeSlider.addMouseListener(new MouseAdapter() {
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

        favoriteToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favoriteToggleButtonActionPerformed(evt);
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

        durationLabel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        durationLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        durationLabel.setText("0:00 - 0:00");

        backSongLabel.setText("A");
        backSongLabel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backSongLabelMouseReleased(evt);
            }
        });

        playSongLabel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                playLabelMouseReleased(evt);
            }
        });

        nextSongLabel.setText("A");
        nextSongLabel.addMouseListener(new MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                nextSongLabelMouseReleased(evt);
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
                        .addComponent(backSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coverArtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPrincipalLayout.createSequentialGroup()
                                .addComponent(artistLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(durationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(progressBarSong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameSongLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(5, 5, 5))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cancelLoadingButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(randomCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(repeatSongCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(favoriteToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(nameSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(artistLabel)
                            .addComponent(durationLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBarSong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(playSongLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nextSongLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backSongLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(coverArtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(volumeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelLoadingButton)
                            .addComponent(loadInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(randomCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(repeatSongCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(favoriteToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        setVolume(volumeSlider.getValue());
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
            loadSong();
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
                        loadFilesInTreeNode(node, file);
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

    private void treeSongMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSong.getLastSelectedPathComponent();

            if (node != null) {
                Object userObject = node.getUserObject();

                if (userObject instanceof Song) {
                    Song song = (Song) userObject;
                    play(song);
                }
            }

        }
    }

    private void songProgressBarMouseReleased(MouseEvent mouseEvent) {
        changeSongProgressBarValue((mouseEvent.getX() * 100) / progressBarSong.getWidth(), true);

        // seteo el volumen por un bug descubierto hoy (9 de marzo de 2018)
        setVolume(volumeSlider.getValue());
        printProgressBar = true;
    }

    private void songProgressBarMouseDragged(MouseEvent mouseEvent) {
        changeSongProgressBarValue((mouseEvent.getX() * 100) / progressBarSong.getWidth(), false);
        printProgressBar = false;
    }

    private void cleanLabelMouseReleased(MouseEvent mouseEvent) {
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(63, 81, 181));
        currentSongs = new ArrayList<>();
        loadAlbumsInTreeSong(getAlbums(currentSongs));
    }

    private void cleanLabelMousePressed(MouseEvent mouseEvent) {
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(26, 35, 126));
    }

    private void cleanLabelMouseExited(MouseEvent mouseEvent) {
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new java.awt.Color(63, 81, 181));
    }

    private void cleanLabelMouseEntered(MouseEvent mouseEvent) {
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(92, 107, 192));
    }

    private void songsTableKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {

            SongTableModel songTableModel = (SongTableModel) songsTable.getModel();
            int index = songsTable.getSelectedRow();
            // @TODO: index = 0 ?
            Song song = (Song) songTableModel.getValueAt(index, 0);

            play(song);
        }
    }

    // @TODO: Quizás eliminar este método
    private void songsTableMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            SongTableModel songTableModel = (SongTableModel) songsTable.getModel();
        }
    }

    private void mostPlayedSongTreeMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) mostPlayedSongTree.getLastSelectedPathComponent();
            if (node != null) {
                Object userObject = node.getUserObject();

                if (userObject instanceof Song) {
                    // @TODO: Una vez que lo de abajo pase a clase Album, cambiar nonbre a song
                    Song selectedSong = (Song) userObject;

                    currentSongs = musicLibrary.getMostPlayedSongs();

                    /*Ahora debo poner las canciones en un album*/
                    // @TODO: Crear constructor sin año en Album, y pasándole una lista de canciones
                    Album album = new Album(Rule.NAME, "Canciones más escuchadas!", null);

                    // @TODO: Esto irá en el constructor de album
                    for (Song can : currentSongs) {
                        album.addSong(can);
                    }

                    List<ImageIcon> coversArt = new ArrayList<>();
                    coversArt.add(SongListTreeCellRenderer.getImageIcon(Path.HEART_ICON));
                    album.setCoversArt(coversArt);

                    List<Album> albums = new ArrayList<>();
                    albums.add(album);
                    /*Ahora debo poner las canciones en un album*/

                    loadAlbumsInTreeSong(albums);

                    play(selectedSong);

                    mainTabbedPane.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
                }
            }
        }
    }

    private void mainTabbedPaneMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (mainTabbedPane.getSelectedIndex() == 1) {
                loadSongsInMusicLibrary(musicLibrary.getSongs());
            }
        }

        switch (mainTabbedPane.getSelectedIndex()) {
            case Rule.TabIndex.MUSIC_LIBRARY:
            case Rule.TabIndex.EXPLORER:
            case Rule.TabIndex.LOGGER:
                loadInfoLabel.setText(musicLibrary.getLibraryDuration());
                break;

            case Rule.TabIndex.FAVORITES:
                loadInfoLabel.setText(musicLibrary.getFavoritesDuration());
                break;

            case Rule.TabIndex.CURRENT_SONGS_LIST:
                loadInfoLabel.setText("Lista actual --> " + Util.getFormattedDuration(millisecondsOfCurrentSongs));
                break;

            case Rule.TabIndex.MOST_PLAYED:
                loadInfoLabel.setText(musicLibrary.getMostPlayedDuration());
                break;
        }
    }

    private void favoriteToggleButtonActionPerformed(ActionEvent actionEvent) {
        if (musicPlayer.getCurrentSong() != null) {
            if (favoriteToggleButton.isSelected()) {
                musicLibrary.addFavoriteSong(musicPlayer.getCurrentSong());
            } else {
                musicLibrary.removeFavoriteSong(musicPlayer.getCurrentSong());
            }

            loadFavoritesSongsTree();
        }
    }

    private void favoritesTreeMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) favoritesTree.getLastSelectedPathComponent();

            if (node != null) {
                Object userObject = node.getUserObject();

                // @TODO: Una vez que lo de abajo pase a clase Album, cambiar nonbre a song
                if (userObject instanceof Song) {
                    Song selectedSong = (Song) userObject;

                    currentSongs = musicLibrary.getFavoritesSongs();

                    /*Ahora debo poner las canciones en un album*/
                    // @TODO: Crear constructor sin año en Album, y pasándole una lista de canciones
                    Album album = new Album(Rule.NAME, "Favoritas!", null);

                    // @TODO: Esto irá en el constructor de album
                    for (Song song : currentSongs) {
                        album.addSong(song);
                    }

                    // @TODO: esto esta en el método mostPlayedSongTreeMouseReleased()
                    List<ImageIcon> coversArt = new ArrayList<>();
                    coversArt.add(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON));
                    album.setCoversArt(coversArt);

                    List<Album> albums = new ArrayList<>();
                    albums.add(album);
                    /*Ahora debo poner las canciones en un album*/

                    loadAlbumsInTreeSong(albums);

                    play(selectedSong);

                    mainTabbedPane.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
                }
            }
        }
    }

    private void playLabelMouseReleased(MouseEvent mouseEvent) {
        try {
            if (musicPlayer != null) {
                if (isPlay) {
                    isPlay = false;

                    if (Rule.FOREGROUND_COLOR == Color.black) {
                        playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
                    } else {
                        playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));
                    }

                    musicPlayer.pause();
                } else {
                    isPlay = true;

                    if (Rule.FOREGROUND_COLOR == Color.black) {
                        playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PAUSE_ICON)));
                    } else {
                        playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PAUSE_ICON)));
                    }

                    if (isStop) {
                        playCurrentSong();
                        isPlay = true;
                        isStop = false;
                    } else {
                        musicPlayer.resume();
                    }
                }
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void backSongLabelMouseReleased(MouseEvent mouseEvent) {
        if (!currentSongs.isEmpty()) {
            // @TODO: Idea: Métodos play en model (jplay) y llamar a listeners (xjplay)
            if (isRandom) {
                playRandomSong();
            } else if (repeatSong) {
                playCurrentSong();
            } else {
                playPreviousSong();
            }
        }
    }

    private void nextSongLabelMouseReleased(MouseEvent mouseEvent) {
        if (!currentSongs.isEmpty()) {
            if (isRandom) {
                playRandomSong();
            } else if (repeatSong) {
                playCurrentSong();
            } else {
                playNextSong();
            }
        }
    }

    private void artistListMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Object selectedValue = artistList.getSelectedValue();
            ListCellRenderer listCellRenderer = null;
            ListModel listModel = null;

            if (selectedValue instanceof ArtistCoverArt) {
                ArtistCoverArt artistCoverArt = (ArtistCoverArt) selectedValue;
                List<Album> albumsByArtist = musicLibrary.getAlbumsByArtist(artistCoverArt.getArtistName());
                albumsByArtist.add(0, new BackAlbum());

                listCellRenderer = new AlbumListCellRenderer(albumsByArtist);
                listModel = new AlbumListModel(albumsByArtist);

            } else if (selectedValue instanceof BackAlbum) {
                // quiere ir atrás, o sea a los artistas
                listCellRenderer = new ArtistListCellRenderer(artistCoversArt);
                listModel = new ArtistListModel(artistCoversArt);
            }

            artistList.setCellRenderer(listCellRenderer);
            artistList.setModel(listModel);
        }
    }

    private void loadSave() {
        if (new File(Path.SAVE).exists()) {
            try {
                Save save = (Save) IO.readObject(Path.SAVE);

                currentSongs = save.songs;
                artistCoversArt = save.artistCoversArt;

                /*Recuperando el volumen del usuario*/
                volumeSlider.setValue(save.volume);
                setVolume(volumeSlider.getValue());

                /*Recuperando el volumen del usuario*/
                mainTabbedPane.setSelectedIndex(save.indexTab);

                mainTabbedPane.setTitleAt(Rule.TabIndex.LOGGER, "Logger (" + loggerTable.getRowCount() + ")");

                setCoverArt(save.cover);

                musicLibrary = (MusicLibrary) IO.readObject(Path.MUSIC_LIBRARY);

                loadAlbumsInTreeSong(save.albums);
                loadSongsInMusicLibrary(musicLibrary.getSongs());
                showCurrentSongInfo();
            } catch (InvalidClassException ex) {
                musicLibrary = new MusicLibrary();
                currentSongs = musicLibrary.getSongs();
                artistCoversArt = new ArrayList<>();
                loadDefault();
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            loadDefault();
        }

        switch (mainTabbedPane.getSelectedIndex()) {
            case Rule.TabIndex.MUSIC_LIBRARY:
            case Rule.TabIndex.EXPLORER:
            case Rule.TabIndex.LOGGER:
                loadInfoLabel.setText(musicLibrary.getLibraryDuration());
                break;

            case Rule.TabIndex.FAVORITES:
                loadInfoLabel.setText(musicLibrary.getFavoritesDuration());
                break;

            case Rule.TabIndex.CURRENT_SONGS_LIST:
                loadInfoLabel.setText("Lista actual --> " + Util.getFormattedDuration(millisecondsOfCurrentSongs));
                break;

            case Rule.TabIndex.MOST_PLAYED:
                loadInfoLabel.setText(musicLibrary.getMostPlayedDuration());
                break;
        }
    }

    private void loadFilesInTreeNode(DefaultMutableTreeNode rootTreeNode, File rootFile) {
        List<File> files = new ArrayList<>();

        if (rootFile.listFiles() != null) {
            File namedFile;
            for (File file : rootFile.listFiles()) {
                if (!Validate.isHiddenFile(file)) {
                    try {
                        if (file.isDirectory() || Validate.isSong(file)) {
                            /*Esto es solo para que se vea el nombre, por ende
                             tuve que sobre escribir el método toString*/
                            namedFile = new File(file.getPath()) {
                                @Override
                                public String toString() {
                                    return this.getName();
                                }
                            };

                            files.add(namedFile);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            //ordenar acá
            Collections.sort(files, (file, anotherFile) -> file.compareTo(anotherFile));

            for (File file : files) {
                rootTreeNode.add(new DefaultMutableTreeNode(file));
            }
        }
    }

    /**
     * Método para cargar canciones cuando el usuario las quiera escoger desde
     * el árbol con el click secundario
     *
     * @param rootFile
     */
    private void loadSongsInMusicLibrary(File rootFile) throws IOException {
        if (rootFile.listFiles() != null) {
            for (File file : rootFile.listFiles()) {
                if (file.isDirectory()) {
                    loadSongsInMusicLibrary(file);
                } else if (Validate.isSong(file)) {
                    Song song = new Song(file.getPath());
                    musicLibrary.addSong(song);
                    loadInfoLabel.setText("Agregando " + song);
                }
            }
        } else {
            Song song = new Song(rootFile.getPath());
            musicLibrary.addSong(song);
            loadInfoLabel.setText("Agregando " + song);
        }
    }

    private void loadSongToSongList(File rootFile) throws IOException, InterruptedException {
        if (rootFile.listFiles() != null) {
            for (File file : rootFile.listFiles()) {
                if (file.isDirectory()) {
                    loadSongToSongList(file);
                } else if (Validate.isSong(file)) {
                    Song song = new Song(file.getPath());
                    currentSongs.add(song);
                }
            }
        }
    }

    public static void main(String args[]) {
        invokeLater(() -> new JPlay().setVisible(true));
    }

    private javax.swing.JToggleButton favoriteToggleButton;
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
    private javax.swing.JLabel backSongLabel;
    private javax.swing.JLabel artistLabel;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JLabel loadInfoLabel;
    private javax.swing.JLabel cleanLabel;
    private javax.swing.JLabel nameSongLabel;
    private javax.swing.JLabel playSongLabel;
    private javax.swing.JLabel nextSongLabel;
    private javax.swing.JList artistList;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox randomCheckbox;
    private javax.swing.JPanel panelFavoritos;
    private javax.swing.JPanel panelListaActual;
    private javax.swing.JPanel panelLogger;
    private javax.swing.JPanel panelMasEscuchadas;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JProgressBar progressBarSong;
    private javax.swing.JCheckBox repeatSongCheckbox;
    private javax.swing.JSlider volumeSlider;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JTable musicLibraryTable;
    private javax.swing.JTable songsTable;
    private javax.swing.JTable loggerTable;
    private javax.swing.JTree treeExplorer;
    private javax.swing.JTree favoritesTree;
    private javax.swing.JTree mostPlayedSongTree;
    private javax.swing.JTree treeSong;

    private void setProgressBarSongMaxValue(int totalBytes) {
        this.totalBytes = totalBytes;
        progressBarSong.setMaximum(totalBytes);
    }

    private void setProgressBarSonValue(int bytesRead) {
        if (printProgressBar) {
            progressBarSong.setValue(bytesRead);
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
    public void opened(Object stream, Map properties) { }

    /**
     *
     * @param bytesRead from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmData PCM samples.
     * @param properties audio stream parameters.
     */
    @Override
    public void progress(int bytesRead, long microseconds, byte[] pcmData, Map properties) {
        setProgressBarSonValue(bytesRead);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
        switch (bpe.getCode()) {
            case BasicPlayerEvent.EOM:
                /*
                End of music: Se ejecuta esto cuando finaliza de tocar un tema
                 */
                if (!repeatSong) {
                    if (isRandom) {
                        playRandomSong();
                    } else {
                        playNextSong();
                    }
                } else {
                    playCurrentSong();
                }

                break;

            case BasicPlayerEvent.RESUMED:
                workerStringProgress.resume();
                break;

            case BasicPlayerEvent.PAUSED:
                workerStringProgress.pause();
                break;

            case BasicPlayerEvent.OPENED:
                setProgressBarSongMaxValue((int) musicPlayer.getCurrentSong().length());
                break;
        }
    }

    @Override
    public void setController(BasicController basicController) {}

    private void setVolume(int volume) {
        try {
            if (musicPlayer != null) {
                musicPlayer.setVolume(volume);
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initExplorerTree() {
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("raiz");
        File[] files = File.listRoots();

        for (File file : files) {
            rootTreeNode.add(new DefaultMutableTreeNode(file));
        }

        treeExplorer.setModel(new DefaultTreeModel(rootTreeNode));
        treeExplorer.setRootVisible(false);

        // @TODO: Mejorar esta mierda, wtf
        treeExplorer.setCellRenderer(new ExplorerTreeCellRenderer(
                new ImageIcon(
                    ExplorerTreeCellRenderer.getImageIcon(Path.MUSIC_ICON).getImage().
                        getScaledInstance(
                                Rule.ICON_EXPLORER_MUSIC_SIZE,
                                Rule.ICON_EXPLORER_MUSIC_SIZE,
                                Image.SCALE_SMOOTH
                        )
                ),
                new ImageIcon(
                    ExplorerTreeCellRenderer.getImageIcon(Path.FOLDER_ICON).getImage().
                        getScaledInstance(
                            Rule.ICON_EXPLORER_SIZE,
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
            millisecondsOfCurrentSongs += song.getMilliSeconds();
        }

        loadInfoLabel.setText("Lista actual --> " + Util.getFormattedDuration(millisecondsOfCurrentSongs));
    }

    private void initMostPlayerSongsTree() {
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("raiz");

        List<Song> mostPlayedSongs = musicLibrary.getMostPlayedSongs();

        for (Song song : mostPlayedSongs) {
            DefaultMutableTreeNode songTreeNode = new DefaultMutableTreeNode(song);
            rootTreeNode.add(songTreeNode);
        }

        mostPlayedSongTree.setModel(new DefaultTreeModel(rootTreeNode));
        mostPlayedSongTree.setRootVisible(false);

        mostPlayedSongTree.setCellRenderer(
            new SongMostPlayedTreeCellRenderer()
        );

        mainTabbedPane.setTitleAt(
            Rule.TabIndex.MOST_PLAYED,
            "+ escuchadas (" + mostPlayedSongs.size() + ")"
        );
    }

    private void loadFavoritesSongsTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("rootNode");
        DefaultMutableTreeNode songNode;

        List<Song> favoritesSongs = musicLibrary.getFavoritesSongs();

        for (Song song : favoritesSongs) {
            songNode = new DefaultMutableTreeNode(song);
            rootNode.add(songNode);
        }

        favoritesTree.setModel(new DefaultTreeModel(rootNode));
        favoritesTree.setRootVisible(false);
        favoritesTree.setCellRenderer(new FavoritesTreeCellRenderer());

        mainTabbedPane.setTitleAt(Rule.TabIndex.FAVORITES, "Favoritos (" + favoritesSongs.size() + ")");
    }

    private void initExplorerTreePopUp() {
        /*
        * Este codigo es para que cuando el usuario haga
        * click secundario se seleccione la fila del arbol
        * */
        MouseListener mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    int selectedRow = treeExplorer.getRowForLocation(
                        mouseEvent.getX(),
                        mouseEvent.getY()
                    );

                    TreePath locationPath = treeExplorer.getPathForLocation(
                        mouseEvent.getX(),
                        mouseEvent.getY()
                    );

                    treeExplorer.setSelectionPath(locationPath);

                    if (selectedRow > - 1) {
                        treeExplorer.setSelectionRow(selectedRow);
                        explorerTreePopUp.show(treeExplorer, mouseEvent.getX() + 10, mouseEvent.getY() + 10);
                    }
                }
            }
        };

        treeExplorer.addMouseListener(mouseAdapter);
        
        explorerTreePopUp = new JPopupMenu();

        JMenuItem toNewListMenuItem = new JMenuItem("A lista nueva");
        JMenuItem toAlreadyExistMenuItem = new JMenuItem("Añadir a existente");
        JMenuItem toMusicLibraryMenuItem = new JMenuItem("Añadir a biblioteca");

        explorerTreePopUp.add(toNewListMenuItem);
        explorerTreePopUp.add(toAlreadyExistMenuItem);
        explorerTreePopUp.add(new Separator());
        explorerTreePopUp.add(toMusicLibraryMenuItem);

        toNewListMenuItem.addActionListener(event -> {
            final File file = getSelectedTreeFile();

            currentSongs = new ArrayList<>();

            loadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);

                try {
                    loadSongToSongList(file);
                    loadAlbumsInTreeSong(getAlbums(currentSongs));
                } catch (IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {}
            });

            loadThread.start();
        });

        toAlreadyExistMenuItem.addActionListener(event -> {
            loadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);

                try {
                    File file = getSelectedTreeFile();
                    loadSongToSongList(file);
                    loadAlbumsInTreeSong(getAlbums(currentSongs));
                } catch (IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            loadThread.start();
        });

        toMusicLibraryMenuItem.addActionListener((ActionEvent event) -> {
            loadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);
                try {
                    File file = getSelectedTreeFile();

                    loadSongsInMusicLibrary(file);
                    loadSongsInMusicLibrary(musicLibrary.getSongs());

                    musicLibrary.addSongsToAlbums();
                    musicLibrary.addPath(file);

                    initArtistCoversArt();
                } catch (IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("HILO CARGAR BIBLIOTECA TERMINADO!");
            });

            loadThread.start();
        });
    }

    private void initMusicLibraryPopUp() {
        musicLibraryPopUp = new JPopupMenu();

        JMenuItem removeFromMusicLibraryMenuItem = new JMenuItem("Remover");
        JMenuItem playAlbumMenuItem = new JMenuItem("Reproducir Disco");
        JMenuItem addAlbumMenuItem = new JMenuItem("Añadir Disco");

        musicLibraryPopUp.add(playAlbumMenuItem);
        musicLibraryPopUp.add(addAlbumMenuItem);
        musicLibraryPopUp.add(new Separator());
        musicLibraryPopUp.add(removeFromMusicLibraryMenuItem);

        playAlbumMenuItem.addActionListener(actionListener -> {
            int[] selectedRows = musicLibraryTable.getSelectedRows();

            List<Album> albums = new ArrayList<>();
            Song selectedSong;
            Album album;

            currentSongs = new ArrayList<>();

            // @TODO: Analizar esto (no lo he hecho)
            // Me tinca que se puede mejorar (y se debe mejorar)
            for (int row : selectedRows) {
                selectedSong = (Song) musicLibraryTable.getValueAt(
                    row, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX
                );

                album = musicLibrary.getAlbum(selectedSong);

                if (!albums.contains(album)) {
                    albums.add(album);

                    currentSongs.addAll(album.getSongs());
                }
            }

            loadAlbumsInTreeSong(getAlbums(currentSongs));
            play(currentSongs.get(0));
            mainTabbedPane.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
        });

        addAlbumMenuItem.addActionListener(actionListener -> {
            int[] selectedRows = musicLibraryTable.getSelectedRows();

            List<Album> albums = new ArrayList<>();
            Song selectedSong;
            Album album;

            for (int row : selectedRows) {
                selectedSong = (Song) musicLibraryTable.getValueAt(
                    row, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX
                );

                album = musicLibrary.getAlbum(selectedSong);

                if (!albums.contains(album)) {
                    albums.add(album);

                    currentSongs.addAll(album.getSongs());
                }
            }

            loadAlbumsInTreeSong(getAlbums(currentSongs));
        });

        removeFromMusicLibraryMenuItem.addActionListener(actionListener -> {
            int[] selectedRows = musicLibraryTable.getSelectedRows();

            List<Song> songs = new ArrayList<>();

            for (int row : selectedRows) {
                songs.add((Song) musicLibraryTable.getValueAt(
                    row, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX)
                );
            }

            for (Song song : songs) {
                musicLibrary.removeSong(song);
            }

            loadSongsInMusicLibrary(musicLibrary.getSongs());
        });

    }

    private File getSelectedTreeFile() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeExplorer.getLastSelectedPathComponent();

        if (treeNode != null) {
            Object userObject = treeNode.getUserObject();

            if (userObject instanceof File) {
                return new File(((File) userObject).getPath());
                /*Por una razón que desconozco, el objeto File guardaba una referencia a Jplay
                por ende, cuando lo serializaba, enviaba un error.*/
            }
        }

        return null;
    }

    private void loadSongsInMusicLibrary(List<Song> songs) {
        Collections.sort(songs, (Comparator<File>) (file, anotherFile) -> file.compareTo(anotherFile));
        musicLibraryTable.setModel(new MusicLabrarySongTableModel(songs));
        mainTabbedPane.setTitleAt(Rule.TabIndex.MUSIC_LIBRARY, "Biblioteca (" + songs.size() + ")");

        musicLibraryTable.getColumnModel().getColumn(0).setPreferredWidth(Rule.TRACK_NUMBER_COLUMN_SIZE);
        musicLibraryTable.getColumnModel().getColumn(1).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        musicLibraryTable.getColumnModel().getColumn(2).setPreferredWidth(Rule.ALBUM_COLUMN_SIZE);
        musicLibraryTable.getColumnModel().getColumn(3).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);

        cancelLoadingButton.setEnabled(false);
        musicLibrary.addSongsToAlbums();
    }

    /*
    * @TODO: Primera idea:
    *   Llamar a play del model (jplay) y desde ahí, con listeners, cambiar el gui (xjplay)
    * */
    private void play(Song song) {
        favoriteToggleButton.setSelected(musicLibrary.isFavoriteSong(song));

        try {
            new Thread(() -> {
                setCover(song);
            }).start();

            if (musicPlayer != null) {
                musicPlayer.stop();
            }

            musicPlayer = new MusicPlayer(song, this);

            musicPlayer.play();

            song.increasePlayCount();

            setTitle(
                Rule.NAME + " - " + 
                Rule.VERSION + " [" + 
                song.getAuthor() + " - " +
                song.getName() + " (" + song.getPlayCount() + ")]"
            );
            
            setVolume(volumeSlider.getValue());

            if (Rule.FOREGROUND_COLOR == Color.white) {
                playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PAUSE_ICON)));
            } else {
                playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PAUSE_ICON)));
            }

            isPlay = true;
            isStop = false;

            initMostPlayerSongsTree();

            if (this.workerStringProgress != null) {
                this.workerStringProgress.cancel(true);
            }

            String durationAsString = Util.getDurationAsString(song.getMicroseconds());
            this.workerStringProgress = new WorkerStringProgress(durationLabel, durationAsString);

            this.workerStringProgress.execute();

            showCurrentSongInfo();
        } catch (BasicPlayerException ex) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void playCurrentSong() {
        play(currentSongs.get(currentSongs.indexOf(musicPlayer.getCurrentSong())));
    }

    private void showCurrentSongInfo() {
        if (musicPlayer != null) {
            Song song = musicPlayer.getCurrentSong();
            artistLabel.setText(song.getAuthor() + " - " + song.getAlbum() + " (" + song.getYear() + ")");

            String durationAsString = Util.getDurationAsString(song.getMicroseconds());
            nameSongLabel.setText(song.getName() + " (" + durationAsString + ")");
        }
    }

    private void loadDefault() {
        loadSongsInMusicLibrary(musicLibrary.getSongs());
        loadAlbumsInTreeSong(null);
        setCoverArt(iconApp);
    }

    private void initTitleMusicLibraryClickListener() {
        musicLibraryTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int column = musicLibraryTable.columnAtPoint(mouseEvent.getPoint());
                String columnName = musicLibraryTable.getColumnName(column);
            }
        });
    }

    public int getRandom() {
        return new Random().nextInt(currentSongs.size());
    }

    private void playSelectedMusicLibrarySong() {
        int selectedRow = musicLibraryTable.getSelectedRow();
        Song song = (Song) musicLibraryTable.getValueAt(selectedRow, MusicLabrarySongTableModel.COMPLETE_OBJECT_INDEX);

        MusicLabrarySongTableModel musicLibraryTableModel = (MusicLabrarySongTableModel) musicLibraryTable.getModel();

        currentSongs = musicLibraryTableModel.songs;

        loadAlbumsInTreeSong(getAlbums(currentSongs));

        play(song);
    }

    // Método que se llama cuando hago doble click en un tema musical en el árbol (explorer tree)
    private void loadSong() {
        final File selectedTreeFile = getSelectedTreeFile();
        if (selectedTreeFile != null) {
            try {
                if (Validate.isSong(selectedTreeFile)) {
                    currentSongs = new ArrayList<>();
                    loadThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            cancelLoadingButton.setEnabled(true);

                            try {
                                loadSongToSongList(selectedTreeFile.getParentFile());
                            } catch (InterruptedException | IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            loadAlbumsInTreeSong(getAlbums(currentSongs));
                        }
                    });

                    loadThread.start();
                    play(new Song(selectedTreeFile.getPath()));
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

    private void changeSongProgressBarValue(int percentage, boolean seek) {
        final float VALUE = totalBytes * ((float) percentage / (float) 100);
        progressBarSong.setValue((int) VALUE);

        if (seek) {
            try {
                this.workerStringProgress.changeProgressBar(percentage);
                musicPlayer.seek((long) VALUE);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método que se llama cuando se reproduce una canción
     * @param song
     */
    private void setCover(Song song) {
        setCoverArt(iconApp);
        Album album = musicLibrary.getAlbum(song);

        if (!song.exists()) { // si canción no existe
            if (JOptionPane.showConfirmDialog(
                    this,
                    song.exists() + "[" + song.getName() + "] no encontrada. "
                    + "¿Desea analizar la lista completa para eliminar los no encontrados?", "Error",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                // @TODO: Desacoplar esto (analizar la lista completa de canciones para eliminar)
                Iterator<Song> songsIterator = currentSongs.iterator();

                int count = 0;
                Song nextSong;

                while (songsIterator.hasNext()) {
                    nextSong = songsIterator.next();

                    if (!nextSong.exists()) {
                        currentSongs.remove(nextSong);

                        count++;
                    }
                }

                JOptionPane.showMessageDialog(
                    this,
                    "Se han eliminado " + count + " canciones de la lista.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else if (!album.hasCoversArt()) { // si el Album NO tiene una lista de imagenes
            List<ImageIcon> coversArt = Resource.getCoversArt(song);

            if (!coversArt.isEmpty()) {
                /*
                si la lista de fotos no esta vacía por lo menos hay una
                para poder comenzar el hilo de las caratulas
                 */
                album.setCoversArt(coversArt);
            } else { // no hay imagenes en la carpeta de la canción
                coversArt = new ArrayList<>();

                try {
                    Image coverArt = LastFM.getCoverArt(song.getAuthor(), song.getAlbum());

                    coverArt = coverArt.getScaledInstance(
                        (int) Rule.COVER_ART_DIMENSION.getWidth(),
                        (int) Rule.COVER_ART_DIMENSION.getHeight(),
                        Image.SCALE_SMOOTH
                    );

                    coversArt.add(new ImageIcon(coverArt));
                } catch (Exception ex) {
                    /*Establezco la caratula por defecto (el disco)*/
//                        icono = icono.getScaledInstance(
//                                (int) Rule.COVER_DIMENSION.getWidth(),
//                                (int) Rule.COVER_DIMENSION.getHeight(),
//                                Image.SCALE_SMOOTH);
                    coversArt.add(new ImageIcon(iconApp));
                }

                album.setCoversArt(coversArt);
            }
        }

        if (coverArtThread != null) {
            coverArtThread.interrupt();
        }

        setCoverArt(album.getCoversArt().get(0).getImage());

        setBackgroundColor(album);

        coverArtThread = new CoverArtThread(coverArtLabel, album.getCoversArt());
        coverArtThread.start();

        setIconImage(album.getCoversArt().get(0).getImage());

        treeSong.setCellRenderer(new SongListTreeCellRenderer());
    }

    /**
     * Reproduce el siguiente, si es el último reproduce el primero
     */
    private void playNextSong() {
        int currentIndex = currentSongs.indexOf(musicPlayer.getCurrentSong());
        currentIndex++;

        if (currentIndex < currentSongs.size()) {
            play(currentSongs.get(currentIndex));
        } else {
            play(currentSongs.get(0));
        }
    }

    /**
     * Reproduce el anterior, si es el primero reproduce el último
     */
    private void playPreviousSong() {
        int currentIndex = currentSongs.indexOf(musicPlayer.getCurrentSong());
        currentIndex--;

        if (currentIndex >= 0) {
            play(currentSongs.get(currentIndex));
        } else {
            // reproduce el último
            play(currentSongs.get(currentSongs.size() - 1));
        }
    }

    private void playRandomSong() {
        play(currentSongs.get(getRandom()));
    }

    @Override
    public void search(String searchText) {
        searchedSongs = new ArrayList<>();

        int i = 1;
        for (Song song : musicLibrary.getSongs()) {
            if (song.getAuthor().toLowerCase().contains(searchText)
                    || song.getAlbum().toLowerCase().contains(searchText)
                    || song.getName().toLowerCase().contains(searchText)) {
                searchedSongs.add(song);
            }
        }

        loadSongsInMusicLibrary(searchedSongs);
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
                mainTabbedPane.setSelectedIndex(Rule.TabIndex.MUSIC_LIBRARY);
                if (searchDialog == null) {
                    searchDialog = new SearchDialog(JPlay.this, false);
                    searchDialog.setSearchable(JPlay.this);
                    searchDialog.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), searchDialog.getHeight());
                    searchDialog.setVisible(true);
                } else {
                    searchDialog.setBounds(JPlay.this.getX(), JPlay.this.getY(), JPlay.this.getWidth(), searchDialog.getHeight());
                    searchDialog.setVisible(!searchDialog.isVisible());
                }
            }
        });
        /*Código para escuchar a un boton para todos los componentes*/
        // </editor-fold>
    }

    // este método se llama cuando apreta enter en buscar
    @Override
    public void focusOn(String searchText) {
        try {
            // @TODO: Desacoplar código de comandos
            if (searchText.startsWith("/")) {
                // si es un comando, despues cargo de nuevo la biblioteca
                loadSongsInMusicLibrary(musicLibrary.getSongs());

                String pathString = "";

                if (searchText.equalsIgnoreCase("/rutas")) {
                    pathString += "RUTAS:\n";

                    for (File file : musicLibrary.getPaths()) {
                        pathString += file.getPath() + "\n";
                    }

                    JOptionPane.showMessageDialog(this, pathString);
                } else if (searchText.equalsIgnoreCase("/scan")) {
                    ScanThread scanThread = new ScanThread(musicLibrary, this);
                    scanThread.start();
                } else if (searchText.equalsIgnoreCase("/favs")) {
                    // @TODO WTF
                }
            }

            musicLibraryTable.setRowSelectionInterval(0, 0);
        } catch (HeadlessException e) {
            // cae aca cuando no hay canciones en la tabla biblioteca
        }
    }

    @Override
    public void loadSearchComboBox(JComboBox searchCombobox) {
        searchCombobox.removeAllItems();
        searchCombobox.addItem("");

        for (String artistName : musicLibrary.getArtistNames()) {
            searchCombobox.addItem(artistName);
        }

        for (Album album : musicLibrary.getAlbums()) {
            if (!album.getName().trim().equals("")) {
                searchCombobox.addItem(album.getName());
            }
        }
    }

    private void initCoverArtPopUp() {
        covertArtPopUp = new JPopupMenu();

        JMenuItem deleteCoverArt = new JMenuItem("Eliminar Cover");

        deleteCoverArt.addActionListener(actionEvent -> {
            try {
                if (coverArtThread.isAlive()) {
                    Image currentCoverArt = coverArtThread.getCurrentCoverArt();

                    coverArtThread.interrupt();

                    Album album = musicLibrary.getAlbum(musicPlayer.getCurrentSong());

                    album.removeCoverArt(new ImageIcon(currentCoverArt));

                    coverArtThread = new CoverArtThread(coverArtLabel, album.getCoversArt());
                    coverArtThread.start();
                }
            } catch (NullPointerException ex) {}
        });

        covertArtPopUp.add(deleteCoverArt);

        coverArtLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopUpCover(e);
            }
        });
    }

    private void showPopUpCover(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            covertArtPopUp.show(mouseEvent.getComponent(), mouseEvent.getX() + 8, mouseEvent.getY() + 8);
        }
    }

    private void initDragDropTabbedPane() {
        mainTabbedPane.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dropTargetDropEvent) {

                try {
                    dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object transferData = dropTargetDropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    final List<File> files = (List) transferData;

                    loadThread = new Thread(() -> {
                        for (File file : files) {
                            try {
                                loadSongsInMusicLibrary(file);
                                loadSongsInMusicLibrary(musicLibrary.getSongs());

                                musicLibrary.addSongsToAlbums();
                                musicLibrary.addPath(file);
                            } catch (IOException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        System.out.println("HILO CARGAR DROP TERMINADO!");
                    });

                    loadThread.start();
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
                currentTabIndex = mainTabbedPane.getSelectedIndex();
                mainTabbedPane.setSelectedIndex(Rule.TabIndex.MUSIC_LIBRARY);
            }

            @Override
            public void dragOver(DropTargetDragEvent dropTargetDragEvent) {}

            @Override
            public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {}

            @Override
            public void dragExit(DropTargetEvent dropTargetEvent) {
                mainTabbedPane.setSelectedIndex(currentTabIndex);
            }
        }));
    }

    /*Este método sirve para actualizar la tabla de biblioteca despues del scan (Clase Scan)*/
    @Override
    public void updateMusicLibraryUI(boolean haschanged) {
        if (haschanged) {
            loadSongsInMusicLibrary(musicLibrary.getSongs());
            musicLibrary.addSongsToAlbums();
        }
    }



    private void initTabIcons() {
        mainTabbedPane.setIconAt(Rule.TabIndex.EXPLORER, SongListTreeCellRenderer.getImageIcon(Path.EXPLORER_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.MUSIC_LIBRARY, SongListTreeCellRenderer.getImageIcon(Path.MUSIC_LIBRARY_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.CURRENT_SONGS_LIST, SongListTreeCellRenderer.getImageIcon(Path.LIST_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.MOST_PLAYED, SongListTreeCellRenderer.getImageIcon(Path.MOST_PLAYED_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.LOGGER, SongListTreeCellRenderer.getImageIcon(Path.LOG_TAB_ICON));
        //@TODO: WTF, mejorar
        mainTabbedPane.setIconAt(Rule.TabIndex.FAVORITES, new ImageIcon(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    private void setBackgroundColor(Album album) {
        /* Colores de fondo */
        if (album.hasCoversArt()) {
            Rule.BACKGROUND_COLOR = ImageProcessor.getAverageColor(album.getCoverArt());
        } else {
            Rule.BACKGROUND_COLOR = Color.white;
        }



        Rule.FOREGROUND_COLOR = ImageProcessor.getForeGroundColorBasedOnBGBrightness(Rule.BACKGROUND_COLOR);

        panelPrincipal.setBackground(Rule.BACKGROUND_COLOR);
        volumeSlider.setBackground(Rule.BACKGROUND_COLOR);
        nameSongLabel.setForeground(Rule.FOREGROUND_COLOR);
        artistLabel.setForeground(Rule.FOREGROUND_COLOR);
        repeatSongCheckbox.setForeground(Rule.FOREGROUND_COLOR);
        randomCheckbox.setForeground(Rule.FOREGROUND_COLOR);
        loadInfoLabel.setForeground(Rule.FOREGROUND_COLOR);
        durationLabel.setForeground(Rule.FOREGROUND_COLOR);

        progressBarSong.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Rule.BACKGROUND_COLOR;
            }

            @Override
            protected Color getSelectionForeground() {
                return Rule.FOREGROUND_COLOR;
            }
        });

        // @TODO: Color en colores
        progressBarSong.setForeground(new Color(76, 175, 80));

        Color color = Rule.BACKGROUND_COLOR.darker().darker();
        mainTabbedPane.setBackground(color);
        mainTabbedPane.setForeground(ImageProcessor.getForeGroundColorBasedOnBGBrightness(color));

        // Acá cambio los iconos según color
        if (Rule.FOREGROUND_COLOR == Color.white) {
            playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));
            nextSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_NEXT_ICON)));
            backSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_BACK_ICON)));
        } else {
            playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
            nextSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_NEXT_ICON)));
            backSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_BACK_ICON)));
        }
    }

    // @TODO: No se como arreglar estos métodos (setCoverArt) pero me suena a que se puede hacer mejor
    private void setCoverArt(Icon cover) {
        BufferedImage bufferedImage = new BufferedImage(
            cover.getIconWidth(),
            cover.getIconHeight(),
            BufferedImage.TYPE_INT_RGB
        );

        cover.paintIcon(null, bufferedImage.getGraphics(), 0, 0);

        coverArtLabel.setIcon(new ImageIcon(
            bufferedImage.getScaledInstance(
                (int) Rule.COVER_ART_DIMENSION.getWidth(),
                (int) Rule.COVER_ART_DIMENSION.getHeight(),
                Image.SCALE_SMOOTH
            )
        ));
    }

    private void setCoverArt(Image cover) {
        coverArtLabel.setIcon(new ImageIcon(
            cover.getScaledInstance(
                (int) Rule.COVER_ART_DIMENSION.getWidth(),
                (int) Rule.COVER_ART_DIMENSION.getHeight(),
                Image.SCALE_SMOOTH
            )
        ));
    }

    private void initArtistCoversArt() {
        if (artistCoversArt == null) {
            artistCoversArt = new ArrayList<>();

            musicLibrary.getArtistNames().forEach((artist) -> {
                try {
                    artistCoversArt.add(new ArtistCoverArt(artist));
                } catch (Exception ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            for (String artistName : musicLibrary.getArtistNames()) {
                if (!artistExist(artistName)) {
                    try {
                        artistCoversArt.add(new ArtistCoverArt(artistName));
                    } catch (Exception ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /*Ordena descendente los artistas*/
        Collections.sort(artistCoversArt, (artistCoverArt, anotherArtistCoverArt) ->
            artistCoverArt.getArtistName().compareTo(anotherArtistCoverArt.getArtistName())
        );

        try {
            artistList.setCellRenderer(new ArtistListCellRenderer(artistCoversArt));
            artistList.setModel(new ArtistListModel(artistCoversArt));
            artistList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            artistList.setVisibleRowCount(-1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean artistExist(String artistName) {
        boolean anyMatch = artistCoversArt.stream().anyMatch(
            (artistCoverArt) -> (artistCoverArt.getArtistName().equals(artistName))
        );

        System.out.println("ARTISTA [" + artistName + "] --> " + anyMatch);

        return anyMatch;
    }

}
