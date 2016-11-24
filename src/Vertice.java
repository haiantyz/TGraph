import java.util.HashSet;

/**
 * Created by tyz on 2016/11/14.
 */
public class Vertice {

        private int verticeid;
        private String label;
        private int weight;
        public Vertice(int verticeid){
            this.verticeid =verticeid;
        }
        private int ServerId;
        private boolean is_boundaryV = false;
        HashSet<NeighborVertice> neighbor = new HashSet<>();
        public int getVerticeid() {
            return verticeid;
        }

        public void setVerticeid(int verticeid) {
            this.verticeid = verticeid;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getWeight() {
            return label.length();
        }

        public int getServerId() {
            return ServerId;
        }

        public void setServerId(int serverId) {
            ServerId = serverId;
        }

        public boolean is_boundaryV() {
            return is_boundaryV;
        }

        public void setIs_boundaryV(boolean is_boundaryV) {
            this.is_boundaryV = is_boundaryV;
        }


}
