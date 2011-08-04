<?php
include("dbconnect.inc.php");
$id=$_GET['id'];

//mysql_query("set names 'utf8'");
$acc_res = mysql_query("select comment,amount from ps_accounts where id = '$id'");
$acc_num = mysql_numrows($acc_res);
if ($acc_num == 1) {
  $comment = mysql_result($acc_res, 0, "comment");
  $balance = mysql_result($acc_res, 0, "amount");
  echo("OK#$comment#$balance");
} else {
  echo("FAILED#Nem létező számla.#0 ");
}

mysql_close();

?>
