<%@ page import="ca.nrc.cadc.config.ApplicationConfiguration" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<!--[if IE 7]>
<html lang="en" class="no-js ie7"><![endif]-->
<!--[if IE 8]>
<html lang="en" class="no-js ie8"><![endif]-->
<!--[if gt IE 8]><!-->
<html lang="en" class="no-js">
  <!--<![endif]-->

  <head>
    <meta charset="utf-8"/>
    <title>CAOM-2 Search</title>

    <meta name="dcterms.creator"
          content="Government of Canada. National Research Council Canada"/>
    <meta name="dcterms.title"
          content="National science infrastructure (NRC Herzberg, Programs in Astronomy and Astrophysics) - National Research Council Canada"/>
    <meta name="dcterms.issued" title="W3CDTF" content="2013-04-30"/>

    <!--#if expr="$QUERY_STRING_UNESCAPED = /LAST_MOD=([0-9]{4}-[0-9]{2}-[0-9]{2})/" -->
    <!--#set var="DCTERMS_LMOD" value="<meta name=\"dcterms.modified\" title=\"W3CDTF\" content=\"$1\" />" -->
    <!--#echo encoding="none" var="DCTERMS_LMOD" -->
    <!--#endif -->

    <meta name="dcterms.language" title="ISO639-2" content="eng"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="dcterms.contributor"
          content="NRC Communications and Corporate Relations Branch"/>
    <meta name="dcterms.description"
          content="NRC Herzberg is Canada's gateway to the stars. In addition to operating the government's observatories and a national astronomy data centre, we develop advanced scientific instruments for astronomical observatories in collaboration with industrial partners � bringing out-of-this-world technology back down to Earth."/>
    <meta name="description"
          content="NRC Herzberg is Canada's gateway to the stars. In addition to operating the government's observatories and a national astronomy data centre, we develop advanced scientific instruments for astronomical observatories in collaboration with industrial partners � bringing out-of-this-world technology back down to Earth."/>
    <meta name="keywords"
          content="NRC Herzberg; astronomy; astronomical observatories; Dominion Astrophysical Observatory; Dominion Radio Astrophysical Observatory; Astrophysics"/>
    <meta name="dcterms.subject"
          content="NRC Herzberg; National Science Infrastructure; astronomy; astronomical observatories; Dominion Astrophysical Observatory; Dominion Radio Astrophysical Observatory; Astrophysics"/>
    <meta name="dcterms.audience" title="gcaudience" content="business; general public; scientists"/>
    <meta name="dcterms.type" title="gctype"
          content="contact information; organizational description; promotional material"/>
    <meta name="dcterms.identifier" title="nrc.dcr"
          content="/default/main/prod/public/nrc-cnrc/WORKAREA/nrc-cnrc/templatedata/gcweb/generic/data/eng-fra/rd-rd/nsi-isn/index-index"/>


    <script type="text/javascript" src="js/jquery-2.2.4.min.js" ></script>
    <script type="text/javascript" src="js/jquery.address.js"></script>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="css/bootstrap-theme.min.css" rel="stylesheet" media="screen">
    <link href="css/bootstrap-toggle.min.css" rel="stylesheet" media="screen">

    <!-- CustomCSSStart -->
    <link href="css/caom2_search_bootstrap_override.css" rel="stylesheet" media="screen">
    <!-- CustomCSSEnd -->

  </head>

  <!-- noindex -->
