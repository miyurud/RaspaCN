import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Reader {
    public static String COMMA_DELIMITER = ",";
    static double maximum_throughput = 0;
    static int maximum_index = -1;
    //read the number of  partial siddhi apps from configuration files
    static int no_of_partial_siddhi_apps = 9;
    static int no_of_nodes = 4;
    static String worker1 = "wso2sp-worker-1";
    static String worker2 = "wso2sp-worker-2";
    static String worker3 = "wso2sp-worker-3";
    static String worker4 = "wso2sp-worker-4";
    static String worker5 = "wso2sp-worker-5";
    static String worker6 = "wso2sp-worker-6";
    static String worker7 = "wso2sp-worker-7";
    static String worker8 = "wso2sp-worker-8";
    static String worker9 = "wso2sp-worker-9";
    static String worker10 = "wso2sp-worker-10";
    static String worker11 = "wso2sp-worker-11";


    public static void main(String args[]) {

            // read the ML predictions
        try {
            List<List<String>> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("./Sample_Output.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(COMMA_DELIMITER);
                    records.add(Arrays.asList(values));
                }
            }

            for (int i = 1; i < records.size(); i++) {
                double throughput = Double.parseDouble(records.get(i).get(6));
                if (maximum_throughput < throughput) {
                    maximum_throughput = throughput;
                    maximum_index = i;
                }

            }

            System.out.println("Maximum Throughput : " + maximum_throughput);
            System.out.println("No of workers : " + records.get(maximum_index).get(1));
            System.out.println("No of Partial Siddhi Apps: " + records.get(maximum_index).get(2));


            // run the deploy.sh files

            ProcessBuilder pb = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/auto_deploy.sh",records.get(maximum_index).get(1));
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


            // sleep for some time till the deployment process completes(1 min)

            Thread.sleep(600000);

            // while true loop for continuously check whether their are empty workers

            while (true) {
                // check whether all the partial siddhi apps are deployed

//                ProcessBuilder pb_new = new ProcessBuilder("bash", "/home/winma/Documents/BashClient/ML/PredictionsReader/src/observe_siddhi_apps_are_deployed.sh", records.get(maximum_index).get(1));
//                Process p_new = pb_new.start();
//                BufferedReader reader_new = new BufferedReader(new InputStreamReader(p_new.getInputStream()));
//                String line_new ;
//                String app_count = null;
//                while ((line_new = reader_new.readLine()) != null) {
//                    if (line_new != null){
//                        app_count = line_new;
//                    }
//
//                }

//                System.out.println("Thread Sleeping ................");
//                int siddhi_apps_count = 0;
//                siddhi_apps_count = Integer.parseInt(app_count);

                    // call the undeploy script
                    System.out.println("Calling the undeploy file .................");
                    ProcessBuilder pb_undeploy = null;
            if ((records.get(maximum_index).get(1)).equals("1")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1);

            }
            else if ((records.get(maximum_index).get(1)).equals("2")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker4);

            }
            else if ((records.get(maximum_index).get(1)).equals("3")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker4);

            }
            else if ((records.get(maximum_index).get(1)).equals("4")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker4, worker5);

            }
            else if ((records.get(maximum_index).get(1)).equals("5")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker3, worker4, worker5);

            }
            else if ((records.get(maximum_index).get(1)).equals("6")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker3, worker4, worker5, worker6);

            }
            else if ((records.get(maximum_index).get(1)).equals("7")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker3, worker4, worker5, worker6, worker7);

            }
            else if ((records.get(maximum_index).get(1)).equals("8")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker3, worker4, worker5, worker6,worker7,worker10);

            }
            else if ((records.get(maximum_index).get(1)).equals("9")) {
                pb_undeploy = new ProcessBuilder("bash", "/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/scripts/observe-undeploy.sh",
                        Integer.toString(no_of_nodes), records.get(maximum_index).get(1), worker1, worker2, worker3, worker4, worker5, worker6,worker7, worker8, worker10);

            }
            //pb_undeploy.start();
            Process pb_un = pb_undeploy.start();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(pb_un.getInputStream()));
            String line2 = null;
            while ((line2 = reader2.readLine()) != null) {
                System.out.println(line2);
            }

            Thread.sleep(300000);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
