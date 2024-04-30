last_id = 0;

function appendField(start_disabled) {
    form = document.getElementById("fields");
    last_id++;
    this_id = "field_" + (last_id.toString().padStart(3, '0'));

    field = document.createElement("div");
    field.id = "field_" + (last_id.toString().padStart(3, '0'));

    label_name = document.createElement("label");
    label_name_input = document.createElement("input");
    label_name_input.name = this_id + "_name";
    label_name_input.disabled = start_disabled;
    label_name.append("Nome: ", label_name_input);

    label_type = document.createElement("label");
    field_types = document.createElement("select");
    field_types.name = this_id + "_type";
    field_types.disabled = start_disabled;
    option = document.createElement("option");
    option.text = "Texto libre"
    field_types.add(option);
    option = document.createElement("option");
    option.text = "Ligazón"
    field_types.add(option);
    option = document.createElement("option");
    option.text = "Data"
    field_types.add(option);
    label_type.append("Tipo: ", field_types);

    label_mandatory = document.createElement("label");
    input_mandatory = document.createElement("input");
    input_mandatory.name = this_id + "_mandatory";
    input_mandatory.type = "checkbox";
    input_mandatory.disabled = start_disabled;
    label_mandatory.append("Obrigatorio? ", input_mandatory)

    delete_button = document.createElement("button");
    delete_button.addEventListener("click", (event) => removeField(event.target));
    delete_button.innerText = "Borrar campo";
    delete_button.type = "submit";

    field.append(label_name, label_type, label_mandatory, delete_button);
    form.appendChild(field);
}

function removeField(field) {
    field.parentNode.remove();
}

