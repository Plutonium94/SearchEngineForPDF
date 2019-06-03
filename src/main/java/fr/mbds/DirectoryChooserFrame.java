package fr.mbds;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DirectoryChooserFrame extends JFrame {

	private JTextField textField = new JTextField();
	private JFileChooser fileChooser = new JFileChooser();
	private JButton okButton = new JButton("Ok");
	private File selectedFile;
	private JPanel panel = new JPanel();


	public DirectoryChooserFrame() throws IOException {
		regularSetup();
		addToPanel();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showDialog(panel, "Index");
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			textField.setText(selectedFile.getAbsolutePath());
			System.out.println(selectedFile);
			App.indexPath(selectedFile.toPath());
		}

	}

	private void addToPanel() {
		for(JComponent jc : new JComponent[]{textField, fileChooser, okButton}) {
			panel.add(jc);
		}
		add(panel);
	}

	private void regularSetup() {
		setVisible(true);
		setTitle("Select directory to index : ");
		setSize(300, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}