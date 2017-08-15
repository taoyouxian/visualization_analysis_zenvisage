/// <reference path="../jQuery-2.1.3.min.js" />
/// <reference path="../json.js" />
/// <reference path="../jQuery.md5.js" />
/// <reference path="../base64.js" />
/// <reference path="../baiduTpls.js" />
/// <reference path="../date.js" />
/// <reference path="../hhls.js" />

var WmsV30 = {
    Datas: {
        OrgID: "",
        AppNameID: "",
        SolutionID: "",
        Url: "",
        Actions: {
            AcCreateSession: "AcCreateSession",
            AcCreatePusher: "AcCreatePusher",
            AcGetSolutionSessions: "AcGetSolutionSessions",
            AcGetLiveSessions: "AcGetLiveSessions",
            AcGetSessionInfo: "AcGetSessionInfo",
            AcGetSessionPushers: "AcGetSessionPushers",
            AcGetPusherInfo: "AcGetPusherInfo",
            AcStartRecord: "AcStartRecord",
            AcStopRecord: "AcStopRecord",
            AcStartPush: "AcStartPush",
            AcStopPush: "AcStopPush",
            AcCreateLiveThumbImg: "AcCreateLiveThumbImg",
            AcCreateVodThumbImg: "AcCreateVodThumbImg"
        }
    },
    doInit: function (aOrgId, aAppName, aSolutionId, aUrl) {
        var me = WmsV30;
        try {
            me.Datas.OrgID = aOrgId;
            me.Datas.AppNameID = aAppName;
            me.Datas.SolutionID = aSolutionId;
            me.Datas.Url = aUrl;
        }
        catch (e) { ; }
        return aUrl;
    },
    getUrl: function (aAc, aPs) {
        var me = WmsV30;
        var aUrl = me.Datas.Url;
        try { }
        catch (e) { ; }
        return aUrl;
    },
    Actions: {
        CallAction: function (aAc, aPs, aCallback) {
            var me = WmsV30;
            try {
                var aUrl = me.Datas.Url;
                aUrl += aUrl.indexOf("?") > 0 ? "&" : "?";
                aUrl += "WmsVersion=V30";
                aUrl += "&Ac=" + aAc;
                if (aPs != null) {
                    for (p in aPs) {
                        aUrl += "&" + p + "=" + aPs[p];
                    }
                }
                aUrl += "&OrgID=" + me.Datas.OrgID;
                aUrl += "&SolutionID=" + me.Datas.SolutionID;
                aUrl += "&AppName=" + me.Datas.AppName;

                $.ajaxSetup({
                    contentType: "application/x-www-form-urlencoded; charset=utf-8"
                });
                $.post(aUrl, {}, function (text, status, jxh) {
                    try {
                        var aObj = hhls.getJsonObj(text);
                        var aRes = {
                            Ac: aObj.Ac,
                            Data: aObj.Result
                        };
                        hhls.callBack(aCallback, aRes);
                    }
                    catch (Ex) { ; }
                });
            }
            catch (e) { ; }
        },
        AcGetSolutionSessions: function (aCallback) {
            var me = WmsV30;
            try {
                me.Actions.CallAction(me.Datas.Actions.AcGetSolutionSessions, {}, aCallback);
            }
            catch (e) { ; }
        },
        AcGetLiveSessions: function (aCallback) {
            var me = WmsV30;
            try {
                me.Actions.CallAction(me.Datas.Actions.AcGetLiveSessions, {}, aCallback);
            }
            catch (e) { ; }
        },
        AcCreateSession: function (aCaption, aDesc, aSessionClientID ,aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    Caption: aCaption ,
                    Desc: aDesc,
                    SessionClientID: aSessionClientID
                };
                me.Actions.CallAction(me.Datas.Actions.AcCreateSession, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcGetSessionInfo: function (aSessionID, aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID
                };
                me.Actions.CallAction(me.Datas.Actions.AcGetSessionInfo, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcCreatePusher: function (aSessionID, aPusherClientID, aDstHost, aDstPort, aDstAppName, aDstStreamName, aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID,
                    PusherClientID: aPusherClientID,
                    DstHost: aDstHost,
                    DstPort: aDstPort,
                    DstAppName: aDstAppName,
                    DstStreamName: aDstStreamName
                };
                me.Actions.CallAction(me.Datas.Actions.AcCreatePusher, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcStartPush: function (aSessionID,aPusherID, aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID,
                    PusherID: aPusherID 
                };
                me.Actions.CallAction(me.Datas.Actions.AcStartPush, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcStopPush: function (aSessionID, aPusherID, aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID,
                    PusherID: aPusherID
                };
                me.Actions.CallAction(me.Datas.Actions.AcStopPush, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcStartRecord: function (aSessionID,  aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID 
                };
                me.Actions.CallAction(me.Datas.Actions.AcStartRecord, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcStopRecord: function (aSessionID,  aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID
                };
                me.Actions.CallAction(me.Datas.Actions.AcStopRecord, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcCreateLiveThumbImg: function (aSessionID, aThumbSize, aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID ,
                    ThumbSize: aThumbSize
                };
                me.Actions.CallAction(me.Datas.Actions.AcCreateLiveThumbImg, aPs, aCallback);
            }
            catch (e) { ; }
        },
        AcCreateVodThumbImg: function (aSessionID, aThumbSize, ,aThumbDelayTime, aCallback) {
            var me = WmsV30;
            try {
                var aPs = {
                    SessionID: aSessionID ,
                    ThumbSize: aThumbSize,
                    ThumbDelayTime: aThumbDelayTime
                };
                me.Actions.CallAction(me.Datas.Actions.AcCreateVodThumbImg, aPs, aCallback);
            }
            catch (e) { ; }
        }
    }
}
