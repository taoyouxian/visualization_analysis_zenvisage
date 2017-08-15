package applibs.flash.HhucVideos  {
	import flash.display.Sprite;
	import flash.net.NetConnection;
	import flash.events.NetStatusEvent;
	import flash.net.NetStream;
	import flash.external.ExternalInterface;
	import flash.media.Video;
	 
	
	
	public class TPlayImp extends Sprite { 
		var Rtmp:String ="";
		var StreamName:String ="";
		var BufTime=0;
		var State:int=0;
		var JsCallback:String ="";
		var Conn:NetConnection;
		var Stream:NetStream;
		var PlayVideo:Video;
		var PlayState:TPlayState = new TPlayState();
		public function TPlayImp() {
			// constructor code
			this.LoadPs();
			this.PublishFuctions();
				
				var aResult:TJsCallbackResult = new TJsCallbackResult();
				aResult.Ac = "OnPlayStart";
				aResult.Owner = "TPlayImp";
				aResult.Data = "OnPlayStart";
				aResult.SubData = "PlayState";
				var aStateJson = JSON.stringify( aResult);
			
			//var aRtmp:String="rtmp://media.hhuc-service.com:1935/hhucv30vod";
			//var aStreamName:String="aaa";
			//this.doStartPlay(aRtmp , aStreamName);
			
		}
		public function doStartPlay(aRtmp:String, aStreamName:String):void {
			try{ 
				
				this.doStopPlay();
				this.Rtmp = aRtmp;
				this.StreamName = aStreamName;
				this.Conn = new NetConnection();
				this.Conn.addEventListener(NetStatusEvent.NET_STATUS , OnStateChanged);
				this.Conn.connect( this.Rtmp);
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		public function doStopPlay( ):void {
			try{
				this.doClose();
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		public function doPause( ):void {
			try{
				this.Stream.pause();
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		public function doResume( ):void {
			try{
				this.Stream.resume();
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		function OnStateChanged(event:NetStatusEvent):void{
			try{
				this.ShowLog( event.info.code.toString()); 
				if( event.info.code== "NetConnection.Connect.Success"){
					this.doPlay();
				}
				else if( event.info.code== "NetStream.Buffer.Full"){
					this.State = 1;
					this.doOnStateChenged();
				}
				else if( event.info.code== "NetStream.Buffer.Flush"){
					this.State = 2;
					this.doOnStateChenged();
				}				
				else if( event.info.code== "NetConnection.Connect.Closed"){
					this.State = 0;
					this.doOnStateChenged();
				}				
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}		
		function doOnStateChenged():void{
			try{
				/*
				this.PlayState.JsCallback = this.JsCallback;
				this.PlayState.IsPlaying = this.State;
				var aStateJson = JSON.stringify( this.PlayState);
				this.CallJs( this.JsCallback , aStateJson);
				*/
				
				var aResult:TJsCallbackResult = new TJsCallbackResult();
				aResult.Ac = "PlayStateChanged";
				aResult.Owner = "TPlayImp";
				aResult.Data = this.State+"";
				aResult.SubData = "PlayState";
				var aStateJson = JSON.stringify( aResult);
				this.CallJs( this.JsCallback , aStateJson);
			}
			catch(e:Error){
				this.ShowLog( e.message);
			} 
		}		
		function doClose():void{
			try{
				if( this.Stream!=null){
					this.Stream.close();
					this.Stream = null;
				} 
			}
			catch(e:Error){
				this.ShowLog( e.message);
			} 
			try{ 
				if( this.Conn!=null){
					this.Conn.close();
					this.Conn = null;
				}
			}
			catch(e:Error){
				this.ShowLog( e.message);
			} 
		}
		function doPlay():void{
			try{
				this.Stream = new NetStream( this.Conn );
				this.Stream.addEventListener(NetStatusEvent.NET_STATUS , OnStateChanged);
				this.Stream.bufferTime = this.BufTime;
				this.PlayVideo = new Video( this.stage.stageWidth , this.stage.stageHeight);
				this.PlayVideo.attachNetStream( this.Stream);
				this.addChild( this.PlayVideo);
				this.Stream.play( this.StreamName);
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		}
		
		function LoadPs():void{
			try{ 
				this.JsCallback = this.stage.loaderInfo.parameters["jscallback"]; 
				this.BufTime = int(this.stage.loaderInfo.parameters["buftime"]); 
				this.ShowLog(JsCallback);
				trace("load ps success");
			}
			catch(e:Error){
				this.ShowLog( e.message);
			}
		} 		
		function PublishFuctions( ){
			try{
				ExternalInterface.addCallback("doStartPlay", doStartPlay);
				ExternalInterface.addCallback("doStopPlay", doStopPlay); 
				ExternalInterface.addCallback("doPause", doPause);
				ExternalInterface.addCallback("doResume", doResume); 
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
				trace(aLog);
			}
			catch(e:Error){
				CallJs( 'alert' , e.message);
			}
		}
	}
	
}
