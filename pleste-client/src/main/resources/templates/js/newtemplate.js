last_id = 0;

function appendField() {
    const form = document.getElementById("fields");
    last_id++;
    const this_id = "field_" + (last_id.toString().padStart(3, '0'));

    const template = document.createElement('template');
    template.innerHTML = "<div id=\"" + this_id + "\" class=\"mt-2 mb-2\"><div class=\"input-group\">\n" +
        "                        <div class=\"input-group-prepend\">\n" +
        "                            <div class=\"input-group-text\" title=\"Obrigatorio\">\n" +
        "                                <input type=\"checkbox\" id=\"" + this_id + "_mandatory\" name=\"" + this_id + "_mandatory\"/>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                        <input class=\"form-control\" name=\"" + this_id + "_name\" placeholder=\"Nome\" />\n" +
        "                        <select class=\"custom-select\" style=\"max-width: 20%\" name=\"" + this_id + "_type\">\n" +
        "                            <option>Texto libre</option>\n" +
        "                            <option>Ligazón</option>\n" +
        "                            <option>Data</option>\n" +
        "                        </select>\n" +
        "                        <div class=\"input-group-append\">\n" +
        "                            <button class=\"btn btn-outline-secondary\" type=\"button\" onclick=\"removeField('" + last_id.toString().padStart(3, '0') + "')\">Borrar campo</button>\n" +
        "                        </div>\n" +
        "                    </div></div>";
    form.appendChild(template.content.children[0]);
}

function removeField(field) {
    document.getElementById("field_" + field).remove();
}

