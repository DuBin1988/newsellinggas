<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
<title>用户查询与绑定</title>
<link href="style/css.css" rel="stylesheet" type="text/css">
</head>
<script src="js/jquery-1.7.2.min.js"></script>
<script src="js/jquery.json-2.3.min.js"></script>
<script src='js/knockout-2.1.0.js'></script>
<script src="js/knockout.mapping-latest.js"></script>
<script src="js/knockout.validation.min.js"></script>
<script src='js/af.js'></script>

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
	function f_select(id) {
		var f_userid = document.getElementById("username").value;
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
		
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				
				if (xhttp.responseText == null||xhttp.responseText=="") {
					alert(xhttp.responseText);
					alert("请检查您输入的用户编号是否正确");
					return;
				}
				
				var object = xhttp.responseText;
				var obj = JSON.parse(object);
              if( obj.mmessage=="请检查您输入的用户编号是否正确"){
            	  alert("请检查您输入的用户编号是否正确");
					return;
				}
				var f_name = obj.f_username;
				$("#name").html(f_name);
				var f_address = obj.f_address;
				$("#address").html(f_address);
				if (!f_name == "") {
					document.getElementById("sel").style.display = 'block';
				}
			}
		};
		xhttp.open("GET", "rs/weixin/one/" + f_userid, true);
		xhttp.send();
	}
	function f_bind() {
		var openid = getUrlParam('openid');
		var f_userid = document.getElementById("username").value;
		//var message = alert(document.getElementById("sel").style.display);
		if (document.getElementById("sel").style.display == 'block') {
			var xhttp = new XMLHttpRequest();
			xhttp.onreadystatechange = function() {
				if (xhttp.readyState == 4 && xhttp.status == 200) {
					var object = xhttp.responseText;
					var obj = JSON.parse(object);
					var message = obj.message;
					if (message == "此号已经被别的用户绑定") {
						alert(message);
						return;
					}
					alert(message);
					var zhye = obj.zhye;
					var money = obj.money;
					var zhinajin = obj.zhinajin;
					var arr1 = obj.arr;
					var arr = JSON.stringify(arr1);
					document.location.href = "qf1.html?openid=" + openid
							+ "&showwxpaytitle=1" + "&f_zhye=" + zhye
							+ "&money=" + money + "&zhinajin=" + zhinajin
							+ "&arr=" + arr;
				}
			};
			xhttp.open("GET", "rs/weixin/one/" + f_userid + "/" + openid, true);
			xhttp.send();
		} else {
			alert("请先点击查询按钮确认您的信息");
			return;
		}

	}
</script>
<body>
	<header class="findstyle">
	<ul class="list">
		<li  class="userid">用户编号：</li><br>
		<li  class="textinput"><input type="text"
			value="" id=username></li>
	<li class="findbtn"><input type="button"
			value="查询" onclick="f_select();"></li>
		<br>
	</ul>
	
	</header>
	<section class="userinfo" style="display: none;" id="sel">
	<body onload="f_select()">
		<ul class="list">
			<li><strong>用户姓名：</strong><span
				id="name"></li>
			<li><strong>用户地址：</strong><span
				id="address"></span></li>
			<li>请您仔细核对自己的信息，然后再绑定</li>
		</ul>
	</section>
	<div class="btn">
		<input type="submit" value="绑定" class="bdbtn"
			onclick="f_bind();">
	</div>
	
	
</body>
</html>