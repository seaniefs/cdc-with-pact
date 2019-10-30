#!/bin/bash
mvn clean verify -Dpact.verifier.publishResults=true -Dpact.provider.version=1.0.0 -Dpact.provider.tag=DEV -Dpactbroker.auth.username=pacttest -Dpactbroker.auth.password=pacttest -Dpactbroker.scheme=http -Dpactbroker.host=127.0.0.1 -Dpactbroker.port=9292


