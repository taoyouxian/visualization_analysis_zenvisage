package applibs.flash  {
	import flash.display.Sprite;
	
 import com.adobe.serialization.json.JSON;
	//import com.adobe.serialization.json.*;;
	//import com.adobe.serialization.json.JSONEncoder;
	//import com.adobe.serialization.json.JSONDecoder;
	import flash.external.ExternalInterface;
	import com.adobe.serialization.json.JSON;

	//import com.adobe.protocols.dict.util.CompleteResponseEvent;

	public class TPushCamV20Imp  extends Sprite{
		var PsJson:String="";
		public function TPushCamV20Imp() {
			// constructor code
			
			var aInfo :TPushCamV20VideoInfo = new TPushCamV20VideoInfo();
			aInfo.Width=640;
			aInfo.Height=480;
		//	var aEnc = new com.adobe.serialization.json.JSONEncoder( aInfo);
			var aStr = JSON.stringify( aInfo);
			trace( aStr);
			
var a:*=  JSON.decode( aStr, true);
		}
		
		function readPs():void{
			this.PsJson = this.stage.loaderInfo.parameters.Json;
			
		}
		
		function callJs( aJs :String, aPs:String){
				
				if(ExternalInterface.available) { 
        			ExternalInterface.call(aJs, aPs); 
      			} 
				
		}
		
		

	}
	
		 
	
}
