<?php
include("dbconnect.inc.php");
echo("<html><head><meta http-equiv='content-type' content='text/html; charset=UTF-8'></head><body>");

echo("Nyuszika halállistája:<br/><br/><table>");
mysql_query("set names 'utf8'");
$acc_res = mysql_query("select * from ps_accounts where amount < 0 and id !=5 and id != 6 order by comment"); // this is a hack to filter out system accounts
$acc_num = mysql_numrows($acc_res);
$i=0;
while ($i < $acc_num) {

  $id = mysql_result($acc_res, $i, "id");
  $comment = mysql_result($acc_res, $i, "comment");
  $balance = mysql_result($acc_res, $i, "amount");
  echo("<tr><td>$comment ($id) </td>  <td width='20px'> </td><td><b>$balance pont</b></td></tr>");

  $i++;
}
echo("</body></table></html>");

mysql_close();

?>
