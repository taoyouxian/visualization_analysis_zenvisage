!function(a) {
a.hash || (a.hash = function(b, c) {
function d(a) {
return "string" == typeof a || "[object String]" === Object.prototype.toString.call(a)
}
var e, f, g, h;
if (d(b) && "" != b) return e = new RegExp("(;" + b + "=[^;]*)|(\\b" + b + "=[^;]*&)|(\\b" + b + "=[^;]*)", "ig"),
f = new RegExp(";*\\b" + b + "=[^;]*", "i"),
"undefined" == typeof c ? (g = location.hash.match(f), g ? decodeURIComponent(a.trim(g[0].split("=")[1])) : null) : (null === c ? location.hash = location.hash.replace(e, "") : (c += "", h = location.hash.replace(e, ""), h += ";" + b + "=" + encodeURIComponent(c), location.hash = h), void 0)

})

} (jQuery);