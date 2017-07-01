var HEALTH = {
	timeout: 1000,//60ms
	timeoutObj: null,
	reset: function(){
	    clearTimeout(this.timeoutObj);
	},
	start: function(){
		this.reset();
		this.timeoutObj = setInterval(function(){
			if (stompClient.connected == false) {
				var gid = GAME.gameId;
				var status = GAME.gameStatus;
				joinGame(gid, status);
			}
		}, this.timeout);
		
	}
}