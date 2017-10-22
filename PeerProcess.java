
package peerprocess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


class Host{
    int id;
    String hostname;
    int port;
    boolean hasFile;
}
class Process{
    int prefNeighbors;
    int unchokingInterval;
    int oUnchokingInterval;
    String fileName;
    long fileSize;
    long pieceSize;
    ArrayList<Host> hosts;
    
    BitSet pieces;
    
    public Process(int id) throws IOException {
        hosts = new ArrayList<Host>();
        //read config files
        readCommon();
        readPeers(id);
        
        //calculate size of bitset and initialize structure
        int bit_size = (int) Math.ceil((float)fileSize / pieceSize);
        pieces = new BitSet(bit_size);
        
        
    }
    private void readCommon() throws IOException{
        try{
            List<String> configLines = Files.readAllLines(Paths.get("Common.cfg"));
            for(String line : configLines){
                String[] words = line.split(" ");
                String var = words[0];
                String value = words[1];
                switch(var){
                    case "NumberOfPreferredNeighbors":
                        prefNeighbors = Integer.parseInt(value);
                        break;
                    case "UnchokingInterval":
                        unchokingInterval = Integer.parseInt(value);
                        break;
                    case "OptimisticUnchokingInterval":
                        oUnchokingInterval = Integer.parseInt(value);
                        break;
                    case "FileName":
                        fileName = value;
                        break;
                    case "FileSize":
                        fileSize = Long.parseLong(value);
                        break;
                    case "PieceSize":
                        pieceSize = Long.parseLong(value);
                        break;
                            
                            
                }
                
            }
        } catch (IOException e){
            System.out.println("ERROR, no config file found");
            throw new IOException();
        }
    
    }
    private void readPeers(int id) throws IOException{
        List<String> peerLines = Files.readAllLines(Paths.get("PeerInfo.cfg"));
        for(String line : peerLines){
            //parsing each line for [peer id] [host-name] [port] [has-file]
            String[] tokens = line.split(" ");
            //for now, just parse the string into a host object.
            //TODO: Add connections
            Host h = new Host();
            h.id = Integer.parseInt(tokens[0]);
            h.hostname = tokens[1];
            h.port = Integer.parseInt(tokens[2]);
            h.hasFile = Integer.parseInt(tokens[3]) == 1;
            hosts.add(h);
            for (String t : tokens){
                System.out.println(t); //just checking tokenizing works
            }
        }
    }
    
}
public class PeerProcess {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Starting");
        //System.out.println(Paths.get("").toAbsolutePath());
        Process p;
        try{
            p = new Process(Integer.parseInt(args[0]));
            System.out.println(p.fileName);
        } catch (IOException e){
            return;
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        System.out.println(p.fileName);
        //System.out.println(p.fileName);
        
    }
    
}
