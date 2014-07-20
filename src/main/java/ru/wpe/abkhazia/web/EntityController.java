package ru.wpe.abkhazia.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import ru.wpe.abkhazia.*;
import ru.wpe.abkhazia.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/entitys")
@Controller
@RooWebScaffold(path = "entitys", formBackingObject = Entity.class)
public class EntityController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public List<Entity> show(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            List<Entity> resultList = new ArrayList<Entity>();
            resultList.add(Entity.findEntity(id));
            return resultList;
        }

        return Entity.findAllActiveEntitys();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("entity", Entity.findEntity(id));
        uiModel.addAttribute("resources", Resource.findAllResources());
        uiModel.addAttribute("itemId", id);
        return "entitys/show";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("entitys", Entity.findEntityEntries(firstResult, sizeNo));
            float nrOfPages = (float) Entity.countEntitys() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("entitys", Entity.findAllEntitys());
        }
        return "entitys/list";
    }

    @RequestMapping(params = "find", method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String find(@RequestParam(value = "like", required = false) String like, Model uiModel) {
        uiModel.addAttribute("entitys", Entity.findAllEntitys(like));
        return "entitys/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String create(@Valid Entity entity, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, entity);
            return "entitys/create";
        }
        uiModel.asMap().clear();
        entity.persist();
        return "redirect:/entitys/" + encodeUrlPathSegment(entity.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    @Transactional
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Entity());
        return "entitys/create";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    @Transactional
    public String update(@Valid Entity entity, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, entity);
            return "entitys/update";
        }
        uiModel.asMap().clear();
        entity = entity.merge();
        EntityController.calculateEntityRate(entity);
        if (entity.getParent() != null) {
            entity.getParent().increaseVersion();
        }

        return "redirect:/entitys/" + encodeUrlPathSegment(entity.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    @Transactional
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Entity.findEntity(id));
        return "entitys/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    @Transactional
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Entity entity = Entity.findEntity(id);
        entity.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/entitys";
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

    void populateEditForm(Model uiModel, Entity entity) {
        List<Node> nodeList = new ArrayList<Node>();
        for (Node node : Node.findAllNodes()) {
            if (!node.getLeaf()) {
                nodeList.add(node);
            }
        }

        uiModel.addAttribute("entity", entity);
        uiModel.addAttribute("entitytypes", Arrays.asList(EntityType.values()));
        uiModel.addAttribute("geopoints", GeoPoint.findAllGeoPoints());
        uiModel.addAttribute("nodes", nodeList);
        uiModel.addAttribute("resources", Resource.findAllResources());
        uiModel.addAttribute("rewievs", Rewiev.findAllRewievs());
        uiModel.addAttribute("regions", Region.findAllRegions());
    }

    @Transactional
    public static void calculateEntityRate(Entity entity) {
        Set<Rewiev> reviews = entity.getRewievs();
        float newRate = 0;

        if (reviews.size() > 0) {
            for (Rewiev review : reviews) {
                newRate += review.getRating();
            }
            newRate = newRate / reviews.size();
        } else {
            newRate = 0;
        }

        entity.setRate(newRate);
        entity.persist();
    }
}
