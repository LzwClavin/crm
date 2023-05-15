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

            //鼠标悬停显示备注信息列表的修改和删除图标
            $("#remarkBody").on("mouseover", ".remarkDiv", function () {
                $(this).children("div").children("div").show();
            })

            //鼠标离开隐藏备注信息列表的修改和删除图标
            $("#remarkBody").on("mouseout", ".remarkDiv", function () {
                $(this).children("div").children("div").hide();
            })

            /*
            页面加载完毕后，局部刷新市场活动备注信息，因为当前页面的备注列表涉及到增删改查操作，使用局部刷新效率高，不使用传统请求
            使用一个公共方法加载备注列表，供增删改查通用
            */
            remarkList();

            //绑定保存按钮事件，添加备注信息
            $('#saveBtn').click(function () {
                /*
                添加会动态生成一条备注信息在页面上，但现在不能刷新列表，因为使用了before追加，会产生重复记录
                需要提供的信息有文本域中的备注信息和外键市场活动id，其余的id、创建时间、创建者、是否被修改过都由后台生成
                修改时间和修改者为空；获取请求域中的市场活动id
                */
                $.ajax({
                    type: "post",
                    url: "workbench/activity/saveRemark.do",
                    data: {
                        noteContent: $.trim($('#remark').val()),
                        aid: "${activity.id}"
                    },
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            /*
                            添加成功，在文本域的上方新增一个div，展示新的备注信息，所以后台还需要返回当前添加的整条备注记录
                            以填充到备注信息中，该备注对象的名为r
                            */
                            let html = "";
                            html += '<div id="' + data.r.id + '" class="remarkDiv" style="height: 60px;">';
                            html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">';
                            html += '<div style="position: relative; top: -40px; left: 40px;">';

                            //这里的id格式与查询备注列表保持一致，只是n要改成data.r
                            html += '<h5 id="' + data.r.id + 'nc">' + data.r.noteContent + '</h5>';
                            html += '<font color="gray">市场活动</font>';
                            html += '<font color="gray"> - </font>';
                            html += '<b>${activity.name}</b>';

                            //因为是新记录，所以只有创建时间和创建者，不需要判断是否被修改过
                            html += '<small id="e' + data.r.id + '" style="color: gray;"> ' + data.r.createTime + ' 由 ' + data.r.createBy;
                            html += '</small>';
                            html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">';
                            html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\'' + data.r.id + '\')">';
                            html += '<span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #0000FF;">';
                            html += '</span>';
                            html += '</a>&nbsp;&nbsp;&nbsp;&nbsp;';
                            html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\'' + data.r.id + '\')">';
                            html += '<span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;">';
                            html += '</span>';
                            html += '</a>';
                            html += '</div>';
                            html += '</div>';
                            html += '</div>';

                            //在该div之前追加html中的内容，此时不会产出重复记录，因为仅仅是新增一条记录，没有遍历列表
                            $('#remarkDiv').before(html);

                            //清空文本域信息，如果添加失败不需要清空，留给用户修改后再添加
                            $('#remark').val("");
                        } else {
                            alert("添加备注失败！");
                        }
                    }
                })
            })

            //绑定更新按钮事件，修改备注信息
            $('#updateRemarkBtn').click(function () {
                let id = $('#remarkId').val();

                $.ajax({
                    type: "post",
                    url: "workbench/activity/updateRemark.do",
                    data: {
                        /*
                        传递隐藏域的id和新备注信息，根据id修改备注信息，另外修改者、修改时间和是否被修改过由后台修改
                        其余创建者、创建时间和市场活动id保持默认
                        */
                        id: id,
                        noteContent: $.trim($('#noteContent').val())
                    },
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            /*
                            后端除了返回success，还会返回一个备注对象r，用于修改页面上的单条备注信息，因为现在不能局部刷新备注列表
                            需要修改的内容有备注信息、修改者和修改时间，通过各项信息所在标签对应的id获取到这些信息进行修改
                            这里拼接空格时直接输入空格即可，不能在引号中使用nbsp;，这不会识别为空格，直接识别为nbsp;字符串
                            */
                            $('#' + id + 'nc').html(data.r.noteContent);
                            $('#e' + id).html(' ' + data.r.editTime + ' 由 ' + data.r.editBy);

                            //隐藏模态窗口，显示最新备注列表
                            $('#editRemarkModal').modal('hide');
                        } else {
                            alert("修改备注失败！");
                        }
                    }
                })
            })
        });

        /**
         * 展示市场活动备注列表，本方法的调用时机：
         * 1.页面加载完毕后
         * 2.增删改一条备注信息后
         * 备注列表不是查询表中的所有备注信息，而是查询本页面展示的某个市场活动旗下的所有备注信息，市场活动表是父表，备注表是子表
         * 根据市场活动的主键匹配备注表中的所有外键查询出所有符合条件的备注信息
         */
        function remarkList() {
            $.ajax({
                type: "get",
                url: "workbench/activity/getRemarksByAId.do",
                data: {
                    /*
                    传递市场活动的id，根据id查询所有匹配的备注，这里使用EL表达式获取请求域中保存的activity对象中的id
                    因为在JS中，所以EL表达式需要加引号包裹
                    */
                    aid: '${activity.id}'
                },
                dataType: "json",
                success: function (data) {
                    //后端返回备注列表，一个JSON数组；这里获取的是添加备注文本域所在的div，即备注信息列表下面的div
                    let div = $('#remarkDiv');
                    let html = "";
                    $.each(data, function (i, n) {
                        html += '<div id="' + n.id + '" class="remarkDiv" style="height: 60px;">';
                        html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">';
                        html += '<div style="position: relative; top: -40px; left: 40px;">';

                        /*
                        打开修改备注的模态窗口，模态窗口需要接收原备注的id和备注信息，这里选择不走后台，使用前端提供的备注信息
                        这里的h5标签中引用了备注信息，设置一个id，然后模态窗口可以通过id获取到备注信息并填充到文本域中
                        id设置为当前备注的id，这就可以通过该id定位到当前备注获取到对应的备注信息，但该id与上面div的id重名
                        这里在后面拼接多一个子串用于区分
                        */
                        html += '<h5 id="' + n.id + 'nc">' + n.noteContent + '</h5>';//备注信息
                        html += '<font color="gray">市场活动</font>';
                        html += '<font color="gray"> - </font>';
                        html += '<b>${activity.name}</b>';//对应的市场活动名，不用拼接EL表达式，因为EL表达式在字符串中

                        /*
                        如果本备注没有被修改过，填写创建时间和创建者；否则填写修改时间和修改者；根据editFlag的字段值判断是否被修改过
                        1表示修改过，0表示未修改过；这里使用小括号包裹三目运算符，提升优先级
                        修改备注信息时，需要手动修改页面上显示的信息，这里的时间和用户都需要修改，为该small标签设置id
                        通过id获取到这里的信息并进行修改；与上面一样，id设置为当前备注的id，通过id定位备注信息，但由于重名
                        在前面拼接一个e用以区分
                        */
                        html += '<small id="e' + n.id + '" style="color: gray;"> ' + (n.editFlag == 1 ? n.editTime : n.createTime) + ' 由 ' + (n.editFlag == 1 ? n.editBy : n.createBy);
                        html += '</small>';
                        html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">';

                        /*
                        使用bootstrap框架提供的修改和删除图标，span的class属性值就是引用的图标
                        另外，后缀是search表示搜索图标放大镜；music表示音乐图标等
                        class属性值的引用来源是bootstrap官网->组件下面的图标上的引用文字
                        为了实现修改和删除功能，不能在图标上使用id用于绑定事件，因为这是在循环体中，如果绑定一个图标的id
                        则获取的所有备注信息上的图标都是这个id，会造成id重复，所以需要直接触发事件，使用onclick
                        另外如果使用上面的超链接访问服务器，路径写死了，无法扩展，无法在访问前执行其它行为
                        调用回调函数时需要传递修改或删除对应的备注id，回调函数才会将id传给后端，后端根据id修改或删除记录
                        因为id是一个数据，所以需要拼接在参数上，但由于id是动态生成的元素触发的函数的参数，且id是一个32位的字符串
                        所以在拼接时必须使用引号修饰id的值，说明是一个字符串，否则生成HTML元素后，不能识别一个32位的未定义的标识符
                        只有定义是一个字符串，才能识别该字符串值
                        */
                        html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\'' + n.id + '\')">';
                        html += '<span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #0000FF;">';
                        html += '</span>';
                        html += '</a>&nbsp;&nbsp;&nbsp;&nbsp;';
                        html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\'' + n.id + '\')">';
                        html += '<span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #FF0000;">';
                        html += '</span>';
                        html += '</a>';
                        html += '</div>';
                        html += '</div>';
                        html += '</div>';
                    })

                    //在该div之前追加html中的内容，展现备注信息列表
                    div.before(html);
                }
            })
        }

        function deleteRemark(id) {
            //使用确认框提高安全性
            if (confirm("确定删除当前备注信息？")) {
                $.ajax({
                    type: "post",
                    url: "workbench/activity/deleteRemark.do",
                    data: {id: id},
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            /*
                            删除成功刷新备注列表，但这样刷新是错误的，因为会产生重复的数据，上面调用了before方法
                            当再次局部刷新时会在上次的基础上往后动态追加新内容，只有全局刷新才会展示最新的备注列表
                            */
                            //remarkList();

                            /*
                            可以先在动态备注列表上对每次循环生成的备注信息所在的div设置一个id，id就是当前遍历的备注id
                            当删除一条记录后，通过id获取被删除的备注信息对应的div，然后调用remove方法移除div
                            remove方法表示将jQuery对象中包含的DOM元素及其中的子元素一并移除
                            此时并没有再次调用remarkList方法获取备注列表，所以不会追加新的内容
                            */
                            $('#' + id).remove();
                        } else {
                            alert("删除备注失败！");
                        }
                    }
                })
            }
        }

        function editRemark(id) {
            //将模态窗口隐藏域的id赋值为当前传进来的id
            $('#remarkId').val(id);

            //获取存放备注信息的h5标签，再获取其中的备注信息
            let noteContent = $('#' + id + 'nc').html();

            //设置模态窗口的文本域内容为要修改的备注信息
            $('#noteContent').val(noteContent);

            //显示模态窗口
            $('#editRemarkModal').modal('show');
        }
    </script>

</head>
<body>

<!-- 修改市场活动备注的模态窗口 -->
<div class="modal fade" id="editRemarkModal" role="dialog">
    <%-- 备注的id，隐藏域，用于提交给后台修改备注信息 --%>
    <input type="hidden" id="remarkId">
    <div class="modal-dialog" role="document" style="width: 40%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改备注</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">内容</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="noteContent"></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
            </div>
        </div>
    </div>
</div>

<!-- 返回按钮 -->
<div style="position: relative; top: 35px; left: 10px;">
    <a href="javascript:void(0);" onclick="window.history.back();">
        <span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span>
    </a>
</div>

<!-- 大标题 -->
<div style="position: relative; left: 40px; top: -30px;">
    <div class="page-header">
        <%--使用EL表达式取出请求域中的activity对象中的信息--%>
        <h3>市场活动-${activity.name} <small>${activity.startDate} ~ ${activity.endDate}</small></h3>
    </div>
    <div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
        <button type="button" class="btn btn-default" data-toggle="modal" data-target="#editActivityModal">
            <span class="glyphicon glyphicon-edit"></span> 编辑
        </button>
        <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
    </div>
</div>

<!-- 详细信息 -->
<div style="position: relative; top: -70px;">
    <div style="position: relative; left: 40px; height: 30px;">
        <div style="width: 300px; color: gray;">所有者</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.owner}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">名称</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${activity.name}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>

    <div style="position: relative; left: 40px; height: 30px; top: 10px;">
        <div style="width: 300px; color: gray;">开始日期</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.startDate}</b></div>
        <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">结束日期</div>
        <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${activity.endDate}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 20px;">
        <div style="width: 300px; color: gray;">成本</div>
        <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.cost}</b></div>
        <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 30px;">
        <div style="width: 300px; color: gray;">创建者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;">
            <b>${activity.createBy}&nbsp;&nbsp;</b>
            <small style="font-size: 10px; color: gray;">${activity.createTime}</small>
        </div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 40px;">
        <div style="width: 300px; color: gray;">修改者</div>
        <div style="width: 500px;position: relative; left: 200px; top: -20px;">
            <b>${activity.editBy}&nbsp;&nbsp;</b>
            <small style="font-size: 10px; color: gray;">${activity.editTime}</small>
        </div>
        <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
    <div style="position: relative; left: 40px; height: 30px; top: 50px;">
        <div style="width: 300px; color: gray;">描述</div>
        <div style="width: 630px;position: relative; left: 200px; top: -20px;">
            <b>${activity.description}</b>
        </div>
        <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
    </div>
</div>

<!-- 备注 -->
<div style="position: relative; top: 30px; left: 40px;" id="remarkBody">
    <div class="page-header">
        <h4>备注</h4>
    </div>

    <!-- 备注1 -->
    <%--
    <div class="remarkDiv" style="height: 60px;">
        <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
        <div style="position: relative; top: -40px; left: 40px;">
            <h5>哎呦！</h5>
            <font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;">
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
    --%>

    <!-- 备注2 -->
    <%--
    <div class="remarkDiv" style="height: 60px;">
        <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
        <div style="position: relative; top: -40px; left: 40px;">
            <h5>呵呵！</h5>
            <font color="gray">市场活动</font> <font color="gray">-</font> <b>发传单</b> <small style="color: gray;">
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
    --%>

    <div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
        <form role="form" style="position: relative;top: 10px; left: 10px;">
            <textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"
                      placeholder="添加备注..."></textarea>
            <p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
                <button id="cancelBtn" type="button" class="btn btn-default">取消</button>
                <button type="button" class="btn btn-primary" id="saveBtn">保存</button>
            </p>
        </form>
    </div>
</div>
<div style="height: 200px;"></div>
</body>
</html>