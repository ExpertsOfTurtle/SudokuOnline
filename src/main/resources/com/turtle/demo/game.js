var comm = {
	lang : {
		Sudoku : 'Sudoku',
		Rank : {
			Easy : 'Easy',
			'Medium' : 'Medium',
			Hard : 'Hard',
			Evil : 'Evil',
			Extreme : 'Extreme'
		}
	},
	isSolverAll : false,
	tech : {
		NakedSingle : '唯余法(Naked Single)',
		HiddenSingle : '排除法(Hidden Single)',
		Claiming : '行列区块(Claiming)',
		Pointing : '宫区块(Pointing)',
		NakedSubset : '显性数组(Naked Subset)',
		HiddenSubset : '隐性数组(Hidden Subset)',
		X_Wing : 'X-Wing',
		XY_Wing : 'XY-Wing',
		W_Wing : 'Y-Wing (W-Wing)',
		XYZ_Wing : 'XYZ-Wing',
		X_Chains : 'X链(X_Chains)',
		XY_Chains : 'XY链(XY_Chains)'
	}
};
var playTimer = undefined, incrementTime = 1000, currentTime = 0;
$(function() {
	$.init();
	$("#share").text($(document).width());
	var _actionSource = "";
	sd.Init(_actionSource);
	if (playTimer != undefined) {
		playTimer.stop().once();
		currentTime = 0;
		playTimer.play();
	} else {
		playTimer = $.timer(function() {
			var timeString = sd.formatTime(currentTime);
			$("#btnTime").html(timeString);
			currentTime += incrementTime;
		}, incrementTime, true);
	}
	// 单元格输入
	$(".ptb td").click(function(event) {
		var objId = $(event.currentTarget).attr('id');
		sd.workCell(objId);
	});
	$("#btnUndo").click(function(event) {
		sd.Prev();
	});
	$("#btnRedo").click(function(event) {
		sd.Next();
	});
	$("#btnDel").click(function(event) {
		if (sd.workCellId != "") {
			sd.setCellVal(sd.workCellId, '');
			var f = document.getElementById(sd.workCellId);
			sd.CV(null, f);
		} else {
			$.toast('请先选择要删除的数字');
		}
	});
	$("#btn_next").click(function() {
		$.toast('比赛过程就别想作弊');
		return;
		var _$btnNext = $("#btn_next");
		if ($("#solver-steps canvas").length > 0) {
			$("#solver-steps").html("");
			_$btnNext.text('下一步？');
		} else {
			// get puzzle
			var puzzle = sd.GetPuzzle();
			if (sd.CheckPuzzle(puzzle) == false) {
				alert('你已经填错了！横，竖或宫填的不是唯一。');
				return;
			}
			var p3 = puzzle.split('');
			for (var j = 0; j < p3.length && j < 81; j++) {
				solver.p[j] = p3[j];
			}
			solver.initCandidates();
			solver.solve();
			_$btnNext.text('关闭提示');
		}
	});
	// btn
	$(".btnNum").click(function(event) {
		if (sd.workCellId != "") {
			sd.setCellVal(sd.workCellId, $(event.currentTarget).text());
			var f = document.getElementById(sd.workCellId);
			sd.CV(null, f);
		} else {
			$.toast('请先选择要填入的空格');
		}
	});
});
$(document).on('click', '#btn_reset', function() {
	$.confirm('确定要清空全部吗?', function() {
		sd.ClearAll();
	});
});
$(document).on('click', '#btnAnswer', function() {
	$.toast('分个毛线享吖');
});
$(document).on('click', '#btn_save', function() {
	var _$ptb_td = $(".ptb td");
	var process = sd.GetProgress();
	console.log("process:" + process)
	if (process == "100.00" ) {
		var _userSolver = '';
		_$ptb_td.each(function() {
			_userSolver = _userSolver + $.trim($(this).text());
		});
		if (_userSolver.length != _$ptb_td.length) {
			$.toast('还没完成！');
			return;
		}
		if (sd.CheckPuzzle(sd.getUserAnswer()) != true) {
			$.toast('一般傻逼才会填错');
			return;
		}
		if (GAME.completeTime == null) {
			GAME.completeTime = new Date().getTime();
		}
		var useTime = GAME.completeTime - GAME.currentTime;
		var json_data = {
			'gameId' : GAME.gameId,
			'details' : sd.actionSource,
			'usetime' : useTime,
			'username' : GAME.username,
			'answer' : _userSolver
		};
		$.toast('正在发送给服务器');
		$.ajax({
			url : "/sudoku/game/complete",
			data : json_data,
			async : true,
			dataType : "json",
			type : "Post",
			cache : false,
			timeout : 5000,
			success : function(json) {
				console.log(json);
				if (json != null && json.usetime != null && json.usetime > 0) {
					var second = json.usetime / 1000;
					var minutes = parseInt(second / 60);
					second = parseInt(second) % 60;
					$.toast('你的用时：' + minutes + "'" + second + "'");
					sd.Stop();
					if (playTimer != undefined) {
						playTimer.stop().once();
						playTimer = undefined;
					}
					sd.doUpdateProcess();
					sendComplete();
				} else {
					$.toast('考虑再试一次');
				}
			}
		});
	} else {
		$.toast('还没完成！');
	}
});
function select_menu() {
	var buttons1 = [  {
		text : "女警官",
		bold : true,
		color : 'success',
		onClick : function() {
			say("女警官");
		}
	}, {
		text : '你能快一点吗？？',
		bold : true,
		onClick : function() {
			say('你能快一点吗？？');
		}
	}, {
		text : '我错了~~~',
		bold : true,
		onClick : function() {
			say('我错了~~~');
		}
	}, {
		text : '我去睡了',
		bold : true,
		onClick : function() {
			say('我去睡了,你慢慢');
		}
	}];
	var buttons2 = [ {
		text : '取消',
		bg : 'danger'
	} ];
	var groups = [ buttons1, buttons2 ];
	$.actions(groups);
}
$(document).on('click', '.btn_option', function() {
	select_menu();
});