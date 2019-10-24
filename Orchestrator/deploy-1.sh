#!/bin/bash

# ------------------------------------------------------------------------
# Copyright 2018 WSO2, Inc. (http://wso2.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License
# ------------------------------------------------------------------------

ECHO=`which echo`
KUBECTL=`which kubectl`

# methods
function echoBold () {
    ${ECHO} -e $'\e[1m'"${1}"$'\e[0m'
}

function usage () {
    echoBold "This script automates the installation of Stream Processor Fully Distribured Deployment Kubernetes resources\n"
    echoBold "Allowed arguments:\n"
    echoBold "-h | --help"
    echoBold "--wu | --wso2-username\t\tYour WSO2 username"
    echoBold "--wp | --wso2-password\t\tYour WSO2 password"
    echoBold "--cap | --cluster-admin-password\tKubernetes cluster admin password\n\n"
}

WSO2_SUBSCRIPTION_USERNAME=''
WSO2_SUBSCRIPTION_PASSWORD=''
ADMIN_PASSWORD=''

# capture named arguments
while [ "$1" != "" ]; do
    PARAM=`echo $1 | awk -F= '{print $1}'`
    VALUE=`echo $1 | awk -F= '{print $2}'`

    case ${PARAM} in
        -h | --help)
            usage
            exit 1
            ;;
        --wu | --wso2-username)
            WSO2_SUBSCRIPTION_USERNAME=${VALUE}
            ;;
        --wp | --wso2-password)
            WSO2_SUBSCRIPTION_PASSWORD=${VALUE}
            ;;
        --cap | --cluster-admin-password)
            ADMIN_PASSWORD=${VALUE}
            ;;
        *)
            echoBold "ERROR: unknown parameter \"${PARAM}\""
            usage
            exit 1
            ;;
    esac
    shift
done


echo 'Starting to deploy'


# create a new Kubernetes Namespace
kubectl create namespace wso2

# create a new service account in 'wso2' Kubernetes Namespace
kubectl create serviceaccount wso2svcacct -n wso2

# set namespace
kubectl config set-context $(kubectl config current-context) --namespace=wso2


#kubectl create secret docker-registry wso2creds --docker-server=docker.wso2.com --docker-username=chulanilakmalikarandana@gmail.com --docker-password=chul@P292 --docker-email=chulanilakmalikarandana@gmail.com

kubectl create secret docker-registry gcr-json-key --docker-server=https://gcr.io --docker-username=_json_key --docker-password="$(cat /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/wso2-sp-distributed-179caa93fb3d.json)" --docker-email=madushi95lakshini@gmail.com

# create Kubernetes Role and Role Binding necessary for the Kubernetes API requests made from Kubernetes membership scheme
kubectl create --username=admin --password=S8AvQu4BU790bUSs -f /home/user123/Applications/Mavericks_Kubernetes_Client/rbac/rbac.yaml

# volumes
echo 'deploying persistence volumes ...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/volumes/persistent-volumes.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/rdbms/volumes/persistent-volumes.yaml

# Configuration Maps
echo 'deploying config maps ...'
kubectl create configmap sp-manager-bin --from-file=/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/confs/sp-manager/bin/
kubectl create configmap sp-manager-conf --from-file=/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/confs/sp-manager/conf/
kubectl create configmap sp-worker-bin --from-file=/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/confs/sp-worker/bin/
kubectl create configmap sp-worker-conf --from-file=/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/confs/sp-worker/conf/
kubectl create configmap sp-dashboard-conf --from-file=/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/confs/status-dashboard/conf/
kubectl create configmap mysql-dbscripts --from-file=/home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/confs/mysql/dbscripts/

sleep 30s

# databases
echo 'deploying databases ...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/rdbms/mysql/rdbms-persistent-volume-claim.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/rdbms/mysql/rdbms-service.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/rdbms/mysql/rdbms-deployment.yaml

#zookeeper
echo 'deploying Zookeeper ...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/zookeeper/zookeeper-deployment.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/zookeeper/zookeeper-service.yaml

#kafka
echo 'deploying Kafka ...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/kafka/kafka-deployment.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/extras/kafka/kafka-service.yaml

echo 'deploying volume claims...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-mgt-volume-claim.yaml

echo 'deploying Stream Processor manager profile and services...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-manager-1-service.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-manager-2-service.yaml

kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-manager-1-deployment.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-manager-2-deployment.yaml


sleep 30s

echo 'deploying Stream Processor worker profile and services...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-worker-1-service-1.yaml

kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-worker-1-deployment.yaml


echo 'deploying Stream Processor producer profile and services...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-producer-service.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/sp/wso2sp-producer-deployment.yaml

# deploying the ingress resource
echo 'Deploying Ingress...'
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/ingresses/wso2sp-manager-1-ingress.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/ingresses/wso2sp-manager-2-ingress.yaml
kubectl create -f /home/user123/Applications/Mavericks_Kubernetes_Client/pattern-distributed/ingresses/wso2sp-dashboard-ingress.yaml
sleep 20s


kubectl expose deployment wso2sp-worker-1 --type=NodePort --name=wso2sp-worker-1-service-1
NODEPORT1=$(kubectl get -o jsonpath="{.spec.ports[0].nodePort}" services wso2sp-worker-1)
gcloud compute firewall-rules create rule-1 --allow=tcp:$NODEPORT1


echo 'Finished'
