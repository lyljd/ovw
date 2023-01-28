<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><%=request.getAttribute("msg")%>
    </title>
    <link rel="stylesheet" href="//at.alicdn.com/t/c/font_3823987_h5jhj2ymrpu.css">
    <style>
        body, html {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
        }

        body {
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }

        a {
            color: rgb(0, 191, 255);
            text-decoration: none;
        }
    </style>
</head>
<body>
<span class="iconfont icon-cry" style="font-size: 300px; color: rgb(64,64,64)"></span>
<span style="font-size: 100px; margin-top: -50px;color: rgb(64,64,64)"><%=request.getAttribute("status")%></span>
<span style="font-size: 25px;margin-top: -10px;color: rgb(160,160,160)"><%=request.getAttribute("msg")%></span>
<span style="margin-top: 10px;">
    <%
        if (request.getAttribute("status").equals(401)) {
    %>
        <a href="javascript:window.location.href = '../login?target='+window.location.href" style="margin-right: 20px">前往登录</a>
    <%
    } else {
    %>
        <a href="javascript:location.reload();" style="margin-right: 20px">刷新网页</a>
    <%
        }
    %>
    <a href="/videos" style="margin-left: 20px">返回首页</a>
</span>
</body>
</html>
