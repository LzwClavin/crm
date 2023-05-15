<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";

    //获取应用域中的阶段和可能性的映射集合，这里需要强转，因为固定返回Object
    Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

    //获取map的所有key，即所有阶段
    Set<String> stages = pMap.keySet();
%>
<html>
<head>
    <base href="<%=path%>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <%--导入自动补全插件，使用插件能使搜索联想列表贴合bootstrap的样式风格--%>
    <script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>

    <script type="text/javascript">
        //将获取到的pMap转成JSON对象
        let json = {
            <%
                //遍历所有阶段，获取到每个阶段
                for(String stage:stages){
                    //通过阶段获取到对应的可能性
                    String possibility = pMap.get(stage);
            %>
            /*
            拼接JSON对象的key和value，要求必须使用双引号修饰key和value，因为jsp会直接输出这些值，使用双引号表示是一个字符串
            如果不使用，无法直接识别这些标识符；循环结束后，即可实现将map转成JSON
            */
            "<%=stage%>": "<%=possibility%>",
            <%
                }
            %>
        };

        $(function () {
            $(".time1").datetimepicker({
                minView: "month",
                language: 'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "top-left"
            });

            $(".time2").datetimepicker({
                minView: "month",
                language: 'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "bottom-left"
            });

            //使用自动补全插件，这里的选择器是使用自动补全的文本框
            $("#create-accountName").typeahead({
                /*
                source回调函数，在文本框中输入某个文字后自动被调用，发送当前文本框的内容给后台模糊查询匹配的信息，实现搜索联想
                第一个参数query是文本框中的文字；第二个参数为一个函数，用于成功函数中处理响应数据，将数据通过该函数实现自动补全
                process无法直接识别返回的JSON数据，可以识别字符串
                */
                source: function (query, process) {
                    $.get(
                        "workbench/transaction/getCustomerNames.do",
                        {"name": query},
                        function (data) {
                            //因为查询的是客户名列表，data是后端响应的字符串集合，后端会将所有客户名封装成一个字符串集合
                            process(data);
                        },
                        "json"
                    );
                },

                /*
                设置延迟加载时间，以毫秒为单位，即超过1.5秒后才会加载自动补全列表，1.5秒是标准规范
                设置延迟加载可以留给用户一些反应时间，不用频繁地连接后台获取数据，当用户停下来1.5秒后，才连接数据库获取数据
                */
                delay: 1500
            });

            //绑定“阶段”下拉列表的change事件，当选中的项被改变时，根据阶段不同自动填写“可能性”文本框内容
            $('#create-transactionStage').change(function () {
                /*
                获取选中的阶段，这里this就是代表当前下拉列表，获取value就是对应被选中的项的value，因为在字典值中value和text是一样的
                所以获取value就可以获取到阶段的文本信息
                */
                let sta = this.value;

                /*
                根据映射关系pMap通过阶段获取对应的可能性，但pMap是Java中封装的数据，无法在JS中解析，需要先将pMap转成JS中的键值对
                即转成JSON对象，然后可以通过对象名.sta获取对应的可能性
                */

                /*
                获取可能性，但获取失败，因为sta是一个变量，不能使用传统json.key取值，要使用json[key]的方式
                let poss = json.sta;
                */

                let poss = json[sta];

                //对可能性文本框赋值
                $('#create-possibility').val(poss);
            })

            //绑定保存按钮的单击事件，提交交易表单给后台保存交易记录
            $('#saveBtn').click(function () {
                $('#tranForm').submit();
            })
        })
    </script>
</head>
<body>

<!-- 查找市场活动 -->
<div class="modal fade" id="findMarketActivity" role="dialog">
    <div class="modal-dialog" role="document" style="width: 80%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title">查找市场活动</h4>
            </div>
            <div class="modal-body">
                <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                    <form class="form-inline" role="form">
                        <div class="form-group has-feedback">
                            <input type="text" class="form-control" style="width: 300px;"
                                   placeholder="请输入市场活动名称，支持模糊查询">
                            <span class="glyphicon glyphicon-search form-control-feedback"></span>
                        </div>
                    </form>
                </div>
                <table id="activityTable3" class="table table-hover"
                       style="width: 900px; position: relative;top: 10px;">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td></td>
                        <td>名称</td>
                        <td>开始日期</td>
                        <td>结束日期</td>
                        <td>所有者</td>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>
                    <tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- 查找联系人 -->
<div class="modal fade" id="findContacts" role="dialog">
    <div class="modal-dialog" role="document" style="width: 80%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title">查找联系人</h4>
            </div>
            <div class="modal-body">
                <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                    <form class="form-inline" role="form">
                        <div class="form-group has-feedback">
                            <input type="text" class="form-control" style="width: 300px;" placeholder="请输入联系人名称，支持模糊查询">
                            <span class="glyphicon glyphicon-search form-control-feedback"></span>
                        </div>
                    </form>
                </div>
                <table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td></td>
                        <td>名称</td>
                        <td>邮箱</td>
                        <td>手机</td>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>李四</td>
                        <td>lisi@bjpowernode.com</td>
                        <td>12345678901</td>
                    </tr>
                    <tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>李四</td>
                        <td>lisi@bjpowernode.com</td>
                        <td>12345678901</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>


<div style="position:  relative; left: 30px;">
    <h3>创建交易</h3>
    <div style="position: relative; top: -40px; left: 70%;">
        <button type="button" class="btn btn-primary" id="saveBtn">保存</button>
        <button type="button" class="btn btn-default" onclick="window.history.back();">取消</button>
    </div>
    <hr style="position: relative; top: -40px;">
</div>

<%--保存后发送post请求提交表单给后台处理添加交易，因为提交的数据包括长文本，容易超出地址栏上限，所以使用表单的post提交，提交所有name--%>
<form class="form-horizontal" role="form" style="position: relative; top: -30px;" id="tranForm"
      action="workbench/transaction/save.do" method="post">
    <div class="form-group">
        <label for="create-transactionOwner" class="col-sm-2 control-label">所有者<span
                style="font-size: 15px; color: red;">*</span></label>
        <div class="col-sm-10" style="width: 300px;">
            <%--表单提交时会提交value，即被选中的项的value，即所有者id，符合表字段的要求--%>
            <select class="form-control" id="create-transactionOwner" name="owner">
                <%--使用jstl遍历获取请求域中的User列表中的用户名，并填充到下拉列表项中--%>
                <c:forEach items="${users}" var="u">
                    <%--
                    为了实现默认选中当前登录的用户，在option标签中使用EL表达式中的三目运算，使用eq运算符比较user.id和u.id
                    如果两者相等，输出selected字符串，输出后变成HTML语法就是默认被选中的属性，否则输出空串，即保持默认
                    另外不能在select标签中使用value="${user.id}"，这样获取的当前登录用户id无法与option中的匹配
                    因为先渲染select再渲染option
                    --%>
                    <option value="${u.id}" ${user.id eq u.id?"selected":""}>${u.name}</option>
                </c:forEach>
            </select>
        </div>
        <label for="create-amountOfMoney" class="col-sm-2 control-label">金额</label>
        <div class="col-sm-10" style="width: 300px;">
            <input type="text" class="form-control" id="create-amountOfMoney" name="money">
        </div>
    </div>

    <div class="form-group">
        <label for="create-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
        <div class="col-sm-10" style="width: 300px;">
            <input type="text" class="form-control" id="create-transactionName" name="name">
        </div>
        <label for="create-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span
                style="font-size: 15px; color: red;">*</span></label>
        <div class="col-sm-10" style="width: 300px;">
            <input type="text" class="form-control time2" id="create-expectedClosingDate" readonly name="expectedDate">
        </div>
    </div>

    <div class="form-group">
        <label for="create-accountName" class="col-sm-2 control-label">客户名称<span
                style="font-size: 15px; color: red;">*</span></label>
        <div class="col-sm-10" style="width: 300px;">
            <%--
            如果使用自动补全，获取的是已经存在的客户；如果手动填写，会创建一个新客户；这里提交的是客户名，但交易表中保存的是客户id
            客户id由后台处理生成，如果是新建的客户，自动生成id；如果是旧客户，通过客户名获取id
            --%>
            <input type="text" class="form-control" id="create-accountName" placeholder="支持自动补全，输入客户不存在则新建"
                   name="customerName">
        </div>
        <label for="create-transactionStage" class="col-sm-2 control-label">阶段<span
                style="font-size: 15px; color: red;">*</span></label>
        <div class="col-sm-10" style="width: 300px;">
            <%--提交的value就是被选中的阶段名，符合表字段要求--%>
            <select class="form-control" id="create-transactionStage" name="stage">
                <option></option>
                <c:forEach items="${stage}" var="s">
                    <option value="${s.value}">${s.text}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-group">
        <label for="create-transactionType" class="col-sm-2 control-label">类型</label>
        <div class="col-sm-10" style="width: 300px;">
            <select class="form-control" id="create-transactionType" name="type">
                <option></option>
                <c:forEach items="${transactionType}" var="t">
                    <option value="${t.value}">${t.text}</option>
                </c:forEach>
            </select>
        </div>
        <label for="create-possibility" class="col-sm-2 control-label">可能性</label>
        <div class="col-sm-10" style="width: 300px;">
            <%--设置文本框为禁用，只允许系统自动生成，用户不能填写，且不能提交给后台--%>
            <input type="text" class="form-control" id="create-possibility" disabled>
        </div>
    </div>

    <div class="form-group">
        <label for="create-clueSource" class="col-sm-2 control-label">来源</label>
        <div class="col-sm-10" style="width: 300px;">
            <select class="form-control" id="create-clueSource" name="source">
                <option></option>
                <c:forEach items="${source}" var="s">
                    <option value="${s.value}">${s.text}</option>
                </c:forEach>
            </select>
        </div>
        <label for="create-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);"
                                                                                           data-toggle="modal"
                                                                                           data-target="#findMarketActivity"><span
                class="glyphicon glyphicon-search"></span></a></label>
        <div class="col-sm-10" style="width: 300px;">
            <input type="text" class="form-control" id="create-activitySrc" value="产品发布会">

            <%--创建交易需要提交的是市场活动id，使用隐藏域保存id--%>
            <input type="hidden" value="e0ff1bb1d0a64ab2aacc53dfaa3ed17f" name="activityId">
        </div>
    </div>

    <div class="form-group">
        <label for="create-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);"
                                                                                            data-toggle="modal"
                                                                                            data-target="#findContacts"><span
                class="glyphicon glyphicon-search"></span></a></label>
        <div class="col-sm-10" style="width: 300px;">
            <input type="text" class="form-control" id="create-contactsName" value="马云">

            <%--创建交易需要提交的是联系人id，使用隐藏域保存id--%>
            <input type="hidden" value="9859c26463c34e5ebeef1e628822bf5f" name="contactsId">
        </div>
    </div>

    <div class="form-group">
        <label for="create-describe" class="col-sm-2 control-label">描述</label>
        <div class="col-sm-10" style="width: 70%;">
            <%--文本域提交的value就是填写的内容--%>
            <textarea class="form-control" rows="3" id="create-describe" name="description"></textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
        <div class="col-sm-10" style="width: 70%;">
            <textarea class="form-control" rows="3" id="create-contactSummary" name="contactSummary"></textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
        <div class="col-sm-10" style="width: 300px;">
            <input type="text" class="form-control time1" id="create-nextContactTime" readonly name="nextContactTime">
        </div>
    </div>

</form>
</body>
</html>