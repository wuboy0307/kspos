<?php
include("dbconnect.inc.php");

$from_account=$_POST['from'];
$to_account=$_POST['to'];
$amount=$_POST['amount'];
$comment=$_POST['comment'];

//mysql_query("set names 'utf8'");
mysql_query("begin transaction");
mysql_query("insert into ps_transactions (`initiator_account`,`from_account`,`to_account`,`type`,`amount`,`comment`) values ('$from_account','$from_account','$to_account',0,'$amount','$comment');");
mysql_query("update ps_accounts set amount = amount-$amount, modified_on=CURRENT_TIMESTAMP where id='$from_account';");
mysql_query("update ps_accounts set amount = amount+$amount, modified_on=CURRENT_TIMESTAMP where id='$to_account';");

mysql_query("set names 'ISO-8859-2'");

$results = mysql_query("select comment,amount from ps_accounts where id='$from_account';");
$newAmount = mysql_result($results, 0, "amount");
$comment = mysql_result($results, 0, "comment");
echo("$comment#$newAmount");

$results = mysql_query("select comment, amount from ps_accounts where id='$to_account';");
$newAmount = mysql_result($results, 0, "amount");
$comment = mysql_result($results, 0, "comment");
echo("#$comment#$newAmount");

mysql_query("commit;");
mysql_close();
?>
