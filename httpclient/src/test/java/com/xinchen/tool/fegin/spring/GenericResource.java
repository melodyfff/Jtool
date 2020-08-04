package com.xinchen.tool.fegin.spring;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 17:12
 */
interface GenericResource<DTO> {
    @RequestMapping(value = "generic", method = RequestMethod.GET)
    @ResponseBody
    DTO getData(@RequestBody DTO input);
}
