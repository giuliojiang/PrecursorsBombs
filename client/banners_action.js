$(document).ready(function(){

    $("#friend-invited").hide();
    enableTooltip();

    $('#chatBox').hide();
    
    $('#player-stats').click(function(){
        document.getElementById("friendProfile").classList.add("none-it");
        document.getElementById("playerProfile").classList.remove("none-it");
    });


});

function enableTooltip() {
    $(".friend-profile").tooltip({
        placement: "top",
        title: "Player's profile",
        html: true,
        container: 'body'
    });

    $(".friend-game").tooltip({
        placement: "top",
        title: "Play",
        html: true,
        container: 'body'
    });

    $(".friend-chat").tooltip({
        placement: "top",
        title: "Chat",
        html: true,
        container: 'body'
    });

    $(".friend-remove").tooltip({
        placement: "top",
        title: "Remove friend",
        html: true,
        container: 'body'
    });
};
