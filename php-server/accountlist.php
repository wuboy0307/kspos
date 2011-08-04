<?php
include("dbconnect.inc.php");
echo("<html><head><meta http-equiv='content-type' content='text/html; charset=UTF-8'></head><body>");

echo("<table>");
mysql_query("set names 'utf8'");
$acc_res = mysql_query("select * from ps_accounts");
$acc_num = mysql_numrows($acc_res);
$i=0;
while ($i < $acc_num) {

  $id = mysql_result($acc_res, $i, "id");
  $comment = mysql_result($acc_res, $i, "comment");
  $balance = mysql_result($acc_res, $i, "amount");
  $modified_on = mysql_result($acc_res, $i, "modified_on");
  echo("<tr><td style='border: solid black 1px;'>$id</td> <td>$comment</td> <td><b>$balance pont</b></td> <td>$modified_on</td></tr>");
  echo("<tr><td></td><td colspan='3'>" . print_transactions($id) ."</td></tr>");

  $i++;
}
echo("</body></table></html>");

mysql_close();

function print_transactions($tr_id) {
    $output = "";
    $res = mysql_query("(select -amount `amount`,`created_on`,`comment` from ps_transactions where from_account='$tr_id') union (select `amount`,`created_on`,`comment` from ps_transactions where to_account='$tr_id') order by created_on desc limit 10");
    $num = mysql_numrows($res);
    $j=0;
    while ($j < $num) {

        $trcomment = mysql_result($res, $j, "comment");
        $tramount = mysql_result($res, $j, "amount");
        $trcreated_on = mysql_result($res, $j, "created_on");
        $output .= "$trcreated_on ";
        if ($tramount != 0)
            $output .= "|    $tramount pont ";
        $output .= "| $trcomment<br>";

        $j++;
    }
    return $output;
}

?>
