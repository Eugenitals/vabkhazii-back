package ru.wpe.abkhazia.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/geopoints")
@Controller
@RooWebScaffold(path = "geopoints", formBackingObject = GeoPoint.class)
public class GeoPointController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public List<GeoPoint> show(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            List<GeoPoint> resultList = new ArrayList<GeoPoint>();
            resultList.add(GeoPoint.findGeoPoint(id));
            return resultList;
        }

        return GeoPoint.findAllActiveGeoPoints();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("geopoint", GeoPoint.findGeoPoint(id));
        uiModel.addAttribute("itemId", id);
        return "geopoints/show";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("geopoints", GeoPoint.findGeoPointEntries(firstResult, sizeNo));
            float nrOfPages = (float) GeoPoint.countGeoPoints() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("geopoints", GeoPoint.findAllGeoPoints());
        }
        return "geopoints/list";
    }

    @RequestMapping(params = "find", method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String find(@RequestParam(value = "like", required = false) String like, Model uiModel) {
        uiModel.addAttribute("geopoints", GeoPoint.findAllGeoPoints(like));
        return "geopoints/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String create(@RequestBody String body, @RequestParam(value = "active", required = true) Boolean active,
                         @RequestParam(value = "latitude", required = true) double latitude,
                         @RequestParam(value = "longitude", required = true) double longitude,
                         @RequestParam(value = "address", required = true) String address,
                         @RequestParam(value = "region", required = true) long region,
                         @RequestParam(value = "entity", required = true) long entity,
                         Model uiModel, HttpServletRequest httpServletRequest) {

        GeoPoint geoPoint = new GeoPoint.Builder()
                .active(active)
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .region(Region.findRegion(region))
                .entity(Entity.findEntity(entity)).build();
        uiModel.asMap().clear();
        geoPoint.persist();
        return "redirect:/geopoints/" + encodeUrlPathSegment(geoPoint.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    @Transactional
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new GeoPoint());
        return "geopoints/create";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    @Transactional
    public String update(@RequestBody String body, @RequestParam(value = "id", required = true) Long id,
                         @RequestParam(value = "active", required = true) Boolean active,
                         @RequestParam(value = "latitude", required = true) double latitude,
                         @RequestParam(value = "longitude", required = true) double longitude,
                         @RequestParam(value = "address", required = true) String address,
                         @RequestParam(value = "region", required = true) long region,
                         @RequestParam(value = "entity", required = true) long entity,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.asMap().clear();
        GeoPoint geoPoint = GeoPoint.findGeoPoint(id);
        geoPoint.setActive(active);
        geoPoint.setLatitude(latitude);
        geoPoint.setLongitude(longitude);
        geoPoint.setAddress(address);
        geoPoint.setRegion(Region.findRegion(region));
        geoPoint.setEntity(Entity.findEntity(entity));
        geoPoint.persist();
        if (geoPoint.getEntity() != null) {
            geoPoint.getEntity().increaseVersion();
        }

        return "redirect:/geopoints/" + encodeUrlPathSegment(geoPoint.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    @Transactional
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, GeoPoint.findGeoPoint(id));
        return "geopoints/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    @Transactional
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        GeoPoint geoPoint = GeoPoint.findGeoPoint(id);
        geoPoint.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/geopoints";
    }

    void populateEditForm(Model uiModel, GeoPoint geoPoint) {
        uiModel.addAttribute("geoPoint", geoPoint);
        uiModel.addAttribute("entitys", Entity.findAllEntitys());
        uiModel.addAttribute("regions", Region.findAllRegions());
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
