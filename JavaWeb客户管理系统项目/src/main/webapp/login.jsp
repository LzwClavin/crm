<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
    <base href="<%=path%>">
    <meta charset="UTF-8">
    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
</head>
<script type="text/javascript">
    $(() => {
        /*
        如果当前窗口的顶级窗口不是当前窗口，将顶级窗口的地址设置为当前窗口的地址
        防止在工作台销毁session后，点击某个菜单会使登录页出现在主区的情况出现
        */
        if (window.top != window) {
            window.top.location = window.location;
        }

        //页面加载完毕后，让用户名文本框自动获得焦点
        $('#username').focus();

        //点击登录按钮后，验证登录
        $('#b').click(function () {
            //获取去除前后空白的用户名和密码，调用$.trim函数，参数是需要去除空白的文本
            let username = $.trim($('#username').val());
            let password = $.trim($('#password').val());

            //验证账号和密码不能为空，如果用户名或密码是空串，对应的布尔值是false，短路与运算为false，取反是true进入if域
            if (!(username && password)) {
                //填充错误提示信息
                $('#msg').html("用户名或密码不能为空！");
            } else {//这里表示用户名和密码都不为空，发送Ajax请求验证登录，局部刷新错误信息
                $.ajax({
                    type: "post",//登录操作有密码安全信息，是post请求
                    url: "settings/user/login.do",
                    data: {
                        username: username,
                        password: password
                    },
                    dataType: "json",
                    success: function (data) {
                        //data代表的JSON对象的第一个属性是success，值是true或false，表示登录验证成功或失败
                        if (data.success) {
                            //跳转到工作台页面
                            location = "workbench/index.jsp";
                        } else {
                            //第二个属性是msg，值是错误信息，用于更新前端错误信息内容
                            $('#msg').html(data.msg);
                        }
                    }
                })
            }
        })

        //显示错误信息后，如果用户名或密码文本框再次获焦，清空错误信息；将清空语句提取出来一个公共函数访问
        $('#username').focus(function () {
            clearMsg();
        })

        $('#password').focus(function () {
            clearMsg();
        })

        /*
        为当前登录页绑定键盘事件，当按回车键时让登录按钮自动点击，并执行与登录按钮一样的功能
        注意window要使用$包裹变成jQuery对象才能调用keyup方法，否则仍是原生的js，要调用onkeyup
        */
        $(window).keyup(function (event) {
            if (event.keyCode === 13) {
                $('#b').click();
            }
        })

        //对于不同浏览器的每次刷新，有可能会保留上一次的文本框内容，此时需要手动将文本框和错误信息清空
        $('#username').val("");
        $('#password').val("");
        clearMsg();
    })

    function clearMsg() {
        $('#msg').html("");
    }
</script>
<body>
<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
    <img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
</div>
<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
    <div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">
        CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
</div>

<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
    <div style="position: absolute; top: 0px; right: 60px;">
        <div class="page-header">
            <h1>登录</h1>
        </div>
        <form action="" class="form-horizontal" role="form">
            <div class="form-group form-group-lg">
                <div style="width: 350px;">
                    <input class="form-control" type="text" placeholder="用户名" id="username">
                </div>
                <div style="width: 350px; position: relative;top: 20px;">
                    <input class="form-control" type="password" placeholder="密码" id="password">
                </div>
                <div class="checkbox" style="position: relative;top: 30px; left: 10px;">
                    <%--错误信息的存放位置--%>
                    <span id="msg" style="color: red"></span>
                </div>
                <button type="button" id="b" class="btn btn-primary btn-lg btn-block"
                        style="width: 350px; position: relative;top: 45px;">登录
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>