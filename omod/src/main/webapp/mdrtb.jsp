<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<style>
  .container {
    margin: 30px;
    font-size: 20px;    
  }
  
  .button {
    background-color: #1aac9b;
    color: white;
    padding: 8px 22px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    padding: 10px;
  }
</style>

<script>
  window.onload = function() {
    var link = document.getElementById("webappUrl");
    var target = link && link.href;
    if (target && window.location.href !== target) {
      window.location.href = target;
    }
  };
</script>


<div class="container">
	<a id="webappUrl" class="button" href="${webappUrl}">MDRTB 3.0</a>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
