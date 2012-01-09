package org.maxml.reflect;

/**
 * A 1.4 application that requires the following additional files:
 *   TreeDemoHelp.html
 *    arnold.html
 *    bloch.html
 *    chan.html
 *    jls.html
 *    swingtutorial.html
 *    tutorial.html
 *    tutorialcont.html
 *    vm.html
 */

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.ImageIcon;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TypeTreePanel extends JPanel implements TreeSelectionListener {
	private JPanel displayPanel;
	private JCheckBox doFields;
	public JTree tree;
	private Class filterType;
	public TypeTreePanel(Class filterType) {
		super(new GridLayout(1, 0));
		
		this.filterType=filterType;

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				"Swing Components");
		createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		JPanel topPanel = new JPanel(new BorderLayout());
		doFields = new JCheckBox();
        doFields.setText("Use Fields");
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
        
        JButton genButton = new JButton( "Generate" );
        
        genButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BeanSetterPanel().makeCList();
            }
        });
        

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);
        topPanel.add(doFields, BorderLayout.NORTH);
        topPanel.add(treeView, BorderLayout.CENTER);
        topPanel.add(genButton, BorderLayout.SOUTH);
		// Create the HTML viewing pane.
		displayPanel = new JPanel();
		JScrollPane htmlView = new JScrollPane(displayPanel);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100); // XXX: ignored in some releases
		// of Swing. bug 4101306
		// workaround for bug 4101306:
		// treeView.setPreferredSize(new Dimension(100, 100));

		splitPane.setPreferredSize(new Dimension(500, 300));

		// Add the split pane to this panel.
		add(splitPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			CompNodeInfo cnode = (CompNodeInfo) nodeInfo;
			displayBeanSetter(cnode.cc);
		}
	}

	private class CompNodeInfo {

		public CachedClass cc;

		public CompNodeInfo(CachedClass cc) {
			this.cc = cc;
		}

		public String toString() {
			return cc.getShortName();
		}
	}

	private void displayBeanSetter(CachedClass cc) {
        
        displayPanel.removeAll();
        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(new BeanSetterPanel(cc, doFields.isSelected()), BorderLayout.CENTER);
        displayPanel.getParent().validate();
        displayPanel.repaint();
	}

	private void createNodes(DefaultMutableTreeNode top) {
		
		TreeMap nodeMap = new TreeMap();
		
		DefaultMutableTreeNode node = null;

		List l = ReflectCache.i().getClassesOfType(filterType);
		Iterator i = l.iterator();
		while (i.hasNext()) {
			CachedClass cc = (CachedClass) (i.next());

			node = new DefaultMutableTreeNode(new CompNodeInfo(cc));
			
			addToParent(top, node, cc, nodeMap);
		}

	}
	
	private void addToParent( DefaultMutableTreeNode top, DefaultMutableTreeNode newNode, CachedClass cc, TreeMap nodeMap){
		String packageName = cc.getPackageName();
		DefaultMutableTreeNode packageNode = (DefaultMutableTreeNode) nodeMap.get(packageName);
		if( packageNode == null) {
			packageNode = new DefaultMutableTreeNode(packageName);
			nodeMap.put(packageName,packageNode);
			top.add(packageNode);
		}
		String prefix = cc.getShortName().substring(0, 2);
		String catName = packageName+"."+prefix;
		DefaultMutableTreeNode catNode = (DefaultMutableTreeNode) nodeMap.get(catName);
		if( catNode == null) {
			catNode = new DefaultMutableTreeNode(prefix);
			nodeMap.put(catName,catNode);
			packageNode.add(catNode);
		}
		catNode.add(newNode);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = TypeTreePanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("TreeIconDemo2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTabbedPane tabs = new JTabbedPane();
		frame.setContentPane(tabs);
        tabs.addTab("GBC", new TypeTreePanel(Insets.class));
		tabs.addTab("Containers", new TypeTreePanel(Container.class));
		tabs.addTab("Layouts", new TypeTreePanel(LayoutManager.class));
        tabs.addTab("JComponents", new TypeTreePanel(JComponent.class));
		// Create and set up the content pane.

		// Display the window.
		frame.pack();
		frame.setVisible(true);
        
//        try {
//            FileOutputStream fos = new FileOutputStream("/home/jkirkley/jrp/setfer/t.tmp");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//
//            oos.writeObject(tp.tree);
//
//            oos.close();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

	}

	public static void brain()  {
		try{
//			ReflectCache.getInstance().addJar(
//											  "/home/jkirkley/jrp/setfer/lib/swing.jar");
//
//			ReflectCache.getInstance().addJar(
//											  "/home/jkirkley/jrp/setfer/lib/awt.jar");
//
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						createAndShowGUI();
					}
				});
		} catch( Exception e ) {e.printStackTrace(); }
	}

	public static void main(String[] args) throws Exception {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		brain();
	}

}
