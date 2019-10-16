
$( document ).ready(function() {
    console.log( "ready!" );


    function updateView()
    {
        $.getJSON( "api/v1/jobs", function( data ) {
            var items = [];
            console.log( data );

            $.each( data, function( key, val ) {
                $("#"+key).text(val);
            });
        
        
        });
        window.setTimeout(updateView,1000);
    }
    updateView();

});

