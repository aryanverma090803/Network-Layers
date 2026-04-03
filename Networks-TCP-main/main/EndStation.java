package main;

public class EndStation{

    public String stationName;
    
    public EndStation(String stationName){
        this.stationName=stationName;
    }

    public void check(Data data){
        if(data.dest.equals(this.stationName)){
            System.out.println(stationName + " received data: " + data.data);
        }
        else {
            System.out.println(stationName + " ignored the frame");
        }
    }
}