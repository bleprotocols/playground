<?php
include 'lib_constants.php';

function session_filename($session)
{
    return SESSIONPATH . "/" . $session;
}

function read_session_file($filename,$default)
{
    $fn = fopen($filename, "r");
    if ($fn) {
        $contents = fgets($fn);
        fclose($fn);
        return $contents == "" ? $default:$contents;
    }

    return $default;
}


function cleanup_sessions()
{
    foreach (glob(SESSIONPATH . "/*") as $file) {
        if ((filemtime($file) < time() - 24 * 3600) && (is_file($file))) { // 1 hour
            unlink($file);
        }
    }
}
?>
