<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>

<%
  final String name = request.getParameter("name");
  final String labelKey = name + "_FORM_LABEL";
  final String tipSide = request.getParameter("tipSide");
  final String formName = name + "@Boolean";
%>

<div class="col-sm-12 label_tooltip_${param.tipSide}">
    <div class="form-group">
        <cadc:checkbox checkboxName="<%= name %>" i18nKey="<%= labelKey %>" />
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>" >
    </div>
</div>
