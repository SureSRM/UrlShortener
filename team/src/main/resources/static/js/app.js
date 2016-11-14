$(document).ready(
    function() {
        $.ajax('http://localhost:8080/rank', 'GET').done(
            function(data) {
                for (var i = 0; i < data.length; i++) {

                    $("#rank").append(
                        "<div class='rank-position'> <p> Position " + i + ": <a href='http://localhost:8080/"
                        + data[i].hashed
                        + "'>"
                        + data[i].hashed
                        + "</a> with "
                        + data[i].score
                        + " hits.</p></div>"
                    );
                }
            });
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });