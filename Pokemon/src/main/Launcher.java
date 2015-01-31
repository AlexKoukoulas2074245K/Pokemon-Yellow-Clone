package main;

import javax.swing.JFrame;

public class Launcher {
	public static void main(String[] args)
	{	
		JFrame window = new JFrame("New GameLoop");
		window.setContentPane(new Main());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
}
