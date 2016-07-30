$(document).ready(function(){
    $("#submit").hover
    (
        function(){
            $(this).addClass('red');
            $(this).val("BOOM!");
        },
        function(){
            $(this).removeClass('red');
            $(this).val("00:01");
        }
    );
    
    $('#login').hide();
    $('#login-overlay').hide().fadeIn('slow');
    $('#register-window').hide();
    $('#how-to-play-window').hide();

    $('#signUp').click(function() {
        registerNewPlayer();
    });
    
        $('#tologin').click(function() {
        goToLogin();
    });

    $('#tologin2').click(function() {
        goToLogin();
    });
    
});

function registerNewPlayer() {
    var username = document.getElementById("usernamesignup").value;
    var email = document.getElementById("emailsignup").value;
    var password = document.getElementById("passwordsignup").value;

    var connectMsg =
    {
        'type':'registeruser',
        'username':username,
        'password':password,
        'email':email
    };

    game_controller.send(JSON.stringify(connectMsg));
}

function goToRegister() {
    $('#register-window').fadeIn();
    $('#login-window').fadeOut();
    $('#how-to-play-window').fadeOut();

    $('#register').fadeOut();
    $('#how-to-play').fadeIn();
    $('#login').fadeIn();
}

function goToHowToPlay() {
    $('#register-window').fadeOut();
    $('#login-window').fadeOut();
    $('#how-to-play-window').fadeIn();

    $('#register').fadeIn();
    $('#how-to-play').fadeOut();
    $('#login').fadeIn();
}

function goToLogin() {
    $('#login-window').fadeIn();
    $('#register-window').fadeOut();
    $('#how-to-play-window').fadeOut();

    $('#register').fadeIn();
    $('#how-to-play').fadeIn();
    $('#login').fadeOut();
}