#!/bin/bash
echo -e ">> Running Employee Profile API with Id CAND002 >>\n"

curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/CAND002/csv
echo -e "\n---------------------------------------------------------"

#curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/CAND001
#echo -e "\n---------------------------------------------------------"
#
#echo ">> Running Employee Profile API with Id CAND0020"
#curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/CAND0020

#curl -X GET http://localhost:8088/api/service_now/onboarding_candidate_info?page=1&size=1
#curl -X GET http://localhost:8080/api/servicenow/v1/employee-profiles
curl -X GET "http://localhost:8080/api/servicenow/v1/employee-profiles?skip=0&limit=1"

echo -e "\n---------------------------------------------------------"
echo "Done."
