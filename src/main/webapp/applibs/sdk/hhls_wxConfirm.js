/// <reference path="../plugins/jQuery/jQuery-2.1.3.min.js" />
/// <reference path="json.js" />
/// <reference path="baiduTpls.js" />
/// <reference path="hhls.js" />

hhls.WxConfirm = function (aMsg, aOnOk) {
    var me = hhls;
    try {
        var aHtml = "";
        aHtml += "<div id='dbg_WxConfirm' class=\"modal modal-warning fade\" style='z-index:999999999; '>";
        aHtml += "    <div class=\"modal-dialog\">";
        aHtml += "        <div class=\"modal-content\">";
        aHtml += "            <div class=\"modal-header\">";
        aHtml += "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">";
        aHtml += "                    <span aria-hidden=\"true\">×</span></button>";
        aHtml += "                <h4 class=\"modal-title\">提示</h4>";
        aHtml += "            </div>";
        aHtml += "            <div class=\"modal-body\">";
        aHtml += "                <p id=\"dlgWxConfirmMsg\"></p>";
        aHtml += "            </div>";
        aHtml += "            <div class=\"modal-footer\">";
        aHtml += "                <button type=\"button\" class=\"btn btn-warning btn-outline pull-left\" data-dismiss=\"modal\">";
        aHtml += "                    取消</button>";
        aHtml += "                <button type=\"button\" class=\"btn btn-info btn-outline\" id=\"btnWxConfirmOK\">";
        aHtml += "                    确定</button>";
        aHtml += "            </div>";
        aHtml += "        </div>";
        aHtml += "    </div>";
        aHtml += "</div>";
        aHtml += "";
        hhls.GetModalDlg("#dbg_WxConfirm", aHtml, function () {
            $("#dlgWxConfirmMsg").text(aMsg);
            $("#btnWxConfirmOK").unbind('click').bind('click', function () {
                $("#dbg_WxConfirm").modal('toggle');
                hhls.callBack(aOnOk);
            });
        }).modal('show');
    }
    catch (cer) { ; }
}

hhls.WxAlert = function (aMsg , aCallback) {
    var me = hhls;
    try {
        var aHtml = "";
        aHtml += "<div id='dbg_Wxalert' class=\"modal modal-info fade\" style='z-index:999999999; '>";
        aHtml += "    <div class=\"modal-dialog\">";
        aHtml += "        <div class=\"modal-content\">";
        aHtml += "            <div class=\"modal-header\">";
        aHtml += "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">";
        aHtml += "                    <span aria-hidden=\"true\">×</span></button>";
        aHtml += "                <h4 class=\"modal-title\">提示</h4>";
        aHtml += "            </div>";
        aHtml += "            <div class=\"modal-body\">";
        aHtml += "                <p id=\"dlgWxalert\"></p>";
        aHtml += "            </div>";
        aHtml += "            <div class=\"modal-footer\">"; 
        aHtml += "                <button type=\"button\" class=\"btn btn-info btn-outline\"  id=\"btnWxOK\">";
        aHtml += "                    确定</button>";
        aHtml += "            </div>";
        aHtml += "        </div>";
        aHtml += "    </div>";
        aHtml += "</div>";
        aHtml += "";
        hhls.GetModalDlg("#dbg_Wxalert", aHtml, function () {
            $("#dlgWxalert").text(aMsg);
            $("#btnWxOK").unbind('click').bind('click', function () {
                $("#dbg_Wxalert").modal('toggle');
                hhls.callBack(aCallback);
            });
        }).modal('show');
    }
    catch (cer) { ; }
}
