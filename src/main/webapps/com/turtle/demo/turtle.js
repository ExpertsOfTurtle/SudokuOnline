var HOST = "http://" + window.location.host + "/";
var GAME = {
	problemId:null,
	startTime:null,
	level:null,
	gameMode:null,
	gameId:null,
	username:null,
	startTime:null,
	startGameTimer:null
}
var stompClient = null;	//游戏内部的连接：聊天，对战过程
function onCreateGame() {
	clearInterval(tm);
	var url = HOST + "sudoku/game/create";
	var param = {
		"username":$('input:radio[name="user"]:checked').val(),
		"secondToStart":120,
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
			$("#level").html(wf.level);
			$("#create").hide();
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
	gameId = gid;
	console.log("joining game:" + gid);
    var socket = new SockJS('/sudoku/endpointSang');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
//        setConnected(true);
        console.log('Connected:' + frame);
        $("#waiting").show();
		$("#create").hide();
        stompClient.subscribe('/topic/game/' + gameId, function (response) {
        	bd = JSON.parse(response.body);
        	if (bd.messageType == "Chat") {
        		showResponse(JSON.parse(response.body));	
        	} else if (bd.messageType == "Start") {
        		var timestamp = bd.timestamp;
        		$("#btnStart").attr("disabled","disabled")
        		startTime = new Date().getTime() + 10 * 1000;
        		startGameTimer = setInterval(onCountingTime,500);
        	}            
        })
    });
}
function startGame() {
	var nowTime = new Date().getTime();
	var time = nowTime + 10 * 1000;
	var obj = {
		"gameId":gameId,
		"timestamp":time,
		"username":$('input:radio[name="user"]:checked').val(),
		"requestType":"Start"
	}
	stompClient.send("/start", {}, JSON.stringify(obj));
}
function onCountingTime() {
	var t = new Date().getTime();
	var dif = startTime - t;
	if (dif < 0) {
		clearInterval(startGameTimer);
		startGameTimer = null;
		$("#systemInfo").html("Start!");
	} else {
		var msg = "还有 " + dif + " 毫秒开始比赛";
		$("#systemInfo").html(msg);
	}
}
function chat() {
	var msg = $("#text").val();
	var obj = {
		"username":$('input:radio[name="user"]:checked').val(),
		"message":msg,
		"gameId":gameId,
		"requestType":"Chat"
	};
	stompClient.send("/chat", {}, JSON.stringify(obj));
}
function showResponse(obj) {
	console.log(obj);
    var msg = obj.username + ":" + obj.message + "<br>" + $("#chat").html();
    console.log("msg:" + msg);
    $("#chat").html(msg)
}