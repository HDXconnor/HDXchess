package chess.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;

import chess.ai.BoardEval;
import chess.ai.MoveScore;
import chess.ai.Searcher;
import chess.core.PieceColor;


@SuppressWarnings("serial")
public class Chess extends JFrame implements BoardMoveListener {
	private BoardPanel board;
	private EnumMap<PieceColor,JComboBox> searchChoosers;
	private EnumMap<PieceColor,JComboBox> evalChoosers;
	private EnumMap<PieceColor,JComboBox> depthChoosers;
	private JTextField humanMove;
	private JTextArea moves;
	private JButton computerMove;
	private JButton status;
	private JButton reset;
	private JButton replaceGame;
	private DataPanel data;
	private AIReflector<Searcher> searchers;
	private AIReflector<BoardEval> evaluators;
	
	public Chess() throws IOException {
		searchers = new AIReflector<Searcher>(Searcher.class, "chess.ai");
		evaluators = new AIReflector<BoardEval>(BoardEval.class, "chess.ai");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700,800);
		getContentPane().setLayout(new BorderLayout());
		
		board = new BoardPanel();
		board.addBoardMoveListener(this);
		getContentPane().add(board, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
		/*
		control.add(new JLabel("Human move: "));
		humanMove = new JTextField(9);
		humanMove.addActionListener(new HumanMover());
		control.add(humanMove);
		*/
		status = new JButton("Game status");
		status.addActionListener(new ActionListener(){@Override
			public void actionPerformed(ActionEvent e) {
				reportStatus();
			}});
		control.add(status);
		
		computerMove = new JButton("Computer move");
		computerMove.addActionListener(new ComputerMover());
		control.add(computerMove);
		data = new DataPanel("Boards expanded");
		control.add(data);
		
		getContentPane().add(control, BorderLayout.NORTH);
		
		JPanel config = new JPanel();
		config.setLayout(new GridLayout(1, 2));
		
		searchChoosers = new EnumMap<PieceColor,JComboBox>(PieceColor.class);
		evalChoosers = new EnumMap<PieceColor,JComboBox>(PieceColor.class);
		depthChoosers = new EnumMap<PieceColor,JComboBox>(PieceColor.class);
		
		for (PieceColor color: PieceColor.values()) {
			makeAIPanel(config, color);
		}
		
		getContentPane().add(config, BorderLayout.SOUTH);
		
		JPanel side = new JPanel();
		side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
		reset = new JButton("Restart game");
		reset.addActionListener(new ActionListener(){@Override
			public void actionPerformed(ActionEvent e) {board.resetBoard(); moves.setText("");}});
		
		replaceGame = new JButton("Replace game");
		replaceGame.addActionListener(new ActionListener(){@Override
			public void actionPerformed(ActionEvent e) {board.resetBoardWith(moves.getText().trim().split("\\s+"));}});
		
		side.add(reset);
		side.add(replaceGame);
		moves = new JTextArea(20, 10);
		side.add(new JScrollPane(moves));
		getContentPane().add(side, BorderLayout.EAST);
	}
	
	private String capitalized(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	private void makeAIPanel(JPanel parent, PieceColor color) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		searchChoosers.put(color, new JComboBox());
		for (String searcher: searchers.getTypeNames()) {
			searchChoosers.get(color).addItem(searcher);
		}
		evalChoosers.put(color, new JComboBox());
		for (String eval: evaluators.getTypeNames()) {
			evalChoosers.get(color).addItem(eval);
		}
		depthChoosers.put(color, new JComboBox());
		for (int i = 1; i < 10; ++i) {
			depthChoosers.get(color).addItem(Integer.toString(i));
		}
		panel.add(new JLabel(capitalized(color.toString()) + " searcher"));
		panel.add(searchChoosers.get(color));
		panel.add(new JLabel(capitalized(color.toString()) + " evaluator"));
		panel.add(evalChoosers.get(color));
		panel.add(new JLabel(capitalized(color.toString()) + " depth"));
		panel.add(depthChoosers.get(color));
		parent.add(panel);
	}
	
	public static void main(String[] args) throws IOException {
		new Chess().setVisible(true);
	}
	
	private class HumanMover implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String mv = humanMove.getText();
				humanMove.setText("");
				board.move(mv);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(Chess.this, exc.getMessage());
			}
		}
	}
	
	private class ComputerMover implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (board.getBoard().gameInProgress()) {
				AIThread ait = new AIThread();
				DataThread dt = new DataThread(ait);
				ait.start();
				dt.start();
			} else {
				reportStatus();
			}
		}
	}
	
	private void reportStatus() {
		String message = "INTERNAL ERROR: Unknown status";
		if (board.getBoard().gameInProgress()) {
			message = "Game in progress; " + board.getBoard().getMoverColor() + "'s turn";
		} else if (board.getBoard().isCheckmate()) {
			message = "CHECKMATE! " + board.getBoard().getMoverColor().other() + " wins.";
		} else if (board.getBoard().isStalemate()) {
			message = "Stalemate...";
		}
		JOptionPane.showMessageDialog(Chess.this, message);
	}
	
	private class AIThread extends Thread {
		private Searcher s;
		private boolean running = false;
		
		@Override
		public void run() {
			try {
				s = searchers.newInstanceOf(searchChoosers.get(board.mover()).getSelectedItem().toString());
				BoardEval be = evaluators.newInstanceOf(evalChoosers.get(board.mover()).getSelectedItem().toString());
				int depth = Integer.parseInt(depthChoosers.get(board.mover()).getSelectedItem().toString());
				running = true;
				MoveScore best = s.findBestMove(board.getBoard(), be, depth);
				board.move(best.getMove());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			running = false;
		}
		
		public boolean isReady() {
			return s != null;
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public int getBoardsGenerated() {
			return s.getBoardsGenerated();
		}
	}
	
	private class DataThread extends Thread {
		private AIThread searcher;
		DataThread(AIThread searcher) {this.searcher = searcher;}
		
		public void run() {
			long start = System.currentTimeMillis();
			long elapsed = start;
			while (!searcher.isReady());
			do {
				elapsed = System.currentTimeMillis() - start;
				long boards = searcher.getBoardsGenerated();
				data.bump(DataField.TIME, Long.toString(elapsed));
				data.bump(DataField.BOARDS_EXPANDED, Long.toString(boards));
				data.bump(DataField.BOARDS_PER_MS, Float.toString((float)boards / (float)elapsed));
				data.repaint();
			} while (searcher.isRunning()) ;
		}
	}
	
	private class DataPanel extends JPanel {
		private EnumMap<DataField,String> label2data;
		private final static int pixPerLabel = 15;
		
		DataPanel(String... labels) {
			super();
			label2data = new EnumMap<DataField,String>(DataField.class);
			for (DataField label: DataField.values()) {
				label2data.put(label, "");
			}
			this.setBackground(Color.WHITE);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int y = pixPerLabel;
			for (DataField label: DataField.values()) {
				g.drawString(label + ": " + label2data.get(label), 5, y);
				y += pixPerLabel;
			}
		}
		
		public void bump(DataField label, String data) {
			label2data.put(label, data);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(300, pixPerLabel * DataField.values().length);
		}
	}

	@Override
	public void moveMade(String algMove) {
		String space = board.getBoard().getMoverColor() == PieceColor.BLACK ? "\n" : " ";
		moves.append(space + algMove);
	}
	
	private class GameReplacer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}
