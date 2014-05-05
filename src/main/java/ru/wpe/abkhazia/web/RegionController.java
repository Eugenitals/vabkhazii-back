package ru.wpe.abkhazia.web;

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
import ru.wpe.abkhazia.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/regions")
@Controller
@RooWebScaffold(path = "regions", formBackingObject = Region.class)
public class RegionController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public List<Region> show(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            List<Region> resultList = new ArrayList<Region>();
            resultList.add(Region.findRegion(id));
            return resultList;
        }

        return Region.findAllActiveRegions();
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    @Transactional
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("region", Region.findRegion(id));
        uiModel.addAttribute("itemId", id);
        return "regions/show";
    }

    @RequestMapping(produces = "text/html")
    @Transactional
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("regions", Region.findRegionEntries(firstResult, sizeNo));
            float nrOfPages = (float) Region.countRegions() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("regions", Region.findAllRegions());
        }
        return "regions/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String create(@Valid Region region, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, region);
            return "regions/create";
        }
        uiModel.asMap().clear();
        region.persist();
        return "redirect:/regions/" + encodeUrlPathSegment(region.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    @Transactional
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Region());
        return "regions/create";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    @Transactional
    public String update(@Valid Region region, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, region);
            return "regions/update";
        }
        uiModel.asMap().clear();
        region.merge();
        return "redirect:/regions/" + encodeUrlPathSegment(region.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    @Transactional
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Region.findRegion(id));
        return "regions/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    @Transactional
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Region region = Region.findRegion(id);
        region.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/regions";
    }

    void populateEditForm(Model uiModel, Region region) {
        uiModel.addAttribute("region", region);
        uiModel.addAttribute("geopoints", GeoPoint.findAllGeoPoints());
        uiModel.addAttribute("resources", Resource.findAllResources());
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
