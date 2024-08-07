// Copiado de https://www.w3schools.com/Bootstrap/bootstrap_filters.asp
$(document).ready(function(){
    $("#filter_input").on("keyup", function() {
        var value = $(this).val().toLowerCase();
        $("#list li").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});