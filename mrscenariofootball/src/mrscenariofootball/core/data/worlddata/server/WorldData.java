package mrscenariofootball.core.data.worlddata.server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import mrservermisc.network.xml.Helpers;

@XmlRootElement(name="WorldData")
public class WorldData {
    
    @XmlElement(name="time")
	double mPlayTime;
	@XmlElement(name="playMode")
	PlayMode mPlayMode;
	@XmlElement(name="score")
	Score mScore;

	@XmlElement(name="max_agent")
	int mMaxNumberOfAgents;

	@XmlElement(name="ball")
	BallPosition mBallPosition;

	@XmlElement(name="players")
	List<Player> mListOfPlayers;

    @XmlElement(name="flag")
    List<ReferencePoint> mReferencePoints;
	
    public WorldData(){}
    
	public WorldData(
			double aPlayTime, 
			PlayMode aPlayMode, 
			Score aScore,
			int aMaxNumberOfAgents, 
			BallPosition aBallPosition,
			List<Player> aListOfPlayers,
			List<ReferencePoint> aReferencePoints ) {
		
		mPlayTime = aPlayTime;
		mPlayMode = aPlayMode;
		mScore = aScore;
		mMaxNumberOfAgents = aMaxNumberOfAgents;
		mBallPosition = aBallPosition;
		mListOfPlayers = aListOfPlayers;
		mReferencePoints = aReferencePoints;
		
	}	
	
	public WorldData( WorldData aWorldData ) {
		
		mPlayTime = aWorldData.getPlayTime();
		mPlayMode = getPlayMode();
		mScore = new Score( aWorldData.getScore() );
		mMaxNumberOfAgents = aWorldData.getMaxNumberOfAgents();
		mBallPosition = new BallPosition( aWorldData.getBallPosition() );
		mListOfPlayers = new ArrayList<Player>( aWorldData.getListOfPlayers().size() + 10 );
		for( Player vPlayer : aWorldData.getListOfPlayers() ){
			
			mListOfPlayers.add( new Player( vPlayer ) );
			
		}
		
		mReferencePoints =  new ArrayList<ReferencePoint>( aWorldData.getReferencePoints().size() );
		for( ReferencePoint vReferencePoint : aWorldData.getReferencePoints() ){
			
			mReferencePoints.add( new ReferencePoint( vReferencePoint ) );
			
		}
		
	}

	public String toXMLString(){
		
		return Helpers.marshallXMLString( this, WorldData.class );
		
	}

	public static WorldData unmarshallXMLPositionDataPackageString( String aXMLWorldData ){
			
		return Helpers.unmarshallXMLString( aXMLWorldData, WorldData.class );
		
	}
	
	@Override
	public String toString() {
		return "WorldData [mPlayTime=" + mPlayTime + ", mPlayMode=" + mPlayMode
				+ ", mScore=" + mScore + ", mMaxNumberOfAgents="
				+ mMaxNumberOfAgents + ", mBallPosition=" + mBallPosition
				+ ", mListOfPlayers=" + mListOfPlayers + ", mReferencePoints="
				+ mReferencePoints + "]";
	}
	
	public static void createWorldDataSchema( ){

		Helpers.createXMLSchema( "worlddataschema.xsd",
				WorldData.class,
				BallPosition.class,
				Player.class,
				PlayMode.class,
				ReferencePoint.class,
				ReferencePointName.class,
				Score.class,
				Team.class  );

	}

	@XmlTransient
	public double getPlayTime() {
		return mPlayTime;
	}

	public void setPlayTime(double aPlayTime) {
		mPlayTime = aPlayTime;
	}
	@XmlTransient
	public PlayMode getPlayMode() {
		return mPlayMode;
	}

	public void setPlayMode( PlayMode aPlayMode ) {
		mPlayMode = aPlayMode;
	}
	@XmlTransient
	public Score getScore() {
		return mScore;
	}

	public void setScore( Score aScore ) {
		mScore = aScore;
	}
	@XmlTransient
	public int getMaxNumberOfAgents() {
		return mMaxNumberOfAgents;
	}

	public void setMaxNumberOfAgents( int aMaxNumberOfAgents ) {
		mMaxNumberOfAgents = aMaxNumberOfAgents;
	}
	@XmlTransient
	public BallPosition getBallPosition() {
		return mBallPosition;
	}

	public void setBallPosition( BallPosition aBallPosition ) {
		mBallPosition = aBallPosition;
	}
	@XmlTransient
	public List<Player> getListOfPlayers() {
		return mListOfPlayers;
	}

	public void setListOfPlayers( List<Player> aListOfPlayers ) {
		mListOfPlayers = aListOfPlayers;
	}
	@XmlTransient
	public List<ReferencePoint> getReferencePoints() {
		return mReferencePoints;
	}

	public void setReferencePoints( List<ReferencePoint> aReferencePoints ) {
		mReferencePoints = aReferencePoints;
	}

	public WorldData copy() {
		
		return  new WorldData( this );
		
	}
	
}
