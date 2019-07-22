app.controller("baseController",function ($scope) {

    //刷新列表
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);

    };
    //分页参数
    $scope.paginationConf = {
        currentPage: 1,  //当前页码pageNum
        totalItems: 10,     //总记录数
        itemsPerPage: 10,   //每页记录数pageSize
        perPageOptions: [10, 20, 30, 40, 50], //页码选项
        onChange: function () {     //更改页面是触发事件
            $scope.reloadList();
        }
    };

    $scope.selectIds = [];//选中的ID集合
    //更新复选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果是被选中,则增加到数组
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除
        }
    };


    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString=function (jsonString,key) {

        var json = JSON.parse(jsonString);//将json字符串转换为json对象
        var value = "";
        for(var i = 0;i<json.length;i++){

            if(i>0){
                value+=",";
            }
            value += json[i][key]
        }
        return value;
    }

   /* //从集合中按照key查询对象
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(vari=0;i<list.length;i++){
            if(list[i][key]==keyValue){returnlist[i];}}returnnull;
    }*/
   
   
   //从集合中按照可以查询对象  [{“attributeName”:”规格名称”,”attributeValue”:[“规格选项1”,“规格选项2”.... ]    } , ....    ]
    $scope.searchObjectByKey = function (list,key,keyValue) {
        for(var i = 0 ; i < list.length ; i++){
            if(list[i][key] == keyValue){
                return list[i];
            }
        }
        return null;
    }
   
   
});