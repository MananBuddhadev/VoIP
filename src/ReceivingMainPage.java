import java.awt.EventQueue;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 *
 * ReceivingMainPage.java
 *
 * Version: $Id$ : ReceivingMainPage.java, v1.0
 *
 * Revision:
 * 			Initial Revision
 *
 */

/**
 * The user interface for the receiver.
 *
 * @author Manan C Buddhadev
 * @author Hema Bahirwani
 */

public class ReceivingMainPage {

	private JFrame frame;
	public static JTextField textField;
	static serverReceivng receiver;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					ReceivingMainPage window = new ReceivingMainPage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		receiver= new serverReceivng();
	}


	public ReceivingMainPage(String Addr){

	}

	/**
	 * Updating the text box
	 *
	 * @param Addr Message to be displayed
     */
	public void setText(String Addr){
		textField.setText(Addr);
	}


	/**
	 * Create the application.
	 */
	public ReceivingMainPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(25);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnCall = new JButton("");

		btnCall.setIcon(new ImageIcon("imgs\\calling.jpg"));
		panel.add(btnCall);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon("imgs\\hangup.png"));
		panel.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					receiver.disconnect();
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JLabel lblSpirit = new JLabel("S.P.I.R.I.T");
		lblSpirit.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblSpirit.setForeground(Color.ORANGE);
		lblSpirit.setIcon(new ImageIcon("imgs\\logo.png"));
		frame.getContentPane().add(lblSpirit, BorderLayout.NORTH);
		
		textField = new JTextField(10);
		textField.setText("Waiting...");
		frame.getContentPane().add(textField, BorderLayout.CENTER);
		textField.setColumns(1);
		btnCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				receiver.pickUp();
			}
		});
	}

}
