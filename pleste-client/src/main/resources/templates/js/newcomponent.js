function cambiar_modelo() {
    var select = document.getElementById("template_select");
    var name = document.getElementById("component_name");
    var description = document.getElementById("component_description");

    if (name.value === "") {
        name_text = "&name=" + encodeURI(name.value);
    } else {
        name_text = "";
    }

    if (description.value === "") {
        description_text = "&description=" + encodeURI(description.value);
    } else {
        description_text = "";
    }

    location.href = "/newcomponent.html?template=" + encodeURI(select.value) + name_text + description_text;
}