<!--视频详情展示页-->
<%@ page import="model.VideoView" %>
<%@ page import="java.util.Objects" %>
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

<%
    VideoView v = (VideoView) request.getAttribute("video");
%>

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../css/play.css">
    <link rel="stylesheet" href="../bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="//at.alicdn.com/t/c/font_3823987_h5jhj2ymrpu.css">
    <title><%=v.title%>
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
<div class="main-view container">
    <section class="video-head">
        <div class="video-info">
            <div class="video-info-title"><%=v.title%>
            </div>
            <div class="video-time-view">
                <span class="iconfont icon-clock"></span>
                <span><%=v.time%></span>
                <span class="iconfont icon-browse" style="margin-left: 10px"></span>
                <span><%=v.views%></span>
            </div>
        </div>
        <div>
            <a href="/user/<%=v.uid%>" target="_blank" style=" font-size: 18px; text-decoration: none; color: black;">
                <span><%=v.nickname%></span>
                <img src="<%=v.avatar%>" width="50px" height="50px" style="border-radius: 50%"
                     onerror="javascript:this.src='../img/avatar.jpg';">
            </a>
        </div>
    </section>
    <section class="player">
        <video id="myVideo" controls="controls" style="width: 100%; height:100%; object-fit: fill">
            <source src="<%=v.video%>" type="video/webm">
        </video>
    </section>
    <section>
        <div class="opt-father">
            <div class="opt-child">
                <span id="islike" class="iconfont icon-good-fill" style="cursor: pointer" onclick="like()"></span>
                <span id="isnotlike" class="iconfont icon-good" style="cursor: pointer" onclick="like()"></span>
                <span id="like" style="font-size: 20px; margin-right: 30px"><%=v.likes%></span>
                <span id="isstar" class="iconfont icon-collection-fill" style="cursor: pointer" onclick="star()"></span>
                <span id="isnotstar" class="iconfont icon-collection" style="cursor: pointer" onclick="star()"></span>
                <span id="star" style="font-size: 20px"><%=v.stars%></span>
            </div>
            <%
                if (Objects.equals(nickname, v.nickname)) {
            %>
            <div class="opt-child">
                <span class="iconfont icon-editor" style="margin-right: 30px;cursor: pointer" onclick="upd()"></span>
                <span class="iconfont icon-ashbin" style="margin-right: -10px;cursor: pointer" onclick="del()"></span>
            </div>
            <%
                }
            %>
        </div>
        <hr style="width: 1000px; border-color: rgb(192,192,192); margin: 0">
        <div style="margin-top: 7px; width: 1000px">
            <div style="font-size: 20px">简介</div>
            <div>
                <%
                    if (v.info == null) {
                        v.info = "该视频没有简介";
                    }
                %>
                <textarea id="info" readonly
                          style="font-size: 15px; border: none; resize: none; outline: none; overflow: hidden; height: 1px; width: 100%; color: rgb(96,96,96)"><%=v.info%></textarea>
            </div>
        </div>
    </section>
</div>
<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script>
    //进入视频详情页后，请求后端返回的数据中有当前用户是否有点赞和收藏该视频的数据，然后根据结果决定页面的按钮显示
    <%
        if ((boolean) request.getAttribute("islike")) {
    %>
        document.getElementById("isnotlike").hidden = true
    <%
        } else {
    %>
        document.getElementById("islike").hidden = true
    <%
        }
    %>

    <%
        if ((boolean) request.getAttribute("isstar")) {
    %>
    document.getElementById("isnotstar").hidden = true
    <%
        } else {
    %>
    document.getElementById("isstar").hidden = true
    <%
        }
    %>

    //搜索
    function search() {
        let key = document.getElementById("search-input").value;
        if (key.length === 0) {
            alert("搜索关键字为空")
            return
        }

        window.open('../videos/search?key=' + key)
    }

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

    //设置视频播放器默认音量50%
    document.getElementById("myVideo").volume = 0.5

    let infoTA = document.getElementById("info")
    infoTA.style.height = infoTA.scrollHeight + "px"

    //点赞
    function like() {
        <%
            if (nickname == null) {
        %>
        alert("请登录")
        <%
            } else {
        %>
        axios.post('/video/like', {
            vid: "<%=v.id%>",
        }).then(function (res) {
            if (res.data.status === true) {
                alert("点赞成功")
                document.getElementById("isnotlike").hidden = true;
                document.getElementById("islike").removeAttribute("hidden");
            } else {
                alert("取消点赞成功")
                document.getElementById("islike").hidden = true;
                document.getElementById("isnotlike").removeAttribute("hidden");
            }
            document.getElementById("like").innerText = res.data.num
        }).catch(function (err) {
            alert(err.response.data.msg)
        })
        <%
            }
        %>
    }

    //收藏
    function star() {
        <%
            if (nickname == null) {
        %>
        alert("请登录")
        <%
            } else {
        %>
        axios.post('/video/star', {
            vid: "<%=v.id%>",
        }).then(function (res) {
            if (res.data.status === true) {
                alert("收藏成功")
                document.getElementById("isnotstar").hidden = true;
                document.getElementById("isstar").removeAttribute("hidden");
            } else {
                alert("取消收藏成功")
                document.getElementById("isstar").hidden = true;
                document.getElementById("isnotstar").removeAttribute("hidden");
            }
            document.getElementById("star").innerText = res.data.num
        }).catch(function (err) {
            alert(err.response.data.msg)
        })
        <%
            }
        %>
    }

    //更新
    function upd() {
        window.location.href = "<%="/video/update/"+v.id%>"
    }

    //删除
    function del() {
        let r = confirm("你确定要删除该视频吗？");
        if (r === false) {
            return;
        }

        axios.post('/video/delete', {
            id: "<%=v.id%>",
        }).then(function () {
            alert("删除成功")
            window.location.href = "/videos"
        }).catch(function (err) {
            alert(err.response.data.msg)
        })
    }
</script>
</body>

</html>