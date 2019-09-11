package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {

    @Reference
    private ManageService manageService;

    @GetMapping("/spuList")
    public List<SpuInfo> spuList(SpuInfo spuInfo){
        return manageService.spuList(spuInfo);
    }

    @PostMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody SpuInfo spuInfo){
        if(spuInfo != null){
            //调用保存
            manageService.saveSpuInfo(spuInfo);
        }
        return "success";
    }

}
