package mrscenariofootball.core.data.action;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import mrservermisc.network.xml.Helpers;

@XmlRootElement(name="wheel_velocities")
public class Movement extends Command{

	public static final Movement NO_MOVEMENT = new Movement( 0, 0);

	@XmlElement(name="right")
	private int mRightWheelVelocity = 0;
	@XmlElement(name="left")
	private int mLeftWheelVelocity = 0;
	
	public Movement() {}
	
	public Movement( int aRightWheelVelocity, int aLeftWheelVelocity ) {
		
		mLeftWheelVelocity = aLeftWheelVelocity;
		mRightWheelVelocity = aRightWheelVelocity;

	}
	
	public String getXMLString() {
		return "<command> <wheel_velocities> <right>" + mRightWheelVelocity + "</right> <left>" + mLeftWheelVelocity + "</left> </wheel_velocities> </command>";
	}
	
	public String toXMLString(){
		
		return Helpers.marshallXMLString( this, Movement.class );
		
	}

	public static Movement unmarshallXMLPositionDataPackageString( String aXMLMovement ){
			
		return Helpers.unmarshallXMLString( aXMLMovement, Movement.class );
		
	}

	@Override
	public String toString() {
		return "Movement [mRightWheelVelocity=" + mRightWheelVelocity
				+ ", mLeftWheelVelocity=" + mLeftWheelVelocity + "]";
	}
}
