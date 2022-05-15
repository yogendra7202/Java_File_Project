package java14may;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileExplorer {
	
	protected CheckboxTree tree;
	protected JTable table;

	static JFrame frame;
    static String path1 = "";	
	static JScrollPane pane1,pane2;
	static JFileChooser jfc;
    static DefaultTreeModel treeModel;
    static DefaultTableModel tableModel;
	
	FileExplorer(){
		tree = new CheckboxTree();
        treeModel = (DefaultTreeModel)tree.getModel();
        treeModel.setRoot(new DefaultMutableTreeNode(new File("...")));
        
        table = new JTable();
        Object[] column = {"Name","Type","Size","Path"};
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(column);
    	
        table.setRowHeight(30);
        table.setModel(tableModel);
        table.setFillsViewportHeight(true);
        
		frame = new JFrame("File Explorer");
		frame.setMinimumSize(new Dimension(1000,600));
		frame.setResizable(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	static void makeTree(File dir, DefaultMutableTreeNode root) {
		if(dir.isDirectory()) {

//			System.out.println("Path  : "+dir.getPath());
	         File[] files = dir.listFiles();
	         if(files!=null) {
	        	 
	        	 for (int i = 0; i < files.length; i++)
		         {
		        	 DefaultMutableTreeNode node = new DefaultMutableTreeNode(files[i].getName());
		        	 root.add(node);
		        	 makeTree(files[i],node);
		         }
	        	
	         }
	        
		}
	}
	
	
	
	public static void main(String[] args) {
		FileExplorer ob = new FileExplorer();
		
		jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		
		JTextField textpath;
		textpath = new JTextField();
		textpath.setBounds(120, 30, 570, 40);
		
		JButton btn,show;
		btn = new JButton("Browse");
		btn.setBounds(710, 30, 110, 40);
		show = new JButton("Properties");
		show.setBounds(820, 30, 110, 40);
       	
		btn.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){           
            
	    		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

	                ob.tree.clearChecking();
	    			while(tableModel.getRowCount()>0) {
		        		tableModel.removeRow(0);
		        	}
	    			
	   	         	File f1 = jfc.getSelectedFile();
	   	         	path1=f1.getPath();
	   	         	textpath.setText(path1);
		    		DefaultMutableTreeNode root = new DefaultMutableTreeNode(f1.getName());
	   	         	treeModel.setRoot(root);
	   	         	makeTree(f1,root);
	    		}
	    		
	    	}
		});

		show.addActionListener(e -> {
            TreePath[] paths = ob.tree.getCheckingPaths();
            
            while(tableModel.getRowCount()>0) {
        		tableModel.removeRow(0);
        	}
            
            for (TreePath path : paths != null ? paths : new TreePath[0]) {
                String path2 = "";
            	Object elements[] = path.getPath();
            	for (int i = 1, n = elements.length; i < n; i++) {
            		path2+="\\"+elements[i];
            	}
            	File f2 = new File(path1+path2);
            	                           	
    	        tableModel.addRow(
    	        		new Object[]{f2.getName(),f2.isDirectory()?"Folder":"File",f2.length()/1024+" KB",f2.getAbsolutePath()}
    	        );
            }
            
        });
	
		
		JComboBox<?> drivebox;
		File[] drives = File.listRoots();
		drivebox = new JComboBox<Object>(drives);
		drivebox.setBounds(50,30,50,40);
		
		drivebox.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){
	    		
	    		ob.tree.clearChecking();
	    		
	    		String s = drivebox.getSelectedItem().toString();
	    		textpath.setText(s);
	    		path1=s;
	    		    
	    		File f3 = new File(s);	    		
	    		DefaultMutableTreeNode root = new DefaultMutableTreeNode(s);
   	         	treeModel.setRoot(root);
	    		makeTree(f3,root);
	    		
	    	}
		});
		
		

		pane1 = new JScrollPane(ob.tree);
        pane1.setBounds(50, 110, 300, 400);
        pane2 = new JScrollPane(ob.table);
        pane2.setBounds(400,110,530,400);
        
        frame.getContentPane().setLayout(null);
        
        frame.getContentPane().add(drivebox);
		frame.getContentPane().add(textpath); 
		frame.getContentPane().add(btn);
		frame.getContentPane().add(show);
       	frame.getContentPane().add(pane1);
       	frame.getContentPane().add(pane2);
       	frame.pack();
		frame.repaint();
		
	}
}