<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

if (!isset($_GET['links']) ) {
    echo "Please specify at least one link.";
    die();
}

$frames=explode(",",$_GET['links']);

?>

<html>
    <head>
        <title>Web Control</title>



<script type="text/javascript">
function getDocHeight(doc) {
    var body = doc.body;
    var  html = doc.documentElement;
    var height = Math.max( body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight );
    return height;
}

function getDocWidth(doc) {
    var body = doc.body;
    var html = doc.documentElement;
    var width = Math.max( body.scrollWidth, body.offsetWidth, html.clientWidth, html.scrollWidth, html.offsetWidth);
    return width;
}


function resizeIframe(ifrm){
    var doc = ifrm.contentDocument? ifrm.contentDocument: ifrm.contentWindow.document;
    ifrm.style.visibility = 'hidden';
    ifrm.style.height = "10px"; // reset to minimal height/width
    ifrm.style.width = "10px"; // reset to minimal height/width

    ifrm.style.height = getDocHeight( doc ) + 4 + "px";
    ifrm.style.width = getDocWidth( doc ) + 4 + "px";

    ifrm.style.visibility = 'visible';
  }
</script>

    </head>
    <body>
     <?php
foreach ($frames as $value){
  if(strlen($value)>10){
     echo "<iframe  onload=\"resizeIframe(this);\" src=\"$value\"></iframe>";
  }
}
 ?>
     </body>
</html>
