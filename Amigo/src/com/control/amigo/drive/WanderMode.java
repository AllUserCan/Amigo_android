package com.control.amigo.drive;

public class WanderMode implements Runnable {
	private AmigoCommunication Comm;
	
	private static boolean active = false;
	private int[] sonar;
	
	private int transV=400, rotV=40, motorStopTimes=0, backTimes=0;
	private boolean forward = true, begin = true, motor = false;
	
	public WanderMode(AmigoCommunication Comm ) {
		// TODO Auto-generated constructor stub
		this.Comm = Comm;
		sonar = new int[8];
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Comm.setMaxTransVelocity(transV);
			begin=true;
			Thread.sleep(1000);
			sonar = PacketReceiver.mAmigoInfo.getSonars();
			motor = PacketReceiver.mAmigoInfo.isMotor();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while( active ){
			try {
				if( begin ){
					Comm.setTransVelocity(transV);
					Comm.setRotVelocity(0);
					Thread.sleep(300);
					sonar = PacketReceiver.mAmigoInfo.getSonars();
					motor = PacketReceiver.mAmigoInfo.isMotor();
					begin = false;
				}
				else if( motor ){
					while( sonar[2]<550 ){
						Comm.setTransVelocity(0);
						Comm.setRotVelocity((-1)*rotV);
						Thread.sleep(500);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = true;
					}
					
					while( sonar[3]<580 ){
						Comm.setTransVelocity(0);
						Comm.setRotVelocity(rotV);
						Thread.sleep(500);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = true;
						
						if( !motor ) motorStop();
					}
					
					while( sonar[1]<450 ){
						Comm.setTransVelocity(0);
						Comm.setRotVelocity((-1)*rotV);
						Thread.sleep(500);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = true;
						
						if( !motor ) motorStop();
					}
					
					while( sonar[4]<580 ){
						Comm.setTransVelocity(0);
						Comm.setRotVelocity(rotV);
						Thread.sleep(500);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = true;
												
						if( !motor ) motorStop();
					}
					
					while( sonar[0]<250 ){
						Comm.setTransVelocity(0);
						Comm.setRotVelocity((-1)*rotV);
						Thread.sleep(300);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = true;
						
						if( !motor ) motorStop();
					}
					
					while( sonar[5]<250 ){
						Comm.setTransVelocity(0);
						Comm.setRotVelocity(rotV);
						Thread.sleep(300);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = true;
						
						if( !motor ) motorStop();
					}
					
					if( forward ){
						if( !motor ) motorStop();
						Comm.setTransVelocity(transV);
						Comm.setRotVelocity(0);
						sonar = PacketReceiver.mAmigoInfo.getSonars();
						motor = PacketReceiver.mAmigoInfo.isMotor();
						forward = false;
					}
				}
				else if( !motor ){
					motorStop();
					sonar = PacketReceiver.mAmigoInfo.getSonars();
					motor = PacketReceiver.mAmigoInfo.isMotor();
					forward = true;
				}
				
				sonar = PacketReceiver.mAmigoInfo.getSonars();
				motor = PacketReceiver.mAmigoInfo.isMotor();
				
				
//				while( sonar[2]<550 ){
//					Comm.setTransVelocity(0);
//					Comm.setRotVelocity((-1)*rotV);
//					Thread.sleep(500);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//					
//					if( !PacketReceiver.mAmigoInfo.isMotor() ) motorStop();
//				}
//				
//				while( sonar[3]<550 ){
//					Comm.setTransVelocity(0);
//					Comm.setRotVelocity(rotV);
//					Thread.sleep(500);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//					
//					if( !PacketReceiver.mAmigoInfo.isMotor() ) motorStop();
//				}
//				
//				while( sonar[1]<450 ){
//					Comm.setTransVelocity(0);
//					Comm.setRotVelocity((-1)*rotV);
//					Thread.sleep(500);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//					
//					if( !PacketReceiver.mAmigoInfo.isMotor() ) motorStop();
//				}
//				
//				while( sonar[4]<450 ){
//					Comm.setTransVelocity(0);
//					Comm.setRotVelocity(rotV);
//					Thread.sleep(500);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//											
//					if( !PacketReceiver.mAmigoInfo.isMotor() ) motorStop();
//				}
//				
//				while( sonar[0]<250 ){
//					Comm.setTransVelocity(0);
//					Comm.setRotVelocity((-1)*rotV);
//					Thread.sleep(300);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//					
//					if( !PacketReceiver.mAmigoInfo.isMotor() ) motorStop();
//				}
//				
//				while( sonar[5]<250 ){
//					Comm.setTransVelocity(0);
//					Comm.setRotVelocity(rotV);
//					Thread.sleep(300);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//					
//					if( !PacketReceiver.mAmigoInfo.isMotor() ) motorStop();
//				}
//				
//				if( forward ){
//					Comm.setTransVelocity(transV);
//					Comm.setRotVelocity(0);
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = false;
//				}
//				while( !PacketReceiver.mAmigoInfo.isMotor() ){
//					motorStop();
//					sonar = PacketReceiver.mAmigoInfo.getSonars();
//					forward = true;
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void motorStop() throws Exception{
		if( motorStopTimes>=1 ){
			Comm.setTransVelocity((-1)*transV);
			Comm.setRotVelocity(randomRotate());
			Thread.sleep(1000);
			motorStopTimes = 0;
			backTimes++;
		}
		else if( sonar[6]<250 ){
			Comm.setTransVelocity(transV);
			Comm.setRotVelocity(rotV);
			Thread.sleep(500);
		}
		else if( sonar[7]<250 ){
			Comm.setTransVelocity(transV);
			Comm.setRotVelocity((-1)*rotV);
			Thread.sleep(500);
		}
		else if( backTimes==2 ){
			Comm.setTransVelocity(transV);
			Comm.setRotVelocity(0);
			Thread.sleep(500);
			backTimes = 0;
			motorStopTimes = 0;
		}
		else {
			Comm.setTransVelocity((-1)*transV);
			Comm.setRotVelocity(0);
			Thread.sleep(1000);
			motorStopTimes++;
			backTimes++;
		}
		sonar = PacketReceiver.mAmigoInfo.getSonars();
		motor = PacketReceiver.mAmigoInfo.isMotor();
	}
	
	public int randomRotate(){
		int rand = (int)(Math.random()*2+1);
		if( rand==1 ){
			return rotV;
		}
		else{
			return (-1)*rotV;
		}
	}
	
	public void endWanderMode() throws Exception{
		active = false;
		Comm.setTransVelocity(0);
		Comm.setRotVelocity(0);
	}
	
	public void startWanderMode(){
		active = true;
		new Thread(this).start();
	}

}
