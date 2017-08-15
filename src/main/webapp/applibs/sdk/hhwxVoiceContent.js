/// <reference path="../plugins/jQuery/jQuery-2.1.3.min.js" />
/// <reference path="json.js" />
/// <reference path="baiduTpls.js" />
/// <reference path="hhls.js" />

/*
    微信语音识别组件
*/


/*
使用示例


    hhwxVoiceContent.GetVoiceContent(function (aDatas) {
        if (aDatas.ResultState == 1) {
            alert(aDatas.LocalVoiceID + ':' + aDatas.Content);
        }
    });


*/

var hhwxVoiceContent = {
    Html: '	    <div class="modal fade" id="dlg_VoiceContent" style="z-index:99999999;">' +
                    '	        <div class="modal-dialog">	' +
                    '	            <div class="modal-content">	' +
                    '	                <div class="modal-header">	' +
                    '	                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">	' +
                    '	                        <span aria-hidden="true">&times;</span></button>	' +
                    '	                    <h4 class="modal-title">	' +
                    '	                        语音识别</h4>	' +
                    '	                </div>	' +
                    '	                <div class="modal-body">	' +
                    '	                    <textarea id="txtVoiceContent" style="width:100%; padding:5px ; margin:10px 0px" rows="5"></textarea>	' +
                    '	                    <button id="btnVoiceContentStart" onclick="hhwxVoiceContent.OnStart()" type="button" class="btn btn-primary btn-lg btn-block">	' +
                    '	                        <i class="fa fa-microphone"></i> 开始录音	' +
                    '	                    </button>	' +
                    '	                    <button id="btnVoiceContentStop" onclick="hhwxVoiceContent.OnStop()" type="button" class="btn btn-warning btn-lg btn-block" style="display:none">	' +
                    '	                        <i class="fa fa-microphone-slash"></i> 停止录音	' +
                    '	                    </button>	' +
                    '	                </div>	' +
                    '	                <div class="modal-footer clearfix">	' +
                    '	                    <button type="button" class="btn btn-warning pull-left" id="btnVoiceContent_Cancel" data-dismiss="modal" id="btnVoiceContent_Cancel">	' +
                    '	                        取消</button>	' +
                    '	                    <button type="button" class="btn btn-primary pull-right" id="btnVoiceContent_OK"  onclick="hhwxVoiceContent.OnOK()">	' +
                    '	                        确定</button>	' +
                    '	                </div>	' +
                    '	            </div>	' +
                    '	        </div>	' +
                    '	    </div>	',
    Datas: {
        ResultState: 0,
        Content: "",
        LocalVoiceID: ""
    },
    Objs: {
        IsRecording: false,
        Dlg: null,
        btnStart: null,
        btnStop: null,
        btnOk: null,
        btnCancel: null,
        txtContent: null,
        Callback: null
    },
    GetVoiceContent: function (aCallback) {
        var me = hhwxVoiceContent;
        try {

            hhls.clearElement("#dlg_VoiceContent");
            $("#dlg_VoiceContent").remove();

            me.Objs.Dlg = $(me.Html);
            me.Datas = { ResultState: 0, Content: "", LocalVoiceID: "" };
            me.Objs.IsRecording = false;
            me.Objs.Callback = aCallback;
            me.ShowBtn();
            me.Objs.Dlg.on('hidden.bs.modal', function (e) {
                me.OnClose();
            })
            me.Objs.Dlg.modal('show');
        }
        catch (cer) {; }
    },
    ShowBtn: function () {
        var me = hhwxVoiceContent;
        try {
            var btnStart = $("#btnVoiceContentStart");
            var btnStop = $("#btnVoiceContentStop");
            if (me.Objs.IsRecording) {
                btnStart.css('display', "none");
                btnStop.css('display', "");
            }
            else {
                btnStart.css('display', "");
                btnStop.css('display', "none");
            }

            var msg = "Start:" + btnStart.css('display') + ";" + "Stop:" + btnStop.css('display');
            //                    $("#txtVoiceContent").val(msg);
        }
        catch (cer) {; }
    },
    OnEndRecord: function (aId) {
        var me = hhwxVoiceContent;
        try {
            wx.translateVoice({
                localId: aId, // 需要识别的音频的本地Id，由录音相关接口获得
                isShowProgressTips: 1, // 默认为1，显示进度提示
                success: function (res) {
                    me.Objs.IsRecording = false;
                    me.Datas.LocalVoiceID = aId;
                    me.ShowBtn();
                    var aTest = res.translateResult;
                    aTest.replace("。", "");
                    $("#txtVoiceContent").val(aTest); // 语音识别的结果
                }
            });
        }
        catch (cer) {; }
    },
    OnStart: function () {
        var me = hhwxVoiceContent;
        try {
            wx.startRecord({
                success: function (ares) {
                    //alert(hhls.getObjJson(ares));
                    me.Objs.IsRecording = true;
                    me.ShowBtn();
                }
            });
            wx.onVoiceRecordEnd({
                // 录音时间超过一分钟没有停止的时候会执行 complete 回调
                complete: function (res) {
                    var localId = res.localId;
                    me.OnEndRecord(localId);
                }
            });
        }
        catch (cer) {; }
    },
    OnStop: function () {
        var me = hhwxVoiceContent;
        try {
            wx.stopRecord({
                success: function (res) {
                    var localId = res.localId;
                    me.OnEndRecord(localId);
                }
            });
        }
        catch (cer) {; }
    },
    OnOK: function () {
        var me = hhwxVoiceContent;
        try {
            me.Datas.Content = $("#txtVoiceContent").val();
            if (me.Objs.Callback) {
                try {
                    me.Datas.ResultState = 1;
                    me.Objs.Callback(me.Datas);
                    me.Objs.Dlg.modal('hide');
                }
                catch (cer) {; }
            }
        }
        catch (cer) {; }
    },
    OnClose: function () {
        var me = hhwxVoiceContent;
        try {
            wx.stopRecord({
                success: function (res) {
                    var localId = res.localId;
                }
            });
        }
        catch (cer) {; }
    }
}
