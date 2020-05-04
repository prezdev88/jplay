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
import cl.prezdev.xjplay.table.model.MusicLibrarySongTableModel;
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
import java.awt.event.InputEvent;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
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
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class JPlay extends JFrame implements
        BasicPlayerListener, SearchListener, MusicLibraryUiUpdate {

    private MusicPlayer musicPlayer;
    private MusicLibrary musicLibrary;
    
    // son las canciones de la lista de reproducción actual
    private List<Song> songs; 
    private Thread songsLoadThread;
    private JPopupMenu explorerTreePopUp;
    private JPopupMenu musicLibraryPopUp;
    private JPopupMenu coverArtPopUp;

    // Esto es sólo para debug
    private final boolean SAVE = true; 
    private List<Song> songsFound;
    private List<ArtistCoverArt> artistCoverArts;
    private boolean isPlay;
    private boolean isStop;
    private boolean isRandom;
    private boolean repeatSong;
    private Image appIcon;
    
    // hilo para animación de caratulas
    private CoverArtThread coverArtThread; 

    // GUARDA EL TOTAL DE DURACIÓN DE LA CANCION EN MILIS
    private int totalBytes; 

    // para pintar los minutos en la barra
    private WorkerStringProgress workerStringProgress; 
    private boolean printProgressBarSong;
    private SearchDialog searchDialog;
    
    // esto es para el drag and drop
    private int currentTabIndex; 

    /**
     * Esta lista la utilizo cuando guardo en SAVE. Ya que si solo guardo las
     * canciones, el icono no se carga cuando por ejemplo, estoy escuchando las
     * canciones favoritas.
     */
    private List<Album> albums;

    // Son los milisegundos totales de la lista de reproducción actual.
    private int millisecondsOfCurrentSongs;  

    public JPlay() {
        initComponents();
        initMusicPlayer();

        Rule.COVER_ART_DIMENTION = new Dimension(
            coverArtLabel.getWidth(), 
            coverArtLabel.getHeight()
        );

        appIcon = Resource.JPLAY_ICON;

        appIcon = appIcon.getScaledInstance(
            (int) Rule.COVER_ART_DIMENTION.getWidth(),
            (int) Rule.COVER_ART_DIMENTION.getHeight(),
            Image.SCALE_SMOOTH
        );

        songs = new ArrayList<>();
        albums = new ArrayList<>();
        musicLibrary = MusicLibrary.getInstance();

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
        songsTableScrollPane.setVisible(false);
        cleanLabel.setVisible(false);
        /*Se hace invisible la tabla antigua de temas y el boton limpiar*/

        setIconImage(appIcon);

        coverArtLabel.setText(null);

        setBounds(0, 0, 800, 600);
        setLocationRelativeTo(null);
        coverArtThread = null;

        coverArtLabel.requestFocus();
        initMostPlayerSongsTree();
        loadFavoritesSongsTree();
        
        musicLibrary.printAlbumsToLog();
        printProgressBarSong = true;

        initBuscar();
        initDragDropTabbedPane();
        initIconosTabs();
        initArtistCoversArt();
        initIcons();

        mainPanel.setBackground(Color.white);

        setBounds(0, 0, Rule.WIDTH, Rule.HEIGHT);
        setLocationRelativeTo(null);
        //@TODO: Intentar ordenar este constructor, pensar bien la forma de agrupar
        
        
    }

    private void initIcons() {
        backSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_BACK_ICON)));
        nextSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_NEXT_ICON)));
        playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));
        favoriteButton.setIcon(new ImageIcon(getClass().getResource(Path.FAVORITES_TAB_ICON)));
    }
    
    // http://stackoverflow.com/questions/13516730/disable-enter-key-from-moving-down-a-row-in-jtable
    // este método es porque cuando apretaba enter en la tabla de canciones, se veia feo el que
    // el cursor bajara y despues subiera. Este método sobre escribe eso hecho por java automáticamente
    private void setNoActionEnter(JTable table) {
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        
        final int INPUT_MAP = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
        
        table.getInputMap(INPUT_MAP).put(enterKeyStroke, "Enter");
        
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {}
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        panelApp = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        coverArtLabel = new javax.swing.JLabel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        explorerTree = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        musicLibraryTable = new javax.swing.JTable();
        panelListaActual = new javax.swing.JPanel();
        songsTableScrollPane = new javax.swing.JScrollPane();
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        artistList = new javax.swing.JList();
        progressBarSong = new javax.swing.JProgressBar();
        loadInfoLabel = new javax.swing.JLabel();
        cancelLoadingButton = new javax.swing.JButton();
        songNameLabel = new javax.swing.JLabel();
        artistLabel = new javax.swing.JLabel();
        volumeSlider = new javax.swing.JSlider();
        favoriteButton = new javax.swing.JToggleButton();
        repeatSongCheckbox = new javax.swing.JCheckBox();
        shuffleCheckbox = new javax.swing.JCheckBox();
        songDurationLabel = new javax.swing.JLabel();
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
        mainTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainTabbedPaneMouseReleased(evt);
            }
        });

        explorerTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                explorerTreeMouseReleased(evt);
            }
        });
        explorerTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                explorerTreeValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(explorerTree);

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
                songsTableMouseReleased(evt);
            }
        });
        songsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                songsTableKeyReleased(evt);
            }
        });
        songsTableScrollPane.setViewportView(songsTable);

        cleanLabel.setBackground(new java.awt.Color(63, 81, 181));
        cleanLabel.setForeground(new java.awt.Color(254, 254, 254));
        cleanLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cleanLabel.setText("Limpiar");
        cleanLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cleanLabel.setOpaque(true);
        cleanLabel.addMouseListener(new java.awt.event.MouseAdapter() {
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
            .addComponent(cleanLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(songsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelListaActualLayout.setVerticalGroup(
            panelListaActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelListaActualLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(songsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cleanLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainTabbedPane.addTab("Lista actual", panelListaActual);

        panelMasEscuchadas.setLayout(new java.awt.BorderLayout());

        mostPlayedSongTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mostPlayedSongTreeMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(mostPlayedSongTree);

        panelMasEscuchadas.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("+ escuchadas", panelMasEscuchadas);

        panelFavoritos.setLayout(new java.awt.BorderLayout());

        favoritesTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                favoritesTreeMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(favoritesTree);

        panelFavoritos.add(jScrollPane7, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Favoritos", panelFavoritos);

        jPanel1.setLayout(new java.awt.BorderLayout());

        artistList.addMouseListener(new java.awt.event.MouseAdapter() {
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
                progressBarSongMouseDragged(evt);
            }
        });
        progressBarSong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                progressBarSongMouseReleased(evt);
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

        songNameLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        songNameLabel.setText("Artista / Canción");

        artistLabel.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        artistLabel.setText("Artista / Canción");

        volumeSlider.setBackground(new java.awt.Color(255, 255, 255));
        volumeSlider.setMaximum(40);
        volumeSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                volumeSliderMouseDragged(evt);
            }
        });

        favoriteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favoriteButtonActionPerformed(evt);
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

        shuffleCheckbox.setBackground(new java.awt.Color(254, 254, 254));
        shuffleCheckbox.setText("Shuffle");
        shuffleCheckbox.setOpaque(false);
        shuffleCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shuffleCheckboxActionPerformed(evt);
            }
        });

        songDurationLabel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        songDurationLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        songDurationLabel.setText("0:00 - 0:00");

        backSongLabel.setText("A");
        backSongLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backSongLabelMouseReleased(evt);
            }
        });

        playSongLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                playSongLabelMouseReleased(evt);
            }
        });

        nextSongLabel.setText("A");
        nextSongLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                nextSongLabelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(backSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextSongLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coverArtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(artistLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(songDurationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(progressBarSong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(songNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(5, 5, 5))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cancelLoadingButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shuffleCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(repeatSongCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volumeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(favoriteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(songNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(artistLabel)
                            .addComponent(songDurationLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBarSong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(playSongLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(nextSongLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backSongLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(coverArtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(volumeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelLoadingButton)
                            .addComponent(loadInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(shuffleCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(repeatSongCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(favoriteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelAppLayout = new javax.swing.GroupLayout(panelApp);
        panelApp.setLayout(panelAppLayout);
        panelAppLayout.setHorizontalGroup(
            panelAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelAppLayout.setVerticalGroup(
            panelAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(panelApp, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // @TODO: Desacoplar los listeners de volumeSlides
    private void volumeSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_volumeSliderMouseDragged
        setVolume(volumeSlider.getValue());
    }//GEN-LAST:event_volumeSliderMouseDragged

   // @TODO: Desacoplar los listeners de explorerTree
    private void explorerTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_explorerTreeMouseReleased
        if (evt.getClickCount() == 2) {
            loadSong();
        }
    }//GEN-LAST:event_explorerTreeMouseReleased

    private void explorerTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_explorerTreeValueChanged
        // acá cargo los subdirectorios cuando hago click
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();

        if (treeNode != null) {
            if (treeNode.getChildCount() == 0) {
                /*Si no tengo hijos proceso*/
                Object userObject = treeNode.getUserObject();

                if (userObject instanceof File) {
                    File file = (File) userObject;
                    if (file.isDirectory()) {
                        loadFilesInTreeNode(treeNode, file);
                    }
                }
            }
        }
    }//GEN-LAST:event_explorerTreeValueChanged

    private void cancelLoadingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelLoadingButtonActionPerformed
        // @TODO: intentar llamar a interrupt
        songsLoadThread.stop();
        cancelLoadingButton.setEnabled(false);
        loadAlbumsInTreeSong(getAlbums(songs));
    }//GEN-LAST:event_cancelLoadingButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (SAVE) {
            try {
                Save save = new Save();

                save.songs = songs;
                save.indexTab = mainTabbedPane.getSelectedIndex();
                save.cover = coverArtLabel.getIcon();
                save.albums = albums;
                save.volume = volumeSlider.getValue();
                save.artistCoversArt = artistCoverArts;

                IO.writeObject(save, Path.SAVE);
                IO.writeObject(musicLibrary, Path.MUSIC_LIBRARY);
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
    private void musicLibraryTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_musicLibraryTableMouseReleased
        if (evt.getClickCount() == 2) {
            playSelectedMusicLibrarySong();
        }
    }//GEN-LAST:event_musicLibraryTableMouseReleased

    private void musicLibraryTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_musicLibraryTableMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            musicLibraryPopUp.show(musicLibraryTable, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_musicLibraryTableMousePressed

    private void repeatSongCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repeatSongCheckboxActionPerformed
        repeatSong = repeatSongCheckbox.isSelected();
    }//GEN-LAST:event_repeatSongCheckboxActionPerformed

    private void shuffleCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shuffleCheckboxActionPerformed
        isRandom = shuffleCheckbox.isSelected();
    }//GEN-LAST:event_shuffleCheckboxActionPerformed

    private void musicLibraryTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_musicLibraryTableKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            playSelectedMusicLibrarySong(); 
        }
    }//GEN-LAST:event_musicLibraryTableKeyReleased

    private void treeSongMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeSongMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeSong.getLastSelectedPathComponent();
            if (treeNode != null) {
                Object userObject = treeNode.getUserObject();
                if (userObject instanceof Song) {
                    Song song = (Song) userObject;

                    play(song);
                }
            }
        }
    }//GEN-LAST:event_treeSongMouseReleased

    private void progressBarSongMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressBarSongMouseReleased
        changeSongProgressBarValue((evt.getX() * 100) / progressBarSong.getWidth(), true);
        setVolume(volumeSlider.getValue());
        printProgressBarSong = true;
    }//GEN-LAST:event_progressBarSongMouseReleased

    private void progressBarSongMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressBarSongMouseDragged
        changeSongProgressBarValue((evt.getX() * 100) / progressBarSong.getWidth(), false);
        printProgressBarSong = false;
    }//GEN-LAST:event_progressBarSongMouseDragged

    private void cleanLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cleanLabelMouseReleased
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(63, 81, 181));
        songs = new ArrayList<>();
        loadAlbumsInTreeSong(getAlbums(songs));
    }//GEN-LAST:event_cleanLabelMouseReleased

    private void cleanLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cleanLabelMousePressed
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(26, 35, 126));
    }//GEN-LAST:event_cleanLabelMousePressed

    private void cleanLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cleanLabelMouseExited
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(63, 81, 181));
    }//GEN-LAST:event_cleanLabelMouseExited

    private void cleanLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cleanLabelMouseEntered
        // @TODO: Colocar color donde estan todos los colores
        cleanLabel.setBackground(new Color(92, 107, 192));
    }//GEN-LAST:event_cleanLabelMouseEntered

    private void songsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_songsTableKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            SongTableModel songTableModel = (SongTableModel) songsTable.getModel();
            int index = songsTable.getSelectedRow();
            // @TODO: index = 0 ?
            Song song = (Song) songTableModel.getValueAt(index, 0);

            play(song);
        }
    }//GEN-LAST:event_songsTableKeyReleased
    
    // @TODO: eliminar
    private void songsTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_songsTableMouseReleased
        if (evt.getClickCount() == 2) {
            SongTableModel songTableModel = (SongTableModel) songsTable.getModel();
        }
    }//GEN-LAST:event_songsTableMouseReleased

    private void mostPlayedSongTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mostPlayedSongTreeMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) mostPlayedSongTree.getLastSelectedPathComponent();
            if (treeNode != null) {
                Object userObject = treeNode.getUserObject();
                
                if (userObject instanceof Song) {
                    // @TODO: Una vez que lo de abajo pase a clase Album, cambiar nonbre a song
                    Song song = (Song) userObject;

                    songs = musicLibrary.getMostPlayedSongs();

                    /*Ahora debo poner las canciones en un album*/
                    // @TODO: Crear constructor sin año en Album, y pasándole una lista de canciones
                    Album album = new Album(Rule.NAME, "Canciones más escuchadas!", null);

                    // @TODO: Esto irá en el constructor de album
                    songs.stream().forEach((can) -> {
                        album.addSong(can);
                    });

                    List<ImageIcon> coversArt = new ArrayList<>();
                    coversArt.add(SongListTreeCellRenderer.getImageIcon(Path.HEART_ICON));
                    album.setCoversArt(coversArt);

                    List<Album> treeSongsAlbums = new ArrayList<>();
                    treeSongsAlbums.add(album);
                    /*Ahora debo poner las canciones en un album*/

                    loadAlbumsInTreeSong(treeSongsAlbums);

                    play(song);

                    mainTabbedPane.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
                }
            }
        }
    }//GEN-LAST:event_mostPlayedSongTreeMouseReleased

    private void mainTabbedPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTabbedPaneMouseReleased
        if (evt.getClickCount() == 2) {
            if (mainTabbedPane.getSelectedIndex() == 1) {
                loadSongsInMusicLibrary(musicLibrary.getSongs());
            }
        }

        switch (mainTabbedPane.getSelectedIndex()) {
            case Rule.TabIndex.MUSIC_LIBRARY:
                loadInfoLabel.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.TabIndex.EXPLORER:
                loadInfoLabel.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.TabIndex.FAVORITES:
                loadInfoLabel.setText(musicLibrary.getFavoritesDuration());
                break;
            case Rule.TabIndex.CURRENT_SONGS_LIST:
                loadInfoLabel.setText("Lista actual --> " + Util.getDurationAsString(millisecondsOfCurrentSongs));
                break;
            case Rule.TabIndex.LOGGER:
                loadInfoLabel.setText(musicLibrary.getLibraryDuration());
                break;
            case Rule.TabIndex.MOST_PLAYED:
                loadInfoLabel.setText(musicLibrary.getMostPlayedDuration());
                break;
        }
    }//GEN-LAST:event_mainTabbedPaneMouseReleased

    private void favoriteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favoriteButtonActionPerformed
        if (musicPlayer.hasCurrentSong()) {
            if (favoriteButton.isSelected()) {
                musicLibrary.addFavoriteSong(musicPlayer.getCurrentSong());
            } else {
                musicLibrary.removeFavoriteSong(musicPlayer.getCurrentSong());
            }
            
            loadFavoritesSongsTree();
        }
    }//GEN-LAST:event_favoriteButtonActionPerformed

    private void favoritesTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_favoritesTreeMouseReleased
        // @TODO: ver mostPlayedSongTreeMouseReleased
        if (evt.getClickCount() == 2) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) favoritesTree.getLastSelectedPathComponent();
            if (treeNode != null) {
                Object userObject = treeNode.getUserObject();
                
                // @TODO: Una vez que lo de abajo pase a clase Album, cambiar nonbre a song
                if (userObject instanceof Song) {
                    Song song = (Song) userObject;

                    songs = musicLibrary.getFavoritesSongs();

                    /*Ahora debo poner las canciones en un album*/
                    Album album = new Album(Rule.NAME, "Favoritas!", null);

                    // @TODO: Esto irá en el constructor de album
                    songs.stream().forEach((can) -> {
                        album.addSong(can);
                    });

                    // @TODO: esto esta en el método mostPlayedSongTreeMouseReleased()
                    List<ImageIcon> coversArt = new ArrayList<>();
                    coversArt.add(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON));
                    album.setCoversArt(coversArt);

                    List<Album> albums = new ArrayList<>();
                    albums.add(album);
                    /*Ahora debo poner las canciones en un album*/

                    loadAlbumsInTreeSong(albums);

                    play(song);

                    mainTabbedPane.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
                }
            }
        }
    }//GEN-LAST:event_favoritesTreeMouseReleased

    private void playSongLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playSongLabelMouseReleased
        try {
            if (isPlay) {
                isPlay = false;

                if (Rule.FOREGROUND_COLOR == Color.black) {
                    playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PLAY_ICON)));// @TODO: Arreglar esto de los íconos
                } else {
                    playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PLAY_ICON)));// @TODO: Arreglar esto de los íconos
                }

                musicPlayer.pause();
            } else {
                isPlay = true;

                if (Rule.FOREGROUND_COLOR == Color.black) {
                    playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.BLACK_PAUSE_ICON)));// @TODO: Arreglar esto de los íconos
                } else {
                    playSongLabel.setIcon(new ImageIcon(getClass().getResource(Path.WHITE_PAUSE_ICON)));// @TODO: Arreglar esto de los íconos
                }

                if (isStop) {
                    playCurrentSong();
                    isPlay = true;
                    isStop = false;
                } else {
                    musicPlayer.resume();
                }
            }
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_playSongLabelMouseReleased

    private void backSongLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backSongLabelMouseReleased
        if (!songs.isEmpty()) {
            // @TODO: Idea: Métodos play en model (jplay) y llamar a listeners (xjplay)
            if (isRandom) {
                playRandomSong();
            } else if (repeatSong) {
                playCurrentSong();
            } else {
                playPreviousSong();
            }
        }
    }//GEN-LAST:event_backSongLabelMouseReleased

    private void nextSongLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextSongLabelMouseReleased
        if (!songs.isEmpty()) {
            if (isRandom) {
                playRandomSong();
            } else if (repeatSong) {
                playCurrentSong();
            } else {
                playNextSong();
            }
        }
    }//GEN-LAST:event_nextSongLabelMouseReleased

    private void artistListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_artistListMouseReleased
        if (evt.getClickCount() == 2) {
            Object selectedValue = artistList.getSelectedValue();
            
            ListCellRenderer listCellRenderer = null;
            ListModel listModel = null;

            if (selectedValue instanceof ArtistCoverArt) {
                ArtistCoverArt artistCoverArt = (ArtistCoverArt) selectedValue;
                String artistName = artistCoverArt.getArtistName();
                List<Album> albumsByArtist = musicLibrary.getAlbumsByArtist(artistName);
                albumsByArtist.add(0, new BackAlbum());

                listCellRenderer = new AlbumListCellRenderer(albumsByArtist);
                listModel = new AlbumListModel(albumsByArtist);
            } else if (selectedValue instanceof BackAlbum) {
                // quiere ir atrás, o sea a los artistas
                listCellRenderer = new ArtistListCellRenderer(artistCoverArts);
                listModel = new ArtistListModel(artistCoverArts);
            }
            
            artistList.setCellRenderer(listCellRenderer);
            artistList.setModel(listModel);
        }
    }//GEN-LAST:event_artistListMouseReleased

    private void loadSave() {
        if (new File(Path.SAVE).exists()) {
            try {
                Save save = (Save) IO.readObject(Path.SAVE);

                songs = save.songs;
                artistCoverArts = save.artistCoversArt;

                /*Recuperando el volumen del usuario*/
                volumeSlider.setValue(save.volume);
                setVolume(volumeSlider.getValue());

                /*Recuperando el volumen del usuario*/
                mainTabbedPane.setSelectedIndex(save.indexTab);

                setCoverArt(save.cover);

                musicLibrary = (MusicLibrary) IO.readObject(Path.MUSIC_LIBRARY);

                loadAlbumsInTreeSong(save.albums);
                loadSongsInMusicLibrary(musicLibrary.getSongs());
                showCurrentSongInfo();

            } catch (InvalidClassException ex) {
                musicLibrary = MusicLibrary.getInstance();
                songs = musicLibrary.getSongs();
                artistCoverArts = new ArrayList<>();
                loadDefault();
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            loadDefault();
        }

        switch (mainTabbedPane.getSelectedIndex()) {
            case Rule.TabIndex.MUSIC_LIBRARY:
            case Rule.TabIndex.LOGGER:
            case Rule.TabIndex.EXPLORER:
                loadInfoLabel.setText(musicLibrary.getLibraryDuration());
                break;
                
            case Rule.TabIndex.FAVORITES:
                loadInfoLabel.setText(musicLibrary.getFavoritesDuration());
                break;
                
            case Rule.TabIndex.CURRENT_SONGS_LIST:
                loadInfoLabel.setText("Lista actual --> " + Util.getDurationAsString(millisecondsOfCurrentSongs));
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
                            /*
                            Esto es sólo para que se vea el nombre, por ende
                            tuve que sobre escribir el método toString
                             */
                            namedFile = new File(file.getPath()) {

                                @Override
                                public String toString() {
                                    return this.getName();
                                }

                            };
                            /*
                            Esto es sólo para que se vea el nombre, por ende
                            tuve que sobre escribir el método toString
                             */

                            files.add(namedFile);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            // ordenar acá
            Collections.sort(files, (File file, File anotherFile) -> file.compareTo(anotherFile));

            files.stream().forEach((file) -> {
                rootTreeNode.add(new DefaultMutableTreeNode(file));
            });
        }
    }

    /**
     * Método para cargar canciones cuando el usuario las quiera escoger desde
     * el arbol con el click secundario
     *
     * @param rootFile
     */
    private void loadSongsInMusicLibrary(File rootFile) throws IOException, InterruptedException {
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
                    songs.add(song);
                }
            }
        }
    }

    public static void main(String args[]) {
        invokeLater(() -> new JPlay().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel artistLabel;
    private javax.swing.JList artistList;
    private javax.swing.JLabel backSongLabel;
    private javax.swing.JButton cancelLoadingButton;
    private javax.swing.JLabel cleanLabel;
    private javax.swing.JLabel coverArtLabel;
    private javax.swing.JTree explorerTree;
    private javax.swing.JToggleButton favoriteButton;
    private javax.swing.JTree favoritesTree;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel loadInfoLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JTree mostPlayedSongTree;
    private javax.swing.JTable musicLibraryTable;
    private javax.swing.JLabel nextSongLabel;
    private javax.swing.JPanel panelApp;
    private javax.swing.JPanel panelFavoritos;
    private javax.swing.JPanel panelListaActual;
    private javax.swing.JPanel panelMasEscuchadas;
    private javax.swing.JLabel playSongLabel;
    private javax.swing.JProgressBar progressBarSong;
    private javax.swing.JCheckBox repeatSongCheckbox;
    private javax.swing.JCheckBox shuffleCheckbox;
    private javax.swing.JLabel songDurationLabel;
    private javax.swing.JLabel songNameLabel;
    private javax.swing.JTable songsTable;
    private javax.swing.JScrollPane songsTableScrollPane;
    private javax.swing.JTree treeSong;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

    private void setProgressBarSongMaxValue(int totalBytes) {
        this.totalBytes = totalBytes;
        progressBarSong.setMaximum(totalBytes);
    }

    private void setProgressBarSongValue(int readedBytes) {
        if (printProgressBarSong) {
            progressBarSong.setValue(readedBytes);
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
        setProgressBarSongValue(bytesread);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent basicPlayerEvent) {
        switch (basicPlayerEvent.getCode()) {
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
                
            case BasicPlayerEvent.STOPPED:
                break;

            case BasicPlayerEvent.RESUMED:
                workerStringProgress.resume();
                break;
                
            case BasicPlayerEvent.PAUSED:
                workerStringProgress.pause();
                break;
                
            case BasicPlayerEvent.SEEKED:
                break;
                
            case BasicPlayerEvent.OPENED:
                setProgressBarSongMaxValue((int) musicPlayer.getCurrentSong().length());
                break;
        }
    }

    @Override
    public void setController(BasicController bc) {}

    private void setVolume(int vol) {
        try {
            musicPlayer.setVolume(vol);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initExplorerTree() {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("raiz");
        File[] albumsFiles = File.listRoots();

        for (File albumFile : albumsFiles) {
            treeNode.add(new DefaultMutableTreeNode(albumFile));
        }

        explorerTree.setModel(new DefaultTreeModel(treeNode));
        explorerTree.setRootVisible(false);

        // @TODO: WTF!
        explorerTree.setCellRenderer(
            new ExplorerTreeCellRenderer(
                new ImageIcon(
                    ExplorerTreeCellRenderer.getImageIcon(
                        Path.MUSIC_ICON).getImage().
                        getScaledInstance(
                            Rule.ICON_EXPLORER_MUSIC_SIZE,
                            Rule.ICON_EXPLORER_MUSIC_SIZE,
                            Image.SCALE_SMOOTH
                        )
                ),
                new ImageIcon(
                    ExplorerTreeCellRenderer.getImageIcon(
                        Path.FOLDER_ICON).getImage().
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

        Collections.sort(songs, (File file, File anotherFile) -> file.compareTo(anotherFile));

        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("raiz");

        if (albums != null) {
            DefaultMutableTreeNode albumTreeNode;
            for (Album album : albums) {
                albumTreeNode = new DefaultMutableTreeNode(album);

                for (Song song : album.getSongs()) {
                    albumTreeNode.add(new DefaultMutableTreeNode(song));
                }

                treeNode.add(albumTreeNode);
            }
        }

        treeSong.setModel(new DefaultTreeModel(treeNode));
        treeSong.setRootVisible(false);
        treeSong.expandRow(0);
        treeSong.setCellRenderer(new SongListTreeCellRenderer());

        cancelLoadingButton.setEnabled(false);
        millisecondsOfCurrentSongs = 0;
        
        // @TODO: desacoplar esto
        songs.stream().forEach((song) -> {
            millisecondsOfCurrentSongs += song.getMilliSeconds();
        });

        loadInfoLabel.setText("Lista actual --> " + Util.getDurationAsString(millisecondsOfCurrentSongs));
    }

    private void initMostPlayerSongsTree() {
        DefaultMutableTreeNode rootTreNode = new DefaultMutableTreeNode("raiz");

        List<Song> mostPlayedSongs = musicLibrary.getMostPlayedSongs();

        for (Song song : mostPlayedSongs) {
            DefaultMutableTreeNode albumTreeNode = new DefaultMutableTreeNode(song);

            rootTreNode.add(albumTreeNode);
        }

        mostPlayedSongTree.setModel(new DefaultTreeModel(rootTreNode));
        mostPlayedSongTree.setRootVisible(false);
        mostPlayedSongTree.setCellRenderer(new MostPlayedSongsTreeCellRenderer());

        mainTabbedPane.setTitleAt(Rule.TabIndex.MOST_PLAYED, "+ escuchadas (" + mostPlayedSongs.size() + ")");
    }

    // @TODO: ver initMostPlayerSongsTree
    
    private void loadFavoritesSongsTree() {
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("raiz");

        List<Song> favoritesSongs = musicLibrary.getFavoritesSongs();

        for (Song song : favoritesSongs) {
            DefaultMutableTreeNode albumTreeNode = new DefaultMutableTreeNode(song);

            rootTreeNode.add(albumTreeNode);
        }

        favoritesTree.setModel(new DefaultTreeModel(rootTreeNode));
        favoritesTree.setRootVisible(false);

        favoritesTree.setCellRenderer(new FavoritesTreeCellRenderer());

        mainTabbedPane.setTitleAt(Rule.TabIndex.FAVORITES, "Favoritos (" + favoritesSongs.size() + ")");
    }

    private void initExplorerTreePopUp() {
        /*Este codigo es para que cuando el usuario haga click secundario
         se seleccione la fila del arbol*/
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent)) {

                    // @TODO almacenar las x y la y
                    int selectedRow = explorerTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
                    TreePath treePath = explorerTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                    explorerTree.setSelectionPath(treePath);
                    
                    if (selectedRow > -1) {
                        explorerTree.setSelectionRow(selectedRow);
                        explorerTreePopUp.show(explorerTree, mouseEvent.getX() + 10, mouseEvent.getY() + 10);
                    }
                }
            }
        };
        
        explorerTree.addMouseListener(mouseListener);
        
        explorerTreePopUp = new JPopupMenu();
        
        JMenuItem toNewListMenuItem = new JMenuItem("A lista nueva");
        JMenuItem toAlreadyExistMenuItem = new JMenuItem("Añadir a existente");
        JMenuItem toMusicLibraryMenuItem = new JMenuItem("Añadir a biblioteca");

        explorerTreePopUp.add(toNewListMenuItem);
        explorerTreePopUp.add(toAlreadyExistMenuItem);
        explorerTreePopUp.add(new Separator());
        explorerTreePopUp.add(toMusicLibraryMenuItem);

        toNewListMenuItem.addActionListener((ActionEvent actionEvent) -> {
            final File f = getSelectedTreeFile();
            songs = new ArrayList<>();
            songsLoadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);
                try {
                    loadSongToSongList(f);
                    loadAlbumsInTreeSong(getAlbums(songs));
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            songsLoadThread.start();
        });

        toAlreadyExistMenuItem.addActionListener((ActionEvent actionEvent) -> {
            songsLoadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);
                try {
                    File selectedFile = getSelectedTreeFile();
                    
                    loadSongToSongList(selectedFile);
                    loadAlbumsInTreeSong(getAlbums(songs));
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            songsLoadThread.start();
        });

        toMusicLibraryMenuItem.addActionListener((ActionEvent actionEvent) -> {
            songsLoadThread = new Thread(() -> {
                cancelLoadingButton.setEnabled(true);
                try {
                    File selectedTreeFile = getSelectedTreeFile();

                    loadSongsInMusicLibrary(selectedTreeFile);
                    loadSongsInMusicLibrary(musicLibrary.getSongs());
                    
                    musicLibrary.addSongsToAlbums();
                    musicLibrary.addPath(selectedTreeFile);
                    
                    initArtistCoversArt();
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("HILO CARGAR BIBLIOTECA TERMINADO!");
            });

            songsLoadThread.start();
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

        playAlbumMenuItem.addActionListener((ActionEvent actionEvent) -> {
            int[] selectedRows = musicLibraryTable.getSelectedRows();
            List<Album> albums = new ArrayList<>();
            
            Song song;
            Album album;
            
            songs = new ArrayList<>();
            
            // @TODO: Analizar esto (no lo he hecho)
            // Me tinca que se puede mejorar (y se debe mejorar)
            for (int row : selectedRows) {
                song = (Song) musicLibraryTable.getValueAt(row, MusicLibrarySongTableModel.COMPLETE_OBJECT_INDEX);
                album = musicLibrary.getAlbum(song);
                
                if (!albums.contains(album)) {
                    albums.add(album);
                    for (Song can : album.getSongs()) {
                        songs.add(can);
                    }
                }
            }
            
            loadAlbumsInTreeSong(getAlbums(songs));
            play(songs.get(0));
            
            mainTabbedPane.setSelectedIndex(Rule.TabIndex.CURRENT_SONGS_LIST);
        });

        addAlbumMenuItem.addActionListener((ActionEvent actionEvent) -> {
            // @TODO: playAlbumMenuItem.addActionListener
            int[] selectedRows = musicLibraryTable.getSelectedRows();
            List<Album> albums = new ArrayList<>();
            
            Song song;
            Album album;
            
            for (int row : selectedRows) {
                song = (Song) musicLibraryTable.getValueAt(row, MusicLibrarySongTableModel.COMPLETE_OBJECT_INDEX);
                album = musicLibrary.getAlbum(song);
                
                if (!albums.contains(album)) {
                    albums.add(album);
                    for (Song can : album.getSongs()) {
                        songs.add(can);
                    }
                }
            }
            
            loadAlbumsInTreeSong(getAlbums(songs));
        });

        removeFromMusicLibraryMenuItem.addActionListener((ActionEvent actionEvent) -> {
            int[] selectedRows = musicLibraryTable.getSelectedRows();
            
            List<Song> songs = new ArrayList<>();
            
            for (int row : selectedRows) {
                songs.add(
                    (Song) musicLibraryTable.getValueAt(
                        row, 
                        MusicLibrarySongTableModel.COMPLETE_OBJECT_INDEX
                    )
                );
            }
            
            for (Song song : songs) {
                musicLibrary.removeSong(song);
            }
            
            loadSongsInMusicLibrary(musicLibrary.getSongs());
        });
    }

    private File getSelectedTreeFile() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();
        
        if (treeNode != null) {
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof File) {
                return new File(((File) userObject).getPath());
                /*
                Por una razon que desconozco, el objeto File guardaba una referencia a Jplay
                por ende, cuando lo serializaba, enviaba un error.
                */
            }
        }
        
        return null;
    }

    private void loadSongsInMusicLibrary(List<Song> songs) {
        Collections.sort(songs, (File file, File anotherFile) -> {
            return file.compareTo(anotherFile);
        });
        
        musicLibraryTable.setModel(new MusicLibrarySongTableModel(songs));
        mainTabbedPane.setTitleAt(Rule.TabIndex.MUSIC_LIBRARY, "Biblioteca (" + songs.size() + ")");
        
        TableColumnModel musicLibraryColumnModel = musicLibraryTable.getColumnModel();
        
        musicLibraryColumnModel.getColumn(0).setPreferredWidth(Rule.TRACK_NUMBER_COLUMN_SIZE);
        musicLibraryColumnModel.getColumn(1).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        musicLibraryColumnModel.getColumn(2).setPreferredWidth(Rule.ALBUM_COLUMN_SIZE);
        musicLibraryColumnModel.getColumn(3).setPreferredWidth(Rule.ARTIST_COLUMN_SIZE);
        
        cancelLoadingButton.setEnabled(false);
        
        musicLibrary.addSongsToAlbums();
    }

    /*
    * @TODO: Primera idea:
    *   Llamar a play del model (jplay) y desde ahí, con listeners, cambiar el gui (xjplay)
    * */
    private void play(Song song) {
        long startTime = System.nanoTime();
        favoriteButton.setSelected(musicLibrary.isFavoriteSong(song));

        try {
            new Thread(() -> {
                setCoverArt(song);
            }).start();
            
            musicPlayer.play(song);

            song.increasePlayCount();

            setTitle(
                Rule.NAME + " - "
                + Rule.VERSION + " ["
                + song.getAuthor() + " - "
                + song.getName() + " (" + song.getPlayCount() + ")]"
            );
            
            setVolume(volumeSlider.getValue());
            
            // @TODO: Arreglar esto del ícono
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
            this.workerStringProgress = new WorkerStringProgress(songDurationLabel, durationAsString);

            this.workerStringProgress.execute();

            showCurrentSongInfo();
        } catch (BasicPlayerException ex) {
            JOptionPane.showMessageDialog(
                this, 
                "Error al reproducir: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
        long endTime = System.nanoTime();

        long durationInNano = (endTime - startTime);
        long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);

        System.out.println("load song: "+durationInMillis+"ms");
    }

    private void playCurrentSong() {
        // @TODO: WTF TOTAL!
        play(songs.get(songs.indexOf(musicPlayer.getCurrentSong())));
    }

    private void showCurrentSongInfo() {
        if (musicPlayer.hasCurrentSong()) {
            Song currentSong = musicPlayer.getCurrentSong();

            artistLabel.setText(
                currentSong.getAuthor() + " - "
                + currentSong.getAlbum() + " ("
                + currentSong.getYear() + ")"
            );

            String durationAsString = Util.getDurationAsString(currentSong.getMicroseconds());
            songNameLabel.setText(currentSong.getName() + " (" + durationAsString + ")");
        }
    }

    private void loadDefault() {
        loadSongsInMusicLibrary(musicLibrary.getSongs());
        loadAlbumsInTreeSong(null);
        setCovertArtLabel(appIcon);
    }

    private void initTitleMusicLibraryClickListener() {
        // @TODO: Eliminar?
        musicLibraryTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = musicLibraryTable.columnAtPoint(e.getPoint());
                String columnName = musicLibraryTable.getColumnName(columnIndex);
            }
        });
    }

    // @TODO: Sacar de acá
    public int getRandom() {
        return new Random().nextInt(songs.size());
    }

    private void playSelectedMusicLibrarySong() {
        int selectedRow = musicLibraryTable.getSelectedRow();
        
        Song song = (Song) musicLibraryTable.getValueAt(
            selectedRow, 
            MusicLibrarySongTableModel.COMPLETE_OBJECT_INDEX
        );

        MusicLibrarySongTableModel musicLibrarySongTableModel = (MusicLibrarySongTableModel) musicLibraryTable.getModel();

        songs = musicLibrarySongTableModel.songs;
        
        loadAlbumsInTreeSong(getAlbums(songs));
        play(song);
    }

    // Método que se llama cuando hago doble click en un tema musical
    // o cuando apreto enter en el arbol
    private void loadSong() {
        final File selectedTreeFile = getSelectedTreeFile();
        
        if (selectedTreeFile != null) {
            try {
                if (Validate.isSong(selectedTreeFile)) {
                    songs = new ArrayList<>();
                    songsLoadThread = new Thread(() -> {
                        cancelLoadingButton.setEnabled(true);
                        
                        try {
                            loadSongToSongList(selectedTreeFile.getParentFile());
                        } catch (InterruptedException | IOException ex) {
                            Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        loadAlbumsInTreeSong(getAlbums(songs));
                    });

                    songsLoadThread.start();
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
                this.workerStringProgress.changeProgressBarValue(percentage);
                musicPlayer.seek((long) VALUE);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setCoverArt(Song song) {
        setCovertArtLabel(appIcon);
        Album album = musicLibrary.getAlbum(song);

        if (!song.exists()) { // si canción no existe
            if (JOptionPane.showConfirmDialog(
                    this,
                    song.exists() + "[" + song.getName()+ "] no encontrada. "
                            + "¿Desea analizar la lista completa para eliminar los no encontrados?", "Error",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Iterator<Song> iterator = songs.iterator();

                Song nextSong;
                int count = 0;
                
                while (iterator.hasNext()) {
                    nextSong = iterator.next();

                    if (!nextSong.exists()) {
                        songs.remove(nextSong);
                        count++;
                    }
                }

                JOptionPane.showMessageDialog(this, "Se han eliminado " + count + " canciones de la lista.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            } else if (!album.hasCoversArt()) { // si el Album NO tiene una lista de imagenes
            List<ImageIcon> coversArt = Resource.getCoversArt(song);

            /*
            si la lista de fotos no esta vacía por lo menos hay una
            para poder comenzar el hilo de las caratulas
             */
            if (!coversArt.isEmpty()) {   
                album.setCoversArt(coversArt);
            } else { 
                // no hay imágenes en la carpeta de la canción
                coversArt = new ArrayList<>();
                
                try {
                    Image coverArt = LastFM.getCoverArt(song.getAuthor(), song.getAlbum());

                    /*
                    coverArt = coverArt.getScaledInstance(
                        (int) Rule.COVER_ART_DIMENTION.getWidth(),
                        (int) Rule.COVER_ART_DIMENTION.getHeight(),
                        Image.SCALE_SMOOTH
                    );
                     */

                    coversArt.add(new ImageIcon(coverArt));
                } catch (Exception ex) {
                    /*Establezco la caratula por defecto (el disco)*/
                    coversArt.add(new ImageIcon(appIcon));
                }
                
                album.setCoversArt(coversArt);
            }
        } 

        if (coverArtThread != null) {
            coverArtThread.interrupt();
        }

        setCoverArt(album.getCoverArt());
        setColorFondo(album);

        coverArtThread = new CoverArtThread(coverArtLabel, album.getCoversArt());
        coverArtThread.start();

        setIconImage(album.getCoverArt().getImage());

        treeSong.setCellRenderer(
            new SongListTreeCellRenderer()
        );
    }

    /**
     * Reproduce el siguiente, si es el último reproduce el primero
     */
    private void playNextSong() {
        if(musicPlayer.hasCurrentSong()){
            int currentIndex = songs.indexOf(musicPlayer.getCurrentSong());
            currentIndex++;

            if (currentIndex < songs.size()) {
                play(songs.get(currentIndex));
            } else {
                play(songs.get(0));
            }
        }
    }

    /**
     * Reproduce el anterior, si es el primero reproduce el último
     */
    private void playPreviousSong() {
        if(musicPlayer.hasCurrentSong()){
            int currentIndex = songs.indexOf(musicPlayer.getCurrentSong());
            currentIndex--;

            if (currentIndex >= 0) {
                play(songs.get(currentIndex));
            } else {
                // reproduce el último
                play(songs.get(songs.size() - 1));
            }
        }
    }

    private void playRandomSong() {
        play(songs.get(getRandom()));
    }

    @Override
    public void search(String text) {
        songsFound = new ArrayList<>();

        for (Song song : musicLibrary.getSongs()) {
            if (song.getAuthor().toLowerCase().contains(text)
                    || song.getAlbum().toLowerCase().contains(text)
                    || song.getName().toLowerCase().contains(text)) {
                songsFound.add(song);
            }
        }

        loadSongsInMusicLibrary(songsFound);
    }

    private void initBuscar() {
        //<editor-fold defaultstate="collapsed" desc="Código para escuchar a un boton para todos los componentes" >
        /*CON CTRL + F y f3 funciona el buscar*/
        InputMap inputMap = this.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();
        
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), "buscar");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "buscar");
        /*CON CTRL + F y f3 funciona el buscar*/

        actionMap.put("buscar", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainTabbedPane.setSelectedIndex(Rule.TabIndex.MUSIC_LIBRARY);
                
                if (searchDialog == null) {
                    searchDialog = new SearchDialog(JPlay.this, false);
                    searchDialog.setSearchable(JPlay.this);
                    searchDialog.setVisible(true);
                } else {
                    searchDialog.setVisible(!searchDialog.isVisible());
                }
                
                searchDialog.setBounds(
                    JPlay.this.getX(),
                    JPlay.this.getY(),
                    JPlay.this.getWidth(),
                    searchDialog.getHeight()
                );
            }
        });
        /*Código para escuchar a un boton para todos los componentes*/
        // </editor-fold>
    }

    // este método se llama cuando apreta enter en buscar
    @Override
    public void focusOn(String searchText) {
        try {
            if (searchText.startsWith("/")) {

                // si es un comando, despues cargo de nuevo la biblioteca
                loadSongsInMusicLibrary(musicLibrary.getSongs());

                String text = "";

                if (searchText.equalsIgnoreCase("/rutas")) {
                    text += "RUTAS:\n";

                    for (File file : musicLibrary.getPaths()) {
                        text += file.getPath() + "\n";
                    }

                    JOptionPane.showMessageDialog(this, text);
                } else if (searchText.equalsIgnoreCase("/scan")) {
                    ScanThread scanThread = new ScanThread(musicLibrary, this);
                    scanThread.start();
                }
            }
            
            musicLibraryTable.setRowSelectionInterval(0, 0);
        } catch (HeadlessException e) {
            // cae aca cuando no hay canciones en la tabla biblioteca
        }
    }

    @Override
    public void loadSearchComboBox(JComboBox comboBox) {

        comboBox.removeAllItems();
        comboBox.addItem("");

        for (String artistName : musicLibrary.getArtistNames()) {
            comboBox.addItem(artistName);
        }

        for (Album album : musicLibrary.getAlbums()) {
            if (!album.getName().trim().equals("")) {
                comboBox.addItem(album.getName());
            }
        }
    }

    private void initCoverArtPopUp() {
        coverArtPopUp = new JPopupMenu();

        JMenuItem deleteCoverArtItem = new JMenuItem("Eliminar Cover");

        deleteCoverArtItem.addActionListener((ActionEvent e) -> {
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

        coverArtPopUp.add(deleteCoverArtItem);

        coverArtLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showCoverArtPopUp(e);
            }
        });
    }

    private void showCoverArtPopUp(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            coverArtPopUp.show(e.getComponent(), e.getX() + 8, e.getY() + 8);
        }
    }

    private void initDragDropTabbedPane() {
        mainTabbedPane.setDropTarget(new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dropEvent) {

                try {
                    dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object transferData = dropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    final List<File> files = (List) transferData;

                    songsLoadThread = new Thread(() -> {
                        files.stream().forEach((file) -> {
                            try {
                                loadSongsInMusicLibrary(file);
                                loadSongsInMusicLibrary(musicLibrary.getSongs());
                                
                                musicLibrary.addSongsToAlbums();
                                musicLibrary.addPath(file);
                            } catch (IOException | InterruptedException ex) {
                                Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                        
                        System.out.println("HILO CARGAR DROP TERMINADO!");
                    });

                    songsLoadThread.start();
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dragEvent) {
                currentTabIndex = mainTabbedPane.getSelectedIndex();
                mainTabbedPane.setSelectedIndex(Rule.TabIndex.MUSIC_LIBRARY);
            }

            @Override
            public void dragOver(DropTargetDragEvent dragEvent) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dragEvent) {
            }

            @Override
            public void dragExit(DropTargetEvent dropTargetEvent) {
                mainTabbedPane.setSelectedIndex(currentTabIndex);
            }
        }));
    }

    /*Este método sirve para actualizar la tabla de biblioteca despues del scan (Clase Scan)*/
    @Override
    public void updateMusicLibraryUI(boolean hasChanged) {
        if (hasChanged) {
            loadSongsInMusicLibrary(musicLibrary.getSongs());
            musicLibrary.addSongsToAlbums();
        }
    }

    private void initIconosTabs() {
        mainTabbedPane.setIconAt(Rule.TabIndex.EXPLORER, SongListTreeCellRenderer.getImageIcon(Path.EXPLORER_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.MUSIC_LIBRARY, SongListTreeCellRenderer.getImageIcon(Path.MUSIC_LIBRARY_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.CURRENT_SONGS_LIST, SongListTreeCellRenderer.getImageIcon(Path.LIST_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.MOST_PLAYED, SongListTreeCellRenderer.getImageIcon(Path.MOST_PLAYED_TAB_ICON));
        mainTabbedPane.setIconAt(Rule.TabIndex.LOGGER, SongListTreeCellRenderer.getImageIcon(Path.LOG_TAB_ICON));
        //@TODO: WTF, mejorar
        mainTabbedPane.setIconAt(Rule.TabIndex.FAVORITES, new ImageIcon(SongListTreeCellRenderer.getImageIcon(Path.FAVORITES_TAB_ICON).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    private void setColorFondo(Album album) {
        /* Colores de fondo */
        if (album.hasCoversArt()) {
            Rule.BACKGROUND_COLOR = ImageProcessor.getAverageColor(album.getCoverArt());
        } else {
            Rule.BACKGROUND_COLOR = Color.white;
        }
        
        Rule.FOREGROUND_COLOR = ImageProcessor.getForeGroundColorBasedOnBGBrightness(Rule.BACKGROUND_COLOR);

        mainPanel.setBackground(Rule.BACKGROUND_COLOR);
        volumeSlider.setBackground(Rule.BACKGROUND_COLOR);
        songNameLabel.setForeground(Rule.FOREGROUND_COLOR);
        artistLabel.setForeground(Rule.FOREGROUND_COLOR);
        repeatSongCheckbox.setForeground(Rule.FOREGROUND_COLOR);
        shuffleCheckbox.setForeground(Rule.FOREGROUND_COLOR);
        loadInfoLabel.setForeground(Rule.FOREGROUND_COLOR);
        songDurationLabel.setForeground(Rule.FOREGROUND_COLOR);

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
        BufferedImage bufferedImageimage = new BufferedImage(
            cover.getIconWidth(), 
            cover.getIconHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        cover.paintIcon(null, bufferedImageimage.getGraphics(), 0, 0);

        coverArtLabel.setIcon(
            new ImageIcon(
                bufferedImageimage.getScaledInstance(
                (int) Rule.COVER_ART_DIMENTION.getWidth(),
                (int) Rule.COVER_ART_DIMENTION.getHeight(),
                Image.SCALE_SMOOTH)
            )
        );
    }

    private void setCovertArtLabel(Image coverArt) {
        coverArtLabel.setIcon(
            new ImageIcon(
                coverArt.getScaledInstance(
                    (int) Rule.COVER_ART_DIMENTION.getWidth(),
                    (int) Rule.COVER_ART_DIMENTION.getHeight(),
                    Image.SCALE_SMOOTH
                )
            )
        );
    }

    private void initArtistCoversArt() {
        if (artistCoverArts == null) {
            artistCoverArts = new ArrayList<>();
            musicLibrary.getArtistNames().forEach((artist) -> {
                try {
                    artistCoverArts.add(new ArtistCoverArt(artist));
                } catch (Exception ex) {
                    Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            for (String artistName : musicLibrary.getArtistNames()) {
                if (!isArtistExist(artistName)) {
                    try {
                        artistCoverArts.add(new ArtistCoverArt(artistName));
                    } catch (Exception ex) {
                        Logger.getLogger(JPlay.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /*Ordena descendente los artistas*/
        Collections.sort(artistCoverArts, (
            ArtistCoverArt artistCoverArt, 
            ArtistCoverArt anotherArtistCoverArt
        ) -> 
            artistCoverArt.getArtistName().compareTo(anotherArtistCoverArt.getArtistName())
        );

        try {
            artistList.setCellRenderer(new ArtistListCellRenderer(artistCoverArts));
            artistList.setModel(new ArtistListModel(artistCoverArts));
            artistList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            artistList.setVisibleRowCount(-1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isArtistExist(String artistName) {
        boolean anyMatch = artistCoverArts.stream().anyMatch(
            (artistCoverArt) -> (artistCoverArt.getArtistName().equals(artistName))
        );

        System.out.println("ARTISTA [" + artistName + "] --> " + anyMatch);

        return anyMatch;
    }

    private void initMusicPlayer() {
        musicPlayer = MusicPlayer.getInstance();
        musicPlayer.addBasicPlayerListener(this);
    }
}
