package com.aws.codestar.projecttemplates.controller;

import com.aws.codestar.projecttemplates.BatchService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic Spring web service controller that handles all GET requests.
 */
@RestController
@RequestMapping("/batch")
public class SpringBatchController {

    private static final String MESSAGE_FORMAT = "Hello %s!";

    @Autowired
    private BatchService batchService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity helloWorldGet(@RequestParam(value = "name", defaultValue = "World Spring Batch on AWS") String name) {
        return ResponseEntity.ok(createResponse(name));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "start")
    public ResponseEntity startJob() {
        batchService.startJob();
        return ResponseEntity.ok("job started!");
    }

    private String createResponse(String name) {
        return new JSONObject().put("Output", String.format(MESSAGE_FORMAT, name)).toString();
    }
}