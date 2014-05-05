package ru.wpe.abkhazia.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
import ru.wpe.abkhazia.domain.Node;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/nodes")
@Controller
@RooWebScaffold(path = "nodes", formBackingObject = Node.class)
public class NodeController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("node", Node.findNode(id));
        uiModel.addAttribute("itemId", id);
        return "nodes/show";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Transactional
    public List<Node> list(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "regionId", required = true) Long regionId) {
        if(id == null) {
            return Node.findRootNodes(regionId);
        } else {
            return Node.findNodesByParent(Node.findNode(id), regionId);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    @Transactional
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        List<Node> nodeList = new ArrayList<Node>();
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;

            for (Node n : Node.findNodeEntries(firstResult, sizeNo)) {
                if (!n.getLeaf()) {
                    nodeList.add(n);
                }
            }

            float nrOfPages = (float) Node.countNodes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            for (Node n : Node.findAllNodes()) {
                if (!n.getLeaf()) {
                    nodeList.add(n);
                }
            }
        }
        uiModel.addAttribute("nodes", nodeList);
        return "nodes/list";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    @Transactional
    public String create(@Valid Node node, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, node);
            return "nodes/create";
        }
        uiModel.asMap().clear();
        node.persist();
        return "redirect:/nodes/" + encodeUrlPathSegment(node.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    @Transactional
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Node());
        return "nodes/create";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    @Transactional
    public String update(@Valid Node node, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, node);
            return "nodes/update";
        }
        uiModel.asMap().clear();
        node.merge();
        return "redirect:/nodes/" + encodeUrlPathSegment(node.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    @Transactional
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Node.findNode(id));
        return "nodes/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    @Transactional
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Node node = Node.findNode(id);
        node.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/nodes";
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

    void populateEditForm(Model uiModel, Node node) {
        List<Node> nodeList = new ArrayList<Node>();
        for (Node n : Node.findAllNodes()) {
            if (!n.getLeaf()) {
                nodeList.add(n);
            }
        }

        nodeList.remove(node);
        uiModel.addAttribute("node", node);
        uiModel.addAttribute("nodes", nodeList);
        uiModel.addAttribute("categories", Node.categoryList());

    }
}
