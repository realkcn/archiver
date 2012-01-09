<%@page language="java" import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap,com.opensymphony.xwork2.ognl.OgnlValueStack" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
  <pg:first>第一页</pg:first><pg:prev>上一页</pg:prev><pg:pages>  
                    <s:if test="#request.currentPageNumber==#request.pageNumber">    
                       <font color="red">${request.pageNumber}</font>  
                    </s:if><s:else>   
                       <pg:go newpage="${request.pageNumber}">${attr.pageNumber}</pg:go>
                    </s:else>
               </pg:pages><pg:next>下一页</pg:next><pg:last>最后一页</pg:last>&nbsp;<input type="text" id="gopage" maxlength=6 length=6/><pg:jsgo elementid="gopage">跳转</pg:jsgo>
