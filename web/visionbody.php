<?php
include 'lib_ui.php';
include 'lib_http_params.php';
include 'lib_session_file.php';

define("FEELS",array("Soft", "Medium", "Hard"));
define("PROGRAMS",array("GettingStarted", "Basic1", "Basic2", "Continuous1", "Continuous2", "Endurance1", "Endurance2", "Endurance3", "Strength1", "Strength2", "FatBurning1", "FatBurning2", "CoolDown1", "CoolDown2", "Massage", "AntiCell"));


if (!isset($_GET['session']) || !check_session_code($_GET['session'])) {
    echo "Please accuire a valid session link.";
    die();
}

if (rand(0, 100) > 90) {
    cleanup_sessions();
}

$session_file  = session_filename($_GET['session']);
$session_vars  = explode(",", read_session_file($session_file,"Basic1,Hard,10,2,0,0,0,0,0,0,0,0"));
$program       = get_array_postparam('program', PROGRAMS, $session_vars[0]);
$feel          = get_array_postparam('feel', PROGRAMS, $session_vars[1]);
$ontime        = get_numeric_postparam('ontime', $session_vars[2]);
$offtime       = get_numeric_postparam('offtime', $session_vars[3]);
$upperback     = get_numeric_postparam('upperback', $session_vars[4]);
$rear          = get_numeric_postparam('rear', $session_vars[5]);
$legs          = get_numeric_postparam('legs', $session_vars[6]);
$lowerback     = get_numeric_postparam('lowerback', $session_vars[7]);
$arms          = get_numeric_postparam('arms', $session_vars[8]);
$neck          = get_numeric_postparam('neck', $session_vars[9]);
$chest         = get_numeric_postparam('chest', $session_vars[10]);
$stomach       = get_numeric_postparam('stomach', $session_vars[11]);

file_put_contents($session_file, "$program,$feel,$ontime,$offtime,$upperback,$rear,$legs,$lowerback,$arms,$neck,$chest,$stomach");
?>
<html>
    <head>
        <title>VisionBody Web Control</title>
    </head>
    <body>
        <form action="" method="POST">
            <table>
                <?php
                body_println("<tr>", 1);
                body_println("<td>Program:</td>", 2);
                body_println("<td>", 2);
                print_select(PROGRAMS,'program',$program,'', 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Feel:</td>", 2);
                body_println("<td>", 2);
                print_select(FEELS,'feel',$feel,'', 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Stimulation time (seconds):</td>", 2);
                body_println("<td>", 2);
                print_slider("ontime", $ontime,3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Rest time (seconds):</td>", 2);
                body_println("<td>", 2);
                print_slider("offtime", $offtime,3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Upper back power:</td>", 2);
                body_println("<td>", 2);
                print_slider("upperback",$upperback, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Rear power:</td>", 2);
                body_println("<td>", 2);
                print_slider("rear",$rear, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Legs power:</td>", 2);
                body_println("<td>", 2);
                print_slider("legs",$legs, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Lower back power:</td>", 2);
                body_println("<td>", 2);
                print_slider("lowerback",$lowerback, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Arms power:</td>", 2);
                body_println("<td>", 2);
                print_slider("arms",$arms, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Neck power:</td>", 2);
                body_println("<td>", 2);
                print_slider("neck",$neck, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Chest power:</td>", 2);
                body_println("<td>", 2);
                print_slider("chest",$chest, 3);
                body_println("</td>", 2);
                body_println("</tr>", 1);

                body_println("<tr>", 1);
                body_println("<td>Stomach power:</td>", 2);
                body_println("<td>", 2);
                print_slider("stomach",$stomach, 3);
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
