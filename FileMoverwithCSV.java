package java18may;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import java.awt.CardLayout;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class FileMoverwithCSV extends JFrame {

	private JPanel contentPane;
	private JTable infoTable;
	private CheckboxTree fileTree;
	DefaultTreeModel treeModel;
	DefaultTableModel tableModel;
	CardLayout cl;
	JPanel panel;
	File[] source;
	File destination;
	String drive,path2,status="";
    int serial=1;
	JComboBox driveBox = new JComboBox();
	JComboBox childBox =new JComboBox();
	ArrayList<String> folderlist=new ArrayList<String>(); 
	ArrayList<String> filelist=new ArrayList<String>();  

	
	static void makeTree(File dir, DefaultMutableTreeNode root) {
		if(dir.isDirectory()) {

	         File[] files = dir.listFiles();
	         if(files!=null) {
	        	 for (int i = 0; i < files.length; i++)
		         {
	        		 if(files[i].isDirectory()) {
	        			 DefaultMutableTreeNode node = new DefaultMutableTreeNode(files[i].getName());
	        			 root.add(node);
	        			 makeTree(files[i],node);
	        		 }
		         }
	        	
	         }
	        
		}
	}
	void addfile(File[] files,String despath) {
		status="Success";
		for(File src:files) {
			destination = new File(despath);
			String path2=despath+"\\"+src.getName();

			File destinationfile = new File(path2);
			if(destinationfile.exists()) {
//				JOptionPane.showMessageDialog(this,"File Already Exists.","Error!!!", JOptionPane.ERROR_MESSAGE);
				int a=JOptionPane.showConfirmDialog(this,"Do you want to replace the file at Destination?","File Already Exists",JOptionPane.YES_NO_OPTION);  
				if(a==JOptionPane.NO_OPTION||a==JOptionPane.CLOSED_OPTION){  
					status="Failed";
				    continue;  
				}
			}
			try {
				Files.copy(Paths.get(src.toString()), Paths.get(path2),StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				status="Failed";
			}
			File[] srcfiles=src.listFiles();
			if(srcfiles!=null) {
				addfile(srcfiles,path2);
			}
			
		}
	}
	void exportcsvfile() {
		JFileChooser fileChooser = new JFileChooser();
   	 	fileChooser.setSelectedFile(new File("new.csv"));
   	 	fileChooser.setFileFilter(new FileNameExtensionFilter("csv file","csv"));
        fileChooser.setDialogTitle("Save File");
        int userSelection = fileChooser.showSaveDialog(this);
        if(userSelection == JFileChooser.APPROVE_OPTION){
            
        	File fileToSave = fileChooser.getSelectedFile();
            //lets write to file
         
            try {
                  FileWriter fw = new FileWriter(fileToSave);
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write("S. no.,"+"Name,"+"Status,"+"Source,"+"Destination\n");
                  for (int i = 0; i<infoTable.getRowCount(); i++) {
                	  for (int j = 0; j<infoTable.getColumnCount(); j++) {
                        //write
                		  bw.write(infoTable.getValueAt(i, j).toString()+",");
                	  }
                    bw.newLine();//record per line 
                  }
                  JOptionPane.showMessageDialog(this, "SUCCESSFULLY SAVED","INFORMATION",JOptionPane.INFORMATION_MESSAGE);
                  bw.close();
                  fw.close();
            } catch (IOException ex) {
               JOptionPane.showMessageDialog(this, "ERROR","ERROR MESSAGE",JOptionPane.ERROR_MESSAGE);
            }
            
            
        }
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileMoverwithCSV frame = new FileMoverwithCSV();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FileMoverwithCSV() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 762, 488);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(10, 11, 726, 427);
		contentPane.add(panel);
		panel.setLayout(new CardLayout(0, 0));
		
		JPanel panel1 = new JPanel();
		panel.add(panel1, "panel1");
		panel1.setLayout(null);
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setBorder(new BevelBorder(BevelBorder.LOWERED));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setBounds(0, 0, 603, 427);
		panel1.add(fileChooser);
		
		JPanel panel2 = new JPanel();
		panel.add(panel2, "panel2");
		panel2.setLayout(null);
		
		fileTree = new CheckboxTree();
		treeModel = (DefaultTreeModel) fileTree.getModel();
		treeModel.setRoot(new DefaultMutableTreeNode(new File("...")));
		fileTree.setSelectsByChecking(false);
		fileTree.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JScrollPane scrollPane1 = new JScrollPane(fileTree);
		scrollPane1.setBounds(150, 11, 366, 405);
		panel2.add(scrollPane1);
		
		File[] drives = File.listRoots();
		driveBox = new JComboBox<Object>(drives);
		driveBox.setBounds(20, 11, 100, 35);
		driveBox.setFont(new Font("Tahoma", Font.BOLD, 12));
		driveBox.setBackground(Color.WHITE);
		panel2.add(driveBox);
		
		JButton moveBtn = new JButton("MOVE");
		moveBtn.setBackground(Color.WHITE);
		moveBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		moveBtn.setBounds(20, 365, 100, 35);
		panel2.add(moveBtn);
		
		childBox.setBounds(545, 44, 156, 35);
		panel2.add(childBox);
		
		JLabel parentLabel = new JLabel("Parent");
		parentLabel.setFont(new Font("Cambria", Font.BOLD, 16));
		parentLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		parentLabel.setBounds(545, 11, 156, 30);
		panel2.add(parentLabel);
		
		JPanel panel3 = new JPanel();
		panel.add(panel3, "panel3");
		panel3.setLayout(null);
		
		JButton showBtn = new JButton("Show");
		showBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		showBtn.setBackground(Color.WHITE);
		showBtn.setBounds(595, 381, 100, 35);
		panel3.add(showBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(37, 11, 658, 359);
		panel3.add(scrollPane);
		
		infoTable = new JTable() {
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		String[] column = new String[] {
				"S. no.", "Name","Status","Source", "Destination"
			};
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(column);
		infoTable.setModel(tableModel);
        infoTable.setRowHeight(25);
		infoTable.getColumnModel().getColumn(0).setMaxWidth(50);
		infoTable.getColumnModel().getColumn(2).setMaxWidth(80);
		infoTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		infoTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		scrollPane.setViewportView(infoTable);
		infoTable.setBackground(SystemColor.menu);
		
		JButton printBtn = new JButton("Print");
		printBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		printBtn.setBackground(Color.WHITE);
		printBtn.setBounds(485, 381, 100, 35);
		panel3.add(printBtn);
		
		JButton prevBtn = new JButton("Back");
		prevBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		prevBtn.setBackground(Color.WHITE);
		prevBtn.setBounds(37, 381, 100, 35);
		panel3.add(prevBtn);
		

		
        cl = (CardLayout) panel.getLayout();
		 if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	         source = fileChooser.getSelectedFiles();
	         cl.show(panel,"panel2");
		 }
		 
		 /*
		  * Move Button Function
		  */
		 moveBtn.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){           

	    		TreePath[] paths = fileTree.getCheckingPaths();

	            for (TreePath path : paths != null ? paths : new TreePath[0]) {
	            	String path1 = "";
	            	Object elements[] = path.getPath();
	            	for (int i = 1, n = elements.length; i < n; i++) {
	            		path1+="\\"+elements[i];
//	                	System.out.println(path1);
	            	}
	            	path1=drive+path1;
	            	try {
	            	addfile(source,path1);
	            	}
	            	catch(Exception e2) {
	            		System.out.println("ERROR!!!");
	            		status="Failed";
	            	}
	            	for(int i=0;i<source.length;i++) {
	            		filelist.add(path1+"\\"+source[i].getName());
	            		folderlist.add(path1);
	            		
	            		tableModel.addRow(
	        				new Object[] {serial++,source[i].getName(),status,source[i].getPath(),path1}
	        			);
	            	}
	            }
	    		cl.show(panel,"panel3");
	    		
	    	}
		});
		 
		 /*
		  * Show Button Function
		  */
		 showBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						for(String s:folderlist) {
							Desktop.getDesktop().open(new File(s));							
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		 
		 /*
		  * Go Back on Click
		  */
		 prevBtn.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 cl.previous(panel);
			 }
		 });
		 
		 /*
		  * Open folder on row click
		  */
		 infoTable.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect doble click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow();

					try {
						Desktop.getDesktop().open(new File(filelist.get(row)));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // get the value of a row and column.
					
	            }
	         }
	      });
		 driveBox.addActionListener(new ActionListener(){  
		    	public void actionPerformed(ActionEvent e){
		    				    		
		    		drive = driveBox.getSelectedItem().toString();
		    		    
		    		File f3 = new File(drive);	    		
		    		DefaultMutableTreeNode root = new DefaultMutableTreeNode(drive);
		    		treeModel.setRoot(root);
		    		makeTree(f3,root);
		    		
		    	}
			});
		 fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			 public void valueChanged(TreeSelectionEvent e) {
				 childBox.removeAllItems();
		         TreePath parentPath = fileTree.getSelectionPath();
		         String parent = "";
		         if (parentPath != null) {
		            	Object elements[] = parentPath.getPath();
		            	for (int i = 1, n = elements.length; i < n; i++) {
		            		parent+="\\"+elements[i];
		            	}
		         }
				 File prnt = new File(drive+parent);
				 if(prnt.isDirectory()) {
					 File[] childs = prnt.listFiles();
					 for(File child:childs) {
						 childBox.addItem(child.getName());
					 }
				 }
			 }
		 });
		 printBtn.addActionListener(new ActionListener(){  
		    	public void actionPerformed(ActionEvent e){ 
		    		exportcsvfile();
		    	}
		 });
	}
}
