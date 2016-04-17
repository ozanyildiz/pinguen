$(document).ready(function() {
  $('#httpMethod').bind('change', function(event) {
    var httpMethod = $('#httpMethod').val();
    if (httpMethod == 'POST') {
      $('#requestBody').show();
    } else {
      $('#requestBody').hide();
    }
  })
});