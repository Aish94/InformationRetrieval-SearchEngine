<?php
  include 'simple_html_dom.php';

  function getSnippet($query, $html_file, $meta)
  {
    //tokenize query
    $query = strtolower($query);//'developers ensuring');
    $querywords = explode(" ", $query);

    //Parse and tokenize html document
    $html = file_get_html("solr-6.5.0/Data/" . $html_file);/
    $text = $html->plaintext;
    $sentences = explode(". ", $text);
    $best_sentence = "";  //first sentence that contains all the query words
    $potential_sentence = ""; //first sentence that contains atleast one query word
    $pos = 0;
    $matched_word = "";

    //For each sentence
    foreach($sentences as $sentence)
    {

      $sentence = html_entity_decode($sentence, ENT_QUOTES | ENT_HTML5); //
      $sentence = urldecode($sentence);
      $sentence = preg_replace("/[^A-Za-z\-' ]/", ' ', $sentence);
      $sentence = preg_replace('/ +/', ' ', $sentence);
      $sentence = trim($sentence);
      $original = $sentence;
      $sentence = strtolower($sentence);


      $count = 0;

      //For each word in query
      foreach($querywords as $query_word)
      {
        $regex = '/\b'.strtolower($query_word).'\b/';
        //If word matches
        if(preg_match($regex, $sentence,  $matches, PREG_OFFSET_CAPTURE))
        {
          if(empty($potential_sentence))
          {
            $potential_sentence = $original;  //update potential sentence
            $pos = $matches[0][1];
            $matched_word = $query_word;
          }
          $count++;
        }
      }
      if($count == sizeof($querywords)) //all query terms matched?
      {
        $regex = '/\b'.strtolower($querywords[0]).'\b/';
        preg_match($regex, $sentence,  $matches, PREG_OFFSET_CAPTURE);
        $pos = $matches[0][1];
        $best_sentence = $original; //update best sentence
        break;
      }
    }

    if(!empty($best_sentence))
    {
      if($pos > 160)
        return '...'.substr($best_sentence, $pos - 160 , $pos + strlen($querywords[0])).'...';
      else
        return substr($best_sentence, 0 , 160).'...';
    }

    else if(!empty($potential_sentence))
    {
      if($pos > 160)
        return '...'.substr($potential_sentence, $pos - 160 , $pos + strlen($matched_word)).'...';
      else
        return substr($potential_sentence, 0 , 160).'...';
    }

    //check meta
    else
    {
      foreach ($meta as $field => $value)
      {
        $value = preg_replace("/[^A-Za-z' ]/", '', $value);
        $value = preg_replace('/ +/', ' ', $value);
        $value = trim($value);
        foreach($querywords as $query_word)
        {
          $regex = '/\b'.strtolower($query_word).'\b/';
          if(preg_match($regex, strtolower($value),  $matches, PREG_OFFSET_CAPTURE))
          {
            $pos = matches[0][1];
            if($pos > 160)
              return '...'.substr($value, $pos - 160 , $pos + strlen($query_word)).'...';
            else
              return substr($value, 0 , 160).'...';
          }
        }
      }
    }
  }

?>
