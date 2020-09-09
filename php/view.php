<?php
$mysqli = @new mysqli("riverlakestudios.pl", "30908302_wp", "rvrlkWP_", "30908302_wp");

$response=array();

$mysqli -> query("SET CHARSET utf8");
$mysqli -> query("SET NAMES 'utf8' COLLATE 'utf8_polish_ci'");

$nick="";
$status="";
$_exist=0;

$sql="SELECT Nick FROM users ORDER BY Nick ASC";
$que=$mysqli->query($sql);
while($res = mysqli_fetch_array($que)){
    $nick = $res["Nick"];
    $temp_sql="SELECT Available FROM available WHERE Nick='$nick' ORDER BY ID ASC";
    $temp_que = $mysqli -> query($temp_sql);
    while($temp_res = mysqli_fetch_array($temp_que)){
        $_exist=1;
        $status=$temp_res["Available"];
    }
    if($_exist==0){
        $status="0";
    }

    array_push($response, ["nick" => $nick, "status" => $status]);
    $_exist=0;
}

header('Content-Type: application/json');
echo json_encode($response);

$mysqli->close();
?>