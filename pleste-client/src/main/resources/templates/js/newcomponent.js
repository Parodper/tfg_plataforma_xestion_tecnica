function cambiar_modelo() {
    var select = document.getElementById("template_select");

    location.href = "/newcomponent?template=" + encodeURI(select.value);
}