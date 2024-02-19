VAULT_KV_ADDR=http://vault:8200/v1/secret/data
VAULT_AUTH="X-Vault-Token: myroot"

cd prepopulate
sleep 1

curl --header "$VAULT_AUTH" -X POST -d "@myapp-secret.json" $VAULT_KV_ADDR/myapp
