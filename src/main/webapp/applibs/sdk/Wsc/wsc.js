/// <reference path="../plugins/jQuery/jQuery-2.1.3.min.js" />
/// <reference path="json.js" />
/// <reference path="baiduTpls.js" />
/// <reference path="hhls.js" />

function TWsClient() {
    this.wsSocket = null;
    this.WsUrl = "";
    this.WsProxyUrl = "";
    this.WsMsgRootUrl = "";
    this.WsSessionID = "";
    this.ClientEvent = {};
}
function TWsAction() {
    this.Ac = "";
    this.Tags = [];
    this.SessionIDs = [];
    this.MsgData = "";
}
function doCreateWsClient(aWsUrl, aWsProxyUrl, aWsMsgRootUrl, aOnOpen, aOnClose, aOnRecData) {
    var Wsc = new TWsClient();
    Wsc.WsUrl = aWsUrl;
    Wsc.WsProxyUrl = aWsProxyUrl;
    Wsc.WsMsgRootUrl = aWsMsgRootUrl;

    Wsc.ClientEvent.OnOpenned = aOnOpen;
    Wsc.ClientEvent.OnClosed = aOnClose;
    Wsc.ClientEvent.OnRecData = aOnRecData;

    Wsc.wsSocket = new WebSocket(Wsc.WsUrl);

    Wsc.wsSocket.onopen = function (e) {
        try {

        }
        catch (e) { ; }
    };
    Wsc.wsSocket.onclose = function (e) {
        try {
            hhls.callBack(Wsc.ClientEvent.OnClosed, Wsc);
            Wsc.WsSessionID = "";
        }
        catch (e) { ; }
    };
    Wsc.wsSocket.onerror = function (e) {
        try {
            Wsc.WsSessionID = "";
            if (Wsc.wsSocket != null) {
                Wsc.wsSocket.close();
            }
        }
        catch (e) { ; }
    };
    Wsc.wsSocket.onmessage = function (e) {
        try {
            var aObj = hhls.getJsonObj(e.data);
            if (aObj.Flag == "WscV10" && aObj.Ac == "OnClientConnected") {
                Wsc.WsSessionID = aObj.SessionID;
                hhls.callBack(Wsc.ClientEvent.OnOpenned, Wsc);
            }
            else if (aObj.Flag == "WscV10" && aObj.Ac == "SendMsg") {
                var aMsgID = aObj.MsgID;
                var aUrl = Wsc.WsProxyUrl.replace(".ashx?Flag=WscProxy.V10", "_GetMsg.ashx") + "?Flag=WscProxy.V10.GetMsg&ReqSessionID=" + aMsgID;
                $.post(aUrl, function (aResMsg, b, c) {
//                    var aContent = hhls.getJsonObj(hhls.getJsonObj(aResMsg).MsgData);
//                    if (aContent == null) {
//                        aContent = hhls.getJsonObj(aResMsg);
                    //                    }
                    var aContent =  hhls.getJsonObj(aResMsg).MsgData ;
                    hhls.callBack(Wsc.ClientEvent.OnRecData, { Sender: Wsc, MsgData: aContent });
                });
            }
        }
        catch (e) { ; }
    };
    /*
        Ac结构
        public class TWscV10Action
        {
            public static string AcRegist = "AcRegist";
            public static string AcUnRegist = "AcUnRegist";
            public static string AcSendMsg2Tags = "AcSendMsg2Tags";
            public static string AcSendMsg2Sessions = "AcSendMsg2Sessions";
            public string Ac = "";
            public List<string> Tags = new List<string>();
            public List<string> SessionIDs = new List<string>();
            public string MsgData = "";
        }
    */
    Wsc.doSendAction = function (aAction) {
        try {
            var aDatas = hhls.getObjJson(aAction);
            var aUrl = Wsc.WsProxyUrl;
            if (aUrl.indexOf("?Flag=WscProxy.V10") < 0) {
                aUrl += "?Flag=WscProxy.V10";
            }
            //aUrl += "&Datas=" + aDatas;
            //Ac.acHttpGet(aUrl, function (aRes) { });

            $.post(aUrl,{Datas: aDatas}, function (a, b, c) {
                var m = a;
            });
        }
        catch (e) { ; }
    };
    Wsc.doSendActionByParas = function (aAc, aTags, aSessionIDs, aMsgData) {
        try {
            var aAction = new TWsAction();
            aAction.Ac = aAc;
            aAction.Tags = aTags;
            aAction.SessionIDs = aSessionIDs;
            aAction.MsgData = aMsgData;
            Wsc.doSendAction(aAction);
        }
        catch (e) { ; }
    };
    Wsc.doRegistTags = function (aTags) {
        try {
            Wsc.doSendActionByParas("AcRegist", aTags, [Wsc.WsSessionID], "");
        }
        catch (e) { ; }
    };
    Wsc.doRegistUnTags = function (aTags) {
        try {
            Wsc.doSendActionByParas("UnAcRegist", aTags, [Wsc.WsSessionID], "");
        }
        catch (e) { ; }
    };
    Wsc.doSend2Tags = function (aTags, aMsgData) {
        try {
            Wsc.doSendActionByParas("AcSendMsg2Tags", aTags, [], aMsgData);
        }
        catch (e) { ; }
    };
    Wsc.doSend2Sessions = function (aSessionIDs, aMsgData) {
        try {
            Wsc.doSendActionByParas("AcSendMsg2Sessions", [], aSessionIDs, aMsgData);
        }
        catch (e) { ; }
    };
    Wsc.doClose = function () {
        try {
            if (Wsc.wsSocket != null) {
                Wsc.wsSocket.close();
                Wsc.wsSocket = null;
            }
        }
        catch (e) { ; }
    };

    return Wsc;
}