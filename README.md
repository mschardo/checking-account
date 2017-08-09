# Balance Exercise - Simple API with basic features of checking accounts


## Features

The API has four main features (__adding operations__, __getting current balance__, __getting statement__, __getting debt periods__) and one aditional feature (__creating accounts__) for several accounts simulation.

## Usage
### Running
There are three ways for runing the API:
#### Run the application locally

`lein ring server`

#### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

#### Packaging as war

`lein ring uberwar`

### Using
After the server has started, you can use the API. Use GET request for creating an account and POST requests for all the other features. Here's an example using the API with CURL from the terminal:

#### Creating an account
GET request:
```sh
curl -X GET --header 'Accept: application/json' 'http://localhost:3000/api/create'
```
Response JSON showing new account''s number:
```json
{"acc_num":1}
```
#### Adding operations
POST request:
```sh
# Add a Deposit of $1000.00 in 15/10/2017 to account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"type": "debt", "acc_num": 1, "desc": "Deposit", "value": 1000.00, "date": { "day": 15, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/operation'

# Add a Purchase on Amazon of $3.34 in 16/10/2017 to account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"type": "credit", "acc_num": 1, "desc": "Purchase on Amazon", "value": 3.34, "date": { "day": 16, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/operation'

# Add a Purchase on Uber of $45.23 in 16/10/2017 to account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"type": "credit", "acc_num": 1, "desc": "Purchase on Uber", "value": 45.23, "date": { "day": 16, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/operation'

# Add a Withdrawal of $180.00 in 17/10/2017 to account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"type": "credit", "acc_num": 1, "desc": "Withdrawal", "value": 180.00, "date": { "day": 17, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/operation'
```
Response JSON showing the status of the addition:
```json
{"status":"Success"}
{"status":"Success"}
{"status":"Success"}
{"status":"Success"}
```

#### Getting balance
POST request:
```sh
# Get balance of account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "acc_num": 1 }' 'http://localhost:3000/api/balance'
```
Response JSON showing account''s balance:
```json
{"balance":771.43}
```

#### Getting statement
POST request:
```sh
# Get statement from 14/10/2017 to 18/10/2017 of account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"acc_num": 1, "initial_date": { "day": 14, "month": 10, "year": 2017 }, "final_date": { "day": 18, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/statement'
```
Response JSON showing account''s statement:
```json
{"response":[{"date":"2017-10-15T03:00:00.000Z","statement":[{"desc":"Deposit","value":1000.0}],"balance":1000.0},{"date":"2017-10-16T02:00:00.000Z","statement":[{"desc":"Purchase on Amazon","value":-3.34},{"desc":"Purchase on Uber","value":-45.23}],"balance":951.43},{"date":"2017-10-17T02:00:00.000Z","statement":[{"desc":"Withdrawal","value":-180.0}],"balance":771.43}]}
```

#### Getting debt periods
POST request:
```sh
# Add operation to start debt period (Purchase of a flight ticket of $1050.00 in 18/10/2017) to account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"type": "credit", "acc_num": 1, "desc": "Purchase of a flight ticket", "value": 1050.00, "date": { "day": 18, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/operation'

# Add operation to end debt period (Deposit of $100.00 in 25/10/2017) to account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"type": "debt", "acc_num": 1, "desc": "Deposit", "value": 100.00, "date": { "day": 25, "month": 10, "year": 2017 } }' 'http://localhost:3000/api/operation'

# Get debt period of account 1
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "acc_num": 1 }' 'http://localhost:3000/api/debt'
```
Response JSON showing account''s debt period:
```json
{"response":[{"start":"2017-10-25T02:00:00.000Z","principal":178.57},{"start":"2017-10-18T02:00:00.000Z","end":"2017-10-24T02:00:00.000Z","principal":278.57}]}
```
