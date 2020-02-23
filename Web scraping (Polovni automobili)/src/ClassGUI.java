import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class ClassGUI {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClassGUI window = new ClassGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClassGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 960, 540);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setTitle("Polovni automobili WEB SCRAPER");
		
		textField = new JTextField();
		textField.setBounds(10, 32, 934, 36);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("GENERATE");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 32));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Main.getAutoFromLink(textField.getText());
					textField.setText("SUCCESS");
				} catch (Exception e) {
					textField.setText("BAD LINK ILI VOZILO BEZ CENE");
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(352, 207, 233, 142);
		frame.getContentPane().add(btnNewButton);
	}
}
