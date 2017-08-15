/// <reference path="../jQuery-2.1.3.min.js" />
/// <reference path="../json.js" />
/// <reference path="../jQuery.md5.js" />
/// <reference path="../base64.js" />
/// <reference path="../baiduTpls.js" />
/// <reference path="../date.js" />
/// <reference path="../hhls.js" />

var WmsV50 = {
    Configs: {
        WmsAcUrl: "http://media.hhuc-service.com:8086/TWmsV50Http?Version=WmsV50"
    },
    AcCall: function (aAc, aPs, aCallback) {
        var me = WmsV50;
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
                    var aObj = hhls.getJsonObj(text);
                    hhls.callBack(aCallback, aObj);
                }
                catch (Ex) { ; }
            });
        }
        catch (e) { ; }
    },
    AcCall_CreateSession: function (aSolutionCode, aCode, aCaption, aDesc, aAutoRec, aCallback) {
        var me = WmsV50;
        try {
            var aPs = {
                SolutionCode: aSolutionCode,
                Code: aCode,
                Caption: aCaption,
                Desc: aDesc,
                AutoRec: aAutoRec
            };
            var aAc = "AcCreateSession";
            WmsV50.AcCall(aAc, aPs, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_CreatePusher: function (aSrcStreamName, aCode, aCaption, aDesc, aDstHost, aDstPort, aDstAppName, aDstStreamName, aCallback) {
        var me = WmsV50;
        try {
            var aPs = {
                SrcStreamName: aSrcStreamName,
                Code: aCode,
                Caption: aCaption,
                Desc: aDesc,
                DstHost: aDstHost,
                DstPort: aDstPort,
                DstAppName: aDstAppName,
                DstStreamName: aDstStreamName
            };
            var aAc = "AcCreatePusher";
            WmsV50.AcCall(aAc, aPs, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StartRecord: function (aStreamName, aCallback) {
        var me = WmsV50;
        try {
            WmsV50.AcCall("AcStartRecord", { StreamName: aStreamName }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StopRecord: function (aStreamName, aCallback) {
        var me = WmsV50;
        try {
            WmsV50.AcCall("AcStopRecord", { StreamName: aStreamName }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StartPush: function (aPusherGuid, aCallback) {
        var me = WmsV50;
        try {
            WmsV50.AcCall("AcStartPush", { PusherGuid: aPusherGuid }, aCallback);
        }
        catch (e) { ; }
    },
    AcCall_StopPush: function (aPusherGuid, aCallback) {
        var me = WmsV50;
        try {
            WmsV50.AcCall("AcStopPush", { PusherGuid: aPusherGuid }, aCallback);
        }
        catch (e) { ; }
    }
}
