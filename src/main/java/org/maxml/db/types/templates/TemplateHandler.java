package org.maxml.db.types.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.Definition;
import org.maxml.db.types.links.GraphGroup;
import org.maxml.db.types.links.GraphReader;
import org.maxml.db.types.links.GraphWriter;
import org.maxml.db.types.links.Link;
import org.maxml.db.types.links.LinkGraph;
import org.maxml.db.types.links.LinkHandler;
import org.maxml.db.types.links.RegistryOfGraphs;
import org.maxml.util.ClassUtils;

public class TemplateHandler {

    private Collection                templates;
    protected LinkHandler             linkHandler;
    protected DBObjectAccessorFactory objectAccessorFactory  = DBObjectAccessorFactory.i();
    protected GraphGroup              templateLinkGraphGroup = null;
    protected RegistryOfGraphs        registryOfGraphs       = RegistryOfGraphs.getInstance();

    private static Object             instance               = null;

    public static TemplateHandler getInstance() {
        return (TemplateHandler) (instance = ClassUtils.i().getSingletonInstance(instance, TemplateHandler.class));
    }

    public TemplateHandler() throws DBException {
        templates = new ArrayList();
        linkHandler = LinkHandler.getInstance();
        templateLinkGraphGroup = new GraphGroup(Template.getTypeId());
    }

    public Template add(LinkGraph templateLinkGraph) throws DBException {

        Template template = new Template(templateLinkGraph);
        getAccessor(LinkGraph.class).save(templateLinkGraph);
        getAccessor(Template.class).save(template);
        objectAccessorFactory.flush();

        addTemplateLinkGraph(template);

        templates.add(template);
        templateLinkGraphGroup.add(templateLinkGraph.createLinkToMe(template), templateLinkGraph);

        return template;
    }

    /*
     * Three possibilites: 1) used only existing refs in modification - in this
     * case we need to check for matches 2) created new refs in modification -
     * matches not possible -
     */
    public Template modify(Template Template, LinkGraph modifiedTemplateLinkGraph, LinkGraph oldTemplateLinkGraph)
            throws DBException {
        if (oldTemplateLinkGraph != null) {
            Link refLink = new Link(Template, oldTemplateLinkGraph.getRootLink());
            linkHandler.delete(refLink, refLink);
            templateLinkGraphGroup.remove(refLink);
            linkHandler.deleteAllLinksToObject(oldTemplateLinkGraph.getRootLink());
        }
        if (modifiedTemplateLinkGraph != null) {

            addTemplateLinkGraph(Template);
            Link refLink = new Link(Template, modifiedTemplateLinkGraph.getRootLink());
            linkHandler.addLink(refLink);
            templateLinkGraphGroup.add(refLink, modifiedTemplateLinkGraph);
        }
        getAccessor(Template.class).update(Template);
        return Template;
    }

    public Template delete(Template Template) throws DBException {
        templates.remove(Template);
        linkHandler.deleteSubMatches(Template.getGraph().getRootLink());
        getAccessor(Link.class).delete(Template.getGraph().getRootLink());
        getAccessor(Template.class).delete(Template);
        return Template;
    }

    public void checkUpdate(Object updatedObject, Collection Templates) throws DBException {

    }

    public void addLinksToMatchingTemplates(Object target, LinkGraph linkGraph) throws DBException {
        Collection TemplatesMatching = templateLinkGraphGroup.getSuperSets(linkGraph, true);
        linkHandler.addLinksFromObject(target, extractRootLinks(TemplatesMatching));
    }

    public void deleteLinksToMatchingTemplates(Object target, LinkGraph linkGraph) throws DBException {
        Collection TemplatesMatching = templateLinkGraphGroup.getSuperSets(linkGraph, true);
        linkHandler.deleteLinksFromObject(target, extractRootLinks(TemplatesMatching));
    }

    public void checkUpdate(Object updatedObject, LinkGraph oldLinkGraph, LinkGraph newLinkGraph) throws DBException {
        deleteLinksToMatchingTemplates(updatedObject, oldLinkGraph);
        addLinksToMatchingTemplates(updatedObject, newLinkGraph);
    }

    /*
     * 1) add Template hierachy links 2) add link from Template to root of
     * hierarchy 3) add links from all matches to Template
     */
    public void addTemplateLinkGraph(Template Template) throws DBException {

        Collection matchingGraphRefs = getMatchingGraphs(Template);
        linkHandler.addLinksToObjectFromReferrers(Template.getGraph().getRootLink(), matchingGraphRefs);

    }

    public Collection getMatchingGraphs(Template Template) {
        Collection matchingGraphRefs = registryOfGraphs.getSuperSets(Template.getGraph(), true, true);
        return linkHandler.filterOutItemsReferredToByType(matchingGraphRefs, Template.getTypeId());
    }

    public DBObjectAccessor getAccessor(Class objectClass) {
        return objectAccessorFactory.getAccessor(objectClass);
    }

    private Collection extractRootLinks(Collection linkGraphs) {
        return extractRootLinks(linkGraphs, false);
    }

    private Collection extractRootLinks(Collection<LinkGraph> linkGraphs, boolean filterOutTemplatesRootLinks) {
        List rootLinks = new ArrayList();
        for ( LinkGraph linkGraph: linkGraphs ) {

            if (filterOutTemplatesRootLinks && linkGraph.getRootLink().getReferrerType().equals(Template.getTypeId())) {
                continue;
            }
            rootLinks.add(linkGraph.getRootLink());
        }
        return rootLinks;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

//        TemplateHandler ph = TemplateHandler.getInstance();
//        Metadata metadata = new Metadata("bigstuff");
//
//        ArrayList al = new ArrayList();
//        al.add(new AttributeDef("attr.type.1"));
//        al.add(new AttributeDef("attr.type.2"));
//        al.add(new AttributeDef("attr.type.3"));
//
//        ArrayList al2 = new ArrayList();
//        al2.add(new AttributeValue((Definition) al.get(0), "attr.value.1"));
//        al2.add(new AttributeValue((Definition) al.get(1), "attr.value.2"));
//        al2.add(new AttributeValue((Definition) al.get(2), "attr.value.3"));
//        al2.add(new AttributeValue((Definition) al.get(0), "attr.value.4"));
//        al2.add(new AttributeValue((Definition) al.get(1), "attr.value.5"));
//        al2.add(new AttributeValue((Definition) al.get(2), "attr.value.6"));
//        al2.add(new AttributeValue((Definition) al.get(0), "attr.value.7"));
//        al2.add(new AttributeValue((Definition) al.get(1), "attr.value.8"));
//        al2.add(new AttributeValue((Definition) al.get(2), "attr.value.9"));
//
//        ArrayList al3 = new ArrayList();
//        al3.add(new AttributeValue((Definition) al.get(0), "attr.value.01"));
//        al3.add(new AttributeValue((Definition) al.get(1), "attr.value.02"));
//        al3.add(new AttributeValue((Definition) al.get(2), "attr.value.03"));
//        al3.add(new AttributeValue((Definition) al.get(0), "attr.value.04"));
//        al3.add(new AttributeValue((Definition) al.get(1), "attr.value.05"));
//        al3.add(new AttributeValue((Definition) al.get(2), "attr.value.06"));
//        al3.add(new AttributeValue((Definition) al.get(0), "attr.value.07"));
//        al3.add(new AttributeValue((Definition) al.get(1), "attr.value.08"));
//        al3.add(new AttributeValue((Definition) al.get(2), "attr.value.09"));
//
//        ArrayList al4 = new ArrayList();
//        al4.add(new AttributeValue((Definition) al.get(0), "attr.value.11"));
//        al4.add(new AttributeValue((Definition) al.get(1), "attr.value.12"));
//        al4.add(new AttributeValue((Definition) al.get(2), "attr.value.13"));
//        al4.add(new AttributeValue((Definition) al.get(0), "attr.value.14"));
//        al4.add(new AttributeValue((Definition) al.get(1), "attr.value.15"));
//        al4.add(new AttributeValue((Definition) al.get(2), "attr.value.16"));
//        al4.add(new AttributeValue((Definition) al.get(0), "attr.value.17"));
//        al4.add(new AttributeValue((Definition) al.get(1), "attr.value.18"));
//        al4.add(new AttributeValue((Definition) al.get(2), "attr.value.19"));
//
//        try {
//            if (false) {
//                try {
//
//                    Template p = (Template) ph.getAccessor(Template.class).find(1);
//                    LinkGraph lh1 = GraphReader.read((LinkGraph) ph.getAccessor(LinkGraph.class).find(4));
//                    LinkGraph lh2 = GraphReader.read((LinkGraph) ph.getAccessor(LinkGraph.class).find(8));
//
//                    ph.add(lh1);
//                    ph.add(lh2);
//
//                    // ((Link)((List)lh1.getFirstSubLinkGraph().getFirstSubLinkGraph().getChildren()).get(3)).setReferentId(393);
//                    //                    
//                    // ph.modify(p, lh1, lh2);
//
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            } else {
//
//                LinkGraph lh1 = MetadataHandler.getInstance().createMetadata(metadata, al, al2);
//                LinkGraph lh2 = MetadataHandler.getInstance().createMetadata(metadata, al, al3);
//                LinkGraph lh3 = MetadataHandler.getInstance().createMetadata(metadata, al, al4);
//                LinkGraph lh4 = MetadataHandler.getInstance().createMetadata(metadata, al, al4);
//
//                lh1.addSubLinkGraph(lh2);
//
//                lh2.addSubLinkGraph(lh3);
//
//                GraphWriter.write(lh1);
//                lh1.createLinkToMe(metadata);
//                DBObjectAccessorFactory.getInstance().endSession();
//
//                //
//                ((List) lh3.getChildren()).remove(7);
//                ((List) lh3.getChildren()).remove(5);
//                ((List) lh3.getChildren()).remove(3);
//                ((List) lh3.getChildren()).remove(1);
//
//                GraphWriter.write(lh1);
//                lh1.createLinkToMe(metadata);
//                // ph.add(lh1);
//                DBObjectAccessorFactory.getInstance().endSession();
//
//                lh2.removeSubLinkGraph(lh3);
//                ((List) lh4.getChildren()).remove(6);
//                ((List) lh4.getChildren()).remove(4);
//                ((List) lh4.getChildren()).remove(2);
//                ((List) lh4.getChildren()).remove(0);
//                lh2.addSubLinkGraph(lh4);
//
//                GraphWriter.write(lh1);
//                lh1.createLinkToMe(metadata);
//                // ph.add(lh1);
//
//            }
//
//            DBObjectAccessorFactory.getInstance().endSession();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
