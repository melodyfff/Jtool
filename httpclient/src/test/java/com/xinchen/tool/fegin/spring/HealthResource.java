package com.xinchen.tool.fegin.spring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.MissingResourceException;

/**
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 17:13
 */
@RestController
@RequestMapping(value = "/health", produces = "text/html")
interface HealthResource extends GenericResource<Data>{
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    String getStatus();

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    void check(@PathVariable("id") String campaignId,
            @RequestParam(value = "deep", defaultValue = "false") boolean deepCheck);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    void check(
            @PathVariable("id") String campaignId,
            @RequestParam(value = "deep", defaultValue = "false") boolean deepCheck,
            @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun);

    @GetMapping(value = "/{id}")
    void check(@PathVariable("id") String campaignId);

    @ResponseStatus(value = HttpStatus.NOT_FOUND,
            reason = "This customer is not found in the system")
    @ExceptionHandler(MissingResourceException.class)
    void missingResourceExceptionHandler();
}
