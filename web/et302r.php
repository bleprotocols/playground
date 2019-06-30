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
   $session_vars  = explode(",", read_session_file($session_file, "0,0,0"));
   $button        = get_numeric_postparam('button',  $session_vars[0]);
   $duration      = get_numeric_postparam('duration', $session_vars[0]);
   $event         = get_numeric_postparam('event',  $session_vars[0]);
   
   file_put_contents($session_file, "$button,$duration,$event");
?>
<html>
   <head>
      <title></title>
      <script language="javascript">
         var startTime;
         
         function postValues(buttonID,buttonDuration){
            document.getElementById("button").value =buttonID ;
            document.getElementById("duration").value =buttonDuration ;
            document.getElementById("event").value = parseInt((Math.random() * 4294967295), 10);
         
            document.getElementById("form").submit(); 
         }
         
         function getClickDuration(){
                 endTime = new Date().getTime();
                 delta = endTime - startTime;
                 
                 if (delta > 0 && delta < 500) {
                     return 1; //clicked once 
                 }
                 
                 return Math.floor(delta);
         }
         
         function setClickFunctions(elementId,onClickStart,onClickStop){
              var elem = document.getElementById(elementId);
              elem.ontouchstart=onClickStart;
              elem.onmousedown=onClickStart;
              elem.ontouchend=onClickStop;
              elem.onmouseup=onClickStop;
         }
         
         function clickStart(){
            startTime=new Date().getTime();
         }
         
         function init() {
           setClickFunctions('powerAUpButton', clickStart ,function(){postValues(3,getClickDuration());} );
           setClickFunctions('powerADownButton', clickStart ,function(){postValues(4,getClickDuration());} );
           setClickFunctions('powerBUpButton', clickStart ,function(){postValues(5,getClickDuration());} );
           setClickFunctions('powerBDownButton', clickStart ,function(){postValues(6,getClickDuration());} );
         
           setClickFunctions('trainingButton1', clickStart ,function(){postValues(7,getClickDuration());} );
           setClickFunctions('trainingButton2', clickStart ,function(){postValues(8,getClickDuration());} );
           setClickFunctions('trainingButton3', clickStart ,function(){postValues(9,getClickDuration());} );
         }
         
         
      </script>
   </head>
   <body onload="init()">
      <form method="POST" id="form"> 
         <input type="hidden" id="button" name="button" value="">
         <input type="hidden" id="duration" name="duration" value="">
         <input type="hidden" id="event" name="event" value="">
      </form>
      <table>
         <tr>
            <td><input type="button" id="modeUpButton" onclick="postValues(1,1)" value="Mode up" /></td>
            <td><input type="button" id="modeDownButton" onclick="postValues(2,1)" value="Mode down" /></td>
         </tr>
         <tr style="height: 50px;"/>
         <tr>
            <td><input type="button" id="powerAUpButton"  value="Channel A: up"/></td>
            <td><input type="button" id="powerADownButton" value="Channel A: down" /></td>
         </tr>
         <tr style="height: 20px;"/>
         <tr>
            <td><input type="button" id="powerBUpButton"  value="Channel B: up" /></td>
            <td><input type="button"  id="powerBDownButton" value="Channel B: down" /></td>
         </tr>
         <tr style="height: 50px;"/>
         <tr>
            <td><input type="button" id="trainingButton1" value="Training button 1" /></td>
            <td><input type="button" id="trainingButton2" value="Training button 2" /></td>
            <td><input type="button" id="trainingButton3" value="Training button 3" /></td>
         </tr>
      </table>
   </body>
</html>
