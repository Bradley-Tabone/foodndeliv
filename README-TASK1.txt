README-TASK1

1. Ensure docker desktop is running.

2. Run the following commands. 

bash rundb.sh

export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ordersdb
export DB_USER=myadmin
export DB_PASSWORD=mypass

3. Run the app with this command.
mvn spring-boot:run

AN EXAMPLE TEST FLOW

1. Create a rider

RID=$(curl -s -X POST http://localhost:8080/api/riders \
  -H "Content-Type: application/json" \
  -d "{ \"fullName\":\"Test Rider $(date +%s)\", \"phone\":\"+356 $(date +%s)\" }" \
  | jq -r '.id')

echo "Rider ID = $RID"


2. Create a restaurant

RST=$(curl -si -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -d "{ \"name\":\"Test Resto $(date +%s)\", \"address\":\"123 Main St\", \"state\":\"OPEN\" }" \
  | awk -F/ '/^Location:/ {print $NF}' | tr -d '\r')

echo "Restaurant ID = $RST"

3. Create a Customer

CUST=$(curl -si -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d "{ \"name\":\"Customer $(date +%s)\", \"email\":\"cust$(date +%s)@ex.com\", \"state\":\"ACTIVE\" }" \
  | awk -F/ '/^Location:/ {print $NF}' | tr -d '\r')

echo "Customer ID = $CUST"

4. Create an Order

ORD=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d "{
        \"customerId\": $CUST,
        \"restaurantId\": $RST,
        \"orderDetails\": \"Sushi Set x1\",
        \"state\": \"OPEN\",
        \"orderLines\": [ { \"productName\": \"Sushi Set\", \"quantity\": 1, \"price\": 12.90 } ]
      }" | jq -r '.id')

echo "Order ID = $ORD"

5. Create a Delivery

DEL=$(curl -s -X POST http://localhost:8080/api/deliveries \
  -H "Content-Type: application/json" \
  -d "{ \"orderRef\":\"ORD-$(date +%s)\", \"fee\": 4.50 }" \
  | jq -r '.id')

echo "Delivery ID = $DEL"

6. Assign the created Rider to the created delivery.

curl -i -X PATCH "http://localhost:8080/api/deliveries/$DEL/assign" \
  -H "Content-Type: application/json" \
  -d "{ \"riderId\": $RID, \"orderId\": $ORD }"

7. Mark Delivery as Delivered.

curl -i -X PATCH "http://localhost:8080/api/deliveries/$DEL/status" \
  -H "Content-Type: application/json" \
  -d '{ "status":"DELIVERED" }'

