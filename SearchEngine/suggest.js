var cb = function(data, prefix){

  //Display suggestions
  var suggestions = $.parseJSON(data);
  $("#suggestion-box").empty();
  var html = '';
  suggestions.forEach(function(suggestion)
  {
    if(/^[a-zA-Z]+$/.test(suggestion))  //only words
    {
      var full_suggestion = (prefix + " " + suggestion).trim();
      html += '<option value="' + full_suggestion + '">';
    }

  });

  $("#suggestion-box").append(html);

 }

$(document).ready(function () {
  $( ".q" ).on('keyup',function(event) {  //When a user types a letter
    if($('.q').val().trim() != "")
    {
      var prefix = "";
      var query_words = $('.q').val().trim().split(" ");
      var query_data = query_words[query_words.length - 1]; //get last query word
      if(query_words.length > 1)
        prefix = query_words.slice(0,-1).join(" ");

      //AJAX Request to server to get suggestions for autocomplete
      $.ajax({
      type: "GET",
      url: "suggest.php",
      data: {q: query_data},
      success: function(data){
                cb(data, prefix);
              },
      error: function(jqXHR, testStatus, error){
        alert(" jqXHR: " + jqXHR + " testStatus: "+ testStatus + " Error: " + error);
      }
    });
    }
  });
});
