<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";

    //获取详情页发送请求传递过来的参数
    /*
    String id = request.getParameter("id");
    String fullname = request.getParameter("fullname");
    String appellation = request.getParameter("appellation");
    String company = request.getParameter("company");
    String owner = request.getParameter("owner");
    */
%>
<html>
<head>
    <base href="<%=path%>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <script type="text/javascript">
        $(function () {
            $(".time").datetimepicker({
                minView: "month",
                language: 'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "bottom-left"
            });

            $("#isCreateTransaction").click(function () {
                if (this.checked) {
                    $("#create-transaction2").show(200);
                } else {
                    $("#create-transaction2").hide(200);
                }
            });

            //为放大镜图标绑定单击事件，打开搜索市场活动的模态窗口
            $('#openModal').click(function () {
                $('#searchActivityModal').modal('show');
            })

            /*
            绑定搜索市场活动的模态窗口的搜索框的键盘按下事件，捕获回车键键值13
            发送Ajax请求根据搜索框提供的市场活动名模糊查询市场活动列表
            */
            $('#aname').keydown(function (event) {
                if (event.keyCode == 13) {
                    $.ajax({
                        type: "get",
                        url: "workbench/clue/getActivitysByName.do",
                        data: {aname: $.trim($('#aname').val())},
                        dataType: "json",
                        success: function (data) {
                            let html = "";
                            $.each(data, function (i, n) {
                                html += '<tr>';

                                /*
                                此处是单选按钮，value设置为市场活动的id，选中某个市场活动表示该交易来自于该市场活动
                                多个单选按钮使用同一个name表示只能多选一，当选中哪个市场活动，value就是该市场活动的id
                                */
                                html += '<tr>';
                                html += '<td><input type="radio" name="activity" value="' + n.id + '"/></td>';

                                /*
                                为遍历的每个市场活动名所在标签绑定id，id值就是当前市场活动id
                                当选中模态窗口中某个市场活动时，通过单选按钮上的value的id可以连接到此处的id获取到市场活动名
                                */
                                html += '<td id="' + n.id + '">' + n.name + '</td>';
                                html += '<td>' + n.startDate + '</td>';
                                html += '<td>' + n.endDate + '</td>';
                                html += '<td>' + n.owner + '</td>';
                                html += '</tr>';
                            })

                            $('#abody').html(html);
                        }
                    })

                    //禁用强制刷新当前页面的默认行为，强制返回false
                    return false;
                }
            })

            //绑定提交按钮的单击事件
            $('#submitBtn').click(function () {
                //获取所有属性name是activity且被选中的单选按钮对应的市场活动
                let $check = $('input[name=activity]:checked');

                //获取选中的市场活动id，因为是单选按钮，只有一个被选中，所以jQuery数组中只有一个DOM，直接调用val可以获取id
                let id = $check.val();

                //获取选中的市场活动名
                let name = $('#' + id).html();

                //将市场活动id和市场活动名填充到交易表单的市场活动源中
                $('#activity').val(name);
                $('#aid').val(id);

                //关闭模态窗口
                $('#searchActivityModal').modal('hide');

                //清空模态窗口中搜索框填写的信息
                $('#aname').val("");

                //清空模态窗口中搜索出来的市场活动列表
                $('#abody').html("");
            })

            //绑定转换按钮的单击事件，进行线索转换
            $('#convertBtn').click(function () {
                /*
                发送传统请求即可，因为是对线索的删除、对客户和联系人的新增，当前页面没有局部刷新的内容
                且由于当前页面属于当前线索，线索被删除后，当前页面应该也已经不存在；使用传统请求最终跳转到线索模块的主页上
                根据“为客户创建交易”的复选框有没有被选中判断是否需要添加一条交易记录
                prop方法只有一个参数表示获取该DOM元素的该属性值，这里如果值是true，创建一笔交易，否则不创建
                */
                if ($('#isCreateTransaction').prop("checked")) {
                    /*
                    传统请求，需要提交线索id和交易表单中的信息给后台，包括金额、交易名、预计成交日期、阶段和市场活动id
                    可以直接将参数拼接到请求地址上，因为没有安全性信息且字符数不会太长
                    但是如果项目在生产阶段时用户提出需求：为客户创建交易不能使用临时交易，需要将交易的所有信息都填充在表单上
                    此时字符数就不够用了，因为其中涉及到了描述、联系纪要这些大文本信息；而且数据过多时，使用链接跳转拼接参数很麻烦
                    使用表单可以方便提交数据，不用拼接数据在请求地址上，在表单元素中使用name属性即可自动将该参数提交，value是填写的值
                    而且表单可以使用post请求提交，避免请求参数出现在地址栏上导致字符数过多的问题
                    */
                    $('#tranForm').submit();//手动提交表单
                } else {
                    /*
                    直接跳转就是传统请求，需要提交线索id给后台根据id获取线索中的信息转换该线索
                    id从线索详情页提交过来的请求参数中获取
                    */
                    location = "workbench/clue/convert.do?cid=${param.id}";
                }
            })
        });
    </script>

</head>
<body>

<!-- 搜索市场活动的模态窗口 -->
<div class="modal fade" id="searchActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 90%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title">搜索市场活动</h4>
            </div>
            <div class="modal-body">
                <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                    <form class="form-inline" role="form">
                        <div class="form-group has-feedback">
                            <input type="text" class="form-control" style="width: 300px;" id="aname"
                                   placeholder="请输入市场活动名称，支持模糊查询">
                            <span class="glyphicon glyphicon-search form-control-feedback"></span>
                        </div>
                    </form>
                </div>
                <table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td></td>
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
                    --%>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="submitBtn">提交</button>
            </div>
        </div>
    </div>
</div>

<div id="title" class="page-header" style="position: relative; left: 20px;">
    <h4>转换线索
        <small>
            <%--直接输出获取到的参数到前端上即可--%>
            <%--<%=fullname%><%=appellation%> - <%=company%>--%>

            <%--
            使用EL表达式中的隐含对象param访问参数名直接取出请求发过来的参数并输出
            EL表达式提供的隐含对象：
            其中xxxScope表示从域中取数据，可以省略；其余的都不能省略
            pageContext页面域对象可以访问jsp中的九大内置对象，如pageContext.request
            另外jsp九大内置对象也有pageContext，也可以调用getxxx()访问其它内置对象，虽然也可以直接使用其它内置对象
            但在EL表达式中只能通过pageContext访问其它的内置对象，不能直接使用其它内置对象
            pageContext可以变成其它域对象，在存值和取值时使用额外的参数，如参数值是pageContext.REQUEST_SCOPE表示转成请求域
            --%>
            <%
                pageContext.setAttribute("p", "公司", pageContext.REQUEST_SCOPE);
                pageContext.getAttribute("p", pageContext.REQUEST_SCOPE);
            %>
            ${param.fullname}${param.appellation} - ${param.company}${requestScope.p}
        </small>
    </h4>
</div>
<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
    <%--新建客户：<%=company%>--%>
    新建客户：${param.company}
</div>
<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
    <%--新建联系人：<%=fullname%><%=appellation%>--%>
    新建联系人：${param.fullname}${param.appellation}
</div>
<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
    <%--如果与潜在客户签合同时签的是意向合同，暂时还没做交易，需要转换线索为真正客户，但本复选框不用打钩，不创建交易--%>
    <input type="checkbox" id="isCreateTransaction"/>
    为客户创建交易
</div>
<div id="create-transaction2"
     style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;">

    <%--
    转换线索，创建一笔临时交易，表单中的数据都是交易表的数据，但交易表不仅这些数据，在交易模块中选中临时新增的交易条目
    进入修改界面可以完善所有数据；使用post请求提交表单，根据表单中元素的name和value生成请求参数，发送到后台创建交易
    --%>
    <form id="tranForm" action="workbench/clue/convert.do" method="post">
        <div class="form-group" style="width: 400px; position: relative; left: 20px;">
            <label for="amountOfMoney">金额</label>
            <input type="text" class="form-control" id="amountOfMoney" name="money">
        </div>
        <div class="form-group" style="width: 400px;position: relative; left: 20px;">
            <label for="tradeName">交易名称</label>
            <input type="text" class="form-control" id="tradeName" name="name">
        </div>
        <div class="form-group" style="width: 400px;position: relative; left: 20px;">
            <label for="expectedClosingDate">预计成交日期</label>
            <input type="text" class="form-control time" id="expectedClosingDate" readonly name="expectedDate">
        </div>
        <div class="form-group" style="width: 400px;position: relative; left: 20px;">
            <label for="stage">阶段</label>
            <select id="stage" class="form-control" name="stage">
                <option></option>

                <%--使用jstl动态生成下拉列表--%>
                <c:forEach items="${stage}" var="s">
                    <option value="${s.value}">${s.text}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group" style="width: 400px;position: relative; left: 20px;">
            <label for="activity">市场活动源&nbsp;&nbsp;
                <a href="javascript:void(0);" id="openModal" style="text-decoration: none;">
                    <span class="glyphicon glyphicon-search"></span>
                </a>
            </label>

            <%--当用户在模态窗口中选中某个市场活动后，本文本框中的内容变成该市场活动名--%>
            <input type="text" class="form-control" id="activity" placeholder="点击上面搜索" readonly>

            <%--使用隐藏域保存选中的市场活动的id，因为创建交易记录时，表中需要的是市场活动id，不是市场活动名--%>
            <input type="hidden" id="aid" name="activityId">

            <%--提交表单除了上述信息外，还需要一个当前线索的id，这里使用隐藏域表示--%>
            <input type="hidden" name="cid" value="${param.id}">

            <%--
            给后台提供一个标记，隐藏域，表示只有在提交表单时才能在后台中获取到该值，标记需要添加交易记录
            如果后台获取到该值是null，则说明不需要添加交易记录
            --%>
            <input type="hidden" name="flag" value="flag">
        </div>
    </form>

</div>

<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
    记录的所有者：<br>
    <%--<b><%=owner%></b>--%>
    <b>${param.owner}</b>
</div>
<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
    <input class="btn btn-primary" type="button" value="转换" id="convertBtn">
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input class="btn btn-default" type="button" value="取消">
</div>
</body>
</html>