import java.awt.EventQueue;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * CallerMainPage.java
 *
 * Version: $Id$ : CallerMainPage.java, v1.0
 *
 * Revision:
 * 			Initial Revision
 *
 */

/**
 * The user interface for caller.
 *
 * @author Manan C Buddhadev
 * @author Hema Bahirwani
 *
 */

public class CallerMainPage {

	private JFrame frame;
	static JTextField txtS;
	public static String serverAdress;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CallerMainPage window = new CallerMainPage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public CallerMainPage(String Addr){

	}

	/**
	 * Update the textbox
	 *
	 * @param Addr Message to be displayed
     */
	public void setText(String Addr){
		txtS.setText(Addr);
	}

	/**
	 * Create the application.
	 */
	public CallerMainPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(25);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JLabel lblManan = new JLabel("Manan :");
		lblManan.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel.add(lblManan);

		JButton btnNewButton = new JButton("");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					clientCalling c = new clientCalling(serverAdress);
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setIcon(new ImageIcon("imgs\\calling.jpg"));
		panel.add(btnNewButton);

		JButton button = new JButton("");
		button.setIcon(new ImageIcon("imgs\\hangup.png"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientCalling.disconnect();
			}
		});
		panel.add(button);

		JLabel lblNewLabel = new JLabel(" S.P.I.R.I.T");
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setForeground(Color.ORANGE);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblNewLabel.setIcon(new ImageIcon("imgs\\logo.png"));
		frame.getContentPane().add(lblNewLabel, BorderLayout.NORTH);

		txtS = new JTextField();
		txtS.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(txtS, BorderLayout.CENTER);
		txtS.setColumns(1);
		txtS.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				serverAdress=txtS.getText();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}
}
