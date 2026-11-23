
package org.demo.springretry.Controller;

import org.demo.springretry.service.ExceptionRetryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retry")
public class ExceptionRetryController {

    private final ExceptionRetryService exceptionRetryService;

    public ExceptionRetryController(
            ExceptionRetryService exceptionRetryService) {
        this.exceptionRetryService = exceptionRetryService;
    }

    @GetMapping("/{exceptionName}")
    public String testException(
            @PathVariable String exceptionName) {

        return exceptionRetryService.testException(exceptionName);
    }
}
