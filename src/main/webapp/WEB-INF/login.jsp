<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="zh">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录页面</title>
    <link rel="stylesheet" href="./css/login.css">
    <script>
        <%
            Cookie[] cookies = request.getCookies();
            if (!(cookies == null || cookies.length == 0)) {
                String username = null;
                String password = null;
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("username")) {
                        username = cookie.getValue();
                    } else if (cookie.getName().equals("password")) {
                        password = cookie.getValue();
                    }
                }
                if (username!=null && password!=null) {
        %>
        alert("你已经登录")
        window.location.href = "../videos"
        <%
                }
            }
        %>
    </script>
</head>

<body>
<div class="gap"></div>
<div class="form">
    <h2>
        <span>登录</span>
    </h2>
    <div class="form-area">
        <div class="form-item">
            <!-- <div class="form-item haserror"> -->
            <input type="text" placeholder="账号" id="username" autofocus maxlength="10">
        </div>
        <div class="form-item">
            <!-- <div class="form-item haserror"> -->
            <input type="password" placeholder="密码 (6-20个字符组成，区分大小写)" id="password" maxlength="20"
                   onkeydown="if(event.keyCode===13) document.getElementById('login').click()">
        </div>
        <div class="readme">

            <label>
                <input type="checkbox" name="checkbox" checked>
                <!-- <span class="checkbox"></span> -->
                <span>我已同意</span>
                <a href="javascript:alert('用户使用协议')">《用户使用协议》</a>
                和
                <a href="javascript:alert('账号中心规范')">《账号中心规范》</a>
            </label>
        </div>
        <div class="form-item">
            <button class="fill" onclick="login()" id="login">登录</button>
            <!-- <button class="fill" disabled>注册</button> -->
        </div>
        <div class="tip">
            <a href="/videos" style="margin-right: 10px">游客浏览</a>
            <a href="/register">注册&gt;</a>
        </div>
    </div>
</div>
<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script>
    function getQueryString(name) {
        var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    }

    function login() {
        let cb = document.getElementsByName("checkbox")[0]
        if (!cb.checked) {
            alert("请同意 《用户使用协议》 和 《账号中心规范》以使用服务")
            return
        }
        let username = document.getElementById("username").value
        let password = document.getElementById("password").value
        if (username.length < 3 || username.length > 10) {
            alert("账号长度3~10位")
            return
        }
        if (password.length < 6 || password.length > 20) {
            alert("密码长度6~20位")
            return
        }
        axios.post('/login', {
            username: username,
            password: password
        }).then(function () {
            alert("登录成功")
            let target = getQueryString("target")
            if (target === null) {
                window.location.href = "../videos"
            } else {
                window.location.href = target
            }
        }).catch(function (err) {
            alert(err.response.data.msg)
        })
    }
</script>
</body>

</html>