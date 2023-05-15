<%@ page import="java.util.List" %>
<%@ page import="com.lzw.crm.settings.domain.DicValue" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.lzw.crm.workbench.domain.Tran" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";

    //获取字典类型为stage的字典值列表
    List<DicValue> stages = (List<DicValue>) application.getAttribute("stage");

    //获取阶段和可能性的映射集合
    Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");

    //获取映射集合中的key集合，即所有阶段
    Set<String> keys = pMap.keySet();

    //定义交易阶段中正常阶段和异常阶段的分界点下标，即成交和丢失的线索的分界点
    int point = 0;

    //遍历阶段字典值列表
    for (int i = 0; i < stages.size(); i++) {
        //获取每个阶段的字典值对象
        DicValue stageObj = stages.get(i);

        //获取字典值对象中的value，即每个阶段的文字描述
        String stage = stageObj.getValue();

        //根据stage取得可能性
        String possibility = pMap.get(stage);

        //当可能性为0时表示到达了分界点，注意避免空指针异常，使用常量调用equals
        if ("0".equals(possibility)) {
            //设置分界点下标为当前下标，即丢失的线索下标，然后可以退出循环
            point = i;
            break;
        }
    }
%>
<html>
<head>
    <base href="<%=path%>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>

    <style type="text/css">
        .mystage {
            font-size: 20px;
            vertical-align: middle;
            cursor: pointer;
        }

        .closingDate {
            font-size: 15px;
            cursor: pointer;
            vertical-align: middle;
        }
    </style>

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


            //阶段提示框
            $(".mystage").popover({
                trigger: 'manual',
                placement: 'bottom',
                html: 'true',
                animation: false
            }).on("mouseenter", function () {
                var _this = this;
                $(this).popover("show");
                $(this).siblings(".popover").on("mouseleave", function () {
                    $(_this).popover('hide');
                });
            }).on("mouseleave", function () {
                var _this = this;
                setTimeout(function () {
                    if (!$(".popover:hover").length) {
                        $(_this).popover("hide")
                    }
                }, 100);
            });

            //页面加载完毕后刷新交易历史列表
            historyList();
        });

        historyList = function () {
            $.ajax({
                type: "get",
                url: "workbench/transaction/getHistorysByTId.do",
                data: {tranId: '${tran.id}'},//参数是当前交易的id，获取该交易对应的交易历史列表
                dataType: "json",
                success: function (data) {
                    let html = "";
                    $.each(data, function (i, n) {
                        html += ('<tr>');
                        html += ('<td>' + n.stage + '</td>');
                        html += ('<td>' + n.money + '</td>');
                        html += ('<td>' + n.possibility + '</td>');
                        html += ('<td>' + n.expectedDate + '</td>');
                        html += ('<td>' + n.createTime + '</td>');
                        html += ('<td>' + n.createBy + '</td>');
                        html += ('</tr>');
                    })

                    $('#thBody').html(html);
                }
            })
        }

        /**
         * 当图标被点击时，可以变更交易阶段，即点击的图标代表的交易阶段变成当前交易阶段，图标会更改成绿色定位或红叉
         * 且其它图标也会受到牵连而变化；发送Ajax请求局部刷新当前页面，且要修改详情页中列出的阶段、可能性、修改时间和修改者
         * @param stage 当前点击的图标表示的阶段的名称
         * @param i 当前点击的图标表示的阶段的下标
         */
        changeStage = function (stage, i) {
            $.ajax({
                type: "post",
                url: "workbench/transaction/changeStage.do",
                data: {
                    /*
                    关于请求参数，首先传递交易id，根据id获取到该交易记录，然后传递变更后的阶段名，即方法的参数stage
                    以更改交易记录中的阶段，还有修改时间和修改者，由后台生成
                    另外由于修改交易记录会伴随着生成一条交易历史记录，所以需要传递创建交易历史记录需要的参数
                    其中id、创建者和创建时间由后台生成，交易id和交易状态前面已经传递，剩下交易金额和预计成交日期需要传递
                    */
                    id: '${tran.id}',
                    stage: stage,
                    money: '${tran.money}',
                    expectedDate: '${tran.expectedDate}'
                },
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        //局部刷新列出的阶段、可能性、修改时间和修改者，后端将这些信息都放在一个交易对象tran中，转成JSON后响应给前端
                        $('#stage').html(data.tran.stage);
                        $('#possibility').html(data.tran.possibility);

                        //JS中拼接空格，直接将“&nbsp;”作为字符串写在html函数的参数中
                        $('#editBy').html(data.tran.editBy + '&nbsp;&nbsp;');
                        $('#editTime').html(data.tran.editTime);

                        //局部刷新交易历史列表
                        historyList();

                        //将所有阶段图标重新判断生成
                        changeIcon(stage, i);
                    } else {
                        alert("变更交易阶段失败！");
                    }
                }
            })
        }

        changeIcon = function (stage, id) {
            //变更后的当前阶段
            let currentStage = stage;

            //获取阶段对应的可能性，无法从请求域中获取，因为当前是局部刷新，请求域中的可能性没有改变；可以获取局部刷新的值
            let currentPossibility = $('#possibility').html();

            //当前阶段在阶段列表中的下标
            let index = id;

            /*
            正常阶段和异常阶段的分界点下标，固定是“7”
            在JS中可以使用Java脚本获取简单类型的变量，即8中基本类型+字符串，但注意要使用引号修饰
            */
            let point = '<%=point%>';

            //使用JS实现阶段图标的局部刷新
            //如果当前阶段的可能性为0，图标是红叉，其它所有异常阶段的图标都是黑叉，所有正常阶段的图标都是黑圈
            if (currentPossibility == "0") {
                //遍历所有正常阶段，生成黑圈
                for (let i = 0; i < point; i++) {
                    /*
                    因为每个图标的id值都是当前下标，所以这里通过下标可以循环获取到所有对应的图标
                    先移除掉之前图标上的样式，注意只是移除class属性的值，其余属性都会保留
                    */
                    $('#' + i).removeClass();

                    //为图标添加新样式，即设置class属性值为圆圈
                    $('#' + i).addClass("glyphicon glyphicon-record mystage");

                    //为新样式赋予颜色为黑色，即设置style中的color
                    $('#' + i).css("color", "#000000");
                }

                /*
                遍历所有异常阶段，从下标是7开始，到阶段列表的长度-1结束，这里使用Java脚本不用使用引号修饰，因为取出的是一个数字
                JS可以识别数字，并不是一个标识符
                */
                for (let i = point; i < <%=stages.size()%>; i++) {
                    //如果遍历到了当前阶段，即下标是index，生成红叉
                    if (i == index) {
                        $('#' + i).removeClass();

                        //设置class属性值为交叉
                        $('#' + i).addClass("glyphicon glyphicon-remove mystage");

                        //为新样式赋予颜色为红色
                        $('#' + i).css("color", "#FF0000");
                    } else {//如果遍历的是非当前阶段，生成黑叉
                        $('#' + i).removeClass();
                        $('#' + i).addClass("glyphicon glyphicon-remove mystage");
                        $('#' + i).css("color", "#000000");
                    }
                }
            } else {//如果当前阶段的可能性不为0，图标是绿色定位，其它正常阶段的图标有绿圈和黑圈，所有异常阶段的图标都是黑叉
                //遍历所有正常阶段
                for (let i = 0; i < point; i++) {
                    //如果遍历到了当前阶段，即下标是index，生成绿色定位
                    if (i == index) {
                        $('#' + i).removeClass();

                        //设置class属性值为定位
                        $('#' + i).addClass("glyphicon glyphicon-map-marker mystage");

                        //为新样式赋予颜色为绿色
                        $('#' + i).css("color", "#90F790");
                    } else if (i < index) {//如果在当前阶段之前，生成绿圈
                        $('#' + i).removeClass();

                        //设置class属性值为圆圈
                        $('#' + i).addClass("glyphicon glyphicon-ok-circle mystage");
                        $('#' + i).css("color", "#90F790");
                    } else {//如果在当前阶段之后，生成黑圈
                        $('#' + i).removeClass();
                        $('#' + i).addClass("glyphicon glyphicon-record mystage");
                        $('#' + i).css("color", "#000000");
                    }
                }

                //遍历所有异常阶段，生成黑叉
                for (let i = point; i < <%=stages.size()%>; i++) {
                    $('#' + i).removeClass();
                    $('#' + i).addClass("glyphicon glyphicon-remove mystage");
                    $('#' + i).css("color", "#000000");
                }
            }
        }
    </script>

</head>
<body>

<!-- 返回按钮 -->
<div style="position: relative; top: 35px; left: 10px;">
    <a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left"
                                                                         style="font-size: 20px; color: #DDDDDD"></span></a>
</div>

<!-- 大标题 -->
<div style="position: relative; left: 40px; top: -30px;">
    <div class="page-header">
        <h3>${tran.customerId} - ${tran.name} <small>￥${tran.money}</small></h3>
    </div>
    <div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
        <button type="button" class="btn btn-default"
                onclick="window.location.href='workbench/transaction/edit.jsp';"><span
                class="glyphicon glyphicon-edit"></span> 编辑
        </button>
        <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
    </div>
</div>

<!-- 阶段状态 -->
<div style="position: relative; left: 40px; top: -50px;">
    阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <%
        //使用Java脚本拼接生成的交易阶段，如果使用JS拼接，某些属性如data-content不支持
        //获取当前交易条目所在的阶段，从请求域中获取保存的当前交易对象
        Tran tran = (Tran) request.getAttribute("tran");
        String currentStage = tran.getStage();

        //获取当前交易条目的阶段对应的可能性
        String currentPossibility = pMap.get(currentStage);

        //如果当前阶段的可能性为0，所有正常阶段的图标都是黑圈，当前阶段的图标为红叉
        if ("0".equals(currentPossibility)) {
            //遍历阶段字典值列表
            for (int i = 0; i < stages.size(); i++) {
                //获取每个阶段的字典值对象
                DicValue stageObj = stages.get(i);

                //获取字典值对象中的value，即每个阶段的文字描述
                String stage = stageObj.getValue();

                //根据stage取得可能性
                String possibility = pMap.get(stage);

                //当可能性为0时表示到达了分界点，都是异常阶段
                if ("0".equals(possibility)) {
                    //因为当前阶段的可能性为0，如果遍历出来的阶段是当前阶段，图标为红叉
                    if (stage.equals(currentStage)) {
    %>

    <%--
    调用方法的实参使用Java脚本时，需要使用引号修饰说明是一个字符串，否则无法识别输出的标识符；class的值表示引用bootstrap中的交叉图标
    然后在style的值中修改颜色为红色即变成红叉；data-content的值使用字典值对象中的text文本信息
    这里为每个图标生成的id都是遍历的当前下标i，即图标的id是从0到8
    --%>
    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #FF0000;"></span>
    -----------

    <%
    } else {//其余情况的图标都是黑叉
    %>

    <%--在style的值中修改颜色为黑色即变成黑叉--%>
    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #000000;"></span>
    -----------

    <%
        }
    } else {//其余情况都是正常阶段，因为当前阶段的可能性为0，所以这些图标都是黑圈
    %>

    <%--class的值表示引用bootstrap中的圆圈图标，然后在style的值中修改颜色为黑色即变成黑圈--%>
    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #000000;"></span>
    -----------

    <%
            }
        }
    } else {//如果当前阶段的可能性不为0，所有异常阶段的图标都是黑叉，而正常阶段的图标有绿圈和黑圈，当前阶段的图标为绿色定位
        //定义当前阶段在阶段字典值列表中的下标
        int index = 0;

        //遍历阶段字典值列表
        for (int i = 0; i < stages.size(); i++) {
            //获取每个阶段的字典值对象
            DicValue stageObj = stages.get(i);

            //获取字典值对象中的value，即每个阶段的文字描述
            String stage = stageObj.getValue();

            //根据stage取得可能性
            String possibility = pMap.get(stage);

            //如果遍历出来的阶段是当前阶段，图标为绿色定位
            if (stage.equals(currentStage)) {
                //更新当前阶段下标为遍历到的下标
                index = i;
                break;
            }
        }

        //遍历阶段字典值列表
        for (int i = 0; i < stages.size(); i++) {
            //获取每个阶段的字典值对象
            DicValue stageObj = stages.get(i);

            //获取字典值对象中的value，即每个阶段的文字描述
            String stage = stageObj.getValue();

            //根据stage取得可能性
            String possibility = pMap.get(stage);

            //当可能性为0时表示到达了分界点，都是异常阶段
            if ("0".equals(possibility)) {
                //因为当前阶段的可能性不为0，所以该分支的图标都是黑叉
    %>

    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #000000;"></span>
    -----------

    <%
    } else {//其余情况都是正常阶段，因为当前阶段的可能性不为0，所以这些图标有绿圈、绿色定位和黑圈
        //因为当前阶段的可能性不为0，如果遍历出来的阶段是当前阶段，即当前下标i就是当前阶段下标index，图标为绿色定位
        if (i == index) {
    %>

    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-map-marker mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #90F790;"></span>
    -----------

    <%
    } else if (i < index) {//当前下标i小于当前阶段下标index，表示是当前阶段之前的已完成的阶段，图标为绿圈
    %>

    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-ok-circle mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #90F790;"></span>
    -----------

    <%
    } else {//其余情况表示当前下标i大于当前阶段下标index，表示是当前阶段之后的未完成的阶段，图标为黑圈
    %>

    <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
          data-toggle="popover" data-placement="bottom"
          data-content="<%=stageObj.getText()%>" style="color: #000000;"></span>
    -----------

    <%
                    }
                }
            }
        }
    %>

    <%--
    <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
          data-content="资质审查" style="color: #90F790;"></span>
    -----------
    <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
          data-content="需求分析" style="color: #90F790;"></span>
    -----------
    <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
          data-content="价值建议" style="color: #90F790;"></span>
    -----------
    <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
          data-content="确定决策者" style="color: #90F790;"></span>
    -----------
    <span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom"
          data-content="提案/报价" style="color: #90F790;"></span>
    -----------
    <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
          data-content="谈判/复审"></span>
    -----------
    <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
          data-content="成交"></span>
    -----------
    <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
          data-content="丢失的线索"></span>
    -----------
    <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
          data-content="因竞争丢失关闭"></span>
    -----------
    --%>
    <span class="closingDate">${tran.expectedDate}</span>
</div>

<!-- 详细信息 -->
<div style="position: relative; top: 0px;">
    <div style="position: relative; left: 40px; height: 30px;">
        <div style="width: 300px; color: gray;">所有者</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.owner}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.money}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 10px;">
        <div style="width: 300px; color: gray;">名称</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId} - ${tran.name}</b>
        </div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.expectedDate}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 20px;">
        <div style="width: 300px; color: gray;">客户名称</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>

        <%--当变更交易阶段时，会局部刷新当前页面，并修改此处显示的阶段、可能性、修改者和修改时间信息--%>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${tran.stage}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 30px;">
        <div style="width: 300px; color: gray;">类型</div>

        <%--对于不存在的值，必须使用空格占位符，如果该处无任何内容，根据前端设计的效应，该处的位置会产生错乱--%>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.type}&nbsp;</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b
                id="possibility">${tran.possibility}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 40px;">
        <div style="width: 300px; color: gray;">来源</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.source}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.activityId}&nbsp;</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 50px;">
        <div style="width: 300px; color: gray;">联系人名称</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.contactsId}</b></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 60px;">
        <div style="width: 300px; color: gray;">创建者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.createBy}&nbsp;&nbsp;</b><small
                style="font-size: 10px; color: gray;">${tran.createTime}</small></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 70px;">
        <div style="width: 300px; color: gray;">修改者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b
                id="editBy">${tran.editBy}&nbsp;&nbsp;</b><small
                style="font-size: 10px; color: gray;" id="editTime">${tran.editTime}</small></div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 80px;">
        <div style="width: 300px; color: gray;">描述</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                ${tran.description}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 90px;">
        <div style="width: 300px; color: gray;">联系纪要</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>
                &nbsp;${tran.contactSummary}
            </b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 100px;">
        <div style="width: 300px; color: gray;">下次联系时间</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.nextContactTime}&nbsp;</b>
        </div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
</div>

<!-- 备注 -->
<div style="position: relative; top: 100px; left: 40px;">
    <div class="page-header">
        <h4>备注</h4>
    </div>

    <!-- 备注1 -->
    <div class="remarkDiv" style="height: 60px;">
        <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
        <div style="position: relative; top: -40px; left: 40px;">
            <h5>哎呦！</h5>
            <font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;">
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
            <font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;">
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

<!--
阶段历史：交易历史列表是局部刷新的，因为页面顶部可以更改交易阶段，每改变一次都会新增一条交易历史记录，此时交易历史列表就要动态刷新
因为在当前页面中，所以是局部刷新
-->
<div>
    <div style="position: relative; top: 100px; left: 40px;">
        <div class="page-header">
            <h4>阶段历史</h4>
        </div>
        <div style="position: relative;top: 0px;">
            <table id="activityTable" class="table table-hover" style="width: 900px;">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td>阶段</td>
                    <td>金额</td>
                    <td>可能性</td>
                    <td>预计成交日期</td>
                    <td>创建时间</td>
                    <td>创建人</td>
                </tr>
                </thead>
                <tbody id="thBody">
                <%--
                <tr>
                    <td>资质审查</td>
                    <td>5,000</td>
                    <td>10</td>
                    <td>2017-02-07</td>
                    <td>2016-10-10 10:10:10</td>
                    <td>zhangsan</td>
                </tr>
                <tr>
                    <td>需求分析</td>
                    <td>5,000</td>
                    <td>20</td>
                    <td>2017-02-07</td>
                    <td>2016-10-20 10:10:10</td>
                    <td>zhangsan</td>
                </tr>
                <tr>
                    <td>谈判/复审</td>
                    <td>5,000</td>
                    <td>90</td>
                    <td>2017-02-07</td>
                    <td>2017-02-09 10:10:10</td>
                    <td>zhangsan</td>
                </tr>
                --%>
                </tbody>
            </table>
        </div>

    </div>
</div>

<div style="height: 200px;"></div>

</body>
</html>