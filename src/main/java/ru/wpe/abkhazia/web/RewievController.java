package ru.wpe.abkhazia.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import ru.wpe.abkhazia.domain.Entity;
import ru.wpe.abkhazia.domain.GeoPoint;
import ru.wpe.abkhazia.domain.Region;
import ru.wpe.abkhazia.domain.Rewiev;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/rewievs")
@Controller
@RooWebScaffold(path = "rewievs", formBackingObject = Rewiev.class)
public class RewievController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public List<Rewiev> show(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            List<Rewiev> resultList = new ArrayList<Rewiev>();
            resultList.add(Rewiev.findRewiev(id));
            return resultList;
        }
        return Rewiev.findAllRewievs();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("rewiev", Rewiev.findRewiev(id));
        uiModel.addAttribute("itemId", id);
        return "rewievs/show";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("rewievs", Rewiev.findRewievEntries(firstResult, sizeNo));
            float nrOfPages = (float) Rewiev.countRewievs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("rewievs", Rewiev.findAllRewievs());
        }
        addDateTimeFormatPatterns(uiModel);
        return "rewievs/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String create(@RequestBody String body, @RequestParam(value = "rating", required = true) int rating,
                         @RequestParam(value = "title", required = true) String title,
                         @RequestParam(value = "comment", required = true) String comment,
                         @RequestParam(value = "entity", required = true) long entity,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        Entity targetEntity = Entity.findEntity(entity);
        Rewiev review = new Rewiev.Builder()
                .rating(rating)
                .title(title)
                .comment(comment)
                .postingDate(new Date())
                .entity(targetEntity).build();
        uiModel.asMap().clear();
        review.persist();
        targetEntity.getRewievs().add(review);
        EntityController.calculateEntityRate(targetEntity);
        return "redirect:/rewievs/" + encodeUrlPathSegment(review.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    @Transactional
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Rewiev());
        return "rewievs/create";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    @Transactional
    public String update(@RequestBody String body, @RequestParam(value = "id", required = true) Long id,
                         @RequestParam(value = "rating", required = true) int rating,
                         @RequestParam(value = "title", required = true) String title,
                         @RequestParam(value = "comment", required = true) String comment,
                         @RequestParam(value = "entity", required = true) long entity,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.asMap().clear();
        Entity targetEntity = Entity.findEntity(entity);
        Rewiev review = Rewiev.findRewiev(id);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        review.setEntity(targetEntity);
        review.merge();
        targetEntity.getRewievs().add(review);
        EntityController.calculateEntityRate(targetEntity);
        return "redirect:/rewievs/" + encodeUrlPathSegment(review.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    @Transactional
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Rewiev.findRewiev(id));
        return "rewievs/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    @Transactional
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Rewiev rewiev = Rewiev.findRewiev(id);
        Entity targetEntity = rewiev.getEntity();
        rewiev.remove();
        targetEntity.getRewievs().remove(rewiev);
        EntityController.calculateEntityRate(targetEntity);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/rewievs";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("rewiev_postingdate_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

    void populateEditForm(Model uiModel, Rewiev rewiev) {
        uiModel.addAttribute("rewiev", rewiev);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("entitys", Entity.findAllEntitys());
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}
