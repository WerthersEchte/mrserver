package de.fh_kiel.robotics.mr.server.core.botai.network.receive;

import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;

import de.fh_kiel.robotics.mr.server.core.botai.BotAIManagement;
import de.fh_kiel.robotics.mr.server.core.botai.network.BotAiHost;

public class Receiver extends Thread {

	private final BotAiHost mBotAIConnect;
	
	private AtomicBoolean mManageMessagesfromBotAI = new AtomicBoolean( false );
	private AtomicBoolean mSuspend = new AtomicBoolean( false );
	private AtomicBoolean mIsSuspended = new AtomicBoolean( false );

	public Receiver( BotAiHost aBotAIConnect ) {

		mBotAIConnect = aBotAIConnect;

	}
	
	public void close(){
		
		stopManagement();
		mBotAIConnect.closeConnection();
		
	}

	public boolean startManagement() {
		
		BotAIManagement.getLogger().debug( "Starting to receive and process packets from botais" );
		
		if( !isAlive() ) {
			
			super.start();
			mManageMessagesfromBotAI.set( true);
			BotAIManagement.getLogger().info( "Started processing packets from botais on port {}(Team {})", mBotAIConnect.getPort(), mBotAIConnect.getTeam() );
            
		} else {
		    
			BotAIManagement.getLogger().debug( "Packets from botais are already being received and processed" );
			
		}
		
		return mManageMessagesfromBotAI.get();
		
	}
	
	@Override
	public void start(){
		
		this.startManagement();
		
	}
	
	public void stopManagement(){
		 
	    mManageMessagesfromBotAI.set( false );
	    
	    if( isAlive()){
	      
            while( isAlive() ){ 
            	
                try {
                	
                    Thread.sleep( 10 );
                    
                } catch ( InterruptedException vInterruptedException ) {
                	
                	BotAIManagement.getLogger().error( "Error stopping botaireceiver: {}", vInterruptedException.getLocalizedMessage() );
                    BotAIManagement.getLogger().catching( Level.ERROR, vInterruptedException );
                    
                } 
            }
            
	    }
	    
	    BotAIManagement.getLogger().info( "Botaireceiver on Port {}(Team {}) stopped.", mBotAIConnect.getPort(), mBotAIConnect.getTeam() );
	    		
	}

	public synchronized boolean suspendManagement() {
		
		BotAIManagement.getLogger().debug( "Suspending processing packets from botais" ) ;
		return suspendManagement( true );
		
	}

	public synchronized boolean resumeManagement() {
		
		BotAIManagement.getLogger().debug( "Resumeing processing packets from botais" ) ;
		return suspendManagement( false );
		
	}

	private boolean suspendManagement( boolean aSuspend ) {
		mSuspend.set( aSuspend );
		while( mIsSuspended.get() != aSuspend ){ try { this.wait( 1 ); } catch ( InterruptedException vInterruptedException ) { BotAIManagement.getLogger().error( "Error suspending botaireciever: {}", vInterruptedException.getLocalizedMessage() ); BotAIManagement.getLogger().catching( Level.ERROR, vInterruptedException ); } }
		return mIsSuspended.get();
	}
	
	@Override
	public void run(){
	    
		while( mManageMessagesfromBotAI.get() ){
            
			if( mSuspend.get() ){ synchronized(this){ mIsSuspended.set( true ); } }
            while( mSuspend.get() ){ try { this.wait( 10 ); } catch ( InterruptedException vInterruptedException ) { BotAIManagement.getLogger().error( "Error suspending botaireceiver: {}", vInterruptedException.getLocalizedMessage() ); BotAIManagement.getLogger().catching( Level.ERROR, vInterruptedException ); } }
			mIsSuspended.compareAndSet(true, false);
            
			try {
				
				if( mBotAIConnect.isConnected() ) {
					
					Worker.putWorkerInPool( new Worker( mBotAIConnect, mBotAIConnect.getDatagrammPacket( 100 ) ) );
					
				} else {
				    
					BotAIManagement.getLogger().debug( "Botais are not connected" ) ;
					
				}
				
			} catch ( SocketTimeoutException vSocketTimeoutException ) {
                
                BotAIManagement.getLogger().trace( "Received no datagramm from botais in 100ms" );
                                
			} catch ( Exception vException ) {
				
				BotAIManagement.getLogger().error( "Error receiving messages from botais: {}", vException.getLocalizedMessage() );
				BotAIManagement.getLogger().catching( Level.ERROR, vException );
                				
			}
			
		}
		
	}

	@Override
	public String toString() {
		return "Receiver [mBotAIConnect=" + mBotAIConnect + "]";
	}

	public BotAiHost getBotAIConnect() {
		return mBotAIConnect;
	}
	
}
