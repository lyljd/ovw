<!--视频主页和搜索展示页-->
<%@ page import="model.VideoView" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    //读取cookie用于后序判断登录状态
    Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) {
        return;
    }
    String nickname = null;
    String avatar = null;
    for (Cookie cookie : cookies) {
        if (cookie.getName().equals("nickname")) {
            nickname = cookie.getValue();
        } else if (cookie.getName().equals("avatar")) {
            avatar = cookie.getValue();
        }
    }
%>
<!DOCTYPE html>
<html lang="zh">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%=request.getAttribute("page")%>
    </title>
    <link rel="stylesheet" href="../css/index.css">
    <link rel="stylesheet" href="//at.alicdn.com/t/c/font_3823987_h5jhj2ymrpu.css">
</head>

<body>
<div class="head">
    <div>
        <ul class="left-entry_title">
            <li class="v-popover-wrap">
                <a href="/videos">
                    <span>首页</span>
                </a>
                <%
                    if (nickname != null) {
                %>
                <a href="/video/upload" style="margin-left: 10px">
                    <span>投稿</span>
                </a>
                <a href="/me/upload" style="margin-left: 10px">
                    <span>个人空间</span>
                </a>
                <%
                    }
                %>
            </li>
        </ul>
    </div>
    <div id="search">
        <input type="text" placeholder="请输入关键字" id="search-input"
               onkeydown="if(event.keyCode===13) document.getElementById('search-button').click()">
        <button id="search-button" type="submit" onclick="search()">搜索</button>
        <ul class="box" style="display: none;"></ul>
    </div>
    <div>
        <ul class="right-entry">
            <li class="info-login">
                <%
                    if (nickname == null) {
                %>
                <div class="portrait">
                    <a href="/login">登录</a>
                </div>
                <%
                } else {
                %>
                <div style="display: flex;align-items: center; cursor: pointer" onclick="logout()">
                    <span style="margin-right: 5px; font-size: 18px"><%=nickname%></span>
                    <img src="<%=avatar%>" width="36" height="36" style="border-radius: 50%;"
                         onerror="javascript:this.src='../img/avatar.jpg';">
                </div>
                <%
                    }
                %>
            </li>
        </ul>
    </div>
</div>
<div class="main-recommend-title"><%=request.getAttribute("page")%>
</div>
<main class="main-view">
    <section>
        <ul>
            <%
                ArrayList<VideoView> videos = (ArrayList<VideoView>) request.getAttribute("videos");
                if (videos.isEmpty()) {
            %>
            <span>无视频内容</span>
            <%
                }
                for (VideoView v : videos) {
            %>
            <li>
                <div class="border-container">
                    <div>
                        <a href="<%="/video/"+v.id%>" target="_blank" one-link-mark="yes">
                            <img src="<%=v.cover%>" alt loading="lazy">
                        </a>
                    </div>
                    <div class="main-recommend-li-title">
                        <%=v.title%>
                    </div>
                    <div class="video-nickname">
                        <span class="iconfont icon-account"></span>
                        <a href="<%="/user/"+v.uid%>" target="_blank">
                            <span class="nn"><%=v.nickname%></span>
                        </a>
                    </div>
                    <div class="video-time-view">
                        <span class="iconfont icon-clock"></span>
                        <span><%=v.time%></span>
                        <span class="iconfont icon-browse" style="margin-left: 10px"></span>
                        <span><%=v.views%></span>
                    </div>
                </div>
            </li>
            <%
                }
            %>
        </ul>
    </section>
</main>
<script>
    //搜索结果页也使用了该文件渲染，若是搜索结果页，则会在搜索框将关键字放进去
    <%
        if (((String)request.getAttribute("page")).startsWith("搜索")) {
    %>
    document.getElementById("search-input").value = "<%=request.getAttribute("key")%>"
    <%
        }
    %>

    //退出登录
    function logout() {
        let r = confirm("你确定要退出登录吗？");
        if (r === true) {
            let keys = document.cookie.match(/[^ =;]+(?=\=)/g);
            if (keys) {
                for (let i = keys.length; i--;)
                    document.cookie = keys[i] + "=; max-age=0; path=/;";
            }
            window.location.href = "../login"
        }
    }

    //搜索
    function search() {
        let key = document.getElementById("search-input").value;
        if (key.length === 0) {
            alert("搜索关键字为空")
            return
        }
        window.location.href = '../videos/search?key=' + key
    }
</script>
</body>

</html>