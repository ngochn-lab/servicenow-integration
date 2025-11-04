#!/bin/bash
echo ">> Running Employee Profile API with Id CAND002"
echo -e "---------------------------------------------------------"

curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/CAND002/csv
echo -e "\n---------------------------------------------------------"

#curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/CAND002
#echo -e "\n---------------------------------------------------------"
#
#echo ">> Running Employee Profile API with Id CAND0020"
#curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/CAND0020

echo -e "\n---------------------------------------------------------"
echo "Done."
