package applibs.flash  {
	import com.adobe.serialization.json.JSON;
	
	public class TCamRtmpInfo {

		public function TCamRtmpInfo() {
			// constructor code
		}
		public var CameraState:String="None";
		public var MicState:String="None"; 
		public var RtmpConnectState:String="None"; 
		public var RtmpStreamState:String="None"; 
		
		
		public var CameraName:String="";
		public var MicIndex:int=0;		
		public var IsPushStream:int=0;
		public var RtmpSvr:String="";
		public var RtmpStreamName:String="live";		
		public var JsCallback:String="alert";		
		public var JsNofity:String="";		
		public var camW:int=640;
		public var camH:int=480;
		public var camFs:int=30;
		public var camBitrate:int=0;
		public var camQuality:int=60;
		public var camKeyFrameIntervalint=15;
		public var micRate=11;
		public var micSilenceLevel=0;
		
		public function getCameraName():String{
			var aRes= null;
			if( this.CameraName!=""){
				aRes = this.CameraName;
			}
			return aRes;
		}
		public function getMicName():int{ 			 
			return MicIndex;
		}
		public function getState():String{
			var aState:String = com.adobe.serialization.json.JSON.encode(this);
			return aState;
		}
	}
	
}
