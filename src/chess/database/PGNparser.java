package chess.database;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class PGNparser {
    private FileInputStream fstream;
    private BufferedReader br;
    private Scanner scanner;

    private ArrayList<Integer> gameNumber;
    private ArrayList<String> gameResult, gameMoves;

    public void parsePGN(String pgn_file) throws IOException {
        gameNumber = new ArrayList<Integer>();
        gameResult = new ArrayList<String>();
        gameMoves = new ArrayList<String>();

        fstream = null;
        scanner = null;
        try {
            fstream = new FileInputStream(pgn_file);
            scanner = new Scanner(fstream, "UTF-8");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // get Game Numbers
                if(line.startsWith("[FICSGames")){
                    //System.out.println("gameNumber: " + line.split("\"")[1]);
                    gameNumber.add(Integer.parseInt(line.split("\"")[1]));
                }
                //get results of games
                else if(line.startsWith("[Result")) {
                    //System.out.println(line);
                    gameResult.add(line.split("\"")[1]);
                }
                // get moves from games
                else if(line.startsWith("1. ")){
                    //System.out.println("MOVES: " + line.split("\\{")[0]);
                    gameMoves.add(line.split("\\{")[0]);
                }
            }
            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        } finally {
            if (fstream != null) {
                fstream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
//        // Open the file
//        fstream = new FileInputStream(pgn_file);
//        br = new BufferedReader(new InputStreamReader(fstream));
//
//        String strLine;
//
//        //Read File Line By Line
//        while((strLine = br.readLine())!= null) {
//            // get Game Numbers
//            if(strLine.startsWith("[FICSGames")){
//                //System.out.println("gameNumber: " + strLine.split("\"")[1]);
//                gameNumber.add(Integer.parseInt(strLine.split("\"")[1]));
//            }
//            //get results of games
//            else if(strLine.startsWith("[Result")) {
//                //System.out.println(strLine);
//                gameResult.add(strLine.split("\"")[1]);
//            }
//            // get moves from games
//            else if(strLine.startsWith("1. ")){
//                //System.out.println("MOVES: " + strLine.split("\\{")[0]);
//                gameMoves.add(strLine.split("\\{")[0]);
//            }
//        }
//        br.close();
    }

    public ArrayList<Integer> getGameNumberList(){
        return gameNumber;
    }
    public int getGNlistSize(){
        return gameNumber.size();
    }

    public ArrayList<String> getGameResultList(){
        return gameResult;
    }

    public ArrayList<String> getGameMovesList(){
        return gameMoves;
    }

    public static void main(String[] args){
        PGNparser parser = new PGNparser();
        try {
            parser.parsePGN("ficsDB.pgn");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}