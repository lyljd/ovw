<%@ page import="model.VideoView" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.UserView" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    UserView u = (UserView) request.getAttribute("user");
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
    <link rel="stylesheet" href="../bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="../css/index.css">
    <link rel="stylesheet" href="../css/user.css">
    <link rel="stylesheet" href="//at.alicdn.com/t/c/font_3823987_odnm2h4683j.css">
    <script src="../bootstrap/dist/js/"></script>
    <title><%=u.nickname + "的个人空间"%>
    </title>
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
               onkeydown="if(event.keyCode===13) document.getElementById('search-button').click()" style="height: 58%;">
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
<div class="container">
    <div class="top">
        <div class="top-inner">
            <div>
                <img src="<%=u.avatar%>" width="36" height="36"
                     onerror="javascript:this.src='../img/avatar.jpg';" class="user-portrait">
            </div>
            <div class="user-info">
                <div style="font-size: 28px; color: #EBEEF5;"><%=u.nickname%>
                </div>
                <%
                    if (u.signature == null) {
                        u.signature = "该用户没有签名";
                    }
                %>
                <div style="font-size:14px; color: #E4E7ED;">
                    <span class="iconfont icon-a-idID"></span>
                    <span style="margin-right: 3px"><%=u.id%></span>
                    <span class="iconfont icon-file-signature"></span>
                    <span><%=u.signature%></span>
                </div>
            </div>
        </div>
    </div>
    <div class="middle">
        <div class="menu">
            <div class="menu-btn">
                <%
                    if (request.getAttribute("who").equals("我")) {
                %>
                <a href="/me/upload">
                    <%
                        }
                    %>
                    <%=request.getAttribute("who")%>的投稿</a>
            </div>
            <%
                if (request.getAttribute("who").equals("我")) {
            %>
            <div class="menu-btn"><a href="/me/like"><%=request.getAttribute("who")%>的点赞</a></div>
            <div class="menu-btn"><a href="/me/star"><%=request.getAttribute("who")%>的收藏</a></div>
            <hr style="margin: 0;">
            <div class="menu-btn"><a href="/user/update/avatar">修改头像</a></div>
            <div class="menu-btn"><a href="/user/update/info">修改信息</a></div>
            <div class="menu-btn"><a href="/user/update/password">修改密码</a></div>
            <%
                }
            %>
        </div>

        <div class="show-option">
            <div>
                <div class="title" style="text-align: center"><%=request.getAttribute("page")%>
                </div>
                <div>
                    <div class="my-upload-ul">
                        <%
                            ArrayList<VideoView> videos = (ArrayList<VideoView>) request.getAttribute("videos");
                            if (videos.isEmpty()) {
                        %>
                        <span style="text-align: center; width: 100%">无视频内容</span>
                        <%
                            }
                            for (VideoView v : videos) {
                        %>
                        <div class="my-upload-li">
                            <a href="<%="/video/"+v.id%>" target="_blank">
                                <img src="<%=v.cover%>" alt loading="lazy"
                                     style="border: 1px solid rgb(192, 192, 192);border-radius: 10px">
                            </a>
                            <div style="font-size: 15px; margin-top: 3px"><%=v.title%>
                            </div>
                            <div style="display: flex;margin-left: -2px; font-size: 10px">
                                <span class="iconfont icon-clock" style="margin-top: -4px"></span>
                                <span style="margin-top: -1px"><%=v.time%></span>
                                <span class="iconfont icon-browse" style="margin-left: 10px; margin-top: -4px"></span>
                                <span style="margin-top: -1px"><%=v.views%></span>
                            </div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script>
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