//品牌控制
app.controller('BrandController', function ($scope, $controller,brandService) {
    $controller('baseController',{$scope:$scope}); //伪继承

    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    };

    //分页查询
    $scope.findPage = function (page, size) {
        brandService.findPage(page, size).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };
    //findOne
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;

        });
    };

    //新增或修改
    $scope.save = function () {
        var object = null;//方法名称
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);

        }else{
            object = brandService.add($scope.entity);
        }
        object.success(function (response) {

            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    };


    //批量删除
    $scope.dele = function () {
        if (confirm("你确定?")) {
            //获取选中的复选框
            brandService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                    }
                }
            );
        }
    };
    //条件分页查询
    $scope.searchEntity = {}; //初始化变量,查询条件
    $scope.search = function (page, size) {
        brandService.search(page, size,$scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

})