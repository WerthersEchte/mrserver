package de.fh_kiel.robotics.mr.server.core.botai.network.receive;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;

import de.fh_kiel.robotics.mr.server.core.botai.BotAIManagement;
import de.fh_kiel.robotics.mr.server.core.botai.data.BotAI;
import de.fh_kiel.robotics.mr.server.core.botai.network.data.UnkownBotAI;

public class Creator extends Thread {
	
private static final BlockingQueue<UnkownBotAI> UNKOWNSENDERDATAGRAMS = new ArrayBlockingQueue<UnkownBotAI>( 25, true );
	
	public static void putUnkownSenderDatagramInProcessingQueue( UnkownBotAI aUnkownBotAI ){
		
		try {
			BotAIManagement.getLogger().trace( "Putting datagram in unkown sender queue: {}", aUnkownBotAI.toString() + " " );
			UNKOWNSENDERDATAGRAMS.put( aUnkownBotAI );
			
		} catch ( InterruptedException vInterruptedException ) {

			BotAIManagement.getLogger().error( "Error processing unkownbotai: {}", vInterruptedException.getLocalizedMessage() );
			BotAIManagement.getLogger().catching( Level.ERROR, vInterruptedException );
            
		} 
		
	}
	private AtomicBoolean mManageMessagesfromBotAIs = new AtomicBoolean( false );
	public Creator( ) {

	}
	
	public void close(){
		
		stopManagement();
		
	}

	public boolean startManagement() {
		
		BotAIManagement.getLogger().debug( "Starting to process unknown botais" );
		
		if( !isAlive() ) {
			
			super.start();
			mManageMessagesfromBotAIs.set( true);
			BotAIManagement.getLogger().info( "Started processing unknown botais" );
            
		} else {
		    
			BotAIManagement.getLogger().debug( "Unknown botais are already beingprocessed" );
			
		}
		
		return mManageMessagesfromBotAIs.get();
		
	}
	
	@Override
	public void start(){
		
		this.startManagement();
		
	}
	
	public void stopManagement(){
		 
	    mManageMessagesfromBotAIs.set( false );
	    
	    if( isAlive()){
	      
            while( isAlive() ){ 
            	
                try {
                	
                    Thread.sleep( 10 );
                    
                } catch ( InterruptedException vInterruptedException ) {
                	
                	BotAIManagement.getLogger().error( "Error stopping botai-creator: {}", vInterruptedException.getLocalizedMessage() );
                	BotAIManagement.getLogger().catching( Level.ERROR, vInterruptedException );
                    
                } 
            }
            
            UNKOWNSENDERDATAGRAMS.clear();
            
	    }
	    
	    BotAIManagement.getLogger().info( "Botai-creator stopped." );
	    		
	}
	
	@Override
	public void run(){
	    
		BotAI vBotAI;
		UnkownBotAI vUnkownBotAI;
		
		while( mManageMessagesfromBotAIs.get() ){
            
			try {

				vUnkownBotAI = UNKOWNSENDERDATAGRAMS.poll( 100, TimeUnit.MILLISECONDS );
				if( vUnkownBotAI != null ){
						
					if( !BotAIManagement.getInstance().getMapOfBotAIs().containsKey( vUnkownBotAI.getRecievedDatagramPacket().getSocketAddress() ) ){
					
						vBotAI = new BotAI( vUnkownBotAI );
						
						if( vBotAI.connectionRequest( vUnkownBotAI.getRecievedDatagramPacket() ) ){
							
							BotAIManagement.getInstance().putNewAI( vBotAI );
							BotAIManagement.getLogger().info( "New botai connected: {}", (InetSocketAddress) vUnkownBotAI.getRecievedDatagramPacket().getSocketAddress() );
							
						}
					
					} else {
						
						BotAIManagement.getInstance().getMapOfBotAIs().get( vUnkownBotAI.getRecievedDatagramPacket().getSocketAddress() ).processDatagrammPacket( vUnkownBotAI.getRecievedDatagramPacket() );
						
					}
					
				}
				
			} catch ( Exception vException ) {
				
				BotAIManagement.getLogger().error( "Error processing unkown botais: {}", vException.getLocalizedMessage() );
				BotAIManagement.getLogger().catching( Level.ERROR, vException );
                				
			}
			
		}
		
	}

}
