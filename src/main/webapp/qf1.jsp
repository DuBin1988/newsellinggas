<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
<title>欠费明细</title>
<link href="style/css.css" rel="stylesheet" type="text/css">
</head>
<script src="js/jquery-1.7.2.min.js"></script>
<script src="js/jquery.json-2.3.min.js"></script>
<script src='js/knockout-2.1.0.js'></script>
<script src="js/knockout.mapping-latest.js"></script>
<script src="js/knockout.validation.min.js"></script>
<script src='js/af.js'></script>
<body>
	<HEAD>
<script language="javascript" type="text/javascript "
	src="jquery.min.js"></script>
<script type="text/javascript">
	function getUrlParam(name) {
		//构造一个含有目标参数的正则表达式对象 
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		//匹配目标参数 
		var r = window.location.search.substr(1).match(reg);
		//返回参数值 
		if (r != null)
			return unescape(r[2]);
		return null;
	}
	function payload() {
		//获取参数
		var qf = getUrlParam('money');
		var zhye1 = getUrlParam('f_zhye');
		alert(qf);
		alert(zhye1);
		$("#qianfei").html(qf);
		$("#zhye").html(zhye1);
	}

	$(function() {
		var tbody = "";
		var array = getUrlParam('arr');
		alert("---" + array);
		//var list=jQuery.parseJSON(array);
		var list = JSON.parse(array);
		//  var list= array.parseJSON(); 
		// var array = eval('(' + array1 + ')');
		//------------遍历对象 .each的使用-------------  
		//对象语法JSON数据格式(当服务器端回调回来的对象数据格式是json数据格式，必须保证JSON的格式要求，回调的对象必须使用eval函数进行转化（否则将得不到Object）。本文不作详细介绍服务器端回调的数据问题，我们将直接自定义对象)  			 11.   $("#result ").html("------------遍历对象.each的使用-------------");  
		alert("====" + list);//是个object元素  
		//下面使用each进行遍历  
		$.each(list, function(n, value) {
			alert(n + ' ' + value);
			var trs = "<section class=\"re01\">";
			trs += "<ol id=\"project\"><li>抄表日期: "+ value.lastinputdate + "</li><li>上期指数:"
					+ value.astinputgasnum + "</li><li>本期指数:" + value.lastrecord
					+ "</li><li>气量:" + value.oughtmount + "</li><li>气费:"
					+ value.totaloughtfee + "</li><li>已交金额:" + value.oughtfeed
					+ "</li><li>应交金额:"+ value.oughtfee + "</li></ol>";
			tbody += trs+"</section>";
		});
		$("#test").append(tbody);
	});
	function f_jiaofe() {
		var openid = getUrlParam('openid');
		var qf = getUrlParam('money');
		document.location.href = "pay.html?openid=" + openid + "&qf=" + qf;
	}
</script>
</HEAD>

	<body onload="payload()">
	<div id="test"></div>
	<aside class="dq">
		<ol>
		<li>您上期结余为：<span id="zhye"></span></li>
		<li>您当前的欠费金额为：<span id="qianfei"></span></li>
	</ol>
	</aside>
	<div class="btn">
		<input type="submit" value="去交费" class="bdbtn" onclick="f_jiaofe();">
	</div>
</body>

</html>
