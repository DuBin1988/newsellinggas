(function(window){

var af = {};
window.af = af;

//产生可扩展对象，可扩展对象可以扩展自己的属性
af.extendObject = function() {
	return {
		extend: function(obj) {
			$.extend(this, obj);
			return this;
		}
	}
}

//客户自定义事件
af.event = function(source) {
	//事件监听器注册函数
	function ret(action) {
		ret.handlers.push(action);
		return ret.source;
	}
	
	$.extend(ret, {
		//事件源
		source: source,
		
		//处理器列表
		handlers: [],

		//触发事件
		trigger: function() {
			$.each(this.handlers, function(i, action) {
				action(ret.source);
			});
		}
	});
	
	return ret;
};

//产生开关量，开关量可以在true和false之间切换
af.switchObject = function(init) {
	var ret = ko.observable(init);

	$.extend(ret, {
		switchState: function() {
			ret() ? ret(false) : ret(true);
		}
	});

	return ret;
};

//产生单个对象
af.generalObject = function(options) {
	var ret = af.extendObject();
	ret.extend({
		data: options.data,
		
		//如果有路径属性，当路径发生变化时，加载数据
		path: options.path, 
		
		//保存函数
		save : function() {
			var data = [{
				operator: 'save',
				entity: options.entity,
				data: ko.mapping.toJS(this.data) 
			}];
			var self = this;
			$.post(options.url, $.toJSON(data), function(){
				self.saved.trigger();
			});
		},
		
		//加载函数
		load : function() {
			var self = this;
			$.getJSON(this.path(), function(data) {
				self.data = ko.mapping.fromJS(data, [], self.data);
				self.loaded.trigger();
			});
		},
		
		//清除函数
		clear : function() {
			ko.mapping.fromJS({}, this.data);
		},

		//加载完事件
		loaded : af.event(ret),
		
		//保存完事件
		saved: af.event(ret)
	});
	
	return ret;
};

//创建列表对象
af.objectList = function(options) {
	var listView = af.extendObject();
	listView.extend({
		//加载到的数据
		data: ko.mapping.fromJS([]),
		
		//总共多少页
		pages: ko.observable(0),
		
		//每页多少行参数
		rows: options.rows,
		
		//当前页号
		currentPage: (function() {
			var current = ko.observable(1);
			current.subscribe(function() {
				//当前页号发生变化时，加载当前页数据
				listView.loadPage();
			});
			return current;
		})(),
		
		//求总和路径
		sumPath: options.sumPath,

		//path发生变化时，自动加载
		path: (function() {
			options.path.subscribe(function() {
				listView.load();
			});
			return options.path;
		})(),
		
		//求和字段
		sumNames: options.sumNames,
		
		//求和的结果数据
		sums: ko.mapping.fromJS(options.sums),
		
		//当前选中的项
		selected: ko.observable(null),
		
		//是否正在工作
		busy: ko.observable(false),
		
		//加载某一项时的路径
		onePath: ko.observable(options.onePath),

		//选中某一项
		select: function(place) {
			listView.selected(null);
			listView.selected(place);
		},
		
		//往后翻页
		nextPage: function() {
			this.currentPage(this.currentPage() - -1);
		},
		
		//往前翻页
		prevPage: function() {
			this.currentPage(this.currentPage() - 1);
		},
		
		//加载一项数据
		loadItem: function(id) {
			//找到给定id号的对象
			var obj = null, objKey = null;
			$.each(this.data(), function(key, value) {
				if(value.id() == id) {
					obj = value;
					objKey = key;
				}
			});
			var path = this.onePath();
			//替换其中的#id#
			path = path.replace('#id#', id);
			$.getJSON(path, function(data) {
				ko.mapping.fromJS(data,{},obj);
			});
		},
		
		//加载一页数据
		loadPage: function() {
			var value = this.path();
			//如果有当前页号属性，按分页方式加载
			if(this.currentPage && this.rows) {
				value = value + "/" + (this.currentPage() - 1) + "/" + this.rows();
			}
			//path编码
			var path = encodeURI(value);
			this.busy(true);
			$.getJSON(path, function(data) { 
				ko.mapping.fromJS(data, listView.data);
				listView.loaded.trigger();
			}).complete(function() {
				listView.busy(false);
			}).error(function(event){
			});
		},
		
		//加载整体数据
		load: function() {
			//如果不是分页模式，直接加载
			if(!this.rows) {
				this.loadPage();
				return;
			}
			//否则，加载总数信息
			var value = this.sumPath() + "/";
			if(this.sumNames) {
				value += this.sumNames;
			}
			else {
				value += "null";
			}
			var path = encodeURI(value);
			this.busy(true);
			$.getJSON(path, function(data) {
				ko.mapping.fromJS(data, listView.sums);
				//计算总页数，当前页回1
				var pages = Math.floor((listView.sums.Count() - 1) / listView.rows()) + 1;
				listView.pages(pages);
				//如果原来页号为1，强制加载
				if(listView.currentPage() == 1) {
					listView.loadPage();
				}
				else {
					listView.currentPage(1);
				}
			}).complete(function() {
				listView.busy(false);
			});
		},
		
		//一页数据加载完成事件
		loaded: af.event(listView)
	});

	return listView;
};

//产生查询对象
af.searchObject = function(options) {
	//查询条件函数
	var searchFunc = function() {
		var result = "";
		$.each(this.conditions, function(key, v) {
			var value = v();
			//条件存在
			if(value) {
				if(result == "") {
					result += value;
				}
				else {
					result += " and " + value;
				}
			}
		});
		//空，为真
		if(result == "") {
			result = "1=1";
		}
		//迫使条件发生变化
		if(result == this.condition()) {
			result += " ";
		}
		this.condition(result);
	};

	var searchObj = {
		condition: ko.observable(""),
		data: ko.mapping.fromJS(options.data),
		search: searchFunc
	};
	
	//把所有条件转换成计算属性
	var conditions = new Array();
	$.each(options.conditions, function(key, value) {
		conditions.push(ko.computed(value, searchObj.data));
	});
	searchObj.conditions = conditions;
	
	return searchObj;
};

af.param = function(options) {
	//把list里的数据转换成纯名称返回
	var result = ko.mapping.fromJS([]);
	var list = af.objectList(options);
	list.loaded(function() {
		$.each(ko.mapping.toJS(list.data), function(key, value) {
			result.push(value["name"]);
		});
	});
	return result;
};

//给数组添加分类统计功能
Array.prototype.count = function(field) {
	var result = {};
	$.each(this, function(key, value) {
		//如果结果中已经存在，加1，否则，为1
		var v = value[field]();
		if(result[v]) {
			result[v] = result[v] - -1;
		}
		else {
			result[v] = 1;
		}
	});
	var array = [];
	$.each(result, function(key, value) {
		array.push({name:key, count:value});
	});
	return ko.mapping.fromJS(array);
};

})(window);


// 对Date的扩展，将 Date 转化为指定格式的String 
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function(fmt) 
{ //author: meizz 
  var o = { 
    "M+" : this.getMonth()+1,                 //月份 
    "d+" : this.getDate(),                    //日 
    "h+" : this.getHours(),                   //小时 
    "m+" : this.getMinutes(),                 //分 
    "s+" : this.getSeconds(),                 //秒 
    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
    "S"  : this.getMilliseconds()             //毫秒 
  }; 
  if(/(y+)/.test(fmt)) 
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
  for(var k in o) 
    if(new RegExp("("+ k +")").test(fmt)) 
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
  return fmt; 
}
