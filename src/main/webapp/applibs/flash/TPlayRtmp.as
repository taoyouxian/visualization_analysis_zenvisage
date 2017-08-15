package applibs.flash  {
	import flash.display.Sprite;
	import flash.events.StatusEvent;
	import flash.media.Camera;
	import flash.media.Microphone;
	import flash.media.Video;
	import flash.external.ExternalInterface;
	import flash.media.H264Profile;    
    import flash.media.H264VideoStreamSettings;   
	import flash.media.SoundTransform;
	
	import com.adobe.serialization.json.JSON;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.events.NetStatusEvent;
	import flash.media.H264Level;
	import com.adobe.serialization.json.JSONDecoder;

	
	public class TPlayRtmp extends Sprite{
		var nc:NetConnection;  
        var ns:NetStream;  
        var video:Video;  
		var RtmpUrl:String="";
		var StreamName:String="";
		var JsCallback:String="alert";
		var PlayAudio =1;
		public function TPlayRtmp() {
			// constructor code
			this.PublishFunction2Js();
		}
		
		public function doPlay( aRtmpUrl:String , aStreamName:String, aPlayAudio:int):void{
			try{
				this.doStop();
				this.RtmpUrl = aRtmpUrl;
				this.StreamName = aStreamName;
				this.PlayAudio = aPlayAudio;
            	nc = new NetConnection();  
           		nc.addEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);  
            	nc.connect( this.RtmpUrl );  
			}
			catch(e:Error){;}
		}
		public function doStop( ):void{
			try{
				if( this.ns!=null){
					try{
						this.ns.attach(null);
						this.ns.close();
						this.ns = null;
					}
					catch(Ex:Error){;}
				}
				if( this.nc!=null){
					try{
						if(this.nc.connected){
							this.nc.close();
						}
						this.nc = null;
					}
					catch(Ex:Error){;}
				}
			}
			catch(e:Error){;}
		}
		public function doSetJsCallback( aJsFunctionName ):void{
			try{
				this.JsCallback = aJsFunctionName;
			}
			catch(e:Error){;}
		}
		
		private function PublishFunction2Js( ){
			try{
				ExternalInterface.addCallback("doPlay", doPlay);
				ExternalInterface.addCallback("doStop", doStop); 
				ExternalInterface.addCallback("doSetJsCallback", doSetJsCallback); 
			}
			catch(e:Error){
				this.Debug(e);
			}
		}
		private function CallJs(aMsg:String){
			try{
				
				//var aStr = this.mInfo.JsCallback+"('"+ aMsg+"')";				
				if(ExternalInterface.available && this.JsCallback!="") { 
        			ExternalInterface.call(this.JsCallback, aMsg); 
      			} 
			}
			catch(e:Error){;}
		}
		private function doVideo(nc:NetConnection):void {  
            ns = new NetStream(nc);  
            ns.addEventListener(NetStatusEvent.NET_STATUS, netStatusHandler);  
  			if( this.PlayAudio!=1){
				var transform:SoundTransform = ns.soundTransform;
   				transform.volume = 0.0;
   				ns.soundTransform = transform;
			}
  
            video = new Video(this.stage.stageWidth , this.stage.stageHeight);  
            video.attachNetStream(ns);    
            addChild(video);  
			
            ns.play(this.StreamName);  
        } 
		private function netStatusHandler(event:NetStatusEvent):void{
			try{ 
				trace("event.info.level: " + event.info.level + "\n", "event.info.code: " + event.info.code);  
				this.CallJs( event.info.code);
            	switch (event.info.code)  
            	{  
                	case "NetConnection.Connect.Success":  
                    	doVideo(nc);  
                   		break;  
                	case "NetConnection.Connect.Failed":  
                    	break;  
                	case "NetConnection.Connect.Rejected":  
                    	break;  
                	case "NetStream.Play.Stop":  
                    	break;  
                	case "NetStream.Play.StreamNotFound":  
                    	break;  
            	}  				
			}
			catch(e:Error){;}			
		}
	}
	
}
