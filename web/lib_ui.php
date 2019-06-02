<?php

include 'lib_constants.php';

function body_println($value, $ident_extra=0)
{
    print INDENT_CONTROLS;

    for($i=0; $i < $ident_extra; $i++){
        print(INDENT_STEP);
    }

    print $value;
    print "\n";
}


function print_select($array_val, $select_name, $current_value, $onchange, $indent_extra = 0)
{
    body_println("<select id='$select_name' name='$select_name' onchange='$onchange' style='width:20em'>", $indent_extra);

    foreach ($array_val as $val) {
        $selected = "";

        if ($current_value == $val) {
            $selected = " selected=1";
        }

        body_println("<option value=\"$val\"$selected>$val</option>", 1 + $indent_extra);
    }

    body_println("</select>",$indent_extra);
}



function print_slider($name, $value, $indent_extra = 0)
{
    body_println("<input type='range' min='0' max='99' step='1' value=$value name='$name' id='$name' style='width:40em' onchange='document.getElementById(\"${name}_val\").innerHTML=document.getElementById(\"$name\").value;' />", $indent_extra);
    body_println("<span id='${name}_val'>$value</span>", $indent_extra);
}

?>
