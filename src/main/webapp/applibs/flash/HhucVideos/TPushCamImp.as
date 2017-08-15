package applibs.flash.HhucVideos  {
	import flash.display.Sprite;
	import flash.external.ExternalInterface;
	import flash.media.Camera;
	import flash.media.Microphone;
	import flash.media.Video;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.events.NetStatusEvent;
	import flash.media.H264VideoStreamSettings;
	import flash.media.H264Profile;
	import flash.media.H264Level;
	
	public class TPushCamImp extends Sprite{
		public var State:TPushCamState= new TPushCamState();
		public var VideoPs:TPushCamVideoPs = new TPushCamVideoPs();
		public var PushPs:TPushCamPushPs = new TPushCamPushPs();
		
		var Cam :Camera;
		var Mic :Microphone;
		var PreviewVideo:Video;
		var Conn:NetConnection;
		var Stream:NetStream;
		
		public function TPushCamImp() {
			this.PublishFuctions(); 
			// constructor code
			this.LoadPs();
			this.doPreview();
				var aResult:TJsCallbackResult = new TJsCallbackResult();
				aResult.Ac = "OnPushStart";
				aResult.Owner = "TPushCamImp";
				aResult.Data = "OnPushStart";
				aResult.SubData = "OnPushStart";
				var aStateJson = JSON.stringify( aResult);
				this.CallJs( this.State.JsCallback , aStateJson);
		}
		
		public function doStartPush( aRtmp:String , aStreamName:String):void{
			try{
				this.PushPs.Rtmp = aRtmp;
				this.PushPs.StreamName = aStreamName; 
				this.doTryConnect();
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}			
		}
		public function doStopPush(  ){
			try{
				this.doClose();
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}			
		}
		public function doPreview(  ){
			try{
				this.Cam = this.getCam();
				this.Mic = this.getMic();
				this.PreviewVideo = this.getPreviewVideo();
				this.PreviewVideo.attachCamera( this.Cam);				
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}			
		}
		
		function getCam():Camera{
			try{
				this.Cam = Camera.getCamera();
				this.Cam.setMode( this.VideoPs.W , this.VideoPs.H , this.VideoPs.FPS);
				this.Cam.setKeyFrameInterval( this.VideoPs.KeyFrame);
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
			return this.Cam;
		}
		
		function getMic():Microphone{
			try{
				this.Mic = Microphone.getMicrophone();
				this.Mic.encodeQuality = 10;
				this.Mic.rate = 44;
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
			return this.Mic;
		}
		function getPreviewVideo():Video{
			try{
				this.PreviewVideo = new Video( stage.stageWidth , stage.stageHeight); 
				this.addChild( this.PreviewVideo);
				
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
			return this.PreviewVideo;
		}
		
		function doTryConnect():void{
			try{
				this.doClose();
				this.Conn = new NetConnection();
				this.Conn.addEventListener(NetStatusEvent.NET_STATUS , OnStateChanged);
				this.Conn.connect( this.PushPs.Rtmp);
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		
		function doPush():void{
			try{
				this.Cam = this.getCam();
				this.Cam.setMode( this.VideoPs.W , this.VideoPs.H , this.VideoPs.FPS);
				this.Cam.setKeyFrameInterval( this.VideoPs.KeyFrame);
				this.Cam.setQuality( 0 , this.VideoPs.Quality);
				
				
				var h264setting:H264VideoStreamSettings = new H264VideoStreamSettings();
				h264setting.setProfileLevel( flash.media.H264Profile.BASELINE , flash.media.H264Level.LEVEL_4);
				h264setting.setMode( this.VideoPs.W , this.VideoPs.H , this.VideoPs.FPS);
				h264setting.setKeyFrameInterval( this.VideoPs.KeyFrame); 
				
				
				this.Mic = this.getMic();
				
				this.Stream = new NetStream( this.Conn);
				this.Stream.addEventListener(NetStatusEvent.NET_STATUS , OnStateChanged);
				this.Stream.videoStreamSettings = h264setting;
				this.Stream.attachCamera( this.Cam);
				this.Stream.attachAudio(this.Mic);
				this.Stream.bufferTime =this.VideoPs.BufTime;
				this.Stream.publish( this.PushPs.StreamName , "live");
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		
		function OnStateChanged(event:NetStatusEvent):void{
			try{
				this.ShowLog( event.info.code.toString());
				if( event.info.code== "NetConnection.Connect.Success"){
					this.doPush();
				}
				else if( event.info.code== "NetStream.Publish.Start"){
					this.State.IsPushing = 1;
					this.doOnStateChenged();
				}
				else if( event.info.code== "NetConnection.Connect.Closed"){
					this.State.IsPushing = 0;
					this.doOnStateChenged();
				}
				
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		
		function doOnStateChenged():void{
			try{
				var aResult:TJsCallbackResult = new TJsCallbackResult();
				aResult.Ac = "PushStateChanged";
				aResult.Owner = "TPushCamImp";
				aResult.Data = this.State.IsPushing+"";
				aResult.SubData = "IsPushing";
				var aStateJson = JSON.stringify( aResult);
				this.CallJs( this.State.JsCallback , aStateJson);
			}
			catch(e:Error){
				this.ShowLog( e.message);
			} 
		}
		
		function doClose():void{
			try{
				if( this.Conn!=null){
					this.Conn.close();
				}
				this.Conn = null;
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
			try{
				if( this.Stream!=null){
					this.Stream.attachCamera(null);
					this.Stream.attachAudio(null);
					this.Stream.close();
				}
				this.Stream= null;
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		
		function LoadPs():void{
			try{ 
				
				this.VideoPs.W = int( this.stage.loaderInfo.parameters["width"]);
				this.VideoPs.H = int( this.stage.loaderInfo.parameters["height"]);
				this.VideoPs.FPS = int( this.stage.loaderInfo.parameters["fps"]);
				this.VideoPs.KeyFrame = int( this.stage.loaderInfo.parameters["keyframe"]); 
				this.VideoPs.Quality = int( this.stage.loaderInfo.parameters["quality"]);
				this.VideoPs.BufTime = int( this.stage.loaderInfo.parameters["buftime"]); 
				this.State.JsCallback = this.stage.loaderInfo.parameters["jscallback"]; 
				var aLog = JSON.stringify( this.VideoPs); 
				this.ShowLog(aLog);
				trace("load ps success");
			}
			catch(e:Error){
				this.ShowLog("E");
				this.ShowLog( e.message);
			}
		} 
		
		function PublishFuctions( ){
			try{
				ExternalInterface.addCallback("doStartPush", doStartPush);
				ExternalInterface.addCallback("doStopPush", doStopPush); 
				ExternalInterface.addCallback("doCallJs", CallJs); 
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		
		function CallJs( aJs:String , aPs:String) :void{
			try{
				if( flash.external.ExternalInterface.available ){
					flash.external.ExternalInterface.call( aJs , aPs);
				}
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		function ShowLog( aLog:String):void{ 
			try{
				CallJs( this.State.JsCallback , aLog);
				trace(aLog);
			}
			catch(e:Error){
				CallJs( 'alert' , e.message);
			}
		}
	}
	
}
