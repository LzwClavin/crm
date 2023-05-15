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
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

    <%--导入日历控件中的js必须放在导入jQuery和Bootstrap的下面，因为日历js里面使用到了jQuery和Bootstrap的语法--%>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <%--导入bootstrap的分页插件，分页插件必须放在导入jQuery和Bootstrap的下面，因为里面使用到了jQuery和Bootstrap的语法--%>
    <%--分页插件的语言包，没有中文的语言包，可以修改其中的属性值为中文--%>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <link href="jquery/bs_pagination/jquery.bs_pagination.min.css" type="text/css" rel="stylesheet"/>

    <script type="text/javascript">

        $(function () {
            //使用日历控件：类选择器，因为页面中有多处都需要引用该控件；datetimepicker是日历控件中的方法
            $(".time").datetimepicker({
                minView: "month",//设置只显示到月份
                language: 'zh-CN',//中文语言包，即上面引入的bootstrap-datetimepicker.zh-CN.js
                format: 'yyyy-mm-dd',//格式化日期为年月日
                autoclose: true,//使用关闭日历控件的按钮
                todayBtn: true,//使用显示今天的按钮
                pickerPosition: "bottom-left"//设置显示日历控件的位置
            });

            //绑定创建按钮的单击事件，打开添加的模态窗口
            $('#addBtn').click(function () {
                /*
                在打开模态窗口之前，发送Ajax请求获取用户表数据，为模态窗口中的所有者下拉列表初始化用户表的用户名
                然后打开模态窗口可以从下拉列表中选择一个用户；实际开发中可能除了获取用户名还需要用户的其它信息，所以接收整个用户对象
                */
                $.ajax({
                    type: "get",
                    url: "workbench/activity/getUsers.do",
                    dataType: "json",
                    success: function (data) {
                        //后端响应的是一个用户列表List<User>，包含了所有用户对象，对应的JSON是[{user1},{user2}...]
                        let select = $('#create-marketActivityOwner');
                        select.empty();

                        /*
                        遍历JSON数组，调用$.each函数，第一个参数是数组，第二个参数是回调函数，回调函数的第一个参数是数组下标i
                        第二个参数是数组元素n；循环体中对下拉列表select追加子项，项中的值就是n代表的用户对象中的name
                        而项的value是用户对象的id
                        注意要在循环体外面先将下拉列表中的项清空，否则第二次发送请求时会在第一次的基础上继续往后追加，产生重复的数据
                        */
                        $.each(data, function (i, n) {
                            select.append("<option value='" + n.id + "'>" + n.name + "</option>");
                        })

                        /*
                        将当前登录的用户的用户名设置为下拉列表默认被选中的项
                        将select的value设置成某个option的value即可实现默认选中
                        而这里的option的value就是session域中的User对象的id
                        注意在JS中不能直接使用EL表达式，必须使用引号修饰，即必须是一个字符串
                        */
                        select.val("${user.id}");
                    }
                })

                /*
                通过获取模态窗口的id获取操作模态窗口的jQuery对象，调用modal方法，为该方法传递参数，传递show表示展示模态窗口
                传递hide表示隐藏模态窗口
                */
                $('#createActivityModal').modal('show');
            })

            //绑定保存按钮的单击事件
            $('#saveBtn').click(function () {
                //在关闭模态窗口之前，发送Ajax请求添加市场活动记录
                $.ajax({
                    type: "post",
                    url: "workbench/activity/save.do",
                    data: {
                        //select的value就是被选中的option的value，即用户id，因为表中需要使用的就是用户id，这是一个外键
                        owner: $('#create-marketActivityOwner').val(),
                        name: $.trim($('#create-marketActivityName').val()),//去掉文本框的前后空格
                        startDate: $('#create-startTime').val(),
                        endDate: $('#create-endTime').val(),
                        cost: $.trim($('#create-cost').val()),
                        description: $.trim($('#create-describe').val())//文本域的value就是文本域内容
                    },//传递记录的参数
                    dataType: "json",
                    success: function (data) {
                        //后端响应是否添加成功，即success属性
                        if (data.success) {
                            /*
                            调用list函数，参数使用bootstrap分页插件提供的API，activityPage是本页面中引用分页插件的div
                            第一个参数表示停留在列表的当前页，第二个参数表示维持当前已经设置好的每页记录数
                            因为如果用户在页面上设置了每页显示的记录数，如果不使用该API控制，除了分页查询外
                            其余的增、删、改、条件查询、页面刷新都会恢复原来的每页显示的记录数，所以应该维持新的每页显示的记录数
                            但由于现在是添加操作，要显示列表的第1页，因为添加后新记录显示在顶部，所以回到第1页可查看新添加的记录
                            关闭模态窗口后会显示新的市场活动列表
                            */
                            /*
                            list($("#activityPage").bs_pagination('getOption', 'currentPage'),
                             $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            */
                            list(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


                            //隐藏模态窗口，因为添加失败不用关闭模态窗口，留给用户重新填写信息并添加，所以本语句放在if域中
                            $('#createActivityModal').modal('hide');

                            /*
                            关闭模态窗口后，将模态窗口中填写的值清空，防止下次打开时保留上次的值
                            先获取模态窗口所在的表单，然后调用submit方法可以在这里提交表单，但调用reset方法并不能清空表单
                            虽然jQuery对象中的reset方法不能重置表单，但原生的JS中的reset方法可以重置表单
                            所以要先将jQuery对象转成原生JS对象，即DOM对象，因为jQuery对象相当于DOM对象的数组
                            所以从数组中取出唯一元素就是该表单的DOM对象，使用jQuery对象[0]或jQuery对象.get(0)都可以获取
                            另外DOM对象转jQuery对象执行$(DOM对象)即可
                            */
                            $('#activityAddForm')[0].reset();
                        } else {
                            alert("添加市场活动失败！");
                        }
                    }
                })
            })

            //页面加载完毕后自动调用list函数，默认展开列表的第1页，每页显示2条记录
            list(1, 2);

            /*
            绑定查询按钮的单击事件，条件查询市场活动列表，调用list函数，参数使用bootstrap分页插件提供的API
            展开查询结果列表的第1页，使用设定好的每页展示的记录数
            每次点击查询按钮时，应该先将搜索框中的信息保存起来，保存到隐藏域中
            */
            $('#searchBtn').click(function () {
                $('#hidden-name').val($.trim($('#search-name').val()));
                $('#hidden-owner').val($.trim($('#search-owner').val()));
                $('#hidden-startDate').val($('#startTime').val());
                $('#hidden-endDate').val($('#endTime').val());
                list(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
            })

            //绑定全选复选框的点击事件
            $('#checkedAll').click(function () {
                /*
                该选择器表示选择input标签中name属性的属性值为check的所有标签，即选择所有的当前页的复选框
                往后调用prop函数表示对这些复选框设置属性，第一个参数是属性名，第二个参数是属性值
                属性名是checked，属性值中的this表示全选复选框的DOM对象，this.checked表示选中为true，未选中为false
                即设置所有复选框的选中状态为全选复选框的选中状态，同为true或同为false
                */
                $('input[name=check]').prop("checked", this.checked);
            })

            /*
            绑定所有复选框的点击事件，不能使用$('input[name=check]').click(回调函数)，因为动态生成的页面元素不能用于绑定事件
            动态元素要使用on方法绑定和触发事件，执行“动态元素的静态外层元素的jQuery对象.on(绑定事件的方式,动态元素的jQuery对象,回调函数)”
            其中绑定事件的方式即字符串形式的事件句柄，如click
            回调函数中设置全选复选框的选中状态，在属性值中，$('input[name=check]')即所有复选框的jQuery对象或jQuery数组
            里面存储了所有name为check的复选框，往后访问length即表示所有复选框的数量
            $('input[name=check]:checked')即所有复选框的checked属性为true的jQuery对象或jQuery数组
            往后访问length即表示这些复选框的数量；两者如果相等，表示所有复选框都被选中，设置全选复选框的选中状态为true
            一旦有一个没被选中，两者就会不相等，设置全选复选框的选中状态为false
            */
            $('#activityBody').on("click", $('input[name=check]'), function () {
                $('#checkedAll').prop("checked", $('input[name=check]').length == $('input[name=check]:checked').length);
            })

            //绑定删除按钮的单击事件，删除市场活动
            $('#deleteBtn').click(function () {
                let check = $('input[name=check]:checked');

                /*
                如果所有被选中的复选框的jQuery对象的长度为0，取反为true，表示没有任何复选框被选中，提示用户选中
                否则表示用户选中了一个或多个复选框，可以执行删除操作，发送Ajax请求删除
                */
                if (!check.length) {
                    alert("请选择需要删除的市场活动！");
                } else {
                    //删除记录是危险动作，会丢失数据库的数据，需要搭配确认框使用，如果用户点击确认，返回true，执行删除行为
                    if (confirm("确定删除选中的市场活动？")) {
                        /*
                        请求参数无法使用JSON格式，因为如果多选复选框，传递过去的都是市场活动的id，根据id批量删除
                        因为JSON对象要求属性名不能一样，多个id无法区分多个属性；所以必须使用传统形式传递name和value，允许name重复
                        即Ajax请求中的data不使用JSON格式，使用字符串格式，是拼接出来的一个字符串
                        首先需要将jQuery对象数组check中的每一个DOM对象遍历出来，然后获取每个DOM对象的value
                        即获取到了需要删除的市场活动的id；然后拼接在请求参数中，参数名统一是id，每遍历一次就添加一个请求参数
                        */
                        let data = "";
                        for (var i = 0; i < check.length; i++) {
                            data += "id=" + check[i].value;

                            //如果当前元素不是最后一个，需要在后面追加“&”符号，作为请求参数中参数之间的分隔符
                            if (i < check.length - 1) {
                                data += '&';
                            }
                        }

                        $.ajax({
                            type: "post",
                            url: "workbench/activity/delete.do",
                            data: data,
                            dataType: "json",
                            success: function (data) {
                                //后端响应是否删除成功，即success属性
                                if (data.success) {
                                    /*
                                    删除后刷新市场活动列表，调用list函数，参数使用bootstrap分页插件提供的API
                                    对于删除操作，要显示列表的第1页，因为如果批量删除，删除本页所有市场活动，不能再停留在本页
                                    */
                                    list(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                                } else {
                                    alert("删除市场活动失败！");
                                }
                            }
                        })
                    }
                }
            })

            //绑定修改按钮的单击事件，打开修改的模态窗口
            $('#editBtn').click(function () {
                //使用$前缀表示jQuery对象，与DOM对象区分；有一些前端框架或插件只支持DOM对象，不支持jQuery对象，所以需要区分
                let $check = $('input[name=check]:checked');

                /*
                如果所有被选中的复选框的jQuery对象的长度为0，取反为true，表示没有任何复选框被选中，提示用户选中
                如果用户选中了多个复选框，需要提示用户不能多选，因为不能同时修改多条记录
                如果用户仅选中一个复选框，可以执行修改操作，发送Ajax请求修改
                */
                if (!$check.length) {
                    alert("请选择需要修改的市场活动！");
                } else if ($check.length > 1) {
                    alert("每次只能修改一条记录，请重新选择！");
                } else {
                    //获取复选框的value，即需要修改的市场活动的id，直接使用jQuery对象获取即可，因为数组中只有一个DOM对象
                    let id = $check.val();

                    /*
                    在打开模态窗口之前，发送Ajax请求获取用户表数据，为模态窗口中的所有者下拉列表初始化用户表的用户名
                    然后打开模态窗口可以从下拉列表中选择一个用户；实际开发中可能除了获取用户名还需要用户的其它信息
                    所以接收整个用户对象；另外还需要获取当前修改的记录的信息，即市场活动表数据，并将数据填写到各自的文本框上
                    */
                    $.ajax({
                        type: "get",
                        url: "workbench/activity/getUsersActivity.do",
                        data: {
                            id: id//传递id给后端，根据id查单条
                        },
                        dataType: "json",
                        success: function (data) {
                            //后端响应的是一个用户列表，包含了所有用户对象，是一个JSON数组；以及一个市场活动对象，是一个JSON对象
                            let select = $('#edit-marketActivityOwner');
                            select.empty();

                            /*
                            遍历用户列表，追加子项；另外修改操作不需要设置默认显示的所有者为当前登录用户
                            因为当前需要修改的市场活动记录中有自身的所有者，使用的是表中外键指向的真正的所有者
                            */
                            $.each(data.users, function (i, n) {
                                select.append("<option value='" + n.id + "'>" + n.name + "</option>");
                            })

                            //获取市场活动对象，并对模态窗口中的文本框内容填充数据
                            let activity = data.activity;

                            /*
                            返回的市场活动对象中的owner属性即对应用户记录中的id值，这里设置下拉列表的value是该值
                            匹配option项中对应的用户id，则对应的所有者就会被自动选中
                            */
                            select.val(activity.owner);
                            $('#edit-id').val(activity.id);
                            $('#edit-marketActivityName').val(activity.name);
                            $('#edit-startTime').val(activity.startDate);
                            $('#edit-endTime').val(activity.endDate);
                            $('#edit-cost').val(activity.cost);
                            $('#edit-describe').val(activity.description);
                        }
                    })

                    $('#editActivityModal').modal('show');
                }
            })

            //绑定更新按钮的单击事件
            $('#updateBtn').click(function () {
                //在关闭模态窗口之前，发送Ajax请求修改市场活动记录
                $.ajax({
                    type: "post",
                    url: "workbench/activity/update.do",
                    data: {
                        //提交隐藏域id，根据id判断需要修改的项
                        id: $('#edit-id').val(),

                        //select的value就是被选中的option的value，即用户id，因为表中需要使用的就是用户id，这是一个外键
                        owner: $('#edit-marketActivityOwner').val(),
                        name: $.trim($('#edit-marketActivityName').val()),//去掉文本框的前后空格
                        startDate: $('#edit-startTime').val(),
                        endDate: $('#edit-endTime').val(),
                        cost: $.trim($('#edit-cost').val()),
                        description: $.trim($('#edit-describe').val())//文本域的value就是文本域内容
                    },//传递记录的参数
                    dataType: "json",
                    success: function (data) {
                        //后端响应是否修改成功，即success属性
                        if (data.success) {
                            /*
                            调用list函数，参数使用bootstrap分页插件提供的API
                            对于修改操作，要停留在列表的当前页，方便查看修改后的记录
                            关闭模态窗口后会显示新的市场活动列表
                            */
                            list($("#activityPage").bs_pagination('getOption', 'currentPage'),
                                $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

                            //隐藏模态窗口，因为修改失败不用关闭模态窗口，留给用户重新填写信息并修改，所以本语句放在if域中
                            $('#editActivityModal').modal('hide');
                        } else {
                            alert("修改市场活动失败！");
                        }
                    }
                })
            })
        });

        /**
         * 发送Ajax请求获取市场活动列表，并局部刷新展现到页面的主体中
         * @param pageNo 分页查询中的当前页的页码
         * @param pageSize 每页显示的记录条数
         * 对于所有的关系型数据库，前端分页相关操作的基础组件就是pageNo和pageSize，两个条件缺一不可
         * 另外还有总记录条数、总页数等信息，这些信息都可以通过两个必要的组件计算出来
         * 调用本方法刷新市场活动列表的时机：
         * 1.点击左侧菜单的市场活动项
         * 2.创建、修改市场活动的模态窗口执行了保存或更新操作且窗口被关闭之前
         * 3.删除某条市场活动之后
         * 4.进行条件查询时，如果不填写任何查询条件，表示查询所有；如果填写，就使用条件查询更新市场活动列表
         * 5.点击分页组件中的页码、首页、尾页、上一页、下一页等按钮时，每次点击都是刷新指定页码的数据
         * 6.更改分页组件中的每页显示的记录条数，刷新当前页码的数据
         */
        function list(pageNo, pageSize) {
            /*
            将全选复选框取消全选，避免之前全选了，新增一项后由于新记录没有被选中，但全选复选框还是被选中的情况
            还有当删除完本页的所有记录后，此时本页无记录，也需要将全选复选框取消全选
            而新增和删除这两个操作都会调用本方法刷新市场活动列表，所以在这里可以统一将全选复选框取消全选
            而市场活动列表的所有复选框，在加载列表时默认是不选中的，所以当跳转页面或条件查询时不会出现已选中的复选框再次被选中的情况
            在对全选复选框取消选中后，当跳转页面或条件查询时也不会再出现已选中的全选复选框再次被选中的情况
            */
            $('#checkedAll').prop("checked", false);

            /*
            每次查询数据前，先将隐藏域中保存的信息取出来，重新放进搜索框中，这样能防止当点击分页查询时，条件查询的搜索框中如果有数据
            即使没有点击查询按钮，也会连这些数据提交给后端进行条件查询；使用了隐藏域后，分页查询提交数据前获取的是隐藏域中的条件查询数据
            即上一次点击查询按钮时保存到隐藏域中的数据，即上一次条件查询的数据；这样就能实现条件查询是和查询按钮绑定在一起的
            不会影响到分页查询，只有点击了查询按钮才会进行条件查询
            */
            $('#search-name').val($.trim($('#hidden-name').val()));
            $('#search-owner').val($.trim($('#hidden-owner').val()));
            $('#startTime').val($('#hidden-startDate').val());
            $('#endTime').val($('#hidden-endDate').val());

            $.ajax({
                type: "get",
                url: "workbench/activity/getActivitys.do",
                data: {
                    //将函数的两个参数提交给后端，后端根据每页记录数将整个列表分成多个子列表，根据页码定位到需要查询的子列表
                    pageNo: pageNo,
                    pageSize: pageSize,

                    /*
                    传递条件查询使用到的参数给后端，后端根据条件查询指定记录；如查询所有者可以模糊查询
                    条件查询是需要点击查询按钮才会触发的，不是自动展示的，这里是将条件查询与分页查询合并成同一个方法
                    如果不是条件查询，或者点击查询按钮但不填写任何查询条件，以下4项数据都是空字符串，此时不选择条件查询即可
                    或者填写了部分查询条件，使用MyBatis的动态SQL可以快速实现以上功能
                    */
                    name: $.trim($('#search-name').val()),
                    owner: $.trim($('#search-owner').val()),
                    startDate: $('#startTime').val(),
                    endDate: $('#endTime').val(),
                },
                dataType: "json",
                success: function (data) {
                    /*
                    后端响应的是一个市场活动列表List<Activity>，也是一个JSON数组，包含了所有市场活动对象
                    另外还会响应分页插件需要的查询结果总记录数total，所以总结果是一个JSON对象，对象的第一个属性是total
                    第二个属性是市场活动列表JSON数组list
                    */
                    let body = $('#activityBody');

                    //这里使用拼串的方式生成动态列表，因为使用append方法会导致逐行追加标签时出现css格式不正确的情况
                    let html = "";

                    //循环体中对tbody追加行列，表格中的值就是n代表的市场活动对象中的name、startDate和endDate
                    $.each(data.list, function (i, n) {
                        html += '<tr class="active">';

                        /*
                        通过复选框value获取修改或删除的项，采用市场活动对象的id表示value
                        通过统一的name可以获取当前页的所有复选框，用于全选和取消全选操作
                        */
                        html += '<td><input type="checkbox" name="check" value="' + n.id + '"/></td>';

                        /*
                        使用传统请求的方式，先跳转到后台，获取到数据后保存到请求域中，再转发给detail.jsp取出数据渲染页面
                        因为现在是跳转到一个新页面，没有涉及到页面的局部刷新问题，可以使用传统请求跳转
                        而且detail.jsp中还有市场活动的备注信息，里面关联了市场活动信息，使用EL表达式取数据很方便
                        跳转到后台需要传递市场活动的id过去，因为后台需要根据id获取到对应的市场活动记录，然后将该记录保存到域中
                        跳转到detail.jsp并展示该记录中的数据；里面拼接的单引号不需要转义，因为外层的已经被转义
                        里面的单引号可以与外层的匹配组成一个子串
                        */
                        html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id=' + n.id + '\';">' + n.name + '</a></td>';

                        /*
                        因为原owner字段存储的是用户表的UUID，不能直接显示在前端，需要先在后端查询时通过owner找到用户表对应的name
                        并将name赋值给该owner
                        */
                        html += '<td>' + n.owner + '</td>';
                        html += '<td>' + n.startDate + '</td>';
                        html += '<td>' + n.endDate + '</td>';
                        html += '</tr>';
                    })

                    body.html(html);

                    //计算总页数，使用总记录数除以每页显示的记录数，但有可能有余数，此时需要多加一页
                    let simpleTotalPages = data.total / pageSize;

                    //注意需要先将simpleTotalPages转为整数再加1，因为JS除法如果不整除会算出小数，不是整数
                    let totalPages = data.total % pageSize == 0 ? simpleTotalPages : parseInt(simpleTotalPages) + 1;

                    /*
                    响应数据处理完毕后，结合分页插件，对前端展现分页查询效果；bs_pagination是分页插件中的方法
                    这里使用需要引用分页插件的div调用该方法
                    */
                    $("#activityPage").bs_pagination({
                        currentPage: pageNo,//当前页的页码，即外层list方法中的参数pageNo
                        rowsPerPage: pageSize,//每页显示的记录条数，即外层list方法中的参数pageSize
                        maxRowsPerPage: 10,//每页最多显示的记录条数
                        totalPages: totalPages,//总页数
                        totalRows: data.total,//总记录条数，即获取到的total
                        visiblePageLinks: 2,//显示几个卡片，卡片就是分页插件中的页码，点击某个卡片可以跳转到某页，2表示显示1到2页
                        showGoToPage: true,//显示跳转到第几页按钮
                        showRowsPerPage: true,//显示每页显示的记录数按钮

                        /*
                        显示分页信息，格式是“1-2总5记录(页1/3)”
                        表示当前页显示的是第1到第2条记录，总共5条记录，当前页是第1页，共3页
                        */
                        showRowsInfo: true,
                        showRowsDefaultInfo: true,

                        /*
                        该回调函数在用户点击分页组件中的某个按钮时触发，第一个参数是事件对象，第二个参数是本对象data，
                        即外层包裹的对象；触发回调函数，函数体中调用list方法，传递data的当前页码和每页记录数
                        */
                        onChangePage: function (event, data) {
                            list(data.currentPage, data.rowsPerPage);
                        }
                    });
                }
            })
        }

    </script>
</head>
<body>

<%--使用隐藏域保存条件查询中搜索框的4项数据--%>
<input type="hidden" id="hidden-name">
<input type="hidden" id="hidden-owner">
<input type="hidden" id="hidden-startDate">
<input type="hidden" id="hidden-endDate">

<!-- 创建市场活动的模态窗口 -->
<div class="modal fade" id="createActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
            </div>
            <div class="modal-body">

                <form id="activityAddForm" class="form-horizontal" role="form">

                    <div class="form-group">
                        <label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <%--
                            添加的参数都带“create-”前缀，因为本页面有多处使用到相同的字段信息，这是一种规范
                            规定添加使用“create-”开头，修改使用“edit-”开头，查询使用“search-”开头等，区分不同的id
                            --%>
                            <select class="form-control" id="create-marketActivityOwner"></select>
                        </div>
                        <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-marketActivityName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">

                            <%--class设置time，使用日历控件；为了防止用户乱填日期，设置文本框为只读，只能选日期不能手动填--%>
                            <input type="text" class="form-control time" id="create-startTime" readonly>
                        </div>
                        <label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="create-endTime" readonly>
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="create-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-cost">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-describe"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>

                <%--
                添加操作，发送Ajax请求，因为关闭模态窗口后，需要局部刷新市场活动列表
                data-dismiss="modal"表示关闭模态窗口，因为同时涉及到Ajax请求，所以不能将按钮的行为写死在关闭模态窗口上
                应该使用js实现，单击事件触发后，使用js操作按钮的多个行为
                --%>
                <%--<button type="button" class="btn btn-primary" data-dismiss="modal">保存</button>--%>
                <button type="button" class="btn btn-primary" id="saveBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改市场活动的模态窗口 -->
<div class="modal fade" id="editActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form">

                    <%--
                    当点击更新按钮，需要提交所有更新的数据，另外还需要提交一个id，后台根据id才能找到某条记录并修改其中的数据
                    id设置成隐藏域是因为对用户是不可见的
                    --%>
                    <input type="hidden" id="edit-id">

                    <div class="form-group">
                        <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-marketActivityOwner"></select>
                        </div>
                        <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-marketActivityName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="edit-startTime" readonly>
                        </div>
                        <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control time" id="edit-endTime" readonly>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-cost">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <%--对文本域取值和赋值时调用val函数，因为文本框中间的内容就是value，不能使用html或text函数--%>
                            <textarea class="form-control" rows="3" id="edit-describe"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <%--<button type="button" class="btn btn-primary" data-dismiss="modal">更新</button>--%>
                <button type="button" class="btn btn-primary" id="updateBtn">更新</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>市场活动列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="search-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="search-owner">
                    </div>
                </div>


                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">开始日期</div>
                        <input class="form-control time" type="text" id="startTime" readonly/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">结束日期</div>
                        <input class="form-control time" type="text" id="endTime" readonly>
                    </div>
                </div>

                <button type="button" id="searchBtn" class="btn btn-default">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">

                <%--
                data-toggle="modal"：表示点击按钮后打开一个模态窗口
                data-target="#createActivityModal"：表示打开的模态窗口对应的div的id，使用id选择器“#id”定位
                使用属性名和属性值设置模态窗口；但这样做有缺憾，如果点击按钮后除了打开模态窗口，还需要执行其它行为
                如需要在打开模态窗口之前弹框，现在无法实现，即无法扩充按钮的功能；所以实际开发中，模态窗口不会写死在按钮上
                应该使用js代码操作，触发点击按钮事件后在一个函数中对按钮的属性进行操作，这样就可以在操作模态窗口的前后执行一些其它行为
                --%>
                <%--
                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createActivityModal">
                --%>
                <button type="button" class="btn btn-primary" id="addBtn">
                    <span class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <%--
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#editActivityModal">
                --%>
                <button type="button" class="btn btn-default" id="editBtn">
                    <span class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button type="button" class="btn btn-danger" id="deleteBtn">
                    <span class="glyphicon glyphicon-minus"></span> 删除
                </button>
            </div>

        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">

                    <%--全选/取消全选复选框--%>
                    <td><input type="checkbox" id="checkedAll"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="activityBody">
                <%--
                <tr class="active">
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                    <td>zhangsan</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                </tr>
                <tr class="active">
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                    <td>zhangsan</td>
                    <td>2020-10-10</td>
                    <td>2020-10-20</td>
                </tr>
                --%>
                </tbody>
            </table>
        </div>

        <div style="height: 50px; position: relative;top: 30px;">
            <%--对该div引用分页插件--%>
            <div id="activityPage"></div>

            <%--
            <div>
                <button type="button" class="btn btn-default" style="cursor: default;">共<b>50</b>条记录</button>
            </div>
            <div class="btn-group" style="position: relative;top: -34px; left: 110px;">
                <button type="button" class="btn btn-default" style="cursor: default;">显示</button>
                <div class="btn-group">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                        10
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">20</a></li>
                        <li><a href="#">30</a></li>
                    </ul>
                </div>
                <button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
            </div>
            <div style="position: relative;top: -88px; left: 285px;">
                <nav>
                    <ul class="pagination">
                        <li class="disabled"><a href="#">首页</a></li>
                        <li class="disabled"><a href="#">上一页</a></li>
                        <li class="active"><a href="#">1</a></li>
                        <li><a href="#">2</a></li>
                        <li><a href="#">3</a></li>
                        <li><a href="#">4</a></li>
                        <li><a href="#">5</a></li>
                        <li><a href="#">下一页</a></li>
                        <li class="disabled"><a href="#">末页</a></li>
                    </ul>
                </nav>
            </div>
            --%>
        </div>

    </div>

</div>
</body>
</html>