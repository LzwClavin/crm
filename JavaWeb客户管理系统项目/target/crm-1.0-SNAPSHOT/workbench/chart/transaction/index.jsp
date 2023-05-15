<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <base href="<%=path%>">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title></title>
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="ECharts/echarts.min.js"></script>
    <script type="text/javascript">
        $(function () {
            //页面加载完毕后绘制统计图
            getCharts();
        })

        getCharts = function () {
            $.ajax({
                type: "get",
                url: "workbench/transaction/getCharts.do",
                dataType: "json",
                success: function (data) {
                    /*
                    调用echarts.init方法初始化一个echarts实例，实参是容纳统计图的盒子DOM，返回一个echarts实例
                    注意这里获取盒子时不能使用jQuery语法，必须使用原生JS语法，因为ECharts不支持jQuery
                    */
                    var myChart = echarts.init(document.getElementById('main'));

                    //指定图表的配置项和数据，生成统计图，option就是图表对象
                    var option = {
                        title: {
                            text: '交易漏斗图',//大标题
                            subtext: '统计交易阶段数量漏斗图'//标题描述
                        },
                        tooltip: {
                            trigger: 'item',

                            //数据百分比，鼠标停留在某项上会显示，这里的a是图表标题，b是当前阶段的阶段名，c是当前阶段所占百分比
                            formatter: "{a} <br/>{b} : {c}%"
                        },
                        toolbox: {//工具盒
                            feature: {
                                /*
                                数据视图按钮，点击后将统计图变成数据列表列出来，readOnly是false表示允许修改这些数据
                                修改后刷新即可动态展现修改后的漏斗图
                                */
                                dataView: {readOnly: false},
                                restore: {},//还原按钮，使用数据视图修改数据后，点击还原按钮可以还原
                                saveAsImage: {}//保存图表为图片按钮
                            }
                        },
                        legend: {
                            data: ['展现', '点击', '访问', '咨询', '订单']
                        },
                        calculable: true,
                        series: [
                            {
                                name: '交易漏斗图',//数据视图中显示的标题
                                type: 'funnel',
                                left: '10%',
                                top: 60,
                                //x2: 80,
                                bottom: 60,
                                width: '80%',
                                //height: {totalHeight} - y - y2,
                                min: 0,
                                max: data.total,//需要统计的总条数，获取后端响应的data中的total
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,//每项之间的边距
                                label: {//显示的文字设置
                                    normal: {
                                        //是否显示统计项文字，如果不设置，默认是true，如果是false，只有鼠标停留在项中才会显示
                                        show: true,

                                        //设置统计项文字显示在漏斗外部，如果不设置，默认是外部，如果值是inside，表示在漏斗内部
                                        position: 'outside'
                                    },
                                    emphasis: {
                                        textStyle: {
                                            //设置鼠标悬停时显示的统计项文字大小，如果不设置，默认与非悬停时的大小一致
                                            fontSize: 16,

                                            //设置鼠标悬停时显示的统计项文字颜色，如果不设置，默认与非悬停时的颜色一致
                                            color: '#000'
                                        }
                                    }
                                },
                                labelLine: {
                                    normal: {
                                        length: 10,
                                        lineStyle: {
                                            width: 1,
                                            type: 'solid'
                                        }
                                    }
                                },
                                itemStyle: {//统计项样式设置
                                    normal: {
                                        borderColor: '#000',//每项的边框颜色，如果不设置，默认是白色
                                        borderWidth: 1//每项的边框宽度，如果不设置，默认是1
                                    }
                                },

                                /*
                                data中的name表示统计项，value表示统计项的数量
                                所以底层的SQL语句返回的字段名不能返回stage和count(*)，因为这里无法识别，要改名为name和value并返回
                                如果某些数据过多，某些过少，显示的不是一个标准的漏斗，即会根据数据量确定每一层占用的面积
                                因为需要使用到JSON数据，所以必须发送Ajax请求获取数据，无法使用传统请求
                                所以本图应该放在Ajax的成功函数中；这里获取后端响应的data中的JSON数据datas
                                */
                                data: data.datas
                                /*
                                [
                                    {value: 5, name: '01资质审查'},
                                    {value: 3, name: '02需求分析'},
                                    {value: 2, name: '03价值建议'},
                                    {value: 1, name: '04确定决策者'},
                                    {value: 7, name: '05提案报价'},
                                    {value: 6, name: '06谈判复审'},
                                    {value: 10, name: '07成交'},
                                    {value: 8, name: '08丢失的线索'},
                                    {value: 9, name: '09因竞争丢失关闭'}
                                ]
                                */
                            }
                        ]
                    };

                    //使用上面指定的配置项和数据显示图表，使用ECharts实例myChart调用setOption方法，参数是图表对象
                    myChart.setOption(option);
                }
            })
        }
    </script>
</head>
<body>
<%--为ECharts统计图准备一个一定大小的DOM容器--%>
<div id="main" style="width: 600px;height:400px;"></div>
</body>
</html>