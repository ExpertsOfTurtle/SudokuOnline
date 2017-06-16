var HOST = "http://" + window.location.host + "/";
var GAME = {
	startTime:null,
	level:null,
	gameMode:null,
	gameId:null,
	username:null,
	startTime:null,
	startGameTimer:null,
	currentTime:null,
	completeTime:null
}
var stompClient = null;	//游戏内部的连接：聊天，对战过程
function onCreateGame() {
	clearInterval(tm);
	var url = HOST + "sudoku/game/create";
	var param = {
		"username":$('input[name="user"]:checked').val(),
		"secondToStart":1800,
		"title":"System",
		"level":$("[name=level]").val(),
		"gameMode":$("[name=gameMode]").val()
	}
	console.log(param);
	$.ajax({
		type:"post",
		url:url,
		headers: {'Content-type': 'application/json;charset=UTF-8'},
		data:JSON.stringify(param),
		dataType:"json",
		success:function(response) {
			wf=JSON.stringify(response);
			console.log(response);
			$("#waiting").show();
			$("#create").hide();
			$("#level").html(wf.level);
			joinGame(response.id);
		},
		error:function(er) {
			console.log("error");
		}
	});
}

function disconnectGame(client) {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    stompClient = null;
    console.log('Disconnected');
}
function getAllGamesInfo() {
	var url = HOST + "sudoku/game/queryGame";
	var param = {		
	}
	$("#games").html("Refreshing...");
	console.log(param);
	$.ajax({
		type:"post",
		url:url,
		headers: {'Content-type': 'application/json;charset=UTF-8'},
		data:JSON.stringify(param),
		dataType:"text",
		success:function(response) {
			$("#games").html(response);
		},
		error:function(er) {
			console.log("error");
			$("#games").html("Fail");
		}
	});
}

function joinGame(gid) {
	clearInterval(tm);
	disconnectGame();
	GAME.gameId = gid;
	console.log("joining game:" + gid);
    var socket = new SockJS('/sudoku/endpointSang');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
//        setConnected(true);
        console.log('Connected:' + frame);
        $("#waiting").show();
		$("#create").hide();
        stompClient.subscribe('/topic/game/' + GAME.gameId, function (response) {
        	var bd = JSON.parse(response.body);
        	if (bd.messageType == "Chat") {
        		showResponse(bd);	
        	} else if (bd.messageType == "Start") {
        		var timestamp = bd.timestamp;
        		$("#btnStart").attr("disabled","disabled")
        		GAME.startTime = new Date().getTime() + 5 * 1000;
        		GAME.startGameTimer = setInterval(onCountingTime,500);
        	} else if (bd.messageType == "Join") {
        		welcomeJoin(bd);
        	} else if (bd.messageType == "Update") {
        		updateStatus(bd);
        	}
        });
        doJoin();
    });
    
}
function startGame() {
	var nowTime = new Date().getTime();
	var time = nowTime + 5 * 1000;
	var obj = {
		"gameId":GAME.gameId,
		"timestamp":time,
		"username":$('input[name="user"]:checked').val(),
		"requestType":"Start"
	} 
	stompClient.send("/start", {}, JSON.stringify(obj));
}
function onCountingTime() {
	var t = new Date().getTime();
	var dif = GAME.startTime - t;
	if (dif < 0) {
		clearInterval(GAME.startGameTimer);
		GAME.startGameTimer = null;
		$("#systemInfo").html("Start!");
		getProblem();
	} else {
		var msg = "还有 " + dif + " 毫秒开始比赛";
		$("#systemInfo").html(msg);
	}
}
function chat() {
	var msg = $("#text").val();
	var obj = {
		"username":$('input[name="user"]:checked').val(),
		"message":msg,
		"gameId":GAME.gameId,
		"requestType":"Chat"
	};
	stompClient.send("/chat", {}, JSON.stringify(obj));
}
function doJoin() {
	var msg = $("#text").val();
	var obj = {
		"username":$('input[name="user"]:checked').val(),
		"message":msg,
		"gameId":GAME.gameId,
		"requestType":"Join"
	};
	stompClient.send("/join", {}, JSON.stringify(obj));
}
function sendUpdate(process) {
	var msg = $("#text").val();
	var obj = {
		"username":$('input[name="user"]:checked').val(),
		"gameId":GAME.gameId,
		"process":process,
		"details": sd.actionSource,
		"requestType":"Update"
	};
	stompClient.send("/updateStatus", {}, JSON.stringify(obj));
}
function getProblem() {
	var url = HOST + "sudoku/game/getProblem/" + GAME.gameId;
	$.ajax({
		type:"post",
		url:url,
		headers: {'Content-type': 'application/json;charset=UTF-8'},
		data:"",
		dataType:"json",
		success:function(response) {
			console.log(response);
			var problem = response.problem;
			updateBoard(problem);
			currentTime = 0;
			GAME.currentTime = new Date().getTime();
			GAME.completeTime = null;
			GAME.username = $('input[name="user"]:checked').val();
			sd.Start();
			$("#play").show();
			$("#waiting").hide();
			$("#create").hide();
		},
		error:function(er) {
			console.log("error");
			$("#games").html("Fail");
		}
	});
}
function updateBoard(problem) {
	console.log(problem);
	var idx = 0;
	for (var i = 1; i <= 9; i++) {
		for (var j = 1; j <= 9; j++) {
			var key = "#k" + j + "s" + i;
			if (problem[idx] == '0') {
				$(key).html("&nbsp;");
			} else {
				$(key).html("<span class='fixedCell'>" + problem[idx] + "</span>");
			}
			idx++;
		}
	}
}
function showResponse(obj) {
	console.log(obj);
    var msg = obj.username + ":" + obj.message + "<br>" + $("#chat").html();
    console.log("msg:" + msg);
    $("#chat").html(msg)
}
function welcomeJoin(obj) {
	console.log(obj);
    var msg = "welcome " + obj.username + " !!<br>" + $("#chat").html();
    console.log("msg:" + msg);
    $("#chat").html(msg);
    
    var userList = obj.userList;
    for (var i = 0; i < userList.length; i++) {
    	var username = userList[i].username;
    	var id = "li_" + username;
        var obj = $("#" + id);
        if (obj.length == 0) {
        	var x=$("<li><div/></li>");
        	$(x).attr("id", id);
        	$("#otherPlayers ul").append(x);
//        	$(x).html(username);
        }
    }
}
function updateStatus(obj) {
	var username = obj.username;
	var process = obj.process;
	var x = "#li_" + username + " >div";
	$(x).html(obj.username + ":" + process + "%");
	$(x).css("width", process + "%");
	if (process < 20) {
		$(x).css("background-color", "#e23e0d");
	} else if (process < 40) {
		$(x).css("background-color", "#daa328");
	} else if (process < 60) {
		$(x).css("background-color", "#5882f7");
	} else if (process < 80) {
		$(x).css("background-color", "#35c7d6");
	} else if (process <= 100) {
		$(x).css("background-color", "#2ada38");
	}
}