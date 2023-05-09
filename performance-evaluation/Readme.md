# Matching Engine Load Testing Repository

## Locust k8s operator
    helm repo add locust-k8s-operator https://abdelrhmanhamouda.github.io/locust-k8s-operator
    helm install locust-operator locust-k8s-operator/locust-k8s-operator -n demo-exchange

## Bullish Orders test
    locust -f locustfile-bullish.py