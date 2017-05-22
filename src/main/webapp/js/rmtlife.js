// 微信签名路径
var appIdstr = "";
var timestampstr = "";
var nonceStrstr = "";
var signaturestr = "";

//后台路径
//var basePath = "http://www.prmt.cn/mpprmt/wxJsAPIback";
//var bindPath = "http://www.prmt.cn/mpprmt/wxStrback";
//设备id
var deviceId = "";
var deviceIds = "";

var intType = 0;
var LowPow = 1;
// 读卡器返回的字节数组
var recevibuf = new Array();

//发鿁数捿
var data = "";

//蓝牙连接状濿
var BluetoothS = 0;
//蓝牙断开的id
var disconnectdevId = "";

//扫描设备
var scandevice="";
var macids = ""; 

//连接设备 0 未连接，1已连掿
var connectdevice = "0";

//绑定与解绿1表示有设备绑定，0表示没有设备绑定
var bindbackstr = "";
// 当前想设备发送消息的函数是，作为标示
var sendfunction = "";

var xiekalen = [[0,50],[50,50],[100,50],[150,50],[200,50],[250,6]];
var xiekaIndex = 0;
// 多次读卡收到的数捿
var zrsl = {};
// 循环读卡变量
//var z = 0;



	// 未使甿
	function initDev() {
	    //1. 打开微信设备   
	    my_openWXDeviceLib();

	    //3. 扫描到某个设夿  
	    my_onScanWXDeviceResult();

	    //4. 接收设备数据
	    my_onReceiveDataFromWXDevice();

	    //5.监听蓝牙连接状濿
	    my_onWXDeviceStateChange();
	}
// 未使甿
function init() {

    //2. 获取设备信息  
    my_getWXDeviceInfos();



    return;

}

//打开微信设备  没有使用
function my_openWXDeviceLib() {
    var x = "";
    WeixinJSBridge.invoke('openWXDeviceLib', {}, function (res) {
//    	alert(JSON.stringify(res));
        // mlog("打开设备返回＿"+res.err_msg);  
        if (res.err_msg == 'openWXDeviceLib:ok') {
            if (res.bluetoothState == 'off') {
                x = "太着急啦亿,使用前请先打弿手机蓝牙＿";
                $("#xieka").val("未连接设备，无法写卡");
                $("#xieka").attr("disabled", "disabled");
                
                $("#saomiao").val("请打弿手机蓝牙");
                $("#saomiao").attr("disabled", "disabled");
            };
            if (res.bluetoothState == 'unauthorized') {
                x = "出错啦亲,请授权微信蓝牙功能并打开蓝牙＿";
            };
            if (res.bluetoothState == 'on') {
                x = "蓝牙已打开,未找到设备";
                alert("打开微信硬件库成功");
            };
        }
        else {

            x = "打开微信硬件库失败！";
        }
    });
    return x;  //0表示成功 1表示失败  
}

//获取设备信息
function my_getWXDeviceInfos() {

    deviceIds = "";
    WeixinJSBridge.invoke('getWXDeviceInfos', {}, function (res) {
    	//alert("获取设备信息99"+JSON.stringify(res));
    	for(i=0; i<res.deviceInfos.length;i++){
    		var connectstr;
    		if(res.deviceInfos[i].state === "connected"){
    			connectdevice = "1";
    			deviceId = res.deviceInfos[i].deviceId;
    			connectstr = "已连接";
    			$("span#"+deviceId).text("已连接 ");
            	$("#xieka").val("写卡 ");
            	$("#xieka").removeAttr("disabled");
    		}else{
    			connectstr = "已绑定未连接";
    			connectdevice = "0";
    		}
    		
    		//alert(connectstr);
    			$("#devicelist").append('<td class="table"><span>' + res.deviceInfos[i].deviceId.split("_")[2] 
				+'</span><span id="' + res.deviceInfos[i].deviceId 
				+ '">'+connectstr+'</span><input type="button" value="解绑" id="'
				+ res.deviceInfos[i].deviceId+'" onclick="jbind(\''+res.deviceInfos[i].deviceId+'\')"/></td>');
    		
    	}
    	
    	
        //var len = res.deviceInfos.length;  //绑定设备总数釿  
        //for (i = 0; i <= len - 1; i++) {
           // if (res.deviceInfos[i].state === "connected") {
                // x= "设备已成功连掿";
              //  deviceIds += res.deviceInfos[i].deviceId + "&";

                //h5界面接收数据
                //h5();

              //  break;
           // }
        //}
       // deviceIds = deviceIds.substring(0, deviceIds.length - 1);
    });


    return deviceIds;
}

//扫描设备 
function my_startScanWXDevice() {
    //关闭设备庿
    wx.invoke('closeWXDeviceLib', {}, function (res) {

    });
    //初始化设备库
    wx.invoke('openWXDeviceLib', {}, function (res) {

    });
    //扫描设备
    wx.invoke('startScanWXDevice', { "btVersion": "ble" }, function (res) {
        // mlog("扫描设备返回＿"+res.err_msg);  
        if (res.err_msg == 'startScanWXDevice:ok') {
            scandevice = "1";
        }
        else {
            scandevice = "0";
        }
    });
    return scandevice;     
}

//连接设备  未使甿
function my_connectWXDevice(devMacId) {
    data = { "deviceId": devMacId, "connType": "blue" };
    WeixinJSBridge.invoke('connectWXDevice', data, function (res) {
        if (res.err_msg == 'connectWXDevice:ok') {
            connectdevice = "1";
//            alert("连接");
        }
        else {
            connectdevice = "0";
            alert("连接失败");
        }
    });
}

//绑定设备
function my_getWXDeviceTicket(devMacId, openId) {
	$("span#"+devMacId).text("请求绑定");
//	alert("正式绑定  156衿");
    data = { "deviceId": devMacId, "type": 1 };
    wx.invoke('getWXDeviceTicket', data, function (res) {
//    	alert(JSON.stringify(res));
        if (res.err_msg == 'getWXDeviceTicket:ok') {
        	$("span#"+devMacId).text("绑定中请稍等.. ");
            $.ajax({
                type: "GET", //请求的类垿
                url: "rs/weixin/bind", //请求的地坿
                data: { device: devMacId, ticket: res.ticket, marke: "1", openid: openId },
                success: function (bindback) {
                	var res = JSON.parse(bindback);
                	if(res.base_resp.errmsg === "ok"){
//                		alert("绑定成功 ");
                		bindbackstr = 1;
                		$("span#"+devMacId).text("未绑宿 ");
		        		$("input#"+devMacId).attr("value","绑定");
		        		$("input#"+devMacId).attr("onclick","bind(\' "+ devMacId +"\')");
		        		$("#xieka").val("未连接设备，无法写卡");
		                $("#xieka").attr("disabled","disabled");
                	}else{
                		alert("绑定失败,原因为"+res.base_resp.errmsg);
                	}
                }
            });
        }else{
        	$("span#"+devMacId).text("请求绑定失败");
        }
    });
}

//解除绑定
function my_getWXDeviceTicketUb(devMacId, openId) {

//	alert("解绑");
	$("span#"+devMacId).text("请求解绑");
    data = { "deviceId": devMacId, "type": 2 };
    wx.invoke('getWXDeviceTicket', data, function (res) {
        if (res.err_msg == 'getWXDeviceTicket:ok') {
        	$("span#"+devMacId).text("解绑中请稍等..");
            $.ajax({
                type: "GET", //请求的类垿
                url: "rs/weixin/bind", //请求的地坿
                data: { device: devMacId, ticket: res.ticket, marke: "2", openid: openId },
                success: function (bindback) {
                	var res = JSON.parse(bindback);
                	if(res.base_resp.errmsg === "ok"){
                		alert("解绑成功 ");
                		bindbackstr = 0;
                		$("span#"+devMacId).text("未绑宿 ");
		        		$("input#"+devMacId).attr("value","绑定");
		        		$("input#"+devMacId).attr("onclick","bind(\' "+ devMacId +"\')");
		        		$("#xieka").val("未连接设备，无法写卡");
		                $("#xieka").attr("disabled","disabled");
                	}else{
                		alert("解绑失败，原因为"+res.base_resp.errmsg);
                	}
                	
                    
                }
            });
        }else{
        	$("span#"+devMacId).text("请求解绑失败");
        }
    });

}

//扫描到某个设夿  
function my_onScanWXDeviceResult() {
    wx.on('onScanWXDeviceResult', function (argv) {
    	$("#saomiao").val("扫描设备");
    	$("#saomiao").removeAttr("disabled");
    	macids = "";
        var ret = argv.devices;
        for (var i = 0; i < ret.length; i++) {
            macids += JSON.stringify(argv.devices[i].deviceId).replace(/\"/g, "") + "&";
        }
        //alert("270行扫描到的设夿" + macids);
        onmacids(macids)
        
        
    });
}

//监听蓝牙连接状濿 未使甿
function my_onWXDeviceStateChange() {
    wx.on('onWXDeviceStateChange', function (argv) {
        var macid = argv.deviceId;
        if (argv.state == 'disconnected') {
            BluetoothS = 1;
            disconnectdevId = macid;
//            alert("ID丿"+macid+"的设备已断开连接＿");
        }
        else if (argv.state == 'connected') {
            BluetoothS = 0;
            alert(macid,"已连接");
        }
        else {
            BluetoothS = 2;
        }

    });
}

// 丿下所有为点击写卡承要调用的函数，上面是初始化㿁扫描㿁绑定所霿要的函数
function h5() {
	z = 0;
    intType = 0;
    var Bytes = CheckBalance11();
    //alert("获取到的设备id为：" + deviceId);
//    alert("305--res.deviceId:" + bytes_array_to_base64(Bytes))
    data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(Bytes) };
    //发鿁数捿 暂时屏蔽
    sendfunction = "h5";
    my_sendDataToWXDevice();
    //alert("h5调用发鿁数据返囿"+strdata);
    return;
}
// 将设备返回的额数据发送到前台
function CheckBalance11() {
    var Bytes = new Array(3);

    Bytes[0] = 0x01;
    Bytes[1] = 0xfd;
    Bytes[2] = 0xfe;

    return Bytes;
}


//接收数据  
function my_onReceiveDataFromWXDevice() {
    WeixinJSBridge.on('onReceiveDataFromWXDevice', function (argv) {
    	// alert("316--"+sendfunction+"收到设备数据丿--" + JSON.stringify(argv));
        displayData(CharToHex(base64decode(argv.base64Data)));
    });
}

//判卡
function IsCardTy() {
    intType = 0;
    var s = "010506";
    var buf = new Array(3);

    //十六进制转字节数绿			
    buf = Str2Bytes(s);
    data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(buf) };
    sendfunction = "IsCardTy";
    //发鿁数捿
    my_sendDataToWXDevice();

    return;
}

//选卡
function CardTy(type) {
	//alert("选卡函数弿始时执行");
    var typestr = type.toString(16);
    if (typestr.length < 2) {
        typestr = "0" + typestr;
    }
    var s = "0201" + typestr;
    sendfunction = "CardTy";
    var strdata = onwrite(s);

    return strdata;
}

//上电
function power_on() {
	//alert("上电函数弿始执衿");
    intType = 0;
    var s = "010203";
    var buf = new Array(3);

    //十六进制转字节数绿			
    buf = Str2Bytes(s);
    data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(buf) };
    sendfunction = "power_on";
    //发鿁数捿
    var strdata = my_sendDataToWXDevice();

    return strdata;
}

//下电
function power_off() {
	//alert("下电函数弿始执衿");
	sendfunction = "power_off";
    intType = 0;
    var s = "010809";
    var buf = new Array(3);

    //十六进制转字节数绿			
    buf = Str2Bytes(s);
    data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(buf) };
    sendfunction = "power_off";
    //发鿁数捿
    var strdata = my_sendDataToWXDevice();

    return strdata;
}


//校验密码
function Wrcmp_sc(passw) {
//	alert("校验密码函数开始执行");
    var s = "";
    sendfunction = "Wrcmp_sc";
    if (passw.length == 4) {
        s = "050400" + passw + "CC";
    }
    if (passw.length == 8) {
        s = "080400" + passw.substring(0, 4) + "CC" + passw.substring(4, 8) + "CC";
    }
    if (passw.length == 12) {
        s = "0B0400" + passw.substring(0, 4) + "CC" + passw.substring(4, 8) + "CC" + passw.substring(8, 12) + "CC";
    }

    var strdata = onwrite(s);

    return strdata;
}

//4442卡校验密砿
function Wrcmp4442_sc(passw) {
//	alert("密码校验");
    var s = "";
    if (passw.length == 6) {
        s = "050400" + passw;
    }
    if (passw.length == 12) {
        s = "080400" + passw;
    }
    if (passw.length == 18) {
        s = "0B0400" + passw;
    }
    sendfunction = "Wrcmp4442_sc";
    var strdata = onwrite(s);

    return strdata;
}

//读卡
function rd_dat(zone, start, length) {
//	alert("425--读卡函数弿始执衿--zone"+zone+"start"+start+"length"+length);
    var zo = zone.toString(16);
    if (zo.length < 2) {
        zo = "0" + zo;
    }

    var st = start.toString(16);
    //if (st.length < 2) {
     //   st = "0" + st;
    //}
    if(st.length % 2 === 1) {
    	st = "0" + st;
    }

    var le = length.toString(16);
    if (le.length < 2) {
        le = "0" + le;
    }

    var s = "0609" + zo + "00" + st + "00" + le;
    sendfunction = "rd_dat";
//    alert("444--读卡函数传入onwrite函数的是＿"+s);
    var strdata = onwrite(s);
    return strdata;

}

//写卡(清卡)
function wrclear_dat(zone, start, length) {
	sendfunction = "wrclear_dat";
    var zo = zone.toString(16);
    if (zo.length < 2) {
        zo = "0" + zo;
    }

    var st = start.toString(16);
    if (st.length < 2) {
        st = "0" + st;
    }

    var le = length.toString(16);
//    if (le.length < 2) {
//        le = "0" + le;
//    }
    if(le.length % 2 === 1) {
    	le = "0" + le;
    }
    var s = "0603" + zo + "00" + st + "00" + le;
//    alert("509----调用写卡命令" + s);
    var strdata = onwrite(s);

    return strdata;

}

//写卡(命令)
function wrcomd_dat(zone, start, length) {
	sendfunction = "wrcomd_dat";
    var zo = zone.toString(16);
    if (zo.length < 2) {
        zo = "0" + zo;
    }
//    alert(zo);
    var st = start.toString(16);

    if(st.length % 2 === 1) {
    	st = "0" + st;
    }
    var le = length.toString(16);
    if(le.length % 2 === 1) {
    	le = "0" + le;
    }
    var s = "060A" + zo + "00" + st + "00" + le;
    var strdata = onwrite(s);
    return strdata;
}

//写卡(数据)
function wrdat_dat(bytes) {
	sendfunction = "wrdat_dat";
//	alert("写卡开始--》" + bytes);
    var lenstr = bytes.length;
//    alert(lenstr);
    var rebuf = new Array(lenstr + 2);
    rebuf[0] = lenstr & 0xFF;
    var crc = 0;
    for (var i = 0; i <= lenstr; i++) {
        if (i != lenstr) {
            rebuf[i + 1] = bytes[i];
        }
        crc += rebuf[i];
    }

//    alert("542-->"+crc);
    rebuf[lenstr + 1] = crc & 0xFF;
//    alert("544-->"+rebuf);
    intType = 0;
    data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(rebuf) };
    //发鿁数捿
//    alert(JSON.stringify(data));
    var strdata = my_sendDataToWXDevice();
    return strdata;
}

function Check() {
    var Bytes = new Array(2);

    Bytes[0] = 11;
    Bytes[1] = 12;

    return Bytes;
}


//写函敿
function onwrite(send) {
	//alert("531--写函数开始执衿"+ send);
    var i = 0;
    intType = 0;
    var buf = new Array();
    var copybuf = new Array();
    var crcstr = 0;

    //去掉空格
    send = send.replace(/\s/g, '');
    //alert("540--send"+ send);
    //十六进制转字节数绿			
    buf = Str2Bytes(send);
    //alert("543--buf"+ buf);
    for (i = 0; i < buf.length; i++) {
        copybuf[i] = buf[i];
        crcstr += copybuf[i];
    }

    copybuf[buf.length] = crcstr & 0xFF;
    data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(copybuf) };
    //alert("写函数向设备发鿁数捿"+JSON.stringify(data));
    //发鿁数捿
    var strdata = my_sendDataToWXDevice();

    return strdata;
}

//展示数据
function displayData(strbuf) {
//	alert("569展示前数据为---"+ strbuf);
    //去掉空格
    strbuf = strbuf.replace(/\s/g, '');
    var buf = new Array();
    buf = Str2Bytes(strbuf);
    //alert("568--要展示的数据在判断前+"+buf);
    var len = buf.length;
    var copybuf = new Array();

    if ((buf[0] & 0xFF) == 0xAA && len == 1) {
        intType = 2;
        LowPow = 1;
    } else if ((buf[0] & 0xFF) == 0xAA
			&& (buf[1] & 0xFF) == 0x00 && len == 2) {
        intType = 3;
        LowPow = 1;
    } else if ((buf[0] & 0xFF) == 0xAA
			&& (buf[1] & 0xFF) == 0xFD && len == 2) {
        intType = 4;
        LowPow = 1;
    } else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 0 && len == 3) {
        intType = 5;
        LowPow = 1;
    } else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 1 && len == 3) {
        intType = 6;
        LowPow = 1;
    } else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 0x16 && len == 3) {
        intType = 8;
        LowPow = 1;
    } else if ((buf[0] & 0xFF) == 0x01 && (buf[1] & 0xFF) == 0xBB
			&& (buf[2] & 0xFF) == 0xBC && len == 3) {
        intType = 9;
        LowPow = 0;
        alert("蓝牙设备电量低，为保证正确写卡，请先对设备进行充电然后在写卡");
        $('#loadering').css("display","none");
        return ;

    }

    //4442卿
    else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 0x02 && len == 3) {
        intType = 11;
        LowPow = 1;
    }
    //102卿
    else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 0x03 && len == 3) {
        intType = 12;
        LowPow = 1;
    }
    // 射频卿
    else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 0x04 && len == 3) {
        intType = 13;
        LowPow = 1;
    }
    //卡未插好或未知卡
    else if ((buf[0] & 0xFF) == 0xAA && (buf[1] & 0xFF) == 0
			&& (buf[2] & 0xFF) == 0x55 && len == 3) {
        intType = 14;
        LowPow = 1;
    }

    else if (intType == 3) {
        var crc = 0;
        var i = 0;

        for (i = 0; i < len - 1; i++) {
            crc += buf[i];
        }

        if ((buf[0] & 0xFF) == len - 2 && (buf[len - 1] & 0xFF) == (crc & 0xFF)) {
            //if ((buf[0] & 0xFF) ==len-2) {
            intType = 7;
            LowPow = 1;

            for (i = 0; i < len; i++) {
                recevibuf[i] = buf[i];
            }
            copybuf = Str2Bytes("AA");
        	data = { "deviceId": deviceId, "base64Data": bytes_array_to_base64(copybuf) };
            //发鿁数捿
        	orgData(buf);
        	
//    		alert("665 -- 我出现就表示要结束了");
            my_sendDataToWXDevice();
        }
    } else {
        intType = 10;
        LowPow = 1;
    }
    //alert("646当前发鿁消息的函数昿--"+sendfunction);
    // 根据发鿁信息的函数判断返回信息
    if(sendfunction === "h5"){ // h5函数给设备发信息
    	if(intType === 3){
    		//alert("h5函数执行成功，inType---"+ intType);
    		// 继续盗用判卡函数
    		IsCardTy();
    	}else{
    		alert("调用设备返回数据到前台函数失败");
    		$('#loadering').css("display","none");
    	}
    }else if(sendfunction === "IsCardTy") { // 判卡
    	
    	if(intType === 11) {
//    		alert("插入的IC卡为4442卿");
    		// 调用选卡
    		CardTy(19);
    	}else if(intType === 12) {
    		//alert("插入的IC卡为102卿");
    		// 调用选卡
    		CardTy(0);
    	}else if(intType === 13) {
    		//alert("插入的IC卡为射频卿");
    	}else if(intType === 14) { 
    		alert("未检测出卡的类型，请确认卡已插好或插入正确的卿");
    		$('#loadering').css("display","none");
    	}
//    	alert("657--判卡返回的intType---"+intType);
    }else if(sendfunction === "CardTy") { // 选卡
    	if(intType === 2){
    		//alert("选卡成功返回的intType---"+intType);
        	// 调用上电函数
        	power_on();
    	}else {
    		alert("选卡失败inType为"+ intType);
    		$('#loadering').css("display","none");
    	}
    }else if(sendfunction === "power_on") { // 上电
    	if(intType === 3){
//    		alert("上电操作成功，intType---"+intType);
//    		rd_dat(2,ZRSL[z][0],ZRSL[z][1]);
    		rd_dat(2,0,32);
    		
    	}else{
    		alert("上电操作失败，intType为"+intType);
    		$('#loadering').css("display","none");
    	}
    	
    }else if(sendfunction === "rd_dat") { // 读卡
    	if(intType === 3) {
//    		alert("707读卡成功,本次buf携带数据丿-->" + buf);
    	}else {
    		alert("读卡失败，intType为" + intType);
    		$('#loadering').css("display","none");
    	}
    }else if(sendfunction === "Wrcmp4442_sc") { // 密码校验
    	if(intType === 5) {
//    		alert("709--密码校验成功");
    		// 将十六进制字符串转成字节数组
    		// 先清卡，写卡命令，写卡
//    		alert(xiekaIndex);
//    		alert("744--qingka--"+ xiekalen[xiekaIndex][0]+ xiekalen[xiekaIndex][1]);
    		if (xiekaIndex <= 5) {
    			wrclear_dat(2,xiekalen[xiekaIndex][0],xiekalen[xiekaIndex][1]);
    		}
    		
    		
    	}else {
    		alert("密码校验失败，请联系工作人员，三次密码校验失败后卡将作废");
    		$('#loadering').css("display","none");
    	}
    	
    }
    else if(sendfunction === "wrclear_dat") { // 写卡(清卡)
    	if(intType === 8){
//    		alert("清卡成功，intType---"+intType);
    		// 清卡成功,调用写卡命令
    		if(xiekaIndex <= 5) {
    			wrcomd_dat(2, xiekalen[xiekaIndex][0], xiekalen[xiekaIndex][1]);
    		}else if (xiekaIndex === 6){
    			alert("调用修改卡密码写卡命令");
    			wrcomd_dat(0,1,3);
    		}
    		
    	}else{
    		alert("清卡失败，intType为"+intType);
    		$('#loadering').css("display","none");
    	}
    }
    else if(sendfunction === "wrcomd_dat") {// 写卡(命令)
    	if(intType === 3){
//    		alert("写卡命令成功，intType---"+intType);
    		// 清卡成功,调用写卡命令
    		if(xiekaIndex < 5) {
//    			alert(kdata.slice(0,50));
    			wrdat_dat(kdata.splice(0,50));
    		}else if (xiekaIndex === 5)  {
//    			alert(kdata.slice(0,50));
    			// 因为最后写卡的不足50位，如果写卡位数不对，将出现写卡失败，无法完成，所以单独处理
    			wrdat_dat(kdata.splice(0,6));
    		}else if (xiekaIndex === 6) {
    			//alert("写卡函数修改卡密码为：" + Kmm)
    			wrdat_dat(Str2Bytes(Kmm));
    		}
    	}else{
    		alert("写卡命令失败，intType为"+intType);
    		$('#loadering').css("display","none");
    	}
    }else if(sendfunction === "wrdat_dat") {// 写卡(数据)
    	if(intType === 3){
    		xiekaIndex++; // 若果写卡未完成继续调用清卡函数

    		if(xiekaIndex < 6){
    			wrclear_dat(2,xiekalen[xiekaIndex][0],xiekalen[xiekaIndex][1]);
    		}else if(xiekaIndex === 6) {
    			//alert("修改该密码开始，清卡")
    			if (Kmm === "" || Kmm === "000000") { // 如果卡要修改的卡密码为空或000000，表示不修改卡卡密码
    				xiekaIndex = 0;
        			//alert("卡密码没有变，不用修改，表示未"+ Kmm);
        			power_off();
    			}else {
    				wrclear_dat(0,1,3);
    			}
    			
    		}else if(xiekaIndex === 7) { // 空出的6是给修改卡密码的
//    			alert("779--写卡完成调用下电");
    			// 写卡完成，先调用修改卡密码，卡密码为在
    			//0区从1开始，长度3位，是不是通用，不知道，只收到一段c++代码片段
    			xiekaIndex = 0;
    			//alert("微信，更新快点好吗");
    			power_off();
    		}
    		// 清卡成功,调用写卡命令
    		
    	}else{
    		alert("写卡数据失败，intType为"+intType);
    		$('#loadering').css("display","none");
    	}
    }else if(sendfunction === "power_off") { // 下电
    	// 不管下电成不成功，都调用后台修改写卡标记和档案中的密码
    	writesuccess();
    }
};

//发鿁数捿
function my_sendDataToWXDevice() {
//	alert("794"+sendfunction + "--发鿁数据为--"+JSON.stringify(data));
	WeixinJSBridge.invoke('sendDataToWXDevice', data, function (res) {
//    	alert(JSON.stringify(res));
        var strdata = "";
        if (res.err_msg == 'sendDataToWXDevice:ok') {
            strdata = "数据发送成功";
            //alert("733----" + strdata);
             
        }else {
            strdata = "数据发送失败，原因为"+res.err_msg;
          	alert("向蓝牙发送数据失败，原因为"+res.err_msg);
          	$('#loadering').css("display","none");
        }
    });
//	alert("767-0---adfad");
    return strdata;
}

//base64编码
function bytes_array_to_base64(array) {
    if (array.length == 0) {
        return "";
    }
    var b64Chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    var result = "";
    // 给末尾添加的字符,先计算出后面的字笿
    var d3 = array.length % 3;
    var endChar = "";
    if (d3 == 1) {
        var value = array[array.length - 1];
        endChar = b64Chars.charAt(value >> 2);
        endChar += b64Chars.charAt((value << 4) & 0x3F);
        endChar += "==";
    } else if (d3 == 2) {
        var value1 = array[array.length - 2];
        var value2 = array[array.length - 1];
        endChar = b64Chars.charAt(value1 >> 2);
        endChar += b64Chars.charAt(((value1 << 4) & 0x3F) + (value2 >> 4));
        endChar += b64Chars.charAt((value2 << 2) & 0x3F);
        endChar += "=";
    }

    var times = array.length / 3;
    var startIndex = 0;
    // 弿始计箿
    for (var i = 0; i < times - (d3 == 0 ? 0 : 1); i++) {
        startIndex = i * 3;

        var S1 = array[startIndex + 0];
        var S2 = array[startIndex + 1];
        var S3 = array[startIndex + 2];

        var s1 = b64Chars.charAt(S1 >> 2);
        var s2 = b64Chars.charAt(((S1 << 4) & 0x3F) + (S2 >> 4));
        var s3 = b64Chars.charAt(((S2 & 0xF) << 2) + (S3 >> 6));
        var s4 = b64Chars.charAt(S3 & 0x3F);
        // 添加到结果字符串丿
        result += (s1 + s2 + s3 + s4);
    }

    return result + endChar;
}

// base64解码  
var base64DecodeChars = new Array(
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
	    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
	    -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
	    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
	    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
	    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1);
function base64decode(str) {
    var c1, c2, c3, c4;
    var i, len, out;
    len = str.length;
    i = 0;
    out = "";
    while (i < len) {
        c1
        do {
            c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
        }
        while (i < len && c1 == -1);
        if (c1 == -1)
            break;
        c2
        do {
            c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
        }
        while (i < len && c2 == -1);
        if (c2 == -1)
            break;
        out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));
        c3
        do {
            c3 = str.charCodeAt(i++) & 0xff;
            if (c3 == 61)
                return out;
            c3 = base64DecodeChars[c3];
        }
        while (i < len && c3 == -1);
        if (c3 == -1)
            break;
        out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));
        c4
        do {
            c4 = str.charCodeAt(i++) & 0xff;
            if (c4 == 61)
                return out;
            c4 = base64DecodeChars[c4];
        }
        while (i < len && c4 == -1);
        if (c4 == -1)
            break;
        out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
    }
    return out;
}

//字符转十六进制字符串
function CharToHex(str) {
	//alert("859--返回数据字符串未转换为十六进制前--"+ str);
    var out, i, len, c, h;

    out = "";
    len = str.length;
    i = 0;
    while (i < len) {
        c = str.charCodeAt(i++);
        h = c.toString(16);
        if (h.length < 2)
            h = "0" + h;

        out += h;
        if (i > 0 && i % 8 == 0)
            out += "\r\n";
    }
    //alert("875--返回数据字符串转换为十六进制吿--"+ out);
    return out;
}

//十六进制字符串转字节数组
function Str2Bytes(str) {
	//alert("881---十六进制字符串转字节数组剿" + str);
    var pos = 0;
    var len = str.length;
    if (len % 2 != 0) {
        return null;
    }
    len /= 2;
    var hexA = new Array();
    for (var i = 0; i < len; i++) {
        var s = str.substr(pos, 2);
        var v = parseInt(s, 16);
        hexA.push(v);
        pos += 2;
    }
    //alert("896--十六进制字符串转字节数组吿" + hexA);
    return hexA;
}

//字节数组转十六进制字符串
function Bytes2Str(bytes) {
	var str16 = "";
	for(var s=0; s<bytes.length; s++) {
		var s2x = bytes[s].toString(16);
		if(s2x.length % 2 === 1) {
			s2x = "0" + s2x;
		}
		//if(s !== bytes.length - 1){
			//str16 += s2x + ",";
			str16 += s2x;
		//}
		//else {
		//	str16 += s2x; 
		//}
	}
	return str16;
}
// 将每次读卡的返回的数据进行组织，组织完成后发给卡云服势
var datas = "";
var readData = "";
var datalen = [[0,32],[32,32],[64,32],[96,32],[128,32],[160,32],[192,32],[224,32]]
var z = 0;
function orgData(data){
	var da = Bytes2Str(data)
	datas += da.substring(2,da.length-2);
	z++;
	if(z < datalen.length) {
		rd_dat(2,datalen[z][0],datalen[z][1])
	}else {
		z = 0;
		readData = datas;
		datas = "";
	}
	readServices(readData);
}
// 从卡里读出的内容存储起来，方便写卡完成后
var readData
function readServices(datas) {
//	alert("向云服务请求");
//	alert(datas);
	$.ajax({
		type:'GET', //GET
		url: "rs/weixin/datas",
		data: {"cardinfo": datas },
		success:function(data,textStatus){
			readData = $.parseJSON(data);
			//alert(JSON.stringify(readData));
			// 如果是创源，进行气量取整
			if(readData.Factory == "ZhenLan") { //如果是真蓝，金额输入多少就是多少
				if (readData.Money > 0) {
					alert("卡上已有" + readData.Money + "元，请将卡中金额冲入表中后，再写卡");
					$('#loadering').css("display","none");
				}else {
					if (confirm("当前插入卡的卡号为：" + readData.CardID + ",\n卡上剩余金额为：" + readData.money
							+",\n本次写卡金额为：" + chMoney
							+",\n卡厂家为：" + readData.Factory
							+",\表累计购汽金额：" + readData.LJMoney
							+"。\n核对无误后点击确定")) {
						writeServices(datas, readData.Factory, readData.CardID, readData.Times, readData.LJMoney);
					}else {
						alert("已经取消写卡");
						$('#loadering').css("display","none");
					}
				}	
			}else if (readData.Factory == "ChuangYuan") {//如果是创源，需要进行气量取整
				if (readData.Gas > 0) {
					alert("卡上已有" + readData.Gas + "方气，请将卡中气量冲入表中后，再写卡");
					$('#loadering').css("display","none");
				}else {
					if (confirm("当前插入卡的卡号为：" + readData.CardID + ",\n卡上剩余气量为：" + readData.Gas 
							+ ",\n卡厂家为：" + readData.Factory
							+ ",\n本次真实写卡气量为：" + realgqgas
							+ ",\n本次真实写卡金额为：" + realgqjine
							+ "。\n核对无误后点击确定")) {
						writeServices(datas, readData.Factory, readData.CardID,readData.Times, 0);
					}else {
						alert("已经取消写卡");
						$('#loadering').css("display","none");
					}
				}	
			}
	    },
	    error: function(res) {
	    	$('#loadering').css("display","none");
	    	alert("向云服务发送读取卡数据error："+JSON.stringify(res));
	    }
	});
}
// canbuyGas(getUrlParam("pregas"),$("#shuru").val() -0)
// 写卡数据
var kdata;
// 写卡完成后要修改的卡密码
var kmm;
var writeDate;// 写卡时间
function writeServices(datas, factory, cardid, times, LjMoney) {
	var wdata;
	if(factory == "ZhenLan") {
		wdata = {"cardinfo": datas, "f_userid": getUrlParam('f_userid'), 
				"gas": gq, "jine": chMoney, "factory":factory, "cardid": cardid, "times":times, "ljine":LjMoney}
	}else if (factory == "ChuangYuan"){
		wdata = {"cardinfo": datas, "f_userid": getUrlParam('f_userid'), 
			"gas": realgqgas, "jine": realgqjine, "factory":factory, "cardid": cardid, "times":times, "ljine":0}
	}
//	alert(times);
	$.ajax({
		type:'GET',
		url: "rs/weixin/writedata",
		data: wdata,
		success:function(data,textStatus){
			var writeData = $.parseJSON(data);
//			alert(JSON.stringify(writeData));
			if(writeData.Err == null && writeData.Exception == null) {
				hint(); // 只要后台发来写卡数据表余额和购气量都已经生成，刷新提示
				writeDate = writeData.writeDate;
				kdata = Str2Bytes(writeData.Kdata);
				// 校验卡密码
				//alert("卡密码=" + writeData.CscKmm);
				Kmm = writeData.WscKmm;
				//alert("写卡卡密码=" + Kmm);
				Wrcmp4442_sc(writeData.CscKmm);
				// 将读出的卡密码转为可写入的数据
				
			} else if (writeData.kdata == "没有查询到相关用户") {
				$('#loadering').css("display","none");
				alert("没有查询到相关用户");
			} else {
				$('#loadering').css("display","none");
				alert("调用云服务写卡函数失败Err："+  writeData.Err + ",Exception:" + writeData.Exception);
			}
		
	    },
	    error: function(res) {
	    	$('#loadering').css("display","none");
	    	alert("调用云服务写卡函数失败error"+JSON.stringify(res));
	    }
	});
}
// 写卡完成后调用服务，修改写卡标记和密码
function writesuccess() {
	$.ajax({
		type:'GET',
		url: "rs/weixin/success",
		data: {cardid: readData.CardID, kmm: readData.Kmm, userid: getUrlParam('f_userid'), writeDate: writeDate},
		success:function(data,textStatus){
			// 修改成功后将动态图取消
			$('#loadering').css("display","none");
			alert("写卡成功");
	    },
	    error: function(res) {
	    	alert("修改标记和密码错误");
	    	$('#loadering').css("display","none");
	    }
	});
}

function click(e) {
    if (document.all) { if (event.button == 2 || event.button == 3) { oncontextmenu = 'return false'; } }
    if (document.layers) { if (e.which == 3) { oncontextmenu = 'return false'; } } 
}
if (document.layers) { document.captureEvents(Event.MOUSEDOWN); }
document.onmousedown = click;
document.oncontextmenu = new Function("return false;")
