<%@ page contentType="text/html;charset=UTF-8" %>
<%
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
  if (request.getAttribute("signature") == null) {
    request.setAttribute("signature", "");
  }
%>
<html>
<head>
  <title><%=request.getAttribute("page")%>
  </title>
  <link rel="stylesheet" href="/css/index.css">
  <link rel="stylesheet" href="/css/register.css">
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

  <%
    if (request.getAttribute("page").equals("修改信息")) {
  %>
  <div class="form-area">
    <div class="form-item">
      <input type="text" id="nickname" maxlength="10" minlength="3" placeholder="昵称" autofocus>
    </div>
    <div class="form-item">
      <input type="text" id="signature" maxlength="25" placeholder="签名" autofocus>
    </div>
    <div class="form-item">
      <button class="fill" onclick="updateInfo()">提交</button>
    </div>
  </div>
  <%
    }
  %>

  <%
    if (request.getAttribute("page").equals("修改头像")) {
  %>
  <div class="form-area">
    <div class="form-item">
      头像：
      <input type="file" id="avatar">
    </div>
    <div class="form-item">
      <button class="fill" onclick="updateAvatar()">提交</button>
    </div>
  </div>
  <%
    }
  %>

  <%
    if (request.getAttribute("page").equals("修改密码")) {
  %>
  <div class="form-area">
    <div class="form-item">
      <input type="password" id="password" maxlength="20" minlength="6" placeholder="密码 (6-20个字符组成，区分大小写)" autofocus>
    </div>
    <div class="form-item">
      <input type="password" id="password2" maxlength="20" minlength="6" placeholder="再次填写密码" autofocus>
    </div>
    <div class="form-item">
      <button class="fill" onclick="updatePassword()">提交</button>
    </div>
  </div>
  <%
    }
  %>
</div>

<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script>
  function updateInfo() {
    let nickname = document.getElementById("nickname").value
    let signature = document.getElementById("signature").value
    if (nickname.length < 3 || nickname.length > 10) {
      alert("昵称长度3~10")
      return
    }
    if (signature.length > 25) {
      alert("签名长度0~25")
      return
    }
    if (signature.length === 0) {
      signature = null
    }
    axios({
      url: '<%=request.getAttribute("url")%>',
      method: 'post',
      data: {
        nickname: nickname,
        signature: signature
      },
    }).then(function (res) {
      alert("<%=request.getAttribute("page")%>成功")
      window.location.href = "/user/" + res.data.id
    }).catch(function (err) {
      alert(err.response.data.msg)
    })
  }

  function updateAvatar() {
    let avatar = document.getElementById("avatar").files[0]
    if (avatar === undefined) {
      alert("请选择头像")
      return
    }
    let formData = new FormData()
    formData.append('avatar', avatar)
    axios({
      url: '<%=request.getAttribute("url")%>',
      method: 'post',
      data: formData,
    }).then(function (res) {
      alert("<%=request.getAttribute("page")%>成功")
      window.location.href = "/user/" + res.data.id
    }).catch(function (err) {
      alert(err.response.data.msg)
    })
  }

  function updatePassword() {
    let password = document.getElementById("password").value
    let password2 = document.getElementById("password2").value
    if (password.length < 6 || password.length > 20) {
      alert("密码长度6~20位")
      return
    }
    if (password2 !== password) {
      alert("两次输入的密码不一致")
      return
    }
    axios.post('<%=request.getAttribute("url")%>', {
      password: password,
      password2: password2
    }).then(function (res) {
      alert("<%=request.getAttribute("page")%>成功")
      window.location.href = "/user/" + res.data.id
    }).catch(function (err) {
      alert(err.response.data.msg)
    })
  }

  function logout() {
    let r = confirm("你确定要退出登录吗？");
    if (r === true) {
      let keys = document.cookie.match(/[^ =;]+(?=\=)/g);
      if (keys) {
        for (let i = keys.length; i--;)
          document.cookie = keys[i] + "=; max-age=0; path=/;";
      }
      window.location.href = "/login"
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

  <%
      if(request.getAttribute("page").equals("修改信息")) {
  %>
  document.getElementById("nickname").value = "<%=request.getAttribute("nickname")%>"
  document.getElementById("signature").value = "<%=request.getAttribute("signature")%>"
  <%
      }
  %>
</script>
</body>
</html>
