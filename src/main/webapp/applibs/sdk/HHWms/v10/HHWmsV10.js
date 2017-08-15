/// <reference path="../../jQuery-2.1.3.min.js" />
/// <reference path="../../json.js" />
/// <reference path="../../jQuery.md5.js" />
/// <reference path="../../base64.js" />
/// <reference path="../../baiduTpls.js" />
/// <reference path="../../date.js" />
/// <reference path="../../hhls.js" />
var HHWms = {
    Configs: {
        WmsAcUrl: "http://media.hhuc-service.com:8086/TWmsHttpInf?Version=HHWms.V10"  
    },
    AcCall: function (aAc, aPs, aCallback) {
        var me = HHWms;
        try {
            var aUrl = me.Configs.WmsAcUrl;
            aUrl += "&Ac=" + aAc;
            for (var p in aPs) {
                aUrl += "&" + p + "=" + aPs[p];
            }
            $.ajaxSetup({
                contentType: "application/x-www-form-urlencoded; charset=utf-8"
            });
            $.post(aUrl, {}, function (text, status, jxh) {
                try {
                    try {
                        var aObj = eval('(' + text + ')');
                        if (aCallback != null) {
                            aCallback(aObj);
                        }
                    }
                    catch (Exc) { ; }
                }
                catch (Ex) { ; }
            });
        }
        catch (e) { ; }
    },
    AcCall_CreateSession: function (aOrgCode, aSolutionCode, aCode, aCaption, aDesc, aAutoRec, aCallback) {
        var me = HHWms;
        try {
            var aPs = {
                OrgCode: aOrgCode,
                SolutionCode: aSolutionCode,
                Code: aCode,
                Caption: aCaption,
                Desc: aDesc,
                AutoRec: aAutoRec 
            };
            var aAc = "AcCreateSession";
            HHWms.AcCall(aAc, aPs, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_CreatePusher: function (aSrcStreamName, aCode, aCaption, aDesc, aDstHost, aDstPort, aDstAppName, aDstStreamName, aCallback) {
        var me = HHWms;
        try {
            var aPs = {
                StreamName: aSrcStreamName,
                Code: aCode,
                Caption: aCaption,
                Desc: aDesc,
                DstHost: aDstHost,
                DstPort: aDstPort,
                DstAppName: aDstAppName,
                DstStreamName: aDstStreamName
            };
            var aAc = "AcCreatePusher";
            HHWms.AcCall(aAc, aPs, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StartRecord: function (aStreamName, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcStartRecord", { StreamName: aStreamName }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StopRecord: function (aStreamName, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcStopRecord", { StreamName: aStreamName }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StartPush: function (aPusherGuid, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcStartPush", { PushGuid: aPusherGuid }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StopPush: function (aPusherGuid, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcStopPush", { PushGuid: aPusherGuid }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_GetSessions: function (aOrgCode, aSolutionCode, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcGetSessions", { OrgCode: aOrgCode, SolutionCode: aSolutionCode }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_GetLiveSessions: function (aOrgCode, aSolutionCode, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcGetLiveSessions", { OrgCode: aOrgCode, SolutionCode: aSolutionCode }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_GetSessionInfo: function (aStreamName, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcGetSessionInfo", { StreamName: aStreamName }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_CreateLiveThumb: function (aStreamName, aSize, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcCreateLiveThumb", { StreamName: aStreamName, Size: aSize }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_CreateVodThumb: function (aStreamName, aSize, aDelayTime, aCallback) {
        var me = HHWms;
        try {
            HHWms.AcCall("AcCreateLiveThumb", { StreamName: aStreamName, Size: aSize, DelayTime: aDelayTime }, aCallback);
        }
        catch (e) { ; }
    }
} 