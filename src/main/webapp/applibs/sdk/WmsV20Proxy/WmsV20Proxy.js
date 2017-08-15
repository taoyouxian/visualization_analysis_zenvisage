/// <reference path="../jQuery-2.1.3.min.js" />
/// <reference path="../json.js" />

/// <reference path="../baiduTpls.js" />

/// <reference path="../hhls.js" />

/// <reference path="../hhac.js" />

function TWmsV20Action() {
    this.WmsSvcUrl = "http://sys.czbtzn.cn:17006/TMoHttpInf";
    this.WmsSvr = "sys.czbyzn.cn:17005";
    this.LiveAppName = "hhucwowzav20live";
    this.Org = "HHUC";
    this.CustomApp = "HhucVideoV20";
    this.Uid = "hhuc.baeeq";
    this.Pwd = "hhuc.baeeq";
    this.Caption = "";
    this.Memo = "";
    this.Signature = "";
    this.CustomExDatas = "";
    this.CustomNotifyUrl = "";
    this.getSignature = function () {
        return "";
    }
};
var WmsV20Proxy = {
    Datas: {
        Config: {
            LiveSvrHost: "sys.czbtzn.cn",
            LiveSvrPort: "17005",
            LiveAppName: "hhucwowzav20live",
            VodSvrHost: "sys.czbtzn.cn",
            VodSvrPort: "17005",
            VodAppName: "hhucwowzav20vod",
            WmsSvcUrl: "http://sys.czbtzn.cn:17006/TMoHttpInf"
        },
        Ac: TWmsV20Action()
    },
    doCall: function (aUrl, aCallback) {
        var me = WmsV20Proxy;
        try {
            $.post(aUrl, {}, function (a, b, c) {
                var aRes = a;
                hhls.callBack(aCallback, a);
            });
        }
        catch (e) { ; }
    },
    AcCreateSession: function (aAc, aCallback) {
        var me = WmsV20Proxy;
        try {
            var aUrl = aAc.WmsSvcUrl +
                "?Ac=AcCreateSession" +
                "&LiveAppName=" + aAc.LiveAppName +
                "&Org=" + aAc.Org +
                "&CustomApp=" + aAc.CustomApp +
                "&Uid=" + aAc.Uid +
                "&Pwd=" + aAc.Pwd +
                "&Caption=" + aAc.Caption +
                "&Memo=" + aAc.Memo +
                "&Signature=" + aAc.getSignature() +
                "&CustomExDatas=" + aAc.CustomExDatas +
                "&CustomNotifyUrl=" + aAc.CustomNotifyUrl;
            me.doCall(aUrl, function (aRes) {
                try {
                    var aSession = hhls.getJsonObj(aRes);
                    hhls.callBack(aCallback, aSession);
                }
                catch (Ex) { ; }
            });
        }
        catch (e) { ; }
    }
};