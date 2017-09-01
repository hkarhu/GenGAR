package gengar.editor;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

public class EditorWindow extends JFrame implements ActionListener, ListSelectionListener, ChangeListener {

	/**
	 * 
	 */
	private static final String progname = "Table Augmentor 0.1b";
	private static final long serialVersionUID = 8591863043481105121L;
	
	private MapEditPanel editview;
	private Map	map;

	private TextureCellRenderer txtCellRenderer;	
	private JList<TextureListItem> textureList;
	private DefaultListModel<TextureListItem> floorTextureData;

	private JList<TextureListItem> itemList;
	private DefaultListModel<TextureListItem> itemData;

	private JList<TextureListItem> doorList;
	private DefaultListModel<TextureListItem> doorData;

	private JToolBar toolbar;
	private Menu menu;
	private MenuItem menuItem;
	private MenuBar menubar;

	private JTabbedPane tabpane;
	private JScrollPane scrollpane;
	private JSplitPane splitpane;

	
	private JToggleButton buttonBrowse;
	private JToggleButton buttonCreate;
	private JToggleButton buttonMove;

	private EditModes createMode = EditModes.createPoly;

	public EditorWindow() {

		setName(progname);
		setTitle(progname);

		menubar = new MenuBar();
		menu = new Menu("File");

		menuItem = new MenuItem("New map");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new MenuItem("Load map");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new MenuItem("Save map");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menubar.add(menu);
		setMenuBar(menubar);

		setLayout(new BorderLayout());

		toolbar = new JToolBar(JToolBar.HORIZONTAL);
		buttonMove = new JToggleButton("Move");
		buttonMove.addActionListener(this);
		buttonBrowse = new JToggleButton("Browse");
		buttonBrowse.addActionListener(this);
		buttonCreate = new JToggleButton("Create");
		buttonCreate.addActionListener(this);
		buttonCreate.setSelected(true);
		toolbar.add(buttonMove);
		toolbar.add(buttonBrowse);
		toolbar.add(buttonCreate);

		add(toolbar, BorderLayout.PAGE_START);

		editview = new MapEditPanel();
		editview.setMap(map);
		txtCellRenderer = new TextureCellRenderer();

		floorTextureData = new DefaultListModel<>();
		textureList = new JList<>(floorTextureData);
		textureList.setCellRenderer(txtCellRenderer);
		textureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		textureList.setPreferredSize(new Dimension(240,0));
		textureList.addListSelectionListener(this);

		itemData = new DefaultListModel<>();
		itemList = new JList<>(itemData);
		itemList.setCellRenderer(txtCellRenderer);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemList.setPreferredSize(new Dimension(240,0));
		itemList.addListSelectionListener(this);

		doorData = new DefaultListModel<>();
		doorList = new JList<>(doorData);
		doorList.setCellRenderer(txtCellRenderer);
		doorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		doorList.setPreferredSize(new Dimension(240,0));
		doorList.addListSelectionListener(this);

		tabpane = new JTabbedPane();

		scrollpane = new JScrollPane();
		scrollpane.setViewportView(textureList);
		tabpane.add(scrollpane, "Floors");

		scrollpane = new JScrollPane();
		scrollpane.setViewportView(doorList);
		tabpane.add(scrollpane, "Doors");

		scrollpane = new JScrollPane();
		scrollpane.setViewportView(itemList);
		tabpane.add(scrollpane, "Items");

		tabpane.addChangeListener(this);

		splitpane = new JSplitPane();
		splitpane.setLeftComponent(tabpane);
		splitpane.setRightComponent(editview);

		add(splitpane, BorderLayout.CENTER);

		this.setMinimumSize(new Dimension(1024,768));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();
		this.setVisible(true);

		scanForTextures(floorTextureData, Paths.get("gfx/floors/"));
		scanForTextures(itemData, Paths.get("gfx/items/"));
		scanForTextures(doorData, Paths.get("gfx/doors/"));

		if(floorTextureData.getSize() > 0){
			textureList.setSelectedIndex(0);
			editview.setTexture(floorTextureData.getElementAt(0));
		} else {
			System.err.println("No textures available!");
		}

	}

	private void scanForTextures(DefaultListModel<TextureListItem> container, Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{jpg,jpeg,png,gif}")) {
			for (Path entry: stream) {
				System.out.println(entry);
				container.add(0, new TextureListItem(entry));
			}
		} catch (DirectoryIteratorException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println(e.getActionCommand());

		switch (e.getActionCommand()) {
		case "New map":
			RopeTableAugmentor.newMap();
			break;
		case "Save map":
			RopeTableAugmentor.saveMap();
			break;
		case "Load map":
			RopeTableAugmentor.loadMap();
			break;
		case "Add": editview.setMode(EditModes.addVertex); break;
		case "Browse": editview.setMode(EditModes.browse); break;
		case "Create": editview.setMode(createMode); break;
		default: break;
		}

		buttonMove.setSelected(editview.getMode() == EditModes.addVertex);
		buttonBrowse.setSelected(editview.getMode() == EditModes.browse);
		buttonCreate.setSelected(editview.getMode() == EditModes.createPoly ||
				editview.getMode() == EditModes.createDoor ||
				editview.getMode() == EditModes.createItem);

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource().equals(textureList)){
			editview.setTexture(floorTextureData.getElementAt(textureList.getSelectedIndex()));
		} else if(e.getSource().equals(doorList)){
			editview.setTexture(doorData.getElementAt(doorList.getSelectedIndex()));
		} else if(e.getSource().equals(itemList)){
			editview.setTexture(itemData.getElementAt(itemList.getSelectedIndex()));
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		switch (tabpane.getSelectedIndex()) {
		case 0: 
			createMode = EditModes.createPoly;
			textureList.setSelectedIndex(0);
			editview.setTexture(floorTextureData.getElementAt(textureList.getSelectedIndex()));
			break;
		case 1: 
			createMode = EditModes.createDoor; 
			doorList.setSelectedIndex(0);
			editview.setTexture(doorData.getElementAt(doorList.getSelectedIndex())); 
			break;
		case 2: 
			createMode = EditModes.createItem; 
			itemList.setSelectedIndex(0);
			editview.setTexture(itemData.getElementAt(itemList.getSelectedIndex())); 
			break;
		default: break;
		}

		if(buttonCreate.isSelected()) editview.setMode(createMode);

	}

	public void setMap(Map map) {
		editview.setMap(map);
	}

}
