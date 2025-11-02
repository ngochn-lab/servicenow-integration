#!/bin/bash
echo ">> Running Employee Profile API"
echo "---------------------------------------------------------"
curl -X GET http://localhost:8080/api/servicenow/v1/employee-profile/12

echo -e "\n---------------------------------------------------------"
echo "Done."
