$( function() {

$('#nlp_query_text').autocomplete({
    source: function (request, response) {
        $.getJSON("http://localhost:8000/autocomplete/?query="+request.term, function (data) {
            response(data.data);
        });
    },
    select: function( event, ui ) {},
    minLength: 1,
    delay: 100
});
  } );