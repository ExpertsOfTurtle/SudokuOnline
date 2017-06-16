;(function($) {
	$.timer = function(func, time, autostart) {
	 	this.set = function(func, time, autostart) {
	 		this.init = true;
	 	 	if(typeof func == 'object') {
		 	 	var paramList = ['autostart', 'time'];
	 	 	 	for(var arg in paramList) {if(func[paramList[arg]] != undefined) {eval(paramList[arg] + " = func[paramList[arg]]");}};
 	 			func = func.action;
	 	 	}
	 	 	if(typeof func == 'function') {this.action = func;}
		 	if(!isNaN(time)) {this.intervalTime = time;}
		 	if(autostart && !this.isActive) {
			 	this.isActive = true;
			 	this.setTimer();
		 	}
		 	return this;
	 	};
	 	this.once = function(time) {
			var timer = this;
	 	 	if(isNaN(time)) {time = 0;}
			window.setTimeout(function() {timer.action();}, time);
	 		return this;
	 	};
		this.play = function(reset) {
			if(!this.isActive) {
				if(reset) {this.setTimer();}
				else {this.setTimer(this.remaining);}
				this.isActive = true;
			}
			return this;
		};
		this.pause = function() {
			if(this.isActive) {
				this.isActive = false;
				this.remaining -= new Date() - this.last;
				this.clearTimer();
			}
			return this;
		};
		this.stop = function() {
			this.isActive = false;
			this.remaining = this.intervalTime;
			this.clearTimer();
			return this;
		};
		this.toggle = function(reset) {
			if(this.isActive) {this.pause();}
			else if(reset) {this.play(true);}
			else {this.play();}
			return this;
		};
		this.reset = function() {
			this.isActive = false;
			this.play(true);
			return this;
		};
		this.clearTimer = function() {
			window.clearTimeout(this.timeoutObject);
		};
	 	this.setTimer = function(time) {
			var timer = this;
	 	 	if(typeof this.action != 'function') {return;}
	 	 	if(isNaN(time)) {time = this.intervalTime;}
		 	this.remaining = time;
	 	 	this.last = new Date();
			this.clearTimer();
			this.timeoutObject = window.setTimeout(function() {timer.go();}, time);
		};
	 	this.go = function() {
	 		if(this.isActive) {
	 			this.action();
	 			this.setTimer();
	 		}
	 	};

	 	if(this.init) {
	 		return new $.timer(func, time, autostart);
	 	} else {
			this.set(func, time, autostart);
	 		return this;
	 	}
	};
})(Zepto);
// http://jchavannes.com/jquery-timer/demo


var sd = sd || {
        actionSource: "", //记录*
        actionIndex: 0, //idx
        prt: 0,			//指针
        go: null, //time
        vPause: 1, //暂停状态
        isSave: false,//是否保存
        sec: 0, //秒
        tsec: 0, //总初始时间:秒
        min: 0, //分钟
        prevTime: 0, //ms
        pauseTxt: 'Pause',
        pauseContinue: 'Continue',
        save_not_competed: '',
        solverTitle: 'Are you Ready?',
        solverContent: '确认要离开吗？',
        solverOK: 'Yes, I\'m Ready',
        solverCancel: 'No, Lets Wait',
        workCellId: '',
        timer_updateProcess: null,
        doUpdateProcess:function() {
        	var process = this.GetProgress();
        	sendUpdate(process);
        },
        pad:function(number, length) {
            var str = '' + number;
            while (str.length < length) {str = '0' + str;}
            return str;
        },
        formatTime:function(time){
            time = time / 10;
            var min = parseInt(time / 6000),
                sec = parseInt(time / 100) - (min * 60);
                // hundredths = this.pad(time - (sec * 100) - (min * 6000), 2);
            return (min > 0 ? this.pad(min, 2) : "00") + ":" + this.pad(sec, 2);// + ":" + hundredths;
        },
        Start: function () {
            //$("#disTime").val(this.min + ":" + this.sec);
            this.go = setTimeout("sd.Start()", 5000);
            this.tsec+=5;
            this.sec+=5;
            this.min += this.sec / 60;
            this.sec %= 60;
            this.doUpdateProcess();
        },
        //时间 ----ok
        Stop: function () {
            clearTimeout(this.go);
        },
        //输入相同数字就删除
        Vl: function (f) {
            var str = "";
            var strk = f.value;
            for (var i = 0; i < strk.length; i++) {
                if (strk.indexOf(strk.substring(i, i + 1)) == strk.lastIndexOf(strk.substring(i, i + 1)))
                    str += strk.substring(i, i + 1);
            }
            f.value = str;
        },
        //onblur="K(this)" onkeyup="K(this);CV(this)"
        K: function (f) {
            this.Vl(f);
            if (f.value.length > 3) {
                f.className = 'K2';
            } else if (f.value.length > 1) {
                f.className = 'K1';
            } else {
                f.className = 'K0';
            }
        },
        //onfocus="F(this)"
        F: function (f) {
            this.Vl(f);
            if (f.value.length > 3) {
                f.className = 'F2';
            } else if (f.value.length > 1) {
                f.className = 'F1';
            } else {
                f.className = 'F0';
            }
        },
        //给input设置css
        _Inptcs: function (f) {
            if (f.value.length > 3) {
                f.className = 'K2';
            } else if (f.value.length > 1) {
                f.className = 'K1';
            } else {
                f.className = 'K0';
            }
        },
        //onkeyup="k(this);CV(event,this)"  设置时间与记录
        CV: function (e, f) {
            var t = (new Date()).getTime() - this.prevTime;
            t = this.prevTime == 0 ? 0 : t;
            t = t > 0 ? Math.floor(t / 10) : 0;

            if (e == null) {
                key = 0;
            } else {
                var key = e ? e.which : event.keyCode; // Number(input.keyCode);
                if (key == undefined)
                    key = e.keyCode;
            }

            this.prevTime = (new Date()).getTime();
            if (key < 37 || key > 40) {
                var _v = $.trim($("#" + sd.workCellId).text());
                this.actionSource += f.id.substring(1) + "$" + _v + "|" + t + "#";//f.value
                //console.info(this.actionSource);
                this.actionIndex++;
                this.prt++; //new
            }
        },
        /**
         * 暂停
         * 2014-08-24
         * @constructor
         */
        Pause: function () {
            var n = $(".ptb tr").first().children("td").length; //$("#hidSize").val();
            if (sd.vPause == 1) {
                this.userSol = "";
                for (var i = 1; i <= n; i++) {
                    for (var j = 1; j <= n; j++) {
                        var _$cell = $('#k' + j + 's' + i);
                        if (_$cell.find('span').hasClass("fixedCell") == false) {
                            this.userSol = this.userSol + $.trim(_$cell.text()) + ",";
                            _$cell.html('<span class="varCell1">?</span>');
                        }
                        else
                            this.userSol = this.userSol + "0,";
                    }
                }

                $("#btnPause").text(this.pauseContinue);
                this.vPause = 0;
                this.Stop();
            }
            else {
                var UserSolAl = this.userSol.split(',');
                for (i = 1; i <= n; i++) {//b
                    for (j = 1; j <= n; j++) {
                        var _$cell = $('#k' + j + 's' + i);
                        if (_$cell.find('span').hasClass("fixedCell") == false) { //a
                            var _userSol = UserSolAl[((i - 1) * n + j) - 1];
                            this.setCellVals(_$cell, _userSol);
                        } //a
                    }
                } //b

                $("#btnPause").text(this.pauseTxt);
                this.vPause = 1;
                this.userSol = "";
                this.Start();
            }
        },
        /**
         * 清除所有
         * @constructor
         * 2014-08-24
         */
        ClearAll: function () {
            sd.prt = 0;
            $(".ptb td").each(function (i) {
                if ($(this).find('span').hasClass("fixedCell") == false) {
                    $(this).html('&nbsp;');
                }
            });
            
            var t = (new Date()).getTime() - this.prevTime;
            t = this.prevTime == 0 ? 0 : t;
            t = t > 0 ? Math.floor(t / 10) : 0;
            
            this.prevTime = (new Date()).getTime();
            
            this.actionSource += "0s0$0" + "|" + t + "#";//f.value
            //console.info(this.actionSource);
            this.actionIndex++;
            this.prt++; //new
        },
        //还原所有 ok
        ShowAll: function () {
            this.ClearAll();
            for (var i = 0; i < this.actionIndex; i++) {
                var _val = this.actionSource.split("#")[i].split("$")[1].split('|')[0],
                    _$cell = $("#k" + this.actionSource.split("#")[i].split("$")[0]);
                this.setCellVals(_$cell, _val);
            }
            this.prt = this.actionIndex;
        },
        /**
         * 上一步
         * @constructor
         */
        Prev: function () {
            if (this.prt > 0) {
                --this.prt;
                this.setCellVals($("#k" + this.actionSource.split("#")[this.prt].split("$")[0]), '');
            }
        },
        /**
         * 下一步
         * @constructor
         */
        Next: function () {
            if (this.actionIndex > this.prt) {
                this.setCellVals($("#k" + this.actionSource.split("#")[this.prt].split("$")[0]),
                    this.actionSource.split("#")[this.prt].split("$")[1].split("|")[0]);
                ++this.prt;
            }
        },
        /**
         * 高亮选中的cell
         * @param objId
         * @constructor
         */
        SelIpt: function (objId) {
            var _$cellSpan = $("#" + objId + " span");

            $(".ptb td").removeClass('act_cell');
            $("#" + objId).addClass('act_cell');
            sd.workCellId = objId;
            if (_$cellSpan.attr("class") != undefined && _$cellSpan.attr("class") == "fixedCell") {
                sd.workCellReadOnly = true;
            } else {
                sd.workCellReadOnly = false;
            }
        },
        /**
         * 计算用户填的进度，填完成是100.00
         * 2014-08-25
         * @returns {string}
         * @constructor
         */
        GetProgress: function () {
            //已知数
            var total = $(".ptb td").length;
            //用户填的数
            var user_ipt = 0;
            var rlen = 0;
            $('.ptb td span').each(function (idx, item) {
                if ($(this).hasClass('fixedCell')) {
                    rlen++;
                } else {
                    if ($.trim($(this).text()).length == 1) {
                        user_ipt++;
                    }
                }
            });
            return ((user_ipt / (total - rlen)) * 100).toFixed(2);
        },
        /**
         * 检查用户填的是否是唯一
         * @param q
         * @returns {boolean}
         * @private
         */
        CheckPuzzle:function(q){
            var _val = '';
          //row
            for(var i=0;i<=81;i+=9){
                _val = q.substr(i,9);
                if(this.CheckRepeat(_val)){return false;}
            }
          // col
            for(var i=0;i<9;i++) {
                var cols = [];
                for(var j=0;j<9;j++){
                    cols.push(q.substr(j*9+i, 1));
                }
                _val =cols.join('');
                if(this.CheckRepeat(_val)){return false;}
            }
          // box
            for(var i=0;i<9;i++) {
                var boxs = [];
                for(var r=0;r<3;r++){
                    for(var c=0;c<3;c++){
                        boxs.push(q.substr((Math.floor(i/3)*2+r)*9+c+i*3,1));
                    }
                }
                _val =boxs.join('');
                if(this.CheckRepeat(_val)){return false;}
            }

            return true;
        },
        CheckRepeat:function(str){
            str = str.replace(new RegExp('0',"gm"),''); // 把0替换成空 replace all  String.prototype.replaceAll = function(s1,s2){return this.replace(new RegExp(s1,"gm"),s2);}
            var reg = /(?:^|)(\S{1}).*\1/g;
            return reg.test(str)?true:false;
        },
        GetPuzzle:function(){
            var _p = [],
                _val = '';
            $(".ptb td").each(function(){
                _val = $.trim($(this).text());
                if(_val.length==1){
                    _p.push(_val);
                }else{
                    _p.push('0');
                }
            });
            return _p.join('');
        },
        Save: function () {
            var _$tips = $("#tips"),
                _$ptb_td = $(".ptb td");

            if (this.GetProgress() == "100.00") {
                var _userSolver = '';
                _$ptb_td.each(function () {
                    _userSolver = _userSolver + $.trim($(this).text());
                });
                if (_userSolver.length != _$ptb_td.length) {
                    _$tips.html("<span class='text-danger glyphicon glyphicon-exclamation-sign'></span> " + this.save_not_competed);
                    _$tips.attr("class", 'show alert alert-warning');
                    setTimeout(function () {
                        $("#tips").attr("class", "hide");
                    }, 3500);
                    return;
                }
                var json_data = {
                    'user_id': $("#user_id").val(),
                    'pzId': $("#pzId").val(),
                    'answer': _userSolver,
                    'tsec': this.tsec,
                    'actionSource': this.actionSource,
                    'difficulty': $("#difficulty").val(),
                    'difficultySource': $("#difficultySource").val()
                };
                $.ajax({
                    url: "/save",
                    data: json_data,
                    async: true,
                    dataType: "json",
                    type: "Post",
                    cache: false,
                    timeout: 5000,
                    success: function (json) {
                        //console.info(json);
                        if (json.success) {
                            _$tips.html("<span class='text-danger glyphicon glyphicon-ok'></span> " + json.message);
                            _$tips.attr("class", 'show alert alert-success');
                            $("#share_tools").removeClass("hide");
                        } else {
                            _$tips.html("<span class='text-danger glyphicon glyphicon-exclamation-sign'></span> " + json.message);
                            _$tips.attr("class", 'show alert alert-warning')
                            //setTimeout(function(){$("#tips").attr("class","hide");},3500);
                        }
                    }
                });
            }
            else {
                _$tips.html("<span class='text-danger glyphicon glyphicon-exclamation-sign'></span> " + this.save_not_competed);
                _$tips.attr("class", 'show alert alert-warning')
                setTimeout(function () {
                    $("#tips").attr("class", "hide");
                }, 3500);
            }
        },
        /**
         * 单元格的fun入口：onmousedown=this
         * @param objId
         */
        workCell: function (objId) {
            var _$thisCell = $("#" + objId),
                _$cellSpan = $("#" + objId + " span");

            if (_$cellSpan.attr("class") != undefined && _$cellSpan.attr("class") == "fixedCell") {
                sd.workCellReadOnly = true;
            } else {
                sd.workCellReadOnly = false;
            }

            if (sd.workCellId != objId) {
                $("#" + sd.workCellId).removeClass('act_cell');
                _$thisCell.addClass('act_cell');
                sd.workCellId = objId;
            }

            //if(sd.workCellReadOnly==false){
            //_$thisCell.html('<span class="varcell" style="color:#0011FF;">8</span>');
            //    this.setCellVal(objId,2);
            //}
            /* 鼠标的
             if($("#mouse_mode").prop('checked')){
             var off1 = $("#mouse_parent").offset(),
             offset = _$thisCell.offset(),
             _$mouse_board_div = $("#mouse_board_div");
             $("#mouse_var").val(objId);
             _$mouse_board_div.removeClass("hide")
             .css({"left":parseFloat(offset.left-off1.left),"top":parseFloat(offset.top-off1.top+42)})
             }
             */
            //console.info(objId);
            //alert(objId);
        },
        /**
         * 给指定单元格设置一个值，一般是用户输入的时候用的，有录像记录
         * @param objId
         * @param val
         */
        setCellVal: function (objId, val) {
            var _$thisCell = $("#" + objId),
                _$objSpan = $("#" + objId + " span"),
                _vals = "",
                _newVal = '',
                _fontClass = '';

            if (val == undefined) {
                return;
            }

            // 只读cell
            if (sd.workCellReadOnly) {
                return;
            }

            if (val == "") {
                _$thisCell.html('&nbsp;');
            } else if (_$objSpan.size() == 0) {
                //是空的，新填
                _$thisCell.html('<span class="varCell1">' + val + '</span>');
            } else {
                // 获取已经输入的数字
                _vals = _$objSpan.text();

                // 判断输入是否已经存在，存在删除，没存在添加
                if (_vals.indexOf(val) > -1) {
                    _newVal = _vals.replace(val, '');
                    if (_newVal.length > 3) {
                        _fontClass = 'varCell3';
                    } else if (_newVal.length > 1) {
                        _fontClass = 'varCell2';
                    } else {
                        _fontClass = 'varCell1';
                    }
                    _$thisCell.html('<span class="' + _fontClass + '">' + _newVal + '</span>');
                } else {
                    _newVal = _vals.toString() + val.toString();
                    if (_newVal.length > 3) {
                        _fontClass = 'varCell3';
                    } else if (_newVal.length > 1) {
                        _fontClass = 'varCell2';
                    } else {
                        _fontClass = 'varCell1';
                    }
                    _$thisCell.html('<span class="' + _fontClass + '">' + _newVal + '</span>');
                }
            }
        },
        /**
         * 给指定的单元格设置值（可能是多个值，不记录录像）
         * @param $cell
         * @param vals
         */
        setCellVals: function ($cell, vals) {
            if (vals.length > 3)
                $cell.html('<span class="varCell3">' + vals + '</span>');
            else if (vals.length > 1)
                $cell.html('<span class="varCell2">' + vals + '</span>');
            else
                $cell.html('<span class="varCell1">' + vals + '</span>');
        },
        getUserAnswer: function () {
            var _answer = [];
            $(".ptb td").each(function (i) {
                if ($.trim($(this).text()).length == 1) {
                    _answer.push($.trim($(this).text()));
                }
            });
            return _answer.join('');
        },
        Init: function (actionSource) {
            //sd.actionSource = actionSource;
            //sd.actionIndex = sd.actionSource.split("#").length - 1;
            //clear all ipt
            sd.ClearAll();
            //sd.Start();
        }
    };