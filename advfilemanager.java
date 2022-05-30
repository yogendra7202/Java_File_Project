package java20may;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.SystemColor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class advfilemanager extends JFrame {

	private JPanel contentPane;
	private JTable infoTable;
	private CheckboxTree fileTree;  
	private JTable moveTable;
	@SuppressWarnings("rawtypes")
	private JComboBox driveBox = new JComboBox();
	@SuppressWarnings("rawtypes")
	private JComboBox childBox =new JComboBox();
	JRadioButton rdBtn1,rdBtn2;
	DefaultTreeModel treeModel;
	DefaultTableModel tableModel,moveTableModel;
	CardLayout cl;
	JPanel panel;
	private File[] sources;
	ArrayList<String> sourceFiles=new ArrayList<String>();
	ArrayList<String> folderlist=new ArrayList<String>(); 
	ArrayList<String> filelist=new ArrayList<String>(); 
	ArrayList<String> filesname=new ArrayList<String>();
	
    int count,s2=0;
	String drive,status="";
	Boolean loop;

	/*
	 * Method to Make a Tree
	 */
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
	/*
	 * Add Data
	 */
	void adddata(ArrayList<String> datalist) {
		int s1=1;
		moveTableModel.setNumRows(0);
		for(String data:datalist)
		{
			File rowdata = new File(data);
			moveTableModel.addRow(
    				new Object[] {s1++,rowdata.getName().toString(),new File(rowdata.toString()).length()/1024+" KB",rowdata}
    		);
		}
	}
	
	/*
	 * Method to Copy the file from source to destination
	 */
	void addfile(File srcFile,String despath) {
		status="Success";
		count=1;

		String filePath = despath+"\\"+srcFile.getName();
		File destination = new File(despath+"\\"+srcFile.getName());

		while(destination.exists())
		{			
			
			if(rdBtn1.isSelected()) {
				String newfilePath;
				if(destination.isDirectory()) {
					newfilePath = filePath+ " (" + count++ + ")";
				}
				else {
					int lastDotIndex = filePath.lastIndexOf('.');
					newfilePath = filePath.substring(0, lastDotIndex ) + " (" + count++ + ")" + filePath.substring(lastDotIndex);
				}
				destination = new File(newfilePath);
			}
			else {
				status = "Failed (Duplicate)";
				filesname.add(destination.getName());
	    		filelist.add("Not Exist");
				return;
			}
			
		}
		if(loop) {
			filesname.add(destination.getName());
    		filelist.add(destination.toString());		
		}
		
		try {
			Files.copy(Paths.get(srcFile.toString()), Paths.get(destination.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			status="Failed";
		}
		if(srcFile.isDirectory()) {
			loop=false;
			File[] srcfiles = srcFile.listFiles();
			for(File src:srcfiles)
			{
				addfile(src,destination.toString());
			}
		}
	}
	
	/*
	 * Method to Export Report of moved files
	 */
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
					advfilemanager frame = new advfilemanager();
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
	public advfilemanager() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(10, 11, 764, 460);
		contentPane.add(panel);
		panel.setLayout(new CardLayout(0, 0));
		
		JPanel panel1 = new JPanel();
		panel.add(panel1, "panel1");
		panel1.setLayout(null);
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		
		JButton browse = new JButton("Browse");		
		browse.setBackground(Color.WHITE);
		browse.setFont(new Font("Tahoma", Font.BOLD, 12));
		browse.setBounds(25, 11, 100, 35);
		panel1.add(browse);
		
		JButton removeBtn = new JButton("Remove");
		removeBtn.setBackground(Color.WHITE);
		removeBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		removeBtn.setBounds(135, 11, 100, 35);
		panel1.add(removeBtn);
		
		JButton removeAllBtn = new JButton("Remove All");
		removeAllBtn.setBackground(Color.WHITE);
		removeAllBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		removeAllBtn.setBounds(245, 12, 105, 35);
		panel1.add(removeAllBtn);
				
		JButton moveBtn = new JButton("Move");
		moveBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		moveBtn.setBackground(Color.WHITE);
		moveBtn.setBounds(631, 11, 100, 35);
		panel1.add(moveBtn);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(25, 57, 706, 392);
		panel1.add(scrollPane_1);
		
		moveTable = new JTable() {
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		moveTableModel = new DefaultTableModel();
        moveTableModel.setColumnIdentifiers(new String[] {"S. No.", "Name", "Size", "Path"});
        moveTable.setRowHeight(25);
		moveTable.setModel(moveTableModel);
		moveTable.getColumnModel().getColumn(3).setPreferredWidth(300);
		scrollPane_1.setViewportView(moveTable);		
		
		JPanel panel2 = new JPanel();
		panel.add(panel2, "panel2");
		panel2.setLayout(null);
		
		File[] drives = File.listRoots();
		driveBox = new JComboBox<Object>(drives);
		driveBox.setBounds(24, 11, 100, 35);
		driveBox.setFont(new Font("Tahoma", Font.BOLD, 12));
		driveBox.setBackground(Color.WHITE);
		panel2.add(driveBox);
		
		rdBtn1 = new JRadioButton("Allow Duplication",true);
		rdBtn1.setBounds(24, 326, 150, 25);
		panel2.add(rdBtn1);
		
		rdBtn2 = new JRadioButton("Deny Duplication");
		rdBtn2.setBounds(24, 351, 150, 25);
		panel2.add(rdBtn2);
		
		ButtonGroup rdgroup = new ButtonGroup();
		rdgroup.add(rdBtn1);
		rdgroup.add(rdBtn2);
		
		JButton prevBtn2 = new JButton("Back");
		prevBtn2.setBackground(Color.WHITE);
		prevBtn2.setFont(new Font("Tahoma", Font.BOLD, 12));
		prevBtn2.setBounds(24, 395, 100, 35);
		panel2.add(prevBtn2);
		
		JButton nextBtn = new JButton("Next");
		nextBtn.setBackground(Color.WHITE);
		nextBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		nextBtn.setBounds(144, 395, 100, 35);
		panel2.add(nextBtn);
		
		fileTree = new CheckboxTree();
		treeModel = (DefaultTreeModel) fileTree.getModel();
		treeModel.setRoot(new DefaultMutableTreeNode(new File("...")));
		fileTree.setSelectsByChecking(false);
		fileTree.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JScrollPane scrollPane1 = new JScrollPane(fileTree);
		scrollPane1.setBounds(291, 11, 450, 438);
		panel2.add(scrollPane1);
		
		JLabel parentLabel = new JLabel("Parent");
		parentLabel.setFont(new Font("Cambria", Font.BOLD, 16));
		parentLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		parentLabel.setBounds(24, 100, 156, 25);
		panel2.add(parentLabel);
		
		childBox.setBounds(24, 130, 220, 35);
		panel2.add(childBox);
		
		JPanel panel3 = new JPanel();
		panel.add(panel3, "panel3");
		panel3.setLayout(null);
		
		JButton showBtn = new JButton("Show");
		showBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		showBtn.setBackground(Color.WHITE);
		showBtn.setBounds(634, 414, 100, 35);
		panel3.add(showBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(24, 11, 710, 360);
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
		infoTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		infoTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		infoTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		scrollPane.setViewportView(infoTable);
		infoTable.setBackground(SystemColor.menu);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(24, 382, 710, 15);
		panel3.add(progressBar);
		
		
		JButton printBtn = new JButton("Print");
		printBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		printBtn.setBackground(Color.WHITE);
		printBtn.setBounds(524, 414, 100, 35);
		panel3.add(printBtn);
		
		JButton prevBtn = new JButton("Back");
		prevBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		prevBtn.setBackground(Color.WHITE);
		prevBtn.setBounds(24, 414, 100, 35);
		panel3.add(prevBtn);
		

		
        cl = (CardLayout) panel.getLayout();

        /*
         * Make file tree on drive Selection
         */
		 driveBox.addActionListener(new ActionListener(){  
		    	public void actionPerformed(ActionEvent e){
		    				    		
		    		drive = driveBox.getSelectedItem().toString();
		    		    
		    		File f3 = new File(drive);	    		
		    		DefaultMutableTreeNode root = new DefaultMutableTreeNode(drive);
		    		treeModel.setRoot(root);
		    		makeTree(f3,root);
		    		
		    	}
			});
		 
		 /*
		  * Add child to combo box on tree node selection
		  */
		 fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			 @SuppressWarnings("unchecked")
			public void valueChanged(TreeSelectionEvent e) {
				 if(childBox!=null) {
					 childBox.removeAllItems();
				 }
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
					if(childs!=null) {
						for(File child:childs) {
							childBox.addItem(child.getName());
						}
					}
				 }
			 }
		 });
		 
        /*
         * Open Jfilechooser to select file
         */
        browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					sources = fileChooser.getSelectedFiles();
					for(File src:sources) {
						Boolean chk = true;
						for(String str:sourceFiles)
						{
							if(src.toString().equals(str)) {
								JOptionPane.showMessageDialog(contentPane,src.getName().toString()+"\nCouldn't selected Again.","File Already Selected",JOptionPane.INFORMATION_MESSAGE);
								chk = false;
							}
						}
						if(chk) {
							sourceFiles.add(src.toString());
						}
					}
					adddata(sourceFiles);
				}
			}
		});

        /*
         * Move to Panel2
         */
		moveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		         cl.show(panel,"panel2");
			}
		});
		
		/*
		 * Removes row from move table
		 */
		removeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = moveTable.getSelectedRow();
				sourceFiles.remove(row);
				adddata(sourceFiles);
			}
		});

		removeAllBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				while(moveTableModel.getRowCount()>0) {
					moveTableModel.removeRow(0);
				}
				sourceFiles.removeAll(sourceFiles);
			}
		});
		 
		 /*
		  *  Next Button Function
		  */
		
		 nextBtn.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){           

	    		Thread th = new Thread(new Runnable() {
	    			public void run() {
	    				
	    	    		cl.show(panel,"panel3");
	    				showBtn.setEnabled(false);
	    				prevBtn.setEnabled(false);
	    				printBtn.setEnabled(false);
	    	    		progressBar.setValue(0);
	    	    		progressBar.setForeground(new Color(255, 153, 0));
	    	    		
	    				TreePath[] paths = fileTree.getCheckingPaths();
	    				
	    	    		int proval = 0;
	    	            for (TreePath path : paths != null ? paths : new TreePath[0])
	    	            {
	    	            	String despath = "";
	    	            	Object elements[] = path.getPath();
	    	            	
	    	            	for (int i = 0, n = elements.length; i < n; i++) {
	    	            		despath+=elements[i]+"\\";
	    	            	}
//	    	            	despath=drive+despath;
	    	            	folderlist.add(despath);
	    	            	
	    	            	for(int i=0;i<sourceFiles.size();i++) {

	    	            		loop=true;
	    	            		File source = new File(sourceFiles.get(i));
	    	            		try {
	    		            		addfile(source,despath);
	    		            	}
	    		            	catch(Exception e2) {
	    		            		System.out.println("ERROR!!!");
	    		            		status="Failed";
	    		            		e2.getStackTrace();
	    		            	}
	    	            		tableModel.addRow(new Object[] {s2+1,null,status,source.getAbsoluteFile(),despath});
	    	            		tableModel.setValueAt(filesname.get(s2), s2, 1);
	    	            		s2++;
	    	            		
	    	            		if(proval>=35 && proval<55) {
	    	            			progressBar.setForeground(new Color(255, 100, 0));
	    	            		}
	    	            		else if(proval>=55 && proval<80){
	    	            			progressBar.setForeground(new Color(102, 255, 0));	    	            			
	    	            		}
	    	            		else if(proval>80) {
	    	            			progressBar.setForeground(new Color(0, 255, 45));	    	            			
	    	            		}
	    	            		for(int val=proval;val<proval+100/(sourceFiles.size()*paths.length);val++) {
	    	            			progressBar.setValue(val);
	    	            			try {
										Thread.sleep(80);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}	    	            			
	    	            		}
	    	            		proval+=100/(sourceFiles.size()*paths.length);
	    	            	}    	            		  	            		
    	            		
	    	            }

	    				progressBar.setValue(100);
	    				showBtn.setEnabled(true);
	    				prevBtn.setEnabled(true);
	    				printBtn.setEnabled(true);
	    			}
	    		});
	    		th.start();
	    		
	    	}
		});

		 infoTable.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect double click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // get the value of a row and column.

	               if(filelist.get(row).equals("Not Exist")) {
	            	   JOptionPane.showMessageDialog(panel3,"File Not Exists","Alert",JOptionPane.ERROR_MESSAGE);
	               }
	               else {
	            	   try {
							Desktop.getDesktop().open(new File(filelist.get(row)));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println(filelist.get(row));
						}
	               }
					
	            }
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
		 prevBtn2.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 cl.previous(panel);
			 }
		 });
		 prevBtn.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 cl.previous(panel);
			 }
		 });
		 
		 /*
		  * Open folder on row click
		  */
		 printBtn.addActionListener(new ActionListener(){  
		    	public void actionPerformed(ActionEvent e){ 
		    		exportcsvfile();		    		
		    	}
		 });
	}
}
