/// <reference path="../plugins/jQuery/jQuery-2.1.3.min.js" />
/// <reference path="json.js" />
/// <reference path="baiduTpls.js" />
/// <reference path="hhls.js" />

/*
    WebSocket 服务
*/
var WsClient = {
    wsUri: "ws://127.0.0.1:17011",
    wsAppKey: "FBVideo",
    wsRegistGroups: [],
    wsOnReceiveMsg: null,
    websocket: null,
    wsSessionID: "",
    OnOpen: function (evt) {
        var me = WsClient;
        try { }
        catch (e) { ; }
    },
    OnClose: function (evt) {
        var me = WsClient;
        try { }
        catch (e) { ; }
    },
    OnMsg: function (evt) {
        var me = WsClient;
        try {
            var aRecAction = hhls.getJsonObj(evt.data);
            if (aRecAction.Action == "OnConnected") {
                me.wsSessionID = aRecAction.SessionID;
                var aAction = new TWsAction();
                aAction.Action = "Regist";
                aAction.SrcSessionID = aRecAction.SessionID;
                aAction.TargetKey = me.wsAppKey;
                //var aGroups = [hhls.getUrlParam("VideoID")];
                var aGroups = me.wsRegistGroups;
                aAction.Datas = hhls.getObjJson(aGroups);
                var aJson = hhls.getObjJson(aAction);
                me.websocket.send(aJson);
            }
            else {
                hhls.callBack(me.wsOnReceiveMsg, aRecAction);
            }
            //alert(aAction.Action + "," + aAction.SessionID);
        }
        catch (e) { ; }
    },
    OnError: function (evt) {
        var me = WsClient;
        try { }
        catch (e) { ; }
    },
    doStart: function () {
        var me = WsClient;
        try {
            me.websocket = new WebSocket(me.wsUri);
            me.websocket.onopen = me.OnOpen;
            me.websocket.onclose = me.OnClose;
            me.websocket.onmessage = me.OnMsg;
            me.websocket.onerror = me.OnError;
        }
        catch (e) { ; }
    },
    doSendMsg2App: function (aAppKey, aMsg) {
        var me = WsClient;
        try {
            var aAction = {
                Action: "Send2App",
                SrcSessionID: me.wsSessionID,
                TargetKey: aAppKey,
                Datas: "",
                MsgDatas: aMsg
            };
            var aJson = hhls.getObjJson(aAction);
            me.websocket.send(aJson);
        }
        catch (e) { ; }
    },
    doSendMsg2Groups: function (aAppKey, aGroupKey, aMsg) {
        var me = WsClient;
        try {
            var aAction = {
                Action: "Send2Group",
                SrcSessionID: me.wsSessionID,
                TargetKey: aAppKey,
                Datas: aGroupKey,
                MsgDatas: aMsg
            };
            var aJson = hhls.getObjJson(aAction);
            me.websocket.send(aJson);
        }
        catch (e) { ; }
    },
    doSendMsg2Session: function (aAppKey, aSessionID, aMsg) {
        var me = WsClient;
        try {
            var aAction = {
                Action: "Send2Group",
                SrcSessionID: me.wsSessionID,
                TargetKey: aAppKey,
                Datas: aSessionID,
                MsgDatas: aMsg
            };
            var aJson = hhls.getObjJson(aAction);
            me.websocket.send(aJson);
        }
        catch (e) { ; }
    },
    doInit: function (aUrl, aAppKey, aRegistGroupKeys, aOnRecMsg) {
        var me = WsClient;
        try {
            me.wsUri = aUrl;
            me.wsAppKey = aAppKey;
            me.wsRegistGroups = aRegistGroupKeys;
            me.wsOnReceiveMsg = aOnRecMsg;
            me.doStart();
        }
        catch (e) { ; }
    }
};

function TWsAction() {
    this.Action = "";
    this.SrcSessionID = "";
    this.TargetKey = "";
    this.Datas = "";
    this.MsgDatas = "";
}