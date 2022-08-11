package io.lihongbin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class SpeedTestController {

    private final static String DEFAULT_SIZE = "100M";
    private final static byte[] DATA = new byte[1024 * 64];

    @GetMapping("speed/test")
    public void speedTest(@RequestParam("size") String sizeAndUnit, HttpServletResponse response) throws IOException {
        if (!StringUtils.hasText(sizeAndUnit) && sizeAndUnit.length() >= 2) {
            sizeAndUnit = DEFAULT_SIZE;
        }
        // 获取大小
        int size = Integer.parseInt(sizeAndUnit.substring(0, sizeAndUnit.length() - 1));
        // 获取单位
        String unit = sizeAndUnit.substring(sizeAndUnit.length() - 1);
        log.info("download {}{}", size, unit);

        long downloadLength;
        switch (unit) {
            case "B" :
                downloadLength = size;
                break;
            case "K" :
                downloadLength = size * 1024L;
                break;
            case "M" :
                downloadLength = size * 1024L * 1024;
                break;
            case "G" :
                downloadLength = size * 1024L * 1024 * 1024;
                break;
            case "T" :
                downloadLength = size * 1024L * 1024 * 1024 * 1024;
                break;
            default:
                throw new RuntimeException("unit error: " + unit);
        }

        response.setHeader("content-disposition", "attachment;filename=Test.txt");
        ServletOutputStream outputStream = response.getOutputStream();

        long count = downloadLength / DATA.length;
        for (long i = 0; i < count; i++) {
            outputStream.write(DATA);
        }
        int residue = (int) (downloadLength % DATA.length);
        if (residue > 0) {
            byte[] bytes = new byte[residue];
            outputStream.write(bytes);
        }
        outputStream.flush();
    }

}
