<?php
//error_reporting(E_ALL);
//ini_set('display_errors', 1);

include 'lib_ui.php';
include 'lib_http_params.php';
include 'lib_session_file.php';

define("SPECIAL",array('None','Random'));


if (!isset($_GET['session']) || !check_session_code($_GET['session'])) {
    echo "Please accuire a valid session link.";
    die();
}

if (rand(0, 100) > 90) {
    cleanup_sessions();
}

$session_file  = session_filename($_GET['session']);
$session_vars  = explode(",", read_session_file($session_file,"0,None"));
$power_level   = get_numeric_postparam('power_level', $session_vars[0]);
$special_mode  = get_array_postparam('special_mode', SPECIAL, $session_vars[1]);


file_put_contents($session_file, "$power_level,$special_mode");


?>
<html>
    <head>
        <title>Lovesense Web Control</title>
    </head>
    <body>
        <form action="" method="POST">
            <table>
                <?php
                body_println("<tr>", 1);
                body_println("<td>Vibrator power level:</td>", 2);
                body_println("<td>", 2);
                print_slider("power_level", $power_level,3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Special modes:</td>", 2);
                body_println("<td>", 2);
                print_select(SPECIAL,'special_mode',$special_mode,'', 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);
                ?>
            </table>
            <br>
            <br>
            <input type=submit value=Save style="position:relative;width:30em;left:10em"/>
        </form>
     </body>
</html>
