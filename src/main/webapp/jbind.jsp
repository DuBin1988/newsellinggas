<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
<title>用户解绑</title>
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

	function f_jbind() {
		var openid = getUrlParam('openid');
		var f_userid = document.getElementById("username").value;
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {

				var object = xhttp.responseText;
				
				var obj = JSON.parse(object);
				var message=obj.message;
				if(message=="请您检查输入编号是否正确,或您尚未绑定"){
					alert(message);
					return;
				}
				if(message=="解绑成功"){
					document.location.href = "success.jsp?message" + message;
					
				}			
			}
		};
	xhttp.open("GET", "rs/weixin/one/delete/" + f_userid + "/" + openid, true);

		xhttp.send();

	}
</script>
<body>
   <header class="findstyle">
	<ul class="list">
		<li class="userid">用户编号：</li>
		<li class="textinput"><input type="text" value="" id=username></li>
		<li class="findbtn"><input type="button" value="解绑"
			onclick="f_jbind();"></li>
		<br>
	</ul>
	</header>

	
</body>

</html>