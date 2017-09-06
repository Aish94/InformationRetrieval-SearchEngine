<?php

  $query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
  $results = false;
  $limit = 5;
  if($query)
  {
   require_once('Apache/Solr/Service.php');
   $solr = new Apache_Solr_Service('localhost', 8983, 'solr/myexample/');
   if (get_magic_quotes_gpc() == 1)
   {
     $query = stripslashes($query);
   }
  $additionalParameters = array(
   'qt'=>'suggest'
 );

  try
  {
    //Query SOLR
    $results = $solr->search($query, 0, $limit , $additionalParameters);
  }
  catch (Exception $e)
  {
    echo $e;
  }
  if ($results)
  {
    $total = (int) $results->suggest->suggest->$query->numFound;
    $suggestions = $results->suggest->suggest->$query->suggestions;
    if($total > 0)
    {
      $suggestionsArray = array();
      foreach($suggestions as $suggestion)
      {
        $term = htmlspecialchars($suggestion->term, ENT_NOQUOTES, 'utf-8');
        array_push($suggestionsArray,$term);
      }
      $output = json_encode($suggestionsArray); //Send Results back as JSON
      echo $output;
    }
    else
      echo "No results..";

  }
  else {
    echo "No results..";
  }
}
else {
  echo "No query";
}
?>
