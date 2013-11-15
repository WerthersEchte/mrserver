package mrscenariofootball.core.managements;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import mrscenariofootball.core.ScenarioCore;
import mrscenariofootball.core.data.worlddata.server.Player;
import mrscenariofootball.core.data.worlddata.server.WorldData;
import mrservermisc.network.data.position.PositionDataPackage;
import mrservermisc.network.data.position.PositionObject;
import mrservermisc.network.data.position.PositionObjectBot;

import org.apache.logging.log4j.Level;

public class FromVision extends Thread {
	
	private static final BlockingQueue<PositionDataPackage> NEWPOSITIONDATA = new ArrayBlockingQueue<PositionDataPackage>( 5, true );
		
	public static boolean putPositionDatPackageInProcessingQueue( PositionDataPackage aPositionData ){
	
		ScenarioCore.getLogger().trace( "Putting positiondata in queue: " + aPositionData.toString() + " " );
		return NEWPOSITIONDATA.offer( aPositionData );
		
	}
	private AtomicBoolean mManagePositionDataFromVision = new AtomicBoolean( false );
	
	public FromVision( ) {}
	
	public void close(){
		
		stopManagement();
		
	}

	public boolean startManagement() {
		
		ScenarioCore.getLogger().debug( "Starting process packets from vision" );
		
		if( !isAlive() ) {
			
			super.start();
			mManagePositionDataFromVision.set( true);
			ScenarioCore.getLogger().info( "Started processing packets from vision" );
            
		} else {
		    
			ScenarioCore.getLogger().debug( "Packets from vision are already processed" );
			
		}
		
		return mManagePositionDataFromVision.get();
		
	}
	
	@Override
	public void start(){
		
		this.startManagement();
		
	}
	
	public void stopManagement(){
		 
	    mManagePositionDataFromVision.set( false );
	    
	    if( isAlive() ){
	      
            while( isAlive() ){ 
            	
                try {
                	
                    Thread.sleep( 10 );
                    
                } catch ( InterruptedException vInterruptedException ) {
                	
                	ScenarioCore.getLogger().error( "Error stopping fromvisionmanagement: " + vInterruptedException.getLocalizedMessage() );
                	ScenarioCore.getLogger().catching( Level.ERROR, vInterruptedException );
                    
                } 
            }
            
	    }
	    
	    ScenarioCore.getLogger().info( "Fromvisionmanagement stopped." );
	    		
	}
	
	@Override
	public void run(){

		PositionDataPackage vPositionData;
		WorldData vWorldata;
		
		while( mManagePositionDataFromVision.get() ){
              
			try {

				vPositionData = NEWPOSITIONDATA.poll( 100, TimeUnit.MILLISECONDS );
				if( vPositionData != null ){
						
					vWorldata = ScenarioCore.getInstance().getScenarioInformation().getWorldData().copy();
					vWorldata.getListOfPlayers().clear();
					
					for( PositionObject vObject : vPositionData.mListOfObjects ){
						
						if( vObject instanceof PositionObjectBot ){
						
							vWorldata.getListOfPlayers().add( new Player( ( PositionObjectBot ) vObject , ScenarioCore.getInstance().getBotAIs().get( vObject.getId() ) ) );
							
						}
						
					}
					
					ScenarioCore.getInstance().getScenarioInformation().setPlayers( vWorldata.getListOfPlayers() );
					ToBotAIs.putWorldDatainSendingQueue( vWorldata );
					ToGraphics.putWorldDatainSendingQueue( vWorldata );
					
					
				}
								
			} catch ( Exception vException ) {
				
				ScenarioCore.getLogger().error( "Error processing object from vision: " + vException.getLocalizedMessage() );
				ScenarioCore.getLogger().catching( Level.ERROR, vException );
                				
			}
			
		}
		
	}
	
}
