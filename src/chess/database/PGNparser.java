package chess.database;

import java.io.*;
import java.util.ArrayList;

public class PGNparser {
    private FileInputStream fstream;
    private BufferedReader br;

    private ArrayList<Integer> gameNumber;
    private ArrayList<String> gameResult, gameMoves;

    public void parsePGN(String pgn_file) throws IOException {
        gameNumber = new ArrayList<Integer>();
        gameResult = new ArrayList<String>();
        gameMoves = new ArrayList<String>();

        // Open the file
        fstream = new FileInputStream(pgn_file);
        br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        //Read File Line By Line
        while((strLine = br.readLine())!= null) {
            // get Game Numbers
            if(strLine.startsWith("[FICSGames")){
                //System.out.println("gameNumber: " + strLine.split("\"")[1]);
                gameNumber.add(Integer.parseInt(strLine.split("\"")[1]));
            }
            //get results of games
            else if(strLine.startsWith("[Result")) {
                //System.out.println(strLine);
                gameResult.add(strLine.split("\"")[1]);
            }
            // get moves from games
            else if(strLine.startsWith("1. ")){
                //System.out.println("MOVES: " + strLine.split("\\{")[0]);
                gameMoves.add(strLine.split("\\{")[0]);
            }
        }
        br.close();
    }

    public ArrayList<Integer> getGameNumberList(){
        return gameNumber;
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