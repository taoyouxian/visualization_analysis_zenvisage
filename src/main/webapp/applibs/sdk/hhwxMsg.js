/// <reference path="../plugins/jQuery/jQuery-2.1.3.min.js" />
/// <reference path="json.js" />
/// <reference path="baiduTpls.js" />
/// <reference path="hhls.js" />

/*
    微信客服消息模板
*/
var WxCsMsgTpl = {
    text: {
        "touser": "OPENID",
        "msgtype": "text",
        "text":
        {
            "content": "Hello World"
        }
    },
    image: {
        "touser": "OPENID",
        "msgtype": "image",
        "image":
        {
            "media_id": "MEDIA_ID"
        }
    },
    voice: {
        "touser": "OPENID",
        "msgtype": "voice",
        "voice":
        {
            "media_id": "MEDIA_ID"
        }
    },
    voideo: {
        "touser": "OPENID",
        "msgtype": "video",
        "video":
        {
            "media_id": "MEDIA_ID",
            "thumb_media_id": "MEDIA_ID",
            "title": "TITLE",
            "description": "DESCRIPTION"
        }
    },
    music: {
        "touser": "OPENID",
        "msgtype": "music",
        "music":
        {
            "title": "MUSIC_TITLE",
            "description": "MUSIC_DESCRIPTION",
            "musicurl": "MUSIC_URL",
            "hqmusicurl": "HQ_MUSIC_URL",
            "thumb_media_id": "THUMB_MEDIA_ID"
        }
    },
    news: {
        "touser": "OPENID",
        "msgtype": "news",
        "news": {
            "articles": [
                 {
                     "title": "Happy Day",
                     "description": "Is Really A Happy Day",
                     "url": "URL",
                     "picurl": "PIC_URL"
                 },
                 {
                     "title": "Happy Day",
                     "description": "Is Really A Happy Day",
                     "url": "URL",
                     "picurl": "PIC_URL"
                 }
            ]
        }
    }
}

var WxCsMsgTplList = [
    { Key: "text", Caption: "文本", Info: WxCsMsgTpl.text },
    { Key: "image", Caption: "图片", Info: WxCsMsgTpl.image },
    { Key: "voice", Caption: "语音", Info: WxCsMsgTpl.voice },
    { Key: "video", Caption: "视频", Info: WxCsMsgTpl.voideo },
    { Key: "music", Caption: "音乐", Info: WxCsMsgTpl.music },
    { Key: "news", Caption: "图文", Info: WxCsMsgTpl.news }
];


/*
    自定义消息模板
*/
var WxMsgTpl = {
    text: {
        touser: "{OpenID}",
        msgtype: "text",
        text:
        {
            content: "Hello World"
        }
    },
    news: {
        touser: "{OpenID}",
        msgtype: "news",
        news: {
            articles: [
                 //{
                 //    title: "Happy Day",
                 //    description: "Is Really A Happy Day",
                 //    url: "URL",
                 //    picurl: "PIC_URL"
                 //}
            ]
        }
    }
}