package org.maxml.db.types.profiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.Definition;
import org.maxml.db.types.dCRUD;
import org.maxml.db.types.links.ExtLink;
import org.maxml.db.types.links.GraphGroup;
import org.maxml.db.types.links.GraphReader;
import org.maxml.db.types.links.GraphWriter;
import org.maxml.db.types.links.Link;
import org.maxml.db.types.links.LinkGraph;
import org.maxml.db.types.links.LinkHandler;
import org.maxml.db.types.links.RegistryOfGraphs;
import org.maxml.dispatch.Caller;
import org.maxml.dispatch.cContains;
import org.maxml.util.ClassUtils;

public class dProfile extends dCRUD {

    private Collection         profiles;
    protected LinkHandler      linkHandler;
    protected GraphGroup       profileLinkGraphGroup = null;
    protected RegistryOfGraphs registryOfGraphs      = RegistryOfGraphs.getInstance();

    private static Object      instance              = null;

    public static dProfile getInstance() {
        return (dProfile) (instance = ClassUtils.i().getSingletonInstance(instance, dProfile.class));
    }

    public dProfile() throws DBException {
        super();
        profiles = new ArrayList();
        linkHandler = LinkHandler.getInstance();
        profileLinkGraphGroup = new GraphGroup(Profile.getTypeId());
    }

    public Profile add(LinkGraph profileLinkGraph) throws DBException {
        start();

        Profile profile = new Profile(profileLinkGraph);
        getAccessor(LinkGraph.class).save(profileLinkGraph);
        getAccessor(Profile.class).save(profile);
        objectAccessorFactory.flush();

        profiles.add(profile);
        profileLinkGraphGroup.add(profileLinkGraph.createLinkToMe(profile), profileLinkGraph);

        stop("add");
        return profile;
    }

    public Profile delete(Profile profile) throws DBException {
        start();
        profiles.remove(profile);
        linkHandler.deleteGraph(profile.getProfileGraph().getRootLink());

        getAccessor(Link.class).delete(profile.getProfileGraph().getRootLink());
        getAccessor(Profile.class).delete(profile);

        stop("delete");
        return profile;
    }
    
    public LinkGraph getProfileGraph(Link extLink) {
        return (LinkGraph) profileLinkGraphGroup.findGraphByReferent(extLink.getReferentId(), Link.getTypeId()); 
    }

    public void checkUpdate(Object updatedObject, Collection profiles) throws DBException {

    }

    public void addLinksToMatchingProfiles(Object target, LinkGraph linkGraph) throws DBException {
        Collection profilesMatching = profileLinkGraphGroup.getSuperSets(linkGraph, true);
        linkHandler.addLinksFromObject(target, extractRootLinks(profilesMatching));
    }

    public void deleteLinksToMatchingProfiles(Object target, LinkGraph linkGraph) throws DBException {
        Collection profilesMatching = profileLinkGraphGroup.getSuperSets(linkGraph, true);
        linkHandler.deleteLinksFromObject(target, extractRootLinks(profilesMatching));
    }

    public void checkUpdate(Object updatedObject, LinkGraph oldLinkGraph, LinkGraph newLinkGraph) throws DBException {
        deleteLinksToMatchingProfiles(updatedObject, oldLinkGraph);
        addLinksToMatchingProfiles(updatedObject, newLinkGraph);
    }

    /*
     * 1) add profile hierachy links 2) add link from profile to root of
     * hierarchy 3) add links from all matches to profile
     */
    public void addProfileLinkGraph(Profile profile) throws DBException {

        Collection matchingGraphRefs = getMatchingGraphs(profile);
        linkHandler.addLinksToObjectFromReferrers(profile.getProfileGraph().getRootLink(), matchingGraphRefs);

    }

    public Collection getMatchingGraphs(Profile profile) {
        Collection matchingGraphRefs = registryOfGraphs.getSuperSets(profile.getProfileGraph(), true, true);
        return linkHandler.filterOutItemsReferredToByType(matchingGraphRefs, Profile.getTypeId());
    }

    private Collection extractRootLinks(Collection linkGraphs) {
        return extractRootLinks(linkGraphs, false);
    }

    private Collection extractRootLinks(Collection<LinkGraph> linkGraphs, boolean filterOutProfilesRootLinks) {
        List rootLinks = new ArrayList();
        for ( LinkGraph linkGraph: linkGraphs ) {

            if (filterOutProfilesRootLinks && linkGraph.getRootLink().getReferrerType().equals(Profile.getTypeId())) {
                continue;
            }
            rootLinks.add(linkGraph.getRootLink());
        }
        return rootLinks;
    }

    public static class DOIT {
        public void d1(Object[] o) {
            System.out.println("wadidya say mista mogowotz?");
        }

        public void d2(Object[] o) {
            System.out.println("nonayer buziwitz you lolo flopter macknz");
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

//        dProfile ph = dProfile.getInstance();
//        Metadata metadata = new Metadata("bigstuff");
//
//        ArrayList al = new ArrayList();
//        al.add(new AttributeDef("attr.type.1"));
//        al.add(new AttributeDef("attr.type.2"));
//        al.add(new AttributeDef("attr.type.3"));

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
//
//                try {
//                    Profile p = (Profile) ph.getAccessor(Profile.class).find(1);
//                    //LinkGraph lh1 = GraphReader.read((LinkGraph) ph.getAccessor(LinkGraph.class).find(1));
//                    LinkGraph lh2 = GraphReader.read((LinkGraph) ph.getAccessor(LinkGraph.class).find(2));
//                    System.out.println("fromdadb___________________>");
//                    lh2.printTree("");
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//
////                ph.addCaller(cContains.contains(7, 1), new Caller(new DOIT(), "d1"));
////                ph.addCaller(cContains.contains(16, 1), new Caller(new DOIT(), "d2"));
//
//                LinkGraph lh1 = MetadataHandler.getInstance().createMetadata(metadata, al, al2);
//                LinkGraph lh2 = MetadataHandler.getInstance().createMetadata(metadata, al, al3);
//                LinkGraph lh3 = MetadataHandler.getInstance().createMetadata(metadata, al, al4);
//                LinkGraph lh4 = MetadataHandler.getInstance().createMetadata(metadata, al, al4);
//                
//                GraphWriter.write(lh1);
//                ph.add(lh1);
//                System.out.println("lh1: ->" );
//                lh1.printTree("");
//
//                
//                LinkGraph extg = lh2.addSubExtLinkGraph();
//                extg.addP2PLink(lh1.getRootLink());
//
//                
//                Link l1 = (Link)((List) lh1.getChildren()).get(3);
//                LinkGraph lh5 = extg.addP2STLink(l1);
//                lh5.addChildLink((Link)((List) lh4.getChildren()).get(3));
//                lh5.addChildLink((Link)((List) lh4.getChildren()).get(5));
//                lh5.addChildLink((Link)((List) lh4.getChildren()).get(7));
//                
//                System.out.println("lh2 before: ->" );
//                lh2.printTree("");
//                GraphWriter.write(lh2);
//
//                lh2.expandExtLink(extg);
//                
//                System.out.println("lh2: ->" );
//                lh2.printTree("");
//
////                lh1.addSubLinkGraph(lh2);
////                lh2.addSubLinkGraph(lh3);
//
//                if (false) {
//                    lh1.createLinkToMe(metadata);
//                    DBObjectAccessorFactory.getInstance().endSession();
//
//                    ((List) lh3.getChildren()).remove(7);
//                    ((List) lh3.getChildren()).remove(5);
//                    ((List) lh3.getChildren()).remove(3);
//                    ((List) lh3.getChildren()).remove(1);
//
//                    GraphWriter.write(lh1);
//                    lh1.createLinkToMe(metadata);
//                    DBObjectAccessorFactory.getInstance().endSession();
//
//                    lh2.removeSubLinkGraph(lh3);
//                    ((List) lh4.getChildren()).remove(6);
//                    ((List) lh4.getChildren()).remove(4);
//                    ((List) lh4.getChildren()).remove(2);
//                    ((List) lh4.getChildren()).remove(0);
//                    lh2.addSubLinkGraph(lh4);
//
//                    GraphWriter.write(lh1);
//                    lh1.createLinkToMe(metadata);
//                    ph.add(lh1);
//                }
//
//            }
//
//            DBObjectAccessorFactory.getInstance().endSession();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
