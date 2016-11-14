$(document).ready(
    function() {
        $.ajax('http://localhost:8080/info', 'GET').done(
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
            });$.ajax('http://localhost:8080/metrics', 'GET').done(
            function(data) {
                console.log(data);
                $("#uptime").append( data["uptime"] );
                $("#mem").append( data["mem"] );
                $("#memused").append( data["mem.free"] );
                $("#avload").append( data["systemload.average"] );

                $("#countshorted").append( data["counter.status.201.link"] );
                $("#countaccess").append( data["counter.status.304.star-star"] );
                $("#avacessperurl").append( data["counter.status.201.link"] / data["counter.status.304.star-star"] );
                $("#timelastredirection").append( data["gauge.response.star-star"] );
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