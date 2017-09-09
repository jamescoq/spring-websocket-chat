function Chat(options){

    var defaultOpts = {
    	wrapperId: ''
    };

    var opts = $.extend({}, defaultOpts, (options) ? options : {});

    var $wrapper = $('#' + opts.wrapperId);

    var stompClient = null;
    var baseUrl = '/chatapp';
    var myId = '';
	var separator = '±';
	var usersData = null;

    Chat.prototype.init = function(){
        _disconnect();
    	_connect();
    	_initializeComponents();
    }

    Chat.prototype.close = function(){
        _disconnect();
    }
	
	var _connect = function() {
		var socket = new SockJS(baseUrl + '/chat');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			_showChat();
			console.log('Connected: ' + frame);
			myId = frame.headers['user-name'];
			
			stompClient.subscribe('/queue/users', function(message) {
				var users = JSON.parse(message.body);
				usersData = users;
				_showUsers(users);
				_subscribeUserPrivateSession(users);
			});

			stompClient.subscribe('/queue/public', function(message) {
				_showMessage(JSON.parse(message.body), 'group');
				_scrollDown('group');
			});
			
			stompClient.subscribe('/user/' + myId + '/queue/private', function(message) {
				var sessionId = message.body;
				_existingSessionCheckWrapperFunction(sessionId, _subscribePrivateSession);
			});
			
			stompClient.subscribe('/app/users');
			_subscribeSessionHistory('group');
		});
	};
	
	var _subscribeUserPrivateSession = function(users) {
		var user = users[myId];
		for (var s in user.subscriptions) {
			var sessionId = user.subscriptions[s];
			_existingSessionCheckWrapperFunction(sessionId, function(sessionIdArgs) {
				if (_checkDestinationAvailability(sessionIdArgs)) {
					_subscribePrivateSession(sessionIdArgs);
					_subscribeSessionHistory(sessionIdArgs);
				}
			});
		}
	};
	
	var _subscribeSessionHistory = function(sessionId) {
		stompClient.subscribe('/app/history/'+sessionId, function(message) {
			var messages = JSON.parse(message.body);
			for (i = 0; i < messages.length; i++) {
				_showMessage(messages[i], sessionId);
			}
			_scrollDown(sessionId);
		});
	};
	
	var _existingSessionCheckWrapperFunction = function(sessionId, proceedFunction) {
		var alternativeSessionId = _getSenderIdFromSessionId(sessionId) + separator + _getReceiverIdFromSessionId(sessionId);
		
		if (!_doesTabExist(sessionId) && !_doesTabExist(alternativeSessionId)) {
			proceedFunction(sessionId);
		}
	}
	
	var _subscribePrivateSession = function(sessionId) {
		
		stompClient.subscribe('/user/' + sessionId + '/queue/private', function(message) {
			_showMessage(JSON.parse(message.body), sessionId);
			_scrollDown(sessionId);
		});
		_addSessionTab(sessionId);
		_bindRemoveTabAction(sessionId);
		_bindSessionTabClicks();
	};
	
	var _sendNewSessionInfo = function(sessionId) {
		_sendMessageApi('/app/session/' + sessionId);
	};
		
	var _disconnect = function() {
		if (stompClient != null) {
			stompClient.disconnect();
		}
		_hideChat();
		console.log("Disconnected");
	};

	var _showChat = function() {
		$wrapper.find('#chat-window').show();
		$wrapper.find('#chat-textarea').show();
	};
	
	var _hideChat = function() {
		$wrapper.find('#chat-window').hide();
		$wrapper.find('#chat-textarea').hide();
	};
	
	var _initializeComponents = function() {
		$wrapper.find('#message-area').keypress(function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13){
                  $wrapper.find('#send-message').click();
            }
		});
		
		$wrapper.find('#send-message').click(function() {
			_sendMessage();
		});
		
		$wrapper.find('#logout-btn').click(function() {
			_disconnect();
		});
		
		_bindSessionTabClicks();
	};
	
	var _sendMessage = function() {
		var $textArea = $wrapper.find('#message-area');
		var text = $textArea.val().trim();
		
		if (!text) {
			$textArea.parents('.form-group').addClass('has-error');
			return;
		} else {
			$textArea.parents('.form-group').removeClass('has-error');
		}
		
		var sessionId = $wrapper.find('#session-contents div.tab-pane.active').attr('id');
		var destination = (sessionId === 'group') ? '/app/public' : '/app/private/' + sessionId;
		if (sessionId === 'group' || _checkDestinationAvailability(sessionId)) {
			_sendMessageApi(destination, { 'content' : text });
		} else {
			var message = {
					date: '----------',
					user: 'system',
					content: 'Remote user has logged out. Close this tab and initiate new chat session.'
			}
			_showMessage(message, sessionId);
		}
		
		$textArea.val('').blur();
		$textArea.focus();
	};
	
	var _sendMessageApi = function(destination, jsonContent) {
		stompClient.send(destination, {}, (jsonContent) ? JSON.stringify(jsonContent) : '');
	};

	var _showMessage = function(message, sessionId) {
		var $newMessage = $('<p>');
		$newMessage.append(message.date + " : " + message.user + " - "
				+ message.content);
		var $groupBox = $wrapper.find('#'+sessionId);
		var $responseArea = $groupBox.find('.messages-wrapper');
		$responseArea.append($newMessage);
	
		_showSessionTab(sessionId, true);
	};
	
	var _scrollDown = function(sessionId) {
		var $groupBox = $wrapper.find('#'+sessionId);
		$groupBox.animate({
	        scrollTop: $groupBox.find('.anchor').offset().top
	    });
	};

	var _showUsers = function(users) {
		var $userArea = $wrapper.find('#chat-users');
		$userArea.empty();
		for ( var i in users) {
			var user = users[i];
			var userId = user.userId;
			if (userId !== myId) { 
				$userArea.append('<li data-userid="' + userId + '">' + user.name + '</li>');
			}
		}
		_bindUserClicks();
	};
	
	var _addSessionTab = function(sessionId) {
		var name =  (_getSenderIdFromSessionId(sessionId) !== myId) ? _getSenderNameFromSessionId(sessionId) : _getReceiverNameFromSessionId(sessionId); 
		
		var $sessionHeading = $('<li data-sessionid="' + sessionId + '"><a data-toggle="tab" href="#' + sessionId + '"><span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>' + name + '</a></li>');
		var $sessionContent = $('<div id="' + sessionId+ '" class="tab-pane fade in mh-sm mh-md mh-xs"><h3>Relation with user ' + name + '</h3><div class="messages-wrapper"></div><span class="anchor"></span></div>');
		$wrapper.find('#session-headings').append($sessionHeading);
		$wrapper.find('#session-contents').append($sessionContent);
	};
	
	var _bindUserClicks = function() {
		$wrapper.find('ul#chat-users li').dblclick(function() {
			var receiverId = $(this).data('userid');
			
			var sessionId =  receiverId + separator + myId;
			var alternativeSessionId = myId + separator + receiverId;
			
			if (!_doesTabExist(sessionId)) {
				if(!_doesTabExist(alternativeSessionId)) {
					_subscribePrivateSession(sessionId);
					_sendNewSessionInfo(sessionId);
				} else {
					_showSessionTab(alternativeSessionId, false);
				}	
			} else {
				_showSessionTab(sessionId, false);
			}
			_activateTab(sessionId)
		});
	};
	
	var _bindRemoveTabAction = function(sessionId) {
		$wrapper.find('#session-headings li[data-sessionid="' + sessionId + '"]').find('span.glyphicon').click(function(e) {
			e.stopPropagation();
			_hideSessionTab(sessionId);
		});
	};
	
	var _bindSessionTabClicks = function() {
		$wrapper.find('#session-headings li').click(function() {
			$(this).removeClass('waiting');
		});
	};

	var _hideSessionTab = function(sessionId) {
		$wrapper.find('#session-headings li[data-sessionid="' + sessionId + '"]').hide();
		$wrapper.find('#'+sessionId).removeClass('active');
		_activateTab();
	};
	
	var _showSessionTab = function(sessionId, incommingMsg) {
		var $tab = $wrapper.find('#session-headings li[data-sessionid="' + sessionId + '"]');
		if (!$tab.hasClass('active') && incommingMsg) {
			$tab.addClass('waiting');
		}
		$tab.show();
	};
	
	var _doesTabExist = function(sessionId) {
		var $sessionTab = $wrapper.find('#session-headings li[data-sessionid="' + sessionId + '"]');
		return $sessionTab.length !== 0;
	};
	
	var _activateTab = function(sessionId) {
		if (sessionId) {
			$('#session-headings li[data-sessionid="' + sessionId + '"] a').click();
		} else {
			$('#session-headings a:first').click();
		} 
	};
	
	var _checkDestinationAvailability = function(sessionId) {
		var senderSessionId = _getSenderIdFromSessionId(sessionId);
		var receiverSessionId = _getReceiverIdFromSessionId(sessionId);
		var compareId = (senderSessionId === myId) ? receiverSessionId : senderSessionId;
		
		return usersData[compareId] !== undefined;
	};
	
	var _getSenderNameFromSessionId = function(sessionId) {
		return _getSenderIdFromSessionId(sessionId).split('_')[0];
	};
	var _getReceiverNameFromSessionId = function(sessionId) {
		return _getReceiverIdFromSessionId(sessionId).split('_')[0];
	};
	var _getReceiverIdFromSessionId = function(sessionId) {
		return sessionId.split(separator)[0];
	};
	var _getSenderIdFromSessionId = function(sessionId) {
		return sessionId.split(separator)[1];
	};
}
