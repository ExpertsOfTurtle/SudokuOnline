var HOST = "http://" + window.location.host + "/";
var GAME = {
	problemId:null,
	startTime:null,
	level:null,
	gameMode:null,
	gameId:null
}
var stompClient = null;
var commonClient = null;
function onCreateGame() {
	var url = HOST + "sudoku/game/create";
	var param = {
		"username":$("[name=user]").val(),
		"secondToStart":20,
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
			$("#level").html(wf);
			$("#create").hide();
			joinGame(response);
		},
		error:function(er) {
			console.log("error");
		}
	});
}
function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log('Disconnected');
}
function connect() {
    var socket = new SockJS('/sudoku/endpointSang');
    commonClient = Stomp.over(socket);
    commonClient.connect({}, function (frame) {
        console.log('Connected:' + frame);
        commonClient.subscribe('/topic/create', function (response) {
            var json = JSON.parse(response.body);
            var html = json.level + " by " + json.creator + "<br>" + $("#games").html();
        	$("#games").html(html);
        })
    });
}
function joinGame(gid) {
	var gameId = gid;
    var socket = new SockJS('/sudoku/endpointSang');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected:' + frame);
        stompClient.subscribe('/topic/joingame/' + gameId, function (response) {
            showResponse(JSON.parse(response.body).responseMessage);
        })
    });
}
function showResponse(message) {
    var msg = message + "<br>" + $("#response").html();
    $("#response").html(msg)
}