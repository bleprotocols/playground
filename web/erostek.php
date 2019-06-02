<?php
include 'lib_ui.php';
include 'lib_http_params.php';
include 'lib_session_file.php';

define("PROGRAMS",array('Waves','Stroke','Intense','Torment','Climb','Combo','Rythm','Audio1','Audio2','Audio3','Random1','Random2','Toggle','Orgasm','Phase1','Phase2','Phase3','User1','User2','User3','User4','User5','User6','User7'));
define("POWER_LEVELS",array('Low','Normal','High'));


if (!isset($_GET['session']) || !check_session_code($_GET['session'])) {
    echo "Please accuire a valid session link.";
    die();
}

if (rand(0, 100) > 90) {
    cleanup_sessions();
}

$session_file = session_filename($_GET['session']);
$session_vars = explode(",", read_session_file($session_file,"High,Waves,Waves,0,0,0"));
$power_level  = get_array_postparam('power_level',POWER_LEVELS, $session_vars[0]);
$program_a    = get_array_postparam('program_a', PROGRAMS, $session_vars[1]);
$program_b    = get_array_postparam('program_b', PROGRAMS, $session_vars[2]);
$power_a      = get_numeric_postparam("power_a", $session_vars[3]);
$power_b      = get_numeric_postparam("power_b", $session_vars[4]);
$multi_adjust = get_numeric_postparam("multi_adjust", $session_vars[5]);

file_put_contents($session_file, "$power_level,$program_a,$program_b,$power_a,$power_b,$multi_adjust");


?>
<html>
    <head>
        <title>ErosTek Web Control</title>
    </head>
    <body>
        <form action="" method="POST">
            <table>
                <?php
                body_println("<tr>", 1);
                body_println("<td>Box power level:</td>", 2);
                body_println("<td>", 2);
                print_select(POWER_LEVELS,'power_level',$power_level,'', 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Channel A Power:</td>", 2);
                body_println("<td>", 2);
                print_slider("power_a", $power_a,3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Channel B Power:</td>", 2);
                body_println("<td>", 2);
                print_slider("power_b",$power_b, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Multi-adjust level:</td>", 2);
                body_println("<td>", 2);
                print_slider("multi_adjust",$multi_adjust, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Channel A Program:</td>", 2);
                body_println("<td>", 2);
                print_select(PROGRAMS,'program_a',$program_a,'document.getElementById("program_b").value=document.getElementById("program_a").value;', 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);


                body_println("<tr>", 1);
                body_println("<td>Channel B Program:</td>", 2);
                body_println("<td>", 2);
                print_select(PROGRAMS,'program_b',$program_b,'', 3);
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
