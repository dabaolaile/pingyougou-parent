//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    // y页面点击修改时查询实体//知识点 $location。search()  意思就是获取页面的所有参数
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;

                //向富文本中添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction)

                //显示图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages)

                //显示扩展属性
                //因为在查询属性的时候  在下边有个扩展属性的方法和这个一样  会覆盖  所以进行判断
                if ($location.search()['id'] == null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems)
                }

                //读取规格属性
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems)

                //判断属性是否被勾选
                $scope.checkAttributeValue = function () {
                    var item = $scope.entity.goodsDesc.specificationItems;
                    var object = $scope.searchObjectByKey(item, 'attributeName', optionName);
                    if (object == null) {
                        return false;
                    } else {
                        if (object.attributeValue.indexOf(optionName) >= 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                //读取sku列表
                //因为sku是一个集合  需要遍历
                for (var i = 0; i < $scope.entity.itemList.length; i++){
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }



    //保存
    $scope.save=function(){

        $scope.entity.goodsDesc.introduction = editor.html();//一提交将富文本编辑器的东西给了商品介绍introduction
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert("保存成功")

                    location.href = "goods.html"; //跳转到商品列表
                }else{
                    alert(response.message);
                }
            }
        );
    }



   /* //保存   保存时还有一个富文本编辑器   需设置一下富文本
    $scope.add = function () {

        $scope.entity.goodsDesc.introduction = editor.html();//一提交将富文本编辑器的东西给了商品介绍introduction

        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert("保存成功")
                    $scope.entity = {}; //保存成功后清空

                    editor.html('');//清空富文本编辑器

                } else {
                    alert(response.message)
                }
            }
        )
    }*/


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    //图片上传
    $scope.upload = function () {

        uploadService.upload().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;//设置文件地址
                } else {
                    alert(response.message)
                }
            }
        )
    }

    //图片列表   当页面一点击保存时  页面会展示保存的列表

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};//定义页面实体结构

    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    //移除图片


    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1)
    }


    //在商品录入中  实现一级下拉列表  读出数据库中的数据
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.ItemCat1List = response;
            }
        )
    }


    //读取二级分类   $scope.$watch 代表的是监控方法
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {

        //根据选择的值查询二级拉框
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.ItemCat2List = response;
            })

    })


    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {

        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.ItemCat3List = response;
            }
        )
    })


    //读取模板Id
    //三级分类后  利用监控去做  监控第三类后
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;   //更新模板id
            }
        )
    })


    //商品录入中品牌选择   根据模板id   去查询品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//获取类型模板

                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds)
            }
        )
    })


    //功能：扩展属性  在用户更新模板ID时，读取模板中的扩展属性赋给商品的扩展属性
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {

        typeTemplateService.findOne(newValue).success(
            function (response) {

                $scope.typeTemplate = response;//获取类型模板

                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds)

                //扩展属性                                                                    /*模板中定义的属性名称*/
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems)

            }
        );

        // 查询规格列表  模板中显示出规ge
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    });

    //保存选中的规格选项   勾选选项框
    //[{“attributeName”:”规格名称”,”attributeValue”:[“规格选项1”,“规格选项2”.... ]    } , ....    ]
    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);

        if (object != null) {
            //如果被选中了  那就push
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {
                //取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);

                //ru如果选项都取消了  那就把空【】记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]})
        }
    }


    //勾选规格选项框时  录入sku商品信息  利用深克隆生成sku列表
    //思路 ： 我们先定义一个初始的不带规格名称的集合，只有一条记录。 循环用户选择的规格，根据规格名称和已选择的规格选项对原集合进行扩充，添加规格名称和值，新增的记录数与选择的规格选项个数相同

    //创建sku列表
    $scope.createItemList = function () {

        //首先定义一个初始不带规格的集合
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];

        //相当于用户选的一个集合
        var items = $scope.entity.goodsDesc.specificationItems;

        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }

    //sku添加列值
    addColumn = function (list, columnName, conlumnValues) {

        //构建新的集合
        var newList = [];

        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];

            for (var j = 0; j < conlumnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow)); //深克隆
                //加一列
                newRow.spec[columnName] = conlumnValues[j];

                newList.push(newRow);
            }
        }
        return newList;
    }


    //显示商品状态
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];//商品状态


    //商家商品中显示分类
    //定义一个数组来接受响应回来的数据
    $scope.ItemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                //响应回来的是一个数组格式   需要遍历
                //响应回来的是一个数组格式   需要遍历
                for (var i = 0; i < response.length; i++) {
                    //根据id作为下标
                    $scope.ItemCatList[response[i].id] = response[i].name;//因为我们需要根据分类ID得到分类名称，所以我们将返回的分页结果以数组形式再次封装
                }
            }
        )
    }


});
