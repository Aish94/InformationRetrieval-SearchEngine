<?php
include 'SpellCorrector.php';

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10; //Display 10 results
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;  //Check if a query has been given
$results = false;
$pageRank = $_REQUEST['sorting'] == "Default" ? false : true;
$spellCheck = $_REQUEST['spellcheck'] == "true" ? true : false;

if ($query)
{
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('Apache/Solr/Service.php');
 
  // create a new solr service instance - host, port, and corename
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, 'solr/myexample/');
 
  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

  $query = strtolower($query);

  //Add parameters based on sorting method chosen
  if($pageRank)
  {
    $additionalParameters = array(
    'sort'=>'pageRankFile desc',
    'fl'=>'id,description,title'
    );
  }
  else
  {
    $additionalParameters = array(
    'fl'=>'id,description,title');
  }

  //spelling correction
  $corrected_query = '';
  if($spellCheck)
  {
    $words = explode(' ', $query);
    foreach($words as $word)
    {
      $corrected_word = SpellCorrector::correct($word);
      if($corrected_word != $word)
       $corrected_word = "<b>" . $corrected_word . "</b>";
      $corrected_query .= ' ' . $corrected_word;
    }
    $corrected_query = trim($corrected_query);
  }
  else
   $corrected_query = $query;

  try
  {
    $results = $solr->search($corrected_query, 0, $limit, $additionalParameters);
  }
  catch (Exception $e)
  {
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}
?>

<html>
  <head>
    <title>PHP Solr Client Example</title>
    <script
      src="https://code.jquery.com/jquery-3.2.1.min.js"
      integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
      crossorigin="anonymous">
    </script>
    <script
      type="text/javascript"
      src="suggest.js">
    </script>
  </head>

  <body>

    <!-- Query Box -->
    <form accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input class="q" id="q" name="q" type="text" list="suggestion-box"  autocomplete="off" value="<?php echo htmlspecialchars(strip_tags($corrected_query), ENT_QUOTES, 'utf-8'); ?>"/></br>

      <!-- Suggestion Box -->
      <datalist id="suggestion-box">
      </datalist>

      <label for="sorting">Sorting Algo:</label>
      <input type="radio" name="sorting" value="Default" <?php if(!$pageRank) { ?> checked = "checked" <?php } ?>/>Default
      <input type="radio" name="sorting" value="PageRank"<?php if($pageRank) { ?> checked = "checked" <?php } ?> />Page Rank</br>
      <input type="hidden" name="spellcheck" value="true"/>
      <input type="submit"/>
    </form>

    <!-- Display Results -->
    <?php
    if ($results)
    {
      include('testSnippet.php');

      $total = (int) $results->response->numFound;
      $start = min(1, $total);
      $end = min($limit, $total);
    ?>
    <!-- Total no. of results -->
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>

    <!-- Spelling Corrections if any -->
    <?php
      if($corrected_query != $query)
      {
        $search_url = '/solrclient?q=' . $query . '&sorting=' . $_REQUEST['sorting'] . '&spellcheck=false';
    ?>
    <div> Showing results for <?php echo $corrected_query; ?> </div>
    <div> Search instead for <a href='<?php echo $search_url; ?>'> <?php echo $query; ?> </a></div>
    <?php
      }
    ?>

    <!-- Results -->
    <ol style="list-style: none;">
    <?php
      // iterate result documents
      foreach ($results->response->docs as $doc)
      {
        $handle = fopen("mapLATimesDataFile.csv","r");  //open file containing Doc ID - URL mapping
    ?>
    <li>
    <table style="border: none; text-align: left">
    <?php
      $id = "";
      $title = "";
      $link = "";
      $desc = "";
 
      // iterate document fields / values
      foreach ($doc as $field => $value)
      {
        if($field == "id")
        {
          $value = end(explode('/',$value));
          while (($data = fgetcsv($handle, ",")) !== FALSE)
          {
            if($value == $data[0])
            {
              $link = $data[1]; //Get URL from Doc ID
              break;
            }
          }
          $id = $value;
        }
        else if($field == "description")
          $desc = $value;
        else if($field == "title")
          $title = $value;
      }

      $snippet = getSnippet(strip_tags($corrected_query), $id, $doc);
    ?>
    <tr>
      <th><a target="_blank" href="<?php echo htmlspecialchars($link,ENT_NOQUOTES,'utf-8') ?>"><?php echo htmlspecialchars($title, ENT_NOQUOTES, 'utf-8'); ?></a></th>
    </tr>
    <tr>
      <td><a target="_blank" href="<?php echo htmlspecialchars($link,ENT_NOQUOTES,'utf-8'); ?>"><?php echo htmlspecialchars($link, ENT_NOQUOTES, 'utf-8'); ?></a>
      </td>
    </tr>
    <tr>
      <td><?php echo htmlspecialchars($id, ENT_NOQUOTES, 'utf-8'); ?></td>
    </tr>
    <tr>
      <td><?php echo $snippet; ?></td>
    </tr>
  </table>
  </br>
 </li>

  <?php
    fclose($handle);
    }
  ?>
  </ol>
  <?php
    }
  ?>
  </body>
</html>
