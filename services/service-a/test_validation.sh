#!/bin/bash
echo "Testing validation..."
echo "1. Test with blank name:"
curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":""}' 2>/dev/null | jq .

echo ""
echo "2. Test with whitespace name:"
curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":"   "}' 2>/dev/null | jq .

echo ""
echo "3. Test with null name (missing field):"
curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{}' 2>/dev/null | jq .

echo ""
echo "4. Test with valid name:"
curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":"ValidName"}' 2>/dev/null | jq .
