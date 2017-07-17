

$( function() {



$('#nlp_query_text').autocomplete({
    source: function (request, response) {
        $.getJSON("http://localhost:8000/autocomplete/?query="+request.term, function (data) {
            response(data.data);
        });
    },
    focus: function() {
          // prevent value inserted on focus
          return false;
        },
    select: function( event, ui ) {
    	
    	var terms = ($.trim(this.value)).split(" ");
          // remove the current input
          console.log(terms)
          terms.pop();
          console.log(terms)
          // add the selected item
          while (terms[terms.length-1] == ui.item.value.split(" ")[0]){
          	console.log(terms[terms.length-1])
          	console.log(ui.item.value.split(" ")[0])
          	terms.pop()
          }
          terms.push( ui.item.value );
          // add placeholder to get the comma-and-space at the end
          this.value = terms.join(" ");
          console.log(this.value)
          return false;
    },
    minLength: 1,
    delay: 100
});
  } );


function second_submit_nlp(string) {

  $("#nlp_query_text").val(string);
  var scope = angular.element(document.getElementById('top-middle-bar')).scope(); 
  scope.submit_NLP();
  return false;


}