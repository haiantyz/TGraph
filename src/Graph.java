import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by tyz on 2016/11/14.
 *
 * 点的ID从0开始
 */
public class Graph {

    static int verticenumber = 36692;
    static int servernumber = 7;
    static Vertice[] vertices = new Vertice[verticenumber];
    static HashSet<Vertice>[] ServerSet = new HashSet[servernumber];
    static HashSet<Vertice>[] ServerBoundaryVerticeSet = new HashSet[servernumber];
    static HashSet<Vertice>[] ServerMigratedBoundaryVertice = new HashSet[servernumber];
    static HashSet<Vertice>[] hasCommunicatewithMigrateVertice = new HashSet[servernumber];
    //从文件读取数据
    public static StringBuilder readFromFile(String filename) throws IOException {
        StringBuilder s = new StringBuilder();
        BufferedInputStream bufferedinput = null;
        byte[] buffer = new byte[1024];
        try{
            bufferedinput = new BufferedInputStream(new FileInputStream(filename));
            int bytesRead = 0;
            while((bytesRead = bufferedinput.read(buffer))!=-1){
                String chunk = new String(buffer,0,bytesRead);
                s.append(chunk);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedinput != null)
                bufferedinput.close();

        }
        return s;
    }
    //new出这些点的对象
    public static void verticeInitial(int verticenumber){
        for (int i=0;i<verticenumber;i++){
            vertices[i] = new Vertice(i);
        }
    }
    //将读取的数据存到数组中
    public static void getVerticeNeighbor(StringBuilder stringBuilder) throws IOException {
        String[] line = readFromFile(stringBuilder.toString()).toString().split("\n");

        for (int i = 0;i<line.length;i++){
            String[] line33 = line[i].split(" ");
            String[] line22 = line33[1].split("\r");
            int j = Integer.parseInt(line22[0]);
            NeighborVertice neighborVertice = new NeighborVertice(j);
            vertices[Integer.parseInt(line33[0])].neighbor.add(neighborVertice);
        }
    }

    public static void ServerInitial(StringBuilder stringBuilder) throws IOException {
        String[] line = readFromFile(stringBuilder.toString()).toString().split("\n");
        for (int i=0;i<servernumber;i++){
            ServerSet[i] = new HashSet<>();
        }
        for (int i=0;i<line.length;i++){
            ServerSet[Integer.parseInt(line[i])].add(vertices[i]);
            vertices[i].setServerId(Integer.parseInt(line[i]));
        }
    }

//判断某个点是否是边界点
    public static void JudgeIfItisBoundaryVertice(int verticenumber){
        Iterator iterator = vertices[verticenumber].neighbor.iterator();
        while (iterator.hasNext()){
            NeighborVertice n = (NeighborVertice) iterator.next();
            if (vertices[verticenumber].getServerId()==vertices[n.getId()].getServerId())
                continue;
            else
                vertices[verticenumber].setIs_boundaryV(true);
        }
    }
//初始化边界点集合
    public static void ServerBoundaryVerticeSetInitial(){
        for (int i=0;i<servernumber;i++)
            ServerBoundaryVerticeSet[i] = new HashSet<>();
    }
//初始化存储转移点的集合
    public static void ServerMigratedBoundaryVerticeInitial(){
        for (int i=0;i<servernumber;i++) {
            ServerMigratedBoundaryVertice[i] = new HashSet<>();
            hasCommunicatewithMigrateVertice[i] = new HashSet<>();
        }
    }
//让每个SERVER知道自己的哪些点是边界点
    public static void ForEachServerToGetTheirBoundaryVertice(){
        ServerBoundaryVerticeSetInitial();
        for (int i=0;i<servernumber;i++){
            Iterator iterator = ServerSet[i].iterator();
            while (iterator.hasNext()){
                Vertice v = (Vertice) iterator.next();
                JudgeIfItisBoundaryVertice(v.getVerticeid());
                if (v.is_boundaryV())
                    ServerBoundaryVerticeSet[i].add(v);
            }
        }
    }
//计算一个点的gain值并且返回目标partition的ID
    public static int MaxGain(int verticeID){
        int target ;
        int[] communication = new int[servernumber];
        int[] gain = new int[servernumber];
        Iterator iterator = vertices[verticeID].neighbor.iterator();
        while (iterator.hasNext()){
            NeighborVertice neighborVertice = (NeighborVertice) iterator.next();
            communication[vertices[neighborVertice.getId()].getServerId()]++;
            //System.out.println(neighborVertice.getId()+" "+vertices[neighborVertice.getId()].getServerId());
        }

        for (int i=0;i<servernumber;i++){
            gain[i] = communication[i]-communication[vertices[verticeID].getServerId()];
            //System.out.println(gain[i]);
        }
        Arrays.sort(gain);
        for (target = 0; target<servernumber; target++){
            if ((communication[target]-communication[vertices[verticeID].getServerId()])==gain[servernumber-1])
                break;
                //System.out.println(target);
        }
        return target;
    }

    public static void LogicalMigrate(int BoundayaVerticeSetId){
        Iterator iterators = ServerBoundaryVerticeSet[BoundayaVerticeSetId].iterator();
        while (iterators.hasNext()){
            Vertice vertice = (Vertice) iterators.next();
            int initialserverid = vertice.getServerId();
            int initialverticeserverid = vertices[vertice.getVerticeid()].getServerId();
            ServerSet[initialverticeserverid].remove(vertices[vertice.getVerticeid()]);
            ServerSet[MaxGain(vertice.getVerticeid())].add(vertices[vertice.getVerticeid()]);
            vertices[vertice.getVerticeid()].setServerId(MaxGain(vertice.getVerticeid()));
            if (initialserverid!=vertices[vertice.getVerticeid()].getServerId())
                ServerMigratedBoundaryVertice[initialverticeserverid].add(vertice);
        }
    }
//针对第二次转移点的一个迁移方法
    public static void LogicalMigrateII(int BoundayaVerticeSetId){
        Iterator iterators = hasCommunicatewithMigrateVertice[BoundayaVerticeSetId].iterator();
        while (iterators.hasNext()){
            Vertice vertice = (Vertice) iterators.next();
            int initialserverid = vertice.getServerId();
            int initialverticeserverid = vertices[vertice.getVerticeid()].getServerId();
            ServerSet[initialverticeserverid].remove(vertices[vertice.getVerticeid()]);
            ServerSet[MaxGain(vertice.getVerticeid())].add(vertices[vertice.getVerticeid()]);
            vertices[vertice.getVerticeid()].setServerId(MaxGain(vertice.getVerticeid()));
            if (initialserverid!=vertices[vertice.getVerticeid()].getServerId())
                ServerMigratedBoundaryVertice[initialverticeserverid].add(vertice);
        }
    }
//第一次转移点之后找出第二次需要转移的点
    public static void IteratorGetVertice(int Serverid){
        Iterator iterator = ServerMigratedBoundaryVertice[Serverid].iterator();
        while (iterator.hasNext()){
            Vertice vertice = (Vertice) iterator.next();
            Iterator viter = vertice.neighbor.iterator();
            while (viter.hasNext()){
                NeighborVertice nei = (NeighborVertice) viter.next();
                if (vertices[nei.getId()].getServerId()==Serverid && vertices[nei.getId()].is_boundaryV())
                    hasCommunicatewithMigrateVertice[Serverid].add(vertices[nei.getId()]);
            }
        }
    }

    public static void main(String args[]) throws IOException {
        verticeInitial(verticenumber);
        ServerMigratedBoundaryVerticeInitial();
        getVerticeNeighbor(new StringBuilder("/Users/tyz/IdeaProjects/GraphTest/src/email-Enron.txt"));
        ServerInitial(new StringBuilder("/Users/tyz/IdeaProjects/GraphTest/src/email_Enron_metis_format.part.7"));
        ForEachServerToGetTheirBoundaryVertice();
//-------------------------------------------------------------------------------------------------------------------------------------------
  /*
        测试
         */


        /* metis 文件输出*/
//        FileOutputStream fos = new FileOutputStream("/Users/tyz/IdeaProjects/GraphTest/src/metis_file_format");
//        PrintStream printStream = new PrintStream(fos);
//        for (int i=0;i<verticenumber;i++){
//            Iterator iterator = vertices[i].neighbor.iterator();
//            while (iterator.hasNext()){
//                NeighborVertice tempvertice = (NeighborVertice) iterator.next();
//                int vid = tempvertice.getId()+1;
//                printStream.print(vid);
//                printStream.print(" ");
//            }
//            printStream.println();
//        }


//        Iterator iterator = vertices[3].neighbor.iterator();
//        while (iterator.hasNext()){
//            NeighborVertice neighborVertice = (NeighborVertice) iterator.next();
//            System.out.println(neighborVertice.getId());
//        }
//        Iterator iterator = ServerSet[0].iterator();
//        while (iterator.hasNext()){
//            Vertice vertice = (Vertice) iterator.next();
//            System.out.println(vertice.getVerticeid());
////        }
//        System.out.println(vertices[5].getServerId());
//        System.out.println(MaxGain(5));
//        System.out.println(ServerBoundaryVerticeSet[0].size());
//        System.out.println(ServerSet[6].size());
        LogicalMigrate(0);
        System.out.println(ServerSet[0].size());
        ForEachServerToGetTheirBoundaryVertice();
        IteratorGetVertice(0);
        System.out.println(ServerBoundaryVerticeSet[0].size());
        System.out.println(ServerMigratedBoundaryVertice[0].size());
        System.out.println(hasCommunicatewithMigrateVertice[0].size());
        while (true) {
            int j = hasCommunicatewithMigrateVertice[0].size();
            LogicalMigrateII(0);
            ForEachServerToGetTheirBoundaryVertice();
            IteratorGetVertice(0);
            if (j == hasCommunicatewithMigrateVertice[0].size())
                break;
        }
        System.out.println(ServerBoundaryVerticeSet[0].size());
        System.out.println(ServerMigratedBoundaryVertice[0].size());
        System.out.println(hasCommunicatewithMigrateVertice[0].size());
        //System.out.println(ServerBoundaryVerticeSet[0].size());
//        Iterator i = ServerBoundaryVerticeSet[0].iterator();
//        while (i.hasNext()){
//            Vertice v = (Vertice) i.next();
//            System.out.println(v.getVerticeid());
//        }
    }
}
