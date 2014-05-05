package ru.wpe.abkhazia.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import ru.wpe.abkhazia.domain.Entity;
import ru.wpe.abkhazia.domain.Region;
import ru.wpe.abkhazia.domain.Resource;
import ru.wpe.abkhazia.domain.ResourceType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("/resources")
@Controller
@RooWebScaffold(path = "resources", formBackingObject = Resource.class)
public class ResourceController {

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws ServletException {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public List<Resource> show(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            List<Resource> resultList = new ArrayList<Resource>();
            resultList.add(Resource.findResource(id));
            return resultList;
        }

        return Resource.findAllResources();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String show(@PathVariable("id") Long id, Model uiModel, HttpServletRequest request) {
        Resource resource = Resource.findResource(id);
        String baseUrl = String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
        resource.setUrl(baseUrl + "/resources/showres/" + id);
        uiModel.addAttribute("resource", resource);
        return "resources/show";
    }

    @RequestMapping(value = "/showres/{id}", method = RequestMethod.GET)
    @Transactional
    public String showdoc(@PathVariable("id") Long id, HttpServletResponse response, Model model) {
        Resource resource = Resource.findResource(id);

        try {
            response.setHeader("Content-Disposition", "inline;filename=\"" + resource.getFilename() + "\"");

            OutputStream out = response.getOutputStream();
            response.setContentType(resource.getContentType());
            IOUtils.copy(new ByteArrayInputStream(resource.getContent()), out);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("resources", Resource.findResourceEntries(firstResult, sizeNo));
            float nrOfPages = (float) Resource.countResources() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("resources", Resource.findAllResources());
        }
        return "resources/list";
    }

    @RequestMapping(params = "form", produces = "text/html")
    @Transactional
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Resource());
        return "resources/create";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String create(@RequestBody String body, @RequestParam(value = "type", required = true) ResourceType type,
                         @RequestParam(value = "entity", required = true) Long entityId,
                         @RequestParam(value = "description", required = true) String description,
                         @RequestParam(value = "content", required = true) MultipartFile content,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        try {
            Resource resource = new Resource.Builder()
                    .type(type)
                    .entity(Entity.findEntity(entityId))
                    .description(description)
                    .contentType(content.getContentType())
                    .filename(content.getOriginalFilename())
                    .sizee(content.getSize())
                    .content(content.getBytes()).build();
            resource.persist();
            return "redirect:/resources/" + encodeUrlPathSegment(resource.getId().toString(), httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            populateEditForm(uiModel, new Resource());
            return "nodes/create";
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    @Transactional
    public String update(@RequestBody String body, @RequestParam(value = "id", required = true) Long id,
                         @RequestParam(value = "entity", required = true) Long entityId,
                         @RequestParam("description") String description, Model uiModel, HttpServletRequest httpServletRequest) {
        Resource resource = Resource.findResource(id);
        resource.setDescription(description);
        resource.setEntity(Entity.findEntity(entityId));
        uiModel.asMap().clear();
        resource.persist();
        return "redirect:/resources/" + encodeUrlPathSegment(resource.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    @Transactional
    public String updateForm(@PathVariable("id") Long id, Model uiModel, HttpServletRequest request) {
        Resource resource = Resource.findResource(id);
        populateEditForm(uiModel, resource);
        String baseUrl = String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
        resource.setUrl(baseUrl + "/resources/showres/" + id);
        uiModel.addAttribute("resource", resource);
        return "resources/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    @Transactional
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Resource resource = Resource.findResource(id);
        resource.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/resources";
    }

    void populateEditForm(Model uiModel, Resource resource) {
        uiModel.addAttribute("resource", resource);
        uiModel.addAttribute("entitys", Entity.findAllEntitys());
        uiModel.addAttribute("regions", Region.findAllRegions());
        uiModel.addAttribute("resourcetypes", Arrays.asList(ResourceType.values()));
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
}
