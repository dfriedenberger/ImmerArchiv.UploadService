
$( document ).ready(function() {


    var sourceTableRow   = document.getElementById("table-row-template").innerHTML;
    var templateTableRow = Handlebars.compile(sourceTableRow);

    Date.prototype.human = function() {
      
        var dd = this.getDay();
        var MM = this.getMonth() +1;
        var yy = this.getFullYear();
      
        var hh = this.getHours();
        var mm = this.getMinutes();
        var ss = this.getSeconds();

        return [
            (dd>9 ? '' : '0') + dd,
            (MM>9 ? '' : '0') + MM,
            yy
        ].join('.') + " " + [
                (hh>9 ? '' : '0') + hh,
                (mm>9 ? '' : '0') + mm,
                (ss>9 ? '' : '0') + ss
               ].join(':');
    };

    function setHeartBeat(timestamp)
    {
        var current = new Date().getTime();
        var diff = current - timestamp;
        var ok = diff < 1000? true : false;

        if(ok) {
            $("#heart").addClass("blink");
            $("#heart").addClass("alive");
        }
        else
        {
            $("#heart").removeClass("blink");
            $("#heart").removeClass("alive");
        }
    }

    function setNextCycle(timestamp,trigger)
    {
        var current = new Date().getTime();

        // Find the distance between now and the count down date
        var distance = timestamp - current;

        // Time calculations for days, hours, minutes and seconds
        var days = Math.floor(distance / (1000 * 60 * 60 * 24));
        var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);

        // Display the result in the element with id="demo"
        var counter = (days > 0 ? days + "d " : "")
                + (hours>9 ? '' : '0') + hours + "h "
                + (minutes>9 ? '' : '0') + minutes + "m "
                + (seconds>9 ? '' : '0') + seconds+"s"

        // If the count down is finished, write some text
        if (distance < 0) {
           counter = "EXPIRED";
        }
        $("#counter").text(counter);
        $("#trigger").text(trigger);


    }

    function  setTileCounter(id,value)
    {
        var elm = $(id);
        elm.text(value);

        var counter = elm.closest('.single_counter');
        if(value > 0)
        {
                counter.addClass("active");
        }
        else
        {
                counter.removeClass("active");
        }
        
    }
    
    var linesCache = {}; 
    const maxLines = 10;

    function  showLines(id,cnt,url)
    {
        if(!(id in linesCache)) 
            linesCache[id] = 0;

        if(linesCache[id] == cnt) return; //Nothing changed

        var skip = 0;
        var limit = cnt;

        if(cnt > maxLines)
        {
            limit = maxLines;
            skip = cnt - maxLines;
        }

        $(id).empty();

        $.getJSON( url + "?skip="+skip+"&limit="+limit, function( data ) {

            //debug dump all
            console.log( data );

            $.each( data, function( key, val ) {
               
                var html    = templateTableRow({line: val});
                $(id).append(html);
            });

            linesCache[id] = cnt;

        });
    }

    function showFiles(files)
    {
        $.getJSON( "api/v1/status", function( data ) {
            
            //debug dump all
            console.log( data );

            $.each( data["tree"], function( key, val ) {
                console.log(val);
            });
            
        });
    }

    function updateView()
    {
        $.getJSON( "api/v1/status", function( data ) {
            
            //debug dump all
            console.log( data );

            //debug dump jobs
            var jobs = data["jobs"];
            $.each( jobs, function( key, val ) {
                console.log("jobs: "+ key +" = "+val);
            });

            //debug dump files
            var files = data["files"];
            $.each( files, function( key, val ) {
                console.log("files: "+key +" = "+val);
            });

            setHeartBeat(jobs["heartbeat"]);
            setNextCycle(jobs["nextScanTime"],jobs["nextScanTrigger"]);

            var errors = jobs["errors"];
            var uploads = jobs["uploads"];
            setTileCounter("#jobs-errors",errors);
            setTileCounter("#jobs-cnt",jobs["jobCount"]);

            setTileCounter("#files-ok",files["filesOk"]);
            setTileCounter("#files-warning",files["filesWarning"]);
           
            showLines("#errors",errors,"api/v1/errors");
            showLines("#uploads",uploads,"api/v1/uploads");
           
            /*
            jobs: currentStep = 0
            jobs: currentStart = 0
            jobs: currentName = 
            */


        }).fail(function() { 
            setHeartBeat(0); 
        })

        window.setTimeout(updateView,1000);
    }
    updateView();

    showFiles(0);
   

});

