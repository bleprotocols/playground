<?php
include 'lib_constants.php';

function check_session_code($code)
{
    return preg_match('/^[\p{L}\p{N}]{4,16}$/u', $code);
}

function grep($input, $default_value, string $regex)
{
    if (preg_match($regex, $input)) {
        return $input;
    }
    return $default_value;
}

function array_or_default($input, $array_val, $default)
{
    if (in_array($input, $array_val)) {
        return $input;
    } else {
        return $default;
    }
}

function get_numeric_postparam($name, $default)
{
    return isset($_POST[$name]) ? grep($_POST[$name], $default, '/^[\p{N}]{1,16}$/u') : $default;
}

function get_numeric_urlparam($name, $default)
{
    return isset($_GET[$name]) ? grep($_GET[$name], $default, '/^[\p{N}]{1,16}$/u') : $default;
}

function get_array_postparam($name, $from, $default)
{
    return isset($_POST[$name]) ? array_or_default($_POST[$name], $from, $default) : $default;
}


?>
