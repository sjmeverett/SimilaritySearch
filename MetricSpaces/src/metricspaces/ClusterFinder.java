package metricspaces;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author stewart
 */
public class ClusterFinder<ObjectType> {
    private Map<ObjectType, Set<ObjectType>> objectClusterMap;


    public ClusterFinder() {
        objectClusterMap = new HashMap<ObjectType, Set<ObjectType>>();
    }


    /**
     * Adds a pair representing a link between object1 and object2 into the index.
     *
     * @param object1
     * @param object2
     */
    public void addPair(ObjectType object1, ObjectType object2) {
        Set<ObjectType> cluster1 = objectClusterMap.get(object1);
        Set<ObjectType> cluster2 = objectClusterMap.get(object2);

        if (cluster1 == null && cluster2 == null) {
            //create a new cluster to contain both images
            createCluster(object1, object2);
        }
        else if (cluster1 != null && cluster2 == null) {
            //put object2 into object1's cluster
            addImageToCluster(object2, cluster1);
        }
        else if (cluster1 == null && cluster2 != null) {
            //put object1 into object2's cluster
            addImageToCluster(object1, cluster2);
        }
        else if (!cluster1.equals(cluster2)) {
            //images are in different clusters
            //merge the clusters
            mergeClusters(cluster1, cluster2);
        }
    }


    private void createCluster(ObjectType object1, ObjectType object2) {
        Set<ObjectType> cluster = new HashSet<ObjectType>();
        cluster.add(object1);
        cluster.add(object2);

        objectClusterMap.put(object1, cluster);
        objectClusterMap.put(object2, cluster);
    }


    private void addImageToCluster(ObjectType object, Set<ObjectType> cluster) {
        cluster.add(object);
        objectClusterMap.put(object, cluster);
    }


    private void mergeClusters(Set<ObjectType> cluster1, Set<ObjectType> cluster2) {
        //merge the smaller cluster into the bigger one
        if (cluster1.size() > cluster2.size()) {
            _mergeClusters(cluster1, cluster2);
        }
        else {
            _mergeClusters(cluster2, cluster1);
        }
    }


    private void _mergeClusters(Set<ObjectType> cluster1, Set<ObjectType> cluster2) {
        //add everything in cluster 2 into cluster 1
        cluster1.addAll(cluster2);

        //set all the images in cluster2 to have cluster1 as their cluster
        for (ObjectType i: cluster2) {
            objectClusterMap.put(i, cluster1);
        }
    }


    /**
     * Gets the list of clusters found so far.
     *
     * @return
     */
    public Collection<Set<ObjectType>> getClusters() {
        return new HashSet<Set<ObjectType>>(objectClusterMap.values());
    }


    public Map<ObjectType, Set<ObjectType>> getObjectsClusters() {
        return objectClusterMap;
    }

    public Set<ObjectType> getObjects() {
        return objectClusterMap.keySet();
    }
}