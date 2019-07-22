var sellergoods_url = "http://localhost:9101";
app.service("brandService", function ($http) {
    //品牌服务
    this.findAll = function () {
        return $http.get(sellergoods_url + "/brand/findAll.do")
    };

    this.findOne = function (id) {
        return $http.get(sellergoods_url + "/brand/findOne.do?id=" + id)
    };
    this.findPage = function (page, size) {
        return $http.get(sellergoods_url + "/brand/findPage.do?page=" + page + '&size=' + size)
    };

    this.dele = function (ids) {
        return $http.get(sellergoods_url + '/brand/delete.do?ids=' + ids)
    };

    this.search = function (page, size, searchEntity) {
        return $http.post(sellergoods_url + "/brand/search.do?page=" + page + '&size=' + size, searchEntity)
    };

    this.add = function (entity) {
        return $http.post(sellergoods_url + "/brand/add.do", entity)
    };

    this.update = function (entity) {
        return $http.post(sellergoods_url + "/brand/update.do", entity)
    }

    //下拉框
    this.selectOptionList = function () {
        return $http.get(sellergoods_url + "/brand/selectOptionList.do");
    }
});