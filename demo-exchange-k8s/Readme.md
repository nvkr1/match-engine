# Demo Exchange K8S Repository


## K8S Dashboard
###  Installation
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
### Open Dashboard
    http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy

## EKS Token
    aws eks get-token --cluster-name demo-exchange-cluster

## Metric Server
### Installation
    kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

## Locust Kubernetes Operator⚓
[https://abdelrhmanhamouda.github.io/locust-k8s-operator/]()

### Installation
    helm repo add locust-k8s-operator https://abdelrhmanhamouda.github.io/locust-k8s-operator/

### Demo Stress Test
    kubectl apply -f ./stress-test/locust-test-btc-usdt.yml