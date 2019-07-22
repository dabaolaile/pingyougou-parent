app.service("uploadService",function ($http) {
    
    this.upload=function () {

        var formData = new FormData();//意思就是一个上传的表单   只有在上传图片才会用到

        formData.append("file",file.files[0]);  //file 文件上传框的name

        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},//当你上传文件时指定是undefined类型
            transformRequest: angular.identity  //固定写法   对整个表单进行一个序列化
        });
    }
})