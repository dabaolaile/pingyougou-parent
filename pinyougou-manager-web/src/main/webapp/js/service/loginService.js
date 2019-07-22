app.service('loginService',function ($http) {

    //读取用户名

    this.loginName=function () {

        return $http.get('../login/name.do')
    }
})