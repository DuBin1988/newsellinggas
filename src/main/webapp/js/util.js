
// 阶梯气价分别是一阶气量，一阶气价，二阶气量，二阶气价(二阶气量无用，卡表不限购)
var f_stair1amount,f_stair1price,f_stair2amount,f_stair2price;
f_stair1amount = 50;
f_stair1price = 2.25;
f_stair2price = 2.93;
// 从后台获取阶梯气价
(function() {
	var path = "rs/weixin/price?userid=" + getUrlParam('f_userid'); 
	//var path = "/rs/weixin/price?userid='11006875'"; 
//	alert(path);
	$.getJSON(path.replace(/&/g, "$")	, function(data) {
		// alert(JSON.stringify(data));
		/*判断返回是否正确*/
		if (data.error != null && data.error != "undefined") {
			//alert(data.error);
			return;
		}
		console.log(data);
		f_stair1amount = data.f_stair1amount;
		f_stair1price = data.f_stair1price;
		f_stair2price = data.f_stair2price;
		alert("f_stair1amount="+f_stair1amount+",f_stair1price="+f_stair1price+",f_stair2price="+f_stair2price);
	});
})();
// 计算当前金额的可购气量
function buyGas(gas,jine) {
	var kgql;
	if(gas > f_stair1amount){
		kgql = (jine/f_stair2price).toFixed(2);
	}else{
		var djql = jine/f_stair1price;//得到低价购气量
		if(djql+gas > f_stair1amount) {
			var djkg = f_stair1amount-gas; //低价可够气量
			var gjkg = ((jine-djkg*f_stair1price)/f_stair2price); //高价可够气量
			kgql = (djkg+gjkg).toFixed(2);
		}else{
			kgql = djql.toFixed(2);
		}
	}
	if (kgql < 0) {
		kgql = 0;
	}
	return kgql;
}

//取整，购气量不能有小数，所以需要将小数部分的金额返回
// 返回真实的购气量和购汽金额
function canbuyGas(gas,jine) {
	// 获取金额的可购气量
	var kgql = buyGas(gas,jine);
	// 获取小数部分气量
	var realGas = Math.floor(kgql)
	if(realGas < 0) {
		realGas = 0;
	}
	var decimalPart = kgql - realGas;
	
	var realJine = jine;
	if (kgql + gas > f_stair1amount) {
		realJine = jine - decimalPart * f_stair2price;
	}else {
		realJine = jine - decimalPart * f_stair1price;
	}
	var real = {"realJine":realJine.toFixed(2) - 0, "realGas":realGas}
	return real;
}

// 获取用户的余额，为保证余额的真实与实时性
function getZhye(userid){
	$.ajaxSettings.async = false; 
	var path = "rs/weixin/zhye?userid=" + userid;   
	//alert(path);
	var getData = $.getJSON(path.replace(/&/g, "$")	, function(data) {
		//alert("getZhye");
		//alert(data);
		/*判断返回是否正确*/
		if (data.error != null && data.error != "undefined") {
			alert(data.error);
			return;
		}
		//alert(JSON.stringify(data));
		gas = data.gas;
		yue = data.f_zhye;
		//alert(gas);
		//alert(yue);
		return data;
	});
}
