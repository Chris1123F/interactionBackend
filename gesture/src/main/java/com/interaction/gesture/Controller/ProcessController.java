package com.interaction.gesture.Controller;

import com.interaction.gesture.Entry.Project;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;

@RestController("/api/process")
public class ProcessController {
    @RequestMapping("/gerProject")
    public String  gerProject() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        process = runtime.exec("cmd /c reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\");
        BufferedReader in = new BufferedReader(new InputStreamReader(process
                .getInputStream(),"GBK"));

        String string = null;
        List<Project> result = new ArrayList<>();
        while ((string = in.readLine()) != null) {
            System.out.println(string);
            process = runtime.exec("cmd /c reg query " + string + " /v DisplayName");
            System.out.println(process);

            BufferedReader name = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            Project project = queryValue(string);
            if (project != null) {
                result.add(project);
            }
        }
        in.close();
        process.destroy();
        return JSON.toJSONString(result);
    }
    private Project queryValue(String string) throws IOException {
        String nameString = "";
        String versionString = "";

        String publisherString="";
        String uninstallPathString = "";

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        BufferedReader br = null;

        process = runtime.exec("cmd /c reg query " + string + " /v DisplayName");
        br = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if ((nameString=br.readLine())!=null) {
            nameString=nameString.replaceAll("DisplayName    REG_SZ    ", "");  //去掉无用信息
        }


        process = runtime.exec("cmd /c reg query " + string + " /v DisplayVersion");
        br = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
        br.readLine();br.readLine();//去掉前两行无用信息
        if ((versionString=br.readLine()) != null) {
            versionString=versionString.replaceAll("DisplayVersion    REG_SZ    ", ""); //去掉无用信息
        }

        process = runtime.exec("cmd /c reg query " + string + " /v Publisher");
        br = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
        br.readLine();
        br.readLine();//去掉前两行无用信息
        if ((publisherString = br.readLine()) != null) {
            publisherString = publisherString.replaceAll("Publisher    REG_SZ    ", ""); //去掉无用信息
        }

        process = runtime.exec("cmd /c reg query " + string + " /v UninstallString");
        br = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
        br.readLine();
        br.readLine();//去掉前两行无用信息
        if ((uninstallPathString = br.readLine()) != null) {
            uninstallPathString = uninstallPathString.replaceAll("UninstallString    REG_SZ    ", "");    //去掉无用信息
        }
        if (nameString == null) {
            return null; //没有名字的不显示
        }
        Project project = new Project(nameString, versionString, publisherString, uninstallPathString);
        return project;
    }
}
