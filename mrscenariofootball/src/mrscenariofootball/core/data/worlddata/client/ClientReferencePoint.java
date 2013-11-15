package mrscenariofootball.core.data.worlddata.client;

import javax.xml.bind.annotation.XmlElement;

import mrscenariofootball.core.ScenarioCore;
import mrscenariofootball.core.data.worlddata.server.Player;
import mrscenariofootball.core.data.worlddata.server.ReferencePoint;
import mrscenariofootball.core.data.worlddata.server.ReferencePointName;

public class ClientReferencePoint {
    
	@XmlElement(name="id")
	ReferencePointName mPointName;
	@XmlElement(name="dist")
	double mDistanceToPoint;
	@XmlElement(name="angle")
	double mAngleToPoint;
	
	public ClientReferencePoint() {}
	
	public ClientReferencePoint( ReferencePoint aReferencePoint, Player aOrigin) {

		mPointName = aReferencePoint.getPointName().getId() >= 0 ? aReferencePoint.getPointName() : null;
		mDistanceToPoint = aOrigin.getPosition().distance( aReferencePoint.getPosition() ) * ScenarioCore.getInstance().getScenarioInformation().getMaxAbsoluteValue();
		
		mAngleToPoint = Math.toDegrees( Math.atan2( aReferencePoint.getPosition().getY() - aOrigin.getPosition().getY(), 
				aReferencePoint.getPosition().getX() - aOrigin.getPosition().getX() ) ) 
				+ aOrigin.getOrientationAngle();
		
		if( mAngleToPoint > 180.0 ){
			mAngleToPoint = mAngleToPoint - 360;
		}
		if( mAngleToPoint < -180.0 ){
			mAngleToPoint = mAngleToPoint + 360;
		}
		
	}

	@Override
	public String toString() {
		return "ClientReferencePoint [mPointName=" + mPointName
				+ ", mDistanceToPoint=" + mDistanceToPoint + ", mAngleToPoint="
				+ mAngleToPoint + "]";
	}

}
