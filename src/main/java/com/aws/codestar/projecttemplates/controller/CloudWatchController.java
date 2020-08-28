package com.aws.codestar.projecttemplates.controller;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic Spring web service controller that handles all GET requests.
 */
@RestController
@RequestMapping("/cloudwatch")
public class CloudWatchController {

    private static final String MESSAGE_FORMAT = "Hello %s!";

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity helloWorldGet(@RequestParam(value = "name", defaultValue = "World") String name) {
        return ResponseEntity.ok(createResponse(name));
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity helloWorldPost(@RequestParam(value = "name", defaultValue = "World") String name) {
        return ResponseEntity.ok(createResponse(name));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "metrics")
    public ResponseEntity putMetrics(@RequestParam(value = "val", defaultValue = "0.0") Double val) {
        putMetricsToCW(val);
        return ResponseEntity.ok("metrics put successfully");
    }

    private void putMetricsToCW(Double value) {
        final AmazonCloudWatch cw =
                AmazonCloudWatchClientBuilder.defaultClient();

        Dimension dimension = new Dimension()
                .withName("UNIQUE_PAGES")
                .withValue("URLS");

        System.out.println("sending metrics ");

        MetricDatum datum = new MetricDatum()
                .withMetricName("PAGES_VISITED")
                .withUnit(StandardUnit.None)
                .withValue(value)
                .withDimensions(dimension);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("SITE/TRAFFIC")
                .withMetricData(datum);

        PutMetricDataResult response = cw.putMetricData(request);
        System.out.println(response);


    }

    private String createResponse(String name) {
        return new JSONObject().put("Output", String.format(MESSAGE_FORMAT, name)).toString();
    }
}