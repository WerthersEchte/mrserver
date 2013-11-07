package mrserver.core.scenario;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mrserver.core.Core;
import mrservermisc.botcontrol.interfaces.BotControl;
import mrservermisc.bots.interfaces.Bot;
import mrservermisc.graphics.interfaces.Graphics;
import mrservermisc.scenario.interfaces.Scenario;
import mrservermisc.vision.interfaces.Vision;

/**
 * Managed das Szenario des Servers
 * 
 * @author Eike Petersen
 * @since 0.1
 * @version 0.1
 */
public class ScenarioManagement {
	
    private static ScenarioManagement INSTANCE;

    private ScenarioManagement(){
    	
    }
    
    public static ScenarioManagement getInstance() {
        
        if( ScenarioManagement.INSTANCE == null){
        	ScenarioManagement.getLogger().debug( "Creating ScenarioStarter-instance." );
            ScenarioManagement.INSTANCE = new ScenarioManagement();
        }

        ScenarioManagement.getLogger().trace( "Retrieving ScenarioStarter-instance." );
        return ScenarioManagement.INSTANCE;
        
    }
    
    private static Logger SCENARIOMANAGEMENTLOGGER = LogManager.getLogger("SCENARIOMANAGEMENT");
    
    public static Logger getLogger(){
        
        return SCENARIOMANAGEMENTLOGGER;
        
    }

	private Scenario mScenario;
    
    
    public void close(){
    	
    	if( mScenario != null ){
    		mScenario.close();
    		mScenario = null;
    	}
    	INSTANCE = null;
    	
    }
    
	/**
	 * Lädt ein Szenario
	 * 
	 * @return true wenn das Laden erfolgreich war, false wenn nicht
	 */
	public boolean loadScenario() {
           
        try {

        	ScenarioManagement.getLogger().trace( "Loading scenario " + Core.getInstance().getServerConfig().getScenarioClass() + " from " + Core.getInstance().getServerConfig().getScenarioLibrary() );
            URL vUniformResourceLocator = new File( Core.getInstance().getServerConfig().getScenarioLibrary() ).toURI().toURL();
            @SuppressWarnings("resource")
			URLClassLoader vClassloader = new URLClassLoader( new URL[]{ vUniformResourceLocator } );
            synchronized (this) {
                
            	mScenario = (Scenario) vClassloader.loadClass( Core.getInstance().getServerConfig().getScenarioClass() ).newInstance();
                
            }
            ScenarioManagement.getLogger().info( "Loaded scenario " + Core.getInstance().getServerConfig().getScenarioClass() + " from " + Core.getInstance().getServerConfig().getScenarioLibrary() );
           
        } catch ( Exception vException ) {
            
        	ScenarioManagement.getLogger().error( "Error loading AI " +Core.getInstance().getServerConfig().getScenarioClass() + " from " + Core.getInstance().getServerConfig().getScenarioLibrary() + " " + vException.getLocalizedMessage() );
        	ScenarioManagement.getLogger().catching( Level.ERROR, vException );
            
            return false;
            
        }
        
        return true;
        
    }
	
	/**
	 * Schließt und verwirft das momentane Scenario
	 * 
	 */
	public void disposeScenario(){
		
		if( mScenario != null ){
			
			mScenario.close();
			mScenario = null;
			
		}
		
	}
	
	public boolean needsVision(){

		if( mScenario != null ){
			
			return mScenario.needVision();
			
		}
		
		return false;
		
	}
	
	public boolean needsBotControl(){

		if( mScenario != null ){
			
			return mScenario.needBotControl();
			
		}
		
		return false;
		
	}
    
	public boolean registerVision( Vision aVision ){

		if( mScenario != null ){
			
			return mScenario.registerVision( aVision );
			
		}
		
		return false;
		
	}
    
	public boolean registerBotControl( BotControl aBotControl ){

		if( mScenario != null ){
			
			return mScenario.registerBotControl( aBotControl );
			
		}
		
		return false;
		
	}
    
	public boolean registerBotList( List<Bot> aBotList ){

		if( mScenario != null ){
			
			return mScenario.registerBotList( aBotList );
			
		}
		
		return false;
		
	}
    
	public boolean registerGraphics( Graphics aGraphics ){

		if( mScenario != null ){
			
			return mScenario.registerGraphics( aGraphics );
			
		}
		
		return false;
		
	}

}