package com.qiexr.api.client.assembler.runtest;

import com.xingren.common.data.JsonResult;
import com.xingren.invoice.api.controller.IInvoiceController;
import com.xingren.v.auth.XrContextHolder;
import com.xingren.v.logging.annotations.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * @author Xinshuai
 * @description
 * @since 2020-07-03 13:44
 */
@Slf4j(module = "Test")
@Component
public class ApiClientTest {

    @Autowired(required = false)
    private IInvoiceController iinvoicecontroller;

    @PostConstruct
    public void action1() {
        XrContextHolder.setJobContext();
        JsonResult<Integer> result = iinvoicecontroller.availableInvoiceContract(10);
        log.info("------------------");
        log.info(result.toString());
        log.info("------------------");
    }

}
