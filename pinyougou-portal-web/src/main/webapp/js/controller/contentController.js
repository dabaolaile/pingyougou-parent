app.controller("contentController",function ($scope, contentService) {

    //所有的广告集合   为了好区分  因为页面好多广告
    $scope.contentList = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        )
    }
});