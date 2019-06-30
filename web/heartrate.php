<?php
//error_reporting(E_ALL);
//ini_set('display_errors', 1);

include 'lib_ui.php';
include 'lib_http_params.php';
include 'lib_session_file.php';

if (!isset($_GET['session']) || !check_session_code($_GET['session'])) {
    echo "Please accuire a valid session link.";
    die();
}

if (rand(0, 100) > 90) {
    cleanup_sessions();
}

$session_file  = session_filename($_GET['session']);
$heartrate     = get_numeric_urlparam('heartrate', 0);

file_put_contents($session_file, "<html><head><meta http-equiv='refresh' content='2'></head><body style='width:15em;'>Heart rate: $heartrate</body></html>");


?>
