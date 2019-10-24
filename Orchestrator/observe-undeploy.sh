#!/bin/bash
CT="Content-Type:application/json"

echo "Running Auto Undeployment**********"
workers_list=()
#get inputs from args
# store arguments in a special array 
args=( "$@" ) 
# get number of elements 
ELEMENTS=${#args[@]} 
 
# echo each element in array  
nodes_number=${args[0]}

partial_siddhi_apps_number=${args[1]}

# for loop 
for (( i=2;i<$ELEMENTS;i++)); do 
    workers_list+=(${args[${i}]})
    echo ${args[${i}]} 
done



#takes the ip list using
#nodes_number=4
max_num=$(($nodes_number * 5 + 1))
echo $max_num

should_undeploy=()
nodes_list_raw=$( kubectl get nodes )
nodes_list=${nodes_list_raw[0]}
echo $nodes_list;

nodes=()

for ((iter=6; iter<=$max_num; iter=iter+5)) do
    echo ${nodes_list[$iter]}
    SUB=$(echo ${nodes_list[0]}| cut -d' ' -f $iter)
    echo $SUB;
    nodes+=($SUB)
    echo "<<<<<<<<<<<<<<<"
    echo ${nodes[$iter]}
    echo ">>>>>>>>>>>>>>>"
done

echo ${nodes[2]}
echo "${#nodes[@]}"

ip_list=$( kubectl get nodes --selector=kubernetes.io/role!=master -o jsonpath={.items[*].status.addresses[?\(@.type==\"ExternalIP\"\)].address} )
echo $ip_list

SERVICE="-service-1"

no_of_partial_siddhi_apps=0

for u in "${workers_list[@]}"  
do  
    MY_SERVICE="$u$SERVICE"
    echo $MY_SERVICE;
    #kubectl expose deployment $u --type=NodePort --name=$MY_SERVICE
    NODEPORT=$(kubectl get -o jsonpath="{.spec.ports[0].nodePort}" services $u)
    echo $NODEPORT;
    #$RULE=$u
    #gcloud compute firewall-rules create rule-1 --allow=tcp:$NODEPORT
    
    MY_NODE_RAW=$(kubectl get pod -l app=$u -o=custom-columns=NODE:.spec.nodeName)
    MY_NODE=$(echo ${MY_NODE_RAW[0]}| cut -d' ' -f 2)
    echo $MY_NODE;
    
    for ((i=0; i<=${#nodes[@]}; i++))
    do
    	NODE_NAME=${nodes[$i]}
    	echo $NODE_NAME;
    	if [[ "${NODE_NAME}" = "${MY_NODE}" ]]; then
        	echo "${i}";
                IP_INDEX=${i}
                echo "mtched...."
    	fi
    	
    done
    
    NODE_IP_INDEX=$(($IP_INDEX + 1))
    NODE_IP=$(echo ${ip_list[0]}| cut -d' ' -f $NODE_IP_INDEX)
    echo "############"
    echo $NODE_IP
    
    TEST="curl -k -X GET https://$NODE_IP:$NODEPORT/siddhi-apps?isActive=true  -H accept:application/json -u admin:admin -k"
    RESPONSE=`$TEST`
    LEN=${#RESPONSE[0]}
    echo "@@@@@@@@@@@@@@@@@"
    echo $LEN

    if (($LEN > 2));
    then
      echo "There are Partial Siddhi Apps in the worker $u";
     
    else
      echo "There are no Partial Siddhi Apps in the worker $u";
      should_undeploy+=($u)
      #kubectl delete -l mylabel=$u
    fi

    my_string=$RESPONSE
    my_string="${my_string:1}"
    my_string="${my_string::-1}"  
    #echo $my_string

    my_array=($(echo $my_string | tr "," "\n"))

    #Print the split string
    for i in "${my_array[@]}"
    do
       echo $i
       no_of_partial_siddhi_apps=$((no_of_partial_siddhi_apps+1))
    done
    #gcloud compute firewall-rules delete rule-1
done  
echo "The workers that needed to be undeployed.............."
echo "${should_undeploy[*]}"

echo $partial_siddhi_apps_number
echo $no_of_partial_siddhi_apps
if [[ "$partial_siddhi_apps_number" = "$no_of_partial_siddhi_apps" ]];
then
    for w in "${should_undeploy[@]}"  
    do
        kubectl delete deployment -l app=$w
        kubectl delete pod -l app=$w
    done
else
    echo "Siddhi app deployment is not yet completed...Try Again Later";
fi
