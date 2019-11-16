
$( document ).ready(function() {


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

    function heartbeat(ok)
    {
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



    function updateView()
    {
        $.getJSON( "api/v1/jobs", function( data ) {
            var items = [];
            console.log( data );

            $.each( data, function( key, val ) {

                var elm = $("#"+key);

                if((key == "heartbeat") || (key == "jobs-nextscann"))
                {
                    val = new Date(val).human();
                }


                elm.text(val);

                var counter = elm.closest('.single_counter');
                if(counter)
                {
                    console.log(key+" = "+val);

                    //counter
                    if(val > 0)
                    {
                        counter.addClass("active");
                    }
                    else
                    {
                        counter.removeClass("active");
                    }
                }

            });
        
            //error
            var error = data['jobs-errors'];
            if(error > 0)
            {
                $("#bomb").addClass("error");
            }
            else
            {
                $("#bomb").removeClass("error");
            }

            //files-warnings
            var warnings = data['files-warning'];
            if(warnings > 0)
            {
                $("#triangle").addClass("warning");
            }
            else
            {
                $("#triangle").removeClass("warning");
            }
            //files-ok
            var ok = data['files-ok'];
            if(ok > 0)
            {
                $("#check").addClass("ok");
            }
            else
            {
                $("#check").removeClass("ok");
            }


            //heartbeat
            var hb = data['heartbeat'];
            var d = new Date().getTime();
            var diff = d - hb;
            heartbeat(diff < 1000); 

        }).fail(function() { 
            heartbeat(false); 
        })

        window.setTimeout(updateView,1000);
    }
    updateView();

   

});

