package com.pinyougou.shop.controller;

import com.pinyougou.entity.Result;
import com.pinyougou.until.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件上传 Controller
 *
 * @author Administrator
 */
@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址

    @RequestMapping("/upload")
    public Result upload(MultipartFile file) {
        //1、取文件的扩展名
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
//2、创建一个 FastDFS 的客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //3、执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //4、拼接返回的 url 和 ip 地址，拼装成完整的 url
            String url = FILE_SERVER_URL + path;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}















/*package com.pinyougou.shop.controller;


import com.pinyougou.entity.Result;
import com.pinyougou.until.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

*//*
商品上传图片功能   后端
*//*

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file)  {    //传的参数file是前端传给后端的文件对象

        String filename = file.getOriginalFilename();//获取文件的全名称
        String substring = filename.substring(filename.lastIndexOf(".") + 1);//得到扩展名
        try {
            //构建客户端对象
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");

            //执行上传处理
            String fileId = client.uploadFile(file.getBytes(), substring);//第一个参数是  转换为字节码    第二个 是扩展名

            String url= FILE_SERVER_URL + fileId;  //获取到图片的完整地址

            return new Result(true,url);

        } catch (Exception e) {

            e.printStackTrace();

            return new Result(false,"上传失败");
        }
    }

    }*/
