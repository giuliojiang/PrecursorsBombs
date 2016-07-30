function validate() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("loginpassword").value;

    if(username.toString().substring(0, 6) == "guest_") {
        var usernameString = username.toString();
        var restOfString = usernameString.substring(6, usernameString.length);
        if($.isNumeric(restOfString)) {
            alert('A username cannot be of a form "guest_number" >o<');
            return;
        }
    }

    var connectMsg =
    {
         "type": "connect",
         "username": username,
         "password": password
    };
    
    game_controller.send(JSON.stringify(connectMsg));
    $("#login-overlay").fadeOut();
};


function searchKeyPress(e)
{
    // look for window.event in case event isn't passed in
    e = e || window.event;
    if (e.keyCode == 13)
    {
        validate();
        return false;
    };
    return true;
};

function guest_login() {
    var connectMsg =
    {
        "type": "connect",
        "username": "guest",
        "password": "password"
    };

    game_controller.send(JSON.stringify(connectMsg));
    $("#login-overlay").fadeOut();
};
