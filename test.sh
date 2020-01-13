printf "\nworking go"

curl -i -X POST \
   -H "tenant:properTenant" \
   -H "Content-Type:application/json" \
 'http://localhost:8080/working?param=123'

printf "\nfailed go"

curl -i -X POST \
   -H "tenant:properTenant" \
   -H "Content-Type:application/json" \
   -d \
'{"value":21312}' \
 'http://localhost:8080/failed'
