SSM Project

for generate keys:

Server JKS:

keytool -genkey \
-keystore {name}.jks \
-keyalg RSA \
-dname "CN={customer name}, OU={org unit}, O={org}, L={location}, ST={state}, C={country}" \
-storepass {password} \
-alias {alias} \
-keypass {keypassword}

Client trusted store:

keytool -export \
-keystore {name}.jks \
-alias {alias} \
-storepass {password} \
-file {certFileName}.cer

keytool -import \
-keystore {clientTSName}.jks \
-file {certFileName}.cer \
-storepass {password}