function updateMetrics() {
    $.ajax('http://localhost:9090/info', 'GET').done(
        function(data) {
            console.log(data);
            for (var i = 0; i < data.RankList.length; i++) {

                $("#rank").append(
                    "<div class='rank-position'> <p> Position " + i + ": <a href='http://localhost:8080/"
                    + data.RankList[i].hashed
                    + "'>"
                    + data.RankList[i].hashed
                    + "</a> with "
                    + data.RankList[i].score
                    + " hits.</p></div>"
                );
            }
    });
    $.ajax('http://localhost:8080/metrics', 'GET').done(
        function(data) {
        console.log(data);
        $("#uptime").html( data["uptime"] );
        $("#mem").html( data["totalMemory"] );
        $("#memused").html( data["usedMemory"] );
        $("#avload").html( data["averageLoad"] );

        $("#countshorted").html( data["shortedURLs"] );
        $("#countaccess").html( data["redirectedURLs"] );
        $("#avacessperurl").html( data["average_RedirectionsPerURL"] );
        $("#timelastredirection").html( data["responseTimeToTheLastRedirection"] );
    });
}

$(document).ready(
    function() {
        $.ajax('http://localhost:9090/info', 'GET').done(
            function(data) {
                console.log(data);
                for (var i = 0; i < data.RankList.length; i++) {

                    $("#rank").append(
                        "<div class='rank-position'> <p> Position " + i + ": <a href='http://localhost:8080/"
                        + data.RankList[i].hashed
                        + "'>"
                        + data.RankList[i].hashed
                        + "</a> with "
                        + data.RankList[i].score
                        + " hits.</p></div>"
                    );
                }
        });
        $.ajax('http://localhost:8080/metrics', 'GET').done(
        function(data) {
            console.log(data);
            $("#uptime").html( data["uptime"] );
            $("#mem").html( data["totalMemory"] );
            $("#memused").html( data["usedMemory"] );
            $("#avload").html( data["averageLoad"] );

            $("#countshorted").html( data["shortedURLs"] );
            $("#countaccess").html( data["redirectedURLs"] );
            $("#avacessperurl").html( data["average_RedirectionsPerURL"] );
            $("#timelastredirection").html( data["responseTimeToTheLastRedirection"] );
            setInterval(updateMetrics,3000);
        });

        // Get the modal
        var modal = document.getElementById('infoModal');
        // Get the button that opens the modal
        var btn = document.getElementById('infoButton');
        // Get the <span> element that closes the modal
        var span = document.getElementsByClassName("close")[0];
        // When the user clicks on the button, open the modal
        btn.onclick = function() {
            modal.style.display = "block";
        }
        // When the user clicks on <span> (x), close the modal
        span.onclick = function() {
            modal.style.display = "none";
        }
        // When the user clicks anywhere outside of the modal, close it
        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }
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