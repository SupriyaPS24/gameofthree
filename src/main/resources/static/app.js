var stompClient = null;
let currentNumber;
let adjustment;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	if (connected) {
		$("#conversation").show();
		$("#nameArea").show();
	}
	else {
		$("#conversation").hide();
		$("#nameArea").hide();
	}
	$("#GameEventsTable").html("");
}

function joinTheGame() {
	var socket = new SockJS('/gameOfThree');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/gameEvents', function(message) {
			showGameEvents(message.body);
		});
		stompClient.subscribe('/user/queue/playerEvents', function(message) {
			const obj = JSON.parse(message.body);
			showPlayerEvents(obj.message);
			if (obj.turn !== 'PLAYER_WON') {
				$("#turn").val(obj.turn);
				var isAutomatic = $("#automaticPlayer").val();
				if (obj.message === "Welcome to Game Of Three") {
					$("#role").val("PLAYER1");
					if (obj.playerName !== '') {
						$("#playerNameDisplayArea").text(obj.playerName);
					}
					$("#playerNameDisplayArea").attr("style", "display:block");
				} else if (obj.message === "You are now connected, enjoy the game") {
					$("#role").val("PLAYER2");
					if (obj.playerName !== '') {
						$("#playerNameDisplayArea").text(obj.playerName);
					}
					$("#playerNameDisplayArea").attr("style", "display:block");

					if (obj.turn === "PLAYER2_TURN") {
						$("#buttonsArea").attr("style", "display:block");
					} else {
						$("#buttonsArea").attr("style", "display:none");
					}
				} else {
					if (obj.turn === "PLAYER1_TURN" && $("#role").val() === "PLAYER1") {
						if (isAutomatic) {
						    var flag = true;
                            retrieveCurrentNumber(flag);
							stompClient.send("/app/play", {}, JSON.stringify({ 'number': adjustment }));
						} else {
							$("#buttonsArea").attr("style", "display:block");
						}
					}
					else if (obj.turn === "PLAYER2_TURN" && $("#role").val() === "PLAYER2") {
						if (isAutomatic) {
						    var flag = true;
						    retrieveCurrentNumber(flag);
							stompClient.send("/app/play", {}, JSON.stringify({ 'number': adjustment }));
						} else {
							$("#buttonsArea").attr("style", "display:block");
						}
					} else {
						$("#buttonsArea").attr("style", "display:none");
					}
				}
			}
		});
	});
}

function leaveTheGame() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
	$("#playerEventsTable").empty();
	$("#GameEventsTable").empty();
	$("#playerNameDisplayArea").text("");
	$("#playerNameDisplayArea").attr("style", "display:none");
	$("#turn").val("");
	$("#role").val("");

}

function sendName() {
	var automatic = false;
	var flag = false;
	if ($('#automatic').is(":checked")) {
		automatic = true;
		$("#automaticPlayer").val(automatic);
	}
	stompClient.send("/app/register", {}, JSON.stringify({ 'name': $("#name").val(), 'automatic': automatic }));
	retrieveCurrentNumber(flag);
	$("#nameArea").attr("style", "display:none");
}

function sendButtonInput(value) {
    var flag = true;
    retrieveCurrentNumber(flag);
    if (value == adjustment) {
    stompClient.send("/app/play", {}, JSON.stringify({ 'number': adjustment }));
    $("#buttonsArea").attr("style", "display:block");
    }
    else {
      showPopup();
}
}

function retrieveCurrentNumber(flag){
stompClient.subscribe('/topic/currentNumber', function (message) {
         currentNumber = JSON.parse(message.body);
        console.log('Current Number:', currentNumber);
    });
        console.log("outside stomp",Number(currentNumber))
    if(flag === true){
    adjustment = calculateAdjustment(currentNumber);

       console.log("Calculated adjustment:", adjustment);
       const newNumber = currentNumber + adjustment;
               console.log('new Number:', newNumber);
       }
       return
}

function calculateAdjustment(currentNumber) {
    if ((currentNumber + 1) % 3 === 0) {
        return 1;
    }
    else if ((currentNumber - 1) % 3 === 0) {
        return -1;
    }
    else {
        return 0;
    }
}

function showGameEvents(message) {
	$("#GameEventsTable").append("<tr><td>" + message + "</td></tr>");
}

function showPlayerEvents(message) {
	$("#playerEventsTable").append("<tr><td>" + message + "</td></tr>");
}

function showPopup() {
    document.getElementById('popup').classList.add('show');
}

function closePopup() {
    document.getElementById('popup').classList.remove('show');
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() { joinTheGame(); });
	$("#disconnect").click(function() { leaveTheGame(); });
	$("#send").click(function() { sendName(); });
	$("#minusOne").click(function() { sendButtonInput(-1); });
	$("#zero").click(function() { sendButtonInput(0); });
	$("#plusOne").click(function() { sendButtonInput(1); });
});

