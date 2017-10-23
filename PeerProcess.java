import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.net.*;




class Host{
    int id;
    String hostname;
    int port;
    boolean hasFile;
    
    ServerSocket sock;
    BitSet pieces;

    
}
class Process{
    int prefNeighbors;
    int unchokingInterval;
    int oUnchokingInterval;
    String fileName;
    long fileSize;
    long pieceSize;
    ArrayList<Host> hosts;
    
    int id;
    BitSet pieces;
    
    public Process(int id) throws Exception {
        hosts = new ArrayList<>();
        
        readCommon(); //reads common.cfg file to init variables.
        
        //calculate size of bitset and initialize structure
        int bit_size = (int) Math.ceil((float)fileSize / pieceSize);
        this.pieces = new BitSet(bit_size);
        
        this.id = -1; //read peer list and set up hosts
        readPeers(id);
        if(this.id == -1){ //If the id isn't on the list, will still be -1.
            throw new Exception("id not in list");
        } 
        
    }
    private void readCommon() throws IOException{
        try{
            List<String> configLines = Files.readAllLines(Paths.get("Common.cfg"), Charset.forName("US-ASCII"));
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
        List<String> peerLines = Files.readAllLines(Paths.get("PeerInfo.cfg"), Charset.forName("US-ASCII"));
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

            //calculate size of bitset and initialize structure
            int bit_size = (int) Math.ceil((float)fileSize / pieceSize);
            h.pieces = new BitSet(bit_size);
            if(h.hasFile){
                h.pieces.flip(0, h.pieces.length());
            }
            if(id == h.id){
                //reading current host entry
                //don't need to add to the hosts arraylist 
                this.id = id;
                this.pieces = h.pieces;
            } else {
                //not current host
                hosts.add(h);
            }
            if(PeerProcess.DEBUG){
            for (String t : tokens){
                System.out.println(t); //just checking tokenizing works
            }
            }
        }
    }
    
}
public class PeerProcess {
    public static final boolean DEBUG = false; //
    public static void main(String[] args) {

        System.out.println("Starting");
        Process p;
        try{
            p = new Process(Integer.parseInt(args[0])); //args start from 0 in java (program name not included)
            //System.out.println(p.fileName);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        System.out.println("PeerProcess Initialized");
        
    }
    
}
