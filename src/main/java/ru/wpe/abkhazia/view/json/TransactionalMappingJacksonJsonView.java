package ru.wpe.abkhazia.view.json;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene
 * Date: 19.05.13
 * Time: 22:40
 */
public class TransactionalMappingJacksonJsonView extends MappingJacksonJsonView {

    @Override
    @Transactional
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super.prepareResponse(request, response);
    }
}
