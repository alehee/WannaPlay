<?php
$mysqli = @new mysqli("riverlakestudios.pl", "30908302_wp", "rvrlkWP_", "30908302_wp");

$response['message']="Error";

$nick="";
$status="";

$nick = $_POST['nick'];
$status = $_POST['status'];

if($nick!="" && $status!=""){
    $sql="INSERT INTO available VALUES(NULL, '$nick', '$status', CURRENT_TIMESTAMP)";
    $que=$mysqli->query($sql);
    $response['message']="Success!";
}

header('Content-Type: application/json');
echo json_encode($response);

$mysqli->close();
?>