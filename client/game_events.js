function doNothing() {

}

function handleEvent(obj, game_logic)
{
  
    if (obj.type == 'player')
    {        
        var x = obj.x;
        var y = obj.y;
        var z = obj.z;

        var alpha = obj.alpha;
        
        var health = obj.health;
        var maxHealth = obj.maxHealth;
        
        var id = obj.id;
        
        var player = game_logic.getPlayer(id);
        if (!player)
        {
            player = new PlayerLogic(system);
            player.id = id;
            game_logic.addPlayer(player, id);
        }
        
        player.health = health;
        player.maxHealth = maxHealth;
        player.updatePosition(x, y, z);
        player.updateAlpha(alpha);
    }

    if (obj.type == 'map'){
        game_logic.map = obj;
        game_logic.mapUpdated = true;
        console.log('received the map');
    }
    
    if (obj.type == "you"){
        own_player_id = obj.id;
        var player = new PlayerLogic(system);
        player.id = obj.id;
        game_logic.addPlayer(player, obj.id);
        game_logic.current_player_id = player.id;
        document.getElementById("welcome").innerHTML = "Welcome, " + player.id.toString() + "!";
    }
    
    if (obj.type == 'bomb')
    {
        var number = obj.no;
        var action = obj.action;
        var x = obj.x;
        var y = obj.y;
        if (action == 'add')
        {
            game_logic.addBomb(number, x, y);
        } else if (action == 'remove')
        {
            game_logic.removeBomb(number);
        }
    }
    
    if (obj.type == 'chatMessage')
    {
      if (game_logic.in_game)
      {
        chatHideTime = system.now() + 5000;
        $('#chat-div').show();
      }
      var id = obj.id;
      var message = obj.message;
      addMessage(id, message);
    }
    
    if (obj.type == 'powerup')
  {
      var x = obj.x;
      var y = obj.y;
      var action = obj.action;
      var powerUpType = obj.p_type;
      if (action == 'add')
      {
        game_logic.addPowerUp(x, y, powerUpType);
      } else if (action == 'remove')
    {
        game_logic.removePowerUp(x, y, powerUpType);
    }
  }
    if (obj.type == 'exp')
    {
        var x = obj.x;
        var y = obj.y;
        var shape = obj.shape;
        game_logic.addExplosion(x, y, shape);
    }
    
    if (obj.type == "inlobby")
    {
        var list = obj.list;
        
        console.log('Received inlobby list ' + list);
        
        // get the document part to be modified
        var doc = document.getElementById("lobbylist");
        if (!doc) {return};
        var acc = "";
        for (var i = 0; i < list.length; i++)
        {
            var user = list[i];
            acc += '<div class="row" style="margin-left: 2px;">' + user + '</div>';
        }

        doc.innerHTML = acc;
        document.getElementById('invite-and-play').classList.remove('display-none');
        document.getElementById('lobby-button').classList.remove('button-thing');
        document.getElementById('lobby-button').classList.add('in-button-thing');
        document.getElementById('lobby-button').setAttribute("onclick", 'doNothing();');
        document.getElementById('exit_lobby_button').classList.remove('inactive-button');

        
    }
    
    
    if (obj.type == 'pendingfriends')
    {
        function getFriendCode(name)
        {
            var result = '<div class="row"><div class="col-md-6">';
            result += name;
            result += '</div><div class="col-md-3"><button class="btn join btn-default" onclick="game_controller.acceptFriend(\'' + name + '\')">Accept</button></div>';
            result += '<div class="col-md-3"><button class="btn join btn-default" onclick="game_controller.rejectFriend(\'' + name + '\')">Reject</button></div></div>'
            
            return result;
        }
        
        var list = obj.friends;
        
        // get the document part to be modified
        var doc = document.getElementById('pending_friends_list');
        if (!doc) {return;}
        var acc = '';

        for (var i = 0; i < list.length; i++)
        {
            var user = list[i];
            acc += getFriendCode(user);
        }
        
        doc.innerHTML = acc;

    }
    
    if (obj.type == 'listfriends')
    {
        document.getElementById('friends-list').innerHTML = '';
        function getFriendCode(name)
        {
          document.getElementById('friends-list').innerHTML += generateFriendEntry(name);
          enableTooltip();
        }
        
        var list = obj.friends;

        for (var i = 0; i < list.length; i++)
        {
            var user = list[i];
            getFriendCode(user);
        }
    }
    
    
    if (obj.type == 'lobbies')
    {
        function getLobbyCode(name)
        {
            var result = '<div class="row"><div class="col-md-6">';
            result += name;
            result += '</div><div class="col-md-6"><button class="btn join btn-default" onclick="game_controller.joinLobby(\'' + name + '\')">Join Game</button></div></div>';
            
            return result;
        }
        
        var list = obj.list;
        
        // get the document part to be modified
        var doc = document.getElementById('invitelist');
        if (!doc) {return;}
        var acc = '';

        for (var i = 0; i < list.length; i++)
        {
            var user = list[i];
            acc += getLobbyCode(user);
        }
        
        doc.innerHTML = acc;
        
        document.getElementById('invite-and-play').classList.add('display-none');
        document.getElementById('lobby-button').classList.add('button-thing');
        document.getElementById('lobby-button').classList.remove('in-button-thing');
        document.getElementById('lobby-button').setAttribute("onclick", 'send_new_lobby();');
        document.getElementById('invite-button').setAttribute("onclick", 'doNothing();');
        document.getElementById('invite-button').classList.remove('join');
        document.getElementById('invite-button').classList.add('inactive-button');
        document.getElementById('start-game-button').setAttribute("onclick", 'doNothing();');
        document.getElementById('start-game-button').classList.remove('join');
        document.getElementById('start-game-button').classList.add('inactive-button');
        document.getElementById("showMenu").classList.add("display-thing");
       
    }
    
    if (obj.type == 'death')
    {
        var id = obj.id;
        game_logic.removePlayer(id);
        if (id == game_logic.current_player().id && game_logic.in_game)
        {
            //alert("You died!");
            showEndOfGameScreen("You died! <br> ");
        }
    }
    
    if (obj.type == 'gameend')
    {
        document.getElementById("chat-div").style['visibility']= 'hidden';
        document.getElementById("in_game_hints").style['visibility']= 'hidden';
        game_logic.in_game = false;
        var winner = obj.winner;
        var s = 'Game finished!\n';
        s += 'Winner: <br>' + winner;
        showEndOfGameScreen(s);
        
        // Remove all the existing players except the current player
        game_logic.removeAllPlayers();
    }
    
    if (obj.type == 'initgame')
    {
        document.activeElement.blur();
        
        document.getElementById("renderCanvas").classList.remove("hide-it");
        document.getElementById("showMenu").classList.remove("display-thing");
        document.getElementById("chat-div").style['visibility']= 'visible';
        document.getElementById("in_game_hints").style['visibility']= 'visible';
        
        // show full screen canvas and prevent scrolling
        var body = document.getElementsByTagName("BODY")[0];
        body.classList.add("unscrollablebody");
        
        game_logic.in_game = true;
    }
    
    if (obj.type == 'lobbyhost')
    {
        document.getElementById('invite-button').setAttribute("onclick", 'invite_user();');
        document.getElementById('invite-button').classList.add('join');
        document.getElementById('invite-button').classList.remove('inactive-button');
        document.getElementById('start-game-button').setAttribute("onclick", 'start_lobby_game();');
        document.getElementById('start-game-button').classList.add('join');
        document.getElementById('start-game-button').classList.remove('inactive-button');
        document.getElementById('exit_lobby_button').classList.add('join');
        document.getElementById('invite-and-play').classList.remove('display-none');
    }

    if(obj.type == 'registrationsuccess') {
        alert("You have registered successfully! Now let's bomb!");
        $('#login-window').fadeIn();
        $('#register-window').fadeOut();
    }

    if(obj.type == 'registrationfailed') {
        alert(obj.reason);
    }
    
    if (obj.type == 'playerstats')
    {
      var experience = obj.experience;
      var wins = obj.wins;
      var totalgames = obj.totalgames;
      var losses = totalgames - wins;
      
     // var element = document.getElementById("client_player_statistics");
      //element.innerHTML = "<p>Experience: " + experience + "<\/p>\r\n<p>Games won: "+ wins +"<\/p>\r\n<p>Games played: "+ totalgames +"<\/p>"
   

      //var expstat = document.getElementById("Stats");
      var formattedStats = "Statistics: Exp = " + experience;
      $("#Stats").replaceWith("<div class=\"panel-heading\" id=\"Stats\">" + formattedStats + "</div>");

      var doctx = document.getElementById("chart-area3").getContext("2d");
      doughnutData[0].value = losses;
      doughnutData[1].value = wins;
      window.myDoughnut = new Chart(doctx).Doughnut(doughnutData, {responsive : true});
    }

    if (obj.type = 'topfriends')
    {
      var rivals = obj.rivals;
      var i = 1;
      for(rival in rivals){
        var riv = rivals[rival];
        var user = riv.username;
        var wins = riv.wins;

        var formatRivals = "<tr>\r\n<th scope=\"row\">" + i + "</th>\r\n<td>" + user + "</td>\r\n<td>" + 
                            wins + "</td>\r\n</tr>\r\n<tr>";

        $("#rivals").append(formatRivals);

        i++;            
      }


    }
    
    if (obj.type == 'loginfailed')
    {
      alert('Login failed\n Both username and password are case sensitive');
      $("#login-overlay").fadeIn();
      goToLogin();
    }
    
    if (obj.type == 'alert')
    {
        alert(obj.msg);
    }
}

function generateFriendEntry(friendName) {
 var result = '<div class="row" id="'+ friendName +'"> <div class="col-md-6">'
     + friendName + '</div> <div class="col-md-6">' +
        /*'<button class="btn join btn-default small-button friend-profile" ' +
    'style="border-color: #3366ff; color: #3366ff;" onclick="viewPlayerProfile(\'' + friendName + '\')">P' +
        '</button>'*/ '<button class="btn join btn-default small-button friend-game" ' +
    'style="border-color: #3366ff; color: #3366ff;" onclick="playWithPlayer(\'' + friendName + '\')">G' +
        '</button>'+ 
       /* <button class="btn join btn-default small-button friend-chat" ' +
    'style="border-color: #3366ff; color: #3366ff;" onclick="chatWithFriend(\'' + friendName + '\')">C' +
     '<button class="btn join btn-default small-button friend-remove"' +
     'style="border-color: #3366ff; color: #3366ff; margin-left: 4px;" onclick="removeFriend(\'' + friendName + '\')">R' +
        '\n</button>*/ '</div></div>';

    return result;
}

function showEndOfGameScreen(str) {
    document.getElementById('end-of-game-screen').classList.remove('none-it');
    document.getElementById('result').innerHTML = str;
}

function go_back_to_menu() {
    var body = document.getElementsByTagName("BODY")[0];
    body.classList.remove("unscrollablebody");
    resetToInitialState();
    document.getElementById('end-of-game-screen').classList.add('none-it');
}

function resetToInitialState() {
    // document.getElementById('invite-and-play').classList.add('display-none');
//     document.getElementById('lobby-button').classList.add('button-thing');
//     document.getElementById('lobby-button').classList.remove('in-button-thing');
//     document.getElementById('lobby-button').setAttribute("onclick", 'send_new_lobby();');
//     document.getElementById('invite-button').setAttribute("onclick", 'doNothing();');
//     document.getElementById('invite-button').classList.remove('join');
//     document.getElementById('invite-button').classList.add('inactive-button');
//     document.getElementById('start-game-button').setAttribute("onclick", 'doNothing();');
//     document.getElementById('start-game-button').classList.remove('join');
//     document.getElementById('start-game-button').classList.add('inactive-button');
//     document.getElementById("showMenu").classList.add("display-thing");
    document.getElementById("renderCanvas").classList.add("hide-it");
}

function GameController(game_logic)
{
    var self = this;
    var chatOpened = false;
    
    this.game_logic = game_logic;

    // Network tools ===========================================================

    this.socket = new WebSocket(socketURL);
    

    this.socket.onopen = function() {
        console.log('Connection opened');
    };
    
    this.socket.onmessage = function(message) {
        //event handler when data has been received from the server
        var obj = JSON.parse(message.data);
        handleEvent(obj, game_logic);
    };
    
    this.socket.onclose = function() {
        //event handler when the socket has been properly closed
        console.log('Connection closed');
    };
    
    this.socket.onerror = function() {
        //event handler when an error has occurred during communication
        console.log('Connection error');
    };

    
    this.send = function(msg)
    {
        try
        {
            this.socket.send(msg);
        } catch (err)
        {
            console.log('tried to send ' + msg + ' but connection is not open yet');
        }
    };
    
    this.joinLobby = function(name)
    {
        var obj =
        {
            "type": "join",
            "id": game_logic.current_player().id,
            "name": name
        }
        console.log(JSON.stringify(obj));
        self.send(JSON.stringify(obj));
    };
    
    this.acceptFriend = function(name)
    {
      obj =
      {
        'type':'acceptfriend',
        'id':game_logic.current_player().id,
        'friend':name
      };
      self.send(JSON.stringify(obj));
    }
    
    this.rejectFriend = function(name)
    {
      obj =
      {
        'type':'rejectfriend',
        'id':game_logic.current_player().id,
        'friend':name
      };
      self.send(JSON.stringify(obj));
    }
    
    // Key listeners ===========================================================
    this.keyPressedListener = function(e)
    {
      if (!game_logic.in_game)
      {
        return;
      }
      
      if (this.chatOpened) {
        if (e.keyCode == 13) {
              if (game_logic.in_game)
              {
                chatHideTime = system.now() + 5000;
                $('#chat-div').show();
              }
              $('#chatBox').hide();
              $('#chat-div div').css('overflowY', 'hidden');
              this.chatOpened = false;
            }
      } 
      
      else if (e.keyCode == 87)
        {
            handleKeyPress('W', game_logic.current_player(), this.send);
        } else if (e.keyCode == 65)
        {
            handleKeyPress('A', game_logic.current_player(), this.send);
        } else if (e.keyCode == 83)
        {
            handleKeyPress('S', game_logic.current_player(), this.send);
        } else if (e.keyCode == 68)
        {
            handleKeyPress('D', game_logic.current_player(), this.send);
        } else if (e.keyCode == 32)
        {
          console.log('detected a [space]');
          handleSpaceOn();
        } else if (e.keyCode == 84) {
            // Attack key
          var p = game_logic.current_player();
          p.attack = true;
          game_controller.send(p.sendActionUpdate());
        } else if (e.keyCode == 16) {
          var p = game_logic.current_player();
          p.speedBoost = true;
          game_controller.send(p.sendActionUpdate());

        } else if (e.keyCode == 69)
        {
            handleKeyPress('E', game_logic.current_player(), this.send);
        } else if (e.keyCode == 13) {
          if (game_logic.in_game)
          {
            chatHideTime = system.now() + 5000;
            $('#chat-div').show();
          }
          $('#chatBox').show(50, function() {document.getElementById("chatInput").focus();});
          $('#chat-div div').css('overflowY', 'auto');
          this.chatOpened = true;
        }
    };
    
    this.keyReleasedListener = function(e)
    {
        if (e.keyCode == 87)
        {
            handleKeyRelease('W', game_logic.current_player(), this.send);
        } else if (e.keyCode == 65)
        {
            handleKeyRelease('A', game_logic.current_player(), this.send);
        } else if (e.keyCode == 83)
        {
            handleKeyRelease('S', game_logic.current_player(), this.send);
        } else if (e.keyCode == 68)
        {
            handleKeyRelease('D', game_logic.current_player(), this.send);
        } else if (e.keyCode == 32) {
          handleSpaceOff();
        } else if (e.keyCode == 16) {
          var p = game_logic.current_player();
          p.speedBoost = false;
          game_controller.send(p.sendActionUpdate());
        }
    }

}

function handleSpaceOn()
{
  var p = game_logic.current_player();
  p.jump = true;
  game_controller.send(p.sendActionUpdate());
}
function handleSpaceOff()
{
  var p = game_logic.current_player();
  p.jump = false;
  game_controller.send(p.sendActionUpdate());
}

function handleKeyPress(k, p, sender)
{

    if (k == 'W')
    {
        p.forward = 1;
    } else if (k == 'A')
    {
        p.left = 1;
    } else if (k == 'S')
    {
        p.backward = 1;
    } else if (k == 'D')
    {
        p.right = 1;
    } else if (k == 'E')
    {
        // Send message to place a bomb
        var obj = 
        {
            'type':'placebomb',
            'id':p.id
        };
        game_controller.send(JSON.stringify(obj));
        return;
    }
    console.log(p.sendActionUpdate());
    game_controller.send(p.sendActionUpdate());
}

function handleKeyRelease(k, p, sender)
{

    if (k == 'W')
    {
        p.forward = 0;
    } else if (k == 'A')
    {
        p.left = 0;
    } else if (k == 'S')
    {
        p.backward = 0;
    } else if (k == 'D')
    {
      p.right = 0;
    }
    
    game_controller.send(p.sendActionUpdate());
}



// Dynamically resize top bar on click
try
{
  document.getElementById('top-bar').onclick = function()
  {
    var div = document.getElementById('top-bar');
    if (div.style.height == '10%')
    {
      div.style.height = '50%';
    } else
    {
      div.style.height = '10%';
    }
  }
} catch (e) {}


function send_exit_lobby()
{
    obj =
    {
        'type':'exitlobby',
        'id':game_logic.current_player().id
    };
    
    game_controller.send(JSON.stringify(obj));
    
    document.getElementById('invite-and-play').classList.add('display-none');
    document.getElementById('lobby-button').classList.add('button-thing');
    document.getElementById('lobby-button').classList.remove('in-button-thing');
    document.getElementById('lobby-button').setAttribute("onclick", 'send_new_lobby();');
    document.getElementById('invite-button').setAttribute("onclick", 'doNothing();');
    document.getElementById('invite-button').classList.remove('join');
    document.getElementById('invite-button').classList.add('inactive-button');
    document.getElementById('start-game-button').setAttribute("onclick", 'doNothing();');
    document.getElementById('start-game-button').classList.remove('join');
    document.getElementById('start-game-button').classList.add('inactive-button');
    document.getElementById("showMenu").classList.add("display-thing");
}
