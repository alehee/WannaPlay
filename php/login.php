<?php
$mysqli = @new mysqli("riverlakestudios.pl", "30908302_wp", "rvrlkWP_", "30908302_wp");

$response['message']="Error_01";

$passcode = $_POST['opcode'];

$mysqli -> query("SET CHARSET utf8");
$mysqli -> query("SET NAMES 'utf8' COLLATE 'utf8_polish_ci'");

$sql="SELECT Nick FROM users WHERE OP='$passcode' LIMIT 1";
$que=$mysqli->query($sql);
while($res = mysqli_fetch_array($que)){
    $response['message'] = $res['Nick'];
}

header('Content-Type: application/json');
echo json_encode($response);

$mysqli->close();
?>