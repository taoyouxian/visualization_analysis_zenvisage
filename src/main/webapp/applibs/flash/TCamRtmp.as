package applibs.flash  {
	import flash.display.Sprite;
	import flash.events.StatusEvent;
	import flash.media.Camera;
	import flash.media.Microphone;
	import flash.media.Video;
	import flash.external.ExternalInterface;
	import flash.media.H264Profile;    
    import flash.media.H264VideoStreamSettings;   
	
	import com.adobe.serialization.json.JSON;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.events.NetStatusEvent;
	import flash.media.H264Level;
	import com.adobe.serialization.json.JSONDecoder;

	public class TCamRtmp extends Sprite{
		
		var mInfo:TCamRtmpInfo = new TCamRtmpInfo();
		var mCam:Camera;
		var mMic:Microphone;
		var mPreVideo:Video;
		var mPushConn:NetConnection;
		var mPushStream:NetStream;
		public function TCamRtmp() {
			this.doInit();
		}
		public function doInit(){
			try{
				 
				this.PublishFunction2Js();
				//this.doStartPreview();				
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		public function doStartPreview(){
			try{  
				this.mCam = Camera.getCamera( this.mInfo.getCameraName());
				if( this.mCam!=null){
					this.mCam.addEventListener(StatusEvent.STATUS, OnCamStatusEvents);
					this.mCam.setMode( this.mInfo.camW , this.mInfo.camH, this.mInfo.camFs, false);
					this.mCam.setKeyFrameInterval( this.mInfo.camKeyFrameIntervalint);
					this.mCam.setQuality(this.mInfo.camBitrate, this.mInfo.camQuality);
					this.mMic = Microphone.getMicrophone( );
					if( this.mMic!=null){						
						this.mMic.rate = this.mInfo.micRate;
						this.mMic.setSilenceLevel(this.mInfo.micSilenceLevel);
					}
					else{
						this.mInfo.MicState="None";
						this.doNotifyJs();
					}
					this.mPreVideo = new Video( this.stage.stageWidth , this.stage.stageHeight);
					this.mPreVideo.attachCamera( this.mCam);
					this.addChild( this.mPreVideo); 
				}
				else{
					this.mInfo.CameraState="None";
					this.doNotifyJs();
					this.Debug("Camera error!");
				}
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		public function doStopPreview(){
			try{
				
			}
			catch(e:Error){
				this.Debug(e);
			}
		} 
		public function doStartPushByPs(aPsJson){
			try{
				this.Debug(aPsJson);
				this.mInfo = com.adobe.serialization.json.JSON.decode( aPsJson , false);
				this.Debug(aPsJson);
				
				this.doStartPush( this.mInfo.RtmpSvr , this.mInfo.RtmpStreamName);
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		public function doStartPush(aRtmpSvr:String , aRtmpStreamName:String){
			try{
				this.mInfo.RtmpSvr = aRtmpSvr;
				this.mInfo.RtmpStreamName = aRtmpStreamName;
				
				if( this.mPushConn!=null && this.mPushConn.connected){
					this.mPushStream = new NetStream( this.mPushConn);
					this.mPushStream.addEventListener(StatusEvent.STATUS , OnPushStreamChanged);
					this.mPushStream.attachCamera( this.mCam);
					this.mPushStream.attachAudio(this.mMic);
					
					var h264setting:H264VideoStreamSettings = new H264VideoStreamSettings();    
            		h264setting.setProfileLevel(H264Profile.BASELINE, flash.media.H264Level.LEVEL_2); 
					h264setting.setQuality( this.mInfo.camBitrate , this.mInfo.camQuality);
					h264setting.setKeyFrameInterval(this.mInfo.camKeyFrameIntervalint);
					h264setting.setMode( this.mInfo.camW , this.mInfo.camH , this.mInfo.camFs);
					
					this.mPushStream.videoStreamSettings = h264setting;
					this.mPushStream.bufferTime=0;
					this.mPushStream.publish( this.mInfo.RtmpStreamName , "live");
					
					
					//this.Debug("Push:"+ com.adobe.serialization.json.JSON.encode(this.mInfo));
				}
				else{
					this.doFreeStream();
					this.mPushConn = new NetConnection();
					this.mPushConn.addEventListener(NetStatusEvent.NET_STATUS, OnConnectChanged);
					this.mPushConn.client = this;
					this.mPushConn.connect( this.mInfo.RtmpSvr);
				}
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		public function doStopPush(){
			try{
				this.doFreeStream();
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		public function doRegistJsCallback( aJsFunctionName :String){
			try{
				this.mInfo.JsCallback = aJsFunctionName;
				//this.doUpdateStatus2Js();
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		public function doRegistJsNotify( aJsFunctionName :String){
			try{
				this.mInfo.JsNofity = aJsFunctionName;
				//this.doUpdateStatus2Js();
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		
		public  function doNotifyJs():void {
			try{
				//this.Debug("Notify");
				
				if(ExternalInterface.available && this.mInfo.JsNofity!="") { 
					var aState:String = this.mInfo.getState();
        			ExternalInterface.call(this.mInfo.JsNofity, aState); 
      			} 
				
			}
			catch(e:Error){;}
		} 
		
		private function PublishFunction2Js( ){
			try{
				ExternalInterface.addCallback("doStartPreview", doStartPreview);
				ExternalInterface.addCallback("doStopPreview", doStopPreview);
				ExternalInterface.addCallback("doStartPush", doStartPush);
				ExternalInterface.addCallback("doStartPushByPs", doStartPushByPs);
				ExternalInterface.addCallback("doStopPush", doStopPush);
				ExternalInterface.addCallback("doRegistJsCallback", doRegistJsCallback);
				ExternalInterface.addCallback("doRegistJsNotify", doRegistJsNotify);
				ExternalInterface.addCallback("doNotifyJs", doNotifyJs);
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		
		private function OnCamStatusEvents(event:StatusEvent):void {
			try{
				this.mInfo.CameraState = event.code;
				this.doNotifyJs();
				//this.Debug(event.toString());
			}
			catch(e:Error){;}
		}
		private function OnConnectChanged( event:NetStatusEvent):void {
			try{
				this.mInfo.RtmpConnectState = event.info.code;
				this.doNotifyJs();
				//this.Debug("ConnState:"+event.info.code);
				if(event.info.code == "NetConnection.Connect.Success"){  
					this.doStartPush( this.mInfo.RtmpSvr , this.mInfo.RtmpStreamName);
            	}  
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		private function OnPushStreamChanged( event:NetStatusEvent):void {
			try{				
				this.mInfo.RtmpStreamState = event.info.code;
				this.doNotifyJs();
				this.Debug("PushState:"+event.info.code);
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		private function CallJs(aMsg:String){
			try{
				
				//var aStr = this.mInfo.JsCallback+"('"+ aMsg+"')";				
				if(ExternalInterface.available && this.mInfo.JsCallback!="") { 
        			ExternalInterface.call(this.mInfo.JsCallback, aMsg); 
      			} 
			}
			catch(e:Error){;}
		}
		private function Debug(aMsg:String){
			try{
				var aStr = "Debug:"+aMsg;
				trace( aStr);
				this.CallJs(aStr);
			}
			catch(e:Error){
				;
			}
		}
		private function doFreeStream( ){
			try{ 
				if( this.mPushStream!=null){
					this.mPushStream.attachCamera(null);
					this.mPushStream.attachAudio(null);												 
					this.mPushStream.publish(null);
					this.mPushStream.close();
					this.mPushStream = null;
				}
			}
			catch(e:Error){
				;
			}
			try{ 
				if( this.mPushConn!=null){
					this.mPushConn.close();
					this.mPushConn = null;
				}
			}
			catch(e:Error){
				;
			}
		}
		
		
		
		
		
		var cam:Camera;
		var mic:Microphone;
		var pushConn:NetConnection;
		var pushStream:NetStream;		
		var vid:Video;
		var w:int=640;
		var h:int=480;
		var aData:String ="";
		 
		
	}
	
}
