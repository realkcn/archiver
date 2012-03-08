<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

    <title>水木社区归档站 - 版面列表</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="description" content="水木社区版面列表">

    <jsp:include page="include/htmlheader.jsp"/>
    <link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />
</head>
<body onLoad='javascript:document.getElementById("boardsearch").focus()'>
<script type="text/javascript" src="${request.docbase}scripts/jquery-1.7.1.pack.js" ></script>
<script type="text/javascript" src="${request.docbase}scripts/jquery-ui-1.8.18.custom.min.js"></script>
<s:set var="pagedetail" value="'版面列表'" scope="request"/>
<jsp:include page="include/header.jsp"/>
<div class="container">
    <div class="control-group form-horizontal pull-left"><label class="control-label">快速查找版面</label>

        <div class="controls"><input id="boardsearch" name="boardsearch" class="input-medium" /></div>
    </div>
    <table class="table table-bordered">
        <tr>
            <td>序号</td>
            <td>英文名</td>
            <td>中文名</td>
            <td>主题数</td>
            <td>文章数</td>
            <s:set var="boardarray" value="''"/>
            <s:iterator value="boardlist" status="boardindex">
            <s:if test="#boardindex.count==1">
                <s:set var="boardarray" value="#boardarray+'{value:\\''+name+' '+cname+'\\',label:\\''+name+' '+cname+'\\',id:'+boardid+'}\n'"/>
            </s:if><s:else>
                <s:set var="boardarray" value="#boardarray+',{value:\\''+name+' '+cname+'\\',label:\\''+name+' '+cname+'\\',id:'+boardid+'}\n'"/>
            </s:else>
        <tr>
            <td><s:property value="#boardindex.count"/></td>
            <td><a href="board-${boardid}.html"><s:property value="name"/></a></td>
            <td><a href="board-${boardid}.html"><s:property value="cname"/></a></td>
            <td>${threads}</td>
            <td>${articles}</td>
        </tr>
        </s:iterator>
    </table>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
<style>
    #project-label {
        display: block;
        font-weight: bold;
        margin-bottom: 1em;
    }
</style>
<script>
    $(function() {
        var boardArray = [
                <s:property value="#boardarray" escape="false"/>
        ];

        $( "#boardsearch" ).autocomplete({
            minLength: 0,
            source: boardArray,
            focus: function( event, ui ) {
                $( "#project" ).val( ui.item.label );
                return false;
            },
            select: function( event, ui ) {
                window.location.href="board-"+ui.item.id+".html";
                return false;
            }
        })
                .data( "autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li></li>" )
                    .data( "item.autocomplete", item )
                    .append( "<a>" + item.value+"</a>" )
                    .appendTo( ul );
        };
    });
</script>
</html>
