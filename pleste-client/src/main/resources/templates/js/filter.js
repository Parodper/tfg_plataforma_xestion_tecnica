// Copiado de https://www.w3schools.com/Bootstrap/bootstrap_filters.asp
$(document).ready(function(){
    $("#filter_input").on("keyup", function() {
        var value = $(this).val().toLowerCase();
        $("#list li").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});

const ELEMENTS_PER_PAGE = 10;

function next_results() {
    from = Number($("#from-field").val()) + ELEMENTS_PER_PAGE;

    change_results(from);
}

function previous_results() {
    from = Number($("#from-field").val()) - ELEMENTS_PER_PAGE;

    if(from < 0) {
        from = 0;
    }

    change_results(from);
}

function change_results(from) {
    $("#from-field").val(from);
    $("#count-field").val(ELEMENTS_PER_PAGE);

    $("#search-form").submit();
}