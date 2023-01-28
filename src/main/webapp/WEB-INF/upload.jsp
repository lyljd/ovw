<!--视频投稿和更新展示页-->
<%@ page contentType="text/html;charset=UTF-8" %>
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
    if (request.getAttribute("info") == null) {
        request.setAttribute("info", "");
    }
%>
<html>
<head>
    <title><%=request.getAttribute("page")%>
    </title>
    <link rel="stylesheet" href="/css/index.css">
    <link rel="stylesheet" href="/css/register.css">
    <style>
        textarea {
            resize: vertical;
            border: 1px solid #dcdfe6;
            width: 100%;
            box-sizing: border-box;
            border-radius: 4px;
            height: 40px;
            font-size: 14px;
            color: #606266;
            font-family: PingFangSC-Medium;
            height: 100px;
            padding: 9px 1em;
        }

        textarea:hover {
            border-color: #c0c4cc;
        }

        textarea::placeholder {
            color: #ccc;
        }

        textarea:focus {
            border-color: #00a1d6;
        }
    </style>
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
        <input type="text" placeholder="请输入关键字" id="search-input" style="height: 58%; padding-left: 0;"
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
                         onerror="javascript:this.src='/img/avatar.jpg';">
                </div>
                <%
                    }
                %>
            </li>
        </ul>
    </div>
</div>

<div class="gap"></div>
<div class="form">
    <h2>
        <span><%=request.getAttribute("page")%></span>
    </h2>
    <div class="form-area">
        <div class="form-item">
            <input type="text" id="title" maxlength="30" minlength="2" placeholder="标题" autofocus>
        </div>
        <div class="form-item">
            <textarea id="info" maxlength="1000" placeholder="简介"><%=request.getAttribute("info")%></textarea>
        </div>
        <div class="form-item">
            封面：
            <input type="file" id="cover">
        </div>
        <div class="form-item">
            视频：
            <input type="file" id="video">
        </div>
        <%
            if (request.getAttribute("page").equals("修改")) {
        %>
        <span style="color: red; font-size: 10px">*如果不想修改封面或视频就不要选择文件</span>
        <%
            }
        %>
        <div class="form-item">
            <button class="fill" onclick="upload()">提交</button>
        </div>
    </div>
</div>

<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script>
    //上传视频
    function upload() {
        let title = document.getElementById("title").value
        let info = document.getElementById("info").value
        let cover = document.getElementById("cover").files[0]
        let video = document.getElementById("video").files[0]
        if (title.length < 2 || title.length > 30) {
            alert("标题长度2~30")
            return
        }
        <%
            if(request.getAttribute("page").equals("投稿")) {
        %>
        if (cover === undefined) {
            alert("请选择封面")
            return
        }
        if (video === undefined) {
            alert("请选择视频")
            return
        }
        <%
            }
        %>
        let formData = new FormData()
        formData.append('title', title)
        if (info.length !== 0) {
            formData.append('info', info)
        }
        if (cover !== undefined) {
            formData.append('cover', cover)
        }
        if (video !== undefined) {
            formData.append('video', video)
        }
        <%
            if(request.getAttribute("page").equals("修改")) {
        %>
        formData.append('id', <%=request.getAttribute("id")%>)
        <%
            }
        %>
        axios({
            url: '<%=request.getAttribute("url")%>',
            method: 'post',
            data: formData,
        }).then(function (res) {
            alert("<%=request.getAttribute("page")%>成功")
            window.location.href = "/video/" + res.data.id
        }).catch(function (err) {
            alert(err.response.data.msg)
        })
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

    //搜索
    function search() {
        let key = document.getElementById("search-input").value;
        if (key.length === 0) {
            alert("搜索关键字为空")
            return
        }
        window.location.href = '../videos/search?key=' + key
    }

    //视频投稿和修改共用该页面，根据不同的页来决定标题展示
    <%
        if(request.getAttribute("page").equals("修改")) {
    %>
    document.getElementById("title").value = "<%=request.getAttribute("title")%>"
    <%
        }
    %>
</script>
</body>
</html>
