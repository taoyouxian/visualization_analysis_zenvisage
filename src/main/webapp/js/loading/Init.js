
var Init = {
    Datas: { 
    },
    Path: { 
    },
    //Toast Style
    Utility: {
        WxToast: "<div id=\"wxToast\">"
                    + "<div class=\"wx_transparent\"></div>"
                    + "<div class=\"wx-toast\">"
                        + "<div class=\"sk-spinner sk-spinner-three-bounce\">"
                            + "<div class=\"sk-bounce1\"></div>"
                            + "<div class=\"sk-bounce2\"></div>"
                            + "<div class=\"sk-bounce3\"></div>"
                        + "</div>"
                        + "<p class=\"wx-toast_content\">数据加载中</p>"
                    + "</div>"
                + "</div>",
        WebToast: "<div id=\"webToast\">"
                    + "<div class=\"web_transparent\"></div>"
                    + "<div class=\"web-toast\">"
                        + "<div class=\"sk-spinner sk-spinner-three-bounce\">"
                            + "<div class=\"sk-bounce1\"></div>"
                            + "<div class=\"sk-bounce2\"></div>"
                            + "<div class=\"sk-bounce3\"></div>"
                        + "</div>"
                        + "<p class=\"web-toast_content\">数据加载中</p>"
                    + "</div>"
                + "</div>",
        Loading: "<div class='ibox'><div class='ibox-content'><div class='sk-spinner sk-spinner-three-bounce'><div class='sk-bounce1'></div><div class='sk-bounce2'></div><div class='sk-bounce3'></div></div></div></div>",
    },
    //web Toast
    WebToast: function (aContent) {
        var me = Init;
        try {
            $("body").append(me.Utility.WebToast);
            var w = $(window).width();
            var aW = $(".web-toast").width();
            var left = (w - aW) / 2;
            $(".web-toast").css("left", left + "px");
            if (aContent != "")
                $(".web-toast_content").text(aContent);
        }
        catch (e) {; }
    },
    WxToast: function (aContent) {
        var me = Init;
        try {
            $("body").append(me.Utility.WxToast);
            var w = $(window).width();
            var aW = $(".wx-toast").width();
            var left = (w - aW) / 2;
            $(".wx-toast").css("left", left + "px");
            if (aContent != "")
                $(".wx-toast_content").text(aContent);
        }
        catch (e) {; }
    },
    //Toast
    Web_Toast: function (aContent, aTimeOut) {
        var me = Init;
        try {
            me.WebToast(aContent);
            me.ClearToast("#webToast", aTimeOut);
        }
        catch (e) {; }
    },
    Wx_Toast: function (aContent, aTimeOut) {
        var me = Init;
        try {
            me.WxToast(aContent);
            me.ClearToast("#wxToast", aTimeOut);
        }
        catch (e) {; }
    },
    //clear Toast, set time
    ClearToast: function (aElement, aTimeOut) {
        var me = Init;
        try {
            if (aElement == "") {
                aElement = "#webToast";
            }
            setTimeout(function () {
                $(aElement).remove();
            }, aTimeOut * 1000);
        }
        catch (e) {; }
    },
    //clear Toast, set time
    ClearElement: function (aElement) {
        var me = Init;
        try {
            if (aElement == null) {
                $("#webToast").remove();
            } else { 
                $(aElement).remove();
            }
        }
        catch (e) {; }
    },
}