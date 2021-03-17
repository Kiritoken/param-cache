package com.eli.param.cache.center.controller;

import com.eli.param.cache.center.utils.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eli
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public Result<String> test() {
        return Result.wrapSuccessfulResult("dedfaef");
    }


}
