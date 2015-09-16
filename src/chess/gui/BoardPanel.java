package chess.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import chess.core.*;


import java.util.ArrayList;
import java.util.EnumMap;


@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
	final static Color BLACK_SQUARE_COLOR = new Color(58, 95, 205);
	final static Color WHITE_SQUARE_COLOR = new Color(176, 226, 255);
	final static int BOUNDARY_WIDTH = 40;
	
	private Chessboard board;
	private EnumMap<PieceColor,EnumMap<ChessPiece,BufferedImage>> images;
	private int xStart, yStart, xEnd, yEnd, width, height, wSquare, hSquare;
	
	private boolean selected = false;
	private BoardSquare pick;
	private ArrayList<BoardMoveListener> listeners;
	
	public BoardPanel() throws IOException {
		listeners = new ArrayList<BoardMoveListener>();
		
		images = new EnumMap<PieceColor,EnumMap<ChessPiece,BufferedImage>>(PieceColor.class);
		for (PieceColor color: PieceColor.values()) {
			images.put(color, new EnumMap<ChessPiece,BufferedImage>(ChessPiece.class));
			for (ChessPiece cp: ChessPiece.values()) {
				if (cp != ChessPiece.EMPTY) {
					String filename = "images" + File.separator + color + "_" + cp + ".png";
					images.get(color).put(cp, ImageIO.read(new File(filename)));
				}
			}
		}
		
		resetBoardSize();
		addMouseListener(new Mouser());
		resetBoard();
		Algebraic.moveExistsStartingAt(BoardSquare.E2, board);
	}
	
	public void resetBoard() {
		board = new Chessboard();
		repaint();
	}
	
	public void resetBoardWith(String... moves) {
		try {
			board = new Chessboard();
			for (String move: moves) {
				board = Algebraic.successor(board, move);
			}
		} catch (IllegalMoveException ime) {
			JOptionPane.showMessageDialog(this, ime.getMessage());
		}
		repaint();
	}
	
	public void addBoardMoveListener(BoardMoveListener bml) {
		listeners.add(bml);
	}
	
	public int xSquare2Pixel(int file) {
		return xStart + file * wSquare;
	}
	
	public int ySquare2Pixel(int rank) {
		return yStart + rank * hSquare;
	}
	
	public Chessboard getBoard() {return board;}
	
	public PieceColor mover() {
		return board.getMoverColor();
	}
	
	public void move(Move m) {
		String moveStr = Algebraic.encode(m, board);
		board = board.successor(m);
		repaint();
		for (BoardMoveListener bml: listeners) {bml.moveMade(moveStr);}
	}
	
	public void move(String algebraic) throws IllegalMoveException {
		move(Algebraic.decode(algebraic, board));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		resetBoardSize();
		drawBoard(g);
		drawIndices(g);
		drawPieces(g);
	}
	
	private void drawPieces(Graphics g) {
		for (BoardSquare sq: BoardSquare.values()) {
			if (board.at(sq) != ChessPiece.EMPTY) {
				int i = sq.fileNum() - 1;
				int j = 8 - sq.rankNum();
				g.drawImage(images.get(board.colorAt(sq)).get(board.at(sq)), xSquare2Pixel(i), ySquare2Pixel(j), wSquare, hSquare, null);
			}
		}
	}
	
	private void resetBoardSize() {
		xStart = BOUNDARY_WIDTH;
		yStart = xStart;
		width = getWidth() - BOUNDARY_WIDTH - xStart;
		height = getHeight() - BOUNDARY_WIDTH - yStart;
		width -= width % 8;
		height -= height % 8;
		wSquare = width / 8;
		hSquare = height / 8;	
		xEnd = xStart + width;
		yEnd = yStart + height;
	}
	
	private void drawBoard(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(WHITE_SQUARE_COLOR);
		g.fillRect(xStart, yStart, width, height);
		
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				if ((i + j) % 2 == 1) {
					g.setColor(BLACK_SQUARE_COLOR);
					g.fillRect(xSquare2Pixel(i), ySquare2Pixel(j), wSquare, hSquare);
				}
				if (selected && toSquare(xSquare2Pixel(i), ySquare2Pixel(j)) == pick) {
					g.setColor(Color.YELLOW);
					g.fillRect(xSquare2Pixel(i), ySquare2Pixel(j), wSquare, hSquare);
				}
			}
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(xStart, yStart, width, height);		
	}
	
	private void drawIndices(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < 8; ++i) {
			String fileChar = Character.toString((char)('a' + i));
			int x = xSquare2Pixel(i) + wSquare / 2;
			g.drawString(fileChar, x, BOUNDARY_WIDTH / 2);
			g.drawString(fileChar, x, yStart + height + BOUNDARY_WIDTH/2);
			
			String rankChar = Character.toString((char)('1' + (7 - i)));
			int y = ySquare2Pixel(i) + hSquare / 2;
			g.drawString(rankChar, BOUNDARY_WIDTH / 2, y);
			g.drawString(rankChar, xStart + width + BOUNDARY_WIDTH/4, y);
		}
	}
	
	private class Mouser extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getX() < xStart || e.getX() > xEnd || e.getY() < yStart || e.getY() > yEnd) {
				selected = false;
				repaint();
			} else {
				if (selected) {
					BoardSquare stop = toSquare(e.getX(), e.getY());
					try {
						move(Algebraic.decode(pick, stop, getBoard()));
					} catch (IllegalMoveException ime) {
						JOptionPane.showMessageDialog(BoardPanel.this, ime.getMessage());
					}
					selected = false;
				} else {
					pick = toSquare(e.getX(), e.getY());
					if (Algebraic.moveExistsStartingAt(pick, getBoard())) {
						selected = true;
						repaint();
					}
				}
			}
		}
	}
	
	public BoardSquare toSquare(int x, int y) {
		int file = 1 + (x - xStart) / wSquare;
		int rank = 8 - (y - yStart) / hSquare;
		return BoardSquare.fileRank2Square(file, rank);
	}
}
