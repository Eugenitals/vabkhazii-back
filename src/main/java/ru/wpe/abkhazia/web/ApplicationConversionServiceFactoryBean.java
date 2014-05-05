package ru.wpe.abkhazia.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;
import ru.wpe.abkhazia.domain.GeoPoint;
import ru.wpe.abkhazia.domain.Node;
import ru.wpe.abkhazia.domain.Resource;
import ru.wpe.abkhazia.domain.Rewiev;

/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    public Converter<Rewiev, String> getRewievToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<ru.wpe.abkhazia.domain.Rewiev, java.lang.String>() {
            public String convert(Rewiev rewiev) {
                return new StringBuilder().append(rewiev.getRating()).append(' ').append(rewiev.getTitle()).append(' ').append(rewiev.getPostingDate()).toString();
            }
        };
    }

    public Converter<Resource, String> getResourceToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<ru.wpe.abkhazia.domain.Resource, java.lang.String>() {
            public String convert(Resource resource) {
                return new StringBuilder().append(resource.getFilename()).append(' ').append(resource.getDescription()).append(' ').toString();
            }
        };
    }

    public Converter<Node, String> getNodeToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<ru.wpe.abkhazia.domain.Node, java.lang.String>() {
            public String convert(Node node) {
                return new StringBuilder().append(node.getName()).append(' ').toString();
            }
        };
    }

    public Converter<GeoPoint, String> getGeoPointToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<ru.wpe.abkhazia.domain.GeoPoint, java.lang.String>() {
            public String convert(GeoPoint geoPoint) {
                return new StringBuilder().append(geoPoint.getAddress()).toString();
            }
        };
    }

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
	}
}
