import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
public class TrackerClient extends WebSocketClient {

	public TrackerClient( URI serverUri , Draft draft ) {
		super( serverUri, draft );
	}

	public TrackerClient( URI serverURI ) {
		super( serverURI );
	}

	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		System.out.println( "opened connection" );
		this.send("0,0,0,0,0,0,0,0,0");
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage( String message ) {
		System.out.println( "received: " + message );
	}

	@Override
	public void onFragment( Framedata fragment ) {
		System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
	}

	@Override
	public void onClose( int code, String reason, boolean remote ) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
	}

	@Override
	public void onError( Exception ex ) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	public static void main( String[] args ) throws URISyntaxException, UnknownHostException, IOException, InterruptedException {
		Socket echoSocket = new Socket("172.16.97.26", 1999);
	    PrintWriter out =
	        new PrintWriter(echoSocket.getOutputStream(), true);
	    while (true){
	    	out.println("1,0,0,0,0,0,0,0,0");
	    	Thread.sleep(200);
	    }
	    
//		TrackerClient c = new TrackerClient( new URI( ":1999" ), new Draft_17() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
//		c.connect();
//		
//		
//		System.out.println(c.isOpen());
		//c.send("Yo");
	}

}