package uk.ac.rhul.cs.cl1.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.StringUtils;

/**
 * The about dialog box for Cluster ONE.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class AboutDialog extends JDialog implements ActionListener {
	/**
	 * Constructs an about dialog with the given owner and modality.
	 * 
	 * @param owner   the owner of the dialog box
	 * @param modal   the modality of the dialog box
	 */
	public AboutDialog(Frame owner, boolean modal) {
		super(owner, "About Cluster ONE", modal);
		setResizable(false);
		
        JPanel contentPane = new JPanel();
        
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setEditorKit(new HTMLEditorKit());
		editorPane.setBackground(contentPane.getBackground());
        
		URL logoURL = this.getClass().getResource("resources/cl1_logo.png");
		String logoCode = "";
		if (logoURL != null) {
			logoCode = "<center><img src=\""+logoURL+"\" /></center>";
		}
        
        String textTemplate = null;
        try {
        	textTemplate = StringUtils.readInputStream(
        		this.getClass().getResourceAsStream("resources/about_dialog.txt")
        	);
        } catch (IOException ex) {
        	textTemplate = "";
        }
        
        String text = textTemplate.replaceAll("%logo%", logoCode)
                                  .replaceAll("%appname%", ClusterONE.applicationName)
                                  .replaceAll("%version%", ClusterONE.version);
        editorPane.setText(text);
        
        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand("close");
        closeButton.addActionListener(this);
        
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(editorPane);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(closeButton);
        
        setContentPane(contentPane);
        pack();
	}
	
	/**
	 * Constructs an about dialog with the given owner.
	 * 
	 * @param owner   the owner of the dialog box
	 */
	public AboutDialog(Frame owner) {
		this(owner, true);
	}

	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}
}