README-task2

1. Start minikube

minikube start

minikube tunnel

2. 
kubectl -n fnd apply -f k8s/postgres-deployment.yaml
kubectl -n fnd apply -f k8s/foodndeliv.yaml
kubectl -n fnd apply -f k8s/keycloak-deployment.yaml
kubectl -n fnd apply -f k8s/keycloak-service.yaml

3. Create a ConfigMap from the krakend.json file.
kubectl -n fnd create configmap krakend-config \
  --from-file=krakend.json=krakend.json
kubectl -n fnd apply -f k8s/krakend-deployment.yaml
kubectl -n fnd apply -f k8s/krakend-service.yaml


4. KEYCLOAK SETUP
4a.Create realm
	Name: fnd

4b. Create client

	Client ID: foodndeliv-api

	Client protocol: openid-connect

	Access type: confidential

4c. Create roles
	admin
	customer
	rider

4d. Create test users and assign them any one of the created roles. If you want to use the test script I suggest creating the following users for ease of use.

User 1: username=bradley
	password=bradley
	Roles = customer

User 2: username=adminuser
	password=admin
	Roles = admin

User 3: username=rider1
	password=rider
	Roles = rider
5. To run the test file use the following commands. Assuming everything is setup.

ADMIN_TOKEN=$(curl -s -X POST "http://127.0.0.1:80/realms/fnd/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=foodndeliv-api" \
  -d "client_secret=8xG5hK4y4UnhN5O7NwlboqvwPBtIlg3H" \
  -d "username=adminuser" \
  -d "password=admin" \
  -d "grant_type=password" | jq -r '.access_token')

RIDER_TOKEN=$(curl -s -X POST "http://127.0.0.1:80/realms/fnd/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=foodndeliv-api" \
  -d "client_secret=8xG5hK4y4UnhN5O7NwlboqvwPBtIlg3H" \
  -d "username=rider1" \
  -d "password=riderpass" \
  -d "grant_type=password" | jq -r '.access_token')

CUSTOMER_RESPONSE=$(curl -s -X POST "http://127.0.0.1:80/realms/fnd/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=foodndeliv-api" \
  -d "client_secret=8xG5hK4y4UnhN5O7NwlboqvwPBtIlg3H" \
  -d "username=bradley" \
  -d "password=bradley" \
  -d "grant_type=password")

export CUSTOMER_TOKEN=$(echo $CUSTOMER_RESPONSE | jq -r '.access_token')
export CUSTOMER_REFRESH=$(echo $CUSTOMER_RESPONSE | jq -r '.refresh_token')

artillery run tests/rbac-test.yml

