<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=path%>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

    <script type="text/javascript">
        //默认情况下取消和保存按钮是隐藏的
        var cancelAndSaveBtnDefault = true;

        $(function () {
            $("#remark").focus(function () {
                if (cancelAndSaveBtnDefault) {
                    //设置remarkDiv的高度为130px
                    $("#remarkDiv").css("height", "130px");
                    //显示
                    $("#cancelAndSaveBtn").show("2000");
                    cancelAndSaveBtnDefault = false;
                }
            });

            $("#cancelBtn").click(function () {
                //显示
                $("#cancelAndSaveBtn").hide();
                //设置remarkDiv的高度为130px
                $("#remarkDiv").css("height", "90px");
                cancelAndSaveBtnDefault = true;
            });

            $(".remarkDiv").mouseover(function () {
                $(this).children("div").children("div").show();
            });

            $(".remarkDiv").mouseout(function () {
                $(this).children("div").children("div").hide();
            });

            $(".myHref").mouseover(function () {
                $(this).children("span").css("color", "red");
            });

            $(".myHref").mouseout(function () {
                $(this).children("span").css("color", "#E6E6E6");
            });

            //页面加载完毕后，展现本线索关联的市场活动列表
            activityList();

            /*
            绑定关联市场活动模态窗口的搜索框的键盘按下事件，捕获回车键键值13
            发送Ajax请求根据搜索框提供的市场活动名模糊查询市场活动列表；查询条件首先是模糊匹配的市场活动名
            其次必须是本线索没有关联过的市场活动，因为现在是进行关联操作，如果查询出已经关联过的市场活动，毫无意义
            */
            $('#aname').keydown(function (event) {
                if (event.keyCode == 13) {
                    $.ajax({
                        type: "get",
                        url: "workbench/clue/getActivitysNNCI.do",
                        data: {
                            aname: $.trim($('#aname').val()),
                            cid: '${clue.id}'//获取请求域中保存的clue对象中的id，根据id获取本线索关联以外的市场活动
                        },
                        dataType: "json",
                        success: function (data) {
                            let html = "";
                            $.each(data, function (i, n) {
                                html += '<tr>';

                                /*
                                复选框的value设置为市场活动的id，进行关联操作时，使用当前线索id与被选中的市场活动id一起关联
                                使用name是因为需要获取到所有复选框并从中选出所有被选中的
                                */
                                html += '<td><input type="checkbox" name="check" value="' + n.id + '"/></td>';
                                html += '<td>' + n.name + '</td>';
                                html += '<td>' + n.startDate + '</td>';
                                html += '<td>' + n.endDate + '</td>';
                                html += '<td>' + n.owner + '</td>';
                                html += '</tr>';
                            })

                            $('#abody').html(html);
                        }
                    })

                    /*
                    对于模态窗口，如果在文本框中按回车，默认触发强制刷新当前页面事件，会关闭模态窗口，然后清空页面上的数据
                    所以该默认行为应该被禁用，在渲染完市场活动列表后再禁用，直接强制返回即可，注意要返回false
                    */
                    return false;
                }
            })

            //绑定关联按钮的单击事件，关联表示往关系表中添加一条记录，绑定当前线索和选中的市场活动的关联关系，可以批量关联
            $('#bundBtn').click(function () {
                //获取所有属性name是check且checked是true的复选框
                let $check = $('input[name=check]:checked')

                //如果jQuery对象中无元素，即数组长度是0，取反是true，表示无法关联，必须先选择
                if (!$check.length) {
                    alert("请选择需要关联的市场活动！");
                } else {
                    /*
                    批量关联需要发送Ajax请求给后端处理，由于包含多个市场活动id，所以参数名是一样的，不能使用JSON对象声明参数列表
                    必须使用拼串的方式；另外，需要传递市场活动id外，还需要传递当前线索id，将线索与市场活动关联起来
                    而关系表id由后台自动生成
                    */
                    let data = "cid=${clue.id}";//表示当前线索id

                    /*
                    遍历jQuery数组，获取其中每个被选中的复选框，其中的value就是市场活动id，追加到参数中
                    这里的“&”与基础参数线索id拼接上，同时能确保最后一个参数后面无“&”
                    */
                    for (var i = 0; i < $check.length; i++) {
                        data += "&aid=" + $check[i].value;
                    }

                    $.ajax({
                        type: "get",
                        url: "workbench/clue/bund.do",
                        data: data,//以字符串的形式发送请求参数
                        dataType: "json",
                        success: function (data) {
                            if (data.success) {
                                //渲染新的关联列表
                                activityList();

                                //关闭模态窗口
                                $('#bundModal').modal('hide');

                                //清空模态窗口中搜索框填写的信息
                                $('#aname').val("");

                                //全选复选框取消全选
                                $('#checkedAll').prop("checked", false);

                                //清空模态窗口中搜索出来的市场活动列表
                                $('#abody').html("");
                            } else {
                                alert("关联市场活动失败！");
                            }
                        }
                    })
                }
            })

            //绑定全选复选框的点击事件
            $('#checkedAll').click(function () {
                /*
                选择所有的当前页的复选框，对这些复选框设置checked属性，属性值中的this表示全选复选框的DOM对象
                this.checked表示选中为true，未选中为false；即设置所有复选框的选中状态为全选复选框的选中状态，同为true或同为false
                */
                $('input[name=check]').prop("checked", this.checked);
            })

            /*
            绑定所有复选框的点击事件，动态元素使用静态外层元素的jQuery对象的on方法绑定和触发事件，这里绑定事件的方式为事件句柄click
            回调函数中设置全选复选框的选中状态，在属性值中，$('input[name=check]').length表示所有复选框的数量
            $('input[name=check]:checked').length表示被选中的复选框的数量
            两者如果相等，表示所有复选框都被选中，设置全选复选框的选中状态为true
            一旦有一个没被选中，两者就会不相等，设置全选复选框的选中状态为false
            */
            $('#abody').on("click", $('input[name=check]'), function () {
                $('#checkedAll').prop("checked", $('input[name=check]').length == $('input[name=check]:checked').length);
            })
        });

        activityList = function () {
            $.ajax({
                type: "get",
                url: "workbench/clue/getActivitysByCId.do",
                data: {id: '${clue.id}'},//获取请求域中保存的当前clue对象中的id
                dataType: "json",
                success: function (data) {
                    let html = "";
                    $.each(data, function (i, n) {
                        html += '<tr>';
                        html += '<td>' + n.name + '</td>';
                        html += '<td>' + n.startDate + '</td>';
                        html += '<td>' + n.endDate + '</td>';

                        /*
                        所有者不能使用请求域中保存的owner，因为其代表的是线索的所有者，不是市场活动的所有者
                        需要从后台使用市场活动表关联用户表获取市场活动的所有者
                        */
                        html += '<td>' + n.owner + '</td>';
                        html += '<td>';

                        /*
                        为了实现解除关联功能，不能在超链接上使用id用于绑定事件，因为这是在循环体中，如果绑定一个超链接的id
                        则获取的所有超链接都是这个id，会造成id重复，所以需要直接触发事件，使用onclick
                        调用回调函数时需要传递解除关联对应的关系表id，回调函数才会将id传给后端，后端根据id删除记录
                        所以之前返回市场活动列表时返回的id应该是关系表的id，不是市场活动的id
                        如果仍使用市场活动id，为了实现删除关系表的一条记录，还需要提供一个线索id，两个id匹配才能删除
                        但这样做比较麻烦，而使用一个关系表id就能操作删除
                        因为id是一个数据，所以需要拼接在参数上，但由于id是动态生成的元素触发的函数的参数，且id是一个32位的字符串
                        所以在拼接时必须使用引号修饰id的值，说明是一个字符串，否则生成HTML元素后，不能识别一个32位的未定义的标识符
                        只有定义是一个字符串，才能识别该字符串值
                        */
                        html += '<a href="javascript:void(0);" style="text-decoration: none;" onclick="unbund(\'' + n.id + '\')">';

                        //解除关联表示移除关系表中的一条记录，该记录绑定了当前线索和当前市场活动的关联关系
                        html += '<span class="glyphicon glyphicon-remove"></span>解除关联';
                        html += '</a>';
                        html += '</td>';
                        html += '</tr>';
                    })

                    $('#activityBody').html(html);
                }
            })
        }

        unbund = function (id) {
            if (confirm("确定解除关联？")) {
                $.ajax({
                    type: "post",
                    url: "workbench/clue/unbund.do",
                    data: {id: id},
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            //解除成功后，重新渲染关联列表
                            activityList();
                        } else {
                            alert("解除关联失败！");
                        }
                    }
                })
            }
        }
    </script>

</head>
<body>

<!-- 关联市场活动的模态窗口 -->
<div class="modal fade" id="bundModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 80%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title">关联市场活动</h4>
            </div>
            <div class="modal-body">
                <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                    <form class="form-inline" role="form" id="caAddForm">
                        <div class="form-group has-feedback">
                            <%--本搜索框没有点击搜索按钮，直接按回车搜索--%>
                            <input type="text" class="form-control" style="width: 300px;" id="aname"
                                   placeholder="请输入市场活动名称，支持模糊查询">
                            <span class="glyphicon glyphicon-search form-control-feedback"></span>
                        </div>
                    </form>
                </div>
                <table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td><input type="checkbox" id="checkedAll"/></td>
                        <td>名称</td>
                        <td>开始日期</td>
                        <td>结束日期</td>
                        <td>所有者</td>
                        <td></td>
                    </tr>
                    </thead>
                    <tbody id="abody">
                    <%--
                    <tr>
                        <td><input type="checkbox"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>
                    --%>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="bundBtn">关联</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改线索的模态窗口 -->
<div class="modal fade" id="editClueModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 90%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改线索</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">

                    <div class="form-group">
                        <label for="edit-clueOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-clueOwner">
                                <option>zhangsan</option>
                                <option>lisi</option>
                                <option>wangwu</option>
                            </select>
                        </div>
                        <label for="edit-company" class="col-sm-2 control-label">公司<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-company" value="动力节点">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-call" class="col-sm-2 control-label">称呼</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-call">
                                <option></option>
                                <option selected>先生</option>
                                <option>夫人</option>
                                <option>女士</option>
                                <option>博士</option>
                                <option>教授</option>
                            </select>
                        </div>
                        <label for="edit-surname" class="col-sm-2 control-label">姓名<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-surname" value="李四">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-job" class="col-sm-2 control-label">职位</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-job" value="CTO">
                        </div>
                        <label for="edit-email" class="col-sm-2 control-label">邮箱</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-email" value="lisi@bjpowernode.com">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-phone" class="col-sm-2 control-label">公司座机</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-phone" value="010-84846003">
                        </div>
                        <label for="edit-website" class="col-sm-2 control-label">公司网站</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-website"
                                   value="http://www.bjpowernode.com">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-mphone" class="col-sm-2 control-label">手机</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-mphone" value="12345678901">
                        </div>
                        <label for="edit-status" class="col-sm-2 control-label">线索状态</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-status">
                                <option></option>
                                <option>试图联系</option>
                                <option>将来联系</option>
                                <option selected>已联系</option>
                                <option>虚假线索</option>
                                <option>丢失线索</option>
                                <option>未联系</option>
                                <option>需要条件</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-source" class="col-sm-2 control-label">线索来源</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-source">
                                <option></option>
                                <option selected>广告</option>
                                <option>推销电话</option>
                                <option>员工介绍</option>
                                <option>外部介绍</option>
                                <option>在线商场</option>
                                <option>合作伙伴</option>
                                <option>公开媒介</option>
                                <option>销售邮件</option>
                                <option>合作伙伴研讨会</option>
                                <option>内部研讨会</option>
                                <option>交易会</option>
                                <option>web下载</option>
                                <option>web调研</option>
                                <option>聊天</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-describe">这是一条线索的描述信息</textarea>
                        </div>
                    </div>

                    <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative;"></div>

                    <div style="position: relative;top: 15px;">
                        <div class="form-group">
                            <label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-contactSummary">这个线索即将被转换</textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-nextContactTime" value="2017-05-01">
                            </div>
                        </div>
                    </div>

                    <div style="height: 1px; width: 103%; background-color: #D5D5D5; left: -13px; position: relative; top : 10px;"></div>

                    <div style="position: relative;top: 20px;">
                        <div class="form-group">
                            <label for="edit-address" class="col-sm-2 control-label">详细地址</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="1" id="edit-address">北京大兴区大族企业湾</textarea>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" data-dismiss="modal">更新</button>
            </div>
        </div>
    </div>
</div>

<!-- 返回按钮 -->
<div style="position: relative; top: 35px; left: 10px;">
    <a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left"
                                                                         style="font-size: 20px; color: #DDDDDD"></span></a>
</div>

<!-- 大标题 -->
<div style="position: relative; left: 40px; top: -30px;">
    <div class="page-header">
        <%--使用EL表达式直接获取请求转发传过来的线索数据--%>
        <h3>${clue.fullname}${clue.appellation} <small>${clue.company}</small></h3>
    </div>
    <div style="position: relative; height: 50px; width: 500px;  top: -72px; left: 700px;">
        <%--
        转换页面需要线索表中的fullname、appellation、company和owner
        这里直接通过参数的形式将这些数据传给转换页面，因为jsp是Servlet，可以读取出传过来的参数；使用EL表达式从请求域中获取这些数据
        另外还需要一个线索id，提供线索id后转换时才能根据id找到要转换的线索记录进行转换
        使用本方式传参相当于get请求，所以第一个前提是参数中不能有与安全性相关的字段，如密码
        第二个前提是参数使用的字符数不能过长，如果过长，比如带了一些简介信息，超出了浏览器地址栏能接收的字符数，最终只会截取能接收的部分
        每个浏览器的地址栏容纳字符数的上限都不同，从1500到2500个不等
        这里的几个参数都没有敏感信息，且不可能超越地址栏容纳的字符数，所以可以使用get请求
        --%>
        <button type="button" class="btn btn-default"
                onclick="window.location.href='workbench/clue/convert.jsp?id=${clue.id}&fullname=${clue.fullname}&appellation=${clue.appellation}&company=${clue.company}&owner=${clue.owner}';">
            <span class="glyphicon glyphicon-retweet"></span> 转换
        </button>
        <button type="button" class="btn btn-default" data-toggle="modal" data-target="#editClueModal">
            <span class="glyphicon glyphicon-edit"></span> 编辑
        </button>
        <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
    </div>
</div>

<!-- 详细信息 -->
<div style="position: relative; top: -70px;">
    <div style="position: relative; left: 40px; height: 30px;">
        <div style="width: 300px; color: gray;">名称</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;">
            <b>${clue.fullname}${clue.appellation}</b>
        </div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">所有者</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${clue.owner}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 10px;">
        <div style="width: 300px; color: gray;">公司</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${clue.company}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">职位</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${clue.job}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 20px;">
        <div style="width: 300px; color: gray;">邮箱</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${clue.email}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">公司座机</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${clue.phone}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 30px;">
        <div style="width: 300px; color: gray;">公司网站</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${clue.website}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">手机</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${clue.mphone}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 40px;">
        <div style="width: 300px; color: gray;">线索状态</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${clue.state}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">线索来源</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${clue.source}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 50px;">
        <div style="width: 300px; color: gray;">创建者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${clue.createBy}&nbsp;&nbsp;</b><small
                style="font-size: 10px; color: gray;">${clue.createTime}</small></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 60px;">
        <div style="width: 300px; color: gray;">修改者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${clue.editBy}&nbsp;&nbsp;</b><small
                style="font-size: 10px; color: gray;">${clue.editTime}</small></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 70px;">
        <div style="width: 300px; color: gray;">描述</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                ${clue.description}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 80px;">
        <div style="width: 300px; color: gray;">联系纪要</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                ${clue.contactSummary}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 90px;">
        <div style="width: 300px; color: gray;">下次联系时间</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${clue.nextContactTime}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px; "></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 100px;">
        <div style="width: 300px; color: gray;">详细地址</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                ${clue.address}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
</div>

<!-- 备注 -->
<div style="position: relative; top: 40px; left: 40px;">
    <div class="page-header">
        <h4>备注</h4>
    </div>

    <!-- 备注1 -->
    <div class="remarkDiv" style="height: 60px;">
        <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
        <div style="position: relative; top: -40px; left: 40px;">
            <h5>哎呦！</h5>
            <font color="gray">线索</font> <font color="gray">-</font> <b>李四先生-动力节点</b> <small style="color: gray;">
            2017-01-22 10:10:10 由zhangsan</small>
            <div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
                <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit"
                                                                   style="font-size: 20px; color: #E6E6E6;"></span></a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove"
                                                                   style="font-size: 20px; color: #E6E6E6;"></span></a>
            </div>
        </div>
    </div>

    <!-- 备注2 -->
    <div class="remarkDiv" style="height: 60px;">
        <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
        <div style="position: relative; top: -40px; left: 40px;">
            <h5>呵呵！</h5>
            <font color="gray">线索</font> <font color="gray">-</font> <b>李四先生-动力节点</b> <small style="color: gray;">
            2017-01-22 10:20:10 由zhangsan</small>
            <div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
                <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit"
                                                                   style="font-size: 20px; color: #E6E6E6;"></span></a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove"
                                                                   style="font-size: 20px; color: #E6E6E6;"></span></a>
            </div>
        </div>
    </div>

    <div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
        <form role="form" style="position: relative;top: 10px; left: 10px;">
            <textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"
                      placeholder="添加备注..."></textarea>
            <p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
                <button id="cancelBtn" type="button" class="btn btn-default">取消</button>
                <button type="button" class="btn btn-primary">保存</button>
            </p>
        </form>
    </div>
</div>

<!-- 市场活动 -->
<div>
    <div style="position: relative; top: 60px; left: 40px;">
        <div class="page-header">
            <h4>市场活动</h4>
        </div>
        <div style="position: relative;top: 0px;">
            <table class="table table-hover" style="width: 900px;">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td>名称</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                    <td>所有者</td>
                    <td></td>
                </tr>
                </thead>
                <tbody id="activityBody">
                <%--
                <tr>
                    <td>发传单</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                    <td>zhangsan</td>
                    <td><a href="javascript:void(0);" style="text-decoration: none;"><span
                            class="glyphicon glyphicon-remove"></span>解除关联</a></td>
                </tr>
                <tr>
                    <td>发传单</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                    <td>zhangsan</td>
                    <td><a href="javascript:void(0);" style="text-decoration: none;"><span
                            class="glyphicon glyphicon-remove"></span>解除关联</a></td>
                </tr>
                --%>
                </tbody>
            </table>
        </div>

        <div>
            <a href="javascript:void(0);" data-toggle="modal" data-target="#bundModal"
               style="text-decoration: none;"><span class="glyphicon glyphicon-plus"></span>关联市场活动</a>
        </div>
    </div>
</div>


<div style="height: 200px;"></div>
</body>
</html>