<?php
include 'lib_http_params.php';
include 'lib_session_file.php';

if (check_session_code($_GET['session'])) {
    print read_session_file("sessions/" . $_GET['session'],"");
}
?>
