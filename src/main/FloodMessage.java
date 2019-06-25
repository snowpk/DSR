package main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class FloodMessage implements Serializable {

    private static final long serialVersionUID = -980171906680017029L;
    
    // 1 = RREQ, 2 = RREP
    private int type;
    private long timestamp;
    private String message;
    //Source of the RREQ
    InetAddress source;
    //Destination of the RREQ
    InetAddress destination;
    //Route from Source to Destination, including both.
    ArrayList<InetAddress> route;
    //Only for RREP, Index along the route list in backwards order
    int index;

    public FloodMessage(long timestamp, String message, int type, InetAddress dest, InetAddress src) {
        this.timestamp = timestamp;
        this.message = message;
        this.type = type;
        this.destination = dest;
        this.source = src;
        this.index = 0;
        this.route = new ArrayList<InetAddress>();
        
    }
    
    public void addToRoute(InetAddress node) {
    	this.route.add(node);
    }
    
    public void setIndex(int index) {
    	this.index = index;
    }
    
    public InetAddress getSource() {
    	return this.source;
    }
    
    public int getIndex() {
    	return this.index;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public InetAddress getDestination() {
		return destination;
	}

	public void setDestination(InetAddress destination) {
		this.destination = destination;
	}

	public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream os = new ObjectOutputStream(bos)) {
            os.writeObject(this);
        }
        return bos.toByteArray();
    }

    @Override
    public String toString() {
        return "FloodMessage [timestamp=" + timestamp + ", message=" + message + ", type=" + type + ", destination=" + destination.toString() + "]";
    }
}
