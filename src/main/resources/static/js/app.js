var contacts = [];
var messages = [];

var stompClient = null;
var username = null;
var currentChatting = null;

var wsHeader = {
		'APP-TOKEN': APP_TOKEN
};

function connectWS() {
	var socket = new SockJS('/ws');
	stompClient = Stomp.over(socket);

	stompClient.connect({}, onConnected, onError);
}

function onDisconnected() {
	stompClient = null;
	$("#btn-login").data('logged', false).text("Login").removeClass("btn-danger")
			.addClass("btn-primary");
	contacts = [];
	messages = [];
	currentChatting = null;
	username = null;
	$("#input-login").removeAttr('disabled');
	$("#input-login").val('');
	renderContact();
	$(".message").addClass('hide');
	$(".message__empty").removeClass('hide');
}

function onConnected() {
	// process button login
	$("#input-login").attr('disabled', true);
	$("#btn-login").data('logged', true).text("Logout").addClass("btn-danger")
			.removeClass("btn-primary");

	// subcribe contact topic
	stompClient.subscribe('/topic/contacts', onContactReceived);

	// subcribe message topic
	stompClient.subscribe('/topic/messages', onMessageReceived);

	// add username to server
	stompClient.send("/app/user.add", {}, JSON.stringify({
		username : username
	}));

	// get contacts
	fetchContacts();
}

function onError(error) {
	alert(error);
}

function onContactReceived(payload) {
	var message = JSON.parse(payload.body);
	if (message.username !== username) {
		if (message.type === 'LOGIN') {
			contacts.push(message);
			appendContact(message);
		} else {
			var index = contacts.findIndex(function(contact) {
				return contact.username === message.username;
			});
			var contact = Object.assign({}, contacts[index]);
			contacts.splice(index, 1);
			removeContact(contact);
		}
	}
}

function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);
	if (message.sender === currentChatting) {
		message.me = message.sender === username;
		messages.push(message);
		appendMessage(message);
	}
}

function onClickLogin(e) {
	username = $("#input-login").val();
	var btnLogin = $("#btn-login");
	if (username) {
		if (!btnLogin.data('logged')) {
			username = username.trim();
			connectWS();
		} else {
			stompClient.disconnect(onDisconnected);
		}
	}
}

function onClickSend(e) {
	var message = $("#input-message").val();
	if (message) {
		message = message.trim();
		var data = { sender: username, receiver: currentChatting, content: message };
		// add username to server
		stompClient.send("/app/message.send", {}, JSON.stringify(data));
		// append local list
		data.me = true;
		messages.push(data);
		appendMessage(data);
		$("#input-message").val('');
	}
}

function appendMessage(message) {
	console.log(message);
	var listMessage = $("#list-message");
	listMessage.append('<div class="message__item message__item--'
			+ (message.me ? 'right' : 'left') + '">'
			+ '	<div class="message__item-text  bg-primary">' + message.content
			+ '	</div>' + '</div>');
	scrollDownMessage();
}

function renderMessage() {
	var listMessage = $("#list-message");
	$(".message.hide").removeClass('hide');
	$(".message__empty").addClass('hide');
	listMessage.html('');
	for (var i = 0; i < messages.length; i++) {
		var message = messages[i];
		message.me = message.sender === username;
		appendMessage(message)
	}
}

function appendContact(contact) {
	var listContact = $("#list-contact");
	listContact.append('<a href="#" data-username="' + contact.username
			+ '" class="list-group-item list-group-item-action">'
			+ contact.username + '</a>')
}

function removeContact(contact) {
	var listContact = $("#list-contact");
	listContact.find("a[data-username='" + contact.username + "']").remove();
	if (contacts.length == 0) {
		listContact.html('<center><small>Chưa có người liên lạc</small></center>');
	}
}

function renderContact() {
	var listContact = $("#list-contact");
	if (contacts.length > 0) {
		listContact.html('');
		for (var i = 0; i < contacts.length; i++) {
			var contact = contacts[i];
			appendContact(contact);
		}
	} else {
		listContact.html('<center><small>Chưa có người liên lạc</small></center>');
	}
}

function contactEvent() {
	$("#list-contact").on('click', 'a', function(e) {
		var u = $(this).data('username');
		currentChatting = u;
		$("#label-current-chatting").text(currentChatting);
		fetchMessages();
	});
}

function fetchMessages() {
	$.ajax({
		method: 'GET',
		url: '/messages?receiver=' + currentChatting,
		headers: {
			username: username
		}
	}).then(function(data) {
		messages = [];
		for(var i = 0; i < data.length; i++) {
			messages.push(data[i]);
		}
		renderMessage();
	}).catch(function(error) {
		alert(error);
	});
}

function fetchContacts() {
	$.ajax({
		method: 'GET',
		url: '/contacts',
		headers: {
			username: username
		}
	}).then(function(data) {
		for(var i = 0; i < data.length; i++) {
			contacts.push(data[i]);
		}
		renderContact();
	}).catch(function(error) {
		alert(error);
	});
}

function scrollDownMessage() {
	$('#list-message').scrollTop($('#list-message')[0].scrollHeight);
}

$(document).ready(function() {
	$("#btn-login").click(onClickLogin);
	$("#btn-input-submit").click(onClickSend);
	contactEvent();
});