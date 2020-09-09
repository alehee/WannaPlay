<?php
$mysqli = @new mysqli("riverlakestudios.pl", "30908302_wp", "rvrlkWP_", "30908302_wp");

$response['message']="NOK";

$ver = $_POST['ver'];

$sql="SELECT Version FROM ver WHERE Version='$ver' LIMIT 1";
$que=$mysqli->query($sql);
while($res = mysqli_fetch_array($que)){
    $response['message'] = "OK";
}

header('Content-Type: application/json');
echo json_encode($response);

$mysqli->close();
?>