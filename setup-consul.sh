#!/bin/bash

putConsulData() {
    curl -s -X PUT -d 'none' http://consul:8500/v1/kv/sftp-gateway/spring/jpa/hibernate/ddl-auto &&
    curl -s -X PUT -d 'mysql' http://consul:8500/v1/kv/sftp-gateway/db-init/service-name &&
    curl -s -X PUT -d ${MYSQL_DATABASE} http://consul:8500/v1/kv/sftp-gateway/db-init/database &&
    curl -s -X PUT -d ${MYSQL_ROOT_USER} http://consul:8500/v1/kv/sftp-gateway/db-init/user &&
    curl -s -X PUT -d ${MYSQL_ROOT_PASSWORD} http://consul:8500/v1/kv/sftp-gateway/db-init/password &&
    curl -s -X PUT -d 'auth' http://consul:8500/v1/kv/sftp-gateway/auth/service-name &&
    curl -s -X PUT -d '/auth/token' http://consul:8500/v1/kv/sftp-gateway/auth/endpoint &&
    curl -s -X PUT -d ${CLIENT_KEY} http://consul:8500/v1/kv/sftp-gateway/auth/client-key &&
    curl -s -X PUT -d ${CLIENT_SECRET} http://consul:8500/v1/kv/sftp-gateway/auth/client-secret &&
    curl -s -X PUT -d 'blob' http://consul:8500/v1/kv/sftp-gateway/blob/service-name &&
    curl -s -X PUT -d ${BLOB_ACCESS} http://consul:8500/v1/kv/sftp-gateway/blob/access &&
    curl -s -X PUT -d ${BLOB_TYPE} http://consul:8500/v1/kv/sftp-gateway/blob/type &&
    curl -s -X PUT -d 'bouncer' http://consul:8500/v1/kv/sftp-gateway/bouncer/service-name &&
    curl -s -X PUT -d 'admin' http://consul:8500/v1/kv/sftp-gateway/bouncer/roles/allways-allow &&
    curl -s -X PUT -d '' http://consul:8500/v1/kv/sftp-gateway/bouncer/roles/allways-deny &&
    curl -s -X PUT -d '{
  "rest-permissions": {
    "name": {
      "en": "SFTP- Gateway Rest",
      "de": "SFTP- Gateway Rest DE"
    },
    "description": {
      "en": "Configuration of the SFTP Rest",
      "de": "Configuration of the SFTP Rest DE"
    },
    "resources": [
      {
        "roleIds": [
          "*"
        ],
        "type": [
          "rest"
        ],
        "resourceId": "^/api/configs/*",
        "actions": [
          "view",
          "edit",
          "delete"
        ]
      },
      {
        "roleIds": [
          "*"
        ],
        "type": [
          "rest"
        ],
        "resourceId": "^/api/evnts/*",
        "actions": [
          "view"
        ]
      },
      {
        "roleIds": [
          "*"
        ],
        "type": [
          "rest"
        ],
        "resourceId": "^/static/js/*",
        "actions": [
          "view"
        ]
      }
    ]
  },
  "health-permissions": {
    "name": {
      "en": "SFTP- Gateway Healthcheck",
      "de": "SFTP- Gateway Healthcheck DE"
    },
    "description": {
      "en": "Configuration of the SFTP Healthcheck",
      "de": "Configuration of the SFTP Healthcheck DE"
    },
    "resources": [
      {
        "roleIds": [
          "*"
        ],
        "type": [
          "rest"
        ],
        "resourceId": "^/api/health/check",
        "actions": [
          "view"
        ]
      }
    ]
  }
}' http://consul:8500/v1/kv/sftp-gateway/bouncer/permissions &&
    curl -s -X PUT -d '' http://consul:8500/v1/kv/sftp-gateway/bouncer/paths/public &&
    curl -s -X PUT -d ${ACL_TOPIC_NAME} http://consul:8500/v1/kv/sftp-gateway/bouncer/topic-name &&
    curl -s -X PUT -d '/api/sftp/**' http://consul:8500/v1/kv/sftp-gateway/web/security/jwt/uri &&
    curl -s -X PUT -d 'X-User-Id-Token' http://consul:8500/v1/kv/sftp-gateway/web/security/jwt/header &&
    curl -s -X PUT -d 'Bearer' http://consul:8500/v1/kv/sftp-gateway/web/security/jwt/prefix &&
    curl -s -X PUT -d 'JwtSecretKey' http://consul:8500/v1/kv/sftp-gateway/web/security/jwt/secret &&
    curl -s -X PUT -d 'tnt' http://consul:8500/v1/kv/sftp-gateway/tnt/service-name &&
    curl -s -X PUT -d '/api/events' http://consul:8500/v1/kv/sftp-gateway/tnt/event-path &&
    curl -s -X PUT -d ${TNT_ACTIVE} http://consul:8500/v1/kv/sftp-gateway/tnt/active &&
    curl -s -X PUT -d ${TNT_USER} http://consul:8500/v1/kv/sftp-gateway/tnt/username &&
    curl -s -X PUT -d ${TNT_PASSWORD} http://consul:8500/v1/kv/sftp-gateway/tnt/password &&
    curl -s -X PUT -d 'sftp-gateway' http://consul:8500/v1/kv/sftp-gateway/sftp/service-name &&
    curl -s -X PUT -d '2222' http://consul:8500/v1/kv/sftp-gateway/sftp/server/port &&
    curl -s -X PUT -d ${WELCOME_MESSAGE} http://consul:8500/v1/kv/sftp-gateway/sftp/server/welcome-message &&
    curl -s -X PUT -d '-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDTZu35AliNJMyt
DXtM0sPl71iEoU8IEUxdIaybdYbGbdfG5+yipoWlwJCR+mI4gRuqiU18bBJHo5o/
Relqhf5xBNVgG4CH6WIF5LgCGSlULHHBtms/w2TD+/wASzHOXzKzauU5Soa/WX7j
62ZM2pqCDDRTDOUfcEElXN83Mwo/3L6C0GK0cR6cr7QoaqsXR/V0StSQ8xsmfv3r
NABnWWt9r6GqdiXuUUJCO7nnd+1rk79iPelnDkAMQrC5YLWNX8YfP0JxDC/0zYgr
GaR0MeY2Qtoj2ZPE4zzOlShKxjbC6c7D9v5G0GD8uv3zFptpruEMvwoZvzlWBywh
ksU073IxAgMBAAECggEAR/zb+piW6PFoFWHq4909wlX/0aJQlFG3rFskOQgLbhTH
jr85cKW+CxSI5nzZ553neuoojb8LdoO6qeM3ugOJApl/w6t52E5cB9+73VMOJNDB
qBJjcSIrdSWgycTRYBJA5KH5DspXGl/yetCVVIR4auKXCtTwTQVf2cZHaLejvNzj
NdrmGk0OZHT+P/edMAsNbuw4qfu568jaOiDi/74SbQTA3vuXyTsGfIiSBY2r9hKn
hgXRMJEWbVjOgyNlpvWtkac7Omf+c4Y4q4lerilbkhBE/7WLfHc+08u1zWLwZhcv
bIOtpf6cnzJXV/9+3fWarLQtlkBBjxGHky4t73FoMQKBgQDtlCV78D6tK05kG/ky
ZSJxBae/9bLeK8XqvEKcR4QjuhfZpntqZIGRY5fP2HDSfPe7nenv5vkoknsH/ttp
m7e3ROJatDYl6WabTsGjtnokHLKrLRlNRLQNMshOxNA2IRthPcBskSaYeUwmXMX1
rneVUzcAQvYLMMHR5FTh5pW8xQKBgQDjyy+w6zs15vFYzS6MJK63ppu+ZiKnx9zg
Q3OwiZWLi6taK+qYbWk8B8Zg5crx6hgf+l1GdJLfQy+2/86ZZ3AM13xn+hm+P8tR
X4Dq8KBBzXFn5SMBNC4XxW8c2I4/wc3QX7ztkuFDA+Xh/M+ZbdmDMgCYmmR3ZWqI
yswyXnmOfQKBgFEvLbvzdceBI/GLqZUyABn1H8S19iB7fs9e87gprBr1TY8fl87c
d3YyPT9WK/+RmqovJTDIkd3uaEJsMZgeESJ8VIlASbyczGZH/F2wTn6mm9tottuz
nX9hGhfoo2nL2GpbSrUOyMyTrpTxYOg6bTzGyeW5/BAI4kKSLvte604tAoGAEoTi
S6/UEH3GAUItFpek3Kle4AvNpXZKLrFNJn0I+19PfvUzDVFXzNmU9yu3ZBN0AqWI
D8JKbnw31NjXIwGVynV6V7mtfhoRnXv2LyOA49if76JhRifH14blkaLJbcWDxf0C
jw3x4lDez5COEBsuI8xc4AstP8eu8ZALKc0kdMECgYAGA/O1ffPxjf5rQfhAXjjO
hMU9JgMmEbSHGgNloabjrKFebmi/jWWmxCja41bei8oF6ifbBapO8TQ4czfKhDqT
I20lHLfzWfj1dJhThnTDbilJjQnUzXvK3hsD6C+wrcg/ruTZSYDgycd/LkJDRgsu
K8aR9qGecqze7SGJXrAsYw==
-----END PRIVATE KEY-----' http://consul:8500/v1/kv/sftp-gateway/sftp/server/host-key &&
    curl -s -X PUT -d 'kafka' http://consul:8500/v1/kv/sftp-gateway/kafka/service-name &&
    curl -s -X PUT -d ${KAFKA_TOPIC_NAME} http://consul:8500/v1/kv/sftp-gateway/kafka/topic-name &&
    curl -s -X PUT -d ${KAFKA_NUMBER_OF_PARTITIONS} http://consul:8500/v1/kv/sftp-gateway/kafka/number-of-partitions &&
    curl -s -X PUT -d ${KAFKA_REPLICATION_FAKTOR} http://consul:8500/v1/kv/sftp-gateway/kafka/replication-factor &&
    curl -s -X PUT -d ${KAFKA_USER} http://consul:8500/v1/kv/sftp-gateway/kafka/user &&
    curl -s -X PUT -d ${KAFKA_PASSWORD} http://consul:8500/v1/kv/sftp-gateway/kafka/password
}

putConsulData

while [ $? -ne 0 ]; do
    sleep 1
    echo "Could not connect to consul. Retrying..."
    putConsulData
done
